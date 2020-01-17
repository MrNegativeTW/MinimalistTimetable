package com.txwstudio.app.timetable;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import com.github.chrisbanes.photoview.PhotoView;
import com.ortiz.touchview.TouchImageView;
import static com.txwstudio.app.timetable.SettingsFragment.MAP_REQUEST_PREF_NAME;

public class CampusMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.setupTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        WebView webview = (WebView) findViewById(R.id.traffic_schoolmap_webview);
//        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        TouchImageView touchImageView = (TouchImageView) findViewById(R.id.touchImageView);
        TextView campusMapErrorMsg = (TextView) findViewById(R.id.campusMapErrorTextView);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String mapPath = prefs.getString(MAP_REQUEST_PREF_NAME, "");
        Uri mapUri = Uri.parse(mapPath);
        if (!mapPath.isEmpty()) {
            campusMapErrorMsg.setVisibility(View.INVISIBLE);
        }

        /**Old method, remove soon.*/
//        String mapPathString = "file://" + mapPath;
//        Uri mapUri = Uri.parse(mapPathString);

//        WebSettings webSettings = webview.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setSupportZoom(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDisplayZoomControls(false);
//        webview.setWebViewClient(new WebViewClient());
//        webview.setInitialScale(150);
//        webview.loadDataWithBaseURL("", "<img src='"+ mapPath + "' />",
//                "text/html", "utf-8", null);

//        photoView.setImageURI(mapUri);

        touchImageView.setImageURI(mapUri);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) { finish(); }
        return super.onOptionsItemSelected(item);
    }
}
