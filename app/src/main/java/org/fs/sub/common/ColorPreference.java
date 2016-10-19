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
package org.fs.sub.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


public class ColorPreference extends DialogPreference {
  private final String selectNoneButtonText;
  private final Integer defaultColor;
  private final String noneSelectedSummaryText;
  private final CharSequence summaryText;
  private final boolean showAlpha;
  private final boolean showHex;
  private View thumbnail;

  public ColorPreference(Context context) {
    this(context, null);
  }

  public ColorPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    summaryText = super.getSummary();

    if (attrs != null) {
      TypedArray a = context.getTheme()
          .obtainStyledAttributes(attrs, com.rarepebble.colorpicker.R.styleable.ColorPicker, 0, 0);
      selectNoneButtonText = a.getString(com.rarepebble.colorpicker.R.styleable.ColorPicker_colorpicker_selectNoneButtonText);
      noneSelectedSummaryText = a.getString(com.rarepebble.colorpicker.R.styleable.ColorPicker_colorpicker_noneSelectedSummaryText);
      defaultColor =
          a.hasValue(com.rarepebble.colorpicker.R.styleable.ColorPicker_colorpicker_defaultColor) ? a.getColor(
              com.rarepebble.colorpicker.R.styleable.ColorPicker_colorpicker_defaultColor, Color.GRAY) : null;
      showAlpha = a.getBoolean(com.rarepebble.colorpicker.R.styleable.ColorPicker_colorpicker_showAlpha,
          true);
      showHex = a.getBoolean(com.rarepebble.colorpicker.R.styleable.ColorPicker_colorpicker_showHex,
          true);
    } else {
      selectNoneButtonText = null;
      defaultColor = null;
      noneSelectedSummaryText = null;
      showAlpha = true;
      showHex = true;
    }
  }

  @Override public void onBindViewHolder(PreferenceViewHolder holder) {
    thumbnail = addThumbnail(holder.itemView);
    showColor(getPersistedIntDefaultOrNull());
    super.onBindViewHolder(holder);
  }

  private View addThumbnail(View view) {
    LinearLayout widgetFrameView = ((LinearLayout) view.findViewById(android.R.id.widget_frame));
    widgetFrameView.setVisibility(View.VISIBLE);
    widgetFrameView.removeAllViews();
    LayoutInflater.from(getContext())
        .inflate(isEnabled() ? com.rarepebble.colorpicker.R.layout.color_preference_thumbnail : com.rarepebble.colorpicker.R.layout.color_preference_thumbnail_disabled,
            widgetFrameView);
    return widgetFrameView.findViewById(com.rarepebble.colorpicker.R.id.thumbnail);
  }

  private Integer getPersistedIntDefaultOrNull() {
    return getSharedPreferences().contains(getKey()) ? Integer.valueOf(getPersistedInt(Color.GRAY))
        : defaultColor;
  }

  private void showColor(Integer color) {
    Integer thumbColor = color == null ? defaultColor : color;
    if (thumbnail != null) {
      thumbnail.setVisibility(thumbColor == null ? View.GONE : View.VISIBLE);
      thumbnail.findViewById(com.rarepebble.colorpicker.R.id.colorPreview)
          .setBackgroundColor(thumbColor == null ? 0 : thumbColor);
    }
    if (noneSelectedSummaryText != null) {
      setSummary(thumbColor == null ? noneSelectedSummaryText : summaryText);
    }
  }

  public int getPersistedColorInt() {
    return getPersistedInt(defaultColor == null ? Color.GRAY : defaultColor);
  }

  public boolean isShowAlpha() {
    return showAlpha;
  }

  public boolean isShowHex() {
    return showHex;
  }

  public String getSelectNoneButtonText() {
    return selectNoneButtonText;
  }

  private void removeSetting() {
    if (shouldPersist()) {
      getSharedPreferences().edit().remove(getKey()).apply();
    }
  }

  public void setColor(Integer color) {
    if (color == null) {
      removeSetting();
    } else {
      persistInt(color);
    }
    showColor(color);
  }

  public Integer getColor() {
    return getPersistedIntDefaultOrNull();
  }
}