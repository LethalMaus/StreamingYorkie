package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.adapter.UserAdapter;
import com.lethalmaus.streaming_yorkie.entity.FollowingEntity;

import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Class to handle files specific to VODs.
 * Can be used as a runnable
 * @author LethalMaus
 */
public class FollowingRequestHandler extends RequestHandler {

    @Override
    public String url() {
        /*FIXME
           'sortby=login' was added due to bug on twitch side, this will affect the update request handler and should be removed when resolved
            https://github.com/twitchdev/issues/issues/237
        */
        return "https://api.twitch.tv/kraken/users/" + userID + "/follows/channels?limit=" + Globals.USER_REQUEST_LIMIT + "&direction=desc&sortby=login&offset=" + this.offset;
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
    FollowingRequestHandler(final WeakReference<Activity> weakActivity, final WeakReference<Context> weakContext, final WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
        this.requestType = "FOLLOWING";
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread() {
            @Override
            public void run() {
                try {
                    offset += Globals.USER_REQUEST_LIMIT;
                    if (twitchTotal == 0) {
                        twitchTotal = response.getInt("_total");
                    }
                    itemCount += response.getJSONArray("follows").length();
                    for (int i = 0; i < response.getJSONArray("follows").length(); i++) {
                        FollowingEntity followingEntity = new FollowingEntity(Integer.parseInt(response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("_id")),
                                response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("display_name"),
                                response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("logo").replace("300x300", "50x50"),
                                response.getJSONArray("follows").getJSONObject(i).getString("created_at"),
                                response.getJSONArray("follows").getJSONObject(i).getBoolean("notifications"),
                                timestamp);
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
                    if (response.getJSONArray("follows").length() == Globals.USER_REQUEST_LIMIT && itemCount < twitchTotal) {
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
                        //TODO this can be added to onCompletion by extending a base adapter

                        if (Globals.checkWeakActivity(weakActivity) && Globals.checkWeakRecyclerView(recyclerView)) {
                            final UserAdapter userAdapter = (UserAdapter) recyclerView.get().getAdapter();
                            if (userAdapter != null) {
                                weakActivity.get().runOnUiThread(() -> {
                                    recyclerView.get().stopScroll();
                                    recyclerView.get().scrollToPosition(0);
                                    recyclerView.get().getRecycledViewPool().clear();
                                    recyclerView.get().post(userAdapter::datasetChanged);
                                });
                            }
                        }
                        onCompletion(true);
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
}