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

public interface IDownloadHelper {

  /**
   * request made by hash
   *
   * @param serviceProxy proxy instance
   * @param URL url of media
   * @param hash hash of media
   * @param lang language choice
   */
  void withServiceProxyAndUrlAndHash(IServiceEndpoint serviceProxy, String URL, String hash, String lang);

  /**
   * request made by imdb_id
   *
   * @param serviceProxy proxy instance
   * @param URL url of media
   * @param imdb imdb id of media
   * @param lang language choice
   */
  void withServiceProxyAndUrlAndImdb(IServiceEndpoint serviceProxy, String URL, String imdb, String lang);
}
