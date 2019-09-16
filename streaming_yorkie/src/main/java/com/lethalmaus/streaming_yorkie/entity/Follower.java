package com.lethalmaus.streaming_yorkie.entity;

import androidx.room.Entity;

/**
 * Follower Entity that extends User Entity
 * @author LethalMaus
 */
@Entity(tableName = "follower")
public class Follower extends User {

    /**
     * Follower Constructor
     * @param id Twitch Id
     * @param display_name Twitch Name
     * @param logo Twitch Logo
     * @param created_at Following Since
     * @param notifications Notifications Activated
     * @param last_updated Last retrieved from Twitch
     */
    public Follower(int id, String display_name, String logo, String created_at, boolean notifications, long last_updated) {
        super(id, display_name, logo, created_at, notifications, last_updated);
    }
}
