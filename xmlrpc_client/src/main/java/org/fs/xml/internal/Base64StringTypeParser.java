package org.fs.xml.internal;

import org.fs.xml.type.Base64String;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.internal.Base64StringTypeParser
 */
class Base64StringTypeParser implements TypeParser<Base64String> {

    public static Base64StringTypeParser create() {
        return new Base64StringTypeParser();
    }

    private Base64StringTypeParser() {}

    @Override public void write(XmlSerializer writer, Base64String value) throws IOException {
        writer.startTag(null, Constants.BASE64);
        writer.text(value.encode());
        writer.endTag(null, Constants.BASE64);
    }

    @Override public Base64String read(XmlPullParser reader) throws XmlPullParserException, IOException {
        int type = reader.getEventType();
        String text = null;
        while(type != XmlPullParser.END_TAG) {
            if (type == XmlPullParser.TEXT) {
                text = reader.getText();
            }
            type = reader.next();
        }
        //go on next START_TAG
        reader.next();
        return new Base64String(text);
    }

    @Override public boolean hasRead(XmlPullParser reader) {
        final String nodeName = reader.getName();
        return nodeName != null && Constants.BASE64.equalsIgnoreCase(nodeName);
    }

    @Override public boolean hasWrite(Object o) {
        return o != null && o instanceof Base64String;
    }
}
