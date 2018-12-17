package com.lethalmaus.twitchfollowerservice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class Authorization extends AppCompatActivity {

    private Globals globals = new Globals(getApplicationContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization);
        WebView webView = findViewById(R.id.authWebView);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                if (url.contains("localhost") && url.contains("access_token")) {
                    String token = url.substring(url.indexOf("access_token") + 13, url.indexOf("access_token") + 43);
                    globals.deleteFileOrPath("", getApplicationContext());
                    globals.writeToFile("TOKEN", token, getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                } else if (url.contains("localhost")) {
                    Toast.makeText(getApplicationContext(), "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                }
                view.loadUrl(url);
                return false;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=" + globals.CLIENTID + "&redirect_uri=http://localhost&force_verify=true&scope=user_follows_edit user_read channel_read");
    }
}
