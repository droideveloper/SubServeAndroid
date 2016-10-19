package org.fs.xml.type;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fatih on 10/11/15.
 * as org.fs.xml.type.Param
 */
/* @hide */
@Root(name = TypeDefinitions.KEY_PARAM)
public class XMLRpcParam {

    @Element(required = false)
    private XMLRpcObject value;

    public XMLRpcParam() { }
    public XMLRpcParam(XMLRpcObject value) { this.value = value; }

    public XMLRpcObject getValue() {
        return value;
    }

    public void setValue(XMLRpcObject value) {
        this.value = value;
    }

    /**
     * Create Param object from Object instnace
     * @param obj
     * @return
     */
    public static XMLRpcParam withObject(XMLRpcObject obj) {
        return new XMLRpcParam(obj);
    }

    /**
     *
     * @param objs
     * @return
     */
    public static List<XMLRpcParam> withObjects(XMLRpcObject... objs) {
        return withObjects(Arrays.asList(objs));
    }

    /**
     *
     * @param objs
     * @return
     */
    public static List<XMLRpcParam> withObjects(List<XMLRpcObject> objs) {
        List<XMLRpcParam> XMLRpcParams = new ArrayList<>();
        for (XMLRpcObject obj : objs) {
            XMLRpcParams.add(withObject(obj));
        }
        return XMLRpcParams;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "\n@Param { \n%s\n }",
                             value.toString());
    }
}
