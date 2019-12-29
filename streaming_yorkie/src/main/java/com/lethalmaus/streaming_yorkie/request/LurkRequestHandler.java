package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.entity.LurkEntity;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;

/**
 * Class for getting lurk Urls
 * @author LethalMaus
 */
public class LurkRequestHandler extends RequestHandler {

    private String channel;
    private String lurkToken;
    private String signature;

    @Override
    public String url() {
        return "https://api.twitch.tv/api/channels/" + channel.toLowerCase() + "/access_token?need_https=true&platform=web&player_backend=mediaplayer&player_type=embed&client_id=" + Globals.TWITCHID;
    }

    @Override
    public int method() {
        return Request.Method.GET;
    }

    /**
     * Constructor for LurkRequestHandler for requesting lurk Urls
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    public LurkRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
        requestType = "LURK";
    }

    /**
     * Sets the channel for new requests.
     * @author LethalMaus
     * @param channel String of channel name
     * @return instance of itself for method building
     */
    public LurkRequestHandler newRequest(String channel) {
        this.channel = channel;
        return this;
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread() {
            public void run() {
                try {
                    lurkToken = response.getString("token");
                    signature = response.getString("sig");
                    getLurkUrl(new JSONObject(lurkToken).getString("channel_id"));
                } catch (JSONException e) {
                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error reading first Lurk Url JSON | " + e.toString(), true).run();
                }
            }
        }.start();
    }

    @Override
    void errorHandler(VolleyError error) {
        if (error.networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND && weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
            streamingYorkieDB.lurkDAO().deleteLurkByChannelName(channel);
            weakActivity.get().runOnUiThread(
                    new Runnable() {
                        public void run() {
                            Toast.makeText(weakActivity.get(), "Unable to find channel '" + channel + "'", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        } else {
            String errorMessage = error.networkResponse.statusCode + " | " + new String(error.networkResponse.data, StandardCharsets.UTF_8);
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error requesting " + requestType + ": " + errorMessage, true).run();
        }
    }

    /**
     * Gets the audio only lurl url
     * @author LethalMaus
     * @param channelId String of channel ID
     */
    private void getLurkUrl(final String channelId) {
        if (networkIsAvailable(weakContext)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://usher.ttvnw.net/api/channel/hls/" + channel.toLowerCase() + ".m3u8?allow_source=true&allow_audio_only=true&p=" + new Random().nextInt(999999) + "&player=twitchweb&type=any&sig=" + signature + "&token=" + Uri.encode(lurkToken),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            new Thread() {
                                public void run() {
                                    String lurkUrl = response.substring(response.indexOf("VIDEO=\"audio_only\"") + 18);
                                    String broadcastId = response.substring(response.indexOf("BROADCAST-ID=\"") + 14);
                                    broadcastId = broadcastId.substring(0, broadcastId.indexOf("\","));
                                    String htmlBlock = "<div id='" + channel.toLowerCase().trim() + "'>"
                                            + "<video autoplay onerror='this.load()' onloadstart='this.volume=0.000001'>"
                                            + "<source src='" + lurkUrl + "' type='application/x-mpegURL' onended='document.getElementById('" + channel.toLowerCase().trim() + "').outerHTML=\"\"'>"
                                            + "</video></div>";
                                    LurkEntity lurk = streamingYorkieDB.lurkDAO().getLurkByChannelName(channel);
                                    if (lurk.getChannelId() == 0 || lurk.getHtml().isEmpty() || !broadcastId.contentEquals(lurk.getBroadcastId())) {
                                        if (lurk.getLogo() == null || lurk.getLogo().isEmpty()) {
                                            String logo = streamingYorkieDB.followingDAO().getUserById(Integer.parseInt(channelId)).getLogo();
                                            lurk.setLogo(logo.isEmpty() ? null : logo);
                                        }
                                        lurk.setChannelId(Integer.parseInt(channelId));
                                        lurk.setBroadcastId(broadcastId);
                                        lurk.setHtml(htmlBlock);
                                        lurk.setChannelInformedOfLurk(false);
                                        lurk.setChannelIsToBeLurked(lurk.isChannelIsToBeLurked());
                                    } else {
                                        lurk.setHtml(htmlBlock);
                                    }
                                    streamingYorkieDB.lurkDAO().updateLurk(lurk);
                                    onCompletion();
                                }
                            }.start();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage;
                    if (error.networkResponse != null) {
                        errorMessage = error.networkResponse.statusCode + " | " + new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        if (error.networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND || error.networkResponse.statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
                            if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
                                weakActivity.get().runOnUiThread(
                                        new Runnable() {
                                            public void run() {
                                                Toast.makeText(weakActivity.get(), "'" + channel + "' is offline", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );
                            }
                            new Thread() {
                                public void run() {
                                    LurkEntity lurk = streamingYorkieDB.lurkDAO().getLurkByChannelName(channel);
                                    if (lurk.getLogo() == null || lurk.getLogo().isEmpty()) {
                                        String logo = streamingYorkieDB.followingDAO().getUserById(Integer.parseInt(channelId)).getLogo();
                                        lurk.setLogo(logo.isEmpty() ? null : logo);
                                    }
                                    lurk.setChannelId(Integer.parseInt(channelId));
                                    lurk.setBroadcastId(null);
                                    lurk.setHtml(null);
                                    lurk.setChannelInformedOfLurk(false);
                                    lurk.setChannelIsToBeLurked(lurk.isChannelIsToBeLurked());
                                    streamingYorkieDB.lurkDAO().updateLurk(lurk);
                                    onCompletion();
                                }
                            }.start();
                        } else {
                            if (errorMessage.isEmpty()) {
                                errorMessage = error.toString();
                            }
                            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error getting second Lurk Url | " + errorMessage, true).run();
                        }
                    } else {
                        errorMessage = error.toString();
                        new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error getting second Lurk Url | " + errorMessage, true).run();
                    }

                }
            });
            stringRequest.setTag(requestType);
            VolleySingleton.getInstance(weakContext).addToRequestQueue(stringRequest);
        } else {
            offlineResponseHandler();
        }
    }

    @Override
    protected void offlineResponseHandler() {
        if (recyclerView != null && recyclerView.get() != null && weakActivity.get() != null) {
            weakActivity.get().runOnUiThread(
                    new Runnable() {
                        public void run() {
                            Toast.makeText(weakActivity.get(), "OFFLINE: Cannot lurk when offline", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }

    @Override
    HashMap<String, String> getRequestHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/vnd.twitchtv.v5+json");
        headers.put("Client-ID", Globals.TWITCHID);
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }
}
