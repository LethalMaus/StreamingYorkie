package com.lethalmaus.streaming_yorkie.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.lethalmaus.streaming_yorkie.entity.ChannelEntity;

/**
 * ChannelDAO Interface
 * @author LethalMaus
 */
@Dao
public interface ChannelDAO {

    /**
     * Get ChannelEntity
     * @author LethalMaus
     * @return ChannelEntity
     */
    @Query("SELECT * FROM channel LIMIT 1")
    ChannelEntity getChannel();

    /**
     * Get ChannelEntity by Id
     * @author LethalMaus
     * @param id ChannelEntity Id
     * @return ChannelEntity
     */
    @Query("SELECT * FROM channel WHERE id = :id")
    ChannelEntity getChannelById(int id);

    /**
     * Inserts a ChannelEntity and replaces on conflict
     * @author LethalMaus
     * @param channelEntity ChannelEntity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChannel(ChannelEntity channelEntity);

    /**
     * Updates a ChannelEntity
     * @author LethalMaus
     * @param channelEntity ChannelEntity
     */
    @Update
    void updateChannel(ChannelEntity channelEntity);
}
