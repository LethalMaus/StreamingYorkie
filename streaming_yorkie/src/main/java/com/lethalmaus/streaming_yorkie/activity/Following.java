package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.request.FollowingUpdateRequestHandler;

import java.lang.ref.WeakReference;

/**
 * Activity for FollowingEntity view that extends FollowParent
 * @author LethalMaus
 */
public class Following extends FollowParent {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.weakActivity = new WeakReference<Activity>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        super.onCreate(savedInstanceState);

        final String daoType = "FOLLOWING";
        final ImageButton newButton = findViewById(R.id.page1);
        newButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageButtonListenerAction(newButton, "New", daoType, "NEW", "EXCLUDE_BUTTON", "NOTIFICATIONS_BUTTON");
                    }
                });

        final ImageButton currentButton = findViewById(R.id.page2);
        currentButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageButtonListenerAction(currentButton, "Current", daoType, "CURRENT", "EXCLUDE_BUTTON", "NOTIFICATIONS_BUTTON");
                    }
                });

        final ImageButton unfollowedButton = findViewById(R.id.page3);
        unfollowedButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageButtonListenerAction(unfollowedButton, "Unfollowed", daoType, "UNFOLLOWED", "DELETE_BUTTON", "EXCLUDE_BUTTON");
                    }
                });

        final ImageButton exclusionsButton = findViewById(R.id.page4);
        exclusionsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageButtonListenerAction(exclusionsButton, "Excluded", daoType, "EXCLUDED", "INCLUDE_BUTTON", "NOTIFICATIONS_BUTTON");
                    }
                });
        pageButtonListenerAction(newButton, "New", daoType, "NEW", "EXCLUDE_BUTTON", "NOTIFICATIONS_BUTTON");
        requestHandler = new FollowingUpdateRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView));
        progressBar.setVisibility(View.VISIBLE);
        requestHandler.initiate().sendRequest();
    }
}