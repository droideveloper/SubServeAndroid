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

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;

import org.fs.core.AbstractIntentService;
import org.fs.sub.SubServeApplication;
import org.fs.sub.common.IPreference;
import org.fs.sub.net.IServiceEndpoint;
import org.fs.sub.presenter.ILoginServiceViewPresenter;
import org.fs.sub.presenters.LoginServiceViewPresenter;
import org.fs.sub.view.ISubServeView;
import org.fs.sub.view.ILoginServiceView;

public class LoginServiceView extends AbstractIntentService implements ILoginServiceView {

  public final static String KEY_COMMAND = "cmd";

  public final static int COMMAND_LOGIN = 0x1;
  public final static int COMMAND_SESSION = 0x2;
  public final static int COMMAND_LOGOUT = 0x3;

  private ILoginServiceViewPresenter presenterProxy = null;

  public LoginServiceView() {
    this(LoginServiceView.class.getSimpleName());
  }

  public LoginServiceView(String strName) {
    super(strName);
    presenterProxy = new LoginServiceViewPresenter(this);
  }

  @Override public void onHandleIntent(Intent intent) {
    if (presenterProxy != null) {
      presenterProxy.onCommand(intent.getExtras());
    }
    stopSelf();
  }

  @Override protected String getClassTag() {
    return LoginServiceView.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return SubServeApplication.isApplicationLogEnabled();
  }

  @Override public Context context() {
    return getApplicationContext();
  }

  @Override public ISubServeView anyApplication() {
    return (SubServeApplication) getApplication();
  }

  @Override public IPreference preferenceProxy() {
    return anyApplication().preferenceProxy();
  }

  @Override public IServiceEndpoint serviceProxy() {
    return anyApplication().serviceProxy();
  }

  @Override public AlarmManager alarmManager() {
    return (AlarmManager) context().getSystemService(Context.ALARM_SERVICE);
  }
}