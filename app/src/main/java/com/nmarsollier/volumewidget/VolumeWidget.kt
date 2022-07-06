package com.nmarsollier.volumewidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.view.View
import android.widget.RemoteViews


const val VOLUME_DOWN = "VOLUME_DOWN"
const val VOLUME_UP = "VOLUME_UP"
const val VOLUME_OFF = "VOLUME_OFF"
const val VOLUME_ON = "VOLUME_ON"

/**
 * Implementation of App Widget functionality.
 */
class VolumeWidget : AppWidgetProvider() {
    var ids: IntArray? = null
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        ids = appWidgetIds
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent) {
        super.onReceive(context, intent)
        context ?: return

        when (intent.action) {
            VOLUME_DOWN -> volumeDown(context)
            VOLUME_UP -> volumeUp(context)
            VOLUME_OFF -> volumeOff(context)
            VOLUME_ON -> volumeOn(context)
        }
    }

    private fun volumeUp(context: Context) {
        val audioMamager =
            context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioMamager.adjustVolume(AudioManager.ADJUST_RAISE, 0)
    }

    private fun volumeDown(context: Context) {
        val audioMamager =
            context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioMamager.adjustVolume(AudioManager.ADJUST_LOWER, 0)
    }

    private fun volumeOff(context: Context) {
        val audioMamager =
            context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioMamager.adjustVolume(AudioManager.ADJUST_MUTE, 0)
        update(context)
    }

    private fun volumeOn(context: Context) {
        val audioMamager =
            context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioMamager.adjustVolume(AudioManager.ADJUST_UNMUTE, 0)
        update(context)
    }

    private fun isMute(context: Context): Boolean {
        val audioMamager =
            context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        return audioMamager.isStreamMute(AudioManager.STREAM_MUSIC)
    }


    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.volume_widget)
        views.setOnClickPendingIntent(
            R.id.volume_down,
            getPendingSelfIntent(context, VOLUME_DOWN)
        )
        views.setOnClickPendingIntent(
            R.id.volume_up,
            getPendingSelfIntent(context, VOLUME_UP)
        )
        views.setOnClickPendingIntent(
            R.id.volume_off,
            getPendingSelfIntent(context, VOLUME_OFF)
        )
        views.setOnClickPendingIntent(
            R.id.volume_on,
            getPendingSelfIntent(context, VOLUME_ON)
        )

        if (isMute(context)) {
            views.setViewVisibility(R.id.volume_on, View.VISIBLE)
            views.setViewVisibility(R.id.volume_off, View.GONE)
        } else {
            views.setViewVisibility(R.id.volume_on, View.GONE)
            views.setViewVisibility(R.id.volume_off, View.VISIBLE)
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getPendingSelfIntent(context: Context?, action: String?): PendingIntent? {
        val intent = Intent(context, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    fun update(context: Context) {
        val widgetManager = AppWidgetManager.getInstance(context)
        val widgetComponent = ComponentName(context, javaClass)
        val widgetIds = widgetManager.getAppWidgetIds(widgetComponent)

        val intent = Intent(context, javaClass)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        context.sendBroadcast(intent)
    }
}

