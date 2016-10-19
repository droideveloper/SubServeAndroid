package org.fs.sub.usecase;

import org.fs.common.IUseCase;
import org.fs.sub.events.ServerTokenEvent;
import org.fs.sub.events.SrtEntityFoundEvent;
import org.fs.sub.events.SrtEntityNotFoundEvent;
import org.fs.xml.net.XMLRpcRequest;

import java.io.IOException;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Fatih on
 * as org.fs.sub.usecase.ISearchSrtEntityUseCase
 */
public interface ISearchSrtEntityUseCase extends IUseCase {

    /**
     *
     * @param errorEvent
     */
    void checkIfLocalError(SrtEntityNotFoundEvent errorEvent);

    /**
     *
     * @param request
     * @return
     * @throws IOException
     */
    String parse(XMLRpcRequest request) throws IOException;

    /**
     *
     * @param request
     * @return
     */
    Observable<String>  observerXMLRpcRequest(XMLRpcRequest request);

    /**
     *
     * @return
     */
    Action1<String>     successCallback();

    /**
     *
     * @return
     */
    Action1<Throwable>  errorCallback();

    /**
     *
     * @param srtEvent
     */
    void onSrtEntityFound(SrtEntityFoundEvent srtEvent);

    /**
     *
     * @param srtEvent
     */
    void onSrtEntityNotFound(SrtEntityNotFoundEvent srtEvent);

    /**
     *
     * @param tokenEvent
     */
    void onServerTokenValid(ServerTokenEvent tokenEvent);

}