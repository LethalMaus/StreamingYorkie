package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.adapter.LurkAdapter;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.UserRequestHandler;

import java.io.File;
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
        this.weakActivity = new WeakReference<Activity>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lurk);

        new UserRequestHandler(weakActivity, weakContext,true, false, false).sendRequest(0);
        recyclerView = findViewById(R.id.lurk_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new LurkAdapter(weakActivity, weakContext, new WeakReference<>(recyclerView)));

        ImageView lurk_start = findViewById(R.id.lurk_start);
        lurk_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime > 5000) {
                    mLastClickTime = SystemClock.elapsedRealtime();
                    EditText channelInput = findViewById(R.id.lurk_input);
                    if (channelInput != null && channelInput.getText().toString().replaceAll("\\s", "").length() > 0) {
                        new WriteFileHandler(weakContext, Globals.LURK_PATH + File.separator + channelInput.getText().toString().replaceAll("\\s", ""), null, null, false).writeToFileOrPath();
                        recyclerView.setAdapter(new LurkAdapter(weakActivity, weakContext, new WeakReference<>(recyclerView)));
                        channelInput.setText("");
                    }
                }
            }
        });
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(weakActivity.get(), MainActivity.class);
        weakActivity.get().startActivity(intent);
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
