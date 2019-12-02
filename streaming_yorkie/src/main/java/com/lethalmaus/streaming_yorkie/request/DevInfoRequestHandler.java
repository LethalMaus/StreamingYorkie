package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Class to retrieve Developers Twitch account logo
 * @author LethalMaus
 */
public class DevInfoRequestHandler extends RequestHandler {

    @Override
    public String url() {
        return "https://api.twitch.tv/kraken/users/188850000";
    }

    @Override
    public int method() {
        return Request.Method.GET;
    }

    /**
     * Constructor for DevInfoRequestHandler
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public DevInfoRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        super(weakActivity, weakContext, null);
        requestType = "DEV";
    }

    @Override
    public void responseHandler(final JSONObject response) {
        try {
            if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
                ImageView developer_Logo = weakActivity.get().findViewById(R.id.info_developer);
                Glide.with(weakActivity.get()).load(response.getString("logo")).into(developer_Logo);
            }
        } catch (JSONException e) {
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "DevInfo response cant be read | " + e.toString(), true).run();
        }
    }
}
