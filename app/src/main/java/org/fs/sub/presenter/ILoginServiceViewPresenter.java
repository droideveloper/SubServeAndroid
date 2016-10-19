package org.fs.sub.presenter;

import android.os.Bundle;

import java.io.IOException;

/**
 * Created by Fatih o
 * as org.fs.sub.presenter.ILoginServiceViewPresenter
 */
public interface ILoginServiceViewPresenter {

    /**
     *
     * @param userName
     * @param password
     * @throws IOException
     */
    void withUserNameAndPassword(String userName, String password) throws IOException;

    /**
     *
     * @param token
     * @throws IOException
     */
    void withSessionToken(String token) throws IOException;

    /**
     *
     * @param token
     * @throws IOException
     */
    void withTerminateSession(String token) throws IOException;

    /**
     *
     * @param args
     */
    void onCommand(Bundle args);
}
