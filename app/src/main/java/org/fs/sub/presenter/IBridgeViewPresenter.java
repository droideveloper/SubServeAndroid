package org.fs.sub.presenter;

import android.content.Intent;

/**
 * Created by Fatih on
 * as org.fs.sub.presenter.IBridgeViewPresenter
 */
public interface IBridgeViewPresenter {

    /**
     *
     * @param intent
     */
    void onReceiveIntent(Intent intent);

    /**
     *
     * @return
     */
    Intent serviceStart();

    /**
     *
     * @return
     */
    Intent serviceStop();

    /**
     *
     * @return
     */
    Intent serviceResume();

    /**
     *
     * @return
     */
    Intent servicePause();

    /**
     *
     * @param value
     * @return
     */
    Intent serviceElapsedTime(String value);

    /**
     *
     * @param uri
     * @param q
     * @return
     */
    Intent serviceSearch(String uri, boolean q);
}
