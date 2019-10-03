package com.lethalmaus.streaming_yorkie;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.lethalmaus.streaming_yorkie.activity.Authorization;
import com.lethalmaus.streaming_yorkie.activity.Info;
import com.lethalmaus.streaming_yorkie.activity.InfoGuide;
import com.lethalmaus.streaming_yorkie.activity.SettingsMenu;

/**
 * Globals contains centralized constants & variables that are used throughout the whole app.
 * @author LethalMaus
 */
public class Globals {

    //ID of the app registered by Twitch
    public static final String CLIENTID = "tjots3mhxunw0sj2a20ka3wz39p7bp";
    //ID of Twitch
    public static final String TWITCHID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
    //Object limit per request. Gets added to offset
    public static final int USER_REQUEST_LIMIT = 25;
    public static final int USER_UPDATE_REQUEST_LIMIT = 3;
    public static final int VOD_REQUEST_LIMIT = 10;
    public static final int VOD_UPDATE_REQUEST_LIMIT = 1;

    //Request Types
    public static final String CHANNEL = "CHANNEL";

    //Directories for Lurks
    public static final String LURK_PATH = "LURKS";

    //Settings object keys
    public static final String SETTINGS_AUTOFOLLOW = "AutoFollow";
    public static final String SETTINGS_AUTOVODEXPORT = "AutoVODExport";
    public static final String SETTINGS_INTERVAL = "Interval";
    public static final String SETTINGS_INTERVAL_UNIT = "IntervalUnit";
    public static final String SETTINGS_NOTIFICATIONS = "Notifications";
    public static final String SETTINGS_VISIBILITY = "Visibility";
    public static final String SETTINGS_SPLIT = "Split";

    //Settings
    public static final String SETTINGS_OFF = "OFF";
    public static final String SETTINGS_FOLLOW = "FOLLOW";
    public static final String SETTINGS_UNFOLLOW = "UNFOLLOW";
    public static final String SETTINGS_FOLLOWUNFOLLOW = "FOLLOW_UNFOLLOW";
    public static final String SETTINGS_SHARE_F4F_STATUS = "SHARE_F4F_STATUS";
    public static final String SETTINGS_EXPORT = "EXPORT";

    //Settings interval unit
    public static final String SETTINGS_INTERVAL_UNIT_MINUTES = "MINUTES";
    public static final String SETTINGS_INTERVAL_UNIT_HOURS = "HOURS";
    public static final String SETTINGS_INTERVAL_UNIT_DAYS = "DAYS";

    //Notification Folders
    public static final String NOTIFICATION_FOLLOW = "NOTIFICATION_FOLLOW";
    public static final String NOTIFICATION_UNFOLLOW = "NOTIFICATION_UNFOLLOW";
    public static final String NOTIFICATION_VODEXPORT = "NOTIFICATION_VODEXPORT";

    //Notifications ID, Name & Description
    public static final String AUTOFOLLOW_NOTIFICATION_CHANNEL_ID = "AUTOFOLLOW_NOTIFICATION";
    public static final String AUTOFOLLOW_NOTIFICATION_CHANNEL_NAME = "AutoFollow";
    public static final String AUTOFOLLOW_NOTIFICATION_CHANNEL_DESCRIPTION = "Notify channel if new followers are followed and unfollowers that are unfollowed";

    public static final String AUTOVODEXPORT_NOTIFICATION_CHANNEL_ID = "AUTOVODEXPORT_NOTIFICATION";
    public static final String AUTOVODEXPORT_NOTIFICATION_CHANNEL_NAME = "AutoVODExport";
    public static final String AUTOVODEXPORT_NOTIFICATION_CHANNEL_DESCRIPTION = "Notify channel if new VODs have been exported";

    public static final String LURKSERVICE_NOTIFICATION_CHANNEL_ID = "LURKSERVICE_NOTIFICATION";
    public static final String LURKSERVICE_NOTIFICATION_CHANNEL_NAME = "LurkService";
    public static final String LURKSERVICE_NOTIFICATION_CHANNEL_DESCRIPTION = "Notify channel if lurking is activated";

    //Flag Files
    public static final String FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE = "FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE";
    public static final String FLAG_AUTOVODEXPORT_NOTIFICATION_UPDATE = "FLAG_AUTOVODEXPORT_NOTIFICATION_UPDATE";

    /**
     * Options menu to be available throughout app
     * @author LethalMaus
     * @param activity activity requiring options
     * @param item MenuItem that is selected eg. Info or Settings
     * @return boolean whether an option was successfully selected
     */
    public static boolean onOptionsItemsSelected(Activity activity, MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_info_guide:
                intent = new Intent(activity, InfoGuide.class);
                activity.startActivity(intent);
                return true;
            case R.id.menu_info:
                intent = new Intent(activity, Info.class);
                activity.startActivity(intent);
                return true;
            case R.id.menu_settings:
                intent = new Intent(activity, SettingsMenu.class);
                activity.startActivity(intent);
                return true;
            case R.id.menu_logout:
                intent = new Intent(activity, Authorization.class);
                activity.startActivity(intent);
                return true;
            default:
                return false;
        }
    }
}
