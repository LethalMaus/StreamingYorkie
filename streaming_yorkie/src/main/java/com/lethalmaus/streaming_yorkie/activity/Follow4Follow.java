package com.lethalmaus.streaming_yorkie.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.request.FollowersUpdateRequestHandler;
import com.lethalmaus.streaming_yorkie.request.FollowingUpdateRequestHandler;

import java.lang.ref.WeakReference;

/**
 * Activity for F4FEntity view that extends FollowParent. Requests both Followers & FollowingEntity anew
 * @author LethalMaus
 */
public class Follow4Follow extends FollowParent {

    private FollowingUpdateRequestHandler followingUpdateRequestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.weakActivity = new WeakReference<>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        super.onCreate(savedInstanceState);

        final String daoType = "F4FEntity";
        final ImageButton notFollowing_FollowersButton = findViewById(R.id.page1);
        notFollowing_FollowersButton.setImageResource(R.drawable.notfollowing_followers);
        notFollowing_FollowersButton.setOnClickListener((View v) ->
                pageButtonListenerAction(notFollowing_FollowersButton, "Followers not Followed", daoType, "FOLLOWED_NOTFOLLOWING", "EXCLUDE_BUTTON", null)
        );

        final ImageButton follow4FollowButton = findViewById(R.id.page2);
        follow4FollowButton.setImageResource(R.drawable.follow4follow);
        follow4FollowButton.setOnClickListener((View v) ->
                pageButtonListenerAction(follow4FollowButton, "Followers being Followed", daoType, "FOLLOW4FOLLOW", "EXCLUDE_BUTTON", "NOTIFICATIONS_BUTTON")
        );

        final ImageButton following_NonFollowersButton = findViewById(R.id.page3);
        following_NonFollowersButton.setImageResource(R.drawable.following_nonfollowers);
        following_NonFollowersButton.setOnClickListener((View v) ->
                pageButtonListenerAction(following_NonFollowersButton, "FollowingEntity not FollowingEntity back", daoType, "NOTFOLLOWED_FOLLOWING", "EXCLUDE_BUTTON", "NOTIFICATIONS_BUTTON")
        );

        final ImageButton exclusionsButton = findViewById(R.id.page4);
        exclusionsButton.setImageResource(R.drawable.excluded);
        exclusionsButton.setOnClickListener((View v) ->
                pageButtonListenerAction(exclusionsButton, "Excluded", daoType, "EXCLUDED", "INCLUDE_BUTTON", "NOTIFICATIONS_BUTTON")
        );
        pageButtonListenerAction(notFollowing_FollowersButton, "Followers not Followed", daoType, "FOLLOWED_NOTFOLLOWING", "EXCLUDE_BUTTON", null);
        followingUpdateRequestHandler = new FollowingUpdateRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView));
        requestHandler = new FollowersUpdateRequestHandler(weakActivity, weakContext, null) {
            @Override
            public void onCompletion() {
                super.onCompletion();
                runOnUiThread(() ->
                        progressBar.setVisibility(View.VISIBLE)
                );
                followingUpdateRequestHandler.initiate().sendRequest();
            }
        };
        progressBar.setVisibility(View.VISIBLE);
        requestHandler.initiate().sendRequest();
    }
}
