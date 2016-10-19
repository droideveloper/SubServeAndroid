package org.fs.sub.view;

import android.content.Context;
import android.content.Intent;

import org.fs.common.IView;

/**
 * Created by Fatih on
 * as org.fs.sub.view.IBridgeView
 */
public interface IBridgeView extends IView {

    /**
     *
     * @param intent
     */
    void sendCommand(Intent intent);

    /**
     *
     * @return
     */
    Context getContext();
}
