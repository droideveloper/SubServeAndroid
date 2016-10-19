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

/**
 * Created by Fatih
 * as org.fs.sub.common.HashHelper
 */
public final class HashHelper implements Observer<Map<String, Object>> {

    private Subscription subscription = null;

    public void subscribe(@NonNull String uri) {
        subscription = createObservable(IHash.from(uri))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this);
    }

    public void unsubscribe() {
        if(subscription != null) {
            subscription.unsubscribe();
        }
    }

    private Observable<Map<String, Object>> createObservable(IHash hash) {
        return Observable.just(hash)
                         .flatMap(new Func1<IHash, Observable<Map<String, Object>>>() {
                             @Override
                             public Observable<Map<String, Object>> call(IHash hash) {
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

    @Override
    public void onCompleted() {
        unsubscribe();
    }

    @Override
    public void onError(Throwable e) {
        unsubscribe();
        BusManager.MAIN.post(new HashNotFoundEvent());
    }

    @Override
    public void onNext(Map<String, Object> map) {
        long size = (Long) map.get("size");
        String hash = (String) map.get("hash");
        BusManager.MAIN.post(new HashFoundEvent(hash, size));
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
        if(isLogEnabled()) {
            Log.println(lv, getClassTag(), str);
        }
    }
}
