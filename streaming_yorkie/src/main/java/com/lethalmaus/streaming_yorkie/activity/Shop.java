package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.adapter.ShopAdapter;

import java.lang.ref.WeakReference;

/**
 * Activity for Shop view that displays the available SKUs for purchase
 * @author LethalMaus
 */
public class Shop extends AppCompatActivity {

    //All activities & contexts are weak referenced to avoid memory leaks
    protected WeakReference<Activity> weakActivity;
    protected WeakReference<Context> weakContext;
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.weakActivity = new WeakReference<>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.table);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new ShopAdapter(weakActivity, weakContext));
    }

    @Override
    protected void onPause() {
        if (recyclerView != null) {
            recyclerView.stopScroll();
            recyclerView.scrollToPosition(0);
            recyclerView.getRecycledViewPool().clear();
        }
        super.onPause();
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
}
