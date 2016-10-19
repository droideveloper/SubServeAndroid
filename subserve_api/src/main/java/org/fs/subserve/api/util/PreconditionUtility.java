package org.fs.subserve.api.util;

/**
 * Created by Fatih on 23/05/16.
 * as org.fs.subserve.api.util.PreconditionUtility
 */
public final class PreconditionUtility {

    public static <T> void checkIfNull(T object, String msg) {
        if(object == null) {
            throw new NullPointerException(msg);
        }
    }

    public static <T> void checkIfNull(T object) {
        checkIfNull(object, "");
    }
}
