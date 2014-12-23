package com.chc.found;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.webkit.WebView;

import com.chc.found.config.Apis;
import com.test.found.R;


/**
 * Created by Lance on 9/10/14.
 */
public class UserManualActivity extends ActionBarActivity {
    private static final String TAG = UserManualActivity.class.getSimpleName();
    private WebView webView;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_user_manual);

        webView = (WebView) findViewById(R.id.webView1);

        if (icicle == null) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(Apis.URI_USER_MANUAL);
           // webView.loadUrl("www.google.com");
        } else {
            webView.restoreState(icicle);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        finish();
        this.overridePendingTransition(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
