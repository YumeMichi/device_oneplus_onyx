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

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceManager;

public class MusicGestureSwitch implements OnPreferenceChangeListener {

    private static final String FILE = "/proc/touchpanel/music_enable";
    private static final String FILE_ALT = "/proc/touchpanel/double_swipe_enable";

    private static String getFile() {
        if (Utils.fileWritable(FILE)) {
            return FILE;
        }
        if (Utils.fileWritable(FILE_ALT)) {
            return FILE_ALT;
        }
        return null;
    }

    public static boolean isSupported() {
        return Utils.fileWritable(getFile());
    }

    public static boolean isCurrentlyEnabled(Context context) {
        return Utils.getFileValueAsBoolean(getFile(), false);
    }

    public static boolean isEnabled(Context context) {
        boolean enabled = Utils.getFileValueAsBoolean(getFile(), false);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getBoolean(DeviceSettings.KEY_MUSIC_SWITCH, enabled);
    }

    /**
     * Restore setting from SharedPreferences. (Write to kernel.)
     * @param context       The context to read the SharedPreferences from
     */
    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        boolean enabled = isEnabled(context);
        if (enabled)
            Utils.writeValue(getFile(), "1");
        else
            Utils.writeValue(getFile(), "0");
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        if (enabled)
            Utils.writeValue(getFile(), "1");
        else
            Utils.writeValue(getFile(), "0");
        return true;
    }
}
