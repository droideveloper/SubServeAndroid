package org.fs.sub.view;

import org.fs.sub.common.IPreference;
import org.fs.sub.database.IDatabaseHelper;
import org.fs.sub.net.IDownloadHelper;
import org.fs.sub.net.IServiceEndpoint;

/**
 * Created by Fatih on
 * as org.fs.sub.view.IAnySubtitlesView
 */
public interface ISubServeView {

    /**
     *
     * @return
     */
    IDatabaseHelper databaseProxy();

    /**
     *
     * @return
     */
    IDownloadHelper downloadProxy();

    /**
     *
     * @return
     */
    IServiceEndpoint serviceProxy();

    /**
     *
     * @return
     */
    IPreference             preferenceProxy();

}
