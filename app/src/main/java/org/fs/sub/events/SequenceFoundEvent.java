package org.fs.sub.events;

import org.fs.common.IEvent;
import org.fs.sub.model.SrtSequenceEntity;

import java.util.LinkedList;

/**
 * Created by Fatih on
 * as org.fs.sub.events.SequenceFoundEvent
 */
public final class SequenceFoundEvent implements IEvent {

    private final LinkedList<SrtSequenceEntity> queue;

    public SequenceFoundEvent(LinkedList<SrtSequenceEntity> queue) {
        this.queue = queue;
    }

    public LinkedList<SrtSequenceEntity> getQueue() {
        return queue;
    }
}
