package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.adapter.UserAdapter;
import com.lethalmaus.streaming_yorkie.entity.FollowerEntity;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Class for requesting Followers
 * @author LethalMaus
 */
public class FollowersRequestHandler extends RequestHandler {

    private String cursor = "";

    @Override
    public String url() {
        return "https://api.twitch.tv/kraken/channels/" + userID + "/follows?limit=" + Globals.USER_REQUEST_LIMIT + "&direction=desc&cursor=" + cursor;
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
    FollowersRequestHandler(final WeakReference<Activity> weakActivity, final WeakReference<Context> weakContext, final WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
        this.requestType = "FOLLOWERS";
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread() {
            public void run() {
                try {
                    if (response.has("_cursor")) {
                        cursor = response.getString("_cursor");
                        //This fixes the Twitch cursor bug
                        JSONObject cursorObject = new JSONObject(new String(Base64.decode(cursor, Base64.DEFAULT)));
                        cursor = cursorObject.getString("is");
                    }
                    if (twitchTotal == 0) {
                        twitchTotal = response.getInt("_total");
                    }
                    itemCount += response.getJSONArray("follows").length();
                    for (int i = 0; i < response.getJSONArray("follows").length(); i++) {
                        FollowerEntity existingFollowerEntity = streamingYorkieDB.followerDAO().getUserById(Integer.parseInt(response.getJSONArray("follows").getJSONObject(i).getJSONObject("user").getString("_id")));
                        if (existingFollowerEntity != null) {
                            existingFollowerEntity.setLogo(response.getJSONArray("follows").getJSONObject(i).getJSONObject("user").getString("logo").replace("300x300", "50x50"));
                            existingFollowerEntity.setNotifications(response.getJSONArray("follows").getJSONObject(i).getBoolean("notifications"));
                            streamingYorkieDB.followerDAO().updateUser(existingFollowerEntity);
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
                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Followers response error | " + e.toString(), true).run();
                }
            }
        }.start();
    }
}