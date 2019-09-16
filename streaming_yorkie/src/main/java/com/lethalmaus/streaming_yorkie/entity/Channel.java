package com.lethalmaus.streaming_yorkie.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Channel Entity
 * @author LethalMaus
 */
@Entity(tableName = "channel")
public class Channel {

    @PrimaryKey
    private int id;
    private String display_name;
    private String logo;
    private String created_at;
    private String game;
    private int views;
    private int followers;
    private String status;
    private String description;
    private String broadcasterType;

    /**
     * Channel constructor
     * @author LethalMaus
     * @param id Twitch Id
     * @param display_name Twitch Name
     * @param logo Twitch Logo
     * @param game Current Game
     * @param created_at Account creation date
     * @param views View count
     * @param followers Follower amount
     * @param status Current status
     * @param description Current description
     * @param broadcasterType eg. Affiliated
     */
    public Channel(int id, String display_name, String logo, String game, String created_at, int views, int followers, String status, String description, String broadcasterType) {
        this.id = id;
        this.display_name = display_name;
        this.logo = logo;
        this.game = game;
        this.created_at = created_at;
        this.views = views;
        this.followers = followers;
        this.status = status;
        this.description = description;
        if (broadcasterType.contentEquals("")) {
            this.broadcasterType = "streamer";
        } else {
            this.broadcasterType = broadcasterType;
        }
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
     * @param created_at Following Since
     */
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**
     * Get game
     * @author LethalMaus
     * @return String game
     */
    public String getGame() {
        return game;
    }

    /**
     * Set game
     * @author LethalMaus
     * @param game String
     */
    public void setGame(String game) {
        this.game = game;
    }

    /**
     * get views amount
     * @author LethalMaus
     * @return int followers
     */
    public int getViews() {
        return views;
    }

    /**
     * set views amount
     * @author LethalMaus
     * @param views int
     */
    public void setViews(int views) {
        this.views = views;
    }

    /**
     * Get followers amount
     * @author LethalMaus
     * @return int followers
     */
    public int getFollowers() {
        return followers;
    }

    /**
     * Set followers amount
     * @author LethalMaus
     * @param followers int
     */
    public void setFollowers(int followers) {
        this.followers = followers;
    }

    /**
     * Get status
     * @author LethalMaus
     * @return String status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set status
     * @author LethalMaus
     * @param status String
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get description
     * @author LethalMaus
     * @return String description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description
     * @author LethalMaus
     * @param description String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get broadcaster_type
     * @author LethalMaus
     * @return String broadcasterType
     */
    public String getBroadcasterType() {
        return broadcasterType;
    }

    /** Set broadcaster_type
     * @author LethalMaus
     * @param broadcasterType String
     */
    public void setBroadcasterType(String broadcasterType) {
        this.broadcasterType = broadcasterType;
    }
}
