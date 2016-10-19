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
package org.fs.sub;

import okhttp3.OkHttpClient;
import org.fs.core.AbstractApplication;
import org.fs.sub.common.DatabaseHelper;
import org.fs.sub.common.DownloadHelper;
import org.fs.sub.common.IPreference;
import org.fs.sub.common.PreferenceHelper;
import org.fs.sub.database.IDatabaseHelper;
import org.fs.sub.net.IDownloadHelper;
import org.fs.sub.net.IServiceEndpoint;
import org.fs.sub.net.ProxyInterceptor;
import org.fs.sub.view.ISubServeView;
import org.fs.xml.okhttp.SimpleXMLConverterFactory;
import retrofit2.Retrofit;

public class SubServeApplication extends AbstractApplication implements ISubServeView {

  private IDatabaseHelper databaseProxy;
  private IServiceEndpoint serviceProxy;
  private IDownloadHelper downloadProxy;
  private IPreference preferenceProxy;

  public SubServeApplication(boolean debug) {
    super(debug);
  }

  @Override public void onCreate() {
    super.onCreate();
    Retrofit retrofit = createRetrofit();

    //these are essential parts of application
    serviceProxy = retrofit.create(IServiceEndpoint.class);
    databaseProxy = new DatabaseHelper(getApplicationContext());
    downloadProxy = new DownloadHelper(databaseProxy);
    preferenceProxy = new PreferenceHelper(getApplicationContext());
  }

  @Override public IDatabaseHelper databaseProxy() {
    return databaseProxy;
  }

  @Override public IServiceEndpoint serviceProxy() {
    return serviceProxy;
  }

  @Override public IDownloadHelper downloadProxy() {
    return downloadProxy;
  }

  @Override public IPreference preferenceProxy() {
    return preferenceProxy;
  }

  private Retrofit createRetrofit() {
    return new Retrofit.Builder().baseUrl(IServiceEndpoint.ApiConstants.VALUE_BASE_URL)
        .client(createHttpClient())
        .addConverterFactory(SimpleXMLConverterFactory.createNonStrict())
        .build();
  }

  private OkHttpClient createHttpClient() {
    OkHttpClient httpClient = new OkHttpClient();
    httpClient.interceptors().add(new ProxyInterceptor());
    return httpClient;
  }

  public static boolean isApplicationLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return SubServeApplication.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return isApplicationLogEnabled();
  }
}
