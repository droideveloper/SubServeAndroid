package org.fs.xml.okhttp;

import org.fs.xml.internal.Parser;
import org.fs.xml.net.XMLRpcResponse;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.okhttp.LegacyXMLResponseBodyConverter
 */
public class LegacyXMLResponseBodyConverter implements Converter<ResponseBody, XMLRpcResponse> {

    private final String UTF_8  = "UTF-8";
    private final Parser parser;

    public static LegacyXMLResponseBodyConverter create(Parser parser) {
        return new LegacyXMLResponseBodyConverter(parser);
    }

    private LegacyXMLResponseBodyConverter(Parser parser) {
        this.parser = parser;
    }

    @Override public XMLRpcResponse convert(ResponseBody response) throws IOException {
        InputStream in = response.byteStream();
        try {
            return parser.read(in, UTF_8);
        } finally {
            in.close();
        }
    }
}
