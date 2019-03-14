package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Class to retrieve Developers Twitch account logo
 * @author LethalMaus
 */
public class DevInfoRequestHandler extends RequestHandler {

    /**
     * Constructor for DevInfoRequestHandler
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public DevInfoRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        super(weakActivity, weakContext, null);
    }

    /**
     * Request for retrieving Developers Twitch Logo
     * @author LethalMaus
     */
    public void requestDevLogo() {
        if (networkIsAvailable()) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/kraken/users/188850000", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                ImageView developer_Logo = weakActivity.get().findViewById(R.id.developer_Logo);
                                Glide.with(weakActivity.get()).load(response.getString("logo")).into(developer_Logo);
                            } catch (JSONException e) {
                                new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    new WriteFileHandler(weakContext, "ERROR", null, error.toString() + "\n", true).run();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    return getRequestHeaders();
                }
            };
            jsObjRequest.setTag("DEV");
            VolleySingleton.getInstance(weakContext).addToRequestQueue(jsObjRequest);
        }
    }

    /**
     * Method for opening a link in relation to the developer
     * @author LethalMaus
     * @param url a link to eg. Github
     */
    public void requestDevLink(String url) {
        if (networkIsAvailable()) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                weakActivity.get().startActivity(intent);
            } catch(ActivityNotFoundException e) {
                Toast.makeText(weakContext.get(), "No browser can be found", Toast.LENGTH_SHORT).show();
                new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
            }
        } else {
            Toast.makeText(weakContext.get(), "OFFLINE: Can't open link", Toast.LENGTH_SHORT).show();
        }
    }

}
