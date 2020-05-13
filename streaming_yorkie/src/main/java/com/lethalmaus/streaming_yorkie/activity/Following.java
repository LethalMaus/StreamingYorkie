package com.lethalmaus.streaming_yorkie.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.lethalmaus.streaming_yorkie.Globals;
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
        this.weakActivity = new WeakReference<>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        super.onCreate(savedInstanceState);

        final String daoType = "FOLLOWING";
        findViewById(R.id.page1).setVisibility(View.GONE);
        findViewById(R.id.count1).setVisibility(View.GONE);

        final ImageButton currentButton = findViewById(R.id.page2);
        currentButton.setOnClickListener(View ->
                pageButtonListenerAction(currentButton, "Current", daoType, "CURRENT", Globals.EXCLUDE_BUTTON, Globals.NOTIFICATIONS_BUTTON)
        );
        final ImageButton unfollowedButton = findViewById(R.id.page3);
        unfollowedButton.setOnClickListener(View ->
                pageButtonListenerAction(unfollowedButton, "Unfollowed", daoType, "UNFOLLOWED", Globals.DELETE_BUTTON, Globals.EXCLUDE_BUTTON)
        );
        final ImageButton exclusionsButton = findViewById(R.id.page4);
        exclusionsButton.setOnClickListener(View ->
                pageButtonListenerAction(exclusionsButton, "Excluded", daoType, "EXCLUDED", Globals.INCLUDE_BUTTON, Globals.NOTIFICATIONS_BUTTON)
        );
        requestHandler = new FollowingUpdateRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView));
        pageButtonListenerAction(currentButton, "Current", daoType, "CURRENT", Globals.EXCLUDE_BUTTON, Globals.NOTIFICATIONS_BUTTON);
    }
}