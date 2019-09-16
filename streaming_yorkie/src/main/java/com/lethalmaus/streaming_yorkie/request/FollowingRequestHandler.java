package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.adapter.UserAdapter;
import com.lethalmaus.streaming_yorkie.entity.Following;

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
        return "https://api.twitch.tv/kraken/users/" + userID + "/follows/channels?limit=" + Globals.USER_REQUEST_LIMIT + "&direction=asc&offset=" + this.offset;
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
        new Thread(new Runnable() {
            public void run() {
                try {
                    offset += Globals.USER_REQUEST_LIMIT;
                    if (twitchTotal == 0) {
                        twitchTotal = response.getInt("_total");
                    }
                    itemCount += response.getJSONArray("follows").length();
                    if (response.getJSONArray("follows").length() > 0) {
                        for (int i = 0; i < response.getJSONArray("follows").length(); i++) {
                            Following following = new Following(Integer.parseInt(response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("_id")),
                                    response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("display_name"),
                                    response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("logo").replace("300x300", "50x50"),
                                    response.getJSONArray("follows").getJSONObject(i).getString("created_at"),
                                    response.getJSONArray("follows").getJSONObject(i).getBoolean("notifications"),
                                    timestamp);
                            Following existingFollowing = streamingYorkieDB.followingDAO().getUserById(following.getId());
                            if (existingFollowing != null) {
                                if (existingFollowing.getStatus().contentEquals("EXCLUDED")) {
                                    following.setStatus("EXCLUDED");
                                } else {
                                    following.setStatus("CURRENT");
                                }
                                streamingYorkieDB.followingDAO().updateUser(following);
                            } else {
                                following.setStatus("NEW");
                                streamingYorkieDB.followingDAO().insertUser(following);
                            }
                        }
                        sendRequest();
                    } else {
                        if (twitchTotal != itemCount && weakActivity != null && weakActivity.get() != null) {
                            weakActivity.get().runOnUiThread(
                                    new Runnable() {
                                        public void run() {
                                            Toast.makeText(weakActivity.get(), "Twitch is slow. Its data for 'Following' is out of sync. Total should be '" + twitchTotal
                                                    + "' but is only giving '" + itemCount + "'", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                        }
                        new WriteFileHandler(weakContext, "TWITCH_FOLLOWING_TOTAL_COUNT", null, String.valueOf(twitchTotal), false).run();
                        List<Following> unfollowing = streamingYorkieDB.followingDAO().getUnfollowedUsers(timestamp);
                        for (int i = 0; i < unfollowing.size(); i++) {
                            unfollowing.get(i).setStatus("UNFOLLOWED");
                            streamingYorkieDB.followingDAO().updateUser(unfollowing.get(i));
                        }
                        if (recyclerView != null && recyclerView.get() != null && recyclerView.get().getAdapter() != null) {
                            final UserAdapter userAdapter = (UserAdapter) recyclerView.get().getAdapter();
                            if (userAdapter != null && weakActivity != null && weakActivity.get() != null) {
                                userAdapter.setPageCounts();
                                weakActivity.get().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        userAdapter.datasetChanged();
                                    }
                                });
                            }
                        }
                        if (weakActivity != null && weakActivity.get() != null) {
                            weakActivity.get().runOnUiThread(new Runnable() {
                                public void run() {
                                    weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                        onCompletion();
                    }
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
                    new WriteFileHandler(weakContext, "ERROR", null, "Following response error | " + e.toString(), true).run();
                }
            }
            }).start();
    }
}
