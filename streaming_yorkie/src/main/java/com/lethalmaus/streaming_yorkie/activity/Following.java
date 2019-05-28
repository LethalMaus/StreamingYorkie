package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.request.FollowingRequestHandler;

import java.lang.ref.WeakReference;

/**
 * Activity for Following view that extends FollowParent
 * @author LethalMaus
 */
public class Following extends FollowParent {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.weakActivity = new WeakReference<Activity>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        super.onCreate(savedInstanceState);

        usersPath = Globals.FOLLOWING_PATH;
        requestPath = Globals.FOLLOWING_REQUEST_PATH;
        currentUsersPath = Globals.FOLLOWING_CURRENT_PATH;
        newUsersPath = Globals.FOLLOWING_NEW_PATH;
        unfollowedUsersPath = Globals.FOLLOWING_UNFOLLOWED_PATH;
        excludedUsersPath = Globals.FOLLOWING_EXCLUDED_PATH;

        final ImageButton newButton = findViewById(R.id.page1);
        newButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageButtonListenerAction(newButton, "New", "NEW", "EXCLUDE_BUTTON", "NOTIFICATIONS_BUTTON");
                    }
                });

        final ImageButton currentButton = findViewById(R.id.page2);
        currentButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageButtonListenerAction(currentButton, "Current", "CURRENT", "EXCLUDE_BUTTON", "NOTIFICATIONS_BUTTON");
                    }
                });

        final ImageButton unfollowedButton = findViewById(R.id.page3);
        unfollowedButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageButtonListenerAction(unfollowedButton, "Unfollowed", "UNFOLLOWED", "DELETE_BUTTON", "EXCLUDE_BUTTON");
                    }
                });

        final ImageButton exclusionsButton = findViewById(R.id.page4);
        exclusionsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageButtonListenerAction(exclusionsButton, "Excluded", "EXCLUDED", "INCLUDE_BUTTON", "NOTIFICATIONS_BUTTON");
                    }
                });
        highlightButton(newButton);
        setSubtitle("New");
        requestHandler = new FollowingRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView),true)
                .setPaths(currentUsersPath, newUsersPath, unfollowedUsersPath, excludedUsersPath, requestPath, usersPath)
                .setDisplayPreferences("NEW", "EXCLUDE_BUTTON", "NOTIFICATIONS_BUTTON", "FOLLOW_BUTTON");
        progressBar.setVisibility(View.VISIBLE);
        requestHandler.newRequest().sendRequest(0);
    }
}