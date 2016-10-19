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

import android.support.annotation.NonNull;
import android.util.Log;

import org.fs.common.BusManager;
import org.fs.exception.AndroidException;
import org.fs.sub.SubServeApplication;
import org.fs.sub.events.HashFoundEvent;
import org.fs.sub.events.HashNotFoundEvent;
import org.fs.sub.utils.hash.IHash;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public final class HashHelper implements Observer<Map<String, Object>> {

  private Subscription subscription = null;

  public void subscribe(@NonNull String uri) {
    subscription = createObservable(IHash.from(uri)).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this);
  }

  public void unsubscribe() {
    if (subscription != null) {
      subscription.unsubscribe();
    }
  }

  private Observable<Map<String, Object>> createObservable(IHash hash) {
    return Observable.just(hash).flatMap(new Func1<IHash, Observable<Map<String, Object>>>() {
      @Override public Observable<Map<String, Object>> call(IHash hash) {
        try {
          Map<String, Object> map = new HashMap<>();
          map.put("hash", toHexString(hash.invoke()));
          map.put("size", hash.fileSize());
          return Observable.just(map);
        } catch (IOException ioe) {
          throw new AndroidException(ioe);
        }
      }
    });
  }

  private String toHexString(long h) {
    return String.format(Locale.US, "%016x", h);
  }

  @Override public void onCompleted() {
    unsubscribe();
  }

  @Override public void onError(Throwable e) {
    unsubscribe();
    BusManager.send(new HashNotFoundEvent());
  }

  @Override public void onNext(Map<String, Object> map) {
    long size = (Long) map.get("size");
    String hash = (String) map.get("hash");
    BusManager.send(new HashFoundEvent(hash, size));
  }

  protected String getClassTag() {
    return HashHelper.class.getSimpleName();
  }

  protected boolean isLogEnabled() {
    return SubServeApplication.isApplicationLogEnabled();
  }

  protected void log(Exception e) {
    StringWriter strWriter = new StringWriter();
    PrintWriter prtWriter = new PrintWriter(strWriter);
    e.printStackTrace(prtWriter);
    log(Log.ERROR, strWriter.toString());
  }

  protected void log(String str) {
    log(Log.DEBUG, str);
  }

  protected void log(int lv, String str) {
    if (isLogEnabled()) {
      Log.println(lv, getClassTag(), str);
    }
  }
}
