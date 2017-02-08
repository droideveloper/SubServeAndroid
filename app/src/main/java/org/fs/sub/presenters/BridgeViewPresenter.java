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

import android.content.Intent;
import android.net.Uri;

import org.fs.common.AbstractPresenter;
import org.fs.sub.SubServeApplication;
import org.fs.sub.presenter.IBridgeViewPresenter;
import org.fs.sub.view.IBridgeView;
import org.fs.sub.views.BridgeBroadcastView;
import org.fs.sub.views.ServiceView;

public class BridgeViewPresenter extends AbstractPresenter<IBridgeView> implements IBridgeViewPresenter {

  private static final String HOST_SEARCH       = "search";
  private static final String HOST_TIME_ELAPSED = "timeElapsed";
  private static final String HOST_START        = "start";
  private static final String HOST_STOP         = "stop";
  private static final String HOST_RESUME       = "resume";
  private static final String HOST_PAUSE        = "pause";

  public BridgeViewPresenter(IBridgeView view) {
    super(view);
  }

  @Override protected String getClassTag() {
    return BridgeViewPresenter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return SubServeApplication.isApplicationLogEnabled();
  }

  @Override public void onReceiveIntent(Intent intent) {
    String action = intent.getStringExtra(BridgeBroadcastView.EXTRA_COMMAND);
    if (HOST_SEARCH.equalsIgnoreCase(action)) {
      if (intent.hasExtra(BridgeBroadcastView.EXTRA_QUERY)) {
        Intent i = serviceSearch(intent.getStringExtra(BridgeBroadcastView.EXTRA_QUERY), true);
        view.sendCommand(i);
      } else {
        Intent i = serviceSearch(intent.getStringExtra(BridgeBroadcastView.EXTRA_QUERY_2), false);
        view.sendCommand(i);
      }
    } else if (HOST_TIME_ELAPSED.equalsIgnoreCase(action)) {
      String value = intent.getStringExtra(BridgeBroadcastView.EXTRA_QUERY);
      view.sendCommand(serviceElapsedTime(value));
    } else if (HOST_START.equalsIgnoreCase(action)) {
      view.sendCommand(serviceStart());
    } else if (HOST_STOP.equalsIgnoreCase(action)) {
      view.sendCommand(serviceStop());
    } else if (HOST_RESUME.equalsIgnoreCase(action)) {
      view.sendCommand(serviceResume());
    } else if (HOST_PAUSE.equalsIgnoreCase(action)) {
      view.sendCommand(servicePause());
    } else {
      log("We can not find relevant intent");
    }
  }

  @Override public Intent serviceStart() {
    Intent intent = new Intent(view.getContext(), ServiceView.class);
    intent.putExtra(BridgeBroadcastView.EXTRA_COMMAND, BridgeBroadcastView.CMD_START);
    return intent;
  }

  @Override public Intent serviceStop() {
    Intent intent = new Intent(view.getContext(), ServiceView.class);
    intent.putExtra(BridgeBroadcastView.EXTRA_COMMAND, BridgeBroadcastView.CMD_STOP);
    return intent;
  }

  @Override public Intent serviceResume() {
    Intent intent = new Intent(view.getContext(), ServiceView.class);
    intent.putExtra(BridgeBroadcastView.EXTRA_COMMAND, BridgeBroadcastView.CMD_RESUME);
    return intent;
  }

  @Override public Intent servicePause() {
    Intent intent = new Intent(view.getContext(), ServiceView.class);
    intent.putExtra(BridgeBroadcastView.EXTRA_COMMAND, BridgeBroadcastView.CMD_PAUSE);
    return intent;
  }

  @Override public Intent serviceElapsedTime(String value) {
    Intent intent = new Intent(view.getContext(), ServiceView.class);
    intent.putExtra(BridgeBroadcastView.EXTRA_QUERY, parseElapsedTime(value));
    intent.putExtra(BridgeBroadcastView.EXTRA_COMMAND, BridgeBroadcastView.CMD_ELAPSED);
    return intent;
  }

  @Override public Intent serviceSearch(String uri, boolean q) {
    Intent intent = new Intent(view.getContext(), ServiceView.class);
    if (q) {//q search
      intent.putExtra(BridgeBroadcastView.EXTRA_QUERY, Uri.parse(uri));
      intent.putExtra(BridgeBroadcastView.EXTRA_COMMAND, BridgeBroadcastView.CMD_SEARCH);
    } else {//q2 search
      intent.putExtra(BridgeBroadcastView.EXTRA_QUERY_2, uri);
      intent.putExtra(BridgeBroadcastView.EXTRA_COMMAND, BridgeBroadcastView.CMD_SEARCH);
    }
    return intent;
  }

  private long parseElapsedTime(String q) {
    return Long.parseLong(q);
  }


  @Override public void onCreate()  {}
  @Override public void onDestroy() {}
  @Override public void onStart()   {}
  @Override public void onStop()    {}
}