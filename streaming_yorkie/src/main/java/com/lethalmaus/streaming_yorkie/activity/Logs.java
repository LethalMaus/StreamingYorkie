package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.view.LogView;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Activity for basic view of local files & to access the error log
 * @author LethalMaus
 */
public class Logs extends AppCompatActivity {

    private LogView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logs);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        logView = new LogView(new WeakReference<Activity>(this), new WeakReference<Context>(getApplication()));
        logView.execute();
    }

    //Only the back button is available. If the current directory position is a sub folder it goes back. Otherwise it finishes the activity.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (logView.getCurrentDirectory().equals(this.getFilesDir().toString())) {
            finish();
        } else {
            String currentDirectory = logView.getCurrentDirectory();
            logView = new LogView(new WeakReference<Activity>(this), new WeakReference<Context>(getApplication()));
            logView.setCurrentDirectory(currentDirectory.substring(0, currentDirectory.lastIndexOf(File.separator)));
            logView.execute();
        }
        return true;
    }
}
