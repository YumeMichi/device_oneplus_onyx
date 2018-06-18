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

import android.content.res.Resources;
import android.content.Intent;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.util.Log;

public class DeviceSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String KEY_CAMERA_SWITCH = "camera";
    public static final String KEY_TORCH_SWITCH = "torch";
    public static final String KEY_VIBSTRENGTH = "vib_strength";
    public static final String KEY_MUSIC_SWITCH = "music";
    private static final String KEY_SLIDER_MODE_TOP = "slider_mode_top";
    private static final String KEY_SLIDER_MODE_CENTER = "slider_mode_center";
    private static final String KEY_SLIDER_MODE_BOTTOM = "slider_mode_bottom";
    public static final String KEY_SWAP_BACK_RECENTS = "swap_back_recents";
    public static final String KEY_HBM_SWITCH = "hbm";
    public static final String KEY_PROXI_SWITCH = "proxi";
    public static final String KEY_DOUBLE_TAP_SWITCH = "double_tap";

    private TwoStatePreference mTorchSwitch;
    private TwoStatePreference mCameraSwitch;
    private VibratorStrengthPreference mVibratorStrength;
    private TwoStatePreference mMusicSwitch;
    private ListPreference mSliderModeTop;
    private ListPreference mSliderModeCenter;
    private ListPreference mSliderModeBottom;
    private TwoStatePreference mSwapBackRecents;
    private TwoStatePreference mHBMModeSwitch;
    private TwoStatePreference mProxiSwitch;
    private TwoStatePreference mDoubleTapSwitch;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main, rootKey);

        mTorchSwitch = (TwoStatePreference) findPreference(KEY_TORCH_SWITCH);
        mTorchSwitch.setEnabled(TorchGestureSwitch.isSupported());
        mTorchSwitch.setChecked(TorchGestureSwitch.isCurrentlyEnabled(this.getContext()));
        mTorchSwitch.setOnPreferenceChangeListener(new TorchGestureSwitch());

        mCameraSwitch = (TwoStatePreference) findPreference(KEY_CAMERA_SWITCH);
        mCameraSwitch.setEnabled(CameraGestureSwitch.isSupported());
        mCameraSwitch.setChecked(CameraGestureSwitch.isCurrentlyEnabled(this.getContext()));
        mCameraSwitch.setOnPreferenceChangeListener(new CameraGestureSwitch());

        mVibratorStrength = (VibratorStrengthPreference) findPreference(KEY_VIBSTRENGTH);
        if (mVibratorStrength != null) {
            mVibratorStrength.setEnabled(VibratorStrengthPreference.isSupported());
        }

        mMusicSwitch = (TwoStatePreference) findPreference(KEY_MUSIC_SWITCH);
        mMusicSwitch.setEnabled(MusicGestureSwitch.isSupported());
        mMusicSwitch.setChecked(MusicGestureSwitch.isCurrentlyEnabled(this.getContext()));
        mMusicSwitch.setOnPreferenceChangeListener(new MusicGestureSwitch());

        mSliderModeTop = (ListPreference) findPreference(KEY_SLIDER_MODE_TOP);
        mSliderModeTop.setOnPreferenceChangeListener(this);
        int sliderModeTop = getSliderAction(0);
        int valueIndex = mSliderModeTop.findIndexOfValue(String.valueOf(sliderModeTop));
        mSliderModeTop.setValueIndex(valueIndex);
        mSliderModeTop.setSummary(mSliderModeTop.getEntries()[valueIndex]);

        mSliderModeCenter = (ListPreference) findPreference(KEY_SLIDER_MODE_CENTER);
        mSliderModeCenter.setOnPreferenceChangeListener(this);
        int sliderModeCenter = getSliderAction(1);
        valueIndex = mSliderModeCenter.findIndexOfValue(String.valueOf(sliderModeCenter));
        mSliderModeCenter.setValueIndex(valueIndex);
        mSliderModeCenter.setSummary(mSliderModeCenter.getEntries()[valueIndex]);

        mSliderModeBottom = (ListPreference) findPreference(KEY_SLIDER_MODE_BOTTOM);
        mSliderModeBottom.setOnPreferenceChangeListener(this);
        int sliderModeBottom = getSliderAction(2);
        valueIndex = mSliderModeBottom.findIndexOfValue(String.valueOf(sliderModeBottom));
        mSliderModeBottom.setValueIndex(valueIndex);
        mSliderModeBottom.setSummary(mSliderModeBottom.getEntries()[valueIndex]);

        mSwapBackRecents = (TwoStatePreference) findPreference(KEY_SWAP_BACK_RECENTS);
        mSwapBackRecents.setEnabled(SwapBackRecents.isSupported());
        mSwapBackRecents.setChecked(SwapBackRecents.isCurrentlyEnabled(this.getContext()));
        mSwapBackRecents.setOnPreferenceChangeListener(new SwapBackRecents());

        mHBMModeSwitch = (TwoStatePreference) findPreference(KEY_HBM_SWITCH);
        mHBMModeSwitch.setEnabled(HBMModeSwitch.isSupported());
        mHBMModeSwitch.setChecked(HBMModeSwitch.isCurrentlyEnabled(this.getContext()));
        mHBMModeSwitch.setOnPreferenceChangeListener(new HBMModeSwitch());

        mProxiSwitch = (TwoStatePreference) findPreference(KEY_PROXI_SWITCH);
        mProxiSwitch.setChecked(Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.DEVICE_PROXI_CHECK_ENABLED, 1) == 1);

        mDoubleTapSwitch = (TwoStatePreference) findPreference(KEY_DOUBLE_TAP_SWITCH);
        mDoubleTapSwitch.setEnabled(DoubleTapSwitch.isSupported());
        mDoubleTapSwitch.setChecked(DoubleTapSwitch.isCurrentlyEnabled(this.getContext()));
        mDoubleTapSwitch.setOnPreferenceChangeListener(new DoubleTapSwitch());
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mProxiSwitch) {
            Settings.System.putInt(getContext().getContentResolver(),
                    Settings.System.DEVICE_PROXI_CHECK_ENABLED, mProxiSwitch.isChecked() ? 1 : 0);
            return true;
        }
       return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSliderModeTop) {
            String value = (String) newValue;
            int sliderMode = Integer.valueOf(value);
            setSliderAction(0, sliderMode);
            int valueIndex = mSliderModeTop.findIndexOfValue(value);
            mSliderModeTop.setSummary(mSliderModeTop.getEntries()[valueIndex]);
        }
        if (preference == mSliderModeCenter) {
            String value = (String) newValue;
            int sliderMode = Integer.valueOf(value);
            setSliderAction(1, sliderMode);
            int valueIndex = mSliderModeCenter.findIndexOfValue(value);
            mSliderModeCenter.setSummary(mSliderModeCenter.getEntries()[valueIndex]);
        }
        if (preference == mSliderModeBottom) {
            String value = (String) newValue;
            int sliderMode = Integer.valueOf(value);
            setSliderAction(2, sliderMode);
            int valueIndex = mSliderModeBottom.findIndexOfValue(value);
            mSliderModeBottom.setSummary(mSliderModeBottom.getEntries()[valueIndex]);
        }
        return true;
    }

    private int getSliderAction(int position) {
        String value = Settings.System.getString(getContext().getContentResolver(),
                    Settings.System.BUTTON_EXTRA_KEY_MAPPING);
        final String defaultValue = "5,3,0";

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

    private void setSliderAction(int position, int action) {
        String value = Settings.System.getString(getContext().getContentResolver(),
                    Settings.System.BUTTON_EXTRA_KEY_MAPPING);
        final String defaultValue = "5,3,0";

        if (value == null) {
            value = defaultValue;
        } else if (value.indexOf(",") == -1) {
            value = defaultValue;
        }
        try {
            String[] parts = value.split(",");
            parts[position] = String.valueOf(action);
            String newValue = TextUtils.join(",", parts);
            Settings.System.putString(getContext().getContentResolver(),
                    Settings.System.BUTTON_EXTRA_KEY_MAPPING, newValue);
        } catch (Exception e) {
            // Ignore
        }
    }
}
