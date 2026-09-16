package de.mindtrack.app.quicksettings

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import de.mindtrack.app.R
import de.mindtrack.app.quickcapture.QuickCaptureActivity

class CheckInTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        qsTile?.apply {
            label = getString(R.string.quick_tile_label)
            state = Tile.STATE_ACTIVE
            updateTile()
        }
    }

    override fun onClick() {
        super.onClick()

        val intent = Intent(this, QuickCaptureActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }

        // The quick-capture activity contains no history or private summaries and is
        // deliberately safe to show above the lock screen.
        if (isLocked) {
            startActivity(intent)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pendingIntent = PendingIntent.getActivity(
                this,
                1001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
