package org.fs.xml.net;

import org.fs.xml.type.Parameter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.internal.type.XMLRpcResponse
 */
public final class XMLRpcResponse {

    private Object                fault;
    private Collection<Parameter> parameters;

    public static XMLRpcResponse create() {
        return new XMLRpcResponse();
    }

    private XMLRpcResponse() { }

    public void addParameter(Parameter parameter) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        parameters.add(parameter);
    }

    public void addFault(Object fault) {
        this.fault = fault;
    }

    public boolean isSuccess() {
        return fault == null;
    }

    public Collection<Parameter> response() {
        return parameters;
    }

    public Object fault() {
        return fault;
    }
}
