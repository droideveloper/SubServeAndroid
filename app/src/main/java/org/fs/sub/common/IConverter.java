package org.fs.sub.common;

import org.fs.sub.model.SrtEntity;
import org.fs.sub.model.SrtSequenceEntity;

import java.util.LinkedList;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Fatih on
 * as org.fs.sub.common.IConverter
 */
public interface IConverter {

    /**
     *
     * @param entity
     * @return
     */
    Observable<LinkedList<SrtSequenceEntity>>   withSrtEntity(SrtEntity entity);

    /**
     *
     * @return
     */
    Action1<LinkedList<SrtSequenceEntity>>      onSuccess();

    /**
     *
     * @return
     */
    Action1<Throwable>                          onError();


    /**
     *
     */
    void                                        convert();

}
