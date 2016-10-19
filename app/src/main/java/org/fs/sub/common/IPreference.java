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

public interface IPreference {

  /**
   * Whether notification required to start or not
   *
   * @return true or false
   */
  boolean isNotify();

  /**
   *
   * @return
   */
  String languages();

  /**
   *
   * @return
   */
  String userName();

  /**
   *
   * @return
   */
  String password();

  /**
   *
   * @return
   */
  int textColor();

  /**
   *
   * @return
   */
  int textSize();

  /**
   *
   * @return
   */
  String textAlignment();

  /**
   *
   * @return
   */
  String accessToken();

  /**
   *
   * @param token
   */
  void setAccessToken(String token);
}
