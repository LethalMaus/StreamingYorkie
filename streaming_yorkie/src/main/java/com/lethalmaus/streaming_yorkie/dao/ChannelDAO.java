package com.lethalmaus.streaming_yorkie.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.lethalmaus.streaming_yorkie.entity.Channel;

/**
 * ChannelDAO Interface
 * @author LethalMaus
 */
@Dao
public interface ChannelDAO {

    /**
     * Get Channel
     * @author LethalMaus
     * @return Channel
     */
    @Query("SELECT * FROM channel LIMIT 1")
    Channel getChannel();

    /**
     * Get Channel by Id
     * @author LethalMaus
     * @param id Channel Id
     * @return Channel
     */
    @Query("SELECT * FROM channel WHERE id = :id")
    Channel getChanneById(int id);

    /**
     * Inserts a Channel and replaces on conflict
     * @author LethalMaus
     * @param channel Channel
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChannel(Channel channel);

    /**
     * Updates a Channel
     * @author LethalMaus
     * @param channel Channel
     */
    @Update
    void updateChannel(Channel channel);
}
