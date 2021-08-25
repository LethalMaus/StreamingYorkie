package com.lethalmaus.streaming_yorkie.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channel")
class ChannelEntity (

    @field:PrimaryKey var id: Int,
    @ColumnInfo(name = "display_name")
    var displayName: String?,
    var logo: String?,
    var game: String?,
    @ColumnInfo(name = "created_at")
    var createdAt: String?,
    var views: Int,
    var followers: Int,
    var status: String?,
    var description: String?,
    var broadcasterType: String?
)