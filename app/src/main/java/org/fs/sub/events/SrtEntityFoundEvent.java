package org.fs.sub.events;

import org.fs.common.IEvent;
import org.fs.sub.model.SrtEntity;

/**
 * Created by Fatih on
 * as org.fs.sub.events.SrtEntityFoundEvent
 */
public final class SrtEntityFoundEvent implements IEvent {

    public final static int LOCAL   = 0;
    public final static int REMOTE  = 1;

    private final SrtEntity srtEntity;

    private final int code;

    public SrtEntityFoundEvent(SrtEntity srtEntity, int code) {
        this.srtEntity = srtEntity;
        this.code = code;
    }

    public SrtEntity srtEntity() {
        return srtEntity;
    }

    public int getCode() {
        return code;
    }
}
