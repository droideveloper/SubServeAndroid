package org.fs.xml.internal;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.internal.NullTypeParser
 */
class NullTypeParser implements TypeParser<Object> {

    public static NullTypeParser create() {
        return new NullTypeParser();
    }

    private NullTypeParser() {}

    @Override public void write(XmlSerializer writer, Object value) throws IOException {
        writer.startTag(null, Constants.NIL);
        writer.endTag(null, Constants.NIL);
    }

    @Override public Object read(XmlPullParser reader) throws XmlPullParserException, IOException {
        //fact is <nil /> fits both START_TAG and END_TAG events so calling twice needed
        /*<nil /> START_TAG*/   reader.next();
        /*<nil /> END_TAG*/     reader.next(); //go on next START_TAG
        return null;
    }

    @Override public boolean hasRead(XmlPullParser reader) {
        final String nodeName = reader.getName();
        return nodeName != null && Constants.NIL.equalsIgnoreCase(nodeName);
    }

    @Override public boolean hasWrite(Object o) {
        return o == null;
    }
}
