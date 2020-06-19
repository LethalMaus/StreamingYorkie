package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.adapter.HostAdapter;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.ChannelEntity;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.AutoHostRequestHandler;
import com.lethalmaus.streaming_yorkie.util.ItemMoveCallback;
import com.lethalmaus.streaming_yorkie.util.StartDragListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Activity for Hosting view that extends UserParent
 * @author LethalMaus
 */
public class Host extends AppCompatActivity implements StartDragListener {

    //All activities & contexts are weak referenced to avoid memory leaks
    protected WeakReference<Activity> weakActivity;
    protected WeakReference<Context> weakContext;
    protected WeakReference<RecyclerView> weakRecyclerView;

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    private ItemTouchHelper touchHelper;

    //Timer between clicks to prevent multiple requests
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.weakActivity = new WeakReference<>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle("Auto Host List");
        }

        recyclerView = findViewById(R.id.hostList);
        this.weakRecyclerView =  new WeakReference<>(recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final HostAdapter hostAdapter = new HostAdapter(weakActivity, weakContext, weakRecyclerView, new ArrayList<>(), this);
        recyclerView.setAdapter(hostAdapter);
        ItemTouchHelper.Callback callback = new ItemMoveCallback(hostAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        new Thread() {
            @Override
            public void run() {
                ChannelEntity channelEntity = StreamingYorkieDB.getInstance(weakContext.get()).channelDAO().getChannel();
                if (channelEntity != null) {
                    new AutoHostRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView)).postBodyForGettingList(channelEntity.getDisplay_name()).initiate().sendRequest(true);
                }
            }
        }.start();

        EditText hostInput = findViewById(R.id.hostInput);
        ImageView newHost = findViewById(R.id.hostButton);
        newHost.setOnClickListener((View v) -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime > 3000) {
                String hostInputText = hostInput.getText().toString().replaceAll("\\s", "");
                if (hostInputText.length() > 0) {
                    new AutoHostRequestHandler(weakActivity, weakContext, new WeakReference<>(recyclerView)) {
                        @Override
                        public void responseHandler(final JSONObject response) {
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        //TODO here you can check if the user also hosts you
                                        if (response.getJSONObject("data").isNull("user")) {
                                            if (Globals.checkWeakActivity(weakActivity)) {
                                                weakActivity.get().runOnUiThread(() ->
                                                        Toast.makeText(weakActivity.get(), "User does not exist.", Toast.LENGTH_SHORT).show()
                                                );
                                            }
                                        } else {
                                            String id = response.getJSONObject("data").getJSONObject("user").getString("id");
                                            if (Globals.checkWeakRecyclerView(weakRecyclerView)) {
                                                HostAdapter adapter = (HostAdapter) weakRecyclerView.get().getAdapter();
                                                if (adapter != null) {
                                                    adapter.addToAutohostList(id);
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        if (Globals.checkWeakActivity(weakActivity)) {
                                            weakActivity.get().runOnUiThread(() ->
                                                    Toast.makeText(weakActivity.get(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show()
                                            );
                                        }
                                        new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "New host response error | " + e.toString(), true).run();
                                    }
                                }
                            }.start();
                        }
                    }.postBodyForGettingList(hostInputText).initiate().sendRequest(true);
                    hostInput.setText("");
                }
            }
        });
    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
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

    @Override
    public void onBackPressed() {
        Globals.onBackPressed(this);
    }
}