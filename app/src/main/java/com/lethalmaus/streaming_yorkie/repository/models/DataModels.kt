package com.lethalmaus.streaming_yorkie.repository.models

import com.google.gson.annotations.SerializedName

data class TokenValidation (
    var token: String = "", //This will be injected
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("user_id")
    val userId: String
)

data class KrakenUser (
    @SerializedName("_id")
    val id: Int,
    val logo: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("created_at")
    val createdAt: String,
    val bio: String,
)

data class KrakenChannel (
    @SerializedName("display_name")
    val displayName: String,
    val logo: String,
    val game: String,
    @SerializedName("created_at")
    val createdAt: String,
    val views: Int,
    val followers: Int,
    val description: String,
    val broadcaster_type: String?
)

data class KrakenFollowers (
    @SerializedName("_cursor")
    val cursor: String?,
    val follows: ArrayList<KrakenFollows>,
    @SerializedName("_total")
    val total: Int
)

data class KrakenFollows (
    val user: KrakenUser,
    val notifications: Boolean
)

data class HelixFollows (
    val pagination: HelixPagination,
    val total: Int,
    val data: ArrayList<HelixFollowsData>
)

data class HelixPagination (
    val cursor: String?
)

data class HelixFollowsData (
    @SerializedName("from_id")
    val fromId: String,
    @SerializedName("from_name")
    val fromName: String,
    @SerializedName("followed_at")
    val followedAt: String
)

enum class TokenType {
    CLIENT,
    WEBSITE;
}