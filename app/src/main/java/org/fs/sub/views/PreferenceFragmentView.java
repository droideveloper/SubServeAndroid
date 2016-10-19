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

/**
 * Created by Fatih on
 * as org.fs.sub.views.PreferenceFragmentView
 */
public class PreferenceFragmentView extends AbstractPreferenceFragment implements IPreferenceFragmentView {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) { }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        //if you want to show custom view on preference click, then do not forget to implement this
        if(preference instanceof ColorPreference) {
            DialogFragment f = ColorPreferenceDialogCompat.newInstance(preference.getKey());
            f.setTargetFragment(this, 0);
            f.show(getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    protected String getClassTag() {
        return PreferenceFragmentView.class.getSimpleName();
    }

    @Override
    protected boolean isLogEnabled() {
        return SubServeApplication.isApplicationLogEnabled();
    }
}