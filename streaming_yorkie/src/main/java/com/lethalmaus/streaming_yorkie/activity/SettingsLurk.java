package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Activity for Lurk settings that creates the settings only when first opened.
 * The settings are never needed unless the user tries to change them.
 * @author LethalMaus
 */
public class SettingsLurk extends AppCompatActivity {

    //All contexts are weak referenced to avoid memory leaks
    protected WeakReference<Activity> weakActivity;
    protected WeakReference<Context> weakContext;

    private JSONObject settings;
    private JSONObject previousSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weakActivity = new WeakReference<>(SettingsLurk.this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        setContentView(R.layout.settings_lurk);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle("Lurk");
        }

        if (!new File(getFilesDir().toString() + File.separator + Globals.FILE_SETTINGS_LURK).exists()) {
            createSettingsFile();
        }
        try {
            String settingsFile = new ReadFileHandler(weakActivity, weakContext, Globals.FILE_SETTINGS_LURK).readFile();
            settings = new JSONObject(settingsFile);
            previousSettings = new JSONObject(settingsFile);
            //Ensures settings integrity
            if (!settings.has(Globals.SETTINGS_AUTOLURK) || !settings.has(Globals.SETTINGS_WIFI_ONLY) || !settings.has(Globals.SETTINGS_AUDIO_ONLY) || !settings.has(Globals.SETTINGS_LURK_INFORM) || !settings.has(Globals.SETTINGS_LURK_MESSAGE)) {
                Toast.makeText(SettingsLurk.this, getString(R.string.settings_integrity), Toast.LENGTH_SHORT).show();
                new DeleteFileHandler(weakActivity, weakContext, null).deleteFileOrPath(Globals.FILE_SETTINGS_LURK);
                createSettingsFile();
            }
        } catch (JSONException e) {
            Toast.makeText(SettingsLurk.this, getString(R.string.error_reading_lurk_settings), Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null, getString(R.string.error_reading_lurk_settings) + getString(R.string.pipe) + e.toString(), true).run();
        }

        serviceActivationSwitch();
        wifiOnlySwitch();
        audioOnlySwitch();
        informChannelSwitch();
        informChannelMessage();

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
            settings.put(Globals.SETTINGS_AUTOLURK, Globals.SETTINGS_OFF);
            settings.put(Globals.SETTINGS_WIFI_ONLY, true);
            settings.put(Globals.SETTINGS_AUDIO_ONLY, true);
            settings.put(Globals.SETTINGS_LURK_INFORM, false);
            settings.put(Globals.SETTINGS_LURK_MESSAGE, "");
            new WriteFileHandler(weakActivity, weakContext, Globals.FILE_SETTINGS_LURK, null, settings.toString(), false).writeToFileOrPath();
        } catch (JSONException e) {
            Toast.makeText(SettingsLurk.this, getString(R.string.error_creating_lurk_settings), Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null, getString(R.string.error_creating_lurk_settings) + getString(R.string.pipe) + e.toString(), true).run();
        }
    }

    /**
     * Reads settings and changes the AutoLurk activation switch. Also adds the Listener
     * @author LethalMaus
     */
    private void serviceActivationSwitch() {
        SwitchCompat autoLurkActivation = findViewById(R.id.settings_autolurk_activation);
        try {
            autoLurkActivation.setChecked(settings.getString(Globals.SETTINGS_AUTOLURK).equals(Globals.SETTINGS_LURK));
        } catch(JSONException e) {
            Toast.makeText(SettingsLurk.this, getString(R.string.error_reading_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_AUTOLURK, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null,getString(R.string.error_reading_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_AUTOLURK + getString(R.string.pipe) + e.toString(), true).run();
        }
        autoLurkActivation.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            try {
                if (isChecked) {
                    settings.put(Globals.SETTINGS_AUTOLURK, Globals.SETTINGS_LURK);
                } else {
                    settings.put(Globals.SETTINGS_AUTOLURK, Globals.SETTINGS_OFF);
                }
            } catch(JSONException e) {
                Toast.makeText(SettingsLurk.this, getString(R.string.error_changing_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_AUTOLURK, Toast.LENGTH_SHORT).show();
                new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null, getString(R.string.error_changing_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_AUTOLURK + getString(R.string.pipe) + e.toString(), true).run();
            }
        });
    }

    /**
     * Reads settings and changes the WiFi Only activation switch. Also adds the Listener
     * @author LethalMaus
     */
    private void wifiOnlySwitch() {
        SwitchCompat wifiOnly = findViewById(R.id.settings_autolurk_wifi_only);
        try {
            wifiOnly.setChecked(settings.getBoolean(Globals.SETTINGS_WIFI_ONLY));
        } catch(JSONException e) {
            Toast.makeText(SettingsLurk.this, getString(R.string.error_reading_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_WIFI_ONLY, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null,getString(R.string.error_reading_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_WIFI_ONLY + getString(R.string.pipe) + e.toString(), true).run();
        }
        wifiOnly.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            try {
                settings.put(Globals.SETTINGS_WIFI_ONLY, isChecked);
            } catch(JSONException e) {
                Toast.makeText(SettingsLurk.this, getString(R.string.error_changing_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_WIFI_ONLY, Toast.LENGTH_SHORT).show();
                new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null, getString(R.string.error_changing_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_WIFI_ONLY + getString(R.string.pipe) + e.toString(), true).run();
            }
        });
    }

    /**
     * Reads settings and changes the Audio only activation switch. Also adds the Listener
     * @author LethalMaus
     */
    private void audioOnlySwitch() {
        SwitchCompat audioOnly = findViewById(R.id.settings_autolurk_audio_only);
        try {
            audioOnly.setChecked(settings.getBoolean(Globals.SETTINGS_AUDIO_ONLY));
        } catch(JSONException e) {
            Toast.makeText(SettingsLurk.this, getString(R.string.error_reading_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_AUDIO_ONLY, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null,getString(R.string.error_reading_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_AUDIO_ONLY + getString(R.string.pipe) + e.toString(), true).run();
        }
        audioOnly.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            try {
                Toast.makeText(SettingsLurk.this, getString(R.string.settings_autolurk_audio_only_info), Toast.LENGTH_SHORT).show();
                settings.put(Globals.SETTINGS_AUDIO_ONLY, isChecked);
            } catch(JSONException e) {
                Toast.makeText(SettingsLurk.this, getString(R.string.error_changing_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_AUDIO_ONLY, Toast.LENGTH_SHORT).show();
                new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null, getString(R.string.error_changing_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_AUDIO_ONLY + getString(R.string.pipe) + e.toString(), true).run();
            }
        });
    }

    /**
     * Reads settings and changes the Inform Channel activation switch. Also adds the Listener
     * @author LethalMaus
     */
    private void informChannelSwitch() {
        SwitchCompat informChannel = findViewById(R.id.settings_autolurk_inform);
        try {
            informChannel.setChecked(settings.getBoolean(Globals.SETTINGS_LURK_INFORM));
        } catch(JSONException e) {
            Toast.makeText(SettingsLurk.this, getString(R.string.error_reading_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_LURK_INFORM, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null,getString(R.string.error_reading_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_LURK_INFORM + getString(R.string.pipe) + e.toString(), true).run();
        }
        informChannel.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            try {
                settings.put(Globals.SETTINGS_LURK_INFORM, isChecked);
            } catch(JSONException e) {
                Toast.makeText(SettingsLurk.this, getString(R.string.error_changing_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_LURK_INFORM, Toast.LENGTH_SHORT).show();
                new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null, getString(R.string.error_changing_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_LURK_INFORM + getString(R.string.pipe) + e.toString(), true).run();
            }
        });
    }

    /**
     * Reads settings and changes the Inform Channel Message text. Also adds the Listener
     * @author LethalMaus
     */
    private void informChannelMessage() {
        EditText informChannelMessage = findViewById(R.id.settings_autolurk_message);
        try {
            informChannelMessage.setText(settings.getString(Globals.SETTINGS_LURK_MESSAGE));
        } catch(JSONException e) {
            Toast.makeText(SettingsLurk.this, getString(R.string.error_reading_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_LURK_MESSAGE, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null,getString(R.string.error_reading_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_LURK_MESSAGE + getString(R.string.pipe) + e.toString(), true).run();
        }
        informChannelMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    settings.put(Globals.SETTINGS_LURK_MESSAGE, s.toString().trim());
                } catch (JSONException e) {
                    Toast.makeText(SettingsLurk.this, getString(R.string.error_changing_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_LURK_MESSAGE, Toast.LENGTH_SHORT).show();
                    new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null, getString(R.string.error_changing_lurk_settings) + getString(R.string.pipe) + Globals.SETTINGS_LURK_MESSAGE + getString(R.string.pipe) + e.toString(), true).run();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {/*Not used*/}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {/*Not used*/}
        });
    }

    /**
     * Checks if changes were made anywhere and prompts channel to save.
     * @author LethalMaus
     */
    private void saveSettings() {
        try {
            if (!previousSettings.getString(Globals.SETTINGS_AUTOLURK).equals(settings.getString(Globals.SETTINGS_AUTOLURK)) ||
                    previousSettings.getBoolean(Globals.SETTINGS_WIFI_ONLY) != settings.getBoolean(Globals.SETTINGS_WIFI_ONLY) ||
                    previousSettings.getBoolean(Globals.SETTINGS_AUDIO_ONLY) != settings.getBoolean(Globals.SETTINGS_AUDIO_ONLY) ||
                    previousSettings.getBoolean(Globals.SETTINGS_LURK_INFORM) != settings.getBoolean(Globals.SETTINGS_LURK_INFORM) ||
                    !previousSettings.getString(Globals.SETTINGS_LURK_MESSAGE).equals(settings.getString(Globals.SETTINGS_LURK_MESSAGE))) {
                if (!settings.getString(Globals.SETTINGS_AUTOLURK).equals(Globals.SETTINGS_OFF) && (!settings.getBoolean(Globals.SETTINGS_WIFI_ONLY) || (settings.getBoolean(Globals.SETTINGS_LURK_INFORM) && settings.getString(Globals.SETTINGS_LURK_MESSAGE).isEmpty()))) {
                    promptActivatingAutoLurk();
                } else {
                    promptUserSaveSettings();
                }
            } else {
                Toast.makeText(SettingsLurk.this, "No changes made", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch(JSONException e) {
            Toast.makeText(SettingsLurk.this, getString(R.string.error_reading_lurk_settings), Toast.LENGTH_SHORT).show();
            new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null, getString(R.string.error_reading_lurk_settings) + getString(R.string.pipe) + e.toString(), true).run();
        }
    }

    /**
     * Notifies user to prepare before activating the AutoLurk Worker
     * @author LethalMaus
     */
    private void promptActivatingAutoLurk() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsLurk.this, R.style.CustomDialog);
        builder.setPositiveButton("OK", (DialogInterface dialog, int id) ->
                promptUserSaveSettings()
        );
        builder.setNegativeButton("CANCEL", (DialogInterface dialog, int id) ->  {
            Toast.makeText(SettingsLurk.this, "Changes discarded", Toast.LENGTH_SHORT).show();
            finish();
        });
        builder.setTitle("WARNING: AutoLurk requires preparation");
        builder.setMessage("Make sure you have checked the WiFi only option if you do not want to use up your mobile data. If the Inform Channel Message is empty & the Inform Channel switch is activated, the message will be defaulted to '!lurk'");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Prompts user if these settings are wanted, or if they should be discarded.
     * @author LethalMaus
     */
    private void promptUserSaveSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsLurk.this, R.style.CustomDialog);
        builder.setPositiveButton("SAVE", (DialogInterface dialog, int id) ->  {
            new WriteFileHandler(weakActivity, weakContext, Globals.FILE_SETTINGS_LURK, null, settings.toString(), false).run();
            Toast.makeText(SettingsLurk.this, "Changes saved", Toast.LENGTH_SHORT).show();
            finish();
        });
        builder.setNegativeButton("CANCEL", (DialogInterface dialog, int id) ->  {
            Toast.makeText(SettingsLurk.this, "Changes discarded", Toast.LENGTH_SHORT).show();
            finish();
        });
        builder.setTitle("Save Settings");
        builder.setMessage("Would you like to save your changes?");
        builder.create().show();
    }
}
