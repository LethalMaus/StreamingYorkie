package com.lethalmaus.streaming_yorkie.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.ChannelEntity;
import com.lethalmaus.streaming_yorkie.entity.LurkEntity;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.StreamStatusRequestHandler;
import com.lethalmaus.streaming_yorkie.service.LurkService;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.ListenerAdapter;

import java.io.File;
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
    }

    @Override
    @NonNull
    public LurkAdapter.LurkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View lurkRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.lurk_row, parent, false);
        return new LurkAdapter.LurkViewHolder(lurkRow);
    }

    @Override
    public void onBindViewHolder(@NonNull final LurkAdapter.LurkViewHolder lurkViewHolder, final int position) {
        weakActivity.get().runOnUiThread( () ->
            new LurkAdapter.LurkAsyncTask(weakActivity, weakContext, LurkAdapter.this, lurkViewHolder, streamingYorkieDB, position).execute()
        );
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
                ImageView imageView = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_logo);
                if (lurk.getLogo() != null && !lurk.getLogo().isEmpty()) {
                    Glide.with(weakContext.get()).load(lurk.getLogo()).placeholder(R.drawable.user).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.user);
                }
                if (!lurk.isChannelIsToBeLurked()) {
                    ImageButton button1 = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_button1);
                    button1.setImageResource(R.drawable.lurk);
                    button1.setOnClickListener((View v) ->
                            new Thread() {
                                public void run() {
                                    lurk.setChannelIsToBeLurked(true);
                                    streamingYorkieDB.lurkDAO().updateLurk(lurk);
                                    lurkAdapter.datasetChanged(false);
                                }
                            }.start()
                    );
                } else {
                    ImageButton button1 = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_button1);
                    button1.setImageResource(R.drawable.unlurk);
                    button1.setOnClickListener((View v) ->
                            new Thread() {
                                public void run() {
                                    lurk.setChannelIsToBeLurked(false);
                                    lurk.setHtml(null);
                                    streamingYorkieDB.lurkDAO().updateLurk(lurk);
                                    lurkAdapter.datasetChanged(false);
                                }
                            }.start()
                    );
                }
                if (lurk.getHtml() == null || lurk.getHtml().isEmpty()) {
                    ImageButton button2 = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_button2);
                    button2.setImageResource(R.drawable.delete);
                    button2.setOnClickListener((View v) ->
                            new Thread() {
                                public void run() {
                                    streamingYorkieDB.lurkDAO().deleteLurkByChannelName(lurk.getChannelName());
                                    lurkAdapter.datasetChanged(false);
                                }
                            }.start()
                    );
                } else {
                    ImageButton button2 = lurkViewHolder.lurkRow.findViewById(R.id.lurkrow_button2);
                    button2.setImageResource(R.drawable.message);
                    button2.setOnClickListener((View v) -> {
                        final Dialog dialog = new Dialog(weakActivity.get());
                        dialog.setTitle(R.string.lurk_dialog_title);
                        dialog.setContentView(R.layout.lurk_message_dialog);
                        dialog.findViewById(R.id.dialog_cancel).setOnClickListener((View view) ->
                                dialog.dismiss()
                        );
                        dialog.findViewById(R.id.dialog_send).setOnClickListener((View view) -> {
                            EditText editText = dialog.findViewById(R.id.dialog_message);
                            if (!editText.getText().toString().trim().isEmpty()) {
                                lurkAdapter.sendLurkMessage(lurk.getChannelName().toLowerCase(), editText.getText().toString());
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
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
     * Method for getting Lurk HTML
     * @author LethalMaus
     */
    private void getStreamersOnlineStatus() {
        new Thread() {
            public void run() {
                if (streamingYorkieDB.lurkDAO().getChannelsToBeLurkedCount() > 0) {
                    new StreamStatusRequestHandler(weakActivity, weakContext, weakRecyclerView) {
                        @Override
                        public void onCompletion() {
                            if (weakContext != null && weakContext.get() != null) {
                                new Thread() {
                                    public void run() {
                                        Intent intent = new Intent(weakContext.get(), LurkService.class).setAction("MANUAL_LURK");
                                        if (Build.VERSION.SDK_INT < 28) {
                                            weakContext.get().startService(intent);
                                        } else {
                                            weakContext.get().startForegroundService(intent);
                                        }
                                    }
                                }.start();
                                if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
                                    weakActivity.get().runOnUiThread(() ->
                                            datasetChanged(true)
                                    );
                                }
                            }
                        }
                    }.newRequest(streamingYorkieDB.lurkDAO().getChannelIdsToBeLurked()).initiate().sendRequest();
                }
            }
        }.start();
    }

    /**
     * Takes action once a dataset has been changed then notifies UI
     * @author LethalMaus
     * @param updateViewOnly boolean to update view only or db as well
     */
    public void datasetChanged(boolean updateViewOnly) {
        if (!updateViewOnly) {
            new Thread(){
                public void run() {
                    setLurkCount();
                    if (streamingYorkieDB.lurkDAO().getChannelsToBeLurkedCount() > 0) {
                        getStreamersOnlineStatus();
                    } else {
                        Intent intent = new Intent(weakActivity.get(), LurkService.class);
                        weakActivity.get().stopService(intent);
                    }
                }
            }.start();
        }
    }

    /**
     * Method for counting files within channel directories
     * @author LethalMaus
     */
    private void setLurkCount() {
        lurkCount = streamingYorkieDB.lurkDAO().getLurkCount();
        weakRecyclerView.get().post(LurkAdapter.this::notifyDataSetChanged);
    }

    /**
     * Method for sending a message to the lurked channel chat
     * @author LethalMaus
     * @param channel String channel name
     * @param message String message to be sent
     */
    private void sendLurkMessage(String channel, String message) {
        new Thread() {
            public void run() {
                if (new File(weakActivity.get().getFilesDir().toString() + File.separator + "TOKEN").exists()) {
                    String token = new ReadFileHandler(null, new WeakReference<>(weakContext.get()), "TOKEN").readFile();
                    ChannelEntity channelEntity = streamingYorkieDB.channelDAO().getChannel();
                    if (channelEntity != null) {
                        Configuration configuration = new Configuration.Builder()
                                .setAutoNickChange(false)
                                .setOnJoinWhoEnabled(false)
                                .setCapEnabled(true)
                                .addCapHandler(new EnableCapHandler("twitch.tv/membership"))
                                .addServer("irc.twitch.tv")
                                .setName(channelEntity.getDisplay_name())
                                .setServerPassword("oauth:" + token)
                                .addAutoJoinChannel("#" + channel)
                                .addListener(new ListenerAdapter() {})
                                .buildConfiguration();
                        PircBotX bot = new PircBotX(configuration);
                        new Thread() {
                            public void run() {
                                try {
                                    bot.startBot();
                                } catch (Exception e) {
                                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error starting chat: " + e.toString(), true).run();
                                }
                            }
                        }.start();
                        try {
                            //Wait for bot to start as the above method blocks the thread
                            Thread.sleep(1000);
                            if (bot.isConnected()) {
                                bot.sendIRC().message("#" + channel, message);
                                if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
                                    weakActivity.get().runOnUiThread(() ->
                                            Toast.makeText(weakActivity.get(), "Message sent", Toast.LENGTH_SHORT).show()
                                    );
                                }
                            }
                        } catch (Exception e) {
                            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error sending to chat: " + e.toString(), true).run();
                        } finally {
                            bot.stopBotReconnect();
                            bot.close();
                        }
                    }
                }
            }
        }.start();
    }
}
