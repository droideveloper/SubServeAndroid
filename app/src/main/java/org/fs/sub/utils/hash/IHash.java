package org.fs.sub.utils.hash;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;

/**
 * Created by Fatih on
 * as org.fs.sub.common.IHash
 */
public abstract class IHash {

    protected final      long    CHUNK_SIZE   = 64 * 1024;

    public static IHash from(@NonNull String uri) {
        if(uri.startsWith("http://") || uri.startsWith("https://")) {
            return new RemoteFileHash(uri);
        } else {
            return new LocaleFileHash(new File(uri));
        }
    }

    public abstract long invoke() throws IOException;

    public abstract long fileSize();

    protected long hashChunk(ByteBuffer buffer) {
        LongBuffer longBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN)
                .asLongBuffer();
        long hash = 0L;
        while (longBuffer.hasRemaining()) {
            hash += longBuffer.get();
        }
        return hash;
    }
}
