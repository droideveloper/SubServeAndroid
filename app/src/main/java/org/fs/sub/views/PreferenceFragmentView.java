/*
 * SubServe Android Copyright (C) 2016 Fatih.
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
package org.fs.sub.views;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;

import org.fs.core.AbstractPreferenceFragment;
import org.fs.sub.SubServeApplication;
import org.fs.sub.R;
import org.fs.sub.common.ColorPreference;
import org.fs.sub.common.ColorPreferenceDialogCompat;
import org.fs.sub.view.IPreferenceFragmentView;

public class PreferenceFragmentView extends AbstractPreferenceFragment implements IPreferenceFragmentView {

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);
  }

  @Override public void onCreatePreferences(Bundle bundle, String s) { }

  @Override public void onDisplayPreferenceDialog(Preference preference) {
    //if you want to show custom view on preference click, then do not forget to implement this
    if (preference instanceof ColorPreference) {
      DialogFragment f = ColorPreferenceDialogCompat.newInstance(preference.getKey());
      f.setTargetFragment(this, 0);
      f.show(getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
    } else {
      super.onDisplayPreferenceDialog(preference);
    }
  }

  @Override protected String getClassTag() {
    return PreferenceFragmentView.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return SubServeApplication.isApplicationLogEnabled();
  }
}