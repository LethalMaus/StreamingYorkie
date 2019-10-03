package com.lethalmaus.streaming_yorkie.entity;

import androidx.room.Entity;

/**
 * F4FEntity Entity that extends UserEntity Entity
 * @author LethalMaus
 */
@Entity(tableName = "f4f_excluded")
public class F4FEntity extends UserEntity {

    /**
     * F4FEntity Constructor
     * @param id Twitch Id
     * @param display_name Twitch Name
     * @param logo Twitch Logo
     * @param created_at FollowingEntity Since
     * @param notifications Notifications Activated
     * @param last_updated Last retrieved from Twitch
     */
    public F4FEntity(int id, String display_name, String logo, String created_at, boolean notifications, long last_updated) {
        super(id, display_name, logo, created_at, notifications, last_updated);
    }
}
