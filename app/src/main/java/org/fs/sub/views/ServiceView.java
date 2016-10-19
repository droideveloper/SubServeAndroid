package org.fs.sub.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher;

import org.fs.core.AbstractService;
import org.fs.sub.R;
import org.fs.sub.SubServeApplication;
import org.fs.sub.common.STextView;
import org.fs.sub.presenter.IServiceViewPresenter;
import org.fs.sub.presenters.ServiceViewPresenter;
import org.fs.sub.view.IServiceView;
import org.fs.sub.view.ISubServeView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fatih on
 * as org.fs.sub.views.ServiceView
 */
public class ServiceView extends AbstractService implements IServiceView, ViewSwitcher.ViewFactory {

    private IServiceViewPresenter presenterProxy = null;

    private TextSwitcher          textSwitcher   = null;
    private Animation             inAnimation;
    private Animation             outAnimation;

    private final static Map<String, int[]> animationConfig;
    static {
        animationConfig = new HashMap<>();
        animationConfig.put("bottom",   new int[] { R.anim.bottom_in,   R.anim.bottom_out });
        animationConfig.put("top",      new int[] { R.anim.top_in,      R.anim.top_out });
        animationConfig.put("center",   new int[] { R.anim.center_in,   R.anim.center_out });
    }

    public ServiceView() {
        this.presenterProxy = new ServiceViewPresenter(this);
    }

    @Override
    protected String getClassTag() {
        return ServiceView.class.getSimpleName();
    }

    @Override
    protected boolean isLogEnabled() {
        return SubServeApplication.isApplicationLogEnabled();
    }

    @Override
    public ISubServeView applicationProxy() {
        return (SubServeApplication) getApplication();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void toServiceCommand(Intent intent) {
        startService(intent);
    }

    @Override
    public NotificationManagerCompat notificationManager() {
        return NotificationManagerCompat.from(getContext());
    }

    @Override
    public void setText(SpannableString str) {
        if(textSwitcher != null) {
            textSwitcher.setText(str);
        }
    }

    @Override
    public String getText() {
        return textSwitcher != null ? ((STextView) textSwitcher.getCurrentView()).getText().toString() : null;
    }

    @Override
    public void stopShow() {
        stopSelf();
    }

    @Override
    public void pauseShow() {
//        if(textSwitcher != null) {
//            //textSwitcher.setVisibility(View.GONE);//also cause animation problems
//            //setText(new SpannableString(""));
//        }
    }

    @Override
    public void resumeShow() {
//        if(textSwitcher != null) {
//            //textSwitcher.setVisibility(View.VISIBLE);//this cause animation problems
//            //setText(new SpannableString(""));
//        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        tearDownView();
        if(presenterProxy != null) {
            presenterProxy.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUpView();
        if(presenterProxy != null) {
            presenterProxy.onCreate();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(presenterProxy != null) {
            presenterProxy.handleIntent(intent);
        }
        return START_NOT_STICKY;
    }

    /**
     *
     * @return
     */
    private WindowManager windowManager() {
        return (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    /**
     *
     * @return
     */
    private LayoutParams windowLayoutParams() {
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, //Size of View in Window
                LayoutParams.TYPE_SYSTEM_ALERT, //Type of Window
                LayoutParams.FLAG_NOT_FOCUSABLE //Flags
                        | LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT); //Pixel Format

        String gr = applicationProxy().preferenceProxy().textAlignment();
        //load gravity on user choice
        switch (gr) {
            default:
            case "bottom": {
                params.gravity |= Gravity.BOTTOM;
                break;
            }
            case "top": {
                params.gravity |= Gravity.TOP;
                break;
            }
            case "center": {
                params.gravity |= Gravity.CENTER_VERTICAL;
                break;
            }
        }
        return params;
    }

    /**
     *
     * @param v
     * @return
     */
    private float toSp(int v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, v,
                                         getResources().getDisplayMetrics());
    }

    /**
     *
     */
    private void setUpView() {
        String gr = applicationProxy().preferenceProxy().textAlignment();
        //animation relative to position
        if(animationConfig.containsKey(gr)) {
            int[] animArgs = animationConfig.get(gr);

            if(animArgs != null && animArgs.length > 0) {
                inAnimation = AnimationUtils.loadAnimation(getContext(), animArgs[0]);
                outAnimation= AnimationUtils.loadAnimation(getContext(), animArgs[1]);

            }
        }

        textSwitcher = new TextSwitcher(getContext());
        textSwitcher.setFactory(this);
        if(inAnimation != null) {
            textSwitcher.setInAnimation(inAnimation);
            log(Log.ERROR, "inAnimationSet");
        }
        if(outAnimation != null) {
            log(Log.ERROR, "outAnimationSet");
            textSwitcher.setOutAnimation(outAnimation);
        }

        /**
         * this code needed for latest version of Android OS permission request stuff...
         */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(this)) {
                return;
            }
        }

        windowManager().addView(textSwitcher, windowLayoutParams());
    }

    @Override
    public View makeView() {
        float textSize = toSp(applicationProxy().preferenceProxy().textSize());
        STextView textView = new STextView(getContext());
        textView.setTextSize(textSize);//TextSize
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);// TextOrientation
        int color = applicationProxy().preferenceProxy().textColor();
        textView.setTextColor(color);//TextColor
        //fit the parent
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, //match window width
                                                                       FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        textView.setLayoutParams(params);//match text height ;)
        return textView;
    }

    /**
     *
     */
    private void tearDownView() {
        /**
         * if we did not have permission we should say the user that we need that permission
         */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(this)) return;
        }

        windowManager().removeView(textSwitcher);
        textSwitcher.clearAnimation();
        textSwitcher = null;
    }
}