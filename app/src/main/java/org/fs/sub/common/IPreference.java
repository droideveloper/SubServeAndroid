package org.fs.sub.common;

/**
 * Created by Fatih on
 * as org.fs.sub.view.IPreferenceView
 */
public interface IPreference {

    /**
     * Whether notification required to start or not
     * @return true or false
     */
    boolean isNotify();

    /**
     *
     * @return
     */
    String  languages();

    /**
     *
     * @return
     */
    String  userName();

    /**
     *
     * @return
     */
    String  password();

    /**
     *
     * @return
     */
    int textColor();

    /**
     *
     * @return
     */
    int textSize();

    /**
     *
     * @return
     */
    String textAlignment();

    /**
     *
     * @return
     */
    String accessToken();

    /**
     *
     * @param token
     */
    void setAccessToken(String token);
}
