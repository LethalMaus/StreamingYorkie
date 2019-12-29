package com.lethalmaus.streaming_yorkie.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.lethalmaus.streaming_yorkie.dao.ChannelDAO;
import com.lethalmaus.streaming_yorkie.dao.F4FDAO;
import com.lethalmaus.streaming_yorkie.dao.FollowerDAO;
import com.lethalmaus.streaming_yorkie.dao.FollowingDAO;
import com.lethalmaus.streaming_yorkie.dao.LurkDAO;
import com.lethalmaus.streaming_yorkie.dao.VODDAO;
import com.lethalmaus.streaming_yorkie.entity.ChannelEntity;
import com.lethalmaus.streaming_yorkie.entity.F4FEntity;
import com.lethalmaus.streaming_yorkie.entity.FollowerEntity;
import com.lethalmaus.streaming_yorkie.entity.FollowingEntity;
import com.lethalmaus.streaming_yorkie.entity.LurkEntity;
import com.lethalmaus.streaming_yorkie.entity.VODEntity;

/**
 * Streaming Yorkie Database
 * @author LethalMaus
 */
@Database(entities = {FollowerEntity.class, FollowingEntity.class, F4FEntity.class, ChannelEntity.class, VODEntity.class, LurkEntity.class}, exportSchema = false, version = 1)
public abstract class StreamingYorkieDB extends RoomDatabase {
    private static final String DB_NAME = "streaming_yorkie";
    private static StreamingYorkieDB steamingYorkieDBInstance;

    /**
     * Gets the current instance of the DB
     * @author LethalMaus
     * @param context Application Context
     * @return Instance of DB
     */
    public static synchronized StreamingYorkieDB getInstance(Context context) {
        if (steamingYorkieDBInstance == null) {
            steamingYorkieDBInstance = Room.databaseBuilder(context, StreamingYorkieDB.class, DB_NAME).build();
        }
        return steamingYorkieDBInstance;
    }

    /**
     * Abstract FollowerDAO
     * @author LethalMaus
     * @return FollowerDAO
     */
    public abstract FollowerDAO followerDAO();

    /**
     * Abstract FollowingDAO
     * @author LethalMaus
     * @return FollowingDAO
     */
    public abstract FollowingDAO followingDAO();

    /**
     * Abstract F4FDAO
     * @author LethalMaus
     * @return F4FDAO
     */
    public abstract F4FDAO f4fDAO();

    /**
     * Abstract ChannelDAO
     * @author LethalMaus
     * @return ChannelDAO
     */
    public abstract ChannelDAO channelDAO();

    /**
     * Abstract VODDAO
     * @author LethalMaus
     * @return VODDAO
     */
    public abstract VODDAO vodDAO();

    /**
     * Abstract LurkDAO
     * @author LethalMaus
     * @return LurkDAO
     */
    public abstract LurkDAO lurkDAO();

}
