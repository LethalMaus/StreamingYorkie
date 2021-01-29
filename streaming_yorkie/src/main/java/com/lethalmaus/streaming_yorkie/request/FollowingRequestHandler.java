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
                        FollowingEntity existingFollowingEntity = streamingYorkieDB.followingDAO().getUserById(Integer.parseInt(response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("_id")));
                        if (existingFollowingEntity != null) {
                            existingFollowingEntity.setLogo(response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("logo").replace("300x300", "50x50"));
                            existingFollowingEntity.setNotifications(response.getJSONArray("follows").getJSONObject(i).getBoolean("notifications"));
                            streamingYorkieDB.followingDAO().updateUser(existingFollowingEntity);
                        }
                    }
                    if (response.getJSONArray("follows").length() == Globals.USER_REQUEST_LIMIT && itemCount < twitchTotal) {
                        sendRequest(true);
                    } else {
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
