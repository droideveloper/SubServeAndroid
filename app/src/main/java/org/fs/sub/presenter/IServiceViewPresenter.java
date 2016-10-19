package org.fs.sub.presenter;

import android.content.Intent;
import android.net.Uri;

/**
 * Created by Fatih o
 * as org.fs.sub.presenters.IServicePresenter
 */
public interface IServiceViewPresenter {

    /**
     *
     * @param intent
     */
    void handleIntent(Intent intent);

    /**
     *
     * @param elapsedTime
     */
    void setElapsedTime(long elapsedTime);

    /**
     *
     * @return
     */
    long elapsedTime();

    /**
     *
     * @param uri
     */
    void setSearchUri(Uri uri);

    /**
     *
     * @param imdb
     */
    void setSearchImdb(String imdb);

    /**
     *
     */
    void notifyElapsedChanged();

    /**
     *
     */
    void startShow();

    /**
     *
     */
    void stopShow();

    /**
     *
     */
    void pauseShow();

    /**
     *
     */
    void resumeShow();

    /**
     * context callback onCreate
     */
    void onCreate();

    /**
     * context callback onResume
     */
    void onResume();

    /**
     * context callback onPause
     */
    void onPause();

    /**
     * context callback onDestroy
     */
    void onDestroy();
}
