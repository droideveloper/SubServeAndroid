package org.fs.xml.okhttp;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.fs.xml.transform.XMLRpcMatcher;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit.Converter;

/**
 * Created by Fatih on 10/11/15.
 * as org.fs.xml.okhttp.SimpleXMLConverterFactory
 */
public class SimpleXMLConverterFactory extends Converter.Factory {

    public static SimpleXMLConverterFactory create() {
        return create(new Persister(new XMLRpcMatcher(), new Format("<?xml version=\"1.0\" encoding= \"UTF-8\" ?>")));
    }

    public static SimpleXMLConverterFactory create(Serializer serializer) {
        return new SimpleXMLConverterFactory(serializer, true);
    }

    public static SimpleXMLConverterFactory createNonStrict() {
        return create(new Persister(new XMLRpcMatcher(), new Format("<?xml version=\"1.0\" encoding= \"UTF-8\" ?>")));
    }

    public static SimpleXMLConverterFactory createNonStrict(Serializer serializer) {
        return new SimpleXMLConverterFactory(serializer, false);
    }

    private final Serializer serializer;
    private final boolean    strict;

    private SimpleXMLConverterFactory(Serializer serializer, boolean strict) {
        this.serializer = serializer;
        this.strict = strict;
    }

    public boolean isStrict() {
        return strict;
    }

    @Override
    public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
        if(!(type instanceof Class))
            return null;
        return new SimpleXmlRequestBodyConverter<>(serializer);
    }

    @Override
    public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
        if(!(type instanceof Class))
            return null;
        return new SimpleXmlResponseBodyConverter<>((Class<?>)type, serializer, strict);
    }
}
