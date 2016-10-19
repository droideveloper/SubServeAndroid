package org.fs.xml.type;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Fatih on 10/11/15.
 * as org.fs.xml.type.Object
 */
/* @hide */
@Root(name = TypeDefinitions.KEY_VALUE)
public class XMLRpcObject {

    @ElementUnion({
            @Element(name = TypeDefinitions.TYPE_STRING,           type = String.class, required = false),
            @Element(name = TypeDefinitions.TYPE_DOUBLE,           type = Double.class, required = false),
            @Element(name = TypeDefinitions.TYPE_INTEGER,          type = Integer.class, required = false),
            @Element(name = TypeDefinitions.TYPE_INTEGER_V2,       type = Integer.class, required = false),
            @Element(name = TypeDefinitions.TYPE_LONG,             type = Long.class, required = false),
            @Element(name = TypeDefinitions.TYPE_LONG_V2,          type = Long.class, required = false),
            @Element(name = TypeDefinitions.TYPE_BOOLEAN,          type = Boolean.class, required = false),
            @Element(name = TypeDefinitions.TYPE_DATE,             type = Date.class, required = false),
            @Element(name = TypeDefinitions.TYPE_BASE64,           type = XMLRpcBase64.class, required = false),
            @Element(name = TypeDefinitions.TYPE_STRUCT,           type = XMLRpcStruct.class, required = false),
            @Element(name = TypeDefinitions.TYPE_ARRAY,            type = XMLRpcArray.class, required = false)
    })
    private Object value;

    public XMLRpcObject() {
    }

    public XMLRpcObject(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    /**
     * deep cast for expected result as String
     * @return
     */
    public String asString() {
        return (String) getValue();
    }

    /**
     * deep cast for expected result as Double
     * @return
     */
    public Double asDouble() {
        return (Double) getValue();
    }

    /**
     * deep cast for expected result as Integer
     * @return
     */
    public Integer asInteger() {
        return (Integer) getValue();
    }

    /**
     * deep cast for expected result as Long
     * @return
     */
    public Long asLong() {
        return (Long) getValue();
    }

    /**
     * deep cast for expected result as Boolean
     * @return
     */
    public Boolean asBoolean() {
        return (Boolean) getValue();
    }

    /**
     * deep cast for expected result as Date
     * @return
     */
    public Date asDate() {
        return (Date) getValue();
    }

    /**
     * deep cast for expected result as Base64
     * @return
     */
    public XMLRpcBase64 asBase64() {
        return (XMLRpcBase64) getValue();
    }

    /**
     * deep cast for expected result as Struct
     * @return
     */
    public XMLRpcStruct asStruct() {
        return (XMLRpcStruct) getValue();
    }

    /**
     * deep cast for expected result as Array
     * @return
     */
    public XMLRpcArray asArray() {
        return (XMLRpcArray) getValue();
    }

    @Override
    public String toString() {
        return String.format("\n@Object { \nvalue = %s\n }", value.toString());
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     *
     * @param value
     * @return
     */
    public static XMLRpcObject withValue(Object value) {
        return new XMLRpcObject(value);
    }

    /**
     *
     * @param values
     * @return
     */
    public static List<XMLRpcObject> withValues(Object... values) {
        return withValues(Arrays.asList(values));
    }

    /**
     *
     * @param values
     * @return
     */
    public static List<XMLRpcObject> withValues(List<Object> values) {
        List<XMLRpcObject> XMLRpcObjects = new ArrayList<>();
        for(Object obj : values) {
            XMLRpcObjects.add(withValue(obj));
        }
        return XMLRpcObjects;
    }
}
