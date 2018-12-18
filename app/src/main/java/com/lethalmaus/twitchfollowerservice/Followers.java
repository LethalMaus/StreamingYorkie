package com.lethalmaus.twitchfollowerservice;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Followers extends FollowService {

    private ArrayList<String> followers = new ArrayList<>();
    private ArrayList<String> previousFollowers = new ArrayList<>();
    private ArrayList<String> currentFollowers = new ArrayList<>();
    private ArrayList<String> newFollowers = new ArrayList<>();
    private ArrayList<String> unfollowers = new ArrayList<>();
    private ArrayList<String> exclusions = new ArrayList<>();

    private ArrayList<String> usersToDisplay;
    private boolean includeExcludeButton;
    private boolean includeDeleteButton;
    private boolean includeRemoveFromExcludedButton;

    //TODO
    // add follow / unfollow buttons
    //DRY & Refactor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Followers");

        ImageButton refreshPage = findViewById(R.id.refresh);
        refreshPage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VolleySingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll("FOLLOWERS");
                        followerProcess();
                    }
                });

        final ImageButton newButton = findViewById(R.id.page1);
        newButton.setImageResource(R.drawable.new_button);
        newButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(newButton);
                            showFollowers(newFollowers, true, false, false);
                        }
                    }
                });

        final ImageButton currentButton = findViewById(R.id.page2);
        currentButton.setImageResource(R.drawable.follow);
        currentButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(currentButton);
                            showFollowers(currentFollowers, true, false, false);
                        }
                    }
                });

        final ImageButton unfollowedButton = findViewById(R.id.page3);
        unfollowedButton.setImageResource(R.drawable.unfollow);
        unfollowedButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            if (!requestRunning) {
                                highlightButton(unfollowedButton);
                                showFollowers(unfollowers, true, true, false);
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
                            showFollowers(exclusions, false, false, true);
                        }
                    }
                });
        highlightButton(newButton);
        usersToDisplay = newFollowers;
        includeExcludeButton = true;
        includeDeleteButton = false;
        includeRemoveFromExcludedButton = false;
        followerProcess();
    }

    private void followerProcess() {
        twitchTotal = 0;
        offset = 0;
        previousFollowers.clear();
        previousFollowers.addAll(getUserIDs(globals.FOLLOWERS_PATH));
        exclusions.clear();
        exclusions.addAll(getUserIDs(globals.FOLLOWERS_EXCLUDED_PATH));
        followers.clear();
        requestFollowers();
    }

    private void requestFollowers() {
        if (!requestRunning) {
            requestRunning = true;
            progressBar.setVisibility(View.VISIBLE);
            if (globals.isNetworkAvailable((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))) {
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/kraken/channels/" + globals.userID + "/follows" + "?limit=" + limit + "&direction=asc&offset=" + offset, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    offset += limit;
                                    if (twitchTotal == 0) {
                                        twitchTotal = response.getInt("_total");
                                    }
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
                                                userObject.toString(),
                                                getApplicationContext());
                                        followers.add(userObject.getString("_id"));
                                    }
                                    if (response.getJSONArray("follows").length() > 0) {
                                        requestRunning = false;
                                        requestFollowers();
                                    } else {
                                        if (twitchTotal != followers.size()) {
                                            Toast.makeText(getApplicationContext(), "Twitch Data for 'Followers' is out of sync. Total should be '" + twitchTotal
                                                    + "' but is only giving '" + followers.size() + "'", Toast.LENGTH_SHORT).show();
                                        }
                                        organizeUsers(followers,
                                                previousFollowers,
                                                newFollowers,
                                                globals.FOLLOWERS_NEW_PATH,
                                                currentFollowers,
                                                globals.FOLLOWERS_CURRENT_PATH,
                                                unfollowers,
                                                globals.FOLLOWERS_UNFOLLOWED_PATH,
                                                exclusions);
                                        showFollowers(usersToDisplay, includeExcludeButton, includeDeleteButton, includeRemoveFromExcludedButton);
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show();
                                } finally {
                                    requestRunning = false;
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error requesting Followers", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "OFFLINE: Showing saved followers", Toast.LENGTH_SHORT).show();
                        followers.addAll(getUserIDs(globals.FOLLOWERS_PATH));
                        organizeUsers(followers,
                                previousFollowers,
                                newFollowers,
                                globals.FOLLOWERS_NEW_PATH,
                                currentFollowers,
                                globals.FOLLOWERS_CURRENT_PATH,
                                unfollowers,
                                globals.FOLLOWERS_UNFOLLOWED_PATH,
                                exclusions);
                        showFollowers(usersToDisplay, includeExcludeButton, includeDeleteButton, includeRemoveFromExcludedButton);
                        requestRunning = false;
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Accept", "application/vnd.twitchtv.v5+json");
                        headers.put("Client-ID", globals.CLIENTID);
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };
                jsObjRequest.setTag("FOLLOWERS");
                VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
            } else {
                Toast.makeText(getApplicationContext(), "OFFLINE: Showing saved followers", Toast.LENGTH_SHORT).show();
                followers.addAll(getUserIDs(globals.FOLLOWERS_PATH));
                organizeUsers(followers,
                        previousFollowers,
                        newFollowers,
                        globals.FOLLOWERS_NEW_PATH,
                        currentFollowers,
                        globals.FOLLOWERS_CURRENT_PATH,
                        unfollowers,
                        globals.FOLLOWERS_UNFOLLOWED_PATH,
                        exclusions);
                showFollowers(usersToDisplay, includeExcludeButton, includeDeleteButton, includeRemoveFromExcludedButton);
                requestRunning = false;
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void showFollowers(ArrayList<String> users, boolean excludeButton, boolean deleteButton, boolean removeFormExcludedButton) {
        usersToDisplay = users;
        this.includeExcludeButton = excludeButton;
        this.includeDeleteButton = deleteButton;
        this.includeRemoveFromExcludedButton = removeFormExcludedButton;

        progressBar.setVisibility(View.VISIBLE);
        final LinearLayout layout = findViewById(R.id.table);
        layout.removeAllViews();
        if (users.size() > 0) {
            for (int i = users.size() - 1; i >= 0; i--) {
                try {
                    final JSONObject user = new JSONObject(globals.readFromFile(
                            getApplicationContext().getFilesDir() + globals.FOLLOWERS_PATH + File.separator + users.get(i),
                            getApplicationContext()));
                    ImageButton imageButton1 = new ImageButton(getApplicationContext());
                    if (deleteButton) {
                        ImageButton delete = new ImageButton(getApplicationContext());
                        delete.setImageResource(R.drawable.delete);
                        delete.setOnClickListener(
                                new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    globals.deleteFileOrPath(globals.FOLLOWERS_UNFOLLOWED_PATH + File.separator + user.getString("_id"), getApplicationContext());
                                    globals.deleteFileOrPath(globals.FOLLOWERS_PATH + File.separator + user.getString("_id"), getApplicationContext());
                                    unfollowers.remove(user.getString("_id"));
                                    organizeUsers(followers,
                                            previousFollowers,
                                            newFollowers,
                                            globals.FOLLOWERS_NEW_PATH,
                                            currentFollowers,
                                            globals.FOLLOWERS_CURRENT_PATH,
                                            unfollowers,
                                            globals.FOLLOWERS_UNFOLLOWED_PATH,
                                            exclusions);
                                    layout.removeView((View) v.getParent());
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "An error occurred deleting Follower", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        imageButton1 = delete;
                    }
                    ImageButton imageButton2 = new ImageButton(getApplicationContext());
                    if (excludeButton) {
                        ImageButton exclude = new ImageButton(getApplicationContext());
                        exclude.setImageResource(R.drawable.excluded);
                        exclude.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            globals.writeToFile(
                                                    globals.FOLLOWERS_EXCLUDED_PATH
                                                            + File.separator
                                                            + user.getString("_id"),
                                                    null,
                                                    getApplicationContext());
                                            exclusions.add(user.getString("_id"));
                                            organizeUsers(followers,
                                                    previousFollowers,
                                                    newFollowers,
                                                    globals.FOLLOWERS_NEW_PATH,
                                                    currentFollowers,
                                                    globals.FOLLOWERS_CURRENT_PATH,
                                                    unfollowers,
                                                    globals.FOLLOWERS_UNFOLLOWED_PATH,
                                                    exclusions);
                                            layout.removeView((View) v.getParent());
                                        } catch (JSONException e) {
                                            Toast.makeText(getApplicationContext(), "An error occurred adding Follower to excluded", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        imageButton2 = exclude;
                    }
                    ImageButton imageButton3 = new ImageButton(getApplicationContext());
                    if (removeFormExcludedButton) {
                        ImageButton remove = new ImageButton(getApplicationContext());
                        remove.setImageResource(R.drawable.include);
                        remove.setOnClickListener(
                                new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    globals.deleteFileOrPath(
                                            globals.FOLLOWERS_EXCLUDED_PATH
                                                    + File.separator
                                                    + user.getString("_id"),
                                            getApplicationContext());
                                    exclusions.remove(user.getString("_id"));
                                    organizeUsers(followers,
                                            previousFollowers,
                                            newFollowers,
                                            globals.FOLLOWERS_NEW_PATH,
                                            currentFollowers,
                                            globals.FOLLOWERS_CURRENT_PATH,
                                            unfollowers,
                                            globals.FOLLOWERS_UNFOLLOWED_PATH,
                                            exclusions);
                                    layout.removeView((View) v.getParent());
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "An error occurred removing Follower from excluded", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        imageButton3 = remove;
                    }
                    ConstraintLayout tableRow = viewDefinitions.userTableRow(
                            getApplicationContext(),
                            user.getString("logo"),
                            user.getString("display_name"),
                            imageButton1,
                            imageButton2,
                            imageButton3);
                    layout.addView(tableRow);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "An error occurred displaying Followers", Toast.LENGTH_SHORT).show();
                }
            }
        }  else {
            final TextView textView = new TextView(getApplicationContext());
            textView.setGravity(Gravity.CENTER);
            textView.setText("Nothing to show");
            layout.addView(textView);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }
}