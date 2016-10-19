package org.fs.xml.internal;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.internal.StructTypeParser
 */
class StructTypeParser implements TypeParser<Map<String, ?>> {

    private final static String MEMBER = "member";
    private final static String NAME   = "name";//for this we gone hijack StringTypeConverter internally, yahoo
    private final static String VALUE  = "value";

    private final StringTypeParser converter = StringTypeParser.create(StringTypeParser.STYLE_WRAP);//we just gone read

    public static StructTypeParser create() {
        return new StructTypeParser();
    }

    private StructTypeParser() {}

    @SuppressWarnings("unchecked")
    @Override public void write(XmlSerializer writer, Map<String, ?> value) throws IOException {
        writer.startTag(null, Constants.STRUCT);
        for (Map.Entry<String, ?> entry : value.entrySet()) {
            writer.startTag(null, MEMBER);
            //write key
            writer.startTag(null, NAME);
            writer.text(entry.getKey());
            writer.endTag(null, NAME);
            //writer value
            writer.startTag(null, VALUE);
            Object o = entry.getValue();
            TypeParser converter = Parser.findWriteParser(o);
            converter.write(writer, o);
            writer.endTag(null, VALUE);

            writer.endTag(null, MEMBER);
        }
        writer.endTag(null, Constants.STRUCT);
    }

    @Override public Map<String, ?> read(XmlPullParser reader) throws XmlPullParserException, IOException {
        Map<String, Object> map = new HashMap<>();
        String key = null;
        int type = reader.getEventType();
        while (true) {
            if (type == XmlPullParser.START_TAG) {
                String text = reader.getName();
                boolean ignore = Constants.STRUCT.equalsIgnoreCase(text) || MEMBER.equalsIgnoreCase(text) || VALUE.equalsIgnoreCase(text);
                if (!ignore) {
                    if (NAME.equalsIgnoreCase(text)) {
                        key = converter.read(reader);//we read key
                        continue;//internal next called
                    } else if (text != null){//I don't know why we getting name null
                        TypeParser converter = Parser.findReadParser(reader);
                        Object object = converter.read(reader);
                        map.put(key, object);
                        key = null;//change it just in case
                        continue;//internal next called
                    }
                }
            } else if (type == XmlPullParser.END_TAG) {
                String text = reader.getName();
                if (Constants.STRUCT.equalsIgnoreCase(text)) {
                    //go on next START_TAG
                    reader.next();
                    break;
                }
            }
            type = reader.next();
        }
        return map;
    }

    @Override public boolean hasRead(XmlPullParser reader) {
        final String nodeName = reader.getName();
        return nodeName != null && Constants.STRUCT.equalsIgnoreCase(nodeName);
    }

    @Override public boolean hasWrite(Object o) {
        return o != null && o instanceof Map && !((Map) o).isEmpty();
    }
}
