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
        if (new File(this.getFilesDir() + File.separator + "TOKEN").exists()) {
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
                final WebView webView = findViewById(R.id.authWebView);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (url.contains("twitch.tv")) {
                            if (url.contains("https://www.twitch.tv/passport-callback#access_token")) {
                                new WriteFileHandler(weakActivity, weakContext, "TWITCH_TOKEN", null, url.substring(url.indexOf("access_token") + 13, url.indexOf("access_token") + 43), false).writeToFileOrPath();
                                Toast.makeText(Authorization.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                webView.destroy();
                                finish();
                                return true;
                            }
                            view.loadUrl(url);
                            return false;
                        }
                        return true;
                    }

                    @RequiresApi(21)
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        if (request.getUrl().toString().contains("twitch.tv")) {
                            if (request.getUrl().toString().contains("https://www.twitch.tv/passport-callback#access_token")) {
                                new WriteFileHandler(weakActivity, weakContext, "TWITCH_TOKEN", null, request.getUrl().toString().substring(request.getUrl().toString().indexOf("access_token") + 13, request.getUrl().toString().indexOf("access_token") + 43), false).writeToFileOrPath();
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
                        }
                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.contains("localhost") && url.contains("access_token") && !url.contains("twitch.tv")) {
                            new WriteFileHandler(weakActivity, weakContext, "TOKEN", null, url.substring(url.indexOf("access_token") + 13, url.indexOf("access_token") + 43), false).writeToFileOrPath();
                            new UserRequestHandler(weakActivity, weakContext).sendRequest();
                            view.loadUrl("https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=" + Globals.TWITCHID + "&redirect_uri=https://www.twitch.tv/passport-callback&scope=chat_login user_read user_subscriptions user_presence_friends_read chat%3Aread+chat%3Aedit+channel%3Amoderate+whispers%3Aread+whispers%3Aedit+channel_editor");
                        } else if (!url.contains("twitch.tv")) {
                            setContentView(R.layout.error);
                        }
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        showError(errorCode);
                        super.onReceivedError(view, errorCode, description, failingUrl);
                    }
                });
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl("https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=" + Globals.CLIENTID + "&redirect_uri=http://localhost&force_verify=true&scope=user_follows_edit user_read channel_read chat%3Aread+chat%3Aedit+channel%3Amoderate+whispers%3Aread+whispers%3Aedit+channel_editor");
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
     * Prompts channel to confirm deletion of all files.
     * @author LethalMaus
     */
    protected void promptUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Authorization.this, R.style.CustomDialog);
        builder.setPositiveButton("OK", (DialogInterface dialog, int id) -> {
             new Thread() {
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

    /**
     * WebViewClient error handler to make the messages more channel friendly
     * @author LethalMaus
     * @param errorCode WebViewClient error code
     */
    private void showError(int errorCode) {
        String message = null;
        String title = null;
        if (errorCode == WebViewClient.ERROR_AUTHENTICATION) {
            message = "ChannelEntity authentication failed on server";
            title = "Auth Error";
        } else if (errorCode == WebViewClient.ERROR_TIMEOUT) {
            message = "The server is taking too much time to communicate. Try again later.";
            title = "Connection Timeout";
        } else if (errorCode == WebViewClient.ERROR_TOO_MANY_REQUESTS) {
            message = "Too many requests during this load";
            title = "Too Many Requests";
        } else if (errorCode == WebViewClient.ERROR_UNKNOWN) {
            message = "Generic error";
            title = "Unknown Error";
        } else if (errorCode == WebViewClient.ERROR_BAD_URL) {
            message = "Check entered URL..";
            title = "Malformed URL";
        } else if (errorCode == WebViewClient.ERROR_CONNECT) {
            message = "Failed to connect to the server";
            title = "Connection";
        } else if (errorCode == WebViewClient.ERROR_FAILED_SSL_HANDSHAKE) {
            message = "Failed to perform SSL handshake";
            title = "SSL Handshake Failed";
        } else if (errorCode == WebViewClient.ERROR_HOST_LOOKUP) {
            message = "Server or proxy hostname lookup failed";
            title = "Host Lookup Error";
        } else if (errorCode == WebViewClient.ERROR_PROXY_AUTHENTICATION) {
            message = "ChannelEntity authentication failed on proxy";
            title = "Proxy Auth Error";
        } else if (errorCode == WebViewClient.ERROR_REDIRECT_LOOP) {
            message = "Too many redirects";
            title = "Redirect Loop Error";
        } else if (errorCode == WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME) {
            message = "Unsupported authentication scheme (not basic or digest)";
            title = "Auth Scheme Error";
        } else if (errorCode == WebViewClient.ERROR_UNSUPPORTED_SCHEME) {
            message = "Unsupported URI scheme";
            title = "URI Scheme Error";
        } else if (errorCode == WebViewClient.ERROR_FILE) {
            message = "Generic file error";
            title = "File";
        } else if (errorCode == WebViewClient.ERROR_FILE_NOT_FOUND) {
            message = "File not found";
            title = "File";
        } else if (errorCode == WebViewClient.ERROR_IO) {
            message = "The server failed to communicate. Try again later.";
            title = "IO Error";
        }

        setContentView(R.layout.error);
        if (message != null) {
            TextView errorTitle = findViewById(R.id.error_title);
            errorTitle.setText(title);
            TextView errorMessage = findViewById(R.id.error_message);
            errorMessage.setText(message);
        }
    }
}

