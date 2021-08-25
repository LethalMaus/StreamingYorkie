package com.lethalmaus.streaming_yorkie.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "follower")
class FollowerEntity (
    @field:PrimaryKey
    override var id: Int,
    @ColumnInfo(name = "display_name")
    override var displayName: String?,
    override var logo: String? = null,
    @ColumnInfo(name = "created_at")
    override var createdAt: String?,
    override var notifications: Boolean,
    @ColumnInfo(name = "last_updated")
    override var lastUpdated: Long
) : UserEntity(id, displayName, logo, createdAt, notifications, lastUpdated)