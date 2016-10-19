package org.fs.xml.net;

import org.fs.xml.type.XMLRpcParam;
import org.fs.xml.type.TypeDefinitions;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fatih on 10/11/15.
 * as org.fs.xml.request.XMLRpcRequest
 */
@Root(name = TypeDefinitions.METHOD_CALL)
public class XMLRpcRequest {

    @Element(required = true)
    private String methodName;

    @Path(TypeDefinitions.KEY_PARAMS)
    @ElementList(inline = true, required = false)
    private List<XMLRpcParam> XMLRpcParams;

    public XMLRpcRequest() {
    }

    public List<XMLRpcParam> getXMLRpcParams() {
        return XMLRpcParams;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setXMLRpcParams(List<XMLRpcParam> XMLRpcParams) {
        this.XMLRpcParams = XMLRpcParams;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Add param object to list of params
     * @param XMLRpcParam Param object instance to be added
     * @return self
     */
    public XMLRpcRequest add(XMLRpcParam XMLRpcParam) {
        if(XMLRpcParams != null) {
            XMLRpcParams.add(XMLRpcParam);
        }
        else {
            return withEmptyParams()
                    .add(XMLRpcParam);
        }
        return this;
    }

    /**
     * Start new param List or clear old one
     * @return
     */
    public XMLRpcRequest withEmptyParams() {
        if(XMLRpcParams == null) {
            XMLRpcParams = new ArrayList<>();
        } else {
            XMLRpcParams.clear();
        }
        return this;
    }

    /**
     * sets the method name that proxy to service method (Remote Proxy)
     * @param methodName String name of method
     * @return self
     */
    public XMLRpcRequest withMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("@Request { \n");
        for (XMLRpcParam XMLRpcParam : XMLRpcParams) {
            str.append(XMLRpcParam.toString());
            str.append("\n");
        }
        str.append("\n }");
        return str.toString();
    }
}
