/*Copyright (C) 2015 The ResurrectionRemix Project
     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
*/
package com.android.settings.rr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.provider.SearchIndexableResource;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import com.android.settings.rr.utils.RRUtils;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.search.SearchIndexable;

import android.provider.Settings;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
@SearchIndexable
public class PartSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String SMART_PIXELS = "smart_pixels";
    private Preference mSmartPixels;
    private static final String KEY_PHOTOS_SPOOF = "use_photos_spoof";
    private static final String SYS_PHOTOS_SPOOF = "persist.sys.pixelprops.gphotos";
    private SwitchPreference mPhotosSpoof;
    private static final String KEY_GAMES_SPOOF = "use_games_spoof";
    private static final String SYS_GAMES_SPOOF = "persist.sys.pixelprops.games";
    private SwitchPreference mGamesSpoof;
    private static final String KEY_STREAM_SPOOF = "use_stream_spoof";
    private static final String SYS_STREAM_SPOOF = "persist.sys.pixelprops.streaming";
    private SwitchPreference mStreamSpoof;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.rr_parts);
        Context mContext = getActivity().getApplicationContext();

        final PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources res = getResources();
        mSmartPixels = (Preference) findPreference(SMART_PIXELS);
        boolean mSmartPixelsSupported = res.getBoolean(
                com.android.internal.R.bool.config_supportSmartPixels);
        if (!mSmartPixelsSupported)
            removePreference("smart_pixels");
        mGamesSpoof = (SwitchPreference) prefScreen.findPreference(KEY_GAMES_SPOOF);
        mGamesSpoof.setChecked(SystemProperties.getBoolean(SYS_GAMES_SPOOF, false));
        mGamesSpoof.setOnPreferenceChangeListener(this);

        mPhotosSpoof = (SwitchPreference) prefScreen.findPreference(KEY_PHOTOS_SPOOF);
        mPhotosSpoof.setChecked(SystemProperties.getBoolean(SYS_PHOTOS_SPOOF, true));
        mPhotosSpoof.setOnPreferenceChangeListener(this);

        mStreamSpoof = (SwitchPreference) findPreference(KEY_STREAM_SPOOF);
        mStreamSpoof.setChecked(SystemProperties.getBoolean(SYS_STREAM_SPOOF, true));
        mStreamSpoof.setOnPreferenceChangeListener(this);

        int anim = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.RR_CONFIG_ANIM, 0);
        try {
            if (anim == 0) {
                removePreference("animation");
            } else if (anim == 1) {
                removePreference("preview");
            } else if (anim == 2) {
                removePreference("animation");
                removePreference("preview");
            }
        } catch (Exception e) {}

    }

     @Override
     public void onResume() {
         super.onResume();
     }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
	if (preference == mPhotosSpoof) {
            boolean value = (Boolean) newValue;
            SystemProperties.set(SYS_PHOTOS_SPOOF, value ? "true" : "false");
            return true;
	 } else if (preference == mGamesSpoof) {
            boolean value = (Boolean) newValue;
            SystemProperties.set(SYS_GAMES_SPOOF, value ? "true" : "false");
            return true;
        } else if (preference == mStreamSpoof) {
            boolean value = (Boolean) newValue;
            SystemProperties.set(SYS_STREAM_SPOOF, value ? "true" : "false");
            return true;
	}
        return false;
    }

    public static void reset(Context mContext) {
        SystemProperties.set(SYS_GAMES_SPOOF, "false");
        SystemProperties.set(SYS_PHOTOS_SPOOF, "true");
        SystemProperties.set(SYS_STREAM_SPOOF, "true");
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.RESURRECTED;
    }

    /**
     * For Search.
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        new BaseSearchIndexProvider() {
            @Override
            public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean enabled) {
                ArrayList<SearchIndexableResource> result =
                    new ArrayList<SearchIndexableResource>();
                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.rr_parts;
                    result.add(sir);
                    return result;
            }

            @Override
            public List<String> getNonIndexableKeys(Context context) {
                List<String> keys = super.getNonIndexableKeys(context);
                    final Resources res = context.getResources();
                    boolean mSmartPixelsSupported = res.getBoolean(
                            com.android.internal.R.bool.config_supportSmartPixels);
                    if (!mSmartPixelsSupported)
                        keys.add("smart_pixels");
                return keys;
            }
        };
}
