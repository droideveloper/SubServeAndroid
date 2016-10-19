package org.fs.sub.views;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.fs.core.AbstractActivity;
import org.fs.sub.SubServeApplication;
import org.fs.sub.R;
import org.fs.sub.view.IPreferenceActivityView;

/**
 * Created by Fatih on
 * as org.fs.sub.views.PreferenceView
 */
public class PreferenceActivityView extends AbstractActivity implements IPreferenceActivityView {

    private final static int REQUEST_CODE = 0x01;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ActionBar supportActionBar = getSupportActionBar();
        //don't want to show this
        if(supportActionBar != null) {
            supportActionBar.setDisplayShowTitleEnabled(false);
        }
        //need to show this since OpenSubtitle.org asks us to do
        View view = findViewById(R.id.redirectView);
        if(view != null) {

        }
        //checks if we have permission from user
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionAlertWindow();
        }

        if(PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE}, 0x0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionAlertWindow() {
        if(!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                       Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE) {
            if(Settings.canDrawOverlays(this)) {
                log(Log.INFO, "we got the permission of SYSTEM_WINDOW_ALERT one ;)");
            }
        }
    }

    @Override
    protected String getClassTag() {
        return PreferenceActivityView.class.getSimpleName();
    }

    @Override
    protected boolean isLogEnabled() {
        return SubServeApplication.isApplicationLogEnabled();
    }
}