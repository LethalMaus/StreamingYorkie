package com.lethalmaus.streaming_yorkie.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

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
    protected WeakReference<Context> weakContext;

    private JSONObject settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weakContext = new WeakReference<>(getApplicationContext());
        setContentView(R.layout.settings_f4f);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle("F4F");
        }

        //This creates the setting first time. It is done here because it is only ever needed if the user wants to change settings. Otherwise its never needed.
        if (!new File(getFilesDir().toString() + File.separator + "SETTINGS_F4F").exists()) {
            settings = new JSONObject();
            try {
                settings.put(Globals.SETTINGS_AUTOFOLLOW, SETTINGS_OFF);
                settings.put(Globals.SETTINGS_INTERVAL, 1);
                settings.put(Globals.SETTINGS_INTERVAL_UNIT, SETTINGS_INTERVAL_UNIT_DAYS);
                settings.put(Globals.SETTINGS_NOTIFICATIONS, false);
                new WriteFileHandler(weakContext, "SETTINGS_F4F", null, settings.toString(), false).writeToFileOrPath();
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error creating VOD Settings", Toast.LENGTH_SHORT).show();
                new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
            }
        }
        try {
            settings = new JSONObject(new ReadFileHandler(weakContext, "SETTINGS_F4F").readFile());
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error reading F4F Settings", Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
        }

        serviceActivation();
        intervalValue();
        intervalUnitRadioGroup();
        notificationSwitch();

        ImageButton save = findViewById(R.id.settings_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    //The only option is the back button for saving settings
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        saveSettings();
        return true;
    }

    /**
     * Reads settings and changes the AutoFollow RadioGroup. Also adds the Listener
     * @author LethalMaus
     */
    private void serviceActivation() {
        RadioGroup autoFollowRadioGroup = findViewById(R.id.settings_autoFollow);
        try {
            switch (settings.getString(Globals.SETTINGS_AUTOFOLLOW)) {
                case SETTINGS_OFF:
                    autoFollowRadioGroup.check(R.id.settings_autoFollow_off);
                    break;
                case SETTINGS_FOLLOW:
                    autoFollowRadioGroup.check(R.id.settings_autoFollow_follow);
                    break;
                case SETTINGS_UNFOLLOW:
                    autoFollowRadioGroup.check(R.id.settings_autoFollow_unfollow);
                    break;
                case SETTINGS_FOLLOWUNFOLLOW:
                    autoFollowRadioGroup.check(R.id.settings_autoFollow_followUnfollow);
                    break;
                default:
                    autoFollowRadioGroup.check(R.id.settings_autoFollow_off);
                    break;
            }
        } catch(JSONException e) {
            Toast.makeText(getApplicationContext(), "Error reading F4F Settings, " + Globals.SETTINGS_AUTOFOLLOW, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
        }
        autoFollowRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                try {
                    switch (checkedId) {
                        case R.id.settings_autoFollow_off:
                            settings.put(Globals.SETTINGS_AUTOFOLLOW, SETTINGS_OFF);
                            break;
                        case R.id.settings_autoFollow_follow:
                            settings.put(Globals.SETTINGS_AUTOFOLLOW, SETTINGS_FOLLOW);
                            break;
                        case R.id.settings_autoFollow_unfollow:
                            settings.put(Globals.SETTINGS_AUTOFOLLOW, SETTINGS_UNFOLLOW);
                            break;
                        case R.id.settings_autoFollow_followUnfollow:
                            settings.put(Globals.SETTINGS_AUTOFOLLOW, SETTINGS_FOLLOWUNFOLLOW);
                            break;
                        default:
                            settings.put(Globals.SETTINGS_AUTOFOLLOW, SETTINGS_OFF);
                            break;
                    }
                } catch(JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error changing F4F Settings, " + Globals.SETTINGS_AUTOFOLLOW, Toast.LENGTH_SHORT).show();
                    new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
                }
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
            Toast.makeText(getApplicationContext(), "Error reading F4F Settings, " + Globals.SETTINGS_INTERVAL, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
        }

        autoFollowIntervalValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                autoFollowIntervalValueText.setText(String.valueOf(autoFollowIntervalValue.getProgress() + 1));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    settings.put(Globals.SETTINGS_INTERVAL, autoFollowIntervalValue.getProgress() + 1);
                } catch(JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error changing F4F Settings, " + Globals.SETTINGS_INTERVAL, Toast.LENGTH_SHORT).show();
                    new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
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
        try {
            switch (settings.getString(Globals.SETTINGS_INTERVAL_UNIT)) {
                case SETTINGS_INTERVAL_UNIT_MINUTES:
                    autoFollowIntervalUnitRadioGroup.check(R.id.settings_autoFollowIntervalUnit_minutes);
                    break;
                case SETTINGS_INTERVAL_UNIT_HOURS:
                    autoFollowIntervalUnitRadioGroup.check(R.id.settings_autoFollowIntervalUnit_hours);
                    break;
                case SETTINGS_INTERVAL_UNIT_DAYS:
                    autoFollowIntervalUnitRadioGroup.check(R.id.settings_autoFollowIntervalUnit_days);
                    break;
                default:
                    autoFollowIntervalUnitRadioGroup.check(R.id.settings_autoFollowIntervalUnit_days);
                    break;
            }
        } catch(JSONException e) {
            Toast.makeText(getApplicationContext(), "Error reading F4F Settings, " + Globals.SETTINGS_INTERVAL_UNIT, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
        }
        autoFollowIntervalUnitRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                try {
                    switch (checkedId) {
                        case R.id.settings_autoFollowIntervalUnit_minutes:
                            settings.put(Globals.SETTINGS_INTERVAL_UNIT, SETTINGS_INTERVAL_UNIT_MINUTES);
                            break;
                        case R.id.settings_autoFollowIntervalUnit_hours:
                            settings.put(Globals.SETTINGS_INTERVAL_UNIT, SETTINGS_INTERVAL_UNIT_HOURS);
                            break;
                        case R.id.settings_autoFollowIntervalUnit_days:
                            settings.put(Globals.SETTINGS_INTERVAL_UNIT, SETTINGS_INTERVAL_UNIT_DAYS);
                            break;
                        default:
                            settings.put(Globals.SETTINGS_INTERVAL_UNIT, SETTINGS_INTERVAL_UNIT_DAYS);
                            break;
                    }
                } catch(JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error changing F4F Settings, " + Globals.SETTINGS_INTERVAL_UNIT, Toast.LENGTH_SHORT).show();
                    new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
                }
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
            Toast.makeText(getApplicationContext(), "Error reading F4F Settings, " + Globals.SETTINGS_NOTIFICATIONS, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
        }
        autoFollowNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    settings.put(Globals.SETTINGS_NOTIFICATIONS, isChecked);
                } catch(JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error changing F4F Settings, " + Globals.SETTINGS_NOTIFICATIONS, Toast.LENGTH_SHORT).show();
                    new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
                }
            }
        });
    }

    /**
     * Checks if changes were made anywhere and prompts user to save.
     * @author LethalMaus
     */
    private void saveSettings() {
        try {
            JSONObject previousSettings = new JSONObject(new ReadFileHandler(weakContext, "SETTINGS_F4F").readFile());

            if (!previousSettings.getString(Globals.SETTINGS_AUTOFOLLOW).equals(settings.getString(Globals.SETTINGS_AUTOFOLLOW))||
                    previousSettings.getInt(Globals.SETTINGS_INTERVAL) != settings.getInt(Globals.SETTINGS_INTERVAL) ||
                    !previousSettings.getString(Globals.SETTINGS_INTERVAL_UNIT).equals(settings.getString(Globals.SETTINGS_INTERVAL_UNIT)) ||
                    previousSettings.getBoolean(Globals.SETTINGS_NOTIFICATIONS) != settings.getBoolean(Globals.SETTINGS_NOTIFICATIONS)) {
                if (!settings.getString(Globals.SETTINGS_AUTOFOLLOW).equals(Globals.SETTINGS_OFF)) {
                    promptActivatingAutoFollow();
                } else if (settings.getInt(Globals.SETTINGS_INTERVAL) < 15 && settings.getString(Globals.SETTINGS_INTERVAL_UNIT).equals(SETTINGS_INTERVAL_UNIT_MINUTES)) {
                    promptUserIntervalTooSmall();
                } else {
                    promptUserSaveSettings();
                }
            } else {
                Toast.makeText(getApplicationContext(), "No changes made", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch(JSONException e) {
            Toast.makeText(getApplicationContext(), "Error reading F4F Settings", Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
        }
    }

    /**
     * Notifies user to prepare before activating the AutoFollow Worker
     * @author LethalMaus
     */
    private void promptActivatingAutoFollow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsF4F.this, R.style.CustomDialog);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                    if (settings.getInt(Globals.SETTINGS_INTERVAL) < 15 && settings.getString(Globals.SETTINGS_INTERVAL_UNIT).equals(SETTINGS_INTERVAL_UNIT_MINUTES)) {
                        promptUserIntervalTooSmall();
                    } else {
                        promptUserSaveSettings();
                    }
                } catch(JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error reading F4F Settings", Toast.LENGTH_SHORT).show();
                    new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), "Changes discarded", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setTitle("WARNING: AutoFollow preparation is required");
        builder.setMessage("Make sure you have excluded Followers/Following/F4F from the AutoFollow Worker");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Notifies User if the AutoFollow Interval is less than 15 Minutes.
     * Android only allows Periodic Requests from 15 Minutes onwards
     * @author LethalMaus
     */
    private void promptUserIntervalTooSmall() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsF4F.this, R.style.CustomDialog);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                promptUserSaveSettings();
            }
        });
        builder.setTitle("Interval too small");
        builder.setMessage("15 minutes is the smallest interval possible");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Prompts user if these settings are wanted, or if they should be discard.
     * @author LethalMaus
     */
    private void promptUserSaveSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsF4F.this, R.style.CustomDialog);
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                new WriteFileHandler(weakContext, "SETTINGS", null, settings.toString(), false).run();
                Toast.makeText(getApplicationContext(), "Changes saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), "Changes discarded", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setTitle("Save Settings");
        builder.setMessage("Would you like to save your changes?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}