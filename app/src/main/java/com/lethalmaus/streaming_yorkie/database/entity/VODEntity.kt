package com.lethalmaus.streaming_yorkie.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vod")
class VODEntity (

    @field:PrimaryKey
    var id: Int,
    var title: String?,
    var url: String?,
    @ColumnInfo(name = "created_at")
    var createdAt: String?,
    var description: String?,
    @ColumnInfo(name = "tag_list")
    var tagList: String?,
    var game: String?,
    var length: String?,
    var preview: String?,
    var exported: Boolean = false,
    var excluded: Boolean = false,
    @ColumnInfo(name = "last_updated")
    var lastUpdated: Long
)