package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

/**
 * Class for requesting VODs to check if an Update is needed
 * @author LethalMaus
 */
public class VODUpdateRequestHandler extends RequestHandler {

    @Override
    public String url() {
        return "https://api.twitch.tv/kraken/channels/" + userID + "/videos" + "?limit=" + Globals.VOD_UPDATE_REQUEST_LIMIT;
    }

    @Override
    public int method() {
        return Request.Method.GET;
    }

    /**
     * Constructor for VODUpdateRequestHandler for requesting current VODs
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    public VODUpdateRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
        requestType = "VOD_UPDATE";
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread(() -> {
            try {
                long lastUpdated = 0;
                String lastUpdatedString = new ReadFileHandler(weakActivity, weakContext, "VOD_TIMESTAMP").readFile();
                if (!lastUpdatedString.isEmpty()) {
                    lastUpdated = Long.parseLong(lastUpdatedString);
                }
                int[] lastVODs = streamingYorkieDB.vodDAO().getLastVODs(lastUpdated);
                if (lastVODs.length == response.getJSONArray("videos").length() && response.getInt("_total") == streamingYorkieDB.vodDAO().getVODsCount()) {
                    for (int i = 0; i < lastVODs.length; i++) {
                        if (lastVODs[i] != Integer.parseInt(response.getJSONArray("videos").getJSONObject(i).getString("_id").replace("v", ""))) {
                            new VODRequestHandler(weakActivity, weakContext, recyclerView){
                                @Override
                                public void onCompletion(boolean hideProgressBar) {
                                    VODUpdateRequestHandler.this.onCompletion(hideProgressBar);
                                }
                            }.initiate().sendRequest(true);
                            return;
                        }
                    }
                    if (weakActivity != null && weakActivity.get() != null) {
                        weakActivity.get().runOnUiThread(() ->
                                weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE)
                        );
                    }
                    onCompletion(true);
                } else {
                    new VODRequestHandler(weakActivity, weakContext, recyclerView){
                        @Override
                        public void onCompletion(boolean hideProgressBar) {
                            VODUpdateRequestHandler.this.onCompletion(hideProgressBar);
                        }
                    }.initiate().sendRequest(true);
                }
            } catch (JSONException e) {
                if (weakActivity != null && weakActivity.get() != null) {
                    weakActivity.get().runOnUiThread(() ->
                            Toast.makeText(weakActivity.get(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show()
                    );
                }
                new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "VODUpdate response error | " + e.toString(), true).run();
            }
        }).start();
    }

    @Override
    public Response<JSONObject> parseRequestNetworkResponse(NetworkResponse response, String PROTOCOL_CHARSET) {
        try {
            String utf8String = new String(response.data, StandardCharsets.UTF_8);
            return Response.success(new JSONObject(utf8String), HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }
}
