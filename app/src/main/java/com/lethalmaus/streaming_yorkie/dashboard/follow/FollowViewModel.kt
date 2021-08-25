package com.lethalmaus.streaming_yorkie.dashboard.follow

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.lethalmaus.streaming_yorkie.common.*
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB
import com.lethalmaus.streaming_yorkie.database.entity.FollowerEntity
import com.lethalmaus.streaming_yorkie.repository.RequestManager
import com.lethalmaus.streaming_yorkie.repository.TwitchHelixService
import com.lethalmaus.streaming_yorkie.repository.models.HelixFollows
import com.lethalmaus.streaming_yorkie.repository.models.HelixFollowsData
import kotlinx.coroutines.launch

const val NEW = "NEW"
const val CURRENT = "CURRENT"
const val UNFOLLOWED = "UNFOLLOWED"

class FollowViewModel: BaseViewModel() {

    val errorLD = MutableLiveData<NetworkError>()
    val wrongCountLD = MutableLiveData<WrongCount>()
    val followerProgressLD = MutableLiveData<Int>()

    private lateinit var db: StreamingYorkieDB
    private var followerCount = 0

    fun getFollowers(activity: Activity) = launch {
        db = StreamingYorkieDB.getInstance(activity)
        followerCount = 0
        getFollowerRequest(activity, updatedAt = System.currentTimeMillis())
    }

    private suspend fun getFollowerRequest(activity: Activity, shouldUpdate: Boolean = false, cursor: String = "", updatedAt: Long) {
        val response = TwitchHelixService(activity).getFollowers(cursor)
        if (response is SuccessResponse) {
            handleSuccessResponse(activity, response, shouldUpdate, updatedAt)
        } else {
            emitLiveData(errorLD, response as NetworkError)
        }
    }

    private fun handleSuccessResponse(activity: Activity, response: SuccessResponse<HelixFollows?>, shouldUpdate: Boolean, updatedAt: Long) {
        val lastCount = RequestManager.getFollowersCount(activity)
        var needsUpdate = shouldUpdate
        if (!needsUpdate && lastCount == response.data?.total?: 0) {
            val lastUsers = db.followerDAO().getLastUsers()
            lastUsers?.forEachIndexed { index, lastUser ->
                if (response.data?.data?.size?: 0 > index && response.data?.data?.get(index)?.fromId != lastUser.toString()) {
                    needsUpdate = true
                }
            }
        }
        handleFollowerUpdate(activity, response, needsUpdate, updatedAt)
    }

    private fun handleFollowerUpdate(activity: Activity, response: SuccessResponse<HelixFollows?>, needsUpdate: Boolean, updatedAt: Long) {
        if (needsUpdate) {
            updateFollowers(response.data?.data, updatedAt)
            sendNextResponseOrFinish(activity, response, needsUpdate, updatedAt)
        } else {
            emitLiveData(followerProgressLD, 100)
        }
    }

    private fun updateFollowers(followers: List<HelixFollowsData>?, updatedAt: Long) {
        followers?.forEach { data ->
            followerCount++
            val userId = Integer.parseInt(data.fromId)
            val existingFollower = db.followerDAO().getUserById(userId)
            if (existingFollower != null) {
                existingFollower.displayName = data.fromName
                existingFollower.createdAt = data.followedAt
                existingFollower.lastUpdated = updatedAt
                if (existingFollower.status == NEW) {
                    existingFollower.status = CURRENT
                }
                db.followerDAO().insertUser(existingFollower)
            } else {
                val follower = FollowerEntity(Integer.parseInt(data.fromId), data.fromName, createdAt = data.followedAt, notifications = true, lastUpdated = updatedAt)
                follower.status = NEW
                db.followerDAO().insertUser(follower)
            }
        }
    }

    private fun sendNextResponseOrFinish(activity: Activity, response: SuccessResponse<HelixFollows?>, needsUpdate: Boolean, updatedAt: Long) {
        if (response.data?.data?.size?: 0 == 25 && followerCount < response.data?.total?: 0) {
            emitLiveData(followerProgressLD, 100.times(followerCount).div(response.data?.total?: 0).div(2))
            launch {
                getFollowerRequest(activity, needsUpdate, response.data?.pagination?.cursor?: "", updatedAt)
            }
        } else {
            handlePossibleWrongCount(response.data?.total?: 0)
            val unfollowed = db.followerDAO().getUnfollowedUsers(updatedAt)
            unfollowed?.forEach {
                it.status = UNFOLLOWED
                db.followerDAO().updateUser(it)
            }
            //TODO kraken get followers
        }
    }

    private fun handlePossibleWrongCount(expectedCount: Int) {
        if (followerCount < expectedCount) {
            emitLiveData(wrongCountLD, WrongCount(RequestType.FOLLOWERS, followerCount, expectedCount))
        }
    }

    data class WrongCount(
        val type: RequestType,
        val actualCount: Int,
        val expectedCount: Int?
    )

    enum class RequestType {
        FOLLOWERS
    }
}