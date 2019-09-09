package com.txwstudio.app.timetable;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.github.chrisbanes.photoview.PhotoView;
import com.ortiz.touchview.TouchImageView;

public class CampusMapActivity extends AppCompatActivity {

    private WebView webview;
    private PhotoView photoView;
    private TouchImageView touchImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.setupTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        webview = (WebView) findViewById(R.id.traffic_schoolmap_webview);
//        photoView = (PhotoView) findViewById(R.id.photo_view);
        touchImageView = (TouchImageView) findViewById(R.id.touchImageView);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String schoolMap = prefs.getString("schoolMapPath", "");
        String schoolMapPath = "file://" + schoolMap;
        Uri campusMapUri = Uri.parse(schoolMapPath);

//        WebSettings webSettings = webview.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setSupportZoom(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDisplayZoomControls(false);
//        webview.setWebViewClient(new WebViewClient());
//        webview.setInitialScale(150);
//        webview.loadDataWithBaseURL("", "<img src='"+ schoolMapPath + "' />",
//                "text/html", "utf-8", null);


//        photoView.setImageURI(campusMapUri);

        touchImageView.setImageURI(campusMapUri);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
