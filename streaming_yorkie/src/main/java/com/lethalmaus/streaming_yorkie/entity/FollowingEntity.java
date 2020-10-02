package com.lethalmaus.streaming_yorkie.entity;

import androidx.room.Entity;

/**
 * FollowingEntity Entity that extends UserEntity Entity
 * @author LethalMaus
 */
@Entity(tableName = "following")
public class FollowingEntity extends UserEntity {

    private long excludeUntil = 0;

    /**
     * FollowingEntity Constructor
     * @param id Twitch Id
     * @param display_name Twitch Name
     * @param logo Twitch Logo
     * @param created_at FollowingEntity Since
     * @param notifications Notifications Activated
     * @param last_updated Last retrieved from Twitch
     * @param excludeUntil keeps the user excluded until the time is reached
     */
    public FollowingEntity(int id, String display_name, String logo, String created_at, boolean notifications, long last_updated, long excludeUntil) {
        super(id, display_name, logo, created_at, notifications, last_updated);
        excludeUntil = this.excludeUntil;
    }

    /**
     * Get excludedUntil
     * @author LethalMaus
     * @return excludeUntil as timpestamp in millis
     */
    public long getExcludeUntil() { return excludeUntil; }

    /**
     * Set excludedUntil
     * @author LethalMaus
     * @param excludeUntil excludeUntil as timpestamp in millis
     */
    public void setExcludeUntil(long excludeUntil) { this.excludeUntil = excludeUntil; }
}
