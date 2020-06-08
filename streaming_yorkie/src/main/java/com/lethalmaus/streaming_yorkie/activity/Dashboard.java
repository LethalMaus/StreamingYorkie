package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lethalmaus.streaming_yorkie.R;

import java.lang.ref.WeakReference;

/**
 * Activity for channel moderation
 * @author LethalMaus
 */
public class Dashboard extends AppCompatActivity {

    //All activities & contexts are weak referenced to avoid memory leaks
    protected WeakReference<Activity> weakActivity;
    protected WeakReference<Context> weakContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weakActivity = new WeakReference<>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        setContentView(R.layout.dashboard);
    }
}
