package org.fs.xml.type;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.internal.type.Parameter
 */
public final class Parameter  {

    private final Object value;

    public static Parameter create(Object value) { return new Parameter(value); }

    private Parameter(Object value) {
        this.value = value;
    }

    public Base64String asBase64String() {
        return type(value);
    }

    public Boolean asBoolean() {
        return type(value);
    }

    public Collection asCollection() {
        return type(value);
    }

    public Date asDate() {
        return type(value);
    }

    public Double asDouble() {
        return type(value);
    }

    public Float asFloat() {
        return type(value);
    }

    public Integer asInteger() {
        return type(value);
    }

    public Long asLong() {
        return type(value);
    }

    public Object asNil() {
        return type(value);
    }

    public String asString() {
        return type(value);
    }

    public Map asMap() {
        return type(value);
    }

    @SuppressWarnings("unchecked")
    private static <C> C type(Object o) {
        return (C) o;
    }
}
