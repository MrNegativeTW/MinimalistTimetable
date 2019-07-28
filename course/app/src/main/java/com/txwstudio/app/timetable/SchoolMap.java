package com.txwstudio.app.timetable;

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
//        setTitle(R.string.SchoolMap);

        WebView webview = (WebView) findViewById(R.id.traffic_schoolmap_webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //setContentView(webview);

        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.setInitialScale(150);
        /*webview.loadUrl("https://raw.githubusercontent.com/MrNegativeTW/simpleCourseTable/master/schoolMap.jpg");*/
        webview.loadDataWithBaseURL("file:///android_res/drawable/school_map.jpg", "<img src='school_map.jpg' />", "text/html", "utf-8", null);

    }
}
