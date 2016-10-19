package org.fs.subserve.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.fs.subserve.api.core.ILogger;
import org.fs.subserve.api.util.PreconditionUtility;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Fatih on 23/05/16.
 * as org.fs.subserve.api.SubServeAPI
 */
public final class SubServeAPI implements ILogger {

    private static final String HOST_SEARCH         = "search";
    private static final String HOST_TIME_ELAPSED   = "timeElapsed";
    private static final String HOST_START          = "start";
    private static final String HOST_STOP           = "stop";
    private static final String HOST_RESUME         = "resume";
    private static final String HOST_PAUSE          = "pause";

    private static final String EXTRA_COMMAND        = "cmd";
    private static final String EXTRA_QUERY          = "q";
    private static final String EXTRA_QUERY_2        = "q2";

    private static final String INTENT_ACTION        = "org.fs.sub.action.ACTION_SHOW";
    private static final String INTENT_CATEGORY      = "org.fs.sub.category.CATEGORY_BROWSE";


    private Context context;

    private static SubServeAPI      mInstance = null;


    private SubServeAPI(Context context) {
        this.context = context;
    }

    public static void onCreate(Context context) {
        PreconditionUtility.checkIfNull(context, "context is null");
        if(mInstance == null) {
            mInstance = new SubServeAPI(context);
        }
    }

    public static void onDestroy() {
        if(mInstance.context != null) {
            mInstance.context = null;
        }
    }

    public static void startApp() {
        Intent intent = createIntent();
        intent.putExtra(EXTRA_COMMAND, HOST_START);
        sendBroadcast(intent);
    }

    public static void stopApp() {
        Intent intent = createIntent();
        intent.putExtra(EXTRA_COMMAND, HOST_STOP);
        sendBroadcast(intent);
    }

    public static void resumeApp() {
        Intent intent = createIntent();
        intent.putExtra(EXTRA_COMMAND, HOST_RESUME);
        sendBroadcast(intent);
    }

    public static void pauseApp() {
        Intent intent = createIntent();
        intent.putExtra(EXTRA_COMMAND, HOST_PAUSE);
        sendBroadcast(intent);
    }

    public static void notifyTime(long ms) {
        Intent intent = createIntent();
        intent.putExtra(EXTRA_COMMAND, HOST_TIME_ELAPSED);
        intent.putExtra(EXTRA_QUERY, String.valueOf(ms));
        sendBroadcast(intent);
    }

    public static void searchWithURL(String url) {
        Intent intent = createIntent();
        intent.putExtra(EXTRA_COMMAND, HOST_SEARCH);
        intent.putExtra(EXTRA_QUERY, url);
        sendBroadcast(intent);
    }

    public static void searchWithIMDB(String imdb) {
        Intent intent = createIntent();
        intent.putExtra(EXTRA_COMMAND, HOST_SEARCH);
        intent.putExtra(EXTRA_QUERY_2, imdb);
        sendBroadcast(intent);
    }

    private static Intent createIntent() {
        if(mInstance != null) {
            Intent intent = new Intent();
            intent.setAction(INTENT_ACTION);
            intent.addCategory(INTENT_CATEGORY);
            return intent;
        } else {
            throw new NullPointerException("you should call SubServeAPI.onCreate(Context context) at first!");
        }
    }

    private static void sendBroadcast(Intent in) {
        if(mInstance != null) {
            if(mInstance.context != null) {
                mInstance.context.sendBroadcast(in);
            }
        }
    }

    @Override
    public void log(String msg) {
        log(Log.DEBUG, msg);
    }

    @Override
    public void log(Exception e) {
        StringWriter strWriter = new StringWriter();
        PrintWriter  prtWriter = new PrintWriter(strWriter);
        e.printStackTrace(prtWriter);
        log(Log.ERROR, strWriter.toString());
    }

    @Override
    public void log(int priority, String msg) {
        if(isLogEnabled()) {
            Log.println(priority, getClassTag(), msg);
        }
    }

    @Override
    public String getClassTag() {
        return SubServeAPI.class.getSimpleName();
    }

    @Override
    public boolean isLogEnabled() {
        return BuildConfig.DEBUG;
    }
}
