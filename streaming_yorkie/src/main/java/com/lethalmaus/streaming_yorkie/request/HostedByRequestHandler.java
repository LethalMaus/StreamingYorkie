package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;

import java.lang.ref.WeakReference;

/**
 * Class for getting listed of users who hosts a live stream
 * @author LethalMaus
 */
public class HostedByRequestHandler extends RequestHandler {

    @Override
    public String url() {
        return "https://tmi.twitch.tv/hosts?include_logins=1&target=" + userID;
    }

    @Override
    public int method() {
        return Request.Method.GET;
    }

    /**
     * Constructor for HostedByRequestHandler for getting listed of users who hosts a live stream
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    public HostedByRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, final WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
        this.requestType = "HOSTED_BY";
    }
}
