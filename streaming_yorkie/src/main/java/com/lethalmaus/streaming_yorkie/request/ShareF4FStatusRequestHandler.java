package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

/**
 * Class to share F4FEntity status to Discord
 * @author LethalMaus
 */
public class ShareF4FStatusRequestHandler extends RequestHandler {

    @Override
    public String url() {
        System.out.println( new String(Base64.decode("aHR0cHM6Ly9kaXNjb3JkYXBwLmNvbS9hcGkvd2ViaG9va3MvNjA3Mjc1MjA3OTU4MzMxNDYyLzJYWUNpS3BVMWhiWDN0Z0dwUUM1bktOM2VFTUlELWlpbHdnbGU4bGUxRUIwbmhzVXpXX2NkbUlRLTRGNmFvNVVRZ2xF", Base64.DEFAULT)));
        return new String(Base64.decode("aHR0cHM6Ly9kaXNjb3JkYXBwLmNvbS9hcGkvd2ViaG9va3MvNjA3Mjc1MjA3OTU4MzMxNDYyLzJYWUNpS3BVMWhiWDN0Z0dwUUM1bktOM2VFTUlELWlpbHdnbGU4bGUxRUIwbmhzVXpXX2NkbUlRLTRGNmFvNVVRZ2xF", Base64.DEFAULT));
    }

    @Override
    public int method() {
        return Request.Method.POST;
    }

    /**
     * Constructor for ShareF4FStatusRequestHandler
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public ShareF4FStatusRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        super(weakActivity, weakContext, null);
        requestType = "SHARE_F4F";
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread(new Runnable() {
            public void run() {
                if (weakActivity != null && weakActivity.get() != null) {
                    weakActivity.get().runOnUiThread(
                            new Runnable() {
                                public void run() {
                                    Toast.makeText(weakActivity.get(), "Shared F4F Status to Discord", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                }
            }
        }).start();
    }

    @Override
    public Response<JSONObject> parseRequestNetworkResponse(NetworkResponse response, String PROTOCOL_CHARSET) {
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
}
