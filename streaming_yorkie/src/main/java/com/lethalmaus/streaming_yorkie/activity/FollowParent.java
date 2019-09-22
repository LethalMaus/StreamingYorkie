package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.adapter.UserAdapter;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.request.RequestHandler;
import com.lethalmaus.streaming_yorkie.request.VolleySingleton;
import com.lethalmaus.streaming_yorkie.view.UserView;

import java.io.File;
import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    //Each sub class has at least one sub class of the RequestHandler
    protected RequestHandler requestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_parent);
        new UserView(weakActivity, weakContext).execute();
        WorkManager.getInstance().cancelUniqueWork(Globals.SETTINGS_AUTOFOLLOW);

        ImageButton refreshPage = findViewById(R.id.refresh);
        progressBar = findViewById(R.id.progressbar);
        refreshPage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime > 5000 && progressBar.getVisibility() != View.VISIBLE) {
                            mLastClickTime = SystemClock.elapsedRealtime();
                            progressBar.setVisibility(View.VISIBLE);
                            requestHandler.initiate().sendRequest();
                        }
                    }
                });
        cancelRequests();
        deleteNotifications();
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

    /**
     * Deletes any notification files when the activity is started
     * @author LethalMaus
     */
    private void deleteNotifications() {
        new Thread(new Runnable() {
            public void run() {
                if (new File(getFilesDir() + File.separator + Globals.NOTIFICATION_FOLLOW).exists()) {
                    new DeleteFileHandler(weakContext, Globals.NOTIFICATION_FOLLOW).run();
                }
                if (new File(getFilesDir() + File.separator + Globals.NOTIFICATION_UNFOLLOW).exists()) {
                    new DeleteFileHandler(weakContext, Globals.NOTIFICATION_UNFOLLOW).run();
                }
                if (new File(getFilesDir() + File.separator + Globals.FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE).exists()) {
                    new DeleteFileHandler(weakContext, Globals.FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE).run();
                }
            }
        }).start();
    }

    /**
     * Requests are cancelled when activity ends as they are not needed
     * @author LethalMaus
     */
    protected void cancelRequests() {
        VolleySingleton volleySingleton = VolleySingleton.getInstance(weakContext);
        if (volleySingleton != null) {
            volleySingleton.getRequestQueue().cancelAll("FOLLOWING_UPDATE");
            volleySingleton.getRequestQueue().cancelAll("FOLLOWERS_UPDATE");
            volleySingleton.getRequestQueue().cancelAll("FOLLOWING");
            volleySingleton.getRequestQueue().cancelAll("FOLLOWERS");
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return Globals.onOptionsItemsSelected(this, item);
    }

    /**
     * Used to set subtitle in Action bar, helps explain where the channel is
     * @author LethalMaus
     * @param subtitle explains where the channel is
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
     * @param subtitle explains where the channel is
     * @param daoType DAO Type (eg. Follower, Following, ...)
     * @param entityStatus entity status (eg. new, current)
     * @param actionButtonType1 action button belonging to users to be displayed, can be null
     * @param actionButtonType2 action button belonging to users to be displayed, can be null
     */
    protected void pageButtonListenerAction(ImageButton button, String subtitle, String daoType, String entityStatus, String actionButtonType1, String actionButtonType2) {
        if ((SystemClock.elapsedRealtime() - mLastClickTime > 500 && progressBar.getVisibility() != View.VISIBLE) || SystemClock.elapsedRealtime() - mLastClickTime > 10000) {
            mLastClickTime = SystemClock.elapsedRealtime();
            progressBar.setVisibility(View.VISIBLE);
            highlightButton(button);
            setSubtitle(subtitle);
            recyclerView.stopScroll();
            recyclerView.getRecycledViewPool().clear();
            UserAdapter userAdapter = (UserAdapter) recyclerView.getAdapter();
            if (userAdapter != null) {
                userAdapter.setDisplayPreferences(daoType, entityStatus, actionButtonType1, actionButtonType2, "FOLLOW_BUTTON").datasetChanged();
            }
        } else {
            Toast.makeText(this, "Updating Users, please be patient.", Toast.LENGTH_SHORT).show();
        }
    }
}