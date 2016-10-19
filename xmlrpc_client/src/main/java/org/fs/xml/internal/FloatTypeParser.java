package org.fs.xml.internal;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.internal.FloatTypeParser
 */
class FloatTypeParser implements TypeParser<Float> {

    public static FloatTypeParser create() {
        return new FloatTypeParser();
    }

    private FloatTypeParser() {}

    @Override public void write(XmlSerializer writer, Float value) throws IOException {
        writer.startTag(null, Constants.FLOAT);
        writer.text(String.valueOf(value));
        writer.endTag(null, Constants.FLOAT);
    }

    @Override public Float read(XmlPullParser reader) throws XmlPullParserException, IOException {
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
        return Float.parseFloat(text);
    }

    @Override public boolean hasRead(XmlPullParser reader) {
        final String nodeName = reader.getName();
        return nodeName != null && Constants.FLOAT.equalsIgnoreCase(nodeName);
    }

    @Override public boolean hasWrite(Object o) {
        return o != null && o instanceof Float;
    }
}
