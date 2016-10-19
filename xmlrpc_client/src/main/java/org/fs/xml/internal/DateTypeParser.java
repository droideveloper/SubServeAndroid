package org.fs.xml.internal;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.internal.DateTypeParser
 */
class DateTypeParser implements TypeParser<Date> {

    private final SimpleDateFormat objParser;

    public static DateTypeParser create() {
        return create("yyyyMMdd'T'HH:mm:ss");
    }

    public static DateTypeParser create(String str) {
        return create(str, Locale.getDefault());
    }

    public static DateTypeParser create(String str, Locale locale) {
        return create(str, locale, TimeZone.getTimeZone("GMT"));
    }

    public static DateTypeParser create(String str, Locale locale, TimeZone zone) {
        return new DateTypeParser(str, locale, zone);
    }

    private DateTypeParser(String str, Locale locale, TimeZone zone) {
        this.objParser = new SimpleDateFormat(str, locale);
        this.objParser.setTimeZone(zone);
    }

    @Override public void write(XmlSerializer writer, Date value) throws IOException {
        writer.startTag(null, Constants.DATE);
        writer.text(objParser.format(value));
        writer.endTag(null, Constants.DATE);
    }

    @Override public Date read(XmlPullParser reader) throws XmlPullParserException, IOException {
        int type = reader.getEventType();
        String text = null;
        while (type != XmlPullParser.END_TAG) {
            if (type == XmlPullParser.TEXT) {
                text = reader.getText();
            }
            type = reader.next();
        }
        //go on next START_TAG
        reader.next();
        try {
            return objParser.parse(text);
        } catch (ParseException e) {
            //wrap it with IOException, yahoo
            throw new IOException(e);
        }
    }

    @Override public boolean hasRead(XmlPullParser reader) {
        final String nodeName = reader.getName();
        return nodeName != null && Constants.DATE.equalsIgnoreCase(nodeName);
    }

    @Override public boolean hasWrite(Object o) {
        return o != null && o instanceof Date;
    }
}
