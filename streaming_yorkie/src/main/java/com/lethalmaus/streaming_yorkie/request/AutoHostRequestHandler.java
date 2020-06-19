package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.adapter.HostAdapter;
import com.lethalmaus.streaming_yorkie.data_model.HostDataModel;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class for getting & changing auto host list
 * @author LethalMaus
 */
public class AutoHostRequestHandler extends RequestHandler {

    @Override
    public String url() {
        return "https://gql.twitch.tv/gql";
    }

    @Override
    public int method() {
        return Request.Method.POST;
    }

    /**
     * Constructor for AutoHostRequestHandler for getting and posting to auto host list
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    public AutoHostRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, final WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
        this.requestType = "AUTOHOST";
    }

    /**
     * Sets the required body for the request
     * @author LethalMaus
     * @param userId the user setting the list
     * @param dataset the list of hosts
     * @return instance of itself for method building
     */
    public AutoHostRequestHandler postBodyForSettingList(String userId, List<HostDataModel> dataset) {
        try {
            JSONObject postBody = new JSONObject();
            postBody.put("operationName", "setAutohostChannels");
            JSONArray channelIDs = new JSONArray();
            for (int i = 0; i < dataset.size(); i++) {
                channelIDs.put(dataset.get(i).getId());
            }
            JSONObject input = new JSONObject();
            input.put("channelIDs", channelIDs);
            input.put("userID", userId);
            JSONObject variables = new JSONObject();
            variables.put("input", input);
            postBody.put("variables", variables);
            JSONObject persistedQuery = new JSONObject();
            persistedQuery.put("version", 1);
            //TODO a list of sha256Hash is needed and should be externalized
            persistedQuery.put("sha256Hash", "7f03cc00e328e61d08c5d860b981db1edba48e43671ce91fe9c8501e93586d25");
            JSONObject extensions = new JSONObject();
            extensions.put("persistedQuery", persistedQuery);
            postBody.put("extensions", extensions);
            setPostBody(postBody);
        } catch (JSONException e) {
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error setting postBodyForSettingList |" + e, true).run();
        }
        return this;
    }

    /**
     * Sets the required body for the request
     * @author LethalMaus
     * @param username the username making the request
     * @return instance of itself for method building
     */
    public AutoHostRequestHandler postBodyForGettingList(String username) {
        try {
            JSONObject postBody = new JSONObject();
            postBody.put("operationName", "AutohostListPage_ListItems");
            JSONObject variables = new JSONObject();
            variables.put("channelLogin", username);
            postBody.put("variables", variables);
            JSONObject persistedQuery = new JSONObject();
            persistedQuery.put("version", 1);
            //TODO a list of sha256Hash is needed and should be externalized
            persistedQuery.put("sha256Hash", "4e5dda07121b8335fa31b5ff669ab01f2926b6b443b99e65fa7f3be52893ebe1");
            JSONObject extensions = new JSONObject();
            extensions.put("persistedQuery", persistedQuery);
            postBody.put("extensions", extensions);
            setPostBody(postBody);
        } catch (JSONException e) {
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error setting postBodyForGettingList |" + e, true).run();
        }
        return this;
    }

    @Override
    HashMap<String, String> getRequestHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/vnd.twitchtv.v5+json");
        headers.put("Client-ID", Globals.TWITCHID);
        headers.put("Content-Type", "application/json; charset=utf-8");
        if (Globals.checkWeakReference(weakContext) && new File(weakContext.get().getFilesDir().toString() + File.separator + "TWITCH_TOKEN").exists()) {
            headers.put("Authorization", "OAuth " + new ReadFileHandler(weakActivity, weakContext,"TWITCH_TOKEN").readFile());
        }
        return headers;
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread() {
            @Override
            public void run() {
                try {
                    List<HostDataModel> dataset = new ArrayList<>();
                    JSONArray hostArray;
                    if (response.getJSONObject("data").isNull("setAutohostChannels")) {
                        hostArray = response.getJSONObject("data").getJSONObject("user").getJSONObject("autohostChannels").getJSONArray("nodes");
                    } else {
                        hostArray = response.getJSONObject("data").getJSONObject("setAutohostChannels").getJSONObject("user").getJSONObject("autohostChannels").getJSONArray("nodes");
                    }
                    for (int i = 0; i < hostArray.length(); i++) {
                        JSONObject hostObject = hostArray.getJSONObject(i);
                        HostDataModel host = new HostDataModel(hostObject.getString("id"), hostObject.getString("login"), hostObject.getString("displayName"), hostObject.getString("profileImageURL"));
                        dataset.add(host);
                    }
                    if (Globals.checkWeakActivity(weakActivity) && Globals.checkWeakRecyclerView(recyclerView)) {
                        weakActivity.get().runOnUiThread(() -> {
                            recyclerView.get().stopScroll();
                            recyclerView.get().getRecycledViewPool().clear();
                            HostAdapter adapter = (HostAdapter) recyclerView.get().getAdapter();
                            if (adapter != null) {
                                adapter.setDataset(dataset);
                            }
                        });
                    }
                    onCompletion(true);
                } catch (JSONException e) {
                    if (Globals.checkWeakActivity(weakActivity)) {
                        weakActivity.get().runOnUiThread(() ->
                                Toast.makeText(weakActivity.get(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show()
                        );
                    }
                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "AutoHost response error | " + e.toString(), true).run();
                }
            }
        }.start();
    }
}
