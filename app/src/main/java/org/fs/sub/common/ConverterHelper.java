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

import org.fs.common.BusManager;
import org.fs.exception.AndroidException;
import org.fs.sub.events.SequenceFoundEvent;
import org.fs.sub.events.SequenceNotFoundEvent;
import org.fs.sub.model.SrtEntity;
import org.fs.sub.model.SrtSequenceEntity;
import org.fs.util.PreconditionUtility;

import java.io.IOException;
import java.util.LinkedList;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ConverterHelper implements IConverter {

  private Subscription subscription = null;

  private final SrtEntity entity;

  public ConverterHelper(SrtEntity srtEntity) {
    PreconditionUtility.checkNotNull(srtEntity,
        "you are screwed! since srtEntity is null? = " + String.valueOf(srtEntity == null));
    this.entity = srtEntity;
  }

  @Override public void convert() {
    subscription = withSrtEntity(entity).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(onSuccess(), onError());
  }

  @Override public Observable<LinkedList<SrtSequenceEntity>> withSrtEntity(SrtEntity entity) {
    return Observable.just(entity).flatMap(new Func1<SrtEntity, Observable<LinkedList<SrtSequenceEntity>>>() {
      @Override public Observable<LinkedList<SrtSequenceEntity>> call(SrtEntity entity) {
        try {
          return Observable.just(SrtSequenceEntity.create(entity.data(), entity.lang()));
        } catch (IOException ioError) {
          throw new AndroidException(ioError);
        }
      }
    });
  }

  @Override public Action1<LinkedList<SrtSequenceEntity>> onSuccess() {
    return new Action1<LinkedList<SrtSequenceEntity>>() {
      @Override public void call(LinkedList<SrtSequenceEntity> srtSequenceEntities) {
        cleanUp();
        BusManager.send(new SequenceFoundEvent(srtSequenceEntities));
      }
    };
  }

  @Override public Action1<Throwable> onError() {
    return new Action1<Throwable>() {
      @Override public void call(Throwable throwable) {
        cleanUp();
        BusManager.send(new SequenceNotFoundEvent());
      }
    };
  }

  private void cleanUp() {
    if (subscription != null) {
      subscription.unsubscribe();
    }
  }
}
