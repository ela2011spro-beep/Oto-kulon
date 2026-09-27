package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ActivityNotification
import com.example.data.model.CloneTask
import com.example.data.model.ConnectedAccounts
import com.example.data.model.InstagramTarget
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface OtoKlonDao {

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    // Instagram Targets
    @Query("SELECT * FROM instagram_targets ORDER BY id DESC")
    fun getAllTargets(): Flow<List<InstagramTarget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarget(target: InstagramTarget): Long

    @Update
    suspend fun updateTarget(target: InstagramTarget)

    @Delete
    suspend fun deleteTarget(target: InstagramTarget)

    @Query("DELETE FROM instagram_targets WHERE id = :id")
    suspend fun deleteTargetById(id: Long)

    // Clone Tasks
    @Query("SELECT * FROM clone_tasks ORDER BY createdAt DESC")
    fun getAllCloneTasks(): Flow<List<CloneTask>>

    @Query("SELECT * FROM clone_tasks ORDER BY estimatedViews DESC")
    fun getTopCloneTasks(): Flow<List<CloneTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloneTask(task: CloneTask): Long

    @Update
    suspend fun updateCloneTask(task: CloneTask)

    @Query("DELETE FROM clone_tasks WHERE id = :id")
    suspend fun deleteCloneTask(id: Long)

    @Query("DELETE FROM clone_tasks")
    suspend fun clearAllCloneTasks()

    @Query("DELETE FROM instagram_targets")
    suspend fun clearAllTargets()

    // Activity Notifications
    @Query("SELECT * FROM activity_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<ActivityNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: ActivityNotification): Long

    @Query("DELETE FROM activity_notifications")
    suspend fun clearAllNotifications()

    // Connected Accounts
    @Query("SELECT * FROM connected_accounts WHERE id = 1")
    fun getConnectedAccounts(): Flow<ConnectedAccounts?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConnectedAccounts(accounts: ConnectedAccounts)
}
