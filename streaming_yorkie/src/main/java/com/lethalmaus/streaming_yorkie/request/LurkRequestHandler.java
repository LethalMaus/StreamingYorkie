package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.adapter.LurkAdapter;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

/**
 * Class for getting lurk Urls
 * @author LethalMaus
 */
public class LurkRequestHandler extends RequestHandler {

    private String channel;
    private String lurkToken;
    private String signature;

    /**
     * Constructor for LurkRequestHandler for requesting lurk Urls
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    public LurkRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
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
    public void sendRequest(final int offset) {
        if (networkIsAvailable()) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/api/channels/" + channel.toLowerCase() + "/access_token?need_https=true&oauth_token=" + token + "&platform=web&player_backend=mediaplayer&player_type=embed", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                lurkToken = response.getString("token");
                                signature = response.getString("sig");
                                getLurkUrl(new JSONObject(lurkToken).getString("channel_id"));
                            } catch (JSONException e) {
                                new WriteFileHandler(weakContext, "ERROR", null, "Error reading first Lurk Url JSON | " + e.toString(), true).run();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage = error.toString();
                    if (error.networkResponse != null) {
                        errorMessage = error.networkResponse.statusCode + " | " + new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    }
                    new WriteFileHandler(weakContext, "ERROR", null, "Error getting first Lurk Url | " + errorMessage, true).run();
                }
            });
            jsObjRequest.setTag("LURK1");
            VolleySingleton.getInstance(weakContext).addToRequestQueue(jsObjRequest);
        } else {
            offlineResponseHandler();
        }
    }

    /**
     * Gets the audio only lurl url
     * @author LethalMaus
     * @param channelId String of channel ID
     */
    private void getLurkUrl(final String channelId) {
        if (networkIsAvailable()) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://usher.ttvnw.net/api/channel/hls/" + channel.toLowerCase() + ".m3u8?allow_source=true&allow_audio_only=true&baking_bread=true&baking_brownies=true&baking_brownies_timeout=1050&fast_bread=true&p=9293905&player_backend=mediaplayer&playlist_include_framerate=true&reassignments_supported=true&sig=" + signature + "&token=" + Uri.encode(lurkToken),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (weakActivity != null && weakActivity.get() != null) {
                                String lurkUrl = response.substring(response.indexOf("VIDEO=\"audio_only\"")+18);
                                String video = "<video autoplay onerror='this.load()' onloadstart='this.volume=0.000001' id='" + channel + "'><source src='" + lurkUrl + "' type='application/x-mpegURL' onended='document.getElementById('" + channel + "').outerHTML=\"\"'></video>";
                                new DeleteFileHandler(weakContext, null).deleteFileOrPath(Globals.LURK_PATH + File.separator + channel);
                                new WriteFileHandler(weakContext, Globals.LURK_PATH + File.separator + channelId + "-" + channel, null, video, false).writeToFileOrPath();
                                if (recyclerView != null && recyclerView.get() != null &&  recyclerView.get().getAdapter() != null) {
                                    recyclerView.get().setAdapter(new LurkAdapter(weakActivity, weakContext, recyclerView));
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage = "";
                    if (error.networkResponse != null) {
                        errorMessage = error.networkResponse.statusCode + " | " + new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        if (error.networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND || error.networkResponse.statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
                            Toast.makeText(weakActivity.get(), "Stream from '" + channel + "' has ended", Toast.LENGTH_SHORT).show();
                            new DeleteFileHandler(weakContext, null).deleteFileOrPath(Globals.LURK_PATH + File.separator + channel);
                            if (recyclerView != null && recyclerView.get() != null &&  recyclerView.get().getAdapter() != null) {
                                recyclerView.get().setAdapter(new LurkAdapter(weakActivity, weakContext, recyclerView));
                            }
                        } else {
                            if (errorMessage.isEmpty()) {
                                errorMessage = error.toString();
                            }
                            new WriteFileHandler(weakContext, "ERROR", null, "Error getting second Lurk Url | " + errorMessage, true).run();
                        }
                    } else {
                        if (errorMessage.isEmpty()) {
                            errorMessage = error.toString();
                        }
                        new WriteFileHandler(weakContext, "ERROR", null, "Error getting second Lurk Url | " + errorMessage, true).run();
                    }

                }
            });
            stringRequest.setTag("LURK2");
            VolleySingleton.getInstance(weakContext).addToRequestQueue(stringRequest);
        } else {
            offlineResponseHandler();
        }
    }

    @Override
    protected void offlineResponseHandler() {
        if (weakActivity != null && weakActivity.get() != null) {
            Toast.makeText(weakActivity.get(), "OFFLINE: Cannot lurk when offline", Toast.LENGTH_SHORT).show();
        }
    }
}
