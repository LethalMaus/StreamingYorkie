package com.lethalmaus.streaming_yorkie.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.RequestHandler;
import com.lethalmaus.streaming_yorkie.request.UserRequestHandler;
import com.lethalmaus.streaming_yorkie.view.UserView;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Activity for logging in & out of twitch & the app
 * @author LethalMaus
 */
//This is needed to log into Twitch, even though its not recommended & considered dangerous. Hence the Lint suppression
@SuppressLint("SetJavaScriptEnabled")
public class Authorization extends AppCompatActivity {

    //All activities & contexts are weak referenced to avoid memory leaks
    private WeakReference<Activity> weakActivity;
    private WeakReference<Context> weakContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weakActivity = new WeakReference<>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        if (new File(this.getFilesDir() + File.separator + Globals.FILE_TOKEN).exists()) {
            setContentView(R.layout.logout);
            if(getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Logout");
            }
            new UserView(weakActivity, weakContext).execute();
            ImageButton logout = findViewById(R.id.authorization_logout);
            logout.setOnClickListener((View v) ->
                    promptUser()
            );
        } else {
            setContentView(R.layout.authorization);
            if (RequestHandler.networkIsAvailable(weakContext)) {
                setWebView();
            } else {
                setContentView(R.layout.error);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }

    /**
     * Sets up the WebView for handling Twitch login
     * @author LethalMaus
     */
    private void setWebView() {
        final WebView webView = findViewById(R.id.authWebView);
        webView.setWebViewClient(new WebViewClient() {

            @RequiresApi(21)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().contains(Globals.TWITCH_URL)) {
                    if (request.getUrl().toString().contains("https://www.twitch.tv/?no-reload=true")){
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        webView.destroy();
                        finish();
                        return false;
                    }
                    if (request.getUrl().toString().contains("https://www.twitch.tv/passport-callback#access_token")) {
                        new WriteFileHandler(weakActivity, weakContext, Globals.FILE_TWITCH_TOKEN, null, request.getUrl().toString().substring(request.getUrl().toString().indexOf(Globals.ACCESS_TOKEN) + 13, request.getUrl().toString().indexOf(Globals.ACCESS_TOKEN) + 43), false).writeToFileOrPath();
                        Toast.makeText(Authorization.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        webView.destroy();
                        finish();
                        return false;
                    }
                    view.loadUrl(request.getUrl().toString());
                    return false;
                } else if (request.getUrl().toString().contains("http://localhost/?error=access_denied")) {
                    setContentView(R.layout.error);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("localhost") && url.contains(Globals.ACCESS_TOKEN) && !url.contains(Globals.TWITCH_URL)) {
                    new WriteFileHandler(weakActivity, weakContext, Globals.FILE_TOKEN, null, url.substring(url.indexOf(Globals.ACCESS_TOKEN) + 13, url.indexOf(Globals.ACCESS_TOKEN) + 43), false).writeToFileOrPath();
                    new UserRequestHandler(weakActivity, weakContext).sendRequest();
                    view.loadUrl("https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=" + Globals.TWITCHID + "&redirect_uri=https://www.twitch.tv/passport-callback&scope=chat_login user_read user_subscriptions user_presence_friends_read chat%3Aread+chat%3Aedit+channel%3Amoderate+whispers%3Aread+whispers%3Aedit+channel_editor");
                } else if (!url.contains(Globals.TWITCH_URL)) {
                    setContentView(R.layout.error);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) {
                    setContentView(R.layout.error);
                    TextView errorTitle = findViewById(R.id.error_title);
                    errorTitle.setText(R.string.login_error_title);
                    TextView errorMessage = findViewById(R.id.error_message);
                    errorMessage.setText(R.string.login_error_message);
                }
                super.onReceivedError(view, request, error);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=" + Globals.CLIENTID + "&redirect_uri=http://localhost&force_verify=true&scope=user_follows_edit user_read channel_read chat%3Aread+chat%3Aedit+channel%3Amoderate+whispers%3Aread+whispers%3Aedit+channel_editor");
    }

    /**
     * Prompts channel to confirm deletion of all files.
     * @author LethalMaus
     */
    protected void promptUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Authorization.this, R.style.CustomDialog);
        builder.setPositiveButton("OK", (DialogInterface dialog, int id) -> {
            new Thread() {
                @Override
                public void run() {
                    StreamingYorkieDB streamingYorkieDB = StreamingYorkieDB.getInstance(getApplicationContext());
                    streamingYorkieDB.clearAllTables();
                    new DeleteFileHandler(weakActivity, weakContext, "").deleteFileOrPath("");
                }
            }.start();
            new DeleteFileHandler(weakActivity, weakContext, "").run();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("CANCEL", (DialogInterface dialog, int id) -> {
            //Do nothing
        });
        builder.setMessage("All data will be deleted. Are you sure?");
        builder.setTitle("Logout");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}