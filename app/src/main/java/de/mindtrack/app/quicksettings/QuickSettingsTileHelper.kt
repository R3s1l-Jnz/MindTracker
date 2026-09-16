package de.mindtrack.app.quicksettings

import android.app.Activity
import android.app.StatusBarManager
import android.content.ComponentName
import android.graphics.drawable.Icon
import android.os.Build
import de.mindtrack.app.R

object QuickSettingsTileHelper {
    fun canRequestAddTile(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun requestAddTile(
        activity: Activity,
        onResult: (String) -> Unit
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onResult("Öffne die Schnelleinstellungen, tippe auf Bearbeiten und füge „MindTrack Check-in“ hinzu.")
            return
        }

        val statusBarManager = activity.getSystemService(StatusBarManager::class.java)
        if (statusBarManager == null) {
            onResult("Die Schnelleinstellungen konnten auf diesem Gerät nicht geöffnet werden.")
            return
        }

        statusBarManager.requestAddTileService(
            ComponentName(activity, CheckInTileService::class.java),
            activity.getString(R.string.quick_tile_label),
            Icon.createWithResource(activity, R.drawable.ic_quick_capture),
            activity.mainExecutor
        ) { result ->
            val message = when (result) {
                StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED -> "✓ Check-in-Kachel hinzugefügt."
                StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED -> "Die Check-in-Kachel ist bereits hinzugefügt."
                StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_NOT_ADDED -> "Kachel wurde nicht hinzugefügt."
                else -> "Kachel konnte nicht hinzugefügt werden (Code $result)."
            }
            onResult(message)
        }
    }
}
