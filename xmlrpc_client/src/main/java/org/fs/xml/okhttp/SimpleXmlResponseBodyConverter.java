package org.fs.xml.okhttp;

import com.squareup.okhttp.ResponseBody;

import org.simpleframework.xml.Serializer;

import java.io.IOException;
import java.io.InputStream;

import retrofit.Converter;

/**
 * Created by Fatih on 10/11/15.
 * as org.fs.xml.okhttp.SimpleXmlResponseBodyConverter
 */
public class SimpleXmlResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Class<T>   clazz;
    private final Serializer serializer;
    private final boolean    strict;

    public SimpleXmlResponseBodyConverter(Class<T> clazz, Serializer serializer, boolean strict) {
        this.clazz = clazz;
        this.serializer = serializer;
        this.strict = strict;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        InputStream input = value.byteStream();
        try {
            T read = serializer.read(clazz, input, strict);
            if(read == null) {
                throw new RuntimeException("can not serialize response as " + clazz);
            }
            return read;
        } catch (RuntimeException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
              input.close();
            } catch (IOException ignored) {
            }
        }
    }
}
