package com.lethalmaus.streaming_yorkie.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.VODExportRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Recycler View Adapter for VODs
 * @author LethalMaus
 */
public class VODAdapter extends RecyclerView.Adapter<VODAdapter.VODViewHolder> {

    //All activities & contexts are weak referenced to avoid memory leaks
    private WeakReference<Activity> weakActivity;
    private WeakReference<Context> weakContext;
    private String appDirectory;

    //Display preferences
    private String vodsToDisplay;
    private ArrayList<String> vodDataset;
    private String actionButtonType;
    private String vodPath;

    //Page counts
    private int pageCount1;
    private int pageCount2;

    //VOD export properties
    private String title;
    private String description;
    private String tags;
    private boolean publish;
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
     */
    public VODAdapter(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        if (weakContext != null && weakContext.get() != null) {
            this.appDirectory = weakContext.get().getFilesDir().toString();
        }
    }

    /**
     * Sets Display preferences
     * @author LethalMaus
     * @param vodsToDisplay constant of which users are to be displayed
     * @param actionButtonType constant of which button is required in relation to the itemsToDisplay
     * @return an instance of itself for method building
     */
    public VODAdapter setDisplayPreferences(String vodsToDisplay, String actionButtonType) {
        this.vodsToDisplay = vodsToDisplay;
        this.actionButtonType = actionButtonType;
        getVODs();
        actionAllButton(actionButtonType);
        setPageCounts();
        setPageCountViews(weakActivity);
        //An empty row or table can be displayed based on if the dataset is empty or not
        if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
            if (vodDataset.size() > 0) {
                weakActivity.get().findViewById(R.id.table).setVisibility(View.VISIBLE);
                weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.GONE);
            } else {
                weakActivity.get().findViewById(R.id.table).setVisibility(View.GONE);
                weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.VISIBLE);
            }
            weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.GONE);
        }
        return this;
    }

    @Override
    @NonNull
    public VODAdapter.VODViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vodRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.vod_row, parent, false);
        return new VODAdapter.VODViewHolder(vodRow);
    }

    @Override
    public void onBindViewHolder(@NonNull VODAdapter.VODViewHolder vodViewHolder, int position) {
        if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing() && weakContext != null && weakContext.get() != null) {
            try {
                JSONObject vodObject = new JSONObject(new ReadFileHandler(weakContext, vodPath + File.separator + vodDataset.get(position)).readFile());

                TextView title = vodViewHolder.vodRow.findViewById(R.id.vod_title);
                title.setText(vodObject.getString("title"));

                TextView game = vodViewHolder.vodRow.findViewById(R.id.vod_game);
                game.setText(vodObject.getString("game"));

                TextView duration = vodViewHolder.vodRow.findViewById(R.id.vod_duration);
                duration.setText(vodObject.getString("length"));

                TextView createdAt = vodViewHolder.vodRow.findViewById(R.id.vod_createdAt);
                createdAt.setText(vodObject.getString("created_at"));

                ImageView preview = vodViewHolder.vodRow.findViewById(R.id.vod_preview);
                Glide.with(weakContext.get()).load(vodObject.getString("preview")).into(preview);

                ImageButton action = vodViewHolder.vodRow.findViewById(R.id.vod_action_button);
                editButton(action, actionButtonType, vodObject.getString("_id"));

                ProgressBar progressBar = weakActivity.get().findViewById(R.id.progressbar);
                progressBar.setVisibility(View.GONE);

            } catch (JSONException e) {
                new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return vodDataset != null ? vodDataset.size() : 0;
    }

    /**
     * Gets the Dataset (list of VOD ids) based on itemsToDisplay
     * @author LethalMaus
     */
    private void getVODs() {
        if (vodsToDisplay.contains("NEW")) {
            vodPath = Globals.VOD_PATH;
        } else if (vodsToDisplay.contains("EXPORTED")) {
            vodPath = Globals.VOD_EXPORTED_PATH;
        }
        vodDataset = new ReadFileHandler(weakContext, vodPath).readFileNames();
    }

    /**
     * Edits the button based on the actionButtonType
     * @author LethalMaus
     * @param button which button is to be changed
     * @param actionButtonType a constant of the action button type
     * @param vodID the user id which is related to the button
     */
    private void editButton(ImageButton button, String actionButtonType, String vodID) {
        if (actionButtonType.contentEquals("EXPORT") && !new File(appDirectory + File.separator + Globals.VOD_EXPORTED_PATH + File.separator + vodID).exists()) {
            exportButton(button, vodID);
        } else if (actionButtonType.contentEquals("DELETE") && !new File(appDirectory + File.separator + Globals.VOD_PATH + File.separator + vodID).exists()) {
            deleteButton(button, vodID);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    /**
     * Button for deleting a VOD that no longer is available on twitch.
     * @author LethalMaus
     * @param imageButton button view
     * @param vodID exported VOD to be deleted
     */
    private void deleteButton(ImageButton imageButton, final String vodID) {
        imageButton.setImageResource(R.drawable.delete);
        imageButton.setTag("DELETE_BUTTON");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteFileHandler(weakContext, Globals.VOD_EXPORTED_PATH + File.separator + vodID).run();
                vodDataset.remove(vodID);
                pageCount2--;
                setPageCountViews(weakActivity);
                notifyDataSetChanged();
            }
        });
    }

    /**
     * Button for exporting a VOD that hasn't already been exported
     * @author LethalMaus
     * @param imageButton button view
     * @param vodID VOD to be exported
     */
    private void exportButton(final ImageButton imageButton, final String vodID) {
        imageButton.setImageResource(R.drawable.export);
        imageButton.setTag("EXPORT_BUTTON");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final JSONObject vod = new JSONObject(new ReadFileHandler(weakContext, Globals.VOD_PATH + File.separator + vodID).readFile());
                    title = vod.getString("title");
                    description = vod.getString("description");
                    tags = vod.getString("tag_list");

                    final Dialog dialog = new Dialog(weakActivity.get());
                    dialog.setTitle("VOD Export");
                    dialog.setContentView(R.layout.vod_export_dialog);

                    final EditText exportDialogTitle = dialog.findViewById(R.id.vod_export_dialog_title_text);
                    exportDialogTitle.setText(title);

                    final EditText exportDialogDescription = dialog.findViewById(R.id.vod_export_dialog_description_text);
                    exportDialogDescription.setText(description);

                    final EditText exportDialogTags = dialog.findViewById(R.id.vod_export_dialog_tags_text);
                    exportDialogTags.setText(tags);

                    final Switch exportDialogPublish = dialog.findViewById(R.id.vod_export_dialog_publish);
                    exportDialogPublish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            publish = isChecked;
                        }
                    });

                    final Switch exportDialogSplit = dialog.findViewById(R.id.vod_export_dialog_split);
                    exportDialogSplit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            split = isChecked;
                        }
                    });

                    ImageButton cancelButton = dialog.findViewById(R.id.vod_export_dialog_cancel);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(weakContext.get(), "Export cancelled", Toast.LENGTH_SHORT).show();
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
                            new VODExportRequestHandler(weakActivity, weakContext).export(vodID, title, description, tags, publish, split);
                            imageButton.setVisibility(View.GONE);
                            Toast.makeText(weakContext.get(), "Starting export", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } catch (JSONException e) {
                    if (weakContext != null && weakContext.get() != null) {
                        Toast.makeText(weakContext.get(), "Twitch export could not be initiated.", Toast.LENGTH_SHORT).show();
                    }
                    new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
                }
            }
        });
    }

    /** TODO this needs to be finished or removed
     * Button for exporting/deleting all VODS within menu, that can be exported/deleted.
     * @author LethalMaus
     * @param actionButtonType type of button the actionAll should contain
     */
    private void actionAllButton(String actionButtonType) {
        final ImageButton imageButton = weakActivity.get().findViewById(R.id.export_delete_all);
        if (actionButtonType.contentEquals("EXPORT")) {
            imageButton.setImageResource(R.drawable.export);
            imageButton.setTag("EXPORT_ALL_BUTTON");
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (String vodID : vodDataset) {
                        if (!new File(appDirectory + File.separator + Globals.VOD_EXPORTED_PATH + File.separator + vodID).exists()) {
                            try {
                                JSONObject vod = new JSONObject(new ReadFileHandler(weakContext, Globals.VOD_PATH + File.separator + vodID).readFile());
                                new VODExportRequestHandler(weakActivity, weakContext).export(vodID, vod.getString("title"), vod.getString("description"), vod.getString("tag_list"), false, false);
                                pageCount1--;
                            } catch (JSONException e) {
                                if (weakContext != null && weakContext.get() != null) {
                                    Toast.makeText(weakContext.get(), "Twitch export could not be initiated.", Toast.LENGTH_SHORT).show();
                                }
                                new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
                            }
                        }
                    }
                    setPageCountViews(weakActivity);
                    notifyDataSetChanged();
                }
            });
        } else if (actionButtonType.contentEquals("DELETE")) {
            imageButton.setImageResource(R.drawable.delete);
            imageButton.setTag("DELETE_ALL_BUTTON");
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (String vodID : vodDataset) {
                        if (!new File(appDirectory + File.separator + Globals.VOD_PATH + File.separator + vodID).exists()) {
                            new DeleteFileHandler(weakContext, Globals.VOD_EXPORTED_PATH + File.separator + vodID).run();
                            vodDataset.remove(vodID);
                            pageCount2--;
                        }
                    }
                    setPageCountViews(weakActivity);
                    notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * Method for counting files within user directories
     * @author LethalMaus
     */
    private void setPageCounts() {
        pageCount1 = new ReadFileHandler(weakContext, Globals.VOD_PATH).countFiles();
        pageCount2 = new ReadFileHandler(weakContext, Globals.VOD_EXPORTED_PATH).countFiles();
    }

    /**
     * Method for setting the page count views
     * @author LethalMaus
     * @param weakActivity weak reference of an activity which contains the views
     */
    private void setPageCountViews(WeakReference<Activity> weakActivity) {
        if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
            TextView page1 = weakActivity.get().findViewById(R.id.count1);
            TextView page2 = weakActivity.get().findViewById(R.id.count2);
            page1.setText(String.valueOf(pageCount1));
            page2.setText(String.valueOf(pageCount2));
        }
    }
}
