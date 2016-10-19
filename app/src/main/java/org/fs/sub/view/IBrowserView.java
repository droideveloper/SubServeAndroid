package org.fs.sub.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebChromeClient;

import org.fs.common.IView;
import org.fs.sub.events.ActionEvent;

/**
 * Created by Fatih on 19/05/16.
 * as org.fs.sub.view.IBrowserView
 */
public interface IBrowserView extends IView {

    void toBroadcast(Intent intent);

    Context getContext();

    void setUpWebView();

    void setUpActions();

    void onActionEvent(ActionEvent event);

    void onBindCustomView(View view, WebChromeClient.CustomViewCallback callback);

    void onUnBindCustomView();

    void enterFullScreen();

    void exitFullScreen();

}
