package org.fs.sub.presenters;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.SpannableString;
import android.util.Log;

import com.squareup.otto.Subscribe;

import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.sub.R;
import org.fs.sub.SubServeApplication;
import org.fs.sub.common.ConverterHelper;
import org.fs.sub.common.HashHelper;
import org.fs.sub.common.IConverter;
import org.fs.sub.common.IPreference;
import org.fs.sub.database.IDatabaseHelper;
import org.fs.sub.event.IServiceEvent;
import org.fs.sub.events.ApplicationState;
import org.fs.sub.events.HashFoundEvent;
import org.fs.sub.events.HashNotFoundEvent;
import org.fs.sub.events.SequenceFoundEvent;
import org.fs.sub.events.SequenceNotFoundEvent;
import org.fs.sub.events.SrtEntityFoundEvent;
import org.fs.sub.events.SrtEntityNotFoundEvent;
import org.fs.sub.model.SrtSequenceEntity;
import org.fs.sub.net.IDownloadHelper;
import org.fs.sub.net.IServiceEndpoint;
import org.fs.sub.presenter.IServiceViewPresenter;
import org.fs.sub.usecase.ISearchSrtEntityUseCase;
import org.fs.sub.usecases.SearchSrtEntityUseCase;
import org.fs.sub.utils.CollectionIterator;
import org.fs.sub.view.IServiceView;
import org.fs.sub.views.BridgeBroadcastView;
import org.fs.sub.views.LoginServiceView;
import org.fs.sub.views.ServiceView;
import org.fs.util.StringUtility;

/**
 * Created by Fatih on
 * as org.fs.sub.presenters.ServiceViewPresenter
 */
public class ServiceViewPresenter extends AbstractPresenter<IServiceView> implements IServiceViewPresenter, IServiceEvent {

    private final static int NOTIFICATION_ID   = 0x999;
    public final static long EXPECTED_INTERVAL = 50L;
    public final static long SESSION_INTERVAL  = 14 * 60 * 1000L; //14 mins

    private       ApplicationState         currentState;
    private       SrtSequenceEntity        currentSequence             = null;
    private       ISearchSrtEntityUseCase  searchSrtEntityUseCaseProxy = null;
    private final Handler                  backgroundHandler           = new Handler(Looper.getMainLooper());
    private final Object                   mutex;

    private       CollectionIterator       collection                  = null;
    /*
        this is the session control, keep making request available
     */
    private final Runnable                 sessionWorker               = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(view.getContext(), LoginServiceView.class);
            intent.putExtra(LoginServiceView.KEY_COMMAND, LoginServiceView.COMMAND_SESSION);
            view.toServiceCommand(intent);
            backgroundHandler.postDelayed(this, SESSION_INTERVAL);//session update
        }
    };

    /*
        this is elapsed time control, keep showing text available
     */
    private final Runnable                 backgroundWorker            = new Runnable() {
        @Override
        public void run() {
            setElapsedTime(elapsedTime() + EXPECTED_INTERVAL);
            backgroundHandler.postDelayed(this, EXPECTED_INTERVAL);
        }
    };

    private long elapsedTime = 0L;

    public ServiceViewPresenter() {
        mutex = this;
        currentState = ApplicationState.STATE_STOP;
    }

    public ServiceViewPresenter(IServiceView view) {
        super(view);
        currentState = ApplicationState.STATE_STOP;
        mutex = this;
    }

    @Override
    protected String getClassTag() {
        return ServiceViewPresenter.class.getSimpleName();
    }

    @Override
    protected boolean isLogEnabled() {
        return SubServeApplication.isApplicationLogEnabled();
    }

    @Override
    public void handleIntent(Intent intent) {
        int cmdKey = intent.getIntExtra(BridgeBroadcastView.EXTRA_COMMAND, -1);
        if (cmdKey == BridgeBroadcastView.CMD_START) {
            currentState = ApplicationState.STATE_START;
            startShow();
        } else if (cmdKey == BridgeBroadcastView.CMD_STOP) {
            currentState = ApplicationState.STATE_STOP;
            stopShow();
        } else if (cmdKey == BridgeBroadcastView.CMD_RESUME) {
            if(currentState.equals(ApplicationState.STATE_STOP)) {
                currentState = ApplicationState.STATE_START;
                resumeShow();//actually change state of app do nothing else
            }
        } else if (cmdKey == BridgeBroadcastView.CMD_PAUSE) {
            if(currentState.equals(ApplicationState.STATE_START)) {
                currentState = ApplicationState.STATE_STOP;
                pauseShow();//same here we do nothing but only thing to do is change state nothing else
            }
        } else if (cmdKey == BridgeBroadcastView.CMD_SEARCH) {
            if(currentState.equals(ApplicationState.STATE_START)) {
                //this part is search with hash
                if (intent.hasExtra(BridgeBroadcastView.EXTRA_QUERY)) {
                    String URL = intent.getStringExtra(BridgeBroadcastView.EXTRA_QUERY);
                    setSearchUri(Uri.parse(URL));
                    //this part is search with imdb_id
                } else if (intent.hasExtra(BridgeBroadcastView.EXTRA_QUERY_2)) {
                    String imdb = intent.getStringExtra(BridgeBroadcastView.EXTRA_QUERY_2);
                    setSearchImdb(imdb);
                }
            }
        } else if (cmdKey == BridgeBroadcastView.CMD_ELAPSED) {
            if(currentState.equals(ApplicationState.STATE_START)) {
                long time = intent.getLongExtra(BridgeBroadcastView.EXTRA_QUERY, 0L);
                setElapsedTime(time);
            }
        }

        /*log(Log.ERROR,
            String.format(Locale.ENGLISH, "intent={ %s }\nstate={ %s }", intent.toString(), currentState));*/
    }

    @Override
    public void setSearchUri(Uri uri) {
        if(StringUtility.isNullOrEmpty(uri)) {
            log(Log.ERROR, "uri is null ? " + String.valueOf(StringUtility.isNullOrEmpty(uri)));
        } else {
            HashHelper hashHelperProxy = new HashHelper();
            hashHelperProxy.subscribe(uri.toString());
        }
    }

    @Override
    public void setSearchImdb(String imdb) {
        if(StringUtility.isNullOrEmpty(imdb)) {
            log(Log.ERROR, "imdb id is null ? " + String.valueOf(StringUtility.isNullOrEmpty(imdb)));
        } else {
            searchSrtEntityUseCaseProxy = createSearchUseCase(imdb);
            searchSrtEntityUseCaseProxy.execute();
        }
    }

    @Override
    public void setElapsedTime(long elapsedTime) {
        synchronized (mutex) {
            this.elapsedTime = elapsedTime;
        }
        notifyElapsedChanged();
    }

    @Override
    public long elapsedTime() {
        synchronized (mutex) {
            return elapsedTime;
        }
    }

    @Override
    public void notifyElapsedChanged() {
        checkIfTextMatch();
    }

    @Override
    public void startShow() {
        setUpSession(); //log in user
        resumeShow();
    }

    @Override
    public void stopShow() {
        tearDownSession(); //log out user
        tearDownElapsedIntent();
        view.stopShow();
    }

    @Override
    public void pauseShow() {
        tearDownElapsedIntent();
        view.pauseShow();
    }

    @Override
    public void resumeShow() {
        clearIfNecessaryAndSetUpNext();
        view.resumeShow();
    }

    @Override
    public void onCreate() {
        notifyUser();
        BusManager.MAIN.register(this);
    }

    @Override
    public void onDestroy() {
        clearNotify();
        BusManager.MAIN.unregister(this);
    }

    /*
        These life-cycle callbacks not been called
        because component is Service instance
     */
    @Override
    public void onResume()  {}
    @Override
    public void onPause()   {}
    /*
        Non-Used component callbacks end here
     */

    private IDatabaseHelper databaseProxy() {
        return view.applicationProxy().databaseProxy();
    }

    private IServiceEndpoint serviceProxy() {
        return view.applicationProxy().serviceProxy();
    }

    private IPreference preferenceProxy() {
        return view.applicationProxy().preferenceProxy();
    }

    private IDownloadHelper downloadProxy() {
        return view.applicationProxy().downloadProxy();
    }

    /*
        search in local database
     */
    private ISearchSrtEntityUseCase createSearchUseCase(HashFoundEvent hashFoundEvent) {
        return new SearchSrtEntityUseCase(this, serviceProxy(), downloadProxy(),
                                           hashFoundEvent, preferenceProxy(), databaseProxy(), null);
    }

    private ISearchSrtEntityUseCase createSearchUseCase(String imdb) {
        return new SearchSrtEntityUseCase(this, serviceProxy(), downloadProxy(),
                                          null, preferenceProxy(), databaseProxy(), imdb);
    }

    /**
     * sets up session
     * that service will handle our
     * login mechanism to use serviceProxy
     */
    private void setUpSession() {
        backgroundHandler.postDelayed(sessionWorker, SESSION_INTERVAL);//session start
        Intent intent = new Intent(view.getContext(), LoginServiceView.class);
        intent.putExtra(LoginServiceView.KEY_COMMAND, LoginServiceView.COMMAND_LOGIN);
        view.toServiceCommand(intent);
    }

    /**
     * tears down session
     * that service will handle our
     * logout mechanism as good citizen
     */
    private void tearDownSession() {
        backgroundHandler.removeCallbacks(sessionWorker);//session cancel
        Intent intent = new Intent(view.getContext(), LoginServiceView.class);
        intent.putExtra(LoginServiceView.KEY_COMMAND, LoginServiceView.COMMAND_LOGOUT);
        view.toServiceCommand(intent);
    }

    /**
     * method is light-weight algorithm for pooling sequence in order to chekc and show in proper time zone
     */
    private void checkIfTextMatch() {
        if(collection != null) {
            if(StringUtility.isNullOrEmpty(currentSequence)) {
                currentSequence = collection.next();
                if(StringUtility.isNullOrEmpty(currentSequence)) {
                    return;
                }
            }
            long ms = elapsedTime();
            if (SrtSequenceEntity.isNotYet(ms, currentSequence)) {
                checkIfTimeIsElastic(ms);
                view.setText(new SpannableString(""));
            } else if (SrtSequenceEntity.isOverDue(ms, currentSequence)) {
                checkIfTimeIsElastic(ms);
                view.setText(new SpannableString(""));
                currentSequence = null;
            } else if (SrtSequenceEntity.isBetween(ms, currentSequence)) {
                SpannableString str = currentSequence.getText();
                if(!textIfAlreadySet(str)) {
                    view.setText(str);
                }
            }
        }
    }

    /**
     * finds where we left of since the api try to go forward always yet sometimes users can skip back or further so...
     * @param ms current ms stated from application
     */
    private void checkIfTimeIsElastic(long ms) {
        synchronized (mutex) {
            if (collection != null) {
                SrtSequenceEntity sq = collection.find(ms);
                if (!StringUtility.isNullOrEmpty(sq)) {
                    currentSequence = sq;
                }
            }
        }
    }

    /**
     *
     * @param str check if text is there.
     * @return SpannableString instance.
     */
    private boolean textIfAlreadySet(SpannableString str) {
        String text = str.toString();
        return text.equalsIgnoreCase(view.getText());
    }

    /**
     *
     */
    private void clearIfNecessaryAndSetUpNext() {
        backgroundHandler.removeCallbacks(backgroundWorker);
        backgroundHandler.postDelayed(backgroundWorker, EXPECTED_INTERVAL);
    }

    private void tearDownElapsedIntent() {
        backgroundHandler.removeCallbacks(backgroundWorker);
    }

    private void notifyUser() {
        Intent intent = new Intent(view.getContext(), ServiceView.class);
        intent.putExtra(BridgeBroadcastView.EXTRA_COMMAND, BridgeBroadcastView.CMD_STOP);

        PendingIntent pendingIntent = PendingIntent.getService(view.getContext(),
                                                               0, intent,
                                                               PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(view.getContext())
                .setContentTitle(getString(R.string.text_notification_stop_title))
                .setContentText(getString(R.string.text_notification_stop_content))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_stat_stop)
                .setAutoCancel(true);

        NotificationManagerCompat notifyManager = view.notificationManager();
        notifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    private CharSequence getString(@StringRes int id) {
        return view.getContext().getString(id);
    }

    private void clearNotify() {
        NotificationManagerCompat notifyManager = view.notificationManager();
        notifyManager.cancel(NOTIFICATION_ID);
    }

    /*
        Event Callback
     */
    @Subscribe
    @Override
    public void onHashFound(HashFoundEvent hashEvent) {
        if(hashEvent != null) {
            searchSrtEntityUseCaseProxy = createSearchUseCase(hashEvent);
            searchSrtEntityUseCaseProxy.execute();
        }
    }

    @Subscribe
    @Override
    public void onHashNotFound(HashNotFoundEvent hashEvent) {
        //this is not good part because we could not compute hash
        // for these reasons
            // 1- IO error which is local or remote file
                // a- security error (we have no right to touch file
                // b- network problem on device
                // c- remote file server is down
            // 2- Memory error which is awful
        //show message to user
        //we can not get hash, then we can not do anything for now...
        log(Log.ERROR,
            "we can not get hash for unknown reason...");
        stopShow();//ship has sink :(
    }

    @Subscribe
    @Override
    public void onSequenceFound(SequenceFoundEvent sequenceEvent) {
        //we received profiled texts to show in text view
        if(sequenceEvent != null) {
            collection = new CollectionIterator(sequenceEvent.getQueue());
        }
    }

    @Subscribe
    @Override
    public void onSequenceNotFound(SequenceNotFoundEvent sequenceEvent) {
        //in this part of place we created SrtEntity to read but something went wrong
           //for these reasons
                // 1 - IO error which is less likely to occur but who knows
                // 2 - Parse error there is thing that caused my parsing algorithm to go down
                    // must be 0 index is number
                    // must be 1 index is interval max and min
                    // must be 2 or more index is text show
        //show message to user ?
        log(Log.ERROR,
            "we get srt but it might be malformed? we could not locate problem, also might be I/O error.");
        stopShow();//abandon hopes :(
    }

    /*
       Callback but not received from BusManager
     */
    @Override
    public void onSrtEntityFound(SrtEntityFoundEvent srtEvent) {
        if(srtEvent != null) {
            IConverter converter = new ConverterHelper(srtEvent.srtEntity());
            converter.convert();
        }
    }

    @Override
    public void onSrtEntityNotFound(SrtEntityNotFoundEvent srtEvent) {
        if(searchSrtEntityUseCaseProxy != null) {
            searchSrtEntityUseCaseProxy.checkIfLocalError(srtEvent);
        }
    }
}