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

import okhttp3.ResponseBody;
import org.fs.xml.net.XMLRpcRequest;
import org.fs.xml.net.XMLRpcResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface IServiceEndpoint {

  /**
   * Post LogIn
   *
   * @param request request instance for this method
   * @return response instance
   * http://trac.opensubtitles.org/projects/opensubtitles/wiki/XmlRpcLogIn
   */
  @POST(ApiConstants.VALUE_PATH) Call<XMLRpcResponse> withLoginRequest(@Body XMLRpcRequest request);

  /**
   * Post LogOut
   *
   * @param request request instance for this method
   * @return response instance
   * http://trac.opensubtitles.org/projects/opensubtitles/wiki/XmlRpcLogOut
   */
  @POST(ApiConstants.VALUE_PATH) Call<XMLRpcResponse> withLogoutRequest(@Body XMLRpcRequest request);

  /**
   * Post noOperation
   *
   * @param request request instance for this method
   * @return response instance
   * http://trac.opensubtitles.org/projects/opensubtitles/wiki/XmlRpcNoOperation
   */
  @POST(ApiConstants.VALUE_PATH) Call<XMLRpcResponse> withNoOperationRequest(@Body XMLRpcRequest request);

  /**
   * @param request request instance for this method
   * @return response instance
   * http://trac.opensubtitles.org/projects/opensubtitles/wiki/XmlRpcSearchSubtitles
   */
  @POST(ApiConstants.VALUE_PATH) Call<XMLRpcResponse> withSearchSubtitlesRequest(@Body XMLRpcRequest request);

  /**
   * lists available subtitle languages on server side
   *
   * @param request request instance
   * @return response instance
   */
  @POST(ApiConstants.VALUE_PATH) Call<XMLRpcResponse> withGetSubtitleLanguages(@Body XMLRpcRequest request);

  /**
   * with this we send direct Get request to the file options are we download it as .zip or .gz
   * .gz is easy to read
   * .zip is little bit tricky yet it's possible to read only file.
   *
   * @param url url of .srt file
   * @return response instance
   */
  @GET Call<ResponseBody> withDownloadUrl(@Url String url);

  /**
   * constants that will be used on building XmlRequest object and method names are below.
   */
  class ApiConstants {
    public final static String VALUE_BASE_URL = "http://api.opensubtitles.org";
    public final static String VALUE_PATH = "/xml-rpc";

    public final static String METHOD_LOG_IN = "LogIn";
    public final static String METHOD_LOG_OUT = "LogOut";
    public final static String METHOD_NO_OPERATION = "NoOperation";
    public final static String METHOD_SEARCH_SUBTITLES = "SearchSubtitles";

    /**
     * Header Params
     */
    public final static String KEY_USER_AGENT = "User-Agent";
    public final static String VALUE_USER_AGENT = "OSTestUserAgent";/*value registered for OpenSubtitles*/
        //TODO change production userAgent
    public final static String KEY_CONNECTION = "Connection";
    public final static String VALUE_CONNECTION = "close";
  }
}
