/*
 * SubServe Android Copyright (C) 2016 Fatih.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

public class BrowserViewPresenter extends AbstractPresenter<IBrowserView> implements IBrowserViewPresenter, View.OnClickListener {

  public BrowserViewPresenter(IBrowserView view) {
    super(view);
  }

  @Override protected String getClassTag() {
    return BrowserViewPresenter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return SubServeApplication.isApplicationLogEnabled();
  }

  @Override public View.OnClickListener viewClickListener() {
    return this;
  }

  @Override public BrowserViewPresenter viewJavascriptInterface() {
    return this;
  }

  @JavascriptInterface public void startApp() {
    SubServeAPI.startApp();
  }

  @JavascriptInterface public void stopApp() {
    SubServeAPI.stopApp();
  }

  @JavascriptInterface public void pauseApp() {
    SubServeAPI.pauseApp();
  }

  @JavascriptInterface public void resumeApp() {
    SubServeAPI.resumeApp();
  }

  @JavascriptInterface public void elapsedTime(long ms) {
    SubServeAPI.notifyTime(ms);
  }

  @JavascriptInterface public void searchWithUri(String uri) {
    SubServeAPI.searchWithURL(uri);
  }

  @JavascriptInterface public void searchWithImdb(String imdb) {
    SubServeAPI.searchWithIMDB(imdb);
  }

  @Override public WebChromeClient viewChromeClient() {
    return new WebChromeClient() {
      @Override public void onHideCustomView() {
        super.onHideCustomView();
        if (BrowserViewPresenter.this.view != null) {
          BrowserViewPresenter.this.view.onUnBindCustomView();
          BrowserViewPresenter.this.view.exitFullScreen();
        }
      }

      @Override public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);
        if (BrowserViewPresenter.this.view != null) {
          BrowserViewPresenter.this.view.enterFullScreen();
          BrowserViewPresenter.this.view.onBindCustomView(view, callback);
        }
      }

      @Override public View getVideoLoadingProgressView() {
        return LayoutInflater.from(view.getContext()).inflate(R.layout.view_progress_loading, null);
      }
    };
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.prevButtonView: {
        BusManager.send(ActionEvent.GO_BACK);
        break;
      }
      case R.id.nextButtonView: {
        BusManager.send(ActionEvent.GO_NEXT);
        break;
      }
      default:
      case R.id.exitButtonView: {
        BusManager.send(ActionEvent.EXIT);
        break;
      }
      case R.id.refreshButtonView: {
        BusManager.send(ActionEvent.REFRESH);
        break;
      }
    }
  }

  @Override public void onCreate() {
    SubServeAPI.onCreate(view.getContext());
  }

  @Override public void onStart() {
  }

  @Override public void onStop() {
  }

  @Override public void onDestroy() {
    SubServeAPI.onDestroy();
  }
}