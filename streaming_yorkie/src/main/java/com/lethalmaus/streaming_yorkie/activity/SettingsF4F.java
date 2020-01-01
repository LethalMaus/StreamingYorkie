package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.ShareF4FStatusRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

import static com.lethalmaus.streaming_yorkie.Globals.SETTINGS_FOLLOW;
import static com.lethalmaus.streaming_yorkie.Globals.SETTINGS_FOLLOWUNFOLLOW;
import static com.lethalmaus.streaming_yorkie.Globals.SETTINGS_INTERVAL_UNIT_DAYS;
import static com.lethalmaus.streaming_yorkie.Globals.SETTINGS_INTERVAL_UNIT_HOURS;
import static com.lethalmaus.streaming_yorkie.Globals.SETTINGS_INTERVAL_UNIT_MINUTES;
import static com.lethalmaus.streaming_yorkie.Globals.SETTINGS_OFF;
import static com.lethalmaus.streaming_yorkie.Globals.SETTINGS_UNFOLLOW;

/**
 * Activity for F4F settings that creates the settings only when first opened.
 * The settings are never needed unless the user tries to change them.
 * @author LethalMaus
 */
public class SettingsF4F extends AppCompatActivity {

    //All contexts are weak referenced to avoid memory leaks
    protected WeakReference<Activity> weakActivity;
    protected WeakReference<Context> weakContext;

    private JSONObject settings;
    private JSONObject previousSettings;
    int autofollowIntervalValueMinimum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weakActivity = new WeakReference<>(SettingsF4F.this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        setContentView(R.layout.settings_f4f);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle("F4F");
        }

        if (!new File(getFilesDir().toString() + File.separator + "SETTINGS_F4F").exists()) {
            createSettingsFile();
        }
        try {
            String settingsFile = new ReadFileHandler(weakActivity, weakContext, "SETTINGS_F4F").readFile();
            settings = new JSONObject(settingsFile);
            previousSettings = new JSONObject(settingsFile);
            //Ensures settings integrity
            if (!settings.has(Globals.SETTINGS_AUTOFOLLOW) || !settings.has(Globals.SETTINGS_INTERVAL) || !settings.has(Globals.SETTINGS_INTERVAL_UNIT) || !settings.has(Globals.SETTINGS_NOTIFICATIONS)) {
                new DeleteFileHandler(weakActivity, weakContext, null).deleteFileOrPath("SETTINGS_F4F");
                createSettingsFile();
            }
        } catch (JSONException e) {
            Toast.makeText(SettingsF4F.this, "Error reading F4F Settings", Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error reading F4F Settings | " + e.toString(), true).run();
        }

        serviceActivation();
        intervalUnitRadioGroup();
        intervalValue();
        notificationSwitch();
        shareF4FStatusSwitch();

        findViewById(R.id.settings_save).setOnClickListener((View v) ->
            saveSettings()
        );
    }

    //The only option is the back button for saving settings
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        saveSettings();
        return true;
    }

    /**
     * This creates the setting first time. It is done here because it is only ever needed if the channel wants to change settings. Otherwise its never needed.
     * @author LethalMaus
     */
    private void createSettingsFile() {
        settings = new JSONObject();
        try {
            settings.put(Globals.SETTINGS_AUTOFOLLOW, SETTINGS_OFF);
            settings.put(Globals.SETTINGS_INTERVAL, 1);
            settings.put(Globals.SETTINGS_INTERVAL_UNIT, SETTINGS_INTERVAL_UNIT_DAYS);
            settings.put(Globals.SETTINGS_NOTIFICATIONS, false);
            settings.put(Globals.SETTINGS_SHARE_F4F_STATUS, false);
            new WriteFileHandler(weakActivity, weakContext, "SETTINGS_F4F", null, settings.toString(), false).writeToFileOrPath();
        } catch (JSONException e) {
            Toast.makeText(SettingsF4F.this, "Error creating F4F Settings", Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error creating F4F Settings | " + e.toString(), true).run();
        }
    }

    /**
     * Reads settings and changes the AutoFollow RadioGroup. Also adds the Listener
     * @author LethalMaus
     */
    private void serviceActivation() {
        RadioGroup autoFollowRadioGroup = findViewById(R.id.settings_autoFollow);
        try {
            switch (settings.getString(Globals.SETTINGS_AUTOFOLLOW)) {
                case SETTINGS_FOLLOW:
                    autoFollowRadioGroup.check(R.id.settings_autoFollow_follow);
                    break;
                case SETTINGS_UNFOLLOW:
                    autoFollowRadioGroup.check(R.id.settings_autoFollow_unfollow);
                    break;
                case SETTINGS_FOLLOWUNFOLLOW:
                    autoFollowRadioGroup.check(R.id.settings_autoFollow_followUnfollow);
                    break;
                case SETTINGS_OFF:
                default:
                    autoFollowRadioGroup.check(R.id.settings_autoFollow_off);
                    break;
            }
        } catch(JSONException e) {
            Toast.makeText(SettingsF4F.this, "Error reading F4F Settings, " + Globals.SETTINGS_AUTOFOLLOW, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error reading F4F Settings, " + Globals.SETTINGS_AUTOFOLLOW + " | " + e.toString(), true).run();
        }
        autoFollowRadioGroup.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
            try {
                switch (checkedId) {
                    case R.id.settings_autoFollow_follow:
                        settings.put(Globals.SETTINGS_AUTOFOLLOW, SETTINGS_FOLLOW);
                        break;
                    case R.id.settings_autoFollow_unfollow:
                        settings.put(Globals.SETTINGS_AUTOFOLLOW, SETTINGS_UNFOLLOW);
                        break;
                    case R.id.settings_autoFollow_followUnfollow:
                        settings.put(Globals.SETTINGS_AUTOFOLLOW, SETTINGS_FOLLOWUNFOLLOW);
                        break;
                    case R.id.settings_autoFollow_off:
                    default:
                        settings.put(Globals.SETTINGS_AUTOFOLLOW, SETTINGS_OFF);
                        break;
                }
            } catch(JSONException e) {
                Toast.makeText(SettingsF4F.this, "Error changing F4F Settings, " + Globals.SETTINGS_AUTOFOLLOW, Toast.LENGTH_SHORT).show();
                new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error changing F4F Settings, " + Globals.SETTINGS_AUTOFOLLOW + " | " + e.toString(), true).run();
            }
        });
    }

    /**
     * Reads settings and changes the Interval Slider. Also adds the Listener
     * @author LethalMaus
     */
    private void intervalValue() {
        final SeekBar autoFollowIntervalValue = findViewById(R.id.settings_autoFollowInterval_value);
        final TextView autoFollowIntervalValueText = findViewById(R.id.settings_autoFollowInterval_value_text);
        try {
            autoFollowIntervalValue.setProgress(settings.getInt(Globals.SETTINGS_INTERVAL) - 1);
            autoFollowIntervalValueText.setText(String.valueOf(settings.getInt(Globals.SETTINGS_INTERVAL)));
        } catch(JSONException e) {
            Toast.makeText(SettingsF4F.this, "Error reading F4F Settings, " + Globals.SETTINGS_INTERVAL, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error reading F4F Settings, " + Globals.SETTINGS_INTERVAL + " | " + e.toString(), true).run();
        }

        autoFollowIntervalValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                autoFollowIntervalValueText.setText(String.valueOf(autoFollowIntervalValue.getProgress() + autofollowIntervalValueMinimum));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    settings.put(Globals.SETTINGS_INTERVAL, autoFollowIntervalValue.getProgress() + autofollowIntervalValueMinimum);
                } catch(JSONException e) {
                    Toast.makeText(SettingsF4F.this, "Error changing F4F Settings, " + Globals.SETTINGS_INTERVAL, Toast.LENGTH_SHORT).show();
                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error changing F4F Settings, " + Globals.SETTINGS_INTERVAL + " | " + e.toString(), true).run();
                }
            }
        });
    }

    /**
     * Reads settings and changes the AutoFollow Interval Unit. Also adds the Listener
     * @author LethalMaus
     */
    private void intervalUnitRadioGroup() {
        RadioGroup autoFollowIntervalUnitRadioGroup = findViewById(R.id.settings_autoFollowIntervalUnit);
        final SeekBar autoFollowIntervalValue = findViewById(R.id.settings_autoFollowInterval_value);
        final TextView autoFollowIntervalValueText = findViewById(R.id.settings_autoFollowInterval_value_text);
        try {
            switch (settings.getString(Globals.SETTINGS_INTERVAL_UNIT)) {
                case SETTINGS_INTERVAL_UNIT_MINUTES:
                    autoFollowIntervalUnitRadioGroup.check(R.id.settings_autoFollowIntervalUnit_minutes);
                    autoFollowIntervalValue.setMax(75);
                    autofollowIntervalValueMinimum = 15;
                    break;
                case SETTINGS_INTERVAL_UNIT_HOURS:
                    autoFollowIntervalUnitRadioGroup.check(R.id.settings_autoFollowIntervalUnit_hours);
                    autoFollowIntervalValue.setMax(71);
                    autofollowIntervalValueMinimum = 1;
                    break;
                case SETTINGS_INTERVAL_UNIT_DAYS:
                default:
                    autoFollowIntervalUnitRadioGroup.check(R.id.settings_autoFollowIntervalUnit_days);
                    autoFollowIntervalValue.setMax(29);
                    autofollowIntervalValueMinimum = 1;
                    break;
            }
        } catch(JSONException e) {
            Toast.makeText(SettingsF4F.this, "Error reading F4F Settings, " + Globals.SETTINGS_INTERVAL_UNIT, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error reading F4F Settings, " + Globals.SETTINGS_INTERVAL_UNIT + " | " + e.toString(), true).run();
        }
        autoFollowIntervalUnitRadioGroup.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
            try {
                switch (checkedId) {
                    case R.id.settings_autoFollowIntervalUnit_minutes:
                        settings.put(Globals.SETTINGS_INTERVAL_UNIT, SETTINGS_INTERVAL_UNIT_MINUTES);
                        autoFollowIntervalValue.setMax(75);
                        autofollowIntervalValueMinimum = 15;
                        break;
                    case R.id.settings_autoFollowIntervalUnit_hours:
                        settings.put(Globals.SETTINGS_INTERVAL_UNIT, SETTINGS_INTERVAL_UNIT_HOURS);
                        autoFollowIntervalValue.setMax(71);
                        autofollowIntervalValueMinimum = 1;
                        break;
                    case R.id.settings_autoFollowIntervalUnit_days:
                    default:
                        settings.put(Globals.SETTINGS_INTERVAL_UNIT, SETTINGS_INTERVAL_UNIT_DAYS);
                        autoFollowIntervalValue.setMax(29);
                        autofollowIntervalValueMinimum = 1;
                        break;
                }
                autoFollowIntervalValue.setProgress(0);
                autoFollowIntervalValueText.setText(String.valueOf(autofollowIntervalValueMinimum));
                settings.put(Globals.SETTINGS_INTERVAL, autofollowIntervalValueMinimum);
            } catch(JSONException e) {
                Toast.makeText(SettingsF4F.this, "Error changing F4F Settings, " + Globals.SETTINGS_INTERVAL_UNIT, Toast.LENGTH_SHORT).show();
                new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error changing F4F Settings, " + Globals.SETTINGS_INTERVAL_UNIT + " | " + e.toString(), true).run();
            }
        });
    }

    /**
     * Reads settings and changes the AutoFollow Notification Preference Switch. Also adds the Listener
     * @author LethalMaus
     */
    private void notificationSwitch() {
        Switch autoFollowNotifications = findViewById(R.id.settings_autoFollowNotifications);
        try {
            autoFollowNotifications.setChecked(settings.getBoolean(Globals.SETTINGS_NOTIFICATIONS));
        } catch(JSONException e) {
            Toast.makeText(SettingsF4F.this, "Error reading F4F Settings, " + Globals.SETTINGS_NOTIFICATIONS, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null,"Error reading F4F Settings, " + Globals.SETTINGS_NOTIFICATIONS + " | " + e.toString(), true).run();
        }
        autoFollowNotifications.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            try {
                settings.put(Globals.SETTINGS_NOTIFICATIONS, isChecked);
            } catch(JSONException e) {
                Toast.makeText(SettingsF4F.this, "Error changing F4F Settings, " + Globals.SETTINGS_NOTIFICATIONS, Toast.LENGTH_SHORT).show();
                new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error changing F4F Settings, " + Globals.SETTINGS_NOTIFICATIONS + " | " + e.toString(), true).run();
            }
        });
    }

    /**
     * Reads settings and changes the AutoFollow Share F4F Status Preference Switch. Also adds the Listener
     * @author LethalMaus
     */
    private void shareF4FStatusSwitch() {
        Switch autoFollowShareF4FStatus = findViewById(R.id.settings_autoFollow_share_f4f_status);
        try {
            if (settings.has(Globals.SETTINGS_SHARE_F4F_STATUS)) {
                autoFollowShareF4FStatus.setChecked(settings.getBoolean(Globals.SETTINGS_SHARE_F4F_STATUS));
            } else {
                autoFollowShareF4FStatus.setChecked(false);
            }
        } catch(JSONException e) {
            Toast.makeText(SettingsF4F.this, "Error reading F4F Settings, " + Globals.SETTINGS_SHARE_F4F_STATUS, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null,"Error reading F4F Settings, " + Globals.SETTINGS_SHARE_F4F_STATUS + " | " + e.toString(), true).run();
        }
        autoFollowShareF4FStatus.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            try {
                settings.put(Globals.SETTINGS_SHARE_F4F_STATUS, isChecked);
                if (isChecked) {
                    Toast.makeText(SettingsF4F.this, "You'll be sharing your F4F status to Discord so that others can follow you. There you can find others who do the same. There is a link in the Info Menu", Toast.LENGTH_LONG).show();
                }
            } catch(JSONException e) {
                Toast.makeText(SettingsF4F.this, "Error changing F4F Settings, " + Globals.SETTINGS_SHARE_F4F_STATUS, Toast.LENGTH_SHORT).show();
                new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error changing F4F Settings, " + Globals.SETTINGS_SHARE_F4F_STATUS + " | " + e.toString(), true).run();
            }
        });
    }

    /**
     * Checks if changes were made anywhere and prompts user to save.
     * @author LethalMaus
     */
    private void saveSettings() {
        try {
            if (!previousSettings.getString(Globals.SETTINGS_AUTOFOLLOW).equals(settings.getString(Globals.SETTINGS_AUTOFOLLOW))||
                    previousSettings.getInt(Globals.SETTINGS_INTERVAL) != settings.getInt(Globals.SETTINGS_INTERVAL) ||
                    !previousSettings.getString(Globals.SETTINGS_INTERVAL_UNIT).equals(settings.getString(Globals.SETTINGS_INTERVAL_UNIT)) ||
                    previousSettings.getBoolean(Globals.SETTINGS_NOTIFICATIONS) != settings.getBoolean(Globals.SETTINGS_NOTIFICATIONS) ||
                    (previousSettings.has(Globals.SETTINGS_SHARE_F4F_STATUS) && previousSettings.getBoolean(Globals.SETTINGS_SHARE_F4F_STATUS) != settings.getBoolean(Globals.SETTINGS_SHARE_F4F_STATUS))) {
                if (!settings.getString(Globals.SETTINGS_AUTOFOLLOW).equals(Globals.SETTINGS_OFF)) {
                    promptActivatingAutoFollow();
                } else {
                    promptUserSaveSettings();
                }
            } else {
                Toast.makeText(SettingsF4F.this, "No changes made", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch(JSONException e) {
            Toast.makeText(SettingsF4F.this, "Error reading F4F Settings", Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error reading F4F Settings | " + e.toString(), true).run();
        }
    }

    /**
     * Notifies user to prepare before activating the AutoFollow Worker
     * @author LethalMaus
     */
    private void promptActivatingAutoFollow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsF4F.this, R.style.CustomDialog);
        builder.setPositiveButton("OK", (DialogInterface dialog, int id) ->
            promptUserSaveSettings()
        );
        builder.setNegativeButton("CANCEL", (DialogInterface dialog, int id) ->  {
            Toast.makeText(SettingsF4F.this, "Changes discarded", Toast.LENGTH_SHORT).show();
            finish();
        });
        builder.setTitle("WARNING: AutoFollow preparation is required");
        builder.setMessage("Make sure you have excluded Followers/Following/F4F from the AutoFollow Worker");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Prompts user if these settings are wanted, or if they should be discarded.
     * @author LethalMaus
     */
    private void promptUserSaveSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsF4F.this, R.style.CustomDialog);
        builder.setPositiveButton("SAVE", (DialogInterface dialog, int id) ->  {
            new WriteFileHandler(weakActivity, weakContext, "SETTINGS_F4F", null, settings.toString(), false).run();
            Toast.makeText(SettingsF4F.this, "Changes saved", Toast.LENGTH_SHORT).show();
            try {
                if (settings.getBoolean(Globals.SETTINGS_SHARE_F4F_STATUS) && (settings.getString(Globals.SETTINGS_AUTOFOLLOW).contentEquals(SETTINGS_FOLLOW) || settings.getString(Globals.SETTINGS_AUTOFOLLOW).contentEquals(SETTINGS_FOLLOWUNFOLLOW))) {
                    postToDiscord("**#USERNAME** is doing F4F automatically every **" + settings.getString(Globals.SETTINGS_INTERVAL) + " " + settings.getString(Globals.SETTINGS_INTERVAL_UNIT) + "**. Follow them here https://www.twitch.tv/#USERNAME");
                } else if ((!settings.getBoolean(Globals.SETTINGS_SHARE_F4F_STATUS) && previousSettings.getBoolean(Globals.SETTINGS_SHARE_F4F_STATUS)) || (previousSettings.getBoolean(Globals.SETTINGS_SHARE_F4F_STATUS) && settings.getString(Globals.SETTINGS_AUTOFOLLOW).contentEquals(SETTINGS_OFF))) {
                    postToDiscord("```diff\n- #USERNAME is NOT doing F4F automatically anymore.\n```");
                }
            } catch(JSONException e) {
                Toast.makeText(SettingsF4F.this, "Error reading F4F Settings, " + Globals.SETTINGS_SHARE_F4F_STATUS, Toast.LENGTH_SHORT).show();
                new WriteFileHandler(weakActivity, weakContext, "ERROR", null,"Error reading F4F Settings, " + Globals.SETTINGS_SHARE_F4F_STATUS + " | " + e.toString(), true).run();
            }
            finish();
        });
        builder.setNegativeButton("CANCEL", (DialogInterface dialog, int id) ->  {
            Toast.makeText(SettingsF4F.this, "Changes discarded", Toast.LENGTH_SHORT).show();
            finish();
        });
        builder.setTitle("Save Settings");
        builder.setMessage("Would you like to save your changes?");
        builder.create().show();
    }

    /**
     * Method for posting to discord
     * @author LethalMaus
     * @param postBodyContent String message that is sent to discord. #USERNAME will be replaced with the username.
     */
    private void postToDiscord(String postBodyContent) {
        new Thread(() -> {
            try {
                JSONObject postBody = new JSONObject();
                String username =  StreamingYorkieDB.getInstance(weakContext.get()).channelDAO().getChannel().getDisplay_name();
                postBody.put("content", postBodyContent.replaceAll("#USERNAME", username));
                new ShareF4FStatusRequestHandler(new WeakReference<>(SettingsF4F.this), weakContext).setPostBody(postBody).sendRequest();
            } catch(JSONException e) {
                Toast.makeText(SettingsF4F.this, "Error sharing F4F status", Toast.LENGTH_SHORT).show();
                new WriteFileHandler(weakActivity, weakContext, "ERROR", null,"Error reading F4F Settings, " + Globals.SETTINGS_SHARE_F4F_STATUS + " | " + e.toString(), true).run();
            }
        }).start();
    }
}
