package org.fs.xml.okhttp;

import org.fs.xml.internal.Parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.okhttp.LegacyXMLConverterFactory
 */
public class LegacyXMLConverterFactory extends Converter.Factory {

    private final Parser parser;

    public static LegacyXMLConverterFactory create() {
        return new LegacyXMLConverterFactory();
    }
    public static LegacyXMLConverterFactory create(Parser parser) { return new LegacyXMLConverterFactory(parser); }

    /**
     * Defaults initialized by api
     */
    private LegacyXMLConverterFactory() {
        parser = new Parser();
        parser.addBooleanConverter(true);//<boolean>1</boolean>
        parser.addStringConverter(false);//wrapped <string>text</string>
        parser.addDateConverter("yyyyMMdd'T'HH:mm:ss", Locale.getDefault(), TimeZone.getTimeZone("GMT"));
    }

    /**
     * Pass custom Parser instance into Converter#Factory instance
     * @param parser Parser parser instance
     */
    private LegacyXMLConverterFactory(Parser parser) {
        this.parser = parser;
        if (parser == null) {
            throw new NullPointerException("parser is null");
        }
    }

    @Override public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if(!(type instanceof Class)) return null;
        return LegacyXMLRequestBodyConverter.create(parser);
    }

    @Override public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if(!(type instanceof Class)) return null;
        return LegacyXMLResponseBodyConverter.create(parser);
    }
}
