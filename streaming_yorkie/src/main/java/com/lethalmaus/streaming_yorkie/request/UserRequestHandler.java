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
import java.util.Map;

/**
 * Class for requesting current user info
 * @author LethalMaus
 */
public class UserRequestHandler extends RequestHandler {

    private UserFileHandler userFileHandler;
    //Whether the user should be displayed or just updated
    private boolean displayUser;
    //Whether all the info or just the name & logo
    private boolean showAllInfo;
    //Whether a request should be sent or just the file to be accessed
    private boolean requestUpdate;

    /**
     * Constructor for UserRequestHandler for requesting current user info
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param displayUser boolean whether user is to be displayed
     * @param showAllInfo boolean whether to show all the info or just the name & logo
     * @param requestUpdate boolean whether a request should be sent or just the file to be accessed
     */
    public UserRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, boolean displayUser, boolean showAllInfo, boolean requestUpdate) {
        super(weakActivity, weakContext, null);
        userFileHandler = new UserFileHandler(weakContext);
        this.displayUser = displayUser;
        this.showAllInfo = showAllInfo;
        this.requestUpdate = requestUpdate;
    }

    @Override
    public void sendRequest(int offset) {
        if (networkIsAvailable() && requestUpdate) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/kraken/channel", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            responseHandler(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
                        Toast.makeText(weakActivity.get(), "Error requesting User", Toast.LENGTH_SHORT).show();
                    }
                    new WriteFileHandler(weakContext, "ERROR", null, error.toString() + "\n", true).run();
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
        new UserView(weakActivity, weakContext, displayUser, showAllInfo).execute();
    }

    @Override
    protected void offlineResponseHandler() {
        if (weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing() && requestUpdate) {
            Toast.makeText(weakActivity.get(), "OFFLINE: Showing saved User", Toast.LENGTH_SHORT).show();
        }
        new UserView(weakActivity, weakContext, displayUser, showAllInfo).execute();
    }
}
