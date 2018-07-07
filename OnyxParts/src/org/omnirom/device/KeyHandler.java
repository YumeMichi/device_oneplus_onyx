/*
* Copyright (C) 2016 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.omnirom.device;

import android.app.ActivityManagerNative;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.IAudioService;
import android.media.AudioManager;
import android.media.session.MediaSessionLegacyHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.util.Log;
import android.view.KeyEvent;
import android.view.HapticFeedbackConstants;
import android.view.WindowManagerGlobal;
import android.view.WindowManagerPolicy;

import com.android.internal.os.DeviceKeyHandler;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.omni.OmniUtils;
import com.android.internal.statusbar.IStatusBarService;

public class KeyHandler implements DeviceKeyHandler {

    private static final String TAG = "KeyHandler";
    private static final boolean DEBUG = true;
    private static final boolean DEBUG_SENSOR = false;

    private static final int GESTURE_WAKELOCK_DURATION = 2000;
    private static final String KEY_CONTROL_PATH = "/proc/touchpanel/keypad_enable";

    private static final int GESTURE_O_SCANCODE = 250;
    private static final int GESTURE_II_SCANCODE = 251;
    private static final int GESTURE_UP_SCANCODE = 252;
    private static final int GESTURE_DOWN_SCANCODE = 253;
    private static final int GESTURE_LEFT_V_SCANCODE = 254;
    private static final int GESTURE_RIGHT_V_SCANCODE = 255;
    private static final int GESTURE_W_SCANCODE = 256;
    private static final int GESTURE_M_SCANCODE = 257;
    private static final int GESTURE_RIGHT_SWIPE_SCANCODE = 258;
    private static final int GESTURE_LEFT_SWIPE_SCANCODE = 259;
    private static final int GESTURE_DOWN_SWIPE_SCANCODE = 260;
    private static final int GESTURE_UP_SWIPE_SCANCODE = 261;

    private static final int KEY_DOUBLE_TAP = 143;
    private static final int KEY_HOME = 102;
    private static final int KEY_BACK = 158;
    private static final int KEY_RECENTS = 580;
    private static final int KEY_SLIDER_TOP = 601;
    private static final int KEY_SLIDER_CENTER = 602;
    private static final int KEY_SLIDER_BOTTOM = 603;

    private static final int BATCH_LATENCY_IN_MS = 100;
    private static final int MIN_PULSE_INTERVAL_MS = 2500;
    private static final String DOZE_INTENT = "com.android.systemui.doze.pulse";
    private static final int HANDWAVE_MAX_DELTA_MS = 1000;
    private static final int POCKET_MIN_DELTA_MS = 5000;

    private static final int[] sSupportedGestures = new int[]{
        GESTURE_O_SCANCODE,
        GESTURE_II_SCANCODE,
        GESTURE_UP_SCANCODE,
        GESTURE_DOWN_SCANCODE,
        GESTURE_LEFT_V_SCANCODE,
        GESTURE_RIGHT_V_SCANCODE,
        GESTURE_W_SCANCODE,
        GESTURE_M_SCANCODE,
        GESTURE_DOWN_SWIPE_SCANCODE,
        GESTURE_UP_SWIPE_SCANCODE,
        GESTURE_LEFT_SWIPE_SCANCODE,
        GESTURE_RIGHT_SWIPE_SCANCODE,
        KEY_DOUBLE_TAP,
        KEY_SLIDER_TOP,
        KEY_SLIDER_CENTER,
        KEY_SLIDER_BOTTOM
    };

    private static final int[] sHandledGestures = new int[]{
        KEY_SLIDER_TOP,
        KEY_SLIDER_CENTER,
        KEY_SLIDER_BOTTOM
    };

    private static final int[] sProxiCheckedGestures = new int[]{
        GESTURE_O_SCANCODE,
        GESTURE_II_SCANCODE,
        GESTURE_UP_SCANCODE,
        GESTURE_DOWN_SCANCODE,
        GESTURE_LEFT_V_SCANCODE,
        GESTURE_RIGHT_V_SCANCODE,
        GESTURE_W_SCANCODE,
        GESTURE_M_SCANCODE,
        GESTURE_DOWN_SWIPE_SCANCODE,
        GESTURE_UP_SWIPE_SCANCODE,
        GESTURE_LEFT_SWIPE_SCANCODE,
        GESTURE_RIGHT_SWIPE_SCANCODE,
        KEY_DOUBLE_TAP
    };

    private final Context mContext;
    private final PowerManager mPowerManager;
    private EventHandler mEventHandler;
    private WakeLock mGestureWakeLock;
    private Handler mHandler = new Handler();
    private SettingsObserver mSettingsObserver;
    private static boolean mButtonDisabled;
    private final NotificationManager mNoMan;
    private final AudioManager mAudioManager;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean mProxyIsNear;
    private boolean mUseProxiCheck;
    private Sensor mTiltSensor;
    private long mTiltSensorTimestamp;
    private boolean mUseTiltCheck;
    private boolean mProxyWasNear;
    private long mProxySensorTimestamp;
    private boolean mUseWaveCheck;
    private boolean mUsePocketCheck;
    private WindowManagerPolicy mPolicy;

    private SensorEventListener mProximitySensor = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            mProxyIsNear = event.values[0] < mSensor.getMaximumRange();
            if (DEBUG_SENSOR) Log.i(TAG, "mProxyIsNear = " + mProxyIsNear);
            if (mUseWaveCheck || mUsePocketCheck) {
                if (mProxyWasNear && !mProxyIsNear) {
                    long delta = SystemClock.elapsedRealtime() - mProxySensorTimestamp;
                    if (mUseWaveCheck && delta < HANDWAVE_MAX_DELTA_MS) {
                        launchDozePulse();
                    }
                    if (mUsePocketCheck && delta > POCKET_MIN_DELTA_MS) {
                        launchDozePulse();
                    }
                }
                mProxySensorTimestamp = SystemClock.elapsedRealtime();
                mProxyWasNear = mProxyIsNear;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private SensorEventListener mTiltSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            long delta = SystemClock.elapsedRealtime() - mTiltSensorTimestamp;
            if (delta < MIN_PULSE_INTERVAL_MS) {
                return;
            } else {
                mTiltSensorTimestamp = SystemClock.elapsedRealtime();
            }

            if (event.values[0] == 1) {
                launchDozePulse();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(
                    Settings.System.HARDWARE_KEYS_DISABLE),
                    false, this);
            mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(
                    Settings.System.DEVICE_PROXI_CHECK_ENABLED),
                    false, this);
            mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(
                    Settings.System.DEVICE_FEATURE_SETTINGS),
                    false, this);
            update();
            updateDozeSettings();
        }

        @Override
        public void onChange(boolean selfChange) {
            update();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.equals(Settings.System.getUriFor(
                    Settings.System.DEVICE_FEATURE_SETTINGS))) {
                updateDozeSettings();
                return;
            }
            update();
        }

        public void update() {
            setButtonDisable(mContext);
            mUseProxiCheck = Settings.System.getIntForUser(
                    mContext.getContentResolver(), Settings.System.DEVICE_PROXI_CHECK_ENABLED, 1,
                    UserHandle.USER_CURRENT) == 1;
        }
    }

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
             if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                 onDisplayOn();
             } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                 onDisplayOff();
             }
         }
    };

    public KeyHandler(Context context) {
        mContext = context;
        mEventHandler = new EventHandler();
        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mGestureWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "GestureWakeLock");
        mSettingsObserver = new SettingsObserver(mHandler);
        mSettingsObserver.observe();
        mNoMan = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mTiltSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_TILT_DETECTOR);
        IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mContext.registerReceiver(mScreenStateReceiver, screenStateFilter);
    }

    private class EventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        }
    }

    @Override
    public boolean handleKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }

        boolean isKeySupported = ArrayUtils.contains(sHandledGestures, event.getScanCode());
        if (isKeySupported) {
            if (DEBUG) Log.i(TAG, "scanCode=" + event.getScanCode());
            switch(event.getScanCode()) {
                case KEY_SLIDER_TOP:
                    if (DEBUG) Log.i(TAG, "KEY_SLIDER_TOP");
                    doHandleSliderAction(0);
                    return true;
                case KEY_SLIDER_CENTER:
                    if (DEBUG) Log.i(TAG, "KEY_SLIDER_CENTER");
                    doHandleSliderAction(1);
                    return true;
                case KEY_SLIDER_BOTTOM:
                    if (DEBUG) Log.i(TAG, "KEY_SLIDER_BOTTOM");
                    doHandleSliderAction(2);
                    return true;
            }
        }
        return isKeySupported;
    }

    @Override
    public boolean canHandleKeyEvent(KeyEvent event) {
        return ArrayUtils.contains(sSupportedGestures, event.getScanCode());
    }

    @Override
    public boolean isDisabledKeyEvent(KeyEvent event) {
        boolean isProxyCheckRequired = mUseProxiCheck &&
                ArrayUtils.contains(sProxiCheckedGestures, event.getScanCode());
        if (mProxyIsNear && isProxyCheckRequired) {
            if (DEBUG) Log.i(TAG, "isDisabledKeyEvent: blocked by proxi sensor - scanCode=" + event.getScanCode());
            return true;
        }
        return false;
    }

    public static void setButtonDisable(Context context) {
        mButtonDisabled = Settings.System.getIntForUser(
                context.getContentResolver(), Settings.System.HARDWARE_KEYS_DISABLE, 0,
                UserHandle.USER_CURRENT) == 1;
        if (DEBUG) Log.i(TAG, "setButtonDisable=" + mButtonDisabled);
        if (mButtonDisabled)
            Utils.writeValue(KEY_CONTROL_PATH, "0");
        else
            Utils.writeValue(KEY_CONTROL_PATH, "1");
    }

    @Override
    public boolean isCameraLaunchEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }
        String value = getGestureValueForScanCode(event.getScanCode());
        return !TextUtils.isEmpty(value) && value.equals(AppSelectListPreference.CAMERA_ENTRY);
    }

    @Override
    public boolean isWakeEvent(KeyEvent event){
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }
        String value = getGestureValueForScanCode(event.getScanCode());
        if (!TextUtils.isEmpty(value) && value.equals(AppSelectListPreference.WAKE_ENTRY)) {
            if (DEBUG) Log.i(TAG, "isWakeEvent " + event.getScanCode() + value);
            return true;
        }
        return event.getScanCode() == KEY_DOUBLE_TAP;
    }

    @Override
    public Intent isActivityLaunchEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return null;
        }
        String value = getGestureValueForScanCode(event.getScanCode());
        if (!TextUtils.isEmpty(value) && !value.equals(AppSelectListPreference.DISABLED_ENTRY)) {
            if (DEBUG) Log.i(TAG, "isActivityLaunchEvent " + event.getScanCode() + value);
            if (!launchSpecialActions(value)) {
                vibe();
                Intent intent = createIntent(value);
                return intent;
            }
        }
        return null;
    }

    private IAudioService getAudioService() {
        IAudioService audioService = IAudioService.Stub
                .asInterface(ServiceManager.checkService(Context.AUDIO_SERVICE));
        if (audioService == null) {
            Log.w(TAG, "Unable to find IAudioService interface.");
        }
        return audioService;
    }

    boolean isMusicActive() {
        return mAudioManager.isMusicActive();
    }

    private void dispatchMediaKeyWithWakeLockToAudioService(int keycode) {
        if (ActivityManagerNative.isSystemReady()) {
            IAudioService audioService = getAudioService();
            if (audioService != null) {
                KeyEvent event = new KeyEvent(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN,
                        keycode, 0);
                dispatchMediaKeyEventUnderWakelock(event);
                event = KeyEvent.changeAction(event, KeyEvent.ACTION_UP);
                dispatchMediaKeyEventUnderWakelock(event);
            }
        }
    }

    private void dispatchMediaKeyEventUnderWakelock(KeyEvent event) {
        if (ActivityManagerNative.isSystemReady()) {
            MediaSessionLegacyHelper.getHelper(mContext).sendMediaButtonEvent(event, true);
        }
    }

    private void onDisplayOn() {
        if (DEBUG) Log.i(TAG, "Display on");
        if (enableProxiSensor()) {
            mSensorManager.unregisterListener(mProximitySensor, mSensor);
        }
        if (mUseTiltCheck) {
            mSensorManager.unregisterListener(mTiltSensorListener, mTiltSensor);
        }
    }

    private void onDisplayOff() {
        if (DEBUG) Log.i(TAG, "Display off");
        if (enableProxiSensor()) {
            mSensorManager.registerListener(mProximitySensor, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            mProxySensorTimestamp = SystemClock.elapsedRealtime();
            mProxyWasNear = false;
        }
        if (mUseTiltCheck) {
            mSensorManager.registerListener(mTiltSensorListener, mTiltSensor,
                    SensorManager.SENSOR_DELAY_NORMAL, BATCH_LATENCY_IN_MS * 1000);
            mTiltSensorTimestamp = SystemClock.elapsedRealtime();
        }
    }

    private int getSliderAction(int position) {
        String value = Settings.System.getStringForUser(mContext.getContentResolver(),
                    Settings.System.BUTTON_EXTRA_KEY_MAPPING,
                    UserHandle.USER_CURRENT);
        final String defaultValue = DeviceSettings.SLIDER_DEFAULT_VALUE;

        if (value == null) {
            value = defaultValue;
        } else if (value.indexOf(",") == -1) {
            value = defaultValue;
        }
        try {
            String[] parts = value.split(",");
            return Integer.valueOf(parts[position]);
        } catch (Exception e) {
            // Ignore
        }
        return 0;
    }

    private void doHandleSliderAction(int position) {
        int action = getSliderAction(position);
        if ( action == 0) {
            mNoMan.setZenMode(Global.ZEN_MODE_OFF_ONLY, null, TAG);
            mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
        } else if (action == 1) {
            mNoMan.setZenMode(Global.ZEN_MODE_OFF_ONLY, null, TAG);
            mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_VIBRATE);
        } else if (action == 2) {
            mNoMan.setZenMode(Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS, null, TAG);
        } else if (action == 3) {
            mNoMan.setZenMode(Global.ZEN_MODE_ALARMS, null, TAG);
        } else if (action == 4) {
            mNoMan.setZenMode(Global.ZEN_MODE_NO_INTERRUPTIONS, null, TAG);
        }
    }

    private Intent createIntent(String value) {
        ComponentName componentName = ComponentName.unflattenFromString(value);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setComponent(componentName);
        return intent;
    }

    private boolean launchSpecialActions(String value) {
        if (value.equals(AppSelectListPreference.TORCH_ENTRY)) {
            mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
            IStatusBarService service = getStatusBarService();
            if (service != null) {
                try {
                    vibe();
                    service.toggleCameraFlash();
                } catch (RemoteException e) {
                    // do nothing.
                }
            }
            return true;
        } else if (value.equals(AppSelectListPreference.MUSIC_PLAY_ENTRY)) {
            mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
            vibe();
            dispatchMediaKeyWithWakeLockToAudioService(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
            return true;
        } else if (value.equals(AppSelectListPreference.MUSIC_NEXT_ENTRY)) {
            if (isMusicActive()) {
                mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
                vibe();
                dispatchMediaKeyWithWakeLockToAudioService(KeyEvent.KEYCODE_MEDIA_NEXT);
            }
            return true;
        } else if (value.equals(AppSelectListPreference.MUSIC_PREV_ENTRY)) {
            if (isMusicActive()) {
                mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
                vibe();
                dispatchMediaKeyWithWakeLockToAudioService(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
            }
            return true;
        }
        return false;
    }

    private String getGestureValueForScanCode(int scanCode) {
        switch(scanCode) {
            case GESTURE_O_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_0, UserHandle.USER_CURRENT);
            case GESTURE_II_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_1, UserHandle.USER_CURRENT);
            case GESTURE_UP_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_2, UserHandle.USER_CURRENT);
            case GESTURE_DOWN_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_3, UserHandle.USER_CURRENT);
            case GESTURE_LEFT_V_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_4, UserHandle.USER_CURRENT);
            case GESTURE_RIGHT_V_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_5, UserHandle.USER_CURRENT);
            case GESTURE_W_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_6, UserHandle.USER_CURRENT);
            case GESTURE_M_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_7, UserHandle.USER_CURRENT);
            case GESTURE_DOWN_SWIPE_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_8, UserHandle.USER_CURRENT);
            case GESTURE_UP_SWIPE_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_9, UserHandle.USER_CURRENT);
            case GESTURE_LEFT_SWIPE_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_10, UserHandle.USER_CURRENT);
            case GESTURE_RIGHT_SWIPE_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_11, UserHandle.USER_CURRENT);
        }
        return null;
    }

    private void launchDozePulse() {
        if (DEBUG) Log.i(TAG, "Doze pulse");
        mContext.sendBroadcastAsUser(new Intent(DOZE_INTENT),
                new UserHandle(UserHandle.USER_CURRENT));
    }

    private boolean enableProxiSensor() {
        return mUsePocketCheck || mUseWaveCheck || mUseProxiCheck;
    }

    private void updateDozeSettings() {
        String value = Settings.System.getStringForUser(mContext.getContentResolver(),
                    Settings.System.DEVICE_FEATURE_SETTINGS,
                    UserHandle.USER_CURRENT);
        if (DEBUG) Log.i(TAG, "Doze settings = " + value);
        if (!TextUtils.isEmpty(value)) {
            String[] parts = value.split(":");
            mUseWaveCheck = Boolean.valueOf(parts[0]);
            mUsePocketCheck = Boolean.valueOf(parts[1]);
            mUseTiltCheck = Boolean.valueOf(parts[2]);
        }
    }

    @Override
    public void setWindowManagerPolicy(WindowManagerPolicy policy) {
        mPolicy = policy;
    }

    private void vibe(){
        boolean doVibrate = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.DEVICE_OFF_SCREEN_GESTURE_FEEDBACK_ENABLED, 0,
                UserHandle.USER_CURRENT) == 1;
        if (doVibrate && mPolicy != null) {
            mPolicy.performHapticFeedbackLw(null, HapticFeedbackConstants.LONG_PRESS, true);
        }
    }

    IStatusBarService getStatusBarService() {
        return IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
    }
}
