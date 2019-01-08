package com.lethalmaus.twitchfollowerservice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.format.Time;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;

@SuppressLint("SetJavaScriptEnabled")
public class Authorization extends UserService {

    private ConnectionTimeoutHandler timeoutHandler = null;
    protected static int PAGE_LOAD_PROGRESS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globals = new Globals(Authorization.this, getApplicationContext());
        if (new File(getFilesDir() + File.separator + "TOKEN").exists()) {
            setContentView(R.layout.logout);
            if(getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Logout");
            }
            globals.showUser();
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
            if (globals.isNetworkAvailable((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))) {
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
                        timeoutHandler = new ConnectionTimeoutHandler(view);
                        timeoutHandler.execute();
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        if (timeoutHandler != null) {
                            timeoutHandler.cancel(true);
                        }
                        if (url.contains("localhost") && url.contains("access_token") && !url.contains("twitch.tv")) {
                            globals.token = url.substring(url.indexOf("access_token") + 13, url.indexOf("access_token") + 43);
                            globals.deleteFileOrPath("");
                            globals.writeToFile("TOKEN", globals.token);
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            getUser();
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
                webView.loadUrl("https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=" + globals.CLIENTID + "&redirect_uri=http://localhost&force_verify=true&scope=user_follows_edit user_read channel_read");
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

    protected void promptUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Authorization.this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                globals.removeFiles();
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

    @Override
    protected void getUserResponseHandler(JSONObject response) {
        setUser(response);
        saveUser();
        globals.initialSetup();
        finish();
    }

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
}
