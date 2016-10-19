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

/**
 * Created by Fatih on
 * as org.fs.sub.common.ConverterHelper
 */
public class ConverterHelper implements IConverter {

    private Subscription subscription = null;

    private final SrtEntity entity;

    public ConverterHelper(SrtEntity srtEntity) {
        PreconditionUtility.checkNotNull(srtEntity,
                                         "you are screwed! since srtEntity is null? = " + String.valueOf(srtEntity == null));
        this.entity = srtEntity;
    }

    @Override
    public void convert() {
        subscription = withSrtEntity(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess(), onError());
    }

    @Override
    public Observable<LinkedList<SrtSequenceEntity>> withSrtEntity(SrtEntity entity) {
        return Observable.just(entity)
                .flatMap(new Func1<SrtEntity, Observable<LinkedList<SrtSequenceEntity>>>() {
                    @Override
                    public Observable<LinkedList<SrtSequenceEntity>> call(SrtEntity entity) {
                        try {
                            return Observable.just(SrtSequenceEntity.create(entity.data(), entity.lang()));
                        } catch (IOException ioError) {
                            throw new AndroidException(ioError);
                        }
                    }
                });
    }

    @Override
    public Action1<LinkedList<SrtSequenceEntity>> onSuccess() {
        return new Action1<LinkedList<SrtSequenceEntity>>() {
            @Override
            public void call(LinkedList<SrtSequenceEntity> srtSequenceEntities) {
                cleanUp();
                BusManager.MAIN.post(new SequenceFoundEvent(srtSequenceEntities));
            }
        };
    }

    @Override
    public Action1<Throwable> onError() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                cleanUp();
                BusManager.MAIN.post(new SequenceNotFoundEvent());
            }
        };
    }

    private void cleanUp() {
        if(subscription != null) {
            subscription.unsubscribe();
        }
    }
}
