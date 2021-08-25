package com.lethalmaus.streaming_yorkie.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lurk")
class LurkEntity (

    @field:PrimaryKey
    val channelName: String,
    var channelId: Int,
    var broadcastId: String?,
    var logo: String?,
    var html: String?,
    var channelInformedOfLurk: Boolean,
    var channelIsToBeLurked: Boolean
)