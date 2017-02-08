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
package org.fs.sub.usecase;

import org.fs.common.IUseCase;
import org.fs.sub.events.ServerTokenEvent;
import org.fs.sub.events.SrtEntityFoundEvent;
import org.fs.sub.events.SrtEntityNotFoundEvent;
import org.fs.xml.net.XMLRpcRequest;

import java.io.IOException;

import rx.Observable;
import rx.functions.Action1;

public interface ISearchSrtEntityUseCase extends IUseCase {

  void checkIfLocalError(SrtEntityNotFoundEvent errorEvent);

  String parse(XMLRpcRequest request) throws IOException;

  Observable<String> observerXMLRpcRequest(XMLRpcRequest request);

  Action1<String> successCallback();

  Action1<Throwable> errorCallback();

  void execute();

  void onSrtEntityFound(SrtEntityFoundEvent srtEvent);

  void onSrtEntityNotFound(SrtEntityNotFoundEvent srtEvent);

  void onServerTokenValid(ServerTokenEvent tokenEvent);
}