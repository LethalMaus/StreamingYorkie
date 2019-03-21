package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.adapter.UserAdapter;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.FollowFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Parent class for all request handlers
 * @author LethalMaus
 */
public class RequestHandler {

    //User token for authorization
    private String token;

    int offset;
    int twitchTotal;
    int userCount;
    String userID;

    //Weak references to avoid memory leaks
    WeakReference<Activity> weakActivity;
    WeakReference<Context> weakContext;
    WeakReference<RecyclerView> recyclerView;

    FollowFileHandler followFileHandler;

    //Paths for the file handlers
    String currentUsersPath;
    String newUsersPath;
    String unfollowedUsersPath;
    String excludedUsersPath;
    String usersPath;
    String requestPath;

    //Display preferences
    protected String usersToDisplay;
    protected String actionButtonType1;
    protected String actionButtonType2;
    protected String actionButtonType3;

    //Boolean whether to show updated files
    boolean displayUsers;
    //Boolean whether to F4F files or not
    boolean commonFolders;

    /**
     * Constructor for RequestHandler. Parent class for all request handlers
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    public RequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> recyclerView){
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        this.recyclerView = recyclerView;

        if (weakContext != null && weakContext.get() != null && new File(weakContext.get().getFilesDir().toString() + File.separator + "TOKEN").exists()) {
            token = new ReadFileHandler(weakContext,"TOKEN").readFile();
        }
        try {
            JSONObject user = new JSONObject(new ReadFileHandler(weakContext, "USER").readFile());
            userID = user.getString("_id");
        } catch (JSONException e) {
            new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
        }
    }

    /**
     * Sets Paths for file handlers
     * @author LethalMaus
     * @param currentUsersPath constant directory of current users
     * @param newUsersPath constant directory of new users
     * @param unfollowedUsersPath constant directory of unfollowed users
     * @param excludedUsersPath constant directory of excluded users
     * @param requestPath constant directory of to temporarily store requested users before being organized
     * @param usersPath constant directory of user objects
     * @return instance of itself for method building
     */
    public RequestHandler setPaths(String currentUsersPath, String newUsersPath, String unfollowedUsersPath, String excludedUsersPath, String requestPath, String usersPath) {
        this.currentUsersPath = currentUsersPath;
        this.newUsersPath = newUsersPath;
        this.unfollowedUsersPath = unfollowedUsersPath;
        this.excludedUsersPath = excludedUsersPath;
        this.requestPath = requestPath;
        this.usersPath = usersPath;
        return this;
    }

    /**
     * Sets display preferences
     * @author LethalMaus
     * @param usersToDisplay constant of users to display
     * @param actionButtonType1 constant of which button type is required
     * @param actionButtonType2 constant of which button type is required
     * @param actionButtonType3 constant of which button type is required
     * @return instance of itself for method building
     */
    public RequestHandler setDisplayPreferences(String usersToDisplay, String actionButtonType1, String actionButtonType2, String actionButtonType3) {
        this.usersToDisplay = usersToDisplay;
        this.actionButtonType1 = actionButtonType1;
        this.actionButtonType2 = actionButtonType2;
        this.actionButtonType3 = actionButtonType3;
        return this;
    }

    /**
     * Sets the total & user count to '0' for new requests.
     * This variables are dynamic & reused during multiple request firing
     * @author LethalMaus
     * @return instance of itself for method building
     */
    public RequestHandler newRequest() {
        twitchTotal = 0;
        userCount = 0;
        return this;
    }

    /**
     * Method for sending a request.
     * This method is overridden in every sub-class.
     * @author LethalMaus
     * @param offset used for stacking requests
     */
    public void sendRequest(int offset) {}

    /**
     * Method for handling a request response
     * This method is overridden in every sub-class
     * @author LethalMaus
     * @param response JSON response object
     */
    public void responseHandler(JSONObject response) {}

    /**
     * Method to display saved users when offline or when an error occurs
     * @author LethalMaus
     */
    protected void offlineResponseHandler() {
        if (displayUsers && recyclerView != null && recyclerView.get() != null) {
            if (weakContext != null && weakContext.get() != null) {
                Toast.makeText(weakContext.get(), "OFFLINE: Showing saved Users", Toast.LENGTH_SHORT).show();
            }
            recyclerView.get().setAdapter(new UserAdapter(weakActivity, weakContext)
                    .setPaths(newUsersPath, currentUsersPath, unfollowedUsersPath, excludedUsersPath, usersPath)
                    .setDisplayPreferences(usersToDisplay, actionButtonType1, actionButtonType2, actionButtonType3));
        }
    }

    /**
     * Method for handling a request error response.
     * Provokes offlineResponseHandler
     * @author LethalMaus
     * @param e Volley Error
     */
    void errorHandler(VolleyError e) {
        if (weakContext != null && weakContext.get() != null) {
            Toast.makeText(weakContext.get(), "Error requesting Users", Toast.LENGTH_SHORT).show();
        }
        new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
        offlineResponseHandler();
    }

    /**
     * Method for retrieving Request headers
     * @author LethalMaus
     * @return HashMap - String, String - of headers
     */
    HashMap<String, String> getRequestHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/vnd.twitchtv.v5+json");
        headers.put("Client-ID", Globals.CLIENTID);
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "OAuth " + token);
        return headers;
    }

    /**
     * Cancels all requests sent and voids any methods that would be provoked afterwards
     * @author LethalMaus
     */
    public void cancelAllRequests() {
        VolleySingleton.getInstance(weakContext).getRequestQueue().cancelAll("FOLLOWERS");
        VolleySingleton.getInstance(weakContext).getRequestQueue().cancelAll("FOLLOWING");
        VolleySingleton.getInstance(weakContext).getRequestQueue().cancelAll("FOLLOW");
    }

    /**
     * Method for checking network status
     * @author LethalMaus
     * @return boolean as to whether network is available
     */
    public boolean networkIsAvailable() {
        NetworkInfo activeNetwork = null;
        if (weakContext != null && weakContext.get() != null) {
            ConnectivityManager systemService = (ConnectivityManager) weakContext.get().getSystemService(CONNECTIVITY_SERVICE);
            activeNetwork = systemService.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
