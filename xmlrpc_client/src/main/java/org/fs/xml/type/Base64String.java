package org.fs.xml.type;

import android.text.TextUtils;
import android.util.Base64;

import java.nio.charset.Charset;

/**
 * Created by Fatih on 25/06/16.
 * as org.fs.xml.internal.type.Base64String
 */
public final class Base64String {

    private final String str;

    public Base64String(String str) {
        this.str = str;
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("string is null");
        }
    }

    public String decode() {
        return decode("UTF-8", Base64.NO_WRAP);
    }

    public String decode(String enc, int flags) {
        byte[] buffer = str.getBytes(Charset.forName(enc));
        buffer = Base64.decode(buffer, flags);
        return new String(buffer, Charset.forName(enc));
    }

    public String encode() {
        return encode("UTF-8", Base64.NO_WRAP);
    }

    public String encode(String enc, int flags) {
        byte[] buffer = str.getBytes(Charset.forName(enc));
        buffer = Base64.encode(buffer, flags);
        return new String(buffer, Charset.forName(enc));
    }
}
