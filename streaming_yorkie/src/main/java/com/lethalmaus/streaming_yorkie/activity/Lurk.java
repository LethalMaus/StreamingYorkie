package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.adapter.LurkAdapter;
import com.lethalmaus.streaming_yorkie.dao.LurkDAO;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.LurkEntity;
import com.lethalmaus.streaming_yorkie.request.LurkRequestHandler;
import com.lethalmaus.streaming_yorkie.view.UserView;

import java.lang.ref.WeakReference;

/**
 * Activity for lurking
 * @author LethalMaus
 */
public class Lurk extends AppCompatActivity {

    //All activities & contexts are weak referenced to avoid memory leaks
    protected WeakReference<Activity> weakActivity;
    protected WeakReference<Context> weakContext;

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;

    //Timer between clicks to prevent multiple requests
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 23) {
            checkDrawOverlayPermission();
        }
        this.weakActivity = new WeakReference<>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lurk);

        new UserView(weakActivity, weakContext).execute();
        recyclerView = findViewById(R.id.lurk_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        LurkAdapter lurkAdapter = new LurkAdapter(weakActivity, weakContext, new WeakReference<>(recyclerView));
        recyclerView.setAdapter(lurkAdapter);
        recyclerView.post(() ->
                lurkAdapter.datasetChanged(false)
        );

        ImageView lurk_start = findViewById(R.id.lurk_start);
        lurk_start.setOnClickListener((View v) -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime > 5000) {
                        mLastClickTime = SystemClock.elapsedRealtime();
                        EditText channelInput = findViewById(R.id.lurk_input);
                        recyclerView.stopScroll();
                        recyclerView.scrollToPosition(0);
                        recyclerView.getRecycledViewPool().clear();
                        if (channelInput != null && channelInput.getText().toString().replaceAll("\\s", "").length() > 0) {
                            String channelInputText = channelInput.getText().toString().replaceAll("\\s", "");
                            channelInput.setText("");
                            new Thread() {
                                public void run() {
                                    LurkDAO lurkDAO = StreamingYorkieDB.getInstance(getApplicationContext()).lurkDAO();
                                    lurkDAO.insertLurk(new LurkEntity(channelInputText, 0, null, null, null, true, true));
                                    new LurkRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView)) {
                                        @Override
                                        public void onCompletion() {
                                            recyclerView.post(() ->
                                                    lurkAdapter.datasetChanged(false)
                                            );
                                        }
                                    }.newRequest(channelInputText).initiate().sendRequest();
                                }
                            }.start();
                        }
                    }
                }
        );
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

    public final static int REQUEST_CODE = 6421;

    /**
     * Checks for permission for the Lurk Service
     * @author LethalMaus
     */
    @RequiresApi(23)
    public void checkDrawOverlayPermission() {
        if (!Settings.canDrawOverlays(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @RequiresApi(23)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Cannot Lurk without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
