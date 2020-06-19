package com.lethalmaus.streaming_yorkie.data_model;

/**
 * Data model fro AutoHostList requests
 * @author LethalMaus
 */
public class HostDataModel {

    private String id;
    private String login;
    private String displayName;
    private String profileImageURL;

    /**
     * Constructor for the data model for requests
     * @author LethalMaus
     * @param id twitch user id
     */
    public HostDataModel(String id) {
        this.id = id;
    }

    /**
     * Constructor for the data model for request response
     * @author LethalMaus
     * @param id twitch user id
     * @param login twitch login name (may vary to displayName)
     * @param displayName twitch display name
     * @param profileImageURL user logo
     */
    public HostDataModel(String id, String login, String displayName, String profileImageURL) {
        this.id = id;
        this.login = login;
        this.displayName = displayName;
        this.profileImageURL = profileImageURL.replace("300x300", "50x50");
    }

    /**
     * Get users twitch id
     * @author LethalMaus
     * @return String id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets users login name
     * @return String login
     */
    public String getLogin() {
        return login;
    }

    /**
     *Gets users display name
     * @author LethalMaus
     * @return String displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets users logo
     * @author LethalMaus
     * @return String profileImageURL
     */
    public String getProfileImageURL() {
        return profileImageURL;
    }
}
