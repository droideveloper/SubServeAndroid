package org.fs.sub.utils.hash;

import org.fs.util.PreconditionUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Fatih on
 * as org.fs.sub.utils.LocaleFileHash
 */
final class LocaleFileHash extends IHash {

    private final File file;

    public LocaleFileHash(File file) {
        PreconditionUtility.checkNotNull(file, "why file == null ?");
        this.file = file;
    }

    @Override
    public long fileSize() {
        return file.length();
    }

    @Override
    public long invoke() throws IOException {
        long size = file.length();
        long chunkSize = Math.min(size, CHUNK_SIZE);

        FileChannel fileChannel = new FileInputStream(file).getChannel();
        try {
            long head = hashChunk(fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, chunkSize));
            long tail = hashChunk(fileChannel.map(FileChannel.MapMode.READ_ONLY, Math.max(size - chunkSize, 0), chunkSize));
            return head + tail + size;
        } finally {
            fileChannel.close();
        }
    }
}
