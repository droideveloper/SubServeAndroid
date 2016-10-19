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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.preference.PreferenceManager;

import org.fs.common.BusManager;
import org.fs.sub.events.ServerTokenEvent;
import org.fs.util.StringUtility;

public final class PreferenceHelper implements IPreference {

  public final static String PREF_NOTIFY = "checkNotify";
  public final static String PREF_LANGUAGES = "listLanguage";
  public final static String PREF_TEXT_SIZE = "listTextSize";
  public final static String PREF_USER_NAME = "txtUserName";
  public final static String PREF_PASSWORD = "txtPassword";
  public final static String PREF_ACCESS_TOKEN = "accessToken";
  public final static String PREF_TEXT_COLOR = "textColor";
  public final static String PREF_TEXT_ALIGNMENT = "textAlignment";

  private SharedPreferences sharedPreferences = null;

  public PreferenceHelper(Context context) {
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
  }

  @Override public boolean isNotify() {
    return sharedPreferences != null && sharedPreferences.getBoolean(PREF_NOTIFY, false);
  }

  @Override public String languages() {
    return sharedPreferences != null ? sharedPreferences.getString(PREF_LANGUAGES, "eng") : "eng";//default is eng
  }

  @Override public String userName() {
    return sharedPreferences != null ? sharedPreferences.getString(PREF_USER_NAME, "") : "";
  }

  @Override public String password() {
    return sharedPreferences != null ? sharedPreferences.getString(PREF_PASSWORD, "") : "";
  }

  @Override public int textSize() {
    return sharedPreferences != null ? Integer.parseInt(
        sharedPreferences.getString(PREF_TEXT_SIZE, "16")) : 16; //will be converted
  }

  @Override public String accessToken() {
    return sharedPreferences != null ? sharedPreferences.getString(PREF_ACCESS_TOKEN, "") : "";
  }

  @Override public int textColor() {
    return sharedPreferences != null ? sharedPreferences.getInt(PREF_TEXT_COLOR, Color.WHITE) : Color.WHITE;
  }

  @Override public String textAlignment() {
    return sharedPreferences != null ? sharedPreferences.getString(PREF_TEXT_ALIGNMENT, "bottom") : "bottom";
  }

  @Override public void setAccessToken(String token) {
    sharedPreferences.edit().putString(PREF_ACCESS_TOKEN, token).apply();

    //if we get new handler we post an event
    if (!StringUtility.isNullOrEmpty(token)) {
      Handler handler = new Handler(Looper.getMainLooper());
      handler.post(new Runnable() {
        @Override public void run() {
          BusManager.send(new ServerTokenEvent());
        }
      });
    }
  }
}
