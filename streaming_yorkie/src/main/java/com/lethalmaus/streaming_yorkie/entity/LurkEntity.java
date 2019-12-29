package com.lethalmaus.streaming_yorkie.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * VODEntity Entity
 * @author LethalMaus
 */
@Entity(tableName = "lurk")
public class LurkEntity {

    @PrimaryKey
    @NonNull
    private String channelName;
    private int channelId;
    private String broadcastId;
    private String logo;
    private String html;
    private boolean channelInformedOfLurk;
    private boolean channelIsToBeLurked;

    /**
     * LurkEntity Constructor
     * @author LethalMaus
     * @param channelName Channel Name
     * @param channelId Channel Id
     * @param broadcastId Broadcast Id
     * @param logo Channel Logo
     * @param html Video player html block
     * @param channelInformedOfLurk To ensure channel is only informed once
     * @param channelIsToBeLurked To ensure whether the channel is to be lurked or not
     */
    public LurkEntity(@NonNull String channelName, int channelId, String broadcastId, String logo, String html, boolean channelInformedOfLurk, boolean channelIsToBeLurked) {
        this.channelName = channelName;
        this.channelId = channelId;
        this.broadcastId = broadcastId;
        this.logo = logo;
        this.html = html;
        this.channelInformedOfLurk = channelInformedOfLurk;
        this.channelIsToBeLurked = channelIsToBeLurked;
    }

    /**
     * Get Channel Id
     * @author LethalMaus
     * @return int channelId
     */
    public int getChannelId() {
        return channelId;
    }

    /**
     * Set Channel Id
     * @author LethalMaus
     * @param channelId int
     */
    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    /**
     * Get Channel Name
     * @author LethalMaus
     * @return String channelName
     */
    @NonNull
    public String getChannelName() {
        return channelName;
    }

    /**
     * Get Broadcast Id
     * @author LethalMaus
     * @return String broadcastId
     */
    public String getBroadcastId() {
        return broadcastId;
    }

    /**
     * Set Broadcast Id
     * @author LethalMaus
     * @param broadcastId String
     */
    public void setBroadcastId(String broadcastId) {
        this.broadcastId = broadcastId;
    }

    /**
     * Get Channel Logo
     * @author LethalMaus
     * @return String logo
     */
    public String getLogo() {
        return logo;
    }

    /**
     * Set Channel Logo
     * @author LethalMaus
     * @param logo String
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     * Get HTML block
     * @author LethalMaus
     * @return String html
     */
    public String getHtml() {
        return html;
    }

    /**
     * Set HTML block
     * @author LethalMaus
     * @param html String
     */
    public void setHtml(String html) {
        this.html = html;
    }

    /**
     * Is Channel informed of Lurk
     * @author LethalMaus
     * @return boolean channelInformedOfLurk
     */
    public boolean isChannelInformedOfLurk() {
        return channelInformedOfLurk;
    }

    /**
     * Set Channel informed of Lurk
     * @author LethalMaus
     * @param channelInformedOfLurk boolean
     */
    public void setChannelInformedOfLurk(boolean channelInformedOfLurk) {
        this.channelInformedOfLurk = channelInformedOfLurk;
    }

    /**
     * Is Channel to be lurked
     * @author LethalMaus
     * @return boolean channelIsToBeLurked
     */
    public boolean isChannelIsToBeLurked() {
        return channelIsToBeLurked;
    }

    /**
     * Set Channel is to be lurked
     * @author LethalMaus
     * @param channelIsToBeLurked boolean
     */
    public void setChannelIsToBeLurked(boolean channelIsToBeLurked) {
        this.channelIsToBeLurked = channelIsToBeLurked;
    }
}
