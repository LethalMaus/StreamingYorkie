package com.lethalmaus.streaming_yorkie.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.LurkRequestHandler;
import com.lethalmaus.streaming_yorkie.service.LurkService;

import org.json.JSONException;
import org.json.JSONObject;

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
    private String appDirectory;

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
        if (weakContext != null && weakContext.get() != null) {
            this.appDirectory = weakContext.get().getFilesDir().toString();
        }
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
        if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing() && weakContext != null && weakContext.get() != null) {
            final String channel = lurkDataset.get(position);
            if (channel.contains("-")) {
                String[] channelDetails = lurkDataset.get(position).split("-", 2);
                String channelId = channelDetails[0];
                String channelName = channelDetails[1];
                String logo = "";
                if (new File(appDirectory + File.separator + Globals.FOLLOWING_PATH + File.separator + channelId).exists()) {
                    try {
                        logo = new JSONObject(new ReadFileHandler(weakContext, Globals.FOLLOWING_PATH + File.separator + channelId).readFile()).getString("logo");
                    } catch (JSONException e) {
                        new WriteFileHandler(weakContext, "ERROR", null, "Could not get Lurk Channel Logo | " + e.toString(), true).run();
                    }
                }
                TextView textView = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_username);
                textView.setText(channelName);

                if (!logo.isEmpty()) {
                    ImageView imageView = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_logo);
                    Glide.with(weakContext.get()).load(logo).placeholder(R.drawable.user).into(imageView);
                }
            } else {
                TextView textView = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_username);
                textView.setText(channel);
            }
            ImageButton button1 = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_button1);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DeleteFileHandler(weakContext,null).deleteFileOrPath(Globals.LURK_PATH + File.separator + channel);
                    lurkDataset.remove(channel);
                    datasetChanged();
                }
            });
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
        if (new ReadFileHandler(weakContext, Globals.LURK_PATH).countFiles() > 0) {
            lurkDataset = new ReadFileHandler(weakContext, Globals.LURK_PATH).readFileNames();
            int videoCount = 0;
            for (int i = 0; i < lurkDataset.size(); i++) {
                String video = new ReadFileHandler(weakContext, Globals.LURK_PATH + File.separator + lurkDataset.get(i)).readFile();
                if (video.isEmpty()) {
                    new LurkRequestHandler(weakActivity, weakContext, weakRecyclerView).newRequest(lurkDataset.get(i)).sendRequest(0);
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
        if (getItemCount() > 0) {
            getLurks();
        } else {
            Intent intent = new Intent(weakActivity.get(), LurkService.class);
            weakActivity.get().stopService(intent);
        }
        notifyDataSetChanged();
    }
}
