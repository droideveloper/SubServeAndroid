package org.fs.sub.usecases;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.otto.Subscribe;

import org.fs.common.BusManager;
import org.fs.exception.AndroidException;
import org.fs.sub.SubServeApplication;
import org.fs.sub.common.IPreference;
import org.fs.sub.database.IDatabaseHelper;
import org.fs.sub.event.IServiceEvent;
import org.fs.sub.events.HashFoundEvent;
import org.fs.sub.events.ServerTokenEvent;
import org.fs.sub.events.SrtEntityFoundEvent;
import org.fs.sub.events.SrtEntityNotFoundEvent;
import org.fs.sub.net.IDownloadHelper;
import org.fs.sub.net.IServiceEndpoint;
import org.fs.sub.usecase.ISearchSrtEntityUseCase;
import org.fs.util.StringUtility;
import org.fs.xml.net.XMLRpcRequest;
import org.fs.xml.net.XMLRpcResponse;
import org.fs.xml.type.XMLRpcArray;
import org.fs.xml.type.XMLRpcObject;
import org.fs.xml.type.XMLRpcParam;
import org.fs.xml.type.XMLRpcStruct;

import java.io.IOException;

import retrofit.Call;
import retrofit.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Fatih on
 * as org.fs.sub.usecases.SearchSrtEntityUseCase
 */
public class SearchSrtEntityUseCase implements ISearchSrtEntityUseCase {

    private final IServiceEvent    serviceEventProxy;
    private final IServiceEndpoint serviceProxy;
    private final IDatabaseHelper  databaseProxy;
    private final IDownloadHelper  downloadProxy;
    private final IPreference      prefProxy;

    private final HashFoundEvent            hashFoundEvent;
    private final String                    imdb;

    private int tryCount = 0;


    private final static String KEY_SUB_LANGUAGE_ID    = "sublanguageid";
    private final static String KEY_MOVIE_HASH         = "moviehash";
    private final static String KEY_MOVIE_BYTE_SIZE    = "moviebytesize";

    private final static String KEY_IMDB_ID            = "imdbid";

    private Subscription             subscription  = null;
    private SrtEntityNotFoundEvent   srtErrorEvent = null;

    public SearchSrtEntityUseCase(IServiceEvent serviceEventProxy,
                                  IServiceEndpoint serviceProxy,
                                  IDownloadHelper downloadProxy,
                                  HashFoundEvent hashFoundEvent,
                                  IPreference preferenceProxy,
                                  IDatabaseHelper databaseProxy, String imdb) {

        this.serviceEventProxy = serviceEventProxy;
        this.serviceProxy = serviceProxy;
        this.hashFoundEvent = hashFoundEvent;
        this.prefProxy = preferenceProxy;
        this.databaseProxy = databaseProxy;
        this.downloadProxy = downloadProxy;
        this.imdb = imdb;
    }

    @Override
    public void checkIfLocalError(final SrtEntityNotFoundEvent errorEvent) {
        if(errorEvent != null) {
            if(SrtEntityNotFoundEvent.LOCAL == errorEvent.getCode()) {

                String token = prefProxy.accessToken();
                if(StringUtility.isNullOrEmpty(token)) {
                    this.srtErrorEvent = errorEvent;
                    return;
                }

                XMLRpcRequest request = createRequest();

                subscription = observerXMLRpcRequest(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(successCallback(), errorCallback());

            } else if(SrtEntityNotFoundEvent.REMOTE == errorEvent.getCode()) {

            }
        }
    }

    private XMLRpcRequest createRequest() {
        XMLRpcRequest request;
        XMLRpcStruct XMLRpcStruct;
        XMLRpcArray XMLRpcArray;
        if(hashFoundEvent != null) {
            /*
                hash search method parameters are as below
             */
            XMLRpcStruct = new XMLRpcStruct()
                    .add(KEY_SUB_LANGUAGE_ID, XMLRpcObject.withValue(prefProxy.languages()))
                    .add(KEY_MOVIE_HASH, XMLRpcObject.withValue(hashFoundEvent.getHashString()))
                    .add(KEY_MOVIE_BYTE_SIZE, XMLRpcObject.withValue(hashFoundEvent.getFileSize()));

            XMLRpcArray = new XMLRpcArray()
                    .add(XMLRpcObject.withValue(XMLRpcStruct));

            request = new XMLRpcRequest()
                    .withMethodName(IServiceEndpoint.ApiConstants.METHOD_SEARCH_SUBTITLES)
                    .add(XMLRpcParam.withObject(XMLRpcObject.withValue(prefProxy.accessToken())))
                    .add(XMLRpcParam.withObject(XMLRpcObject.withValue(XMLRpcArray)));

            return request;
        } else if(!StringUtility.isNullOrEmpty(imdb)) {
            /*
                imdb id search method parameters are as below
             */
            XMLRpcStruct = new XMLRpcStruct()
                    .add(KEY_SUB_LANGUAGE_ID, XMLRpcObject.withValue(prefProxy.languages()))
                    .add(KEY_IMDB_ID, XMLRpcObject.withValue(imdb));

            XMLRpcArray = new XMLRpcArray()
                    .add(XMLRpcObject.withValue(XMLRpcStruct));

            request = new XMLRpcRequest()
                    .withMethodName(IServiceEndpoint.ApiConstants.METHOD_SEARCH_SUBTITLES)
                    .add(XMLRpcParam.withObject(XMLRpcObject.withValue(prefProxy.accessToken())))
                    .add(XMLRpcParam.withObject(XMLRpcObject.withValue(XMLRpcArray)));
            return request;
        }
        throw new AndroidException("both hashEvent and imdb is null, how we call this search function ?");
    }

    @Override
    public String parse(XMLRpcRequest request) throws IOException {
        if(serviceEventProxy != null) {
            Call<XMLRpcResponse> callRequest = serviceProxy.withSearchSubtitlesRequest(request);
            Response<XMLRpcResponse> response = callRequest.execute();
            if (response.isSuccess()) {
                final XMLRpcResponse xmlResponse = response.body();
                if (xmlResponse.isSuccess()) {
                    try {
                        XMLRpcStruct XMLRpcStruct = xmlResponse.firstParam().getValue().asStruct();
                        XMLRpcArray XMLRpcArray = XMLRpcStruct.getValue("data").asArray();
                        XMLRpcStruct = first(XMLRpcArray).asStruct();
                        String URL = XMLRpcStruct.getValue("SubDownloadLink").asString();//gzip link if available
                        if (StringUtility.isNullOrEmpty(URL)) {
                            URL = XMLRpcStruct.getValue("ZipDownloadLink").asString();
                        }
                        return URL;
                    } catch (Exception e) {
                        log(Log.ERROR, xmlResponse.toString());

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //SrtEntityNotFound on Remote due to and error
                                BusManager.MAIN.post(new SrtEntityNotFoundEvent(SrtEntityNotFoundEvent.REMOTE));
                            }
                        });
                    }
                } else {
                    log(xmlResponse.toString());
                    /*
                        malformed crap
                     */
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //SrtEntityNotFound on Remote due to and error
                            BusManager.MAIN.post(new SrtEntityNotFoundEvent(SrtEntityNotFoundEvent.REMOTE));
                        }
                    });
                }
            }
        }
        throw new AndroidException("network error");
    }

    private XMLRpcObject first(XMLRpcArray XMLRpcArray) {
        return XMLRpcArray.getValues().get(0);
    }

    @Override
    public Observable<String> observerXMLRpcRequest(XMLRpcRequest request) {
        return Observable.just(request)
                .flatMap(new Func1<XMLRpcRequest, Observable<String>>() {
                    @Override
                    public Observable<String> call(XMLRpcRequest request) {
                        try {
                            return Observable.just(parse(request));
                        } catch (IOException ioError) {
                            throw new AndroidException(ioError);
                        }
                    }
                });
    }

    @Override
    public Action1<String> successCallback() {
        return new Action1<String>() {
            @Override
            public void call(String URL) {
                if(subscription != null) {
                    subscription.unsubscribe();
                }
                if(hashFoundEvent != null) {
                    //search with movie hash
                    downloadProxy.withServiceProxyAndUrlAndHash(serviceProxy,
                                                                URL,
                                                                hashFoundEvent.getHashString(),
                                                                prefProxy.languages());
                } else if(!StringUtility.isNullOrEmpty(imdb)) {
                    //search with imdb_id
                    downloadProxy.withServiceProxyAndUrlAndImdb(serviceProxy,
                                                                URL,
                                                                imdb,
                                                                prefProxy.languages());
                }
            }
        };
    }

    @Override
    public Action1<Throwable> errorCallback() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if(subscription != null) {
                    subscription.unsubscribe();
                }
                checkTryCountAndRequestAgain();
            }
        };
    }

    /**
     * checks error count and might try again if needed
     */
    private void checkTryCountAndRequestAgain() {
        Handler handler = new Handler(Looper.getMainLooper());
        tryCount++;//then we check if we hit 3
        if(tryCount >= 3) {
            srtErrorEvent = null;//clean up event too
            tryCount = 0;//stop trying this
            handler.post(new Runnable() {
                @Override
                public void run() {
                    BusManager.MAIN.unregister(SearchSrtEntityUseCase.this);
                }
            });
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkIfLocalError(srtErrorEvent);
                }
            }, 500);//every half a second we try request
        }
    }

    @Override
    public void execute() {
        BusManager.MAIN.register(this);
        if(databaseProxy != null) {
            //hash search
            if(hashFoundEvent != null) {
                databaseProxy.withHashAndLangAndFirst(hashFoundEvent.getHashString(),
                                                      prefProxy.languages());
            }
            //imdb search
            else if(!StringUtility.isNullOrEmpty(imdb)) {
                databaseProxy.withImdbAndLangAndFirst(imdb,
                                                      prefProxy.languages());
            }
        }
    }

    @Subscribe
    @Override
    public void onSrtEntityNotFound(SrtEntityNotFoundEvent srtEvent) {
        if(serviceEventProxy != null) {
            serviceEventProxy.onSrtEntityNotFound(srtEvent);
        }
    }

    @Subscribe
    @Override
    public void onSrtEntityFound(SrtEntityFoundEvent srtEvent) {
        BusManager.MAIN.unregister(this);
        if(serviceEventProxy != null) {
            serviceEventProxy.onSrtEntityFound(srtEvent);
        }
    }

    @Subscribe
    @Override
    public void onServerTokenValid(ServerTokenEvent tokenEvent) {
        log(Log.ERROR, "we received token let's check if we have previous event waiting for us");
        if(srtErrorEvent != null) {
            checkIfLocalError(srtErrorEvent);//this way we proceed with proper way.
        }
    }

    protected String getClassTag() {
        return SearchSrtEntityUseCase.class.getSimpleName();
    }

    protected void log(String text) {
        log(Log.DEBUG, text);
    }

    protected void log(int lv, String text) {
        if(SubServeApplication.isApplicationLogEnabled()) {
            Log.println(lv, getClassTag(), text);
        }
    }
}