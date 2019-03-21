package com.lethalmaus.streaming_yorkie.request;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.lang.ref.WeakReference;

/**
 * Class for sending Volley Requests
 * @author LethalMaus
 */
public class VolleySingleton {

    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static WeakReference<Context> weakContext;

    /**
     * Constructor for VolleySingleton for sending Volley Requests
     * @author LethalMaus
     * @param newWeakContext weak referenced context
     */
    private VolleySingleton(WeakReference<Context> newWeakContext) {
        weakContext = newWeakContext;
        mRequestQueue = getRequestQueue();
    }

    /**
     * Method for getting a synchronized VolleySingleton Instance
     * @author LethalMaus
     * @param weakContext weak referenced context
     * @return synchronized VolleySingleton Instance
     */
    public static synchronized VolleySingleton getInstance(WeakReference<Context> weakContext) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(weakContext);
        }
        return mInstance;
    }

    /**
     * Method for requesting RequestQueue
     * @author LethalMaus
     * @return newRequestQueue or null
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null && weakContext != null && weakContext.get() != null) {
            mRequestQueue = Volley.newRequestQueue(weakContext.get().getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Method for adding a request to the RequestQueue
     * @author LethalMaus
     * @param request the volley http request
     * @param <T> the request type
     */
    <T> void addToRequestQueue(Request<T> request) {
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 0));
        getRequestQueue().add(request);
    }
}
