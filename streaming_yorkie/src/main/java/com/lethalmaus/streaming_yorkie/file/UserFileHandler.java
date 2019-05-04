package com.lethalmaus.streaming_yorkie.file;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Class specific to Users own Twitch Info. Can be used as a runnable.
 * @author LethalMaus
 */
public class UserFileHandler implements Runnable {

    private WeakReference<Context> weakContext;
    private JSONObject response;

    /**
     * Constructor with weak reference to a context, needed to display the user info
     * @author LethalMaus
     * @param weakContext weak reference context
     */
    public UserFileHandler(WeakReference<Context> weakContext) {
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
        writeUser();
    }

    /**
     * Method to write user object. Used in run, can also be called directly.
     * @author LethalMaus
     */
    public void writeUser() {
        try {
            JSONObject user = new JSONObject();
            user.put("display_name", response.getString("display_name"));
            user.put("_id", response.getString("_id"));
            user.put("logo", response.getString("logo"));
            user.put("game", response.getString("game"));
            user.put("created_at", response.getString("created_at").replace("T", " ").replace("Z", ""));
            user.put("views", response.getInt("views"));
            user.put("followers", response.getInt("followers"));
            user.put("status", response.getString("status"));
            user.put("description", response.getString("description"));
            //Broadcaster type is empty for non affiliates, so a custom one is given
            if (response.getString("broadcaster_type").equals("")) {
                user.put("broadcaster_type","streamer");
            } else {
                user.put("broadcaster_type",  response.getString("broadcaster_type"));
            }
            new WriteFileHandler(weakContext, "USER", null, user.toString(), false).run();
        } catch (JSONException e) {
            Toast.makeText(weakContext.get(), "User can't be saved", Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n",true).run();
        }
    }
}
