package org.fs.sub.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;

import org.fs.common.BusManager;
import org.fs.common.IPresenter;
import org.fs.core.AbstractActivity;
import org.fs.sub.SubServeApplication;
import org.fs.sub.R;
import org.fs.sub.events.ActionEvent;
import org.fs.sub.presenter.IBrowserViewPresenter;
import org.fs.sub.presenters.BrowserViewPresenter;
import org.fs.sub.view.IBrowserView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Fatih on 19/05/16.
 * as org.fs.sub.views.BrowserView
 */
public class BrowserView extends AbstractActivity implements IBrowserView {

    private final static int ENABLED_ALPHA  = 100;
    private final static int DISABLED_ALPHA = 70;

    private IBrowserViewPresenter presenter;


    private Toolbar     toolbar;
    private WebView     webView;
    private FrameLayout customView;

    private ImageView   prevView;
    private ImageView   nextView;
    private ImageView   refreshView;
    private ImageView   exitView;

    private String url;

    public BrowserView() {
        presenter = new BrowserViewPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        customView = (FrameLayout) findViewById(R.id.customView);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LayoutInflater.from(getContext())
                .inflate(R.layout.layout_action_view, toolbar);
        webView = (WebView)findViewById(R.id.webView);
        prevView = (ImageView)findViewById(R.id.prevButtonView);
        nextView = (ImageView)findViewById(R.id.nextButtonView);
        refreshView = (ImageView)findViewById(R.id.refreshButtonView);
        exitView = (ImageView)findViewById(R.id.exitButtonView);

        Uri uri = getIntent().getData();
        try {
            url = URLDecoder.decode(uri.getQueryParameter("q"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log(e);
        }
        presenter().onCreate();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setUpActions();
        setUpWebView();
        webView.loadUrl(url);
    }

    @Override
    protected String getClassTag() {
        return BrowserView.class.getSimpleName();
    }

    @Override
    protected boolean isLogEnabled() {
        return SubServeApplication.isApplicationLogEnabled();
    }

    @Override
    public void toBroadcast(Intent intent) {
        sendBroadcast(intent);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setUpActions() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
        }
        exitView.setOnClickListener(presenter.viewClickListener());
        refreshView.setOnClickListener(presenter.viewClickListener());
        invalidate();
    }

    @Override
    public void setUpWebView() {
        if(webView != null) {
            webView.setWebChromeClient(presenter.viewChromeClient());
            //can you stop showing this warning, will ya?
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.getSettings().setDefaultTextEncodingName("UTF-8");
            webView.getSettings().setUseWideViewPort(true);
            webView.setOverScrollMode(WebView.OVER_SCROLL_ALWAYS);
            webView.setInitialScale(1);
            webView.addJavascriptInterface(presenter.viewJavascriptInterface(), "SubServeJS");
        }
    }

    @Override
    public void onBindCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        webView.setVisibility(View.GONE);
        customView.setVisibility(View.VISIBLE);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                                                       FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_VERTICAL
                | Gravity.CENTER_HORIZONTAL;

        customView.addView(view, params);
    }

    @Override
    public void onUnBindCustomView() {
        customView.removeAllViews();
        customView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    @Override
    public void enterFullScreen() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void exitFullScreen() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusManager.MAIN.register(this);
        if(webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onPause() {
        BusManager.MAIN.unregister(this);
        if(webView !=  null) {
            webView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        presenter().onDestroy();
        super.onDestroy();
    }

    @Subscribe
    @Override
    public void onActionEvent(ActionEvent event) {
        switch (event) {
            case GO_BACK: {
                if(webView.canGoBack()) {
                    webView.goBack();
                }
                break;
            }

            case GO_NEXT: {
                if(webView.canGoForward()) {
                    webView.goForward();
                }
                break;
            }

            case REFRESH: {
                webView.reload();
                break;
            }

            default:
            case EXIT: {
                finish();
                return;
            }
        }
        invalidate();
    }

    private IPresenter presenter() {
        return (IPresenter) presenter;
    }

    private void invalidate() {
        if(!webView.canGoBack()) {
            prevView.getDrawable()
                    .mutate()
                    .setAlpha(DISABLED_ALPHA);
            prevView.invalidate();
            prevView.setOnClickListener(null);
        } else {
            prevView.getDrawable()
                    .mutate()
                    .setAlpha(ENABLED_ALPHA);
            prevView.invalidate();
            prevView.setOnClickListener(presenter.viewClickListener());
        }

        if(!webView.canGoForward()) {
            nextView.getDrawable()
                    .mutate()
                    .setAlpha(DISABLED_ALPHA);
            nextView.invalidate();
            nextView.setOnClickListener(null);
        } else {
            nextView.getDrawable()
                    .mutate()
                    .setAlpha(DISABLED_ALPHA);
            nextView.invalidate();
            nextView.setOnClickListener(presenter.viewClickListener());
        }
    }
}