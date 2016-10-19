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

public class BridgeBroadcastView extends AbstractBroadcastReceiver implements IBridgeView {

  public static final int CMD_SEARCH = 1;
  public static final int CMD_ELAPSED = 2;
  public static final int CMD_START = 3;
  public static final int CMD_STOP = 4;
  public static final int CMD_PAUSE = 5;
  public static final int CMD_RESUME = 6;

  public static final String EXTRA_COMMAND = "cmd";
  public static final String EXTRA_QUERY = "q";
  public static final String EXTRA_QUERY_2 = "q2";

  private WeakReference<Context> contextReference = null;
  private IBridgeViewPresenter presenterProxy = null;

  public BridgeBroadcastView() {
    super();
    presenterProxy = new BridgeViewPresenter(this);
  }

  @Override public void onReceive(Context context, Intent intent) {
    contextReference = new WeakReference<>(context);
    presenterProxy.onReceiveIntent(intent);
    contextReference.clear();
    contextReference = null;
  }

  @Override public void sendCommand(Intent intent) {
    Context context = getContext();
    if (context != null) {
      context.startService(intent);
    }
  }

  @Nullable @Override public Context getContext() {
    return contextReference != null ? contextReference.get() : null;
  }

  @Override protected String getClassTag() {
    return BridgeBroadcastView.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return SubServeApplication.isApplicationLogEnabled();
  }
}