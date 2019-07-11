package com.lethalmaus.streaming_yorkie.file;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Class specific to Users own Twitch Channel Info. Can be used as a runnable.
 * @author LethalMaus
 */
public class ChannelFileHandler implements Runnable {

    private WeakReference<Context> weakContext;
    private JSONObject response;

    /**
     * Constructor with weak reference to a context, needed to display the channel info
     * @author LethalMaus
     * @param weakContext weak reference context
     */
    public ChannelFileHandler(WeakReference<Context> weakContext) {
        this.weakContext = weakContext;
    }

    /**
     * Sets response rather than creating multiple instances. Helps preserve resources
     * @param response Volley JSON response
     */
    public void setResponse(JSONObject response) {
        this.response = response;
    }

    @Override
    public void run() {
        writeChannel();
    }

    /**
     * Method to write channel object. Used in run, can also be called directly.
     * @author LethalMaus
     */
    public void writeChannel() {
        try {
            JSONObject channel = new JSONObject();
            channel.put("display_name", response.getString("display_name"));
            channel.put("_id", response.getString("_id"));
            channel.put("logo", response.getString("logo"));
            channel.put("game", response.getString("game"));
            channel.put("created_at", response.getString("created_at").replace("T", " ").replace("Z", ""));
            channel.put("views", response.getInt("views"));
            channel.put("followers", response.getInt("followers"));
            channel.put("status", response.getString("status"));
            channel.put("description", response.getString("description"));
            //Broadcaster type is empty for non affiliates, so a custom one is given
            if (response.getString("broadcaster_type").equals("")) {
                channel.put("broadcaster_type","streamer");
            } else {
                channel.put("broadcaster_type",  response.getString("broadcaster_type"));
            }
            new WriteFileHandler(weakContext, "CHANNEL", null, channel.toString(), false).run();
        } catch (JSONException e) {
            Toast.makeText(weakContext.get(), "Channel can't be saved", Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakContext, "ERROR", null, "Error writing Channel File | " + e.toString(),true).run();
        }
    }
}
