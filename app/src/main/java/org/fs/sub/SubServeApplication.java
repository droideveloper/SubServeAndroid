package org.fs.sub;

import com.squareup.okhttp.OkHttpClient;

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

import retrofit.Retrofit;

/**
 * Created by Fatih on
 * as org.fs.sub.SubApplication
 */
public class SubServeApplication extends AbstractApplication implements ISubServeView {

    private IDatabaseHelper  databaseProxy;
    private IServiceEndpoint serviceProxy;
    private IDownloadHelper  downloadProxy;
    private IPreference      preferenceProxy;

    @Override
    public void onCreate() {
        super.onCreate();
        Retrofit retrofit = createRetrofit();

        //these are essential parts of application
        serviceProxy = retrofit.create(IServiceEndpoint.class);
        databaseProxy = new DatabaseHelper(getApplicationContext());
        downloadProxy = new DownloadHelper(databaseProxy);
        preferenceProxy = new PreferenceHelper(getApplicationContext());

    }

    @Override
    public IDatabaseHelper databaseProxy() {
        return databaseProxy;
    }

    @Override
    public IServiceEndpoint serviceProxy() {
        return serviceProxy;
    }

    @Override
    public IDownloadHelper downloadProxy() {
        return downloadProxy;
    }

    @Override
    public IPreference preferenceProxy() {
        return preferenceProxy;
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                           .baseUrl(IServiceEndpoint.ApiConstants.VALUE_BASE_URL)
                           .client(createHttpClient())
                           .addConverterFactory(SimpleXMLConverterFactory.createNonStrict())
                           .build();
    }

    private OkHttpClient createHttpClient() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors()
                .add(new ProxyInterceptor());
        return httpClient;
    }

    public static boolean isApplicationLogEnabled() {
        return BuildConfig.DEBUG;
    }

    @Override
    protected String getClassTag() {
        return SubServeApplication.class.getSimpleName();
    }

    @Override
    protected boolean isLogEnabled() {
        return isApplicationLogEnabled();
    }
}
