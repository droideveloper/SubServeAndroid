package org.fs.sub.events;

import org.fs.common.IEvent;

/**
 * Created by Fatih on
 * as org.fs.sub.events.HashEvent
 */
public final class HashFoundEvent implements IEvent {

    private final String hashString;

    private final long   fileSize;

    public HashFoundEvent(String hashString, long fileSize) {
        this.hashString = hashString;
        this.fileSize = fileSize;
    }

    public String getHashString() {
        return hashString;
    }

    public long getFileSize() {
        return fileSize;
    }
}
