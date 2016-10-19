package org.fs.sub.events;

import org.fs.common.IEvent;

/**
 * Created by Fatih on
 * as org.fs.sub.events.SrtEntityNotFoundEvent
 */
public final class SrtEntityNotFoundEvent implements IEvent {

    public final static int LOCAL   = 0;
    public final static int REMOTE  = 1;

    private final int code;

    public SrtEntityNotFoundEvent(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
