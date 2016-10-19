package org.fs.xml.okhttp;

import org.fs.xml.internal.Parser;
import org.fs.xml.net.XMLRpcRequest;

import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Converter;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.okhttp.LegacyXMLRequestBodyConverter
 */
public class LegacyXMLRequestBodyConverter implements Converter<XMLRpcRequest, RequestBody> {

    private final static MediaType MEDIA_TYPE = MediaType.parse("text/xml; charset=UTF-8");
    private final static String    UTF_8      = "UTF-8";

    private final Parser parser;

    public static LegacyXMLRequestBodyConverter create(final Parser parser) {
        return new LegacyXMLRequestBodyConverter(parser);
    }

    private LegacyXMLRequestBodyConverter(final Parser parser) {
        this.parser = parser;
    }

    @Override public RequestBody convert(XMLRpcRequest request) throws IOException {
        Buffer buffer = new Buffer();
        try {
            OutputStreamWriter out = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            parser.write(out, request, UTF_8);
            out.flush();
        } catch (Exception wrapped) {
            throw new RuntimeException(wrapped);
        }
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
    }
}
