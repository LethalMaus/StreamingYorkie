package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Class for getting & changing auto host list
 * @author LethalMaus
 */
public class AutoHostRequestHandler extends RequestHandler {

    @Override
    public String url() {
        return "https://gql.twitch.tv/gql";
    }

    @Override
    public int method() {
        return Request.Method.POST;
    }

    /**
     * Constructor for AutoHostRequestHandler for getting and posting to auto host list
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    public AutoHostRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, final WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
        this.requestType = "AUTOHOST";
    }

    @Override
    HashMap<String, String> getRequestHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/vnd.twitchtv.v5+json");
        headers.put("Client-ID", Globals.TWITCHID);
        headers.put("Content-Type", "application/json; charset=utf-8");
        if (Globals.checkWeakReference(weakContext) && new File(weakContext.get().getFilesDir().toString() + File.separator + "TWITCH_TOKEN").exists()) {
            headers.put("Authorization", "OAuth " + new ReadFileHandler(weakActivity, weakContext,"TWITCH_TOKEN").readFile());
        }
        return headers;
    }
}
