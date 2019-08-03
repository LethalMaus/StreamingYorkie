package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lethalmaus.streaming_yorkie.file.UserFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.view.UserView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Class for requesting current user info
 * @author LethalMaus
 */
public class UserRequestHandler extends RequestHandler {

    private UserFileHandler userFileHandler;
    //Whether a request should be sent or just the file to be accessed
    private boolean requestUpdate;

    /**
     * Constructor for UserRequestHandler for requesting current user info
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param displayUser boolean whether channel is to be displayed
     * @param requestUpdate boolean whether a request should be sent or just the file to be accessed
     */
    public UserRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, boolean displayUser, boolean requestUpdate) {
        super(weakActivity, weakContext, null);
        userFileHandler = new UserFileHandler(weakContext, requestUpdate);
        this.displayRequest = displayUser;
        this.requestUpdate = requestUpdate;
    }

    @Override
    public void sendRequest(int offset) {
        if (networkIsAvailable() && requestUpdate) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/kraken/user", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            responseHandler(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (weakActivity != null && weakActivity.get() != null) {
                        Toast.makeText(weakActivity.get(), "Error requesting User", Toast.LENGTH_SHORT).show();
                    }
                    String errorMessage = error.toString();
                    if (error.networkResponse != null) {
                        errorMessage = error.networkResponse.statusCode + " | " + new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    }
                    new WriteFileHandler(weakContext, "ERROR", null, "Error requesting User | " + errorMessage, true).run();
                    offlineResponseHandler();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    return getRequestHeaders();
                }
            };
            jsObjRequest.setTag("USER");
            VolleySingleton.getInstance(weakContext).addToRequestQueue(jsObjRequest);
        } else {
            offlineResponseHandler();
        }
    }

    @Override
    public void responseHandler(JSONObject response) {
        userFileHandler.setResponse(response);
        userFileHandler.writeUser();
        new UserView(weakActivity, weakContext, displayRequest).execute();
    }

    @Override
    protected void offlineResponseHandler() {
        if (weakActivity != null && weakActivity.get() != null && requestUpdate) {
            Toast.makeText(weakActivity.get(), "OFFLINE: Showing saved User Info", Toast.LENGTH_SHORT).show();
        }
        new UserView(weakActivity, weakContext, displayRequest).execute();
    }
}
