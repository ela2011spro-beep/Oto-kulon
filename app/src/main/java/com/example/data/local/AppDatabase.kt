package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ActivityNotification
import com.example.data.model.CloneTask
import com.example.data.model.ConnectedAccounts
import com.example.data.model.InstagramTarget
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        InstagramTarget::class,
        CloneTask::class,
        ActivityNotification::class,
        ConnectedAccounts::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun otoKlonDao(): OtoKlonDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "otoklon_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.otoKlonDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: OtoKlonDao) {
            dao.saveUserProfile(
                UserProfile(
                    id = 1,
                    channelName = "Kanalım",
                    toneStyle = "Merak uyandırıcı, doğrudan soruyla başlayan, samimi ve akıcı bir dil. İzleyiciyi ilk 2 saniyede yakalayan kurgu.",
                    voiceTone = "Erkek - Tok & Güçlü (Can)",
                    stripPosition = "TOP",
                    stripOpacity = 0.95f,
                    stripTextColor = "#FFEB3B",
                    pinCode = "1234",
                    isLockEnabled = true,
                    isBiometricEnabled = false
                )
            )

            dao.saveConnectedAccounts(
                ConnectedAccounts(
                    id = 1,
                    instagramConnected = false,
                    instagramUsername = "",
                    youtubeConnected = false,
                    youtubeChannel = "",
                    autoSyncActive = false,
                    pollIntervalMinutes = 5,
                    uploadPrivacy = "PUBLIC"
                )
            )

            // Database starts completely clean with 0 fake tasks and 0 fake targets
            dao.insertNotification(
                ActivityNotification(
                    title = "Oto Klon Kuruldu",
                    message = "Sistem hazır. 'Yapım' sekmesinden bir hedef ekleyip ilk videonuzu klonlayabilirsiniz.",
                    stepType = "SYSTEM",
                    timestamp = System.currentTimeMillis(),
                    isSuccess = true
                )
            )
        }
    }
}
