package org.fs.sub.net;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Fatih
 * as org.fs.sub.net.ProxyInterceptor
 */
public final class ProxyInterceptor implements Interceptor {

    /**
     * when we set this interceptor instance on okHttpClient it let's user modify every request going through in a chain
     * then you can add parameters as you see above
     * we added header required for app to call service endpoint
     * @param chain will produce request
     * @return response instance
     * @throws IOException when there is I/O related error
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        return chain.proceed(request.newBuilder()
                                    .addHeader(IServiceEndpoint.ApiConstants.KEY_USER_AGENT,
                                               IServiceEndpoint.ApiConstants.VALUE_USER_AGENT)
                                    .addHeader(IServiceEndpoint.ApiConstants.KEY_CONNECTION,
                                               IServiceEndpoint.ApiConstants.VALUE_CONNECTION)
                                    .build());
    }
}
