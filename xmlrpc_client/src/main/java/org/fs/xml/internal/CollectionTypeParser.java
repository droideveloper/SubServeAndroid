package org.fs.xml.internal;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.internal.type.CollectionTypeParser
 */
class CollectionTypeParser implements TypeParser<Collection<?>> {

    private final static String DATA    = "data";
    private final static String VALUE   = "value";

    public static CollectionTypeParser create() {
        return new CollectionTypeParser();
    }

    private CollectionTypeParser() { }

    @SuppressWarnings("unchecked")
    @Override public void write(XmlSerializer writer, Collection<?> collection) throws IOException {
        writer.startTag(null, Constants.ARRAY);
        writer.startTag(null, DATA);
        for (Object object : collection) {
            writer.startTag(null, VALUE);
            TypeParser converter = Parser.findWriteParser(object);
            converter.write(writer, object);
            writer.endTag(null, VALUE);
        }
        writer.endTag(null, DATA);
        writer.endTag(null, Constants.ARRAY);
    }

    @Override public Collection<?> read(XmlPullParser reader) throws XmlPullParserException, IOException {
        Collection<Object> array = new ArrayList<>();
        int type = reader.getEventType();
        while (true) {
            if (type == XmlPullParser.START_TAG) {
                String text = reader.getName();
                boolean ignore = Constants.ARRAY.equalsIgnoreCase(text) || DATA.equalsIgnoreCase(text) || VALUE.equalsIgnoreCase(text);
                if (!ignore) {
                    if (text != null) {//this shows null sometimes
                        TypeParser converter = Parser.findReadParser(reader);
                        Object object = converter.read(reader);
                        array.add(object);
                        continue;//since we change internal converters we might get where we are already no need to call for next
                    }
                }
            } else if (type == XmlPullParser.END_TAG) {
                String text = reader.getName();
                if (Constants.ARRAY.equalsIgnoreCase(text)) {
                    //go on next START_TAG
                    reader.next();
                    break;
                }
            }
            type = reader.next();
        }
        return array;
    }

    @Override public boolean hasRead(XmlPullParser reader) {
        final String nodeName = reader.getName();
        return nodeName != null && Constants.ARRAY.equalsIgnoreCase(nodeName);
    }

    @Override public boolean hasWrite(Object o) {
        return o != null && o instanceof Collection && !((Collection) o).isEmpty();
    }
}
