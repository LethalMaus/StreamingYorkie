package com.lethalmaus.streaming_yorkie.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.VODEntity;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.VODExportRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Recycler View Adapter for VODs
 * @author LethalMaus
 */
public class VODAdapter extends RecyclerView.Adapter<VODAdapter.VODViewHolder> {

    //All activities & contexts are weak referenced to avoid memory leaks
    private WeakReference<Activity> weakActivity;
    private WeakReference<Context> weakContext;
    private RecyclerView recyclerView;
    private String vodsType;
    private String actionButtonType1;
    private String actionButtonType2;
    private int pageCount1;
    private int pageCount2;
    private int pageCount3;
    private int currentPageCount = 0;
    private StreamingYorkieDB streamingYorkieDB;
    private VODExportRequestHandler vodExportRequestHandler;

    //VOD export properties
    private String title;
    private String description;
    private String tags;
    private boolean visibility;
    private boolean split;

    /**
     * Simple View Holder for loading the View with a Dataset Row
     * @author LethalMaus
     */
    static class VODViewHolder extends RecyclerView.ViewHolder {

        View vodRow;

        /**
         * Holder for VOD View
         * @param vodRow View for VOD Row
         */
        VODViewHolder(View vodRow) {
            super(vodRow);
            this.vodRow = vodRow;
        }
    }

    /**
     * Adapter for displaying a VOD Dataset
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param recyclerView weak referenced recyclerView
     */
    public VODAdapter(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> recyclerView) {
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        vodExportRequestHandler = new VODExportRequestHandler(weakActivity, weakContext, recyclerView);
        streamingYorkieDB = StreamingYorkieDB.getInstance(weakContext.get());
    }

    /**
     * Sets Display preferences
     * @author LethalMaus
     * @param vodsType type of vods that are to be displayed
     * @param actionButtonType1 constant of which button is required in relation to the itemsType
     * @param actionButtonType2 constant of which button is required in relation to the itemsType
     * @return an instance of itself for method building
     */
    public VODAdapter setDisplayPreferences(String vodsType, String actionButtonType1, String actionButtonType2) {
        this.vodsType = vodsType;
        this.actionButtonType1 = actionButtonType1;
        this.actionButtonType2 = actionButtonType2;
        return this;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    @NonNull
    public VODAdapter.VODViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vodRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.vod_row, parent, false);
        return new VODViewHolder(vodRow);
    }

    @Override
    public void onBindViewHolder(@NonNull final VODViewHolder vodViewHolder, final int position) {
        weakActivity.get().runOnUiThread(new Runnable() {
            public void run() {
                new VODAsyncTask(weakActivity, weakContext,VODAdapter.this, vodViewHolder, streamingYorkieDB, vodsType, position, actionButtonType1, actionButtonType2).execute();
            }
        });

    }

    /**
     * Async Task to request VOD and display it
     * @author LethalMaus
     */
    private static class VODAsyncTask extends AsyncTask<Void, Void, VODEntity> {

        private WeakReference<Activity> weakActivity;
        private WeakReference<Context> weakContext;
        private VODAdapter vodAdapter;
        private VODAdapter.VODViewHolder vodViewHolder;
        private StreamingYorkieDB streamingYorkieDB;
        private String vodsType;
        private int position;
        private String actionButtonType1;
        private String actionButtonType2;

        /**
         * Async Task constructor
         * @author LethalMaus
         * @param weakActivity inner class reference
         * @param weakContext inner class reference
         * @param vodAdapter inner class reference
         * @param vodViewHolder inner class reference
         * @param streamingYorkieDB inner class reference
         * @param vodsType inner class reference
         * @param position inner class reference
         * @param actionButtonType1 inner class reference
         * @param actionButtonType2 inner class reference
         */
        VODAsyncTask(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, VODAdapter vodAdapter, VODAdapter.VODViewHolder vodViewHolder, StreamingYorkieDB streamingYorkieDB, String vodsType, int position, String actionButtonType1, String actionButtonType2) {
            this.weakActivity = weakActivity;
            this.weakContext = weakContext;
            this.vodAdapter = vodAdapter;
            this.vodViewHolder = vodViewHolder;
            this.streamingYorkieDB = streamingYorkieDB;
            this.vodsType = vodsType;
            this.position = position;
            this.actionButtonType1 = actionButtonType1;
            this.actionButtonType2 = actionButtonType2;
        }

        @Override
        protected VODEntity doInBackground(Void... params) {
            VODEntity vodEntity;
            if (vodsType.contentEquals("CURRENT")) {
                vodEntity = streamingYorkieDB.vodDAO().getCurrentVODByPosition(position);
            } else if (vodsType.contentEquals("EXPORTED")) {
                vodEntity = streamingYorkieDB.vodDAO().getExportedVODByPosition(position);
            } else {
                vodEntity = streamingYorkieDB.vodDAO().getExcludedVODByPosition(position);
            }
            return vodEntity;
        }

        @Override
        protected void onPostExecute(final VODEntity vodEntity) {
            if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing() && weakContext != null && weakContext.get() != null) {
                if (vodEntity != null) {
                    TextView title = vodViewHolder.vodRow.findViewById(R.id.vod_title);
                    title.setText(vodEntity.getTitle());
                    TextView game = vodViewHolder.vodRow.findViewById(R.id.vod_game);
                    game.setText(vodEntity.getGame());
                    TextView duration = vodViewHolder.vodRow.findViewById(R.id.vod_duration);
                    duration.setText(vodEntity.getLength());
                    TextView createdAt = vodViewHolder.vodRow.findViewById(R.id.vod_createdAt);
                    createdAt.setText(vodEntity.getCreated_at());

                    ImageView preview = vodViewHolder.vodRow.findViewById(R.id.vod_preview);
                    Glide.with(weakContext.get()).load(vodEntity.getPreview()).into(preview);

                    final String vodUrl = vodEntity.getUrl();
                    preview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            weakActivity.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(vodUrl)));
                        }
                    });

                    final ImageButton action1 = vodViewHolder.vodRow.findViewById(R.id.vod_action_button1);
                    action1.setVisibility(View.VISIBLE);
                    final ImageButton action2 = vodViewHolder.vodRow.findViewById(R.id.vod_action_button2);
                    action2.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        public void run() {
                            vodAdapter.editButton(action1, actionButtonType1, vodEntity);
                            vodAdapter.editButton(action2, actionButtonType2, vodEntity);
                        }
                    }).start();
                }
                weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Takes action once a dataset has been changed then notifies UI
     * @author LethalMaus
     */
    public void datasetChanged() {
        new Thread(new Runnable() {
            public void run() {
                setPageCounts();
                actionAllButton();
                weakActivity.get().runOnUiThread(
                        new Runnable() {
                            public void run() {
                                setPageCountViews();
                                if (currentPageCount > 0) {
                                    weakActivity.get().findViewById(R.id.table).setVisibility(View.VISIBLE);
                                    weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.GONE);
                                } else {
                                    weakActivity.get().findViewById(R.id.action_all_button).setVisibility(View.GONE);
                                    weakActivity.get().findViewById(R.id.table).setVisibility(View.GONE);
                                    weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.VISIBLE);
                                }
                                weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                            }
                        });
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return currentPageCount < 0 ? 0 : currentPageCount;
    }

    /**
     * Edits the button based on the actionButtonType
     * @author LethalMaus
     * @param button which button is to be changed
     * @param actionButtonType a constant of the action button type
     * @param vodEntity the channel id which is related to the button
     */
    private void editButton(final ImageButton button, final String actionButtonType, final VODEntity vodEntity) {
        if (actionButtonType != null && actionButtonType.contentEquals("EXPORT") && !vodEntity.isExported()) {
            exportButton(button, vodEntity);
        } else if (actionButtonType != null && actionButtonType.contentEquals("DELETE")) {
            deleteButton(button, vodEntity);
        } else if (actionButtonType != null && actionButtonType.contentEquals("EXCLUDE")) {
            excludeButton(button, vodEntity);
        } else if (actionButtonType != null && actionButtonType.contentEquals("INCLUDE")) {
            includeButton(button, vodEntity);
        } else {
            weakActivity.get().runOnUiThread(() ->
                    button.setVisibility(View.INVISIBLE)
            );
        }
    }

    /**
     * Button for deleting a VOD from the exported view and current view (if expired in twitch)
     * @author LethalMaus
     * @param imageButton button view
     * @param vodEntity exported VODEntity to be deleted
     */
    private void deleteButton(final ImageButton imageButton, final VODEntity vodEntity) {
        weakActivity.get().runOnUiThread(() -> {
            imageButton.setImageResource(R.drawable.delete);
            imageButton.setTag("DELETE_BUTTON");
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(() -> {
                        streamingYorkieDB.vodDAO().updateVODExportStatusById(false, vodEntity.getId());
                        datasetChanged();
                    }).start();
                }
            });
        });
    }

    /**
     * Button for exporting a VOD that hasn't already been exported
     * @author LethalMaus
     * @param imageButton button view
     * @param vodEntity VODEntity to be exported
     */
    private void exportButton(final ImageButton imageButton, final VODEntity vodEntity) {
        weakActivity.get().runOnUiThread(
                new Runnable() {
                    public void run() {
                        imageButton.setImageResource(R.drawable.export);
                        imageButton.setTag("EXPORT_BUTTON");
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        title = vodEntity.getTitle();
                                        description = vodEntity.getDescription();
                                        tags = vodEntity.getTag_list();
                                        weakActivity.get().runOnUiThread(
                                                new Runnable() {
                                                    public void run() {
                                                        final Dialog dialog = new Dialog(weakActivity.get());
                                                        dialog.setTitle("VOD Export");
                                                        dialog.setContentView(R.layout.vod_export_dialog);

                                                        final EditText exportDialogTitle = dialog.findViewById(R.id.vod_export_dialog_title_text);
                                                        exportDialogTitle.setText(title);

                                                        final EditText exportDialogDescription = dialog.findViewById(R.id.vod_export_dialog_description_text);
                                                        exportDialogDescription.setText(description);

                                                        final EditText exportDialogTags = dialog.findViewById(R.id.vod_export_dialog_tags_text);
                                                        exportDialogTags.setText(tags);

                                                        RadioGroup radioGroup = dialog.findViewById(R.id.vod_export_dialog_visibility);
                                                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                            @Override
                                                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                                                visibility = (checkedId == R.id.vod_export_dialog_visibility_public);
                                                            }
                                                        });

                                                        final Switch exportDialogSplit = dialog.findViewById(R.id.vod_export_dialog_split);
                                                        exportDialogSplit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                            @Override
                                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                split = isChecked;
                                                            }
                                                        });

                                                        try {
                                                            if (new File(weakActivity.get().getFilesDir() + File.separator + "SETTINGS_VOD").exists()) {
                                                                JSONObject settings = new JSONObject(new ReadFileHandler(weakActivity, weakContext, "SETTINGS_VOD").readFile());
                                                                if (settings.getBoolean(Globals.SETTINGS_VISIBILITY)) {
                                                                    radioGroup.check(R.id.vod_export_dialog_visibility_public);
                                                                } else {
                                                                    radioGroup.check(R.id.vod_export_dialog_visibility_private);
                                                                }
                                                                exportDialogSplit.setChecked(settings.getBoolean(Globals.SETTINGS_SPLIT));
                                                            } else {
                                                                radioGroup.check(R.id.vod_export_dialog_visibility_private);
                                                                exportDialogSplit.setChecked(false);
                                                            }
                                                        } catch (JSONException e) {
                                                            if (weakActivity != null && weakActivity.get() != null) {
                                                                Toast.makeText(weakActivity.get(), "Twitch export could not be initiated.", Toast.LENGTH_SHORT).show();
                                                            }
                                                            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Twitch export could not be initiated. | " + e.toString(), true).run();
                                                        }

                                                        ImageButton cancelButton = dialog.findViewById(R.id.vod_export_dialog_cancel);
                                                        cancelButton.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Toast.makeText(weakActivity.get(), "Export cancelled", Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                            }
                                                        });

                                                        final ImageButton exportButton = dialog.findViewById(R.id.vod_export_dialog_export);
                                                        exportButton.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                if (!exportDialogTitle.getText().toString().isEmpty()) {
                                                                    title = exportDialogTitle.getText().toString();
                                                                }
                                                                if (!exportDialogDescription.getText().toString().isEmpty()) {
                                                                    description = exportDialogDescription.getText().toString();
                                                                }
                                                                if (!exportDialogTags.getText().toString().isEmpty()) {
                                                                    tags = exportDialogTags.getText().toString();
                                                                }
                                                                new Thread(new Runnable() {
                                                                    public void run() {
                                                                        JSONObject body = new JSONObject();
                                                                        try {
                                                                            body.put("title", title);
                                                                            body.put("description", description);
                                                                            body.put("tag_list", tags);
                                                                            body.put("private", !visibility);
                                                                            body.put("do_split", split);
                                                                        } catch (JSONException e) {
                                                                            if (weakActivity != null && weakActivity.get() != null) {
                                                                                weakActivity.get().runOnUiThread(
                                                                                        new Runnable() {
                                                                                            public void run() {
                                                                                                Toast.makeText(weakActivity.get(), "Twitch export content could not be set.", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                );
                                                                            }
                                                                            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Twitch export content could not be set. | " + e.toString(), true).run();
                                                                        }
                                                                        vodExportRequestHandler.setVodId(vodEntity.getId()).setPostBody(body).sendRequest(true);
                                                                        if (weakActivity != null && weakActivity.get() != null) {
                                                                            weakActivity.get().runOnUiThread(
                                                                                    new Runnable() {
                                                                                        public void run() {
                                                                                            imageButton.setVisibility(View.GONE);
                                                                                            Toast.makeText(weakActivity.get(), "Starting export", Toast.LENGTH_SHORT).show();
                                                                                            dialog.dismiss();
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                }).start();
                                                            }
                                                        });
                                                        dialog.show();
                                                    }
                                                });
                                    }
                                }).start();
                            }
                        });
                    }
                });
    }

    /**
     * Button for excluding a VOD from automation and other views
     * @author LethalMaus
     * @param imageButton button view
     * @param vodEntity VOD to be excluded
     */
    private void excludeButton(final ImageButton imageButton, final VODEntity vodEntity) {
        weakActivity.get().runOnUiThread(
                new Runnable() {
                    public void run() {
                        imageButton.setImageResource(R.drawable.excluded);
                        imageButton.setTag("EXCLUDE_BUTTON");
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        streamingYorkieDB.vodDAO().updateVODExclusionStatusById(true, vodEntity.getId());
                                        datasetChanged();
                                    }
                                }).start();
                            }
                        });
                    }
                });
    }

    /**
     * Button for including a VOD to automation and other views
     * @author LethalMaus
     * @param imageButton button view
     * @param vodEntity VODEntity to be included
     */
    private void includeButton(final ImageButton imageButton, final VODEntity vodEntity) {
        weakActivity.get().runOnUiThread(
                new Runnable() {
                    public void run() {
                        imageButton.setImageResource(R.drawable.included);
                        imageButton.setTag("INCLUDE_BUTTON");
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        streamingYorkieDB.vodDAO().updateVODExclusionStatusById(false, vodEntity.getId());
                                        datasetChanged();
                                    }
                                }).start();
                            }
                        });
                    }
                });
    }

    /**
     * Button for exporting/deleting/including all VODS within menu.
     * @author LethalMaus
     */
    private void actionAllButton() {
        weakActivity.get().runOnUiThread(
                new Runnable() {
                    public void run() {
                        final ImageButton imageButton = weakActivity.get().findViewById(R.id.action_all_button);
                        if (currentPageCount > 1) {
                            imageButton.setVisibility(View.VISIBLE);
                            if (vodsType.contentEquals("CURRENT")) {
                                imageButton.setImageResource(R.drawable.export);
                                imageButton.setTag("EXPORT_ALL_BUTTON");
                                imageButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new Thread(new Runnable() {
                                            public void run() {
                                                for (int i = 0; i < currentPageCount; i++) {
                                                    VODEntity vodEntity = streamingYorkieDB.vodDAO().getCurrentVODByPosition(i);
                                                    if (!vodEntity.isExported()) {
                                                        try {
                                                            boolean visibility = false;
                                                            boolean split = false;
                                                            if (new File(weakContext.get().getFilesDir() + File.separator + "SETTINGS_VOD").exists()) {
                                                                JSONObject settings = new JSONObject(new ReadFileHandler(weakActivity, weakContext, "SETTINGS_VOD").readFile());
                                                                visibility = settings.getBoolean(Globals.SETTINGS_VISIBILITY);
                                                                split = settings.getBoolean(Globals.SETTINGS_SPLIT);
                                                            }
                                                            JSONObject body = new JSONObject();
                                                            try {
                                                                body.put("title", vodEntity.getTitle());
                                                                body.put("description", vodEntity.getDescription());
                                                                body.put("tag_list", vodEntity.getTag_list());
                                                                body.put("private", !visibility);
                                                                body.put("do_split", split);
                                                            } catch (JSONException e) {
                                                                if (weakActivity != null && weakActivity.get() != null) {
                                                                    weakActivity.get().runOnUiThread(
                                                                            new Runnable() {
                                                                                public void run() {
                                                                                    Toast.makeText(weakActivity.get(), "Twitch export content could not be set.", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                    );
                                                                }
                                                                new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Twitch export content could not be set. | " + e.toString(), true).run();
                                                            }
                                                            vodExportRequestHandler.setVodId(vodEntity.getId()).setPostBody(body).sendRequest(true);
                                                        } catch (JSONException e) {
                                                            if (weakActivity != null && weakActivity.get() != null) {
                                                                weakActivity.get().runOnUiThread(
                                                                        new Runnable() {
                                                                            public void run() {
                                                                                Toast.makeText(weakActivity.get(), "All Twitch exports could not be initiated.", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                );
                                                            }
                                                            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "All Twitch exports could not be initiated. | " + e.toString(), true).run();
                                                        }
                                                    }
                                                }
                                                datasetChanged();
                                            }
                                        }).start();
                                    }
                                });
                            } else if (vodsType.contentEquals("EXPORTED")) {
                                imageButton.setImageResource(R.drawable.delete);
                                imageButton.setTag("DELETE_ALL_BUTTON");
                                imageButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new Thread(new Runnable() {
                                            public void run() {
                                                streamingYorkieDB.vodDAO().removeExportedStatus();
                                                datasetChanged();
                                            }
                                        }).start();
                                    }
                                });
                            } else if (vodsType.contentEquals("EXCLUDED")) {
                                imageButton.setImageResource(R.drawable.included);
                                imageButton.setTag("INCLUDE_ALL_BUTTON");
                                imageButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new Thread(new Runnable() {
                                            public void run() {
                                                streamingYorkieDB.vodDAO().removeExcludedStatus();
                                                datasetChanged();
                                            }
                                        }).start();
                                    }
                                });
                            } else {
                                imageButton.setVisibility(View.GONE);
                            }
                        } else {
                            imageButton.setVisibility(View.GONE);
                        }
                    }
                });
    }

    /**
     * Method for counting files within channel directories
     * @author LethalMaus
     */
    public void setPageCounts() {
        pageCount1 = streamingYorkieDB.vodDAO().getCurrentVODsCount();
        pageCount2 = streamingYorkieDB.vodDAO().getExportedVODsCount();
        pageCount3 = streamingYorkieDB.vodDAO().getExcludedVODsCount();
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (vodsType.contentEquals("CURRENT")) {
                    currentPageCount = pageCount1;
                } else if (vodsType.contentEquals("EXPORTED")) {
                    currentPageCount = pageCount2;
                } else if (vodsType.contentEquals("EXCLUDED")) {
                    currentPageCount = pageCount3;
                }
                notifyDataSetChanged();
            }
        });
    }

    /**
     * Method for setting the page count views
     * @author LethalMaus
     */
    private void setPageCountViews() {
        if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
            weakActivity.get().runOnUiThread(
                    new Runnable() {
                        public void run() {
                            TextView page1 = weakActivity.get().findViewById(R.id.count1);
                            TextView page2 = weakActivity.get().findViewById(R.id.count2);
                            TextView page3 = weakActivity.get().findViewById(R.id.count3);
                            page1.setText(String.valueOf(pageCount1));
                            page2.setText(String.valueOf(pageCount2));
                            page3.setText(String.valueOf(pageCount3));
                        }
                    });
        }
    }
}
