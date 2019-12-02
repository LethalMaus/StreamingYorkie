package com.lethalmaus.streaming_yorkie.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.UserEntity;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.request.LurkRequestHandler;
import com.lethalmaus.streaming_yorkie.service.LurkService;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * RecyclerView Adapter for Lurk Objects
 * @author LethalMaus
 */
public class LurkAdapter extends RecyclerView.Adapter<LurkAdapter.LurkViewHolder> {

    //All activities & contexts are weak referenced to avoid memory leaks
    private WeakReference<Activity> weakActivity;
    private WeakReference<Context> weakContext;
    private WeakReference<RecyclerView> weakRecyclerView;
    private StreamingYorkieDB streamingYorkieDB;
    private ArrayList<String> lurkDataset;

    /**
     * Simple View Holder for loading the View with a Dataset Row
     * @author LethalMaus
     */
    static class LurkViewHolder extends RecyclerView.ViewHolder {

        View lurkRow;

        /**
         * Holder for Lurk View
         * @param lurkRow View for Lurk Row
         */
        LurkViewHolder(View lurkRow) {
            super(lurkRow);
            this.lurkRow = lurkRow;
        }
    }

    /**
     * Adapter for displaying a Lurk Dataset
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param weakRecyclerView weak referenced recycler view
     */
    public LurkAdapter(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> weakRecyclerView) {
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        this.weakRecyclerView = weakRecyclerView;
        streamingYorkieDB = StreamingYorkieDB.getInstance(weakContext.get());
        getLurks();
    }

    @Override
    @NonNull
    public LurkAdapter.LurkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View lurkRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.lurk_row, parent, false);
        return new LurkAdapter.LurkViewHolder(lurkRow);
    }

    @Override
    public void onBindViewHolder(@NonNull final LurkAdapter.LurkViewHolder lurkViewHolder, final int position) {
        weakActivity.get().runOnUiThread(new Runnable() {
            public void run() {
                new LurkAdapter.LurkAsyncTask(weakActivity, weakContext, LurkAdapter.this, lurkViewHolder, streamingYorkieDB, lurkDataset.get(position)).execute();
            }
        });
    }

    /**
     * Async Task to request Lurks and display it
     * @author LethalMaus
     */
    private static class LurkAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<Activity> weakActivity;
        private WeakReference<Context> weakContext;
        private StreamingYorkieDB streamingYorkieDB;
        private LurkAdapter lurkAdapter;
        private LurkAdapter.LurkViewHolder lurkViewHolder;
        private String channel;
        private String channelName;
        private String logo;

        /**
         * Async Task constructor
         * @author LethalMaus
         * @param weakActivity inner class reference
         * @param weakContext inner class reference
         * @param lurkAdapter inner class reference
         * @param lurkViewHolder inner class reference
         * @param streamingYorkieDB inner class reference
         * @param channel inner class reference
         */
        LurkAsyncTask(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, LurkAdapter lurkAdapter, LurkAdapter.LurkViewHolder lurkViewHolder, StreamingYorkieDB streamingYorkieDB, String channel) {
            this.weakActivity = weakActivity;
            this.weakContext = weakContext;
            this.streamingYorkieDB = streamingYorkieDB;
            this.lurkAdapter = lurkAdapter;
            this.lurkViewHolder = lurkViewHolder;
            this.channel = channel;
        }

        @Override
        protected Void doInBackground(Void... params) {
            logo = "";
            if (channel.contains("-")) {
                String[] channelDetails = channel.split("-", 2);
                String channelId = channelDetails[0];
                channelName = channelDetails[1];
                UserEntity userEntity = streamingYorkieDB.followingDAO().getUserById(Integer.parseInt(channelId));
                if (userEntity != null) {
                    logo = userEntity.getLogo();
                }
            } else {
                channelName = channel;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void x) {
            if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing() && weakContext != null && weakContext.get() != null) {
                TextView textView = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_username);
                textView.setText(channelName);
                if (!logo.isEmpty()) {
                    ImageView imageView = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_logo);
                    Glide.with(weakContext.get()).load(logo).placeholder(R.drawable.user).into(imageView);
                }
                ImageButton button1 = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_button1);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Thread thread = new Thread() {
                            public void run() {
                                new DeleteFileHandler(weakActivity, weakContext, null).deleteFileOrPath(Globals.LURK_PATH + File.separator + channel);
                                lurkAdapter.lurkDataset.remove(channel);
                                lurkAdapter.datasetChanged();
                            }
                        };
                        thread.start();
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return lurkDataset != null ? lurkDataset.size() : 0;
    }

    /**
     * Method for getting Lurk objects
     * @author LethalMaus
     */
    private void getLurks() {
        if (new ReadFileHandler(weakActivity, weakContext, Globals.LURK_PATH).countFiles() > 0) {
            lurkDataset = new ReadFileHandler(weakActivity, weakContext, Globals.LURK_PATH).readFileNames();
            int videoCount = 0;
            for (int i = 0; i < lurkDataset.size(); i++) {
                String video = new ReadFileHandler(weakActivity, weakContext, Globals.LURK_PATH + File.separator + lurkDataset.get(i)).readFile();
                if (video.isEmpty()) {
                    new LurkRequestHandler(weakActivity, weakContext, weakRecyclerView).newRequest(lurkDataset.get(i)).sendRequest();
                } else {
                    videoCount++;
                }
            }
            if (videoCount == lurkDataset.size()) {
                Thread thread = new Thread(){
                    public void run(){
                        Intent intent = new Intent(weakActivity.get(), LurkService.class);
                        if (Build.VERSION.SDK_INT < 28) {
                            weakActivity.get().startService(intent);
                        } else {
                            weakActivity.get().startForegroundService(intent);
                        }
                    }
                };
                thread.start();
            }
        }
    }

    /**
     * Takes action once a dataset has been changed then notifies UI
     * @author LethalMaus
     */
    private void datasetChanged() {
        Thread thread = new Thread(){
            public void run() {
                if (getItemCount() > 0) {
                    getLurks();
                } else {
                    Intent intent = new Intent(weakActivity.get(), LurkService.class);
                    weakActivity.get().stopService(intent);
                }
                weakActivity.get().runOnUiThread(
                        new Runnable() {
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
            }
        };
        thread.start();
    }
}
