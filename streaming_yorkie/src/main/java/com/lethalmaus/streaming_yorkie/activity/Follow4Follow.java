package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.adapter.UserAdapter;
import com.lethalmaus.streaming_yorkie.request.FollowersRequestHandler;
import com.lethalmaus.streaming_yorkie.request.FollowingRequestHandler;

import java.lang.ref.WeakReference;

/**
 * Activity for F4F view that extends FollowParent. Requests both Followers & Following anew
 * @author LethalMaus
 */
public class Follow4Follow extends FollowParent {

    private FollowingRequestHandler followingRequestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.weakActivity = new WeakReference<Activity>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        super.onCreate(savedInstanceState);

        currentUsersPath = Globals.F4F_FOLLOW4FOLLOW_PATH;
        newUsersPath = Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH;
        unfollowedUsersPath = Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH;
        excludedUsersPath = Globals.F4F_EXCLUDED_PATH;
        usersPath = Globals.FOLLOWERS_PATH;

        final ImageButton notFollowing_FollowersButton = findViewById(R.id.page1);
        notFollowing_FollowersButton.setImageResource(R.drawable.notfollowing_followers);
        notFollowing_FollowersButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (progressBar.getVisibility() != View.VISIBLE) {
                            usersPath = Globals.FOLLOWERS_PATH;
                            followingRequestHandler.setDisplayPreferences("FOLLOWED_NOTFOLLOWING", "EXCLUDE_BUTTON", null, "FOLLOW_BUTTON");
                            pageButtonListenerAction(notFollowing_FollowersButton, "Followers not Followed", "FOLLOWED_NOTFOLLOWING", "EXCLUDE_BUTTON", null);
                        }
                    }
                });

        final ImageButton follow4FollowButton = findViewById(R.id.page2);
        follow4FollowButton.setImageResource(R.drawable.follow4follow);
        follow4FollowButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (progressBar.getVisibility() != View.VISIBLE) {
                            usersPath = Globals.FOLLOWERS_PATH;
                            followingRequestHandler.setDisplayPreferences("FOLLOW4FOLLOW", "EXCLUDE_BUTTON", "NOTIFICATIONS_BUTTON","FOLLOW_BUTTON");
                            pageButtonListenerAction(follow4FollowButton, "Followers being Followed", "FOLLOW4FOLLOW", "EXCLUDE_BUTTON", "NOTIFICATIONS_BUTTON");
                        }
                    }
                });

        final ImageButton following_NonFollowersButton = findViewById(R.id.page3);
        following_NonFollowersButton.setImageResource(R.drawable.following_nonfollowers);
        following_NonFollowersButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (progressBar.getVisibility() != View.VISIBLE) {
                            usersPath = Globals.FOLLOWING_PATH;
                            followingRequestHandler.setDisplayPreferences("NOTFOLLOWED_FOLLOWING", "EXCLUDE_BUTTON", "NOTIFICATIONS_BUTTON","FOLLOW_BUTTON");
                            pageButtonListenerAction(following_NonFollowersButton, "Following not Following back", "NOTFOLLOWED_FOLLOWING", "EXCLUDE_BUTTON", "NOTIFICATIONS_BUTTON");
                        }
                    }
                });

        final ImageButton exclusionsButton = findViewById(R.id.page4);
        exclusionsButton.setImageResource(R.drawable.excluded);
        exclusionsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (progressBar.getVisibility() != View.VISIBLE) {
                            followingRequestHandler.setDisplayPreferences("F4F_EXCLUDED", "INCLUDE_BUTTON", "NOTIFICATIONS_BUTTON","FOLLOW_BUTTON");
                            pageButtonListenerAction(exclusionsButton, "Excluded", "F4F_EXCLUDED", "INCLUDE_BUTTON", "NOTIFICATIONS_BUTTON");
                        }
                    }
                });
        requestHandler = new FollowersRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView) ,false, true) {
            @Override
            protected void responseAction() {
                super.responseAction();
                followingRequestHandler.newRequest().sendRequest(0);
            }
            @Override
            protected void offlineResponseHandler() {
                followingRequestHandler.newRequest().sendRequest(0);
            }
        };
        followingRequestHandler = new FollowingRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView), true, false) {
            @Override
            protected void offlineResponseHandler() {
                Toast.makeText(weakContext.get(), "OFFLINE: Showing saved Users", Toast.LENGTH_SHORT).show();
                recyclerView.setAdapter(new UserAdapter(weakActivity, weakContext)
                        .setPaths(newUsersPath, currentUsersPath, unfollowedUsersPath, excludedUsersPath, usersPath)
                        .setDisplayPreferences(usersToDisplay, actionButtonType1, actionButtonType2, actionButtonType3));
            }
        };
        followingRequestHandler.setDisplayPreferences("FOLLOWED_NOTFOLLOWING", "EXCLUDE_BUTTON", "FOLLOW_BUTTON", null);
        highlightButton(notFollowing_FollowersButton);
        setSubtitle("Followers not being Followed");
        progressBar.setVisibility(View.VISIBLE);
        requestHandler.newRequest().sendRequest(0);
    }
}
