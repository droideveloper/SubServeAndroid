package org.fs.xml.transform;

import org.simpleframework.xml.transform.Transform;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Fatih on 10/11/15.
 * as org.fs.xml.transform.DateTransform
 */
/* @hide */
public class DateTransform implements Transform<Date> {


    private final SimpleDateFormat simpleDateFormat;

    public DateTransform() {
        this("yyyyMMdd'T'HH:mm:ss");
    }

    public DateTransform(String format) {
        this(format, Locale.US);
    }

    public DateTransform(String format, Locale locale) {
        simpleDateFormat = new SimpleDateFormat(format, locale);
    }

    @Override
    public Date read(String source) throws Exception {
        return simpleDateFormat.parse(source);
    }

    @Override
    public String write(Date source) throws Exception {
        return simpleDateFormat.format(source);
    }
}
