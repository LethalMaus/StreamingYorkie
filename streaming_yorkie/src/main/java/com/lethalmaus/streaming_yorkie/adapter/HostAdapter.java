package com.lethalmaus.streaming_yorkie.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.lethalmaus.streaming_yorkie.data_model.HostDataModel;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.ChannelEntity;
import com.lethalmaus.streaming_yorkie.request.AutoHostRequestHandler;
import com.lethalmaus.streaming_yorkie.util.ItemMoveCallback;
import com.lethalmaus.streaming_yorkie.util.StartDragListener;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * RecyclerView Adapter for Host Objects
 * @author LethalMaus
 */
public class HostAdapter extends RecyclerView.Adapter<HostAdapter.HostViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    //All activities & contexts are weak referenced to avoid memory leaks
    private WeakReference<Activity> weakActivity;
    private WeakReference<Context> weakContext;
    private List<HostDataModel> dataset;
    private StartDragListener dragListener;
    private AutoHostRequestHandler requestHandler;

    /**
     * Simple View Holder for loading the View with a Dataset Row
     * @author LethalMaus
     */
    public static class HostViewHolder extends RecyclerView.ViewHolder {

        View hostRow;

        /**
         * Holder for Host View
         * @param hostRow View for Host Row
         */
        HostViewHolder(View hostRow) {
            super(hostRow);
            this.hostRow = hostRow;
        }
    }

    /**
     * Adapter for displaying a Host dataset
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param weakRecyclerView weak referenced recycler view
     * @param dragListener drag listener interface
     * @param dataset host list
     */
    public HostAdapter(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> weakRecyclerView, List<HostDataModel> dataset, StartDragListener dragListener) {
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        this.dataset = dataset;
        this.dragListener = dragListener;
        requestHandler = new AutoHostRequestHandler(weakActivity, weakContext, weakRecyclerView);
    }

    @Override
    @NonNull
    public HostAdapter.HostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View hostRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.host_row, parent, false);
        return new HostAdapter.HostViewHolder(hostRow);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final HostAdapter.HostViewHolder hostViewHolder, final int position) {
        if (Globals.checkWeakActivity(weakActivity) && Globals.checkWeakReference(weakContext)) {
            TextView textView = hostViewHolder.hostRow.findViewById(R.id.username);
            textView.setText(dataset.get(position).getDisplayName());
            ImageView imageView = hostViewHolder.hostRow.findViewById(R.id.userLogo);
            Glide.with(weakContext.get()).load(dataset.get(position).getProfileImageURL()).placeholder(R.drawable.user).into(imageView);
            ImageButton action1 = hostViewHolder.hostRow.findViewById(R.id.action1);
            action1.setOnClickListener((View v) -> {
                dataset.remove(position);
                updateAutohostList();
            });
            ImageButton action2 = hostViewHolder.hostRow.findViewById(R.id.action2);
            action2.setOnTouchListener((v, event) -> {
                if (event.getAction() ==
                        MotionEvent.ACTION_DOWN) {
                    dragListener.requestDrag(hostViewHolder);
                }
                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    /**
     * Posts the new Autohost List to Twitch
     * @author LethalMaus
     */
    private void updateAutohostList() {
        new Thread() {
            @Override
            public void run() {
                ChannelEntity channelEntity = StreamingYorkieDB.getInstance(weakContext.get()).channelDAO().getChannel();
                if (channelEntity != null) {
                    requestHandler.postBodyForSettingList(Integer.toString(channelEntity.getId()), dataset).initiate().sendRequest(true);
                }
            }
        }.start();
    }

    /**
     * Sets the dataset
     * @author LethalMaus
     * @param dataset List HostDataModel
     */
    public void setDataset(List<HostDataModel> dataset) {
        this.dataset = dataset;
        notifyDataSetChanged();
    }


    /**
     * Adds a channel to the Autohost List
     * @author LethalMaus
     * @param channelId String twitch ID
     */
    public void addToAutohostList(String channelId) {
        dataset.add(new HostDataModel(channelId));
        updateAutohostList();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(dataset, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(dataset, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(HostViewHolder hostViewHolder) {
        hostViewHolder.hostRow.setAlpha(0.4F);
    }

    @Override
    public void onRowClear(HostViewHolder hostViewHolder) {
        hostViewHolder.hostRow.setAlpha(1.0F);
        updateAutohostList();
    }
}
