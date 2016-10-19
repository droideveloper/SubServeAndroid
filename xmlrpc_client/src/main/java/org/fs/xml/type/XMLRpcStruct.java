package org.fs.xml.type;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Fatih on 10/11/15.
 * as org.fs.xml.type.Struct
 */
/* @hide */
@Root(name = TypeDefinitions.TYPE_STRUCT)
public class XMLRpcStruct {

    @ElementMap(entry   = TypeDefinitions.ENTRY_MAP,
                key     = TypeDefinitions.KEY_MAP,
                inline  = true, required = false)
    private Map<String, XMLRpcObject> values;

    public XMLRpcStruct() {
    }

    public XMLRpcStruct(Map<String, XMLRpcObject> values) {
        this.values = values;
    }

    public void setValues(Map<String, XMLRpcObject> values) {
        this.values = values;
    }

    public Map<String, XMLRpcObject> getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("@Struct {\n");
        for (Map.Entry<String, XMLRpcObject> entry : values.entrySet()) {
            str.append(String.format(Locale.US, "@Entry { key=%s, value=%s }\n",
                                     entry.getKey(),
                                     entry.getValue().toString()));
        }
        str.append("\n }");
        return str.toString();
    }

    public XMLRpcStruct add(String key, XMLRpcObject value) {
        if(values != null) {
            values.put(key, value);
        } else {
            return withEmptyMap()
                    .add(key, value);
        }
        return this;
    }

    public XMLRpcObject getValue(String key) {
        if(values != null && key != null) {
            return values.get(key);
        } else {
            return null;
        }
    }

    public XMLRpcStruct withEmptyMap() {
        if(values != null) {
            values.clear();
        } else {
            values = new HashMap<>();
        }
        return this;
    }
}
