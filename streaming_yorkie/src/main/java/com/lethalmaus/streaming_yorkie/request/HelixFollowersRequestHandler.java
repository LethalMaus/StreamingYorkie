package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.entity.FollowerEntity;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

/**
 * Class for requesting Followers
 * @author LethalMaus
 */
public class HelixFollowersRequestHandler extends RequestHandler {

    private String cursor = "";

    @Override
    public String url() {
        return "https://api.twitch.tv/helix/users/follows?to_id=" + userID + "&first=" + Globals.USER_REQUEST_LIMIT  + "&after=" + cursor;
    }

    @Override
    public int method() {
        return Request.Method.GET;
    }

    /**
     * Constructor for FollowersRequestHandler
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    HelixFollowersRequestHandler(final WeakReference<Activity> weakActivity, final WeakReference<Context> weakContext, final WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
        this.requestType = "FOLLOWERS";
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread() {
            public void run() {
                try {
                    if (response.getJSONObject("pagination").has("cursor")) {
                        cursor = response.getJSONObject("pagination").getString("cursor");
                    }
                    if (twitchTotal == 0) {
                        twitchTotal = response.getInt("total");
                    }
                    itemCount += response.getJSONArray("data").length();
                    for (int i = 0; i < response.getJSONArray("data").length(); i++) {
                        FollowerEntity followerEntity = new FollowerEntity(Integer.parseInt(response.getJSONArray("data").getJSONObject(i).getString("from_id")),
                                response.getJSONArray("data").getJSONObject(i).getString("from_name"),
                                "", //This is updated via the old request afterwards
                                response.getJSONArray("data").getJSONObject(i).getString("followed_at"),
                                false, //This is updated via the old request afterwards
                                timestamp);
                        FollowerEntity existingFollowerEntity = streamingYorkieDB.followerDAO().getUserById(followerEntity.getId());
                        if (existingFollowerEntity != null) {
                            if (existingFollowerEntity.getStatus() != null && existingFollowerEntity.getStatus().contentEquals("EXCLUDED")) {
                                followerEntity.setStatus("EXCLUDED");
                            } else {
                                followerEntity.setStatus("CURRENT");
                            }
                            streamingYorkieDB.followerDAO().updateUser(followerEntity);
                        } else {
                            followerEntity.setStatus("NEW");
                            streamingYorkieDB.followerDAO().insertUser(followerEntity);
                        }
                    }
                    if (response.getJSONArray("data").length() == Globals.USER_REQUEST_LIMIT && itemCount < twitchTotal) {
                        sendRequest(true);
                    } else {
                        if (twitchTotal != itemCount && Globals.checkWeakActivity(weakActivity)) {
                            weakActivity.get().runOnUiThread(() ->
                                    Toast.makeText(weakActivity.get(), "Twitch is slow. Its data for 'Followers' is out of sync. Total should be '" + twitchTotal
                                            + "' but is only giving '" + itemCount + "'", Toast.LENGTH_SHORT).show()
                            );
                        }
                        new WriteFileHandler(weakActivity, weakContext, "TWITCH_FOLLOWERS_TOTAL_COUNT", null, String.valueOf(twitchTotal), false).run();
                        List<FollowerEntity> unfollowed = streamingYorkieDB.followerDAO().getUnfollowedUsers(timestamp);
                        for (int i = 0; i < unfollowed.size(); i++) {
                            unfollowed.get(i).setStatus("UNFOLLOWED");
                            streamingYorkieDB.followerDAO().updateUser(unfollowed.get(i));
                        }
                        new FollowersRequestHandler(weakActivity, weakContext, recyclerView){
                            @Override
                            public void onCompletion(boolean hideProgressBar) {
                                super.onCompletion(false);
                                HelixFollowersRequestHandler.this.onCompletion(hideProgressBar);
                            }
                        }.initiate().sendRequest(true);
                    }
                } catch (JSONException e) {
                    if (Globals.checkWeakActivity(weakActivity)) {
                        weakActivity.get().runOnUiThread(() ->
                                Toast.makeText(weakActivity.get(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show()
                        );
                    }
                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Followers response error | " + e.toString(), true).run();
                }
            }
        }.start();
    }

    @Override
    HashMap<String, String> getRequestHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/vnd.twitchtv.v5+json");
        headers.put("Client-ID", Globals.CLIENTID);
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }
}