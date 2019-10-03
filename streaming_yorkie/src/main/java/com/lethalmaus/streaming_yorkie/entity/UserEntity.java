package com.lethalmaus.streaming_yorkie.entity;

import androidx.room.PrimaryKey;

/**
 * UserEntity Entity
 * @author LethalMaus
 */
public class UserEntity {

    @PrimaryKey
    private int id;
    private String display_name;
    private String logo;
    private String created_at;
    private boolean notifications;
    private long last_updated;
    private String status;

    /**
     * UserEntity Constructor
     * @param id Twitch Id
     * @param display_name Twitch Name
     * @param logo Twitch Logo
     * @param created_at FollowingEntity Since
     * @param notifications Notifications Activated
     * @param last_updated Last retrieved from Twitch
     */
    public UserEntity(int id, String display_name, String logo, String created_at, boolean notifications, long last_updated) {
        this.id = id;
        this.display_name = display_name;
        this.logo = logo;
        this.created_at = created_at;
        this.notifications = notifications;
        this.last_updated = last_updated;
    }

    /**
     * Get id
     * @author LethalMaus
     * @return int id
     */
    public int getId() {
        return id;
    }

    /** Set id
     * @author LethalMaus
     * @param id Twitch Id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get display_name
     * @author LethalMaus
     * @return String display_name
     */
    public String getDisplay_name() {
        return display_name;
    }

    /** Set display_name
     * @author LethalMaus
     * @param display_name Twitch Name
     */
    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    /**
     * Get logo
     * @author LethalMaus
     * @return String logo
     */
    public String getLogo() {
        return logo;
    }

    /** Set logo
     * @author LethalMaus
     * @param logo Twitch Logo
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     * Get created_at
     * @author LethalMaus
     * @return String created_at
     */
    public String getCreated_at() {
        return created_at;
    }

    /** Set created_at
     * @author LethalMaus
     * @param created_at FollowingEntity Since
     */
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**
     * Get notifications
     * @author LethalMaus
     * @return boolean notifications
     */
    public boolean isNotifications() {
        return notifications;
    }

    /** Set notifications
     * @author LethalMaus
     * @param notifications Notifications Activated
     */
    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    /**
     * Get last_updated
     * @author LethalMaus
     * @return long last_updated
     */
    public long getLast_updated() {
        return last_updated;
    }

    /** Set last_updated
     * @author LethalMaus
     * @param last_updated Last retrieved from Twitch
     */
    public void setLast_updated(long last_updated) {
        this.last_updated = last_updated;
    }

    /**
     * Get status
     * @author LethalMaus
     * @return String status
     */
    public String getStatus() {
        return status;
    }

    /** Set status
     * @author LethalMaus
     * @param status String - NEW, CURRENT, EXCLUDED, ...
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
