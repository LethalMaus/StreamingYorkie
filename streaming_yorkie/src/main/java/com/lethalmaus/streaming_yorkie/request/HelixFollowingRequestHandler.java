package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.adapter.UserAdapter;
import com.lethalmaus.streaming_yorkie.entity.FollowerEntity;
import com.lethalmaus.streaming_yorkie.entity.FollowingEntity;

import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

/**
 * Class to handle files specific to VODs.
 * Can be used as a runnable
 * @author LethalMaus
 */
public class HelixFollowingRequestHandler extends RequestHandler {

    private String cursor = "";

    @Override
    public String url() {
        return "https://api.twitch.tv/helix/users/follows?from_id=" + userID + "&after=" + cursor;
    }

    @Override
    public int method() {
        return Request.Method.GET;
    }

    /**
     * Constructor for FollowingRequestHandler
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    HelixFollowingRequestHandler(final WeakReference<Activity> weakActivity, final WeakReference<Context> weakContext, final WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
        this.requestType = "FOLLOWING";
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread() {
            @Override
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
                        FollowingEntity followingEntity = new FollowingEntity(Integer.parseInt(response.getJSONArray("data").getJSONObject(i).getString("to_id")),
                                response.getJSONArray("data").getJSONObject(i).getString("to_name"),
                                "", //This is updated via the old request afterwards
                                response.getJSONArray("data").getJSONObject(i).getString("followed_at"),
                                false, //This is updated via the old request afterwards
                                timestamp,
                                0);
                        FollowingEntity existingFollowingEntity = streamingYorkieDB.followingDAO().getUserById(followingEntity.getId());
                        if (existingFollowingEntity != null) {
                            if (existingFollowingEntity.getStatus() != null
                                    && existingFollowingEntity.getStatus().contentEquals("EXCLUDED")) {
                                followingEntity.setStatus("EXCLUDED");
                            } else {
                                followingEntity.setStatus("CURRENT");
                            }
                            streamingYorkieDB.followingDAO().updateUser(followingEntity);
                        } else {
                            followingEntity.setStatus("NEW");
                            streamingYorkieDB.followingDAO().insertUser(followingEntity);
                        }
                    }
                    if (response.getJSONArray("data").length() == 20 && itemCount < twitchTotal) {
                        sendRequest(true);
                    } else {
                        if (twitchTotal != itemCount && weakActivity != null && weakActivity.get() != null) {
                            weakActivity.get().runOnUiThread(() ->
                                    Toast.makeText(weakActivity.get(), "Twitch is slow. Its data for 'Following' is out of sync. Total should be '" + twitchTotal
                                            + "' but is only giving '" + itemCount + "'", Toast.LENGTH_SHORT).show()
                            );
                        }
                        new WriteFileHandler(weakActivity, weakContext, "TWITCH_FOLLOWING_TOTAL_COUNT", null, String.valueOf(twitchTotal), false).writeToFileOrPath();
                        List<FollowingEntity> unfollowing = streamingYorkieDB.followingDAO().getUnfollowedUsers(timestamp);
                        for (int i = 0; i < unfollowing.size(); i++) {
                            unfollowing.get(i).setStatus("UNFOLLOWED");
                            streamingYorkieDB.followingDAO().updateUser(unfollowing.get(i));
                        }
                        new FollowingRequestHandler(weakActivity, weakContext, recyclerView){
                            @Override
                            public void onCompletion(boolean hideProgressBar) {
                                super.onCompletion(false);
                                HelixFollowingRequestHandler.this.onCompletion(hideProgressBar);
                            }
                        }.initiate().sendRequest(true);
                    }
                } catch (JSONException e) {
                    if (Globals.checkWeakActivity(weakActivity)) {
                        weakActivity.get().runOnUiThread(() ->
                                Toast.makeText(weakActivity.get(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show()
                        );
                    }
                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Following response error | " + e.toString(), true).run();
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
