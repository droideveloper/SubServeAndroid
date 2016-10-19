package org.fs.sub.views;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.fs.core.AbstractBroadcastReceiver;
import org.fs.sub.SubServeApplication;
import org.fs.sub.presenter.IBridgeViewPresenter;
import org.fs.sub.presenters.BridgeViewPresenter;
import org.fs.sub.view.IBridgeView;

import java.lang.ref.WeakReference;


/**
 * Created by Fatih on
 * as org.fs.sub.views.BridgeBroadcast
 */
public class BridgeBroadcastView extends AbstractBroadcastReceiver implements IBridgeView {

    public static final int CMD_SEARCH  = 1;
    public static final int CMD_ELAPSED = 2;
    public static final int CMD_START   = 3;
    public static final int CMD_STOP    = 4;
    public static final int CMD_PAUSE   = 5;
    public static final int CMD_RESUME  = 6;

    public static final String EXTRA_COMMAND        = "cmd";
    public static final String EXTRA_QUERY          = "q";
    public static final String EXTRA_QUERY_2        = "q2";

    private WeakReference<Context> contextReference = null;
    private IBridgeViewPresenter   presenterProxy   = null;

    public BridgeBroadcastView() {
        super();
        presenterProxy = new BridgeViewPresenter(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        contextReference = new WeakReference<>(context);
        presenterProxy.onReceiveIntent(intent);
        contextReference.clear();
        contextReference = null;
    }

    @Override
    public void sendCommand(Intent intent) {
        Context context = getContext();
        if(context != null) {
            context.startService(intent);
        }
    }

    @Nullable
    @Override
    public Context getContext() {
        return contextReference != null ? contextReference.get() : null;
    }

    @Override
    protected String getClassTag() {
        return BridgeBroadcastView.class.getSimpleName();
    }

    @Override
    protected boolean isLogEnabled() {
        return SubServeApplication.isApplicationLogEnabled();
    }
}