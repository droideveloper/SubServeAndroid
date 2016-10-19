package org.fs.xml.internal;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.internal.StringTypeParser
 */
class StringTypeParser implements TypeParser<String> {

    public final static int STYLE_NO_WRAP = 0x01;
    public final static int STYLE_WRAP    = 0x02;

    private final int style;

    public static StringTypeParser create(int style) {
        return new StringTypeParser(style);
    }

    private StringTypeParser(int style) {
        this.style = style;
        if (style < STYLE_NO_WRAP || style > STYLE_WRAP) {
            throw new IllegalArgumentException("style is not valid");
        }
    }

    @Override public void write(XmlSerializer writer, String value) throws IOException {
        if (style == STYLE_NO_WRAP) {
            writer.text(value);
        } else if (style == STYLE_WRAP) {
            writer.startTag(null, Constants.STRING);
            writer.text(value);
            writer.endTag(null, Constants.STRING);
        } else {
            throw new IOException("you should not be here");
        }
    }

    @Override public String read(XmlPullParser reader) throws XmlPullParserException, IOException {
        int type = reader.getEventType();
        if (style == STYLE_NO_WRAP) {
            if (type != XmlPullParser.TEXT) {
                throw new IOException("you should not be here");
            }
            return reader.getText();
        } else if (style == STYLE_WRAP) {
            String text = null;
            while (type != XmlPullParser.END_TAG) {
                if (type == XmlPullParser.TEXT) {
                    text = reader.getText();
                }
                type = reader.next();
            }
            //go on next START_TAG
            reader.next();
            return text;
        } else {
            throw new IOException("you should not be here");
        }
    }

    @Override public boolean hasRead(XmlPullParser reader) {
        try {
            if (style == STYLE_NO_WRAP) {
                return reader.getEventType() == XmlPullParser.TEXT;
            } else if (style == STYLE_WRAP) {
                final String nodeName = reader.getName();
                return nodeName != null && Constants.STRING.equalsIgnoreCase(nodeName);
            } else {
                throw new RuntimeException("you should not be here");
            }
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public boolean hasWrite(Object o) {
        return o != null && o instanceof String;
    }
}
