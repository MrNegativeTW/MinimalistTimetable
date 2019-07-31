package com.txwstudio.app.timetable;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

public class SchoolMap extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.setupTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_school_map);

        WebView webview = (WebView) findViewById(R.id.traffic_schoolmap_webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String schoolMapPath = prefs.getString("schoolMapPath", "");
        String schoolMapPathToDisplay = "file://" + schoolMapPath;

        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.setInitialScale(150);
        /*webview.loadUrl("https://raw.githubusercontent.com/MrNegativeTW/simpleCourseTable/master/schoolMap.jpg");*/
        webview.loadDataWithBaseURL("", "<img src='"+ schoolMapPathToDisplay + "' />", "text/html", "utf-8", null);

    }
}
