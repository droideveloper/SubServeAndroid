package org.fs.sub.events;

import org.fs.common.IEvent;

/**
 * Created by Fatih on
 * as org.fs.sub.events.SrtEntitySavedOrUpdatedEvent
 */
public final class SrtEntitySavedOrUpdatedEvent implements IEvent {

    private final int response;

    public SrtEntitySavedOrUpdatedEvent(int response) {
        this.response = response;
    }

    public int response() {
        return response;
    }

}
