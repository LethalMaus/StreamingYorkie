package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.adapter.VODAdapter;
import com.lethalmaus.streaming_yorkie.entity.VODEntity;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.HashMap;

/**
 * Class for exporting VODs
 * @author LethalMaus
 */
public class VODExportRequestHandler extends RequestHandler {

    @Override
    public String url() {
        return "https://api.twitch.tv/kraken/videos/" + vodId + "/youtube_export";
    }

    @Override
    public int method() {
        return Request.Method.POST;
    }

    /**
     * Constructor for VODExportRequestHandler for exporting a current VODEntity
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recyclerView
     */
    public VODExportRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
        requestType = "VOD_EXPORT";
    }

    private int vodId;

    /**
     * Sets the VodId
     * @author LethalMaus
     * @param vodId int
     * @return instance of itself for method building
     */
    public VODExportRequestHandler setVodId(int vodId) {
        this.vodId = vodId;
        return this;
    }

    /**
     * Gets the VodId
     * @author LethalMaus
     * @return int vodId
     */
    protected int getVodId() {
        return vodId;
    }

    @Override
    public void responseHandler(JSONObject response) {
        new Thread(new Runnable() {
            public void run() {
                VODEntity vodEntity = streamingYorkieDB.vodDAO().getVODById(vodId);
                final String title = vodEntity.getTitle();
                if (weakActivity != null && weakActivity.get() != null) {
                    weakActivity.get().runOnUiThread(
                            new Runnable() {
                                public void run() {
                                    Toast.makeText(weakActivity.get(), "Export successful for '" + title + "'", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                }
                vodEntity.setExported(true);
                streamingYorkieDB.vodDAO().updateVOD(vodEntity);
                if (recyclerView != null && recyclerView.get() != null && recyclerView.get().getAdapter() != null) {
                    final VODAdapter vodAdapter = (VODAdapter) recyclerView.get().getAdapter();
                    if (vodAdapter != null && weakActivity != null && weakActivity.get() != null) {
                        vodAdapter.setPageCounts();
                        weakActivity.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                vodAdapter.datasetChanged();
                            }
                        });
                    }
                }
                onCompletion(true);
            }
        }).start();
    }

    @Override
    HashMap<String, String> getRequestHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/vnd.twitchtv.v5+json");
        headers.put("Client-ID", Globals.TWITCHID);
        headers.put("Content-Type", "application/json");
        if (weakContext != null && weakContext.get() != null && new File(weakContext.get().getFilesDir().toString() + File.separator + "TWITCH_TOKEN").exists()) {
            headers.put("Authorization", "OAuth " + new ReadFileHandler(weakActivity, weakContext,"TWITCH_TOKEN").readFile());
        }
        return headers;
    }

    @Override
    public Response<JSONObject> parseRequestNetworkResponse(NetworkResponse response, String PROTOCOL_CHARSET) {
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

    @Override
    protected void offlineResponseHandler() {
        if (weakActivity.get() != null) {
            weakActivity.get().runOnUiThread(
                    new Runnable() {
                        public void run() {
                            Toast.makeText(weakActivity.get(), "OFFLINE: Cannot export when offline", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }
}
