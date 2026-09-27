package com.example.data.repository

import com.example.data.local.OtoKlonDao
import com.example.data.model.ActivityNotification
import com.example.data.model.CloneTask
import com.example.data.model.ConnectedAccounts
import com.example.data.model.InstagramTarget
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

class OtoKlonRepository(private val dao: OtoKlonDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val allTargets: Flow<List<InstagramTarget>> = dao.getAllTargets()
    val allCloneTasks: Flow<List<CloneTask>> = dao.getAllCloneTasks()
    val topCloneTasks: Flow<List<CloneTask>> = dao.getTopCloneTasks()
    val allNotifications: Flow<List<ActivityNotification>> = dao.getAllNotifications()
    val connectedAccounts: Flow<ConnectedAccounts?> = dao.getConnectedAccounts()

    suspend fun saveUserProfile(profile: UserProfile) {
        dao.saveUserProfile(profile)
    }

    suspend fun addInstagramTarget(username: String, displayName: String): Long {
        val target = InstagramTarget(
            username = if (username.startsWith("@")) username else "@$username",
            displayName = displayName.ifBlank { username },
            isMonitoring = true,
            lastVideoDetected = "İzleme başlatıldı",
            lastCheckedTimestamp = System.currentTimeMillis()
        )
        return dao.insertTarget(target)
    }

    suspend fun updateTarget(target: InstagramTarget) {
        dao.updateTarget(target)
    }

    suspend fun removeTarget(target: InstagramTarget) {
        dao.deleteTarget(target)
    }

    suspend fun saveCloneTask(task: CloneTask): Long {
        return dao.insertCloneTask(task)
    }

    suspend fun updateCloneTask(task: CloneTask) {
        dao.updateCloneTask(task)
    }

    suspend fun clearAllCloneTasks() {
        dao.clearAllCloneTasks()
    }

    suspend fun clearAllTargets() {
        dao.clearAllTargets()
    }

    suspend fun addNotification(notification: ActivityNotification): Long {
        return dao.insertNotification(notification)
    }

    suspend fun clearNotifications() {
        dao.clearAllNotifications()
    }

    suspend fun saveConnectedAccounts(accounts: ConnectedAccounts) {
        dao.saveConnectedAccounts(accounts)
    }
}
