package org.fs.sub.utils.hash;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.fs.exception.AndroidException;
import org.fs.util.PreconditionUtility;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Locale;

/**
 * Created by Fatih on.
 * as org.fs.sub.utils.RemoteFileHash
 */
final class RemoteFileHash extends IHash {

    private final static OkHttpClient httpClient = new OkHttpClient();

    private final String uri;
    private long  fileSize;

    public RemoteFileHash(String uri) {
        PreconditionUtility.checkNotNull(uri, "why uri == null or empty ?");
        this.uri = uri;
    }

    @Override
    public long invoke() throws IOException {
        Response response = withHeadRequest();
        if(response.isSuccessful()) {
            fileSize = Long.parseLong(response.header("Content-Length"));
            return fileSize + withPartialChunk(0, CHUNK_SIZE) + withPartialChunk(fileSize - CHUNK_SIZE, fileSize);
        }
        throw new AndroidException("network error");
    }

    @Override
    public long fileSize() {
        return fileSize;
    }

    private long withPartialChunk(long start, long end) throws IOException {
        DataInputStream dis = null;
        try {
            dis = withPartRequest(toRange(start, end));
            byte[] data = new byte[toInteger(CHUNK_SIZE)];
            dis.readFully(data, 0, toInteger(CHUNK_SIZE));
            return hashChunk(ByteBuffer.wrap(data, 0, toInteger(CHUNK_SIZE)));
        } finally {
            if(dis != null) {
                dis.close();
            }
        }
    }

    private DataInputStream withPartRequest(String range) throws IOException {
        Response response = httpClient.newCall(withGet(range))
                                      .execute();
        if(!response.isSuccessful()) {
            throw new AndroidException("network error");
        }
        return new DataInputStream(response.body().byteStream());
    }

    private Response withHeadRequest() throws IOException {
        return httpClient.newCall(withHead())
                         .execute();
    }

    private Request withHead() {
        return new Request.Builder()
                          .url(uri)
                          .addHeader("Connection", "close")
                          .head()
                          .build();
    }

    private Request withGet(String range) {
        return new Request.Builder()
                          .url(uri)
                          .addHeader("Range", range)
                          .addHeader("Connection", "close")
                          .get()
                          .build();
    }

    private String toRange(long s, long e) {
        return String.format(Locale.US, "bytes=%d-%d", s, e);
    }

    private int toInteger(long l) {
        return (int)l;
    }
}
