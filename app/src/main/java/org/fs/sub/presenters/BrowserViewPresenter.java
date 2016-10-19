package org.fs.sub.presenters;

import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;

import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.sub.R;
import org.fs.sub.SubServeApplication;
import org.fs.sub.events.ActionEvent;
import org.fs.sub.presenter.IBrowserViewPresenter;
import org.fs.sub.view.IBrowserView;
import org.fs.subserve.api.SubServeAPI;

/**
 * Created by Fatih on 19/05/16.
 * as org.fs.sub.presenters.BrowserViewPresenter
 */
public class BrowserViewPresenter extends AbstractPresenter<IBrowserView> implements IBrowserViewPresenter, View.OnClickListener {

    public BrowserViewPresenter(IBrowserView view) {
        super(view);
    }

    @Override
    protected String getClassTag() {
        return BrowserViewPresenter.class.getSimpleName();
    }

    @Override
    protected boolean isLogEnabled() {
        return SubServeApplication.isApplicationLogEnabled();
    }

    @Override
    public View.OnClickListener viewClickListener() {
        return this;
    }

    @Override
    public BrowserViewPresenter viewJavascriptInterface() {
        return this;
    }

    @JavascriptInterface
    public void startApp() {
        SubServeAPI.startApp();
    }

    @JavascriptInterface
    public void stopApp() {
        SubServeAPI.stopApp();
    }

    @JavascriptInterface
    public void pauseApp() {
        SubServeAPI.pauseApp();
    }

    @JavascriptInterface
    public void resumeApp() {
        SubServeAPI.resumeApp();
    }

    @JavascriptInterface
    public void elapsedTime(long ms) {
        SubServeAPI.notifyTime(ms);
    }

    @JavascriptInterface
    public void searchWithUri(String uri) {
        SubServeAPI.searchWithURL(uri);
    }

    @JavascriptInterface
    public void searchWithImdb(String imdb) {
        SubServeAPI.searchWithIMDB(imdb);
    }

    @Override
    public WebChromeClient viewChromeClient() {
        return new WebChromeClient() {
            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                if(BrowserViewPresenter.this.view != null) {
                    BrowserViewPresenter.this.view.onUnBindCustomView();
                    BrowserViewPresenter.this.view.exitFullScreen();
                }
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                if(BrowserViewPresenter.this.view != null) {
                    BrowserViewPresenter.this.view.enterFullScreen();
                    BrowserViewPresenter.this.view.onBindCustomView(view, callback);
                }
            }

            @Override
            public View getVideoLoadingProgressView() {
                return LayoutInflater.from(view.getContext())
                        .inflate(R.layout.view_progress_loading, null);
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prevButtonView: {
                BusManager.MAIN.post(ActionEvent.GO_BACK);
                break;
            }
            case R.id.nextButtonView: {
                BusManager.MAIN.post(ActionEvent.GO_NEXT);
                break;
            }
            default:
            case R.id.exitButtonView: {
                BusManager.MAIN.post(ActionEvent.EXIT);
                break;
            }
            case R.id.refreshButtonView: {
                BusManager.MAIN.post(ActionEvent.REFRESH);
                break;
            }
        }
    }


    @Override
    public void onCreate() {
        SubServeAPI.onCreate(view.getContext());
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
        SubServeAPI.onDestroy();
    }
}