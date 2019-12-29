package com.lethalmaus.streaming_yorkie.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * VODEntity Entity
 * @author LethalMaus
 */
@Entity(tableName = "vod")
public class VODEntity {

    @PrimaryKey
    private int id;
    private String title;
    private String url;
    private String created_at;
    private String description;
    private String tag_list;
    private String game;
    private String length;
    private String preview;
    private boolean exported;
    private boolean excluded;
    private long last_updated;

    /**
     * VODEntity Constructor
     * @author LethalMaus
     * @param id VODEntity Id
     * @param title VODEntity Title
     * @param url VODEntity Url
     * @param created_at VODEntity creation date
     * @param length VODEntity length
     * @param preview VODEntity preview url
     * @param last_updated When the VODEntity was last updated
     */
    public VODEntity(int id, String title, String url, String created_at, String length, String preview, long last_updated) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.created_at = created_at;
        this.length = length;
        this.preview = preview;
        this.last_updated = last_updated;
    }

    /**
     * Get VODEntity id
     * @author LethalMaus
     * @return int id
     */
    public int getId() {
        return id;
    }

    /**
     * Set VODEntity id
     * @author LethalMaus
     * @param id int
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get VODEntity title
     * @author LethalMaus
     * @return String title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set VODEntity title
     * @author LethalMaus
     * @param title String
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get VODEntity Url
     * @author LethalMaus
     * @return String url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set VODEntity Url
     * @author LethalMaus
     * @param url String
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get VODEntity creation date
     * @author LethalMaus
     * @return String created_at
     */
    public String getCreated_at() {
        return created_at;
    }

    /**
     * Set VODEntity creation date
     * @author LethalMaus
     * @param created_at String
     */
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**
     * Get VODEntity description
     * @author LethalMaus
     * @return String description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set VODEntity description
     * @author LethalMaus
     * @param description String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get VODEntity tag list
     * @author LethalMaus
     * @return String tag_list
     */
    public String getTag_list() {
        return tag_list;
    }

    /**
     * Set VODEntity tag list
     * @author LethalMaus
     * @param tag_list String
     */
    public void setTag_list(String tag_list) {
        this.tag_list = tag_list;
    }

    /**
     * Get VODEntity game
     * @author LethalMaus
     * @return String game
     */
    public String getGame() {
        return game;
    }

    /**
     * Set VODEntity game
     * @author LethalMaus
     * @param game String
     */
    public void setGame(String game) {
        this.game = game;
    }

    /**
     * Get VODEntity length
     * @author LethalMaus
     * @return String length
     */
    public String getLength() {
        return length;
    }

    /**
     * Set VODEntity length
     * @author LethalMaus
     * @param length String
     */
    public void setLength(String length) {
        this.length = length;
    }

    /**
     * Get VODEntity preview url
     * @author LethalMaus
     * @return String preview
     */
    public String getPreview() {
        return preview;
    }

    /**
     * Is exported
     * @author LethalMaus
     * @return boolean exported
     */
    public boolean isExported() {
        return exported;
    }

    /**
     * Set exported
     * @author LethalMaus
     * @param exported boolean
     */
    public void setExported(boolean exported) {
        this.exported = exported;
    }

    /**
     * Is excluded
     * @author LethalMaus
     * @return boolean excluded
     */
    public boolean isExcluded() {
        return excluded;
    }

    /**
     * Set excluded
     * @author LethalMaus
     * @param excluded boolean
     */
    public void setExcluded(boolean excluded) {
        this.excluded = excluded;
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
}
