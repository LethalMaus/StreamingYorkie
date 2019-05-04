package com.lethalmaus.streaming_yorkie.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.RequestHandler;
import com.lethalmaus.streaming_yorkie.request.UserRequestHandler;
import com.lethalmaus.streaming_yorkie.view.UserView;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Activity for logging in & out of twitch & the app
 * @author LethalMaus
 */
//This is needed to log into Twitch, even though its not recommended & considered dangerous. Hence the Lint suppression
@SuppressLint("SetJavaScriptEnabled")
public class Authorization extends AppCompatActivity {

    /*FIXME connection timeout isn't working as it should. The page progress wasn't taken into consideration
    private ConnectionTimeoutHandler timeoutHandler = null;
    protected static int PAGE_LOAD_PROGRESS = 0;
    */

    //All activities & contexts are weak referenced to avoid memory leaks
    private WeakReference<Activity> weakActivity;
    private WeakReference<Context> weakContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weakActivity = new WeakReference<Activity>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        if (new File(this.getFilesDir() + File.separator + "TOKEN").exists()) {
            setContentView(R.layout.logout);
            if(getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Logout");
            }
            new UserView(weakActivity, weakContext, true, false).execute();
            ImageButton logout = findViewById(R.id.authorization_logout);
            logout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            promptUser();
                        }
                    });
        } else {
            setContentView(R.layout.authorization);
            if (new RequestHandler(weakActivity, weakContext, null).networkIsAvailable()) {
                WebView webView = findViewById(R.id.authWebView);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (url.contains("twitch.tv")) {
                            view.loadUrl(url);
                            return false;
                        }
                        return true;
                    }

                    @RequiresApi(21)
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        if (request.getUrl().toString().contains("twitch.tv")) {
                            view.loadUrl(request.getUrl().toString());
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        /*FIXME
                        timeoutHandler = new ConnectionTimeoutHandler(view);
                        timeoutHandler.execute();
                        */
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        /*FIXME
                        if (timeoutHandler != null) {
                            timeoutHandler.cancel(true);
                        }
                        */
                        if (url.contains("https://www.twitch.tv/passport-callback#access_token")) {
                            new WriteFileHandler(weakContext, "TWITCH_TOKEN", null, url.substring(url.indexOf("access_token") + 13, url.indexOf("access_token") + 43), false).writeToFileOrPath();
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (url.contains("localhost") && url.contains("access_token") && !url.contains("twitch.tv")) {
                            new WriteFileHandler(weakContext, "TOKEN", null, url.substring(url.indexOf("access_token") + 13, url.indexOf("access_token") + 43), false).writeToFileOrPath();

                            new UserRequestHandler(weakActivity, weakContext, false, false, true) {
                                @Override
                                public void responseHandler(JSONObject response) {
                                    super.responseHandler(response);
                                    createFolders();
                                }
                            }.sendRequest(0);
                            view.loadUrl("https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=" + Globals.TWITCHID + "&redirect_uri=https://www.twitch.tv/passport-callback&scope=chat_login user_read user_subscriptions user_presence_friends_read");
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
                webView.loadUrl("https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=" + Globals.CLIENTID + "&redirect_uri=http://localhost&force_verify=true&scope=user_follows_edit user_read channel_read");
            } else {
                setContentView(R.layout.error);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    /**
     * Prompts user to confirm deletion of all files.
     * @author LethalMaus
     */
    protected void promptUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Authorization.this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                new DeleteFileHandler(weakContext, getFilesDir().toString()).run();
                finish();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Do nothing
            }
        });
        builder.setMessage("All data will be deleted. Are you sure?");
        builder.setTitle("Logout");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Creates all necessary folders for future use
     * @author LethalMaus
     */
    private void createFolders() {
        String appDirectory = Authorization.this.getFilesDir().toString();
        if (
                !new File(appDirectory + File.separator + Globals.FOLLOWERS_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.FOLLOWERS_REQUEST_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.FOLLOWERS_NEW_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.FOLLOWERS_CURRENT_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.FOLLOWERS_UNFOLLOWED_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.FOLLOWERS_EXCLUDED_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.FOLLOWING_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.FOLLOWING_REQUEST_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.FOLLOWING_NEW_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.FOLLOWING_CURRENT_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.FOLLOWING_UNFOLLOWED_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.FOLLOWING_EXCLUDED_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.F4F_FOLLOW4FOLLOW_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH).mkdir() ||
                        !new File(appDirectory + File.separator +  Globals.F4F_EXCLUDED_PATH).mkdir()
                ) {
            new WriteFileHandler(weakContext, "ERROR", null, "Cannot create initial folder structure" + "\n", true).run();
        }
    }

    /**
     * WebViewClient error handler to make the messages more user friendly
     * @author LethalMaus
     * @param errorCode WebViewClient error code
     */
    private void showError(int errorCode) {
        String message = null;
        String title = null;
        if (errorCode == WebViewClient.ERROR_AUTHENTICATION) {
            message = "User authentication failed on server";
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
            message = "User authentication failed on proxy";
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

    /*FIXME connection timeout isn't working as it should. The page progress wasn't taken into consideration
    public class ConnectionTimeoutHandler extends AsyncTask<Void, Void, String> {

        private static final String PAGE_LOADED = "PAGE_LOADED";
        private static final String CONNECTION_TIMEOUT = "CONNECTION_TIMEOUT";
        private static final long CONNECTION_TIMEOUT_UNIT = 20000L;

        private WebView webView;
        private Time startTime = new Time();
        private Time currentTime = new Time();
        private Boolean loaded = false;

        public ConnectionTimeoutHandler(WebView webView) {
            this.webView = webView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.startTime.setToNow();
            PAGE_LOAD_PROGRESS = 0;
        }

        @Override
        protected void onPostExecute(String result) {
            if (CONNECTION_TIMEOUT.equalsIgnoreCase(result)) {
                showError(WebViewClient.ERROR_TIMEOUT);
                this.webView.stopLoading();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            while (! loaded) {
                currentTime.setToNow();
                if (PAGE_LOAD_PROGRESS != 100
                        && (currentTime.toMillis(true) - startTime.toMillis(true)) > CONNECTION_TIMEOUT_UNIT) {
                    return CONNECTION_TIMEOUT;
                } else if (PAGE_LOAD_PROGRESS == 100) {
                    loaded = true;
                }
            }
            return PAGE_LOADED;
        }
    }
    */
}

