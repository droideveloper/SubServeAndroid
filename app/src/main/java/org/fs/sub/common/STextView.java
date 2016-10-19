package org.fs.sub.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Fatih on 13/05/16.
 * as org.fs.sub.common.STextView
 */
public class STextView extends TextView implements ITextView {

    /**
     * TODO find a way to implement stroke over textView
     * TODO make sure that \n is not effected with onDraw(canvas) implementations
     */




    public STextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public STextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public STextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setDrawingCacheEnabled(false);
        strokeThroughText();
    }

    @Override
    public float shadowRadius() {
        return 0.25f * getTextSize();
    }

    @Override
    public  void strokeThroughText() {
        setShadowLayer(shadowRadius(), 0f, 0f, invertColor());
    }

    @Override
    public int invertColor() {
        int color = getCurrentTextColor();
        return (color & 0xFF000000) | (~color & 0x00FFFFFF);
    }
}
