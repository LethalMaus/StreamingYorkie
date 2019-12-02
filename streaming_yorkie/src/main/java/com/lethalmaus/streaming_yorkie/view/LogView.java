package com.lethalmaus.streaming_yorkie.view;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * AsyncTask to view local files & logs
 * @author LethalMaus
 */
public class LogView extends AsyncTask<Void, Button, Void> {

    private WeakReference<Activity> weakActivity;
    private WeakReference<Context> weakContext;
    private String currentDirectory;

    /**
     * Constructor for LogView to view local files & logs
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public LogView(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        if (weakContext != null && weakContext.get() != null) {
            this.setCurrentDirectory(weakContext.get().getFilesDir().toString());
        }
    }

    /**
     * Sets the directory where the channel currently is
     * @author LethalMaus
     * @param currentDirectory directory where the channel currently is
     */
    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    /**
     * Gets the directory where the channel currently is
     * @author LethalMaus
     * @return String currentDirectory
     */
    public String getCurrentDirectory() {
        return this.currentDirectory;
    }

    @Override
    protected void onPreExecute() {
        LinearLayout logsTable = weakActivity.get().findViewById(R.id.log_table);
        logsTable.removeAllViews();
    }

    @Override
    protected Void doInBackground(Void... params) {
        getFileList();
        return null;
    }

    @Override
    protected void onProgressUpdate(Button... button) {
        LinearLayout logsTable = weakActivity.get().findViewById(R.id.log_table);
        logsTable.addView(button[0]);
    }

    /**
     * Simple logic to get list of files & folders with logic to differentiate & react accordingly
     * @author LethalMaus
     */
    private void getFileList() {
        final String directory = currentDirectory;
        String[] filesOrPaths = new File(directory).list();

        if (filesOrPaths != null) {
            for (final String fileOrPath : filesOrPaths) {
                Button button = new Button(weakActivity.get());
                button.setText(fileOrPath);
                if (new File(directory + File.separator + fileOrPath).isDirectory()) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currentDirectory = directory + File.separator + fileOrPath;
                            LinearLayout logsTable = weakActivity.get().findViewById(R.id.log_table);
                            logsTable.removeAllViews();
                            getFileList();
                        }
                    });
                } else {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currentDirectory = directory + File.separator + fileOrPath;
                            TextView textView = new TextView(weakActivity.get());
                            String text = new ReadFileHandler(weakActivity, weakContext, currentDirectory.replace(weakContext.get().getFilesDir().toString(), "")).readFile();
                            textView.setText(text);
                            LinearLayout logsTable = weakActivity.get().findViewById(R.id.log_table);
                            logsTable.removeAllViews();
                            logsTable.addView(textView);
                        }
                    });
                }
                publishProgress(button);
            }
        }
    }
}
