package org.fs.subserve.api.core;

/**
 * Created by Fatih on 23/05/16.
 * as org.fs.subserve.api.core.ILogger
 */
public interface ILogger {

    /**
     *
     * @param msg
     */
    void log(String msg);

    /**
     *
     * @param priority
     * @param msg
     */
    void log(int priority, String msg);

    /**
     *
     * @param e
     */
    void log(Exception e);

    /**
     *
     * @return
     */
    String getClassTag();

    /**
     *
     * @return
     */
    boolean isLogEnabled();
}
