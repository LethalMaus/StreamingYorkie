package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.adapter.UserAdapter;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.request.RequestHandler;
import com.lethalmaus.streaming_yorkie.request.UserRequestHandler;

import java.io.File;
import java.lang.ref.WeakReference;

import androidx.work.WorkManager;

/**
 * Activity with common methods for sub-activities
 * @author LethalMaus
 */
public class FollowParent extends AppCompatActivity {

    //All activities & contexts are weak referenced to avoid memory leaks
    protected WeakReference<Activity> weakActivity;
    protected WeakReference<Context> weakContext;

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;

    protected ProgressBar progressBar;
    //Timer between clicks to prevent multiple requests
    private long mLastClickTime = 0;

    //Paths that each sub class uses to write files locally
    protected String currentUsersPath;
    protected String newUsersPath;
    protected String unfollowedUsersPath;
    protected String excludedUsersPath;
    protected String requestPath;
    protected String usersPath;

    //Each sub class has at least one sub class of the RequestHandler
    protected RequestHandler requestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_parent);
        WorkManager.getInstance().cancelUniqueWork("AUTOFOLLOW_WORKER");

        progressBar = findViewById(R.id.progressbar);
        //This is to make sure its invisible (not necessary but wanted)
        progressBar.setVisibility(View.INVISIBLE);

        ImageButton refreshPage = findViewById(R.id.refresh);
        refreshPage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime > 5000 && progressBar.getVisibility() != View.VISIBLE) {
                            mLastClickTime = SystemClock.elapsedRealtime();
                            progressBar.setVisibility(View.VISIBLE);
                            requestHandler.newRequest().sendRequest(0);
                        }
                    }
                });
        deleteNotifications();
        new UserRequestHandler(weakActivity, weakContext,true, false, true).sendRequest(0);
        recyclerView = findViewById(R.id.table);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new UserAdapter(weakActivity, weakContext));
    }
    @Override
    protected void onPause() {
        cancelRequests();
        super.onPause();
    }
    @Override
    protected void onStop() {
        cancelRequests();
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        cancelRequests();
        super.onDestroy();
    }

    /**
     * Deletes any notification files when the activity is started
     * @author LethalMaus
     */
    private void deleteNotifications() {
        if (new File(getFilesDir() + File.separator + Globals.NOTIFICATION_FOLLOW).exists()) {
            new DeleteFileHandler(weakContext, Globals.NOTIFICATION_FOLLOW).run();
        }
        if (new File(getFilesDir() + File.separator + Globals.NOTIFICATION_UNFOLLOW).exists()) {
            new DeleteFileHandler(weakContext, Globals.NOTIFICATION_UNFOLLOW).run();
        }
    }

    /**
     * Requests are cancelled when activity ends as they are not needed
     * @author LethalMaus
     */
    protected void cancelRequests() {
        requestHandler.cancelAllRequests();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Globals.onOptionsItemsSelected(this, item);
    }

    /**
     * Used to set subtitle in Action bar, helps explain where the user is
     * @author LethalMaus
     * @param subtitle explains where the user is
     */
    protected void setSubtitle(String subtitle) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subtitle);
        }
    }

    /**
     * Sets the background colour of the menu button last pressed
     * @param imageButton image button that has been selected
     */
    protected void highlightButton(ImageButton imageButton) {
        findViewById(R.id.page1).setBackgroundResource(0);
        findViewById(R.id.page2).setBackgroundResource(0);
        findViewById(R.id.page3).setBackgroundResource(0);
        findViewById(R.id.page4).setBackgroundResource(0);
        imageButton.setBackgroundResource(R.drawable.highlight_page_button);
    }

    /**
     * The action performed when a menu button is pressed.
     * @author LethalMaus
     * @param button image button that has been selected
     * @param subtitle explains where the user is
     * @param usersToDisplay users that are to be displayed (eg. new, current)
     * @param actionButtonType1 action button belonging to users to be displayed, can be null
     * @param actionButtonType2 action button belonging to users to be displayed, can be null
     */
    protected void pageButtonListenerAction(ImageButton button, String subtitle, String usersToDisplay, String actionButtonType1, String actionButtonType2) {
        if (progressBar.getVisibility() != View.VISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
            highlightButton(button);
            setSubtitle(subtitle);
            requestHandler.setDisplayPreferences(usersToDisplay, actionButtonType1, actionButtonType2, "FOLLOW_BUTTON");
            recyclerView.setAdapter(new UserAdapter(weakActivity, weakContext)
                    .setPaths(newUsersPath, currentUsersPath, unfollowedUsersPath, excludedUsersPath, usersPath)
                    .setDisplayPreferences(usersToDisplay, actionButtonType1, actionButtonType2, "FOLLOW_BUTTON"));
        }
    }
}