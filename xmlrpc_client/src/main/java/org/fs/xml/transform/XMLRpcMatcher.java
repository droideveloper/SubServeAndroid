package org.fs.xml.transform;

import org.simpleframework.xml.transform.Matcher;
import org.simpleframework.xml.transform.Transform;

import java.util.Date;

/**
 * Created by Fatih on 10/11/15.
 * as org.fs.xml.XMLRpcMatcher
 */
/* @hide */
public class XMLRpcMatcher implements Matcher {

    private final String formatString;

    public XMLRpcMatcher() {
        this(null);
    }

    public XMLRpcMatcher(String formatString) {
        this.formatString = formatString;
    }

    @Override
    public Transform match(Class type) throws Exception {
        if(Boolean.class.equals(type) || boolean.class.equals(type)) {
            return new BooleanTransform();
        } else if(Date.class.equals(type)) {
            if(null != formatString)
                return new DateTransform(formatString);
            return new DateTransform();
        }
        return null;
    }
}
