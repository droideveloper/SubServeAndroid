package org.fs.xml.type;

/**
 * Created by Fatih on 10/11/15.
 * as org.fs.xml.type.Types
 */
/** @hide */
public class TypeDefinitions {

    /**
     * primitive type definitions
     */
    public final static String TYPE_STRING      = "string";
    public final static String TYPE_DOUBLE      = "double";
    public final static String TYPE_INTEGER     = "int";
    public final static String TYPE_INTEGER_V2  = "i4";
    public final static String TYPE_LONG        = "long";
    public final static String TYPE_LONG_V2     = "i8";
    public final static String TYPE_BOOLEAN     = "boolean";

    /**
     * object type definitions
     */
    public final static String TYPE_DATE        = "dateTime.iso8601";
    public final static String TYPE_ARRAY       = "array";
    public final static String TYPE_STRUCT      = "struct";
    public final static String TYPE_BASE64      = "base64";

    /**
     * Definitions used in Serialization process
     */
    public final static String ENTRY_MAP        = "member";
    public final static String KEY_MAP          = "name";
    public final static String PATH_DATA        = "data";
    public final static String KEY_VALUE        = "value";
    public final static String KEY_PARAMS       = "params";
    public final static String METHOD_CALL      = "methodCall";
    public final static String METHOD_RESPONSE  = "methodResponse";
    public final static String KEY_PARAM        = "param";
}
