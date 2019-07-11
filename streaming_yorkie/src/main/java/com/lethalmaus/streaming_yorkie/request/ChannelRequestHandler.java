package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lethalmaus.streaming_yorkie.file.ChannelFileHandler;
import com.lethalmaus.streaming_yorkie.file.UserFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.view.ChannelView;
import com.lethalmaus.streaming_yorkie.view.UserView;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Class for requesting current channel info
 * @author LethalMaus
 */
public class ChannelRequestHandler extends RequestHandler {

    private ChannelFileHandler channelFileHandler;

    /**
     * Constructor for ChannelRequestHandler for requesting current channel info
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public ChannelRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        super(weakActivity, weakContext, null);
        channelFileHandler = new ChannelFileHandler(weakContext);
    }

    @Override
    public void sendRequest(int offset) {
        if (networkIsAvailable()) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/kraken/channel", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            responseHandler(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    if (error.networkResponse.statusCode == HttpURLConnection.HTTP_FORBIDDEN && body.toLowerCase().contains("not allowed to broadcast")) {
                        Toast.makeText(weakActivity.get(), "Twitch Two-Factor Authentication is required for all info", Toast.LENGTH_SHORT).show();
                        new ChannelView(weakActivity, weakContext).execute();
                    } else if (weakActivity != null && weakActivity.get() != null) {
                        Toast.makeText(weakActivity.get(), "Error requesting Channel", Toast.LENGTH_SHORT).show();
                        offlineResponseHandler();
                    }
                    new WriteFileHandler(weakContext, "ERROR", null, "Error requesting Channel | " + error.toString(), true).run();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    return getRequestHeaders();
                }
            };
            jsObjRequest.setTag("CHANNEL");
            VolleySingleton.getInstance(weakContext).addToRequestQueue(jsObjRequest);
        } else {
            offlineResponseHandler();
        }
    }

    @Override
    public void responseHandler(JSONObject response) {
        channelFileHandler.setResponse(response);
        channelFileHandler.writeChannel();
        new ChannelView(weakActivity, weakContext).execute();
    }

    @Override
    protected void offlineResponseHandler() {
        if (weakActivity != null && weakActivity.get() != null) {
            Toast.makeText(weakActivity.get(), "OFFLINE: Showing saved Channel Info", Toast.LENGTH_SHORT).show();
        }
        new ChannelView(weakActivity, weakContext).execute();
    }
}
