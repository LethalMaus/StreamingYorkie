package com.lethalmaus.streaming_yorkie.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.adapter.UserAdapter;
import com.lethalmaus.streaming_yorkie.request.FollowersUpdateRequestHandler;
import com.lethalmaus.streaming_yorkie.request.FollowingUpdateRequestHandler;

import java.lang.ref.WeakReference;

/**
 * Activity for F4FEntity view that extends FollowParent. Requests both Followers & Following anew
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
        findViewById(R.id.count1).setVisibility(View.VISIBLE);
        final ImageButton notFollowingFollowersButton = findViewById(R.id.page1);
        notFollowingFollowersButton.setVisibility(View.VISIBLE);
        notFollowingFollowersButton.setImageResource(R.drawable.notfollowing_followers);
        notFollowingFollowersButton.setOnClickListener((View v) ->
                pageButtonListenerAction(notFollowingFollowersButton, "Followers not Followed", daoType, "FOLLOWED_NOTFOLLOWING", Globals.EXCLUDE_BUTTON, null)
        );

        final ImageButton follow4FollowButton = findViewById(R.id.page2);
        follow4FollowButton.setImageResource(R.drawable.follow4follow);
        follow4FollowButton.setOnClickListener((View v) ->
                pageButtonListenerAction(follow4FollowButton, "Followers being Followed", daoType, "FOLLOW4FOLLOW", Globals.EXCLUDE_BUTTON, Globals.NOTIFICATIONS_BUTTON)
        );

        final ImageButton followingNonFollowersButton = findViewById(R.id.page3);
        followingNonFollowersButton.setImageResource(R.drawable.following_nonfollowers);
        followingNonFollowersButton.setOnClickListener((View v) ->
                pageButtonListenerAction(followingNonFollowersButton, "Following not Following back", daoType, "NOTFOLLOWED_FOLLOWING", Globals.EXCLUDE_BUTTON, Globals.NOTIFICATIONS_BUTTON)
        );

        final ImageButton exclusionsButton = findViewById(R.id.page4);
        exclusionsButton.setImageResource(R.drawable.excluded);
        exclusionsButton.setOnClickListener((View v) ->
                pageButtonListenerAction(exclusionsButton, "Excluded", daoType, "EXCLUDED", Globals.INCLUDE_BUTTON, Globals.NOTIFICATIONS_BUTTON)
        );
        followingUpdateRequestHandler = new FollowingUpdateRequestHandler(weakActivity, weakContext, null) {
            @Override
            public void onCompletion(boolean hideProgressBar) {
                super.onCompletion(true);
                if (Globals.checkWeakActivity(weakActivity) && recyclerView != null) {
                    final UserAdapter userAdapter = (UserAdapter) recyclerView.getAdapter();
                    if (userAdapter != null) {
                        weakActivity.get().runOnUiThread(() -> {
                            recyclerView.stopScroll();
                            recyclerView.scrollToPosition(0);
                            recyclerView.getRecycledViewPool().clear();
                            recyclerView.post(userAdapter::datasetChanged);
                        });
                    }
                }
            }
        };
        requestHandler = new FollowersUpdateRequestHandler(weakActivity, weakContext, null) {
            @Override
            public void onCompletion(boolean hideProgressBar) {
                super.onCompletion(false);
                followingUpdateRequestHandler.initiate().sendRequest(true);
            }
        };
        pageButtonListenerAction(notFollowingFollowersButton, "Followers not Followed", daoType, "FOLLOWED_NOTFOLLOWING", Globals.EXCLUDE_BUTTON, null);
    }
}
