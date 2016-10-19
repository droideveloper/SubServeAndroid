package org.fs.xml.internal;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.internal.IntegerTypeParser
 */
class IntegerTypeParser implements TypeParser<Integer> {

    private final String preferred;

    public static IntegerTypeParser create(String preferred) {
        return new IntegerTypeParser(preferred);
    }

    private IntegerTypeParser(String preferred) {
        this.preferred = preferred;
    }

    @Override public void write(XmlSerializer writer, Integer value) throws IOException {
        writer.startTag(null, preferred);
        writer.text(String.valueOf(value));
        writer.endTag(null, preferred);
    }

    @Override public Integer read(XmlPullParser reader) throws XmlPullParserException, IOException {
        //we hit tag something like this
        //<i4> or <int> or <integer> then we need to call next
        int type = reader.getEventType();
        String text = null;
        while (type != XmlPullParser.END_TAG) {
            if (type == XmlPullParser.TEXT) {
                text = reader.getText();
            }
            type = reader.nextToken();
        }
        //not stay at end tag so
        reader.nextToken();
        return Integer.parseInt(text);
    }

    @Override public boolean hasRead(XmlPullParser reader) {
        final String nodeName = reader.getName();
        return nodeName != null && preferred.equalsIgnoreCase(nodeName);
    }

    @Override public boolean hasWrite(Object o) {
        return o != null && o instanceof Integer;
    }
}
