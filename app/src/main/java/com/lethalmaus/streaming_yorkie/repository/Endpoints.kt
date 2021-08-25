package com.lethalmaus.streaming_yorkie.repository

const val twitchAuthValidate = "validate"
const val twitchAuthRevoke = "revoke"

const val gql = "/gql"
const val hls = "/api/channel/hls/{channel}.m3u8"

const val krakenUser = "/kraken/user"
const val krakenChannel = "/kraken/channel"
const val krakenDevUser = "/kraken/users/188850000"
const val krakenFollowers = "/kraken/channels/{userId}/follows"
const val krakenFollowing = "/kraken/users/userId/follows/channels"
const val krakenVideos = "/kraken/channels/{userId}/videos"
const val krakenVideoExport = "/kraken/videos/{vodId}/youtube_export"

const val helixFollows = "/helix/users/follows"
const val helixStreams = "/helix/streams"

const val discordPurchase = "aHR0cHM6Ly9kaXNjb3JkYXBwLmNvbS9hcGkvd2ViaG9va3MvNzAxNDAzNDEwNTQ4Nzg1MjMzLzl0eFRvM3VkMUQ5WVJ1WDA5N3hBS1daU3UyQWMxNk1Wd0VsN2F2R0JZOGFnU3JSTDN2VkRySDBoZ2hVaGw3ejhIbzdh"
const val discordF4F = "aHR0cHM6Ly9kaXNjb3JkYXBwLmNvbS9hcGkvd2ViaG9va3MvNjA3Mjc1MjA3OTU4MzMxNDYyLzJYWUNpS3BVMWhiWDN0Z0dwUUM1bktOM2VFTUlELWlpbHdnbGU4bGUxRUIwbmhzVXpXX2NkbUlRLTRGNmFvNVVRZ2xF"

enum class Endpoint {
    KRAKEN_USER
}

