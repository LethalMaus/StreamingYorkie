package com.lethalmaus.twitchfollowerservice;

import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Following extends FollowService {

    /*TODO
    keep it the same as followers
    add button for stopping request at following command
    load user portion wise if possible (this might need a big refactor)
*/
    private ArrayList<String> following;
    private ArrayList<String> previousFollowing;
    private ArrayList<String> currentFollowing;
    private ArrayList<String> newFollowing;
    private ArrayList<String> unfollowing;
    private ArrayList<String> excludedFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Following");

        ImageButton refreshPage = findViewById(R.id.refresh);
        refreshPage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        followerProcess();
                    }
                });

        ImageButton newButton = findViewById(R.id.page1);
        newButton.setImageResource(R.drawable.new_button);
        newButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFollowers(newFollowing, true, false, false);
                    }
                });

        ImageButton currentButton = findViewById(R.id.page2);
        currentButton.setImageResource(R.drawable.follow);
        currentButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFollowers(currentFollowing, true, false, false);
                    }
                });

        ImageButton unfollowedButton = findViewById(R.id.page3);
        unfollowedButton.setImageResource(R.drawable.unfollow);
        unfollowedButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFollowers(unfollowing, true, true, false);
                    }
                });

        ImageButton exclusionsButton = findViewById(R.id.page4);
        exclusionsButton.setImageResource(R.drawable.excluded);
        exclusionsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFollowers(excludedFollowing, false, false, true);
                    }
                });

        followerProcess();
    }

    private void followerProcess() {
        twitchTotal = 0;
        offset = 0;
        following = new ArrayList<>();
        previousFollowing = getFollowerIDs(globals.FOLLOWING_PATH);
        excludedFollowing = getFollowerIDs(globals.FOLLOWING_EXCLUDED_PATH);
        requestFollowers();
    }

    private void requestFollowers() {
        if (!requestRunning) {
            requestRunning = true;
            progressBar.setVisibility(View.VISIBLE);
            if (globals.isNetworkAvailable((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))) {
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/kraken/users/" + globals.userID + "/follows/channels" + "?limit=" + limit + "&direction=asc&offset=" + offset, null,
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
                                        userObject.put("display_name", response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("display_name"));
                                        userObject.put("_id", response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("_id"));
                                        userObject.put("logo", response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("logo"));
                                        /*TODO check what is needed
                                        userObject.put("notifications", response.getJSONArray("follows").getJSONObject(i).getBoolean("notifications"));
                                        userObject.put("created_at", response.getJSONArray("follows").getJSONObject(i).getString("created_at"));
                                        */
                                        globals.writeToFile(
                                                globals.FOLLOWING_PATH
                                                        + File.separator
                                                        + userObject.getString("_id"),
                                                userObject.toString(),
                                                getApplicationContext());
                                        following.add(userObject.getString("_id"));
                                    }
                                    if (response.getJSONArray("follows").length() > 0) {
                                        requestRunning = false;
                                        requestFollowers();
                                    } else {
                                        if (twitchTotal != following.size()) {
                                            Toast.makeText(getApplicationContext(), "Twitch Data for 'Following' is out of sync. Total should be '" + twitchTotal
                                                    + "' but is only giving '" + following.size() + "'", Toast.LENGTH_SHORT).show();
                                        }
                                        organizeFollowers();
                                        showFollowers(newFollowing, true, false, false);
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
                        Toast.makeText(getApplicationContext(), "Error requesting Following", Toast.LENGTH_SHORT).show();
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
                VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
            } else {
                Toast.makeText(getApplicationContext(), "OFFLINE: Showing saved following", Toast.LENGTH_SHORT).show();
                following = getFollowerIDs(globals.FOLLOWING_PATH);
                organizeFollowers();
                showFollowers(newFollowing, true, false, false);
                requestRunning = false;
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void deleteFollowerByID(String id) {
        globals.deleteFileOrPath(globals.FOLLOWING_EXCLUDED_PATH + File.separator + id, getApplicationContext());
        globals.deleteFileOrPath(globals.FOLLOWING_NEW_PATH + File.separator + id, getApplicationContext());
        globals.deleteFileOrPath(globals.FOLLOWING_UNFOLLOWED_PATH + File.separator + id, getApplicationContext());
        globals.deleteFileOrPath(globals.FOLLOWING_CURRENT_PATH + File.separator + id, getApplicationContext());
        globals.deleteFileOrPath(globals.FOLLOWING_PATH + File.separator + id, getApplicationContext());
    }

    private ArrayList<String> getFollowerIDs(String path) {
        File pathToFollowers = new File(getApplicationContext().getFilesDir() + path);
        String[] pathToFollowersArray = pathToFollowers.list();
        if (pathToFollowersArray == null || pathToFollowersArray.length <= 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(pathToFollowersArray));
    }

    private void saveFollowerIDs(String path, ArrayList<String> followersToSave) {
        for (int i = 0; i < followersToSave.size(); i++) {
            globals.writeToFile(path + File.separator + followersToSave.get(i), null, getApplicationContext());
        }
    }

    private void organizeFollowers() {
        unfollowing = getFollowerIDs(globals.FOLLOWING_UNFOLLOWED_PATH);
        newFollowing = new ArrayList<>();
        currentFollowing = new ArrayList<>();
        for (int i = 0; i < previousFollowing.size(); i++) {
            if (!following.contains(previousFollowing.get(i)) && !excludedFollowing.contains(previousFollowing.get(i))) {
                unfollowing.add(previousFollowing.get(i));
            }
        }
        for (int i = 0; i < following.size(); i++) {
            if (unfollowing.contains(following.get(i))) {
                unfollowing.remove(following.get(i));
                globals.deleteFileOrPath(globals.FOLLOWING_UNFOLLOWED_PATH + File.separator + following.get(i), getApplicationContext());
            }
            if (!previousFollowing.contains(following.get(i)) && !excludedFollowing.contains(following.get(i))) {
                newFollowing.add(following.get(i));
                currentFollowing.add(following.get(i));
            } else if (!excludedFollowing.contains(following.get(i))) {
                currentFollowing.add(following.get(i));
            }
        }
        if (newFollowing.size() > 0) {
            globals.deleteFileOrPath(globals.FOLLOWING_NEW_PATH, getApplicationContext());
            saveFollowerIDs(globals.FOLLOWING_NEW_PATH, newFollowing);
        }
        newFollowing = getFollowerIDs(globals.FOLLOWING_NEW_PATH);

        if (currentFollowing.size() > 0) {
            globals.deleteFileOrPath(globals.FOLLOWING_CURRENT_PATH, getApplicationContext());
            saveFollowerIDs(globals.FOLLOWING_CURRENT_PATH, currentFollowing);
        }
        currentFollowing = getFollowerIDs(globals.FOLLOWING_CURRENT_PATH);

        if (unfollowing.size() > 0) {
            saveFollowerIDs(globals.FOLLOWING_UNFOLLOWED_PATH, unfollowing);
        }
    }

    public void showFollowers(ArrayList<String> followers, boolean excludeButton, boolean deleteButton, boolean removeFormExcludedButton) {
        //TODO
        final LinearLayout layout = findViewById(R.id.table);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if (followers.size() > 0) {
            layout.removeAllViews();
            for (int i = followers.size() - 1; i >= 0; i--) {
                LinearLayout column = new LinearLayout(getApplicationContext());
                column.setOrientation(LinearLayout.HORIZONTAL);
                final ImageView imageView = new ImageView(getApplicationContext());
                imageView.setLayoutParams(new LinearLayout.LayoutParams(size.y / 16, size.y / 16));
                imageView.setPadding(10, 10, 10, 10);
                final TextView textView = new TextView(getApplicationContext());
                textView.setLayoutParams(new LinearLayout.LayoutParams(size.x / 2, size.y / 16));
                textView.setPadding(10, 0, 0, 0);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                try {
                    final JSONObject user = new JSONObject(globals.readFromFile(
                            getApplicationContext().getFilesDir() + globals.FOLLOWING_PATH + File.separator + followers.get(i), getApplicationContext()));
                    Glide.with(getApplicationContext()).load(user.getString("logo")).into(imageView);
                    textView.setText(user.getString("display_name"));
                    column.addView(imageView);
                    column.addView(textView);
                    if (excludeButton) {
                        Button exclude = new Button(getApplicationContext());
                        exclude.setText("EXCLUDE");
                        exclude.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            globals.writeToFile(
                                                    globals.FOLLOWING_EXCLUDED_PATH
                                                            + File.separator
                                                            + user.getString("_id"),
                                                    null,
                                                    getApplicationContext());
                                            excludedFollowing.add(user.getString("_id"));
                                            organizeFollowers();
                                            layout.removeView((View) v.getParent());
                                        } catch (JSONException e) {
                                            Toast.makeText(getApplicationContext(), "An error occurred adding Follower to excluded", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        column.addView(exclude);
                    }
                    if (deleteButton) {
                        Button delete = new Button(getApplicationContext());
                        delete.setText("DELETE");
                        delete.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            deleteFollowerByID(user.getString("_id"));
                                            unfollowing.remove(user.getString("_id"));
                                            layout.removeView((View) v.getParent());
                                        } catch (JSONException e) {
                                            Toast.makeText(getApplicationContext(), "An error occurred deleting Follower", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        column.addView(delete);
                    }
                    if (removeFormExcludedButton) {
                        Button remove = new Button(getApplicationContext());
                        remove.setText("REMOVE");
                        remove.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            globals.deleteFileOrPath(
                                                    globals.FOLLOWING_EXCLUDED_PATH
                                                            + File.separator
                                                            + user.getString("_id"),
                                                    getApplicationContext());
                                            excludedFollowing.remove(user.getString("_id"));
                                            organizeFollowers();
                                            layout.removeView((View) v.getParent());
                                        } catch (JSONException e) {
                                            Toast.makeText(getApplicationContext(), "An error occurred removing Follower from excluded", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        column.addView(remove);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "An error occurred displaying Followers", Toast.LENGTH_SHORT).show();
                }
                layout.addView(column);
            }
        } else {
            layout.removeAllViews();
            final TextView textView = new TextView(getApplicationContext());
            textView.setGravity(Gravity.CENTER);
            textView.setText("Nothing to show");
            layout.addView(textView);
        }
    }
}
