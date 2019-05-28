package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for exporting VODs
 * @author LethalMaus
 */
public class VODExportRequestHandler extends RequestHandler {

    private String vodID;
    private String title;

    /**
     * Constructor for VODExportRequestHandler for exporting a current VOD
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public VODExportRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        super(weakActivity, weakContext, null);
    }

    /**
     * Exports VOD to Youtube using the undocumented Twitch APIv4
     * @author LethalMaus
     * @param vodID VOD ID
     * @param title title of VOD
     * @param description description of VOD
     * @param tags tags for VOD
     * @param publish if VOD should be private(false) or public(true)
     * @param split if the video should be split
     */
    public void export(String vodID, String title, String description, String tags, boolean publish, boolean split) {
        if (networkIsAvailable()) {
            this.vodID = vodID;
            this.title = title;
            try {
                JSONObject params = new JSONObject();
                params.put("title", title);
                params.put("description", description);
                params.put("tag_list", tags);
                params.put("private", !publish);
                params.put("do_split", split);
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://api.twitch.tv/kraken/videos/" + vodID + "/youtube_export", params,
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
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Accept", "application/vnd.twitchtv.v5+json");
                        headers.put("Client-ID", Globals.TWITCHID);
                        headers.put("Content-Type", "application/json");
                        if (weakContext != null && weakContext.get() != null && new File(weakContext.get().getFilesDir().toString() + File.separator + "TWITCH_TOKEN").exists()) {
                            headers.put("Authorization", "OAuth " + new ReadFileHandler(weakContext,"TWITCH_TOKEN").readFile());
                        }
                        return headers;
                    }
                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        if (response.data != null && response.data.length > 0 && response.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
                            try {
                                String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                                return Response.success(
                                        new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
                            } catch (UnsupportedEncodingException e) {
                                return Response.error(new ParseError(e));
                            } catch (JSONException je) {
                                return Response.error(new ParseError(je));
                            }
                        } else {
                            return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                        }
                    }
                };
                jsObjRequest.setTag("VOD_EXPORT");
                VolleySingleton.getInstance(weakContext).addToRequestQueue(jsObjRequest);
            } catch (JSONException e) {
                if (weakActivity != null && weakActivity.get() != null) {
                    Toast.makeText(weakActivity.get(), "Twitch export could not be sent.", Toast.LENGTH_SHORT).show();
                }
                new WriteFileHandler(weakContext, "ERROR", null, "Twitch export could not be sent | " + e.toString(), true).run();
            }
        } else if (weakActivity != null && weakActivity.get() != null) {
            Toast.makeText(weakActivity.get(), "OFFLINE: Cannot export when offline", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void responseHandler(JSONObject response) {
        if (weakActivity != null && weakActivity.get() != null) {
            Toast.makeText(weakActivity.get(), "Export successful for '" + title + "'", Toast.LENGTH_SHORT).show();
        }
        new WriteFileHandler(weakContext, Globals.VOD_EXPORTED_PATH + File.separator + vodID, null, null, false).run();
    }
}
