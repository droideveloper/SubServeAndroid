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
package org.fs.sub.net;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class ProxyInterceptor implements Interceptor {

  /**
   * when we set this interceptor instance on okHttpClient it let's user modify every request going
   * through in a chain
   * then you can add parameters as you see above
   * we added header required for app to call service endpoint
   *
   * @param chain will produce request
   * @return response instance
   * @throws IOException when there is I/O related error
   */
  @Override public Response intercept(Chain chain) throws IOException {
    final Request request = chain.request();
    return chain.proceed(request.newBuilder()
        .addHeader(IServiceEndpoint.ApiConstants.KEY_USER_AGENT, IServiceEndpoint.ApiConstants.VALUE_USER_AGENT)
        .addHeader(IServiceEndpoint.ApiConstants.KEY_CONNECTION, IServiceEndpoint.ApiConstants.VALUE_CONNECTION)
        .build());
  }
}
