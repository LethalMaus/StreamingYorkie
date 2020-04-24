package com.lethalmaus.streaming_yorkie.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.request.FollowersUpdateRequestHandler;

import java.lang.ref.WeakReference;

/**
 * Activity for Followers view that extends FollowParent
 * @author LethalMaus
 */
public class Followers extends FollowParent {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.weakActivity = new WeakReference<>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        super.onCreate(savedInstanceState);

        final String daoType = "FOLLOWERS";
        final ImageButton newButton = findViewById(R.id.page1);
        newButton.setOnClickListener((View v) ->
                pageButtonListenerAction(newButton, "New", daoType, "NEW", Globals.EXCLUDE_BUTTON, Globals.NOTIFICATIONS_BUTTON)
        );
        final ImageButton currentButton = findViewById(R.id.page2);
        currentButton.setOnClickListener((View v) ->
                pageButtonListenerAction(currentButton, "Current", daoType,"CURRENT", Globals.EXCLUDE_BUTTON, Globals.NOTIFICATIONS_BUTTON)
        );
        final ImageButton unfollowedButton = findViewById(R.id.page3);
        unfollowedButton.setOnClickListener((View v) ->
                pageButtonListenerAction(unfollowedButton, "Unfollowed", daoType,"UNFOLLOWED", Globals.DELETE_BUTTON, Globals.EXCLUDE_BUTTON)
        );
        final ImageButton exclusionsButton = findViewById(R.id.page4);
        exclusionsButton.setOnClickListener((View v) ->
                pageButtonListenerAction(exclusionsButton, "Excluded", daoType,"EXCLUDED", Globals.INCLUDE_BUTTON, Globals.NOTIFICATIONS_BUTTON)
        );
        pageButtonListenerAction(newButton, "New", daoType, "NEW", Globals.EXCLUDE_BUTTON, Globals.NOTIFICATIONS_BUTTON);
        requestHandler = new FollowersUpdateRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView));
        progressBar.setVisibility(View.VISIBLE);
        requestHandler.initiate().sendRequest();
    }
}