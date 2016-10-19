package org.fs.sub.net;

/**
 * Created by Fatih on
 * as org.fs.sub.net.SubtitleDownloadHelper
 */
public interface IDownloadHelper {

    /**
     * request made by hash
     * @param serviceProxy proxy instance
     * @param URL url of media
     * @param hash hash of media
     * @param lang language choice
     */
    void withServiceProxyAndUrlAndHash(IServiceEndpoint serviceProxy, String URL, String hash, String lang);


    /**
     * request made by imdb_id
     * @param serviceProxy proxy instance
     * @param URL url of media
     * @param imdb imdb id of media
     * @param lang language choice
     */
    void withServiceProxyAndUrlAndImdb(IServiceEndpoint serviceProxy, String URL, String imdb, String lang);

}
