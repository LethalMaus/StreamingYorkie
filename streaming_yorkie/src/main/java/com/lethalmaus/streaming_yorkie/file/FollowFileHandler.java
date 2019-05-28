package com.lethalmaus.streaming_yorkie.file;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Class to handle files specific to Followers & Following.
 * Can be used as a runnable
 * @author LethalMaus
 */
public class FollowFileHandler implements Runnable {

    private WeakReference<Context> weakContext;
    private String userPath;
    private String requestDestinationPath;
    private JSONObject response;
    private boolean organize;

    /**
     * Constructor for FollowFileHandler
     * @author LethalMaus
     * @param weakContext to avoid memory leaks. Needed to display followers/following once task is complete
     * @param userPath directory path to store the user object
     * @param requestDestinationPath directory path to store 'current' user ids
     * @param response Volley JSON response
     */
    public FollowFileHandler(WeakReference<Context> weakContext, String userPath, String requestDestinationPath, JSONObject response) {
        this.weakContext = weakContext;
        this.userPath = userPath;
        this.requestDestinationPath = requestDestinationPath;
        this.response = response;
    }

    /**
     * The response can be changed before each run to save calling multiple new instances
     * @author LethalMaus
     * @param response JSONObject Response from Volley request
     */
    public void setResponse(JSONObject response) {
        this.response = response;
    }

    /**
     * Sets the boolean organize
     * @author LethalMaus
     * @param organize boolean wether to organize files or not
     */
    public void setOrganize(boolean organize) {
        this.organize = organize;
    }

    @Override
    public void run() {
        writeFollowerFile();
    }

    /**
     * Writes single & multiple Following or multiple Follower files. Writes an object to the userPath & an ID to the requestDestinationPath
     * @author LethalMaus
     */
    void writeFollowerFile() {
        if (organize) {
            organizeFollowerFiles();
        } else {
            JSONObject userObject;
            try {
                //Follow Request sends back a single user, Follower/Following request sends and array back.
                if (response.has("follows")) {
                    ArrayList<String> userIDs = new ArrayList<>();
                    String arrayPath;
                    //The twitch api has different ways to request Followers/Following & the response is slightly different
                    if (response.getJSONArray("follows").getJSONObject(0).has("user")) {
                        arrayPath = "user";
                    } else {
                        arrayPath = "channel";
                    }
                    for (int i = 0; i < response.getJSONArray("follows").length(); i++) {
                        userObject = new JSONObject();
                        userObject.put("display_name", response.getJSONArray("follows").getJSONObject(i).getJSONObject(arrayPath).getString("display_name"));
                        userObject.put("_id", response.getJSONArray("follows").getJSONObject(i).getJSONObject(arrayPath).getString("_id"));
                        userObject.put("logo", response.getJSONArray("follows").getJSONObject(i).getJSONObject(arrayPath).getString("logo").replace("300x300", "50x50"));
                        userObject.put("created_at", response.getJSONArray("follows").getJSONObject(i).getString("created_at"));
                        //When requesting Followers, the bool notifications is not present
                        if (response.getJSONArray("follows").getJSONObject(i).has("notifications")) {
                            userObject.put("notifications", response.getJSONArray("follows").getJSONObject(i).getBoolean("notifications"));
                        }
                        new WriteFileHandler(weakContext, userPath + File.separator + userObject.getString("_id"), null, userObject.toString(), false).writeToFileOrPath();
                        userIDs.add(response.getJSONArray("follows").getJSONObject(i).getJSONObject(arrayPath).getString("_id"));
                    }
                    new WriteFileHandler(weakContext, requestDestinationPath, userIDs, null, false).writeToFileOrPath();
                } else {
                    userObject = new JSONObject();
                    userObject.put("display_name", response.getJSONObject("channel").getString("display_name"));
                    userObject.put("_id", response.getJSONObject("channel").getString("_id"));
                    userObject.put("logo", response.getJSONObject("channel").getString("logo"));
                    userObject.put("created_at", response.getString("created_at"));
                    //When requesting Followers, the bool notifications is not present
                    if (response.has("notifications")) {
                        userObject.put("notifications", response.getBoolean("notifications"));
                    }
                    new WriteFileHandler(weakContext, userPath + File.separator + userObject.getString("_id"), null, userObject.toString(), false).writeToFileOrPath();
                    new WriteFileHandler(weakContext, requestDestinationPath + File.separator + userObject.getString("_id"), null, null, false).writeToFileOrPath();
                }
            } catch (JSONException e) {
                new WriteFileHandler(weakContext, "ERROR", null, "Error writing FollowFile | " + e.toString(), true).run();
            }
        }
    }

    /**
     * To be overridden to apply specific logic to each use case
     * @author LethalMaus
     */
    public void organizeFollowerFiles(){}
}
