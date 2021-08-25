package com.lethalmaus.streaming_yorkie.database.entity

open class UserEntity (

    open var id: Int,
    open var displayName: String?,
    open var logo: String?,
    open var createdAt: String?,
    open var notifications: Boolean,
    open var lastUpdated: Long,
    var status: String? = null
)