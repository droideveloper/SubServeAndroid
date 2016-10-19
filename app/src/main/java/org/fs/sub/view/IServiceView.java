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
package org.fs.sub.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.text.SpannableString;

import org.fs.common.IView;

public interface IServiceView extends IView {

  /**
   * @return proxy instance for AnySubtitlesView for future use and access some stuff from in it
   * such as WebService, DatabaseHelper etc..
   */
  ISubServeView applicationProxy();

  /**
   * @return gets current Context instance
   */
  Context getContext();

  /**
   * @param intent sends intent to LoginService nothing else (LoginService is intent service so..)
   */
  void toServiceCommand(Intent intent);

  /**
   *
   * @return
   */
  NotificationManagerCompat notificationManager();

  /**
   * @param str text
   */
  void setText(SpannableString str);

  /**
   *
   * @param str text
   * @param state {bold, italic, none}
   */
  //void setText(SpannableString str, TextState state);

  /**
   * @return text currently in show
   */
  String getText();

  /**
   * stop (stop application running no coming back)
   */
  void stopShow();

  /**
   * pause
   */
  void pauseShow();

  /**
   * resume
   */
  void resumeShow();
}
