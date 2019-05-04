package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.FollowFileHandler;
import com.lethalmaus.streaming_yorkie.file.OrganizeFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Class to handle files specific to VODs.
 * Can be used as a runnable
 * @author LethalMaus
 */
public class FollowingRequestHandler extends RequestHandler {

    /**
     * Constructor for FollowingRequestHandler
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     * @param displayUsers constant of users to be displayed
     * @param commonFolders boolean as to whether its F4F or not
     */
    public FollowingRequestHandler(final WeakReference<Activity> weakActivity, final WeakReference<Context> weakContext, final WeakReference<RecyclerView> recyclerView, boolean displayUsers, final boolean commonFolders) {
        super(weakActivity, weakContext, recyclerView);
        this.displayRequest = displayUsers;
        this.commonFolders = commonFolders;

        this.currentUsersPath = Globals.FOLLOWING_CURRENT_PATH;
        this.newUsersPath = Globals.FOLLOWING_NEW_PATH;
        this.unfollowedUsersPath = Globals.FOLLOWING_UNFOLLOWED_PATH;
        this.excludedUsersPath = Globals.FOLLOWING_EXCLUDED_PATH;
        this.requestPath = Globals.FOLLOWING_REQUEST_PATH;
        this.usersPath = Globals.FOLLOWING_PATH;

        followFileHandler = new FollowFileHandler(weakContext, usersPath, requestPath, null) {
            @Override
            public void organizeFollowerFiles() {
                responseAction();
            }
        };
    }

    @Override
    public void sendRequest(int offset) {
        this.offset = offset;
        if (networkIsAvailable()) {
            new WriteFileHandler(weakContext, Globals.FLAG_PATH + File.separator + Globals.FOLLOW_REQUEST_RUNNING_FLAG, null, null, false).run();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/kraken/users/" + userID + "/follows/channels" + "?limit=" + Globals.USER_REQUEST_LIMIT + "&direction=asc&offset=" + this.offset, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            responseHandler(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorHandler(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    return getRequestHeaders();
                }
            };
            jsObjRequest.setTag("FOLLOWING");
            VolleySingleton.getInstance(weakContext).addToRequestQueue(jsObjRequest);
        } else {
            offlineResponseHandler();
        }
    }

    @Override
    public void responseHandler(JSONObject response) {
        try {
            followFileHandler.setResponse(response);
            offset += Globals.USER_REQUEST_LIMIT;
            if (twitchTotal == 0) {
                twitchTotal = response.getInt("_total");
            }
            itemCount += response.getJSONArray("follows").length();
            if (response.getJSONArray("follows").length() > 0) {
                followFileHandler.setOrganize(false);
                followFileHandler.run();
                sendRequest(offset);
            } else {
                followFileHandler.setOrganize(true);
                if (twitchTotal != itemCount && weakContext != null && weakContext.get() != null) {
                    Toast.makeText(weakContext.get(), "Twitch Data for 'Followers' is out of sync. Total should be '" + twitchTotal
                            + "' but is only giving '" + itemCount + "'", Toast.LENGTH_SHORT).show();
                }
                followFileHandler.run();
                //responseAction();
            }
        } catch (JSONException e) {
            if (weakContext != null && weakContext.get() != null) {
                Toast.makeText(weakContext.get(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show();
            }
            new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
        }
    }

    /**
     * Method for performing an action after handling the request response. Separated to be overridden when needed
     * @author LethalMaus
     */
    private void responseAction() {
        new DeleteFileHandler(weakContext, null).deleteFileOrPath(Globals.FLAG_PATH + File.separator + Globals.FOLLOW_REQUEST_RUNNING_FLAG);
        new OrganizeFileHandler(weakActivity, weakContext, recyclerView, displayRequest, commonFolders)
                .setPaths(Globals.FOLLOWING_CURRENT_PATH, Globals.FOLLOWING_NEW_PATH, Globals.FOLLOWING_UNFOLLOWED_PATH, Globals.FOLLOWING_EXCLUDED_PATH, Globals.FOLLOWING_REQUEST_PATH, Globals.FOLLOWING_PATH)
                .setDisplayPreferences(itemsToDisplay, actionButtonType1, actionButtonType2, actionButtonType3)
                .execute();
    }
}
