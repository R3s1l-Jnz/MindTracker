package de.mindtrack.app

import android.app.Application
import de.mindtrack.app.data.AppDatabase
import de.mindtrack.app.data.TrackingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MindTrackApplication : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AppDatabase by lazy { AppDatabase.create(this) }
    val repository: TrackingRepository by lazy {
        TrackingRepository(
            trackingDao = database.trackingEventDao(),
            skillDao = database.skillDao(),
            skillUsageDao = database.skillUsageDao(),
            discoveryDao = database.discoveryDao(),
            contextTagDao = database.contextTagDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        appScope.launch { repository.initializeDefaults() }
    }
}
