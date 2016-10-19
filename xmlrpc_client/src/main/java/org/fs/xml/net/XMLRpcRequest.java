package org.fs.xml.net;

import org.fs.xml.type.Parameter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.internal.type.XMLRpcRequest
 */
public final class XMLRpcRequest {

    private String                methodName;
    private Collection<Parameter> parameters;

    public static XMLRpcRequest create(String methodName) {
        return new XMLRpcRequest(methodName);
    }

    private XMLRpcRequest(String methodName) { this.methodName = methodName; }

    public XMLRpcRequest addParameter(Parameter parameter) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        parameters.add(parameter);
        return this;
    }

    public int parameterSize() {
        return parameters != null ? parameters.size() : 0;
    }

    public Collection<Parameter> parameters() {
        return parameters;
    }

    public String methodName() {
        return methodName;
    }
}
