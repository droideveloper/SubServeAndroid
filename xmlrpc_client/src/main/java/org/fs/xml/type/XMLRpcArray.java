package org.fs.xml.type;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fatih on 10/11/15.
 * as org.fs.xml.type.Array
 */
/* @hide */
@Root(name = TypeDefinitions.TYPE_ARRAY)
public class XMLRpcArray {

    @Path(TypeDefinitions.PATH_DATA)
    @ElementList(inline = true)
    private List<XMLRpcObject> values;

    public XMLRpcArray() {
    }

    public XMLRpcArray(List<XMLRpcObject> values) {
        this.values = values;
    }

    public List<XMLRpcObject> getValues() {
        return values;
    }

    public void setValues(List<XMLRpcObject> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("@Array {\n");
        for (XMLRpcObject o : values) {
            b.append(String.format(Locale.US, "@Element { value=%s }\n",
                                   o.toString()));
        }
        return b.append("\n}").toString();
    }

    public XMLRpcArray add(XMLRpcObject value) {
        if(values != null) {
            values.add(value);
        } else {
            return withEmpty()
                    .add(value);
        }
        return this;
    }

    public XMLRpcArray withEmpty() {
        if(values == null) {
            values = new ArrayList<>();
        } else {
            values.clear();
        }
        return this;
    }
}
