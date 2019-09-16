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

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.adapter.VODAdapter;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.request.VODRequestHandler;
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
 * Activity for VOD view that displays the info from the Users Twitch account VODs
 * @author LethalMaus
 */
public class VODs extends AppCompatActivity {

    //All activities & contexts are weak referenced to avoid memory leaks
    protected WeakReference<Activity> weakActivity;
    protected WeakReference<Context> weakContext;

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;

    protected ProgressBar progressBar;
    //Timer between clicks to prevent multiple requests
    private long mLastClickTime = 0;

    protected VODRequestHandler requestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.weakActivity = new WeakReference<Activity>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vod);
        new UserView(weakActivity, weakContext).execute();
        WorkManager.getInstance().cancelUniqueWork(Globals.SETTINGS_AUTOVODEXPORT);

        progressBar = findViewById(R.id.progressbar);
        //This is to make sure its invisible (not necessary but wanted)
        progressBar.setVisibility(View.GONE);

        deleteNotifications();
        recyclerView = findViewById(R.id.table);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new VODAdapter(weakActivity, weakContext, new WeakReference<>(recyclerView)));

        ImageButton refreshPage = findViewById(R.id.refresh);
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
        final ImageButton vodButton = findViewById(R.id.page1);
        vodButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageButtonListenerAction(vodButton, "VODs", "CURRENT", "EXPORT", "EXCLUDE");
                    }
                });

        final ImageButton exportedButton = findViewById(R.id.page2);
        exportedButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageButtonListenerAction(exportedButton, "Exported", "EXPORTED", null, "DELETE");
                    }
                });
        final ImageButton excludedButton = findViewById(R.id.page3);
        excludedButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageButtonListenerAction(excludedButton, "Excluded", "EXCLUDED", null, "INCLUDE");
                    }
                });
        pageButtonListenerAction(vodButton, "VODs", "CURRENT", "EXPORT", "EXCLUDE");

        requestHandler = new VODRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView));
        requestHandler.initiate().sendRequest();
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
                if (new File(getFilesDir() + File.separator + Globals.NOTIFICATION_VODEXPORT).exists()) {
                    new DeleteFileHandler(weakContext, Globals.NOTIFICATION_VODEXPORT).run();
                }
                if (new File(getFilesDir() + File.separator + Globals.FLAG_AUTOVODEXPORT_NOTIFICATION_UPDATE).exists()) {
                    new DeleteFileHandler(weakContext, Globals.FLAG_AUTOVODEXPORT_NOTIFICATION_UPDATE).run();
                }
            }
        });
    }

    /**
     * Requests are cancelled when activity ends as they are not needed
     * @author LethalMaus
     */
    protected void cancelRequests() {
        VolleySingleton.getInstance(weakContext).getRequestQueue().cancelAll("VOD");
        VolleySingleton.getInstance(weakContext).getRequestQueue().cancelAll("VOD_EXPORT");
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
        imageButton.setBackgroundResource(R.drawable.highlight_page_button);
    }

    /**
     * The action performed when a menu button is pressed.
     * @author LethalMaus
     * @param button image button that has been selected
     * @param subtitle explains where the channel is
     * @param vodsType vod that are to be displayed (eg. new, exported)
     * @param actionButtonType1 first action button belonging to vod to be displayed
     * @param actionButtonType2 second action button belonging to vod to be displayed
     */
    protected void pageButtonListenerAction(ImageButton button, String subtitle, String vodsType, String actionButtonType1, String actionButtonType2) {
        if ((SystemClock.elapsedRealtime() - mLastClickTime > 500 && progressBar.getVisibility() != View.VISIBLE) || SystemClock.elapsedRealtime() - mLastClickTime > 10000) {
            mLastClickTime = SystemClock.elapsedRealtime();
            progressBar.setVisibility(View.VISIBLE);
            highlightButton(button);
            setSubtitle(subtitle);
            recyclerView.stopScroll();
            recyclerView.getRecycledViewPool().clear();
            VODAdapter vodAdapter = (VODAdapter) recyclerView.getAdapter();
            if (vodAdapter != null) {
                vodAdapter.setDisplayPreferences(vodsType, actionButtonType1, actionButtonType2).datasetChanged();
            }
        }
    }
}
