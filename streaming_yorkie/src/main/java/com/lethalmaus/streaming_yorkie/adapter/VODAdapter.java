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
import android.widget.RadioGroup;
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
    private String actionButtonType1;
    private String actionButtonType2;
    private String vodPath;

    //Page counts
    private int pageCount1;
    private int pageCount2;
    private int pageCount3;

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
     * @param actionButtonType1 constant of which button is required in relation to the itemsToDisplay
     * @param actionButtonType2 constant of which button is required in relation to the itemsToDisplay
     * @return an instance of itself for method building
     */
    public VODAdapter setDisplayPreferences(String vodsToDisplay, String actionButtonType1, String actionButtonType2) {
        this.vodsToDisplay = vodsToDisplay;
        this.actionButtonType1 = actionButtonType1;
        this.actionButtonType2 = actionButtonType2;
        getVODs();
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
                if (new File(weakContext.get().getFilesDir() + File.separator + Globals.VOD_PATH + File.separator + vodDataset.get(position)).exists()) {
                    JSONObject vodObject = new JSONObject(new ReadFileHandler(weakContext, Globals.VOD_PATH + File.separator + vodDataset.get(position)).readFile());

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

                    ImageButton action1 = vodViewHolder.vodRow.findViewById(R.id.vod_action_button1);
                    editButton(action1, actionButtonType1, vodObject.getString("_id"));

                    ImageButton action2 = vodViewHolder.vodRow.findViewById(R.id.vod_action_button2);
                    editButton(action2, actionButtonType2, vodObject.getString("_id"));

                    weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.GONE);
                } else {
                    new DeleteFileHandler(weakContext, vodPath + File.separator + vodDataset.get(position)).run();
                    vodDataset.remove(vodDataset.get(position));
                    setPageCountViews(weakActivity);
                    datasetChanged();
                }
            } catch(JSONException e){
                new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true);

            }
        }
    }

    /**
     * Takes action once a dataset has been changed then notifies UI
     */
    private void datasetChanged() {
        if (vodDataset != null && vodDataset.size() > 0) {
            if (vodDataset.size() > 1) {
                weakActivity.get().findViewById(R.id.action_all_button).setVisibility(View.VISIBLE);
            } else {
                weakActivity.get().findViewById(R.id.action_all_button).setVisibility(View.GONE);
            }
            weakActivity.get().findViewById(R.id.table).setVisibility(View.VISIBLE);
            weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.GONE);
        } else {
            weakActivity.get().findViewById(R.id.action_all_button).setVisibility(View.GONE);
            weakActivity.get().findViewById(R.id.table).setVisibility(View.GONE);
            weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
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
        if (new ReadFileHandler(weakContext, Globals.VOD_PATH).countFiles() > 0) {
            String actionAllButtonType = "";
            if (vodsToDisplay.contains("NEW")) {
                vodPath = Globals.VOD_PATH;
                actionAllButtonType = "EXPORT";
            } else if (vodsToDisplay.contains("EXPORTED")) {
                vodPath = Globals.VOD_EXPORTED_PATH;
                actionAllButtonType = "DELETE";
            } else if (vodsToDisplay.contains("EXCLUDED")) {
                vodPath = Globals.VOD_EXCLUDED_PATH;
                actionAllButtonType = "INCLUDE";
            }
            vodDataset = new ReadFileHandler(weakContext, vodPath).readFileNames();
            if (vodPath.contentEquals(Globals.VOD_PATH)) {
                vodDataset.removeAll(new ReadFileHandler(weakContext, Globals.VOD_EXCLUDED_PATH).readFileNames());
                if (new ReadFileHandler(weakContext, Globals.VOD_PATH).countFiles() - new ReadFileHandler(weakContext, Globals.VOD_EXPORTED_PATH).countFiles() > 1) {
                    actionAllButton(actionAllButtonType);
                } else {
                    weakActivity.get().findViewById(R.id.action_all_button).setVisibility(View.GONE);
                }
            } else if (vodDataset.size() > 1) {
                actionAllButton(actionAllButtonType);
            } else {
                weakActivity.get().findViewById(R.id.action_all_button).setVisibility(View.GONE);
            }
        }
    }

    /**
     * Edits the button based on the actionButtonType
     * @author LethalMaus
     * @param button which button is to be changed
     * @param actionButtonType a constant of the action button type
     * @param vodID the user id which is related to the button
     */
    private void editButton(ImageButton button, String actionButtonType, String vodID) {
        if (actionButtonType != null && actionButtonType.contentEquals("EXPORT") && !new File(appDirectory + File.separator + Globals.VOD_EXPORTED_PATH + File.separator + vodID).exists()) {
            exportButton(button, vodID);
        } else if (actionButtonType != null && actionButtonType.contentEquals("DELETE")) {
            deleteButton(button, vodID);
        } else if (actionButtonType != null && actionButtonType.contentEquals("EXCLUDE")) {
            excludeButton(button, vodID);
        } else if (actionButtonType != null && actionButtonType.contentEquals("INCLUDE")) {
            includeButton(button, vodID);
        }  else {
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
                datasetChanged();
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

                    if (new File(appDirectory + File.separator + "SETTINGS_VOD").exists()) {
                        JSONObject settings = new JSONObject(new ReadFileHandler(weakContext, "SETTINGS_VOD").readFile());
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
                            new VODExportRequestHandler(weakActivity, weakContext).export(vodID, title, description, tags, visibility, split);
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

    /**
     * Button for excluding a VOD from automation and other views
     * @author LethalMaus
     * @param imageButton button view
     * @param vodID user to be excluded
     */
    private void excludeButton(ImageButton imageButton, final String vodID) {
        imageButton.setImageResource(R.drawable.excluded);
        imageButton.setTag("EXCLUDE_BUTTON");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WriteFileHandler(weakContext, Globals.VOD_EXCLUDED_PATH + File.separator + vodID, null, null, false).run();
                vodDataset.remove(vodID);
                datasetChanged();
                pageCount1--;
                pageCount3++;
                setPageCountViews(weakActivity);
            }
        });
    }

    /**
     * Button for including a VOD to automation and other views
     * @author LethalMaus
     * @param imageButton button view
     * @param vodID VOD to be included
     */
    private void includeButton(ImageButton imageButton, final String vodID) {
        imageButton.setImageResource(R.drawable.include);
        imageButton.setTag("INCLUDE_BUTTON");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteFileHandler(weakContext, null).deleteFileOrPath(Globals.VOD_EXCLUDED_PATH + File.separator + vodID);
                vodDataset.remove(vodID);
                datasetChanged();
                pageCount1++;
                pageCount3--;
                setPageCountViews(weakActivity);
            }
        });
    }

    /**
     * Button for exporting/deleting/including all VODS within menu.
     * @author LethalMaus
     * @param actionButtonType type of button the actionAll should contain
     */
    private void actionAllButton(String actionButtonType) {
        final ImageButton imageButton = weakActivity.get().findViewById(R.id.action_all_button);
        imageButton.setVisibility(View.VISIBLE);
        if (actionButtonType.contentEquals("EXPORT")) {
            imageButton.setImageResource(R.drawable.export);
            imageButton.setTag("EXPORT_ALL_BUTTON");
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (String vodID : vodDataset) {
                        if (!new File(appDirectory + File.separator + Globals.VOD_EXPORTED_PATH + File.separator + vodID).exists()) {
                            try {
                                boolean visibility = false;
                                boolean split = false;
                                if (new File(appDirectory + File.separator + "SETTINGS_VOD").exists()) {
                                    JSONObject settings = new JSONObject(new ReadFileHandler(weakContext, "SETTINGS_VOD").readFile());
                                    visibility = settings.getBoolean(Globals.SETTINGS_VISIBILITY);
                                    split = settings.getBoolean(Globals.SETTINGS_SPLIT);
                                }
                                JSONObject vod = new JSONObject(new ReadFileHandler(weakContext, Globals.VOD_PATH + File.separator + vodID).readFile());
                                new VODExportRequestHandler(weakActivity, weakContext).export(vodID, vod.getString("title"), vod.getString("description"), vod.getString("tag_list"), visibility, split);
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
                    datasetChanged();
                }
            });
        } else if (actionButtonType.contentEquals("DELETE")) {
            imageButton.setImageResource(R.drawable.delete);
            imageButton.setTag("DELETE_ALL_BUTTON");
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (String vodID : vodDataset) {
                        new DeleteFileHandler(weakContext, Globals.VOD_EXPORTED_PATH + File.separator + vodID).run();
                    }
                    vodDataset.clear();
                    pageCount2 = 0;
                    setPageCountViews(weakActivity);
                    datasetChanged();
                }
            });
        }  else if (actionButtonType.contentEquals("INCLUDE")) {
            imageButton.setImageResource(R.drawable.include);
            imageButton.setTag("INCLUDE_ALL_BUTTON");
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (String vodID : vodDataset) {
                        new DeleteFileHandler(weakContext, Globals.VOD_EXCLUDED_PATH + File.separator + vodID).run();
                    }
                    vodDataset.clear();
                    pageCount2 = 0;
                    setPageCountViews(weakActivity);
                    datasetChanged();
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
        if (pageCount1 > 0) {
            pageCount2 = new ReadFileHandler(weakContext, Globals.VOD_EXPORTED_PATH).countFiles();
            pageCount3 = new ReadFileHandler(weakContext, Globals.VOD_EXCLUDED_PATH).countFiles();
            pageCount1 = pageCount1 - pageCount3 < 0 ? 0 : pageCount1 - pageCount3;
        }
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
            TextView page3 = weakActivity.get().findViewById(R.id.count3);
            page1.setText(String.valueOf(pageCount1));
            page2.setText(String.valueOf(pageCount2));
            page3.setText(String.valueOf(pageCount3));
        }
    }
}
