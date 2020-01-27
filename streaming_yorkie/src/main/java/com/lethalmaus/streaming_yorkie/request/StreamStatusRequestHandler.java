package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.lethalmaus.streaming_yorkie.entity.FollowingEntity;
import com.lethalmaus.streaming_yorkie.entity.LurkEntity;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Class for checking if channels are streaming
 * @author LethalMaus
 */
public class StreamStatusRequestHandler extends RequestHandler {

    private List<String> userIds;
    private StringBuilder userIdsFormatted;
    private int usersToBeLurked = 0;

    @Override
    public String url() {
        return "https://api.twitch.tv/helix/streams?" + userIdsFormatted.toString();
    }

    @Override
    public int method() {
        return Request.Method.GET;
    }

    /**
     * Constructor for StreamStatusRequestHandler for checking if channels are streaming
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    protected StreamStatusRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
        requestType = "STREAM_STATUS";
    }

    /**
     * Sets the userIds for new requests.
     * @author LethalMaus
     * @param userIds String user Ids
     * @return instance of itself for method building
     */
    public StreamStatusRequestHandler newRequest(List<String> userIds) {
        this.userIds = userIds;
        userIdsFormatted = new StringBuilder();
        for (int i = 0; i < userIds.size(); i++) {
            userIdsFormatted.append("user_id=");
            userIdsFormatted.append(userIds.get(i));
            userIdsFormatted.append("&");
        }
        return this;
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread() {
            public void run() {
                try {
                    usersToBeLurked = 0;
                    for (int i = 0; i < userIds.size(); i++) {
                        String user = response.getJSONArray("data").getJSONObject(i).getString("user_name");
                        if (userIds.contains(user)) {
                            usersToBeLurked++;
                            new LurkRequestHandler(weakActivity, weakContext, recyclerView) {
                                @Override
                                public void onCompletion() {
                                    try {
                                        if (usersToBeLurked == response.getJSONArray("data").length()) {
                                            StreamStatusRequestHandler.this.onCompletion();
                                        }
                                    } catch (JSONException e) {
                                        new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error getting stream status | " + e.toString(), true).run();
                                    }
                                }
                            }.newRequest(user.toLowerCase()).initiate().sendRequest();
                        } else {
                            LurkEntity lurk = streamingYorkieDB.lurkDAO().getLurkByChannelName(user);
                            if (lurk.getLogo() == null || lurk.getLogo().isEmpty()) {
                                FollowingEntity following = streamingYorkieDB.followingDAO().getUserById(lurk.getChannelId());
                                if (following != null) {
                                    String logo = following.getLogo();
                                    lurk.setLogo(logo);
                                }
                            }
                            lurk.setBroadcastId(null);
                            lurk.setHtml(null);
                            lurk.setChannelInformedOfLurk(false);
                            lurk.setChannelIsToBeLurked(true);
                            streamingYorkieDB.lurkDAO().updateLurk(lurk);
                        }
                    }
                } catch (JSONException e) {
                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error getting stream status | " + e.toString(), true).run();
                }
            }
        }.start();
    }

    @Override
    void errorHandler(VolleyError error) {
        super.errorHandler(error);
    }

    @Override
    protected void offlineResponseHandler() {
        if (recyclerView != null && recyclerView.get() != null && weakActivity.get() != null) {
            weakActivity.get().runOnUiThread(() ->
                    Toast.makeText(weakActivity.get(), "OFFLINE: Cannot get stream status when offline", Toast.LENGTH_SHORT).show()
            );
        }
    }
}
