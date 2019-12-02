package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

import static com.lethalmaus.streaming_yorkie.Globals.SETTINGS_EXPORT;
import static com.lethalmaus.streaming_yorkie.Globals.SETTINGS_INTERVAL_UNIT_DAYS;
import static com.lethalmaus.streaming_yorkie.Globals.SETTINGS_INTERVAL_UNIT_HOURS;
import static com.lethalmaus.streaming_yorkie.Globals.SETTINGS_OFF;

/**
 * Activity for VOD Export settings that creates the settings only when first opened.
 * The settings are never needed unless the channel tries to change them.
 * @author LethalMaus
 */
public class SettingsVOD extends AppCompatActivity {

    //All contexts are weak referenced to avoid memory leaks
    protected WeakReference<Activity> weakActivity;
    protected WeakReference<Context> weakContext;

    private JSONObject settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weakActivity = new WeakReference<>(SettingsVOD.this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        setContentView(R.layout.settings_vod);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle("VOD Export");
        }

        if (!new File(getFilesDir().toString() + File.separator + "SETTINGS_VOD").exists()) {
            createSettingsFile();
        }
        try {
            settings = new JSONObject(new ReadFileHandler(weakActivity, weakContext, "SETTINGS_VOD").readFile());
            //Ensures settings integrity
            if (!settings.has(Globals.SETTINGS_AUTOVODEXPORT) || !settings.has(Globals.SETTINGS_INTERVAL) || !settings.has(Globals.SETTINGS_INTERVAL_UNIT) || !settings.has(Globals.SETTINGS_VISIBILITY)  || !settings.has(Globals.SETTINGS_SPLIT)) {
                new DeleteFileHandler(weakActivity, weakContext, null).deleteFileOrPath("SETTINGS_VOD");
                createSettingsFile();
            }
        } catch (JSONException e) {
            Toast.makeText(SettingsVOD.this, "Error reading VOD Settings", Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error reading VOD Settings" + e.toString(), true).run();
        }

        serviceActivation();
        intervalValue();
        intervalUnitRadioGroup();
        visibilityRadioGroup();
        splitSwitch();

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
            settings.put(Globals.SETTINGS_AUTOVODEXPORT, SETTINGS_OFF);
            settings.put(Globals.SETTINGS_INTERVAL, 1);
            settings.put(Globals.SETTINGS_INTERVAL_UNIT, SETTINGS_INTERVAL_UNIT_DAYS);
            settings.put(Globals.SETTINGS_VISIBILITY, false);
            settings.put(Globals.SETTINGS_SPLIT, false);
            new WriteFileHandler(weakActivity, weakContext, "SETTINGS_VOD", null, settings.toString(), false).writeToFileOrPath();
        } catch (JSONException e) {
            Toast.makeText(SettingsVOD.this, "Error creating VOD Settings", Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error creating VOD Settings" + e.toString(), true).run();
        }
    }

    /**
     * Reads settings and changes the Auto-VOD-Export RadioGroup. Also adds the Listener
     * @author LethalMaus
     */
    private void serviceActivation() {
        RadioGroup radioGroup = findViewById(R.id.settings_autoVODExport);
        try {
            switch (settings.getString(Globals.SETTINGS_AUTOVODEXPORT)) {
                case SETTINGS_OFF:
                    radioGroup.check(R.id.settings_autoVODExport_off);
                    break;
                case SETTINGS_EXPORT:
                    radioGroup.check(R.id.settings_autoVODExport_export);
                    break;
                default:
                    radioGroup.check(R.id.settings_autoVODExport_off);
                    break;
            }
        } catch(JSONException e) {
            Toast.makeText(SettingsVOD.this, "Error reading VOD Settings, " + Globals.SETTINGS_AUTOVODEXPORT, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error reading VOD Settings, " + Globals.SETTINGS_AUTOVODEXPORT + " | " + e.toString(), true).run();
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                try {
                    switch (checkedId) {
                        case R.id.settings_autoVODExport_off:
                            settings.put(Globals.SETTINGS_AUTOVODEXPORT, SETTINGS_OFF);
                            break;
                        case R.id.settings_autoVODExport_export:
                            settings.put(Globals.SETTINGS_AUTOVODEXPORT, SETTINGS_EXPORT);
                            break;
                        default:
                            settings.put(Globals.SETTINGS_AUTOVODEXPORT, SETTINGS_OFF);
                            break;
                    }
                } catch(JSONException e) {
                    Toast.makeText(SettingsVOD.this, "Error changing VOD Settings, " + Globals.SETTINGS_AUTOVODEXPORT, Toast.LENGTH_SHORT).show();
                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error changing VOD Settings, " + Globals.SETTINGS_AUTOVODEXPORT + " | " + e.toString(), true).run();
                }
            }
        });
    }

    /**
     * Reads settings and changes the Interval Slider. Also adds the Listener
     * @author LethalMaus
     */
    private void intervalValue() {
        final SeekBar intervalValue = findViewById(R.id.settings_autoVODExport_interval_value);
        final TextView intervalValueText = findViewById(R.id.settings_autoVODExport_interval_value_text);
        try {
            intervalValue.setProgress(settings.getInt(Globals.SETTINGS_INTERVAL) - 1);
            intervalValueText.setText(String.valueOf(settings.getInt(Globals.SETTINGS_INTERVAL)));
        } catch(JSONException e) {
            Toast.makeText(SettingsVOD.this, "Error reading VOD Settings, " + Globals.SETTINGS_INTERVAL, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error reading VOD Settings, " + Globals.SETTINGS_INTERVAL + " | " + e.toString(), true).run();
        }

        intervalValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                intervalValueText.setText(String.valueOf(intervalValue.getProgress() + 1));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    settings.put(Globals.SETTINGS_INTERVAL, intervalValue.getProgress() + 1);
                } catch(JSONException e) {
                    Toast.makeText(SettingsVOD.this, "Error changing VOD Settings, " + Globals.SETTINGS_INTERVAL, Toast.LENGTH_SHORT).show();
                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error changing VOD Settings, " + Globals.SETTINGS_INTERVAL + " | " + e.toString(), true).run();
                }
            }
        });
    }

    /**
     * Reads settings and changes the Interval Unit. Also adds the Listener
     * @author LethalMaus
     */
    private void intervalUnitRadioGroup() {
        RadioGroup radioGroup = findViewById(R.id.settings_autoVODExport_interval_unit);
        try {
            switch (settings.getString(Globals.SETTINGS_INTERVAL_UNIT)) {
                case SETTINGS_INTERVAL_UNIT_HOURS:
                    radioGroup.check(R.id.settings_autoVODExport_interval_unit_hours);
                    break;
                case SETTINGS_INTERVAL_UNIT_DAYS:
                default:
                    radioGroup.check(R.id.settings_autoVODExport_interval_unit_days);
                    break;
            }
        } catch(JSONException e) {
            Toast.makeText(SettingsVOD.this, "Error reading VOD Settings, " + Globals.SETTINGS_INTERVAL_UNIT, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error reading VOD Settings, " + Globals.SETTINGS_INTERVAL_UNIT + " | " + e.toString(), true).run();
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                try {
                    switch (checkedId) {
                        case R.id.settings_autoVODExport_interval_unit_hours:
                            settings.put(Globals.SETTINGS_INTERVAL_UNIT, SETTINGS_INTERVAL_UNIT_HOURS);
                            break;
                        case R.id.settings_autoVODExport_interval_unit_days:
                        default:
                            settings.put(Globals.SETTINGS_INTERVAL_UNIT, SETTINGS_INTERVAL_UNIT_DAYS);
                            break;
                    }
                } catch(JSONException e) {
                    Toast.makeText(SettingsVOD.this, "Error changing VOD Settings, " + Globals.SETTINGS_INTERVAL_UNIT, Toast.LENGTH_SHORT).show();
                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error changing VOD Settings, " + Globals.SETTINGS_INTERVAL_UNIT + " | " + e.toString(), true).run();
                }
            }
        });
    }

    /**
     * Reads settings and changes the Auto-Vod-Export publishing preference switch. Also adds the Listener
     * @author LethalMaus
     */
    private void visibilityRadioGroup() {
        RadioGroup radioGroup = findViewById(R.id.settings_autoVODExport_visibilty);
        try {
            if (settings.getBoolean(Globals.SETTINGS_VISIBILITY)) {
                radioGroup.check(R.id.settings_autoVODExport_public);
            } else {
                radioGroup.check(R.id.settings_autoVODExport_private);
            }
        } catch(JSONException e) {
            Toast.makeText(SettingsVOD.this, "Error reading VOD Settings, " + Globals.SETTINGS_VISIBILITY, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error reading VOD Settings, " + Globals.SETTINGS_VISIBILITY + " | " + e.toString(), true).run();
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                try {
                    if (checkedId == R.id.settings_autoVODExport_public) {
                        settings.put(Globals.SETTINGS_VISIBILITY, true);
                    } else {
                        settings.put(Globals.SETTINGS_VISIBILITY, false);
                    }
                } catch(JSONException e) {
                    Toast.makeText(SettingsVOD.this, "Error changing VOD Settings, " + Globals.SETTINGS_VISIBILITY, Toast.LENGTH_SHORT).show();
                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error changing VOD Settings, " + Globals.SETTINGS_VISIBILITY + " | " + e.toString(), true).run();
                }
            }
        });
    }

    /**
     * Reads settings and changes the Auto-Vod-Export splitting preference switch. Also adds the Listener
     * @author LethalMaus
     */
    private void splitSwitch() {
        Switch splitSwitch = findViewById(R.id.settings_autoVODExport_split);
        try {
            splitSwitch.setChecked(settings.getBoolean(Globals.SETTINGS_SPLIT));
        } catch(JSONException e) {
            Toast.makeText(SettingsVOD.this, "Error reading VOD Settings, " + Globals.SETTINGS_SPLIT, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error reading VOD Settings, " + Globals.SETTINGS_SPLIT + " | " + e.toString(), true).run();
        }
        splitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    settings.put(Globals.SETTINGS_SPLIT, isChecked);
                } catch(JSONException e) {
                    Toast.makeText(SettingsVOD.this, "Error changing VOD Settings, " + Globals.SETTINGS_SPLIT, Toast.LENGTH_SHORT).show();
                    new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error changing VOD Settings, " + Globals.SETTINGS_SPLIT + " | " + e.toString(), true).run();
                }
            }
        });
    }

    /**
     * Checks if changes were made anywhere and prompts channel to save.
     * @author LethalMaus
     */
    private void saveSettings() {
        try {
            JSONObject previousSettings = new JSONObject(new ReadFileHandler(weakActivity, weakContext, "SETTINGS_VOD").readFile());

            if (!previousSettings.getString(Globals.SETTINGS_AUTOVODEXPORT).equals(settings.getString(Globals.SETTINGS_AUTOVODEXPORT))||
                    previousSettings.getInt(Globals.SETTINGS_INTERVAL) != settings.getInt(Globals.SETTINGS_INTERVAL) ||
                    !previousSettings.getString(Globals.SETTINGS_INTERVAL_UNIT).equals(settings.getString(Globals.SETTINGS_INTERVAL_UNIT)) ||
                    previousSettings.getBoolean(Globals.SETTINGS_VISIBILITY) != settings.getBoolean(Globals.SETTINGS_VISIBILITY) ||
                    previousSettings.getBoolean(Globals.SETTINGS_SPLIT) != settings.getBoolean(Globals.SETTINGS_SPLIT)) {
                if (!settings.getString(Globals.SETTINGS_AUTOVODEXPORT).equals(Globals.SETTINGS_OFF)) {
                    promptActivatingAutoVODExport();
                } else {
                    promptUserSaveSettings();
                }
            } else {
                Toast.makeText(SettingsVOD.this, "No changes made", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch(JSONException e) {
            Toast.makeText(SettingsVOD.this, "Error reading VOD Settings", Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error reading VOD Settings | " + e.toString(), true).run();
        }
    }

    /**
     * Notifies channel to prepare before activating the AutoVODExport Worker
     * @author LethalMaus
     */
    private void promptActivatingAutoVODExport() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsVOD.this, R.style.CustomDialog);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                promptUserSaveSettings();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(SettingsVOD.this, "Changes discarded", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setTitle("AutoVODExport will be activated");
        builder.setMessage("All your VODs will be exported to Youtube based on these settings.");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Prompts channel if these settings are wanted, or if they should be discard.
     * @author LethalMaus
     */
    private void promptUserSaveSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsVOD.this, R.style.CustomDialog);
        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                new WriteFileHandler(weakActivity, weakContext, "SETTINGS_VOD", null, settings.toString(), false).run();
                Toast.makeText(SettingsVOD.this, "Changes saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(SettingsVOD.this, "Changes discarded", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setTitle("Save Settings");
        builder.setMessage("Would you like to save your changes?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
