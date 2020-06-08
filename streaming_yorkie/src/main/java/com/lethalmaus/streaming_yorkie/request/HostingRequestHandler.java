package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;

import java.lang.ref.WeakReference;

/**
 * Class for getting current hosted stream
 * @author LethalMaus
 */
public class HostingRequestHandler extends RequestHandler {

    @Override
    public String url() {
        return "https://tmi.twitch.tv/hosts?include_logins=1&host=" + userID;
    }

    @Override
    public int method() {
        return Request.Method.GET;
    }

    /**
     * Constructor for HostedByRequestHandler for getting current hosted stream
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recycler view
     */
    public HostingRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, final WeakReference<RecyclerView> recyclerView) {
        super(weakActivity, weakContext, recyclerView);
        this.requestType = "HOSTING";
    }
}
