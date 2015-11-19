/*
 * Copyright (C) 2015 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.settings.device;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.preference.PreferenceManager;
import android.service.gesture.IGestureService;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import java.io.File;

import com.cyanogenmod.settings.device.utils.Constants;
import com.cyanogenmod.settings.device.utils.FileUtils;

public class Startup extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Disable touchscreen gesture settings if needed
            if (!hasTouchscreenGestures()) {
                disableComponent(context, TouchscreenGestureSettings.class.getName());
            } else {
                enableComponent(context, TouchscreenGestureSettings.class.getName());
                // Restore nodes to saved preference values
                for (String pref : Constants.sNodePreferenceMap.keySet()) {
                    boolean defaultValue = Constants.sNodeDefaultMap.get(pref);
                    boolean value = Constants.isPreferenceEnabled(context, pref, defaultValue);
                    String node = Constants.sNodePreferenceMap.get(pref);
                    FileUtils.writeLine(node, value ? "1" : "0");
                }
            }

            // Disable backtouch settings if needed
            if (!context.getResources().getBoolean(
                        com.android.internal.R.bool.config_enableGestureService)) {
                disableComponent(context, GesturePadSettings.class.getName());
            } else {
                IBinder b = ServiceManager.getService("gesture");
                IGestureService sInstance = IGestureService.Stub.asInterface(b);

                // Set longPress event
                toggleLongPress(context, sInstance, Constants.isPreferenceEnabled(
                        context, Constants.TOUCHPAD_LONGPRESS_KEY, false));

                // Set doubleTap event
                toggleLongPress(context, sInstance, Constants.isPreferenceEnabled(
                        context, Constants.TOUCHPAD_DOUBLETAP_KEY, false));
            }

            // Disable O-Click settings if needed
            if (!hasOClick()) {
                disableComponent(context, BluetoothInputSettings.class.getName());
                disableComponent(context, OclickService.class.getName());
            } else {
                updateOClickServiceState(context);
            }
        } else if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            if (hasOClick()) {
                updateOClickServiceState(context);
            }
        } else if (intent.getAction().equals("cyanogenmod.intent.action.GESTURE_CAMERA")) {
            long now = SystemClock.uptimeMillis();
            sendInputEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_CAMERA, 0, 0,
                    KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, InputDevice.SOURCE_KEYBOARD));
            sendInputEvent(new KeyEvent(now, now, KeyEvent.ACTION_UP,KeyEvent.KEYCODE_CAMERA,
                    0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, InputDevice.SOURCE_KEYBOARD));
        }
    }

    public static void toggleDoubleTap(Context context, IGestureService gestureService,
            boolean enable) {
        PendingIntent pendingIntent = null;
        if (enable) {
            Intent doubleTapIntent = new Intent("cyanogenmod.intent.action.GESTURE_CAMERA", null);
            pendingIntent = PendingIntent.getBroadcastAsUser(
                    context, 0, doubleTapIntent, 0, UserHandle.CURRENT);
        }
        try {
            System.out.println("toggleDoubleTap : " + pendingIntent);
            gestureService.setOnDoubleClickPendingIntent(pendingIntent);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void toggleLongPress(Context context, IGestureService gestureService,
            boolean enable) {
        PendingIntent pendingIntent = null;
        if (enable) {
            Intent longPressIntent = new Intent(Intent.ACTION_CAMERA_BUTTON, null);
            pendingIntent = PendingIntent.getBroadcastAsUser(
                    context, 0, longPressIntent, 0, UserHandle.CURRENT);
        }
        try {
            System.out.println("toggleLongPress : " + pendingIntent);
            gestureService.setOnLongPressPendingIntent(pendingIntent);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendInputEvent(InputEvent event) {
        InputManager inputManager = InputManager.getInstance();
        inputManager.injectInputEvent(event,
                InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);
    }

    private boolean hasTouchscreenGestures() {
        return new File(Constants.TOUCHSCREEN_CAMERA_NODE).exists() &&
            new File(Constants.TOUCHSCREEN_MUSIC_NODE).exists() &&
            new File(Constants.TOUCHSCREEN_FLASHLIGHT_NODE).exists();
    }

    private void disableComponent(Context context, String component) {
        ComponentName name = new ComponentName(context, component);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(name,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void enableComponent(Context context, String component) {
        ComponentName name = new ComponentName(context, component);
        PackageManager pm = context.getPackageManager();
        if (pm.getComponentEnabledSetting(name)
                == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            pm.setComponentEnabledSetting(name,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    private static boolean hasOClick() {
        return Build.MODEL.equals("N1") || Build.MODEL.equals("N3");
    }

    private void updateOClickServiceState(Context context) {
        BluetoothManager btManager = (BluetoothManager)
                context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = btManager.getAdapter();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean shouldStartService = adapter != null
                && adapter.getState() == BluetoothAdapter.STATE_ON
                && prefs.contains(Constants.OCLICK_DEVICE_ADDRESS_KEY);
        Intent serviceIntent = new Intent(context, OclickService.class);

        if (shouldStartService) {
            context.startService(serviceIntent);
        } else {
            context.stopService(serviceIntent);
        }
    }
}
