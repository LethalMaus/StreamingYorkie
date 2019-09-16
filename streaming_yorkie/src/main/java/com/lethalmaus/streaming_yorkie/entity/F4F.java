package com.lethalmaus.streaming_yorkie.entity;

import androidx.room.Entity;

/**
 * F4F Entity that extends User Entity
 * @author LethalMaus
 */
@Entity(tableName = "f4f_excluded")
public class F4F extends User {

    /**
     * F4F Constructor
     * @param id Twitch Id
     * @param display_name Twitch Name
     * @param logo Twitch Logo
     * @param created_at Following Since
     * @param notifications Notifications Activated
     * @param last_updated Last retrieved from Twitch
     */
    public F4F(int id, String display_name, String logo, String created_at, boolean notifications, long last_updated) {
        super(id, display_name, logo, created_at, notifications, last_updated);
    }
}
