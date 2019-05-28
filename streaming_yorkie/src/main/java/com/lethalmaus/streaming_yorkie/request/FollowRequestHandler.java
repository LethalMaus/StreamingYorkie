package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.FollowFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Class for following/unfollowing users or changing notifications preferences
 * @author LethalMaus
 */
public class FollowRequestHandler extends RequestHandler {

    //Can be PUT or DELETE
    private int requestMethod;
    //ID of the user to be changed
    private String followingID;
    //Whether the user would like activate/deactivate notifications
    private Boolean notifications;
    //File handler for Following users
    private FollowFileHandler followFileHandler;

    /**
     * Constructor for FollowRequestHandler
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public FollowRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        super(weakActivity, weakContext, null);
        followFileHandler = new FollowFileHandler(weakContext, Globals.FOLLOWING_PATH, Globals.FOLLOWING_CURRENT_PATH, null);
    }

    /**
     * Sets request parameters
     * @author LethalMaus
     * @param requestMethod either PUT or DELETE
     * @param followingID ID of user to be changed
     * @param notifications bool for activating/deactivating notifications
     * @return instance of itself for method building
     */
    public FollowRequestHandler setRequestParameters(int requestMethod, String followingID, Boolean notifications) {
        this.requestMethod = requestMethod;
        this.followingID = followingID;
        this.notifications = notifications;
        return this;
    }

    /**
     * Sends a follow request based on the setRequestParameters
     * @author LethalMaus
     */
    public void requestFollow() {
        if (networkIsAvailable()) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(requestMethod, "https://api.twitch.tv/kraken/users/" + userID + "/follows/channels/" + followingID + "?notifications=" + notifications, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            responseHandler(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (weakActivity != null && weakActivity.get() != null) {
                        Toast.makeText(weakActivity.get(), "Error changing Following preference", Toast.LENGTH_SHORT).show();
                    }
                    new WriteFileHandler(weakContext, "ERROR", null, "Error changing Following preference | " + error.toString(), true).run();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    return getRequestHeaders();
                }

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    if (response.data != null && response.data.length > 0 && response.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
                        try {
                            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                            return Response.success(
                                    new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
                        } catch (UnsupportedEncodingException e) {
                            return Response.error(new ParseError(e));
                        } catch (JSONException je) {
                            return Response.error(new ParseError(je));
                        }
                    } else {
                        return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                    }
                }
            };
            jsObjRequest.setTag("FOLLOW");
            VolleySingleton.getInstance(weakContext).addToRequestQueue(jsObjRequest);
        } else if (weakActivity != null && weakActivity.get() != null) {
            Toast.makeText(weakActivity.get(), "Cannot change Following preferences when offline", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void responseHandler(JSONObject response) {
        if (response != null && !response.toString().equals("") && requestMethod != Request.Method.DELETE) {
            followFileHandler.setResponse(response);
            followFileHandler.run();
            //Check if it is present by F4F
            if (weakContext != null && weakContext.get() != null && new File(weakContext.get().getFilesDir().toString() + File.separator + Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH + File.separator + followingID).exists()) {
                new DeleteFileHandler(weakContext, Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH + File.separator + followingID).run();
                new WriteFileHandler(weakContext, Globals.F4F_FOLLOW4FOLLOW_PATH + File.separator + followingID, null, null, false).run();
            }
        } else {
            new WriteFileHandler(weakContext, Globals.FOLLOWING_UNFOLLOWED_PATH + File.separator + followingID, null, null, false).run();
            new DeleteFileHandler(weakContext, Globals.FOLLOWING_CURRENT_PATH + File.separator + followingID).run();
            if (weakContext != null && weakContext.get() != null && new File(weakContext.get().getFilesDir().toString() + File.separator + Globals.FOLLOWING_NEW_PATH + File.separator + followingID).exists()) {
                new DeleteFileHandler(weakContext, Globals.FOLLOWING_NEW_PATH + File.separator + followingID).run();
            }
            //Check if it is present by F4F
            if (weakContext != null && weakContext.get() != null && new File(weakContext.get().getFilesDir().toString() + File.separator + Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH + File.separator + followingID).exists()) {
                new DeleteFileHandler(weakContext, Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH + File.separator + followingID).run();
            }
            if (weakContext != null && weakContext.get() != null && new File(weakContext.get().getFilesDir().toString() + File.separator + Globals.F4F_FOLLOW4FOLLOW_PATH + File.separator + followingID).exists()) {
                new DeleteFileHandler(weakContext, Globals.F4F_FOLLOW4FOLLOW_PATH + File.separator + followingID).run();
                new WriteFileHandler(weakContext, Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH + File.separator + followingID, null, null, false).run();
            }
        }
    }
}
