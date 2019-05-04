package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.adapter.VODAdapter;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.VODFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Class for requesting VODs
 * @author LethalMaus
 */
public class VODRequestHandler extends RequestHandler {

    private VODFileHandler vodFileHandler;

    /**
     * Constructor for VODRequestHandler for requesting current VODs
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     * @param displayVODs constant of vod to be displayed
     */
    public VODRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> recyclerView, boolean displayVODs) {
        super(weakActivity, weakContext, recyclerView);
        vodFileHandler = new VODFileHandler(weakContext);
        this.displayRequest = displayVODs;
    }

    @Override
    public VODRequestHandler newRequest() {
        super.newRequest();
        if (networkIsAvailable()) {
            new DeleteFileHandler(weakContext, Globals.VOD_PATH).run();
        }
        return this;
    }

    @Override
    public void sendRequest(int offset) {
        this.offset = offset;
        if (networkIsAvailable()) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/kraken/channels/" + userID + "/videos" + "?limit=" + Globals.VOD_REQUEST_LIMIT + "&offset=" + this.offset, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            responseHandler(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorHandler(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    return getRequestHeaders();
                }
                @Override
                protected Response<JSONObject> parseNetworkResponse (NetworkResponse response) {
                    try {
                        String utf8String = new String(response.data, Charset.forName("UTF-8"));
                        
                        return Response.success(new JSONObject(utf8String), HttpHeaderParser.parseCacheHeaders(response));
                    } catch (JSONException e) {
                        // log error
                        return Response.error(new ParseError(e));
                    }
                }
            };
            jsObjRequest.setTag("VOD");
            VolleySingleton.getInstance(weakContext).addToRequestQueue(jsObjRequest);
        } else {
            offlineResponseHandler();
        }
    }

    @Override
    public void responseHandler(JSONObject response) {
        try {
            offset += Globals.VOD_REQUEST_LIMIT;
            if (twitchTotal == 0) {
                twitchTotal = response.getInt("_total");
            }
            itemCount += response.getJSONArray("videos").length();
            vodFileHandler.setResponse(response);
            vodFileHandler.run();
            if (response.getJSONArray("videos").length() > 0) {
                sendRequest(offset);
            } else {
                if (twitchTotal != itemCount && weakContext != null && weakContext.get() != null) {
                    Toast.makeText(weakContext.get(), "Twitch Data for 'VODs' is out of sync. Total should be '" + twitchTotal
                            + "' but is only giving '" + itemCount + "'", Toast.LENGTH_SHORT).show();
                }
                responseAction();
            }
        } catch (JSONException e) {
            if (weakContext != null && weakContext.get() != null) {
                Toast.makeText(weakContext.get(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show();
            }
            new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
        }
    }

    /**
     * Method for performing an action after handling the request response
     * @author LethalMaus
     */
    private void responseAction() {
        if (displayRequest && recyclerView != null && recyclerView.get() != null) {
            recyclerView.get().setAdapter(new VODAdapter(weakActivity, weakContext)
                    .setDisplayPreferences(itemsToDisplay, actionButtonType1));
        }
    }

    @Override
    protected void offlineResponseHandler() {
        if (weakContext != null && weakContext.get() != null) {
            Toast.makeText(weakContext.get(), "OFFLINE: Showing saved items", Toast.LENGTH_SHORT).show();
        }
        responseAction();
    }
}
