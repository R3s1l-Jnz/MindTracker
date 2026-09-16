package de.mindtrack.app.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build

object MindTrackWidgetHelper {
    fun requestPin(activity: Activity): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return "Öffne die Widget-Auswahl deines Launchers und füge MindTrack hinzu."
        }

        val manager = AppWidgetManager.getInstance(activity)
        if (!manager.isRequestPinAppWidgetSupported) {
            return "Dein Launcher unterstützt das direkte Anheften nicht. Öffne die Widget-Auswahl und füge MindTrack dort hinzu."
        }

        val accepted = manager.requestPinAppWidget(
            ComponentName(activity, MindTrackWidgetProvider::class.java),
            null,
            null
        )
        return if (accepted) {
            "Widget-Anfrage geöffnet."
        } else {
            "Widget konnte nicht automatisch angeheftet werden. Öffne die Widget-Auswahl deines Launchers."
        }
    }
}
