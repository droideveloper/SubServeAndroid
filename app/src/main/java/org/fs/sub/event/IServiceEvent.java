package org.fs.sub.event;

import org.fs.sub.events.HashFoundEvent;
import org.fs.sub.events.HashNotFoundEvent;
import org.fs.sub.events.SequenceFoundEvent;
import org.fs.sub.events.SequenceNotFoundEvent;
import org.fs.sub.events.SrtEntityFoundEvent;
import org.fs.sub.events.SrtEntityNotFoundEvent;

/**
 * Created by Fatih on
 * as org.fs.sub.events.IServiceEvent
 */
public interface IServiceEvent {

    /**
     * We will recieve hash events here
     * @param hashEvent
     */
    void onHashFound(HashFoundEvent hashEvent);

    /**
     *
     * @param hashEvent
     */
    void onHashNotFound(HashNotFoundEvent hashEvent);

    /**
     *
     * @param sequenceEvent
     */
    void onSequenceFound(SequenceFoundEvent sequenceEvent);

    /**
     *
     * @param sequenceEvent
     */
    void onSequenceNotFound(SequenceNotFoundEvent sequenceEvent);

    /**
     *
     * @param srtEvent
     */
    void onSrtEntityFound(SrtEntityFoundEvent srtEvent);

    /**
     *
     * @param srtEvent
     */
    void onSrtEntityNotFound(SrtEntityNotFoundEvent srtEvent);
}
