package com.lethalmaus.twitchfollowerservice;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Follow4Follow extends FollowService {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Follow4Follow");

        usersPath = globals.FOLLOWERS_PATH;
        excludedUsersPath = globals.F4F_EXCLUDED_PATH;

        final ImageButton notFollowing_FollowersButton = findViewById(R.id.page1);
        notFollowing_FollowersButton.setImageResource(R.drawable.notfollowing_followers);
        notFollowing_FollowersButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(notFollowing_FollowersButton);
                            usersPath = globals.FOLLOWERS_PATH;
                            displayUsers(newUsers, excludeButtonMethod, followButtonMethod,  null);
                        }
                    }
                });

        final ImageButton follow4FollowButton = findViewById(R.id.page2);
        follow4FollowButton.setImageResource(R.drawable.follow4follow);
        follow4FollowButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(follow4FollowButton);
                            usersPath = globals.FOLLOWING_PATH;
                            displayUsers(currentUsers, excludeButtonMethod, notificationsButtonMethod, followButtonMethod);
                        }
                    }
                });

        final ImageButton following_NonFollowersButton = findViewById(R.id.page3);
        following_NonFollowersButton.setImageResource(R.drawable.following_nonfollowers);
        following_NonFollowersButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(following_NonFollowersButton);
                            usersPath = globals.FOLLOWING_PATH;
                            displayUsers(unfollowedUsers, excludeButtonMethod, notificationsButtonMethod, followButtonMethod);
                        }
                    }
                });

        final ImageButton exclusionsButton = findViewById(R.id.page4);
        exclusionsButton.setImageResource(R.drawable.excluded);
        exclusionsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(exclusionsButton);
                            displayUsers(excludedUsers, includeButtonMethod, notificationsButtonMethod, followButtonMethod);
                        }
                    }
                });
        highlightButton(notFollowing_FollowersButton);
        usersToDisplay = newUsers;
        actionButtonMethod1 = excludeButtonMethod;
        actionButtonMethod2 = followButtonMethod;
        actionButtonMethod3 = null;
        initialExecution();
    }

    @Override
    protected void initialPreparation() {
        offset = 0;
        excludedUsers.clear();
        excludedUsers.addAll(getUserIDs(excludedUsersPath));
        newUsers.clear();
        unfollowedUsers.clear();
    }

    @Override
    protected void initialExecution() {
        initialPreparation();
        requestFollowers(false);
    }

    @Override
    protected void requestFollowersResponseHandler(JSONObject response, boolean recursiveRequest) {
        try {
            offset += globals.REQUEST_LIMIT;
            for (int i = 0; i < response.getJSONArray("follows").length(); i++) {
                JSONObject userObject = new JSONObject();
                userObject.put("display_name", response.getJSONArray("follows").getJSONObject(i).getJSONObject("user").getString("display_name"));
                userObject.put("_id", response.getJSONArray("follows").getJSONObject(i).getJSONObject("user").getString("_id"));
                userObject.put("logo", response.getJSONArray("follows").getJSONObject(i).getJSONObject("user").getString("logo"));
                userObject.put("created_at", response.getJSONArray("follows").getJSONObject(i).getString("created_at"));
                globals.writeToFile(
                        globals.FOLLOWERS_PATH
                                + File.separator
                                + userObject.getString("_id"),
                        userObject.toString());
                newUsers.add(userObject.getString("_id"));
            }
            if (response.getJSONArray("follows").length() > 0) {
                requestFollowers(true);
            } else {
                offset = 0;
                globals.deleteFileOrPath(globals.FOLLOWERS_CURRENT_PATH);
                saveUserIDs(globals.FOLLOWERS_CURRENT_PATH, newUsers);
                requestFollowing(false);
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show();
        } finally {
            requestRunning = false;
        }
    }

    @Override
    protected void requestFollowingResponseHandler(JSONObject response, boolean recursiveRequest) {
        try {
            offset += globals.REQUEST_LIMIT;
            for (int i = 0; i < response.getJSONArray("follows").length(); i++) {
                JSONObject userObject = new JSONObject();
                userObject.put("display_name", response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("display_name"));
                userObject.put("_id", response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("_id"));
                userObject.put("logo", response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("logo"));
                userObject.put("notifications", response.getJSONArray("follows").getJSONObject(i).getBoolean("notifications"));
                userObject.put("created_at", response.getJSONArray("follows").getJSONObject(i).getString("created_at"));
                globals.writeToFile(
                        globals.FOLLOWING_PATH
                                + File.separator
                                + userObject.getString("_id"),
                        userObject.toString());
                unfollowedUsers.add(userObject.getString("_id"));
            }
            if (response.getJSONArray("follows").length() > 0) {
                requestFollowing(true);
            } else {
                globals.deleteFileOrPath(globals.FOLLOWING_CURRENT_PATH);
                saveUserIDs(globals.FOLLOWING_CURRENT_PATH, unfollowedUsers);
                organizeUsers();
                displayUsers(usersToDisplay, actionButtonMethod1, actionButtonMethod2, actionButtonMethod3);
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show();
        } finally {
            requestRunning = false;
        }
    }

    @Override
    protected void requestOfflineHandler() {
        Toast.makeText(getApplicationContext(), "OFFLINE: Showing saved Users", Toast.LENGTH_SHORT).show();
        newUsers.addAll(getUserIDs(globals.FOLLOWERS_CURRENT_PATH));
        unfollowedUsers.addAll(getUserIDs(globals.FOLLOWING_CURRENT_PATH));
        organizeUsers();
        displayUsers(usersToDisplay, actionButtonMethod1, actionButtonMethod2, actionButtonMethod3);
        requestRunning = false;
    }

    @Override
    protected void organizeUsers() {
        newUsers.clear();
        newUsers.addAll(getUserIDs(globals.FOLLOWERS_CURRENT_PATH));
        unfollowedUsers.clear();
        unfollowedUsers.addAll(getUserIDs(globals.FOLLOWING_CURRENT_PATH));
        currentUsers.clear();

        for (int i = 0; i < newUsers.size(); i++) {
            if (unfollowedUsers.contains(newUsers.get(i))) {
                unfollowedUsers.remove(newUsers.get(i));
                currentUsers.add(newUsers.get(i));
            }
        }
        newUsers.removeAll(currentUsers);

        newUsers.removeAll(excludedUsers);
        currentUsers.removeAll(excludedUsers);
        unfollowedUsers.removeAll(excludedUsers);

        setPageCounts(
                newUsers.size(),
                currentUsers.size(),
                unfollowedUsers.size(),
                excludedUsers.size()
        );
    }
}
