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
     * Class to share to Discord if a purchase has been made
     * @author LethalMaus
     */
    public class PurchaseMadeRequestHandler extends RequestHandler {

        @Override
        public String url() {
            return new String(Base64.decode("aHR0cHM6Ly9kaXNjb3JkYXBwLmNvbS9hcGkvd2ViaG9va3MvNzAxNDAzNDEwNTQ4Nzg1MjMzLzl0eFRvM3VkMUQ5WVJ1WDA5N3hBS1daU3UyQWMxNk1Wd0VsN2F2R0JZOGFnU3JSTDN2VkRySDBoZ2hVaGw3ejhIbzdh", Base64.DEFAULT));
        }

        @Override
        public int method() {
            return Request.Method.POST;
        }

        /**
         * Constructor for PurchaseMadeRequestHandler
         * @author LethalMaus
         * @param weakActivity weak referenced activity
         * @param weakContext weak referenced context
         */
        public PurchaseMadeRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
            super(weakActivity, weakContext, null);
            requestType = "PURCHASE_MADE";
        }

        @Override
        public void responseHandler(final JSONObject response) {
            new Thread(() -> {
                if (weakActivity != null && weakActivity.get() != null) {
                    weakActivity.get().runOnUiThread(
                            () -> Toast.makeText(weakActivity.get(), "Purchase successful", Toast.LENGTH_SHORT).show()
                    );
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
                } catch (JSONException | UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            } else {
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
            }
        }
    }

