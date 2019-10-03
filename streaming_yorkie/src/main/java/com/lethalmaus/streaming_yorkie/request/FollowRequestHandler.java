package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.lethalmaus.streaming_yorkie.entity.FollowingEntity;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

/**
 * Class for following/unfollowing users or changing notifications preferences
 * @author LethalMaus
 */
public class FollowRequestHandler extends RequestHandler {

    @Override
    public String url() {
        return "https://api.twitch.tv/kraken/users/" + userID + "/follows/channels/" + followId + "?notifications=" + notifications;
    }

    @Override
    public int method() {
        return requestMethod;
    }

    //Can be PUT or DELETE
    private int requestMethod;
    //ID of the channel to be changed
    private int followId;
    //Whether the channel would like activate/deactivate notifications
    private Boolean notifications;

    /**
     * Constructor for FollowRequestHandler
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public FollowRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        super(weakActivity, weakContext, null);
        requestType = "FOLLOW";
    }

    /**
     * Sets request parameters
     * @author LethalMaus
     * @param requestMethod either PUT or DELETE
     * @param followId ID of channel to be changed
     * @param notifications bool for activating/deactivating notifications
     * @return instance of itself for method building
     */
    public FollowRequestHandler setRequestParameters(int requestMethod, int followId, Boolean notifications) {
        this.requestMethod = requestMethod;
        this.followId = followId;
        this.notifications = notifications;
        return this;
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (response != null && !response.toString().equals("") && requestMethod != Request.Method.DELETE) {
                        String timestamp = new ReadFileHandler(weakContext, "FOLLOWING_TIMESTAMP").readFile();
                        if (timestamp.isEmpty()) {
                            timestamp = "0";
                        }
                        FollowingEntity followingEntity = new FollowingEntity(Integer.parseInt(response.getJSONObject("channel").getString("_id")),
                                response.getJSONObject("channel").getString("display_name"),
                                response.getJSONObject("channel").getString("logo").replace("300x300", "50x50"),
                                response.getString("created_at"),
                                response.getBoolean("notifications"),
                                Long.parseLong(timestamp));
                        FollowingEntity existingFollowingEntity = streamingYorkieDB.followingDAO().getUserById(followingEntity.getId());
                        if (existingFollowingEntity != null) {
                            if (existingFollowingEntity.getStatus().contentEquals("EXCLUDED")) {
                                followingEntity.setStatus("EXCLUDED");
                            } else {
                                followingEntity.setStatus("CURRENT");
                            }
                            streamingYorkieDB.followingDAO().updateUser(followingEntity);
                        } else {
                            followingEntity.setStatus("NEW");
                            streamingYorkieDB.followingDAO().insertUser(followingEntity);
                        }
                    } else {
                        int followingCount = Integer.parseInt(new ReadFileHandler(weakContext, "TWITCH_FOLLOWING_TOTAL_COUNT").readFile());
                        new WriteFileHandler(weakContext, "TWITCH_FOLLOWING_TOTAL_COUNT", null, String.valueOf(followingCount-1), false).writeToFileOrPath();

                        FollowingEntity followingEntity = streamingYorkieDB.followingDAO().getUserById(followId);
                        if (!followingEntity.getStatus().contentEquals("EXCLUDED")) {
                            followingEntity.setStatus("UNFOLLOWED");
                        }
                        followingEntity.setLast_updated(System.currentTimeMillis());
                        streamingYorkieDB.followingDAO().updateUser(followingEntity);
                    }
                    onCompletion();
                } catch (JSONException e) {
                    if (weakActivity != null && weakActivity.get() != null) {
                        weakActivity.get().runOnUiThread(
                                new Runnable() {
                                    public void run() {
                                        Toast.makeText(weakActivity.get(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                    new WriteFileHandler(weakContext, "ERROR", null, "Follow response error | " + e.toString(), true).run();
                }
            }
        }).start();
    }

    @Override
    public Response<JSONObject> parseRequestNetworkResponse(NetworkResponse response, String PROTOCOL_CHARSET) {
        if (response.data != null && response.data.length > 0 && response.statusCode != HttpURLConnection.HTTP_NO_CONTENT) {
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

    @Override
    protected void offlineResponseHandler() {
        if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
            weakActivity.get().runOnUiThread(
                    new Runnable() {
                        public void run() {
                            Toast.makeText(weakActivity.get(), "Cannot change FollowingEntity preferences when offline", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }
}
