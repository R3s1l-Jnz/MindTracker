package de.mindtrack.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import de.mindtrack.app.R
import de.mindtrack.app.data.TrackingDimension
import de.mindtrack.app.quickcapture.QuickCaptureActivity

class MindTrackWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            appWidgetManager.updateAppWidget(appWidgetId, buildRemoteViews(context, appWidgetId))
        }
    }

    private fun buildRemoteViews(context: Context, appWidgetId: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.mindtrack_widget)

        views.setOnClickPendingIntent(
            R.id.widget_energy_label,
            quickCaptureIntent(context, appWidgetId, 1)
        )
        views.setOnClickPendingIntent(
            R.id.widget_tension_label,
            quickCaptureIntent(context, appWidgetId, 2)
        )
        views.setOnClickPendingIntent(
            R.id.widget_open,
            quickCaptureIntent(context, appWidgetId, 3)
        )

        bindLevelRow(
            views = views,
            context = context,
            appWidgetId = appWidgetId,
            dimension = TrackingDimension.ENERGY,
            ids = intArrayOf(
                R.id.widget_energy_b,
                R.id.widget_energy_1,
                R.id.widget_energy_2,
                R.id.widget_energy_3,
                R.id.widget_energy_4,
                R.id.widget_energy_5
            ),
            requestBase = 100
        )
        bindLevelRow(
            views = views,
            context = context,
            appWidgetId = appWidgetId,
            dimension = TrackingDimension.TENSION,
            ids = intArrayOf(
                R.id.widget_tension_b,
                R.id.widget_tension_1,
                R.id.widget_tension_2,
                R.id.widget_tension_3,
                R.id.widget_tension_4,
                R.id.widget_tension_5
            ),
            requestBase = 200
        )

        views.setOnClickPendingIntent(
            R.id.widget_energy_plus,
            presetIntent(context, appWidgetId, 301, TrackingDimension.ENERGY, QuickCaptureActivity.EVENT_INCREASE)
        )
        views.setOnClickPendingIntent(
            R.id.widget_energy_minus,
            presetIntent(context, appWidgetId, 302, TrackingDimension.ENERGY, QuickCaptureActivity.EVENT_DECREASE)
        )
        views.setOnClickPendingIntent(
            R.id.widget_tension_plus,
            presetIntent(context, appWidgetId, 303, TrackingDimension.TENSION, QuickCaptureActivity.EVENT_INCREASE)
        )
        views.setOnClickPendingIntent(
            R.id.widget_tension_minus,
            presetIntent(context, appWidgetId, 304, TrackingDimension.TENSION, QuickCaptureActivity.EVENT_DECREASE)
        )

        return views
    }

    private fun bindLevelRow(
        views: RemoteViews,
        context: Context,
        appWidgetId: Int,
        dimension: TrackingDimension,
        ids: IntArray,
        requestBase: Int
    ) {
        ids.forEachIndexed { index, viewId ->
            val pendingIntent = if (index == 0) {
                presetIntent(
                    context,
                    appWidgetId,
                    requestBase,
                    dimension,
                    QuickCaptureActivity.EVENT_BREAKDOWN
                )
            } else {
                presetIntent(
                    context,
                    appWidgetId,
                    requestBase + index,
                    dimension,
                    QuickCaptureActivity.EVENT_LEVEL,
                    index
                )
            }
            views.setOnClickPendingIntent(viewId, pendingIntent)
        }
    }

    private fun quickCaptureIntent(context: Context, appWidgetId: Int, requestCode: Int): PendingIntent {
        val intent = Intent(context, QuickCaptureActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        return PendingIntent.getActivity(
            context,
            appWidgetId * 1000 + requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun presetIntent(
        context: Context,
        appWidgetId: Int,
        requestCode: Int,
        dimension: TrackingDimension,
        eventType: String,
        level: Int? = null
    ): PendingIntent {
        val intent = Intent(context, QuickCaptureActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(QuickCaptureActivity.EXTRA_DIMENSION, dimension.name)
            putExtra(QuickCaptureActivity.EXTRA_EVENT_TYPE, eventType)
            level?.let { putExtra(QuickCaptureActivity.EXTRA_LEVEL, it) }
        }
        return PendingIntent.getActivity(
            context,
            appWidgetId * 1000 + requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
