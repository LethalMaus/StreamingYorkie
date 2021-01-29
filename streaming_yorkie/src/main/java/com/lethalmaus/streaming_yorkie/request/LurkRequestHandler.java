package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.entity.FollowingEntity;
import com.lethalmaus.streaming_yorkie.entity.LurkEntity;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;

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
        return "https://gql.twitch.tv/gql";
    }

    @Override
    public int method() {
        return Request.Method.POST;
    }

    /**
     * Constructor for LurkRequestHandler for requesting lurk Urls
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    protected LurkRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> recyclerView) {
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
        try {
            JSONObject postBody = new JSONObject();
            postBody.put("operationName", "PlaybackAccessToken");
            JSONObject variables = new JSONObject();
            variables.put("isLive", true);
            variables.put("login", channel.toLowerCase());
            variables.put("isVod", false);
            variables.put("vodID", "");
            variables.put("playerType", "embed");
            postBody.put("variables", variables);
            JSONObject persistedQuery = new JSONObject();
            persistedQuery.put("version", 1);
            persistedQuery.put("sha256Hash", "0828119ded1c13477966434e15800ff57ddacf13ba1911c129dc2200705b0712");
            JSONObject extensions = new JSONObject();
            extensions.put("persistedQuery", persistedQuery);
            postBody.put("extensions", extensions);
            setPostBody(postBody);
        } catch (JSONException e) {
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error setting post body for LurkRequest |" + e, true).run();
        }
        return this;
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread() {
            public void run() {
                try {
                    lurkToken = response.getJSONObject("data").getJSONObject("streamPlaybackAccessToken").getString("value");
                    signature = response.getJSONObject("data").getJSONObject("streamPlaybackAccessToken").getString("signature");
                    getLurkUrl(new JSONObject(lurkToken).getString("channel_id"));
                } catch (JSONException e) {
                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error reading first Lurk Url JSON | " + e.toString(), true).run();
                }
            }
        }.start();
    }

    @Override
    void errorHandler(VolleyError error) {
        if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
            weakActivity.get().runOnUiThread(() ->
                    Toast.makeText(weakActivity.get(), "Unable to find channel '" + channel + "'", Toast.LENGTH_SHORT).show()
            );
        }
        if (error.networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
            new Thread() {
                public void run() {
                    streamingYorkieDB.lurkDAO().deleteLurkByChannelName(channel);
                }
            }.start();
        } else {
            String errorMessage = error.networkResponse.statusCode + " | " + new String(error.networkResponse.data, StandardCharsets.UTF_8);
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error requesting " + requestType + ": " + errorMessage, true).run();
        }
        onCompletion(false);
    }

    /**
     * Gets the audio only lurk url
     * @author LethalMaus
     * @param channelId String of channel ID
     */
    private void getLurkUrl(final String channelId) {
        if (networkIsAvailable(weakContext)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://usher.ttvnw.net/api/channel/hls/" + channel.toLowerCase() + ".m3u8?allow_source=true&allow_audio_only=true&p=" + new SecureRandom().nextInt(999999) + "&player=twitchweb&type=any&sig=" + signature + "&token=" + Uri.encode(lurkToken), (String response) ->
                            new Thread() {
                                public void run() {
                                    LurkEntity lurk = streamingYorkieDB.lurkDAO().getLurkByChannelName(channel);
                                    String broadcastId = response.substring(response.indexOf("BROADCAST-ID=\"") + 14);
                                    if (lurk != null && (lurk.getBroadcastId() == null || !broadcastId.contentEquals(lurk.getBroadcastId()))) {
                                        String lurkUrl = "";
                                        if (Globals.checkWeakReference(weakContext) && new File(weakContext.get().getFilesDir().toString() + File.separator + Globals.FILE_SETTINGS_LURK).exists()) {
                                            String settingsString = new ReadFileHandler(null, weakContext, Globals.FILE_SETTINGS_LURK).readFile();
                                            if (!settingsString.isEmpty()) {
                                                try {
                                                    JSONObject settings = new JSONObject(settingsString);
                                                    if (settings.has(Globals.SETTINGS_AUDIO_ONLY) && !settings.getBoolean(Globals.SETTINGS_AUDIO_ONLY)) {
                                                        if (response.contains("VIDEO=\"160p30\"")) {
                                                            lurkUrl = response.substring(response.indexOf("VIDEO=\"160p30\"") + 15);
                                                        } else {
                                                            lurkUrl = response.substring(response.indexOf("VIDEO=\"chunked\"") + 16);
                                                        }
                                                        lurkUrl = lurkUrl.substring(0, lurkUrl.indexOf("\n")).replace("\n", "");
                                                    }
                                                } catch (JSONException e) {
                                                    Toast.makeText(weakContext.get(), "Error reading settings for " + Globals.SETTINGS_AUTOLURK, Toast.LENGTH_SHORT).show();
                                                    new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null,weakContext.get().getString(R.string.error_reading_lurk_settings) + weakContext.get().getString(R.string.pipe) + Globals.SETTINGS_AUDIO_ONLY + weakContext.get().getString(R.string.pipe) + e.toString(), true).run();
                                                }
                                            }
                                        }
                                        if (lurkUrl.isEmpty()) {
                                            lurkUrl = response.substring(response.indexOf("VIDEO=\"audio_only\"") + 18);
                                        }
                                        broadcastId = broadcastId.substring(0, broadcastId.indexOf("\","));
                                        String htmlBlock = "<div id='" + channel.toLowerCase().trim() + "'>"
                                                + "<video width='200' height='120' autoplay onerror='this.load()' onloadstart='this.volume=0.010001'>"
                                                + "<source src='" + lurkUrl + "' type='application/x-mpegURL' onended='document.getElementById('" + channel.toLowerCase().trim() + "').outerHTML=\"\"'>"
                                                + "</video></div>";
                                        if (lurk.getChannelId() == 0 || lurk.getHtml() == null || lurk.getHtml().isEmpty()) {
                                            if (lurk.getLogo() == null || lurk.getLogo().isEmpty()) {
                                                FollowingEntity following = streamingYorkieDB.followingDAO().getUserById(Integer.parseInt(channelId));
                                                if (following != null) {
                                                    String logo = following.getLogo();
                                                    lurk.setLogo(logo);
                                                }
                                            }
                                            lurk.setChannelId(Integer.parseInt(channelId));
                                            lurk.setBroadcastId(broadcastId);
                                            lurk.setHtml(htmlBlock);
                                            lurk.setChannelIsToBeLurked(lurk.isChannelIsToBeLurked());
                                        } else {
                                            lurk.setHtml(htmlBlock);
                                        }
                                        streamingYorkieDB.lurkDAO().updateLurk(lurk);
                                    }
                                    onCompletion(true);
                                }
                            }.start()
                    , (VolleyError error)  -> {
                    String errorMessage;
                    if (error.networkResponse != null) {
                        errorMessage = error.networkResponse.statusCode + " | " + new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        if (error.networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND || error.networkResponse.statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
                            if (Globals.checkWeakActivity(weakActivity)) {
                                weakActivity.get().runOnUiThread(() ->
                                        Toast.makeText(weakActivity.get(), "'" + channel + "' is offline", Toast.LENGTH_SHORT).show()
                                );
                                new Thread() {
                                    public void run() {
                                        LurkEntity lurk = streamingYorkieDB.lurkDAO().getLurkByChannelName(channel);
                                        if (lurk.getLogo() == null || lurk.getLogo().isEmpty()) {
                                            FollowingEntity following = streamingYorkieDB.followingDAO().getUserById(Integer.parseInt(channelId));
                                            if (following != null) {
                                                String logo = following.getLogo();
                                                lurk.setLogo(logo);
                                            }
                                        }
                                        lurk.setChannelId(Integer.parseInt(channelId));
                                        lurk.setBroadcastId(null);
                                        lurk.setHtml(null);
                                        lurk.setChannelInformedOfLurk(false);
                                        lurk.setChannelIsToBeLurked(lurk.isChannelIsToBeLurked());
                                        streamingYorkieDB.lurkDAO().updateLurk(lurk);
                                        onCompletion(false);
                                    }
                                }.start();
                            }
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
            );
            stringRequest.setTag(requestType);
            VolleySingleton.getInstance(weakContext).addToRequestQueue(stringRequest);
        } else {
            offlineResponseHandler();
        }
    }

    @Override
    protected void offlineResponseHandler() {
        if (Globals.checkWeakActivity(weakActivity)) {
            weakActivity.get().runOnUiThread(() ->
                    Toast.makeText(weakActivity.get(), "OFFLINE: Cannot lurk when offline", Toast.LENGTH_SHORT).show()
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
