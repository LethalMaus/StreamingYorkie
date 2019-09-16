package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.lethalmaus.streaming_yorkie.entity.Channel;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.view.ChannelView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

/**
 * Class for requesting current channel info
 * @author LethalMaus
 */
public class ChannelRequestHandler extends RequestHandler {

    @Override
    public String url() {
        return "https://api.twitch.tv/kraken/channel";
    }

    @Override
    public int method() {
        return Request.Method.GET;
    }

    /**
     * Constructor for ChannelRequestHandler for requesting current channel info
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public ChannelRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        super(weakActivity, weakContext, null);
        this.requestType = "CHANNEL";
    }

    @Override
    void errorHandler(VolleyError error) {
        String errorMessage = "";
        if (error.networkResponse != null) {
            errorMessage = error.networkResponse.statusCode + " | " + new String(error.networkResponse.data, StandardCharsets.UTF_8);
            if (error.networkResponse.statusCode == HttpURLConnection.HTTP_FORBIDDEN && errorMessage.toLowerCase().contains("not allowed to broadcast")) {
                if (weakActivity != null && weakActivity.get() != null) {
                    weakActivity.get().runOnUiThread(
                            new Runnable() {
                                public void run() {
                                    Toast.makeText(weakActivity.get(), "Twitch Two-Factor Authentication is required for all info", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                }
                new ChannelView(weakActivity, weakContext).execute();
            } else if (weakActivity != null && weakActivity.get() != null) {
                if (weakActivity != null && weakActivity.get() != null) {
                    weakActivity.get().runOnUiThread(
                            new Runnable() {
                                public void run() {
                                    Toast.makeText(weakActivity.get(), "Error requesting Channel", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                }
            }
        } else if (weakActivity != null && weakActivity.get() != null) {
            if (weakActivity != null && weakActivity.get() != null) {
                weakActivity.get().runOnUiThread(
                        new Runnable() {
                            public void run() {
                                Toast.makeText(weakActivity.get(), "Error requesting Channel", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        }
        if (errorMessage.isEmpty()) {
            errorMessage = error.toString();
        }
        new WriteFileHandler(weakContext, "ERROR", null, "Error requesting Channel | " + errorMessage, true).run();
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Channel existingChannel = streamingYorkieDB.channelDAO().getChanneById(Integer.parseInt(response.getString("_id")));
                    if (existingChannel != null) {
                        existingChannel.setDisplay_name(response.getString("display_name"));
                        existingChannel.setLogo(response.getString("logo"));
                        existingChannel.setGame(response.getString("game"));
                        existingChannel.setCreated_at(response.getString("created_at").replace("T", " ").replace("Z", ""));
                        existingChannel.setViews(response.getInt("views"));
                        existingChannel.setFollowers(response.getInt("followers"));
                        existingChannel.setStatus(response.getString("status"));
                        existingChannel.setDescription(response.getString("description"));
                        if (response.getString("broadcaster_type").equals("")) {
                            existingChannel.setBroadcasterType("streamer");
                        } else {
                            existingChannel.setBroadcasterType(response.getString("broadcaster_type"));
                        }
                        streamingYorkieDB.channelDAO().updateChannel(existingChannel);
                    } else {
                        Channel channel = new Channel(Integer.parseInt(response.getString("_id")),
                                response.getString("display_name"),
                                response.getString("logo"),
                                response.getString("game"),
                                response.getString("created_at").replace("T", " ").replace("Z", ""),
                                response.getInt("views"),
                                response.getInt("followers"),
                                response.getString("status"),
                                response.getString("description"),
                                response.getString("broadcaster_type"));
                        streamingYorkieDB.channelDAO().insertChannel(channel);
                    }
                    new ChannelView(weakActivity, weakContext).execute();
                } catch (JSONException e) {
                    if (weakActivity != null && weakActivity.get() != null) {
                        weakActivity.get().runOnUiThread(
                                new Runnable() {
                                    public void run() {
                                        Toast.makeText(weakContext.get(), "Channel can't be saved", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                    new WriteFileHandler(weakContext, "ERROR", null, "Error saving Channel | " + e.toString(), true).run();
                }
            }
        }).start();
    }
}
