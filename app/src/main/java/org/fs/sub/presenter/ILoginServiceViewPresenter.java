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
package org.fs.sub.presenter;

import android.os.Bundle;

import java.io.IOException;
import org.fs.common.IPresenter;

public interface ILoginServiceViewPresenter extends IPresenter {

  void withUserNameAndPassword(String userName, String password) throws IOException;

  void withSessionToken(String token) throws IOException;

  void withTerminateSession(String token) throws IOException;

  void onCommand(Bundle args);
}
