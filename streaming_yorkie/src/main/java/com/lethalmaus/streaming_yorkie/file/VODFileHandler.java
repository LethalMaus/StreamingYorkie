package com.lethalmaus.streaming_yorkie.file;

import android.content.Context;

import com.lethalmaus.streaming_yorkie.Globals;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Class specific to Users own Twitch Info. Can be used as a runnable.
 * @author LethalMaus
 */
public class VODFileHandler implements Runnable {

    private WeakReference<Context> weakContext;
    private JSONObject response;

    /**
     * Constructor with weak reference to a context, needed to display the VOD info
     * @author LethalMaus
     * @param weakContext weak reference context
     */
    public VODFileHandler(WeakReference<Context> weakContext) {
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
        writeVOD();
    }

    /**
     * Method to write VOD object. Used in run, can also be called directly.
     * @author LethalMaus
     */
    private void writeVOD() {
        JSONObject vodObject;
        try {
            if (response.has("videos")) {
                for (int i = 0; i < response.getJSONArray("videos").length(); i++) {
                    vodObject = new JSONObject();
                    vodObject.put("_id", response.getJSONArray("videos").getJSONObject(i).getString("_id").replace("v", ""));
                    vodObject.put("title", response.getJSONArray("videos").getJSONObject(i).getString("title"));
                    vodObject.put("description", response.getJSONArray("videos").getJSONObject(i).getString("description"));
                    vodObject.put("tag_list", response.getJSONArray("videos").getJSONObject(i).getString("tag_list"));
                    vodObject.put("url", response.getJSONArray("videos").getJSONObject(i).getString("url"));
                    vodObject.put("created_at", response.getJSONArray("videos").getJSONObject(i).getString("created_at").replace("T", " ").replace("Z", ""));
                    vodObject.put("game", response.getJSONArray("videos").getJSONObject(i).getString("game"));
                    String length = "";
                    int seconds = response.getJSONArray("videos").getJSONObject(i).getInt("length");
                    length += (seconds / 3600) + "h ";
                    length += ((seconds % 3600) / 60) + "m ";
                    length += ((seconds % 3600) % 60) + "s";
                    vodObject.put("length", length);
                    vodObject.put("preview", response.getJSONArray("videos").getJSONObject(i).getJSONObject("preview").getString("medium"));
                    new WriteFileHandler(weakContext, Globals.VOD_PATH + File.separator + vodObject.getString("_id"), null, vodObject.toString(),false).run();
                }
            }
        } catch (JSONException e) {
            new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n",true).run();
        }
    }
}
