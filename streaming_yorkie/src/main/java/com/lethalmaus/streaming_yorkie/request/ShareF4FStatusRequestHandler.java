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
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

/**
 * Class to share F4F status to Discord
 * @author LethalMaus
 */
public class ShareF4FStatusRequestHandler extends RequestHandler {

    /**
     * Constructor for ShareF4FStatusRequestHandler
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public ShareF4FStatusRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        super(weakActivity, weakContext, null);
    }

    /**
     * Request for sharing F4F status to Discord
     * @author LethalMaus
     * @param postBody JSON POST request body
     */
    public void shareF4FStatus(JSONObject postBody) {
        if (networkIsAvailable()) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://discordapp.com/api/webhooks/591659112442363915/fUX2fXd9naJlf3CSwY_IAUYUndOUTxifyfBQYSV2Qq7-dQhFV8sSf1wRM2rived_6BOy", postBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
                                Toast.makeText(weakActivity.get(), "Shared F4F Status to Discord", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage = error.toString();
                    if (error.networkResponse != null) {
                        errorMessage = error.networkResponse.statusCode + " | " + new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    }
                    new WriteFileHandler(weakContext, "ERROR", null, "Share F4F Status Error response | " + errorMessage, true).run();
                }
            }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    if (response.data != null && response.data.length > 0 && response.statusCode != HttpURLConnection.HTTP_NO_CONTENT) {
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
            jsObjRequest.setTag("SHARE_F4F_STATUS");
            VolleySingleton.getInstance(weakContext).addToRequestQueue(jsObjRequest);
        }
    }
}
