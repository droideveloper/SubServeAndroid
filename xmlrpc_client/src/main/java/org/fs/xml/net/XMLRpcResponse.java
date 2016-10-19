package org.fs.xml.net;

import org.fs.xml.type.XMLRpcParam;
import org.fs.xml.type.XMLRpcObject;
import org.fs.xml.type.TypeDefinitions;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Fatih on 10/11/15.
 * as org.fs.xml.net.XMLRpcResponse
 */
@Root(name = TypeDefinitions.METHOD_RESPONSE)
public class XMLRpcResponse {

    @Path(TypeDefinitions.KEY_PARAMS)
    @ElementList(required = false, inline = true)
    private List<XMLRpcParam> XMLRpcParams;

    @Element(required = false)
    private XMLRpcObject fault;

    public XMLRpcResponse() {
    }

    public List<XMLRpcParam> getXMLRpcParams() {
        return XMLRpcParams;
    }

    public XMLRpcObject getFault() {
        return fault;
    }

    public void setXMLRpcParams(List<XMLRpcParam> XMLRpcParams) {
        this.XMLRpcParams = XMLRpcParams;
    }

    /**
     * checks if any fault occured or it's is ok for processing data from response
     * @return
     */
    public boolean isSuccess() {
        return fault == null;
    }

    public XMLRpcParam firstParam() {
        return (XMLRpcParams != null && XMLRpcParams.size() > 0) ? XMLRpcParams.get(0) : null;
    }

    public void setFault(XMLRpcObject fault) {
        this.fault = fault;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (isSuccess()) {
            str.append("@Response { \n");
            for (XMLRpcParam XMLRpcParam : XMLRpcParams) {
                str.append(XMLRpcParam.toString());
                str.append("\n");
            }
            str.append("\n }");
        } else {
            str.append("@Fault { \n");
            str.append(fault.toString());
            str.append("\n}");
        }
        return str.toString();
    }
}
