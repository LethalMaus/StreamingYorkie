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
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.LurkEntity;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.LurkRequestHandler;
import com.lethalmaus.streaming_yorkie.service.LurkService;

import java.lang.ref.WeakReference;

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
    private int lurkCount = 0;

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
                new LurkAdapter.LurkAsyncTask(weakActivity, weakContext, LurkAdapter.this, lurkViewHolder, streamingYorkieDB, position).execute();
            }
        });
    }

    /**
     * Async Task to request Lurks and display it
     * @author LethalMaus
     */
    private static class LurkAsyncTask extends AsyncTask<Void, Void, LurkEntity> {

        private WeakReference<Activity> weakActivity;
        private WeakReference<Context> weakContext;
        private StreamingYorkieDB streamingYorkieDB;
        private LurkAdapter lurkAdapter;
        private LurkAdapter.LurkViewHolder lurkViewHolder;
        private int position;

        /**
         * Async Task constructor
         * @author LethalMaus
         * @param weakActivity inner class reference
         * @param weakContext inner class reference
         * @param lurkAdapter inner class reference
         * @param lurkViewHolder inner class reference
         * @param streamingYorkieDB inner class reference
         * @param position inner class reference
         */
        LurkAsyncTask(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, LurkAdapter lurkAdapter, LurkAdapter.LurkViewHolder lurkViewHolder, StreamingYorkieDB streamingYorkieDB, int position) {
            this.weakActivity = weakActivity;
            this.weakContext = weakContext;
            this.streamingYorkieDB = streamingYorkieDB;
            this.lurkAdapter = lurkAdapter;
            this.lurkViewHolder = lurkViewHolder;
            this.position = position;
        }

        @Override
        protected LurkEntity doInBackground(Void... params) {
            return streamingYorkieDB.lurkDAO().getLurkByPosition(position);
        }

        @Override
        protected void onPostExecute(LurkEntity lurk) {
            if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing() && weakContext != null && weakContext.get() != null) {
                TextView textView = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_username);
                textView.setText(lurk.getChannelName());
                if (lurk.getLogo() != null && !lurk.getLogo().isEmpty()) {
                    ImageView imageView = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_logo);
                    Glide.with(weakContext.get()).load(lurk.getLogo()).placeholder(R.drawable.user).into(imageView);
                }
                if (!lurk.isChannelIsToBeLurked() || lurk.getHtml() == null || lurk.getHtml().isEmpty() ||  lurk.getBroadcastId() == null || lurk.getBroadcastId().isEmpty()) {
                    ImageButton button1 = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_button1);
                    button1.setImageResource(R.drawable.lurk);
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread() {
                                public void run() {
                                    lurk.setChannelIsToBeLurked(true);
                                    streamingYorkieDB.lurkDAO().updateLurk(lurk);
                                    lurkAdapter.datasetChanged();
                                }
                            }.start();
                        }
                    });
                    ImageButton button2 = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_button2);
                    button2.setImageResource(R.drawable.delete);
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread() {
                                public void run() {
                                    streamingYorkieDB.lurkDAO().deleteLurkByChannelName(lurk.getChannelName());
                                    lurkAdapter.datasetChanged();
                                }
                            }.start();
                        }
                    });
                } else {
                    ImageButton button1 = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_button1);
                    button1.setImageResource(R.drawable.unlurk);
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread() {
                                public void run() {
                                    lurk.setChannelIsToBeLurked(false);
                                    streamingYorkieDB.lurkDAO().updateLurk(lurk);
                                    lurkAdapter.datasetChanged();
                                }
                            }.start();
                        }
                    });
                    ImageButton button2 = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_button2);
                    button2.setImageResource(R.drawable.message);
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread() {
                                public void run() {
                                   //TODO open dialog & use chat bot to send message
                                }
                            }.start();
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return  lurkCount < 0 ? 0 : lurkCount;
    }

    /**
     * Method for getting Lurk objects
     * @author LethalMaus
     */
    private void getLurks() {
        new Thread() {
            public void run() {
                int lurkCount = getItemCount();
                if (lurkCount > 0) {
                    for (int i = 0; i < lurkCount; i++) {
                        final LurkEntity lurk = streamingYorkieDB.lurkDAO().getLurkByPosition(i);
                        final StringBuilder htmlInjection = new StringBuilder();
                        if (i == (lurkCount - 1)) {
                            new LurkRequestHandler(weakActivity, weakContext, weakRecyclerView) {
                                @Override
                                public void onCompletion() {
                                    if (lurk.getHtml() != null && !lurk.getHtml().isEmpty() && lurk.isChannelIsToBeLurked()) {
                                        htmlInjection.append(lurk.getHtml());
                                    }
                                    new WriteFileHandler(weakActivity, weakContext, "LURK.HTML", null, htmlInjection.toString(), false).writeToFileOrPath();
                                    new Thread() {
                                        public void run() {
                                            Intent intent = new Intent(weakActivity.get(), LurkService.class);
                                            if (Build.VERSION.SDK_INT < 28) {
                                                weakActivity.get().startService(intent);
                                            } else {
                                                weakActivity.get().startForegroundService(intent);
                                            }
                                        }
                                    }.start();
                                }
                            }.newRequest(lurk.getChannelName()).initiate().sendRequest();
                        } else if (!lurk.getHtml().isEmpty() && lurk.isChannelIsToBeLurked()) {
                            new LurkRequestHandler(weakActivity, weakContext, weakRecyclerView) {
                                @Override
                                public void onCompletion() {
                                    if (lurk.getHtml() != null && !lurk.getHtml().isEmpty() && lurk.isChannelIsToBeLurked()) {
                                        htmlInjection.append(lurk.getHtml());
                                    }
                                }
                            }.newRequest(lurk.getChannelName()).initiate().sendRequest();
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * Takes action once a dataset has been changed then notifies UI
     * @author LethalMaus
     */
    public void datasetChanged() {
        new Thread(){
            public void run() {
                setLurkCount();
                if (getItemCount() > 0) {
                    getLurks();
                } else {
                    Intent intent = new Intent(weakActivity.get(), LurkService.class);
                    weakActivity.get().stopService(intent);
                    new DeleteFileHandler(weakActivity, weakContext, "LURK.HTML").run();
                }
                weakActivity.get().runOnUiThread(
                        new Runnable() {
                            public void run() {
                                if (weakRecyclerView != null && weakRecyclerView.get() != null) {
                                    weakRecyclerView.get().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        });
            }
        }.start();
    }

    /**
     * Method for counting files within channel directories
     * @author LethalMaus
     */
    private void setLurkCount() {
        lurkCount = streamingYorkieDB.lurkDAO().getLurkCount();
        weakRecyclerView.get().post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}
