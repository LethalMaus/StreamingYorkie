package com.lethalmaus.streaming_yorkie.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lethalmaus.streaming_yorkie.database.dao.ChannelDAO
import com.lethalmaus.streaming_yorkie.database.dao.F4FDAO
import com.lethalmaus.streaming_yorkie.database.dao.FollowerDAO
import com.lethalmaus.streaming_yorkie.database.dao.FollowingDAO
import com.lethalmaus.streaming_yorkie.database.dao.LurkDAO
import com.lethalmaus.streaming_yorkie.database.dao.VODDAO
import com.lethalmaus.streaming_yorkie.database.entity.ChannelEntity
import com.lethalmaus.streaming_yorkie.database.entity.F4FEntity
import com.lethalmaus.streaming_yorkie.database.entity.FollowerEntity
import com.lethalmaus.streaming_yorkie.database.entity.FollowingEntity
import com.lethalmaus.streaming_yorkie.database.entity.LurkEntity
import com.lethalmaus.streaming_yorkie.database.entity.VODEntity

@Database(
    entities = [FollowerEntity::class, FollowingEntity::class, F4FEntity::class, ChannelEntity::class, VODEntity::class, LurkEntity::class],
    version = 5
)
abstract class StreamingYorkieDB : RoomDatabase() {

    abstract fun followerDAO(): FollowerDAO
    abstract fun followingDAO(): FollowingDAO
    abstract fun f4fDAO(): F4FDAO
    abstract fun channelDAO(): ChannelDAO
    abstract fun vodDAO(): VODDAO
    abstract fun lurkDAO(): LurkDAO

    companion object {
        private const val DB_NAME = "streaming_yorkie"
        @Volatile private var instance: StreamingYorkieDB? = null

        @Synchronized
        fun getInstance(context: Context): StreamingYorkieDB {
            return instance ?: synchronized(this) {
                instance ?: Room
                    .databaseBuilder(context, StreamingYorkieDB::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_3, MIGRATION_3_4)
                    .build().also { instance = it }
            }
        }

        //Migration 2 was skipped as it was broken. Fixed in Migration 3
        val MIGRATION_1_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `lurk` (`channelName` TEXT NOT NULL, `channelId` INTEGER NOT NULL, `broadcastId` TEXT, `logo` TEXT, `html` TEXT, `channelInformedOfLurk` INTEGER NOT NULL, `channelIsToBeLurked` INTEGER NOT NULL, PRIMARY KEY(`channelName`))")
            }
        }
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `following` ADD COLUMN `excludeUntil` INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
