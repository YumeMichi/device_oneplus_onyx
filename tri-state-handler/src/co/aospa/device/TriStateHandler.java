/*
 * Copyright (C) 2018 The LineageOS Project
 * Copyright (C) 2019 CypherOS
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

package co.aospa.device;

import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.view.KeyEvent;

import com.android.internal.os.DeviceKeyHandler;

public class TriStateHandler implements DeviceKeyHandler {

    // Slider key codes
    private static final int MODE_VIBRATION = 601;
    private static final int MODE_SILENCE = 602;
    private static final int MODE_NORMAL = 603;

    private static AudioManager mAudioManager;
    private static Context mContext;
    private static Vibrator mVibrator;

    public TriStateHandler(Context context) {
        mContext = context;

        mAudioManager = mContext.getSystemService(AudioManager.class);
        mVibrator = mContext.getSystemService(Vibrator.class);
    }

    public KeyEvent handleKeyEvent(KeyEvent event) {
        final int scanCode = event.getScanCode();
        final int currentRingerMode = mAudioManager.getRingerModeInternal();

        switch (scanCode) {
            case MODE_NORMAL:
                if (currentRingerMode != AudioManager.RINGER_MODE_NORMAL) {
                    doHapticFeedback(VibrationEffect.EFFECT_DOUBLE_CLICK);
                    mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
                } else {
                    return event;
                }
                break;
            case MODE_VIBRATION:
                if (currentRingerMode != AudioManager.RINGER_MODE_VIBRATE) {
                    doHapticFeedback(VibrationEffect.EFFECT_THUD);
                    mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_VIBRATE);
                } else {
                    return event;
                }
                break;
            case MODE_SILENCE:
                if (currentRingerMode != AudioManager.RINGER_MODE_SILENT) {
                    doHapticFeedback(VibrationEffect.EFFECT_POP);
                    mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_SILENT);
                } else {
                    return event;
                }
                break;
            default:
                return event;
        }

        return null;
    }

    private void doHapticFeedback(int effect) {
        if (mVibrator != null) {
            if (mVibrator.hasVibrator()) {
                mVibrator.vibrate(VibrationEffect.get(effect));
            }
        }
    }
}
