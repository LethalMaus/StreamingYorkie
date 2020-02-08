package com.lethalmaus.streaming_yorkie.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.lethalmaus.streaming_yorkie.entity.LurkEntity;

/**
 * LurkDAO Interface
 * @author LethalMaus
 */
@Dao
public interface LurkDAO {

    /**
     * Get LurkEntity by Channel Name
     * @author LethalMaus
     * @param channelName LurkEntity Channel Name
     * @return LurkEntity
     */
    @Query("SELECT * FROM lurk WHERE channelName LIKE :channelName")
    LurkEntity getLurkByChannelName(String channelName);

    /**
     * Get LurkEntity by Channel ID
     * @author LethalMaus
     * @param channelId LurkEntity Channel ID
     * @return LurkEntity
     */
    @Query("SELECT * FROM lurk WHERE channelId = :channelId")
    LurkEntity getLurkByChannelId(String channelId);

    /**
     * Get current Lurks by position
     * @author LethalMaus
     * @param offset position
     * @return LurkEntity
     */
    @Query("SELECT * FROM lurk ORDER BY channelName ASC LIMIT 1 OFFSET :offset")
    LurkEntity getLurkByPosition(int offset);

    /**
     * Get current channels to be lurked by position
     * @author LethalMaus
     * @param offset position
     * @return LurkEntity
     */
    @Query("SELECT * FROM lurk WHERE channelIsToBeLurked = 1 ORDER BY channelName ASC LIMIT 1 OFFSET :offset")
    LurkEntity getChannelsToBeLurkedByPosition(int offset);

    /**
     * Get list of channel names to be lurked
     * @author LethalMaus
     * @return ArrayList of Int
     */
    @Query("SELECT channelId FROM lurk WHERE channelIsToBeLurked = 1")
    int[] getChannelIdsToBeLurked();

    /**
     * Get list of channels that are online
     * @author LethalMaus
     * @return ArrayList of Strings
     */
    @Query("SELECT * FROM lurk WHERE channelIsToBeLurked = 1 AND html IS NOT NULL")
    LurkEntity[] getOnlineLurks();

    /**
     * Get list of channel names that are offline
     * @author LethalMaus
     * @return ArrayList of Strings
     */
    @Query("SELECT channelName FROM lurk WHERE html IS NULL")
    String[] getOfflineLurks();

    /**
     * Get total LurkEntity count
     * @author LethalMaus
     * @return int count
     */
    @Query("SELECT COUNT() FROM lurk")
    int getLurkCount();

    /**
     * Get total channels to be lurked count
     * @author LethalMaus
     * @return int count
     */
    @Query("SELECT COUNT() FROM lurk WHERE channelIsToBeLurked = 1")
    int getChannelsToBeLurkedCount();

    /**
     * Inserts a LurkEntity and replaces on conflict
     * @author LethalMaus
     * @param lurkEntity LurkEntity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLurk(LurkEntity lurkEntity);

    /**
     * Updates existing LurkEntity
     * @author LethalMaus
     * @param lurkEntity LurkEntity
     */
    @Update
    void updateLurk(LurkEntity lurkEntity);

    /**
     * Delete LurkEntity by Channel Name
     * @author LethalMaus
     * @param channelName String
     */
    @Query("DELETE FROM lurk WHERE channelName LIKE :channelName")
    void deleteLurkByChannelName(String channelName);
}
