package org.fs.sub.common;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.preference.PreferenceDialogFragmentCompat;

import com.rarepebble.colorpicker.ColorPickerView;

/**
 * Created by Fatih on 13/05/16.
 */
public class ColorPreferenceDialogCompat extends PreferenceDialogFragmentCompat {

    private ColorPickerView picker;
    private ColorPreference colorPreference;

    public ColorPreferenceDialogCompat() {
    }

    public static ColorPreferenceDialogCompat newInstance(String key) {
        ColorPreferenceDialogCompat fragment = new ColorPreferenceDialogCompat();
        Bundle b = new Bundle(1);
        b.putString("key", key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onPrepareDialogBuilder(android.support.v7.app.AlertDialog.Builder builder) {
        picker = new ColorPickerView(getContext());
        colorPreference = getColorPreference();

        if(colorPreference != null) {
            picker.setColor(colorPreference.getPersistedColorInt());
            picker.showAlpha(colorPreference.isShowAlpha());
            picker.showHex(colorPreference.isShowHex());
            builder.setTitle(colorPreference.getTitle())
                   .setView(picker)
                   .setPositiveButton(colorPreference.getPositiveButtonText(), this);
            if (colorPreference.getSelectNoneButtonText() != null) {
                builder.setNeutralButton(colorPreference.getSelectNoneButtonText(), this);
            }
        }
        super.onPrepareDialogBuilder(builder);
    }

    private ColorPreference getColorPreference() { return (ColorPreference) this.getPreference(); }

    private int mWhichClicked;

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        mWhichClicked = which;
    }

    @Override
    public void onDialogClosed(boolean positiveSelected) {
        if(colorPreference != null) {
            if (positiveSelected) {
                final int color = picker.getColor();
                if(colorPreference.callChangeListener(color)) {
                    colorPreference.setColor(color);
                }
            } else {
                if(mWhichClicked == DialogInterface.BUTTON_NEUTRAL) {
                    if (colorPreference.callChangeListener(null)) {
                        colorPreference.setColor(null);
                    }
                }
            }
        }
    }
}
