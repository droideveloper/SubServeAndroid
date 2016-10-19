package org.fs.xml.type;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Text;

/**
 * Created by Fatih on 10/11/15.
 * as org.fs.xml.type.Base64
 */
/** @hide */
@Element(name = TypeDefinitions.TYPE_BASE64)
public class XMLRpcBase64 {

    @Text
    private String value;

    public XMLRpcBase64() {
    }

    public XMLRpcBase64(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * static method to encode string to base64 string
     * @param value
     * @return encoded String
     * @throws Exception
     */
    public static String encode(String value) throws Exception {
        byte[] data = value.getBytes("UTF-8");
        return android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
    }

    /**
     * static method to decode base64 string to string
     * @param value
     * @return decoded string
     * @throws Exception
     */
    public static String decode(String value) throws Exception {
        byte[] data = android.util.Base64.decode(value, android.util.Base64.DEFAULT);
        return new String(data, "UTF-8");
    }
}
