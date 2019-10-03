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
import com.lethalmaus.streaming_yorkie.adapter.VODAdapter;
import com.lethalmaus.streaming_yorkie.entity.VODEntity;

import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.nio.charset.Charset;

/**
 * Class for requesting VODs
 * @author LethalMaus
 */
public class VODRequestHandler extends RequestHandler {

    @Override
    public String url() {
        return "https://api.twitch.tv/kraken/channels/" + userID + "/videos" + "?limit=" + Globals.VOD_REQUEST_LIMIT + "&offset=" + this.offset;
    }

    @Override
    public int method() {
        return Request.Method.GET;
    }

    /**
     * Constructor for VODRequestHandler for requesting current VODs
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    VODRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
        requestType = "VODEntity";
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    offset += Globals.VOD_REQUEST_LIMIT;
                    if (twitchTotal == 0) {
                        twitchTotal = response.getInt("_total");
                    }
                    itemCount += response.getJSONArray("videos").length();
                    if (response.has("videos") && response.getJSONArray("videos").length() > 0) {
                        for (int i = 0; i < response.getJSONArray("videos").length(); i++) {
                            if (!response.getJSONArray("videos").getJSONObject(i).getString("status").contentEquals("recording")) {
                                String length = "";
                                int seconds = response.getJSONArray("videos").getJSONObject(i).getInt("length");
                                length += (seconds / 3600) + "h ";
                                length += ((seconds % 3600) / 60) + "m ";
                                length += ((seconds % 3600) % 60) + "s";
                                VODEntity vodEntity = new VODEntity(Integer.parseInt(response.getJSONArray("videos").getJSONObject(i).getString("_id").replace("v", "")),
                                        response.getJSONArray("videos").getJSONObject(i).getString("title"),
                                        response.getJSONArray("videos").getJSONObject(i).getString("url"),
                                        response.getJSONArray("videos").getJSONObject(i).getString("created_at").replace("T", " ").replace("Z", ""),
                                        length,
                                        response.getJSONArray("videos").getJSONObject(i).getJSONObject("preview").getString("medium"),
                                        timestamp);
                                if (response.getJSONArray("videos").getJSONObject(i).isNull("description")) {
                                    vodEntity.setDescription("");
                                } else {
                                    vodEntity.setDescription(response.getJSONArray("videos").getJSONObject(i).getString("description"));
                                }
                                if (response.getJSONArray("videos").getJSONObject(i).isNull("tag_list")) {
                                    vodEntity.setTag_list("");
                                } else {
                                    vodEntity.setTag_list(response.getJSONArray("videos").getJSONObject(i).getString("tag_list"));
                                }
                                if (response.getJSONArray("videos").getJSONObject(i).isNull("game")) {
                                    vodEntity.setGame("");
                                } else {
                                    vodEntity.setGame(response.getJSONArray("videos").getJSONObject(i).getString("game"));
                                }
                                VODEntity existingVODEntity = streamingYorkieDB.vodDAO().getVODById(vodEntity.getId());
                                if (existingVODEntity != null) {
                                    vodEntity.setExported(existingVODEntity.isExported());
                                    vodEntity.setExcluded(existingVODEntity.isExcluded());
                                    streamingYorkieDB.vodDAO().updateVOD(vodEntity);
                                } else {
                                    vodEntity.setExcluded(false);
                                    vodEntity.setExported(false);
                                    streamingYorkieDB.vodDAO().insertVOD(vodEntity);
                                }
                            }
                        }
                        sendRequest();
                    } else {
                        if (twitchTotal != itemCount && weakActivity != null && weakActivity.get() != null) {
                            weakActivity.get().runOnUiThread(
                                    new Runnable() {
                                        public void run() {
                                            Toast.makeText(weakActivity.get(), "Twitch Data for 'VODs' is out of sync. Total should be '" + twitchTotal
                                                    + "' but is only giving '" + itemCount + "'", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                        }
                        int[] expiredVODs = streamingYorkieDB.vodDAO().getExpiredVODs(timestamp);
                        for (int expiredVOD : expiredVODs) {
                            streamingYorkieDB.vodDAO().deleteVODById(expiredVOD);
                        }
                        if (recyclerView != null && recyclerView.get() != null && recyclerView.get().getAdapter() != null) {
                            final VODAdapter vodAdapter = (VODAdapter) recyclerView.get().getAdapter();
                            if (vodAdapter != null && weakActivity != null && weakActivity.get() != null) {
                                weakActivity.get().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerView.get().stopScroll();
                                        recyclerView.get().scrollToPosition(0);
                                        recyclerView.get().getRecycledViewPool().clear();
                                        vodAdapter.datasetChanged();
                                    }
                                });
                            }
                        }
                        if (weakActivity != null && weakActivity.get() != null) {
                            weakActivity.get().runOnUiThread(new Runnable() {
                                public void run() {
                                    weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                        onCompletion();
                    }
                } catch (JSONException e) {
                    if (twitchTotal != itemCount && weakActivity != null && weakActivity.get() != null) {
                        weakActivity.get().runOnUiThread(
                                new Runnable() {
                                    public void run() {
                                        Toast.makeText(weakActivity.get(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    new WriteFileHandler(weakContext, "ERROR", null, "Error reading VODEntity response | " + e.toString(), true).run();
                }
            }
        }).start();
    }

    @Override
    public Response<JSONObject> parseRequestNetworkResponse(NetworkResponse response, String PROTOCOL_CHARSET) {
        try {
            String utf8String = new String(response.data, Charset.forName("UTF-8"));
            return Response.success(new JSONObject(utf8String), HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }
}
