package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Class for requesting Followers to check if an Update is needed
 * @author LethalMaus
 */
public class FollowersUpdateRequestHandler extends RequestHandler {

    @Override
    public String url() {
        return "https://api.twitch.tv/kraken/channels/" + userID + "/follows?limit=" + Globals.USER_UPDATE_REQUEST_LIMIT + "&direction=desc";
    }

    @Override
    public int method() {
        return Request.Method.GET;
    }

    /**
     * Constructor for FollowersUpdateRequestHandler
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    public FollowersUpdateRequestHandler(final WeakReference<Activity> weakActivity, final WeakReference<Context> weakContext, final WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
        this.requestType = "FOLLOWERS_UPDATE";
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    long lastUpdated = 0;
                    String lastUpdatedString = new ReadFileHandler(weakActivity, weakContext, "FOLLOWERS_TIMESTAMP").readFile();
                    if (!lastUpdatedString.isEmpty()) {
                        lastUpdated = Long.parseLong(lastUpdatedString);
                    }
                    int[] lastFollowers = streamingYorkieDB.followerDAO().getLastUsers(lastUpdated);
                    String totalCountFile = new ReadFileHandler(weakActivity, weakContext, "TWITCH_FOLLOWERS_TOTAL_COUNT").readFile();
                    int twitchFollowerTotalCount = NumberUtils.isParsable(totalCountFile) ? Integer.parseInt(totalCountFile) : 0;
                    if (lastFollowers.length == response.getJSONArray("follows").length() && response.getInt("_total") == twitchFollowerTotalCount) {
                        for (int i = 0; i < lastFollowers.length; i++) {
                            if (lastFollowers[i] != Integer.parseInt(response.getJSONArray("follows").getJSONObject(i).getJSONObject("user").getString("_id"))) {
                                new HelixFollowersRequestHandler(weakActivity, weakContext, recyclerView){
                                    @Override
                                    public void onCompletion(boolean hideProgressBar) {
                                        super.onCompletion(false);
                                        FollowersUpdateRequestHandler.this.onCompletion(hideProgressBar);
                                    }
                                }.initiate().sendRequest(true);
                                return;
                            }
                        }
                        onCompletion(true);
                    } else {
                        new HelixFollowersRequestHandler(weakActivity, weakContext, recyclerView){
                            @Override
                            public void onCompletion(boolean hideProgressBar) {
                                super.onCompletion(false);
                                FollowersUpdateRequestHandler.this.onCompletion(hideProgressBar);
                            }
                        }.initiate().sendRequest(true);
                    }
                } catch (JSONException e) {
                    if (weakActivity != null && weakActivity.get() != null) {
                        weakActivity.get().runOnUiThread(() ->
                                Toast.makeText(weakActivity.get(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show()
                        );
                    }
                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Followers Update response error | " + e.toString(), true).run();
                }
            }
        }).start();
    }
}
