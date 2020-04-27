package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.ChannelEntity;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Parent class for all request handlers
 * @author LethalMaus
 */
public class RequestHandler {

    private String token;
    String userID;
    String requestType;
    int offset;
    int twitchTotal;
    int itemCount;
    long timestamp;
    StreamingYorkieDB streamingYorkieDB;
    private JSONObject postBody;

    //Weak references to avoid memory leaks
    WeakReference<Activity> weakActivity;
    WeakReference<Context> weakContext;
    WeakReference<RecyclerView> recyclerView;

    /**
     * Constructor for RequestHandler. Parent class for all request handlers
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    public RequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> recyclerView) {
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        this.recyclerView = recyclerView;
        streamingYorkieDB = StreamingYorkieDB.getInstance(weakContext.get());
    }

    /**
     * Builds Url dynamically for changing url parameters
     * Is overridden in sub classes
     * @author LethalMaus
     * @return dynamic Url
     */
    public String url() {
        return "";
    }

    /**
     * Returns the method for current request.
     * Is overridden in sub classes
     * @author LethalMaus
     * @return dynamic Url
     */
    public int method() {
        return 0;
    }

    /**
     * Sets the body for POST requests
     * @author LethalMaus
     * @param postBody JSONObject
     * @return instance of itself for method building
     */
    public RequestHandler setPostBody(JSONObject postBody) {
        this.postBody = postBody;
        return this;
    }

    /**
     * Sets the total & channel count to '0' for new requests as well as setting the request timestamp.
     * These variables are dynamic & reused during multiple request firing
     * @author LethalMaus
     * @return instance of itself for method building
     */
    public RequestHandler initiate() {
        twitchTotal = 0;
        itemCount = 0;
        offset = 0;
        timestamp = System.currentTimeMillis();
        return this;
    }

    /**
     * Method for sending a request.
     * Must have called initiate() at least once.
     * This method is overridden in every sub-class.
     * @author LethalMaus
     */
    public void sendRequest() {
        if (weakActivity != null && weakActivity.get() != null) {
            weakActivity.get().runOnUiThread(() -> {
                View progressbar = weakActivity.get().findViewById(R.id.progressbar);
                if (progressbar != null) {
                    progressbar.setVisibility(View.VISIBLE);
                }
            });
        }
        new Thread() {
            public void run() {
                if (weakContext != null && weakContext.get() != null) {
                    if (userID == null) {
                        if (new File(weakContext.get().getFilesDir().toString() + File.separator + "TOKEN").exists()) {
                            token = new ReadFileHandler(weakActivity, weakContext, "TOKEN").readFile();
                        }
                        ChannelEntity channelEntity = streamingYorkieDB.channelDAO().getChannel();
                        if (channelEntity != null) {
                            userID = Integer.toString(channelEntity.getId());
                        }
                    }
                    if (networkIsAvailable(weakContext)) {
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest(method(), url(), postBody,
                                RequestHandler.this::responseHandler,
                                RequestHandler.this::errorHandler
                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                return getRequestHeaders();
                            }
                            @Override
                            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                return parseRequestNetworkResponse(response, PROTOCOL_CHARSET);
                            }
                        };
                        jsObjRequest.setTag(requestType);
                        VolleySingleton.getInstance(weakContext).addToRequestQueue(jsObjRequest);
                    } else {
                        offlineResponseHandler();
                    }
                }
            }
        }.start();
    }

    /**
     * Method for handling a request response
     * This method is overridden in every sub-class
     * @author LethalMaus
     * @param response JSON response object
     */
    public void responseHandler(JSONObject response) {}

    /**
     * Method to display saved items when offline or when an error occurs
     * @author LethalMaus
     */
    protected void offlineResponseHandler() {
        if (recyclerView != null && recyclerView.get() != null && weakActivity.get() != null) {
            weakActivity.get().runOnUiThread(() ->
                    Toast.makeText(weakActivity.get(), "OFFLINE: Showing locally saved data", Toast.LENGTH_SHORT).show()
            );
        }
    }

    /**
     * Method for handling a request error response.
     * @author LethalMaus
     * @param error Volley Error
     */
    void errorHandler(VolleyError error) {
        if (twitchTotal != itemCount && weakActivity != null && weakActivity.get() != null) {
            weakActivity.get().runOnUiThread(() ->
                    Toast.makeText(weakActivity.get(), "Error requesting " + requestType, Toast.LENGTH_SHORT).show()
            );
        }
        String errorMessage = error.toString();
        if (error.networkResponse != null) {
            errorMessage = error.networkResponse.statusCode + " | " + new String(error.networkResponse.data, StandardCharsets.UTF_8);
        }
        new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error requesting " + requestType + ": " + errorMessage, true).run();
    }

    /**
     * Method for retrieving Request headers
     * @author LethalMaus
     * @return HashMap - String, String - of headers
     */
    HashMap<String, String> getRequestHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/vnd.twitchtv.v5+json");
        headers.put("Client-ID", Globals.CLIENTID);
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", "OAuth " + token);
        return headers;
    }

    /**
     * Method for overriding parseNetworkResponse
     * @author LethalMaus
     * @param response JSONObject
     * @param PROTOCOL_CHARSET HTTP Protocol Character set used
     * @return Response
     */
    public Response<JSONObject> parseRequestNetworkResponse(NetworkResponse response, String PROTOCOL_CHARSET) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException | UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * Cancels all requests sent of the same type and voids any methods that would be provoked afterwards
     * @author LethalMaus
     */
    public void cancelRequest() {
        VolleySingleton.getInstance(weakContext).getRequestQueue().cancelAll(requestType);
    }

    /**
     * Method to be called once all requests are complete
     * @author LethalMaus
     */
    public void onCompletion() {
        new WriteFileHandler(weakActivity, weakContext, requestType + "_TIMESTAMP", null, Long.toString(timestamp), false).run();
    }

    /**
     * Method for checking network status
     * @author LethalMaus
     * @param weakContext Weak reference context
     * @return boolean as to whether network is available
     */
    public static boolean networkIsAvailable(WeakReference<Context> weakContext) {
        NetworkInfo activeNetwork = null;
        if (weakContext != null && weakContext.get() != null) {
            ConnectivityManager systemService = (ConnectivityManager) weakContext.get().getSystemService(CONNECTIVITY_SERVICE);
            if (systemService != null) {
                activeNetwork = systemService.getActiveNetworkInfo();
            }
        }
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
