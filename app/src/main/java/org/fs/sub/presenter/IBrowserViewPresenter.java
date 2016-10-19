package org.fs.sub.presenter;

import android.view.View;
import android.webkit.WebChromeClient;

import org.fs.sub.presenters.BrowserViewPresenter;

/**
 * Created by Fatih on 19/05/16.
 * as org.fs.sub.presenter.IBrowserViewPresenter
 */
public interface IBrowserViewPresenter {

    View.OnClickListener viewClickListener();

    BrowserViewPresenter viewJavascriptInterface();

    WebChromeClient      viewChromeClient();
}
