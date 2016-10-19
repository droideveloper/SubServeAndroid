package org.fs.sub.view;

import android.app.AlarmManager;
import android.content.Context;

import org.fs.common.IView;
import org.fs.sub.common.IPreference;
import org.fs.sub.net.IServiceEndpoint;

/**
 * Created by Fatih on
 * as org.fs.sub.view.ILoginServiceView
 */
public interface ILoginServiceView extends IView {

    /**
     *
     * @return
     */
    Context context();

    /**
     *
     * @return
     */
    ISubServeView anyApplication();

    /**
     *
     * @return
     */
    IPreference preferenceProxy();

    /**
     *
     * @return
     */
    IServiceEndpoint serviceProxy();

    /**
     *
     * @return
     */
    AlarmManager alarmManager();
}