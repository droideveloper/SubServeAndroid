package org.fs.xml.transform;

import org.simpleframework.xml.transform.Transform;

/**
 * Created by Fatih on 10/11/15.
 * as org.fs.xml.transform.BooleanTransform
 */
/* @hide */
public class BooleanTransform implements Transform<Boolean> {

    public final static int STYLE_BINARY = 1;
    public final static int STYLE_STR    = 2;

    private final int style;

    public BooleanTransform() {
        this.style = STYLE_BINARY;
    }

    public BooleanTransform(int style) {
        this.style = style;
    }

    @Override
    public Boolean read(String source) throws Exception {
        switch (style) {
            case STYLE_BINARY: {
                return parseBinaryStyle(source);
            }

            case STYLE_STR: {
                return parseStrStyle(source);
            }

            default:
                throw new Exception("if you see this error, you are doomed!");
        }
    }

    /**
     * Parse Binary Style text to Boolean
     * @param str
     * @return
     * @throws Exception
     */
    private boolean parseBinaryStyle(String str) throws Exception {
        int bit = Integer.parseInt(str);
        if(bit < 0 || bit > 1) {
            throw new Exception("we can not convert value out of (1, 0)");
        }
        return bit == 1;
    }

    /**
     * Parse Str Style text to Boolean there is false-positive-parse if there is
     * @param str
     * @return
     * @throws Exception
     */
    private boolean parseStrStyle(String str) throws Exception {
        if(str == null) throw new Exception("you are not giving me false-positive parse bitch!!!");
        return str.equalsIgnoreCase("true");
    }

    @Override
    public String write(Boolean source) throws Exception {
        switch (style) {
            case STYLE_BINARY: {
                return source ? "1" : "0";
            }
            case STYLE_STR: {
                return source ? "true" : "false";
            }
            default:
                throw new Exception("if you see this error, you are doomed!");
        }
    }
}
