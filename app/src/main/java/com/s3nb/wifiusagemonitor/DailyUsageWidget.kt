package com.s3nb.wifiusagemonitor

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi

class DailyUsageWidget : AppWidgetProvider() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        updateAppWidget(context, appWidgetManager, appWidgetIds)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent!!.action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) || intent.action
                .equals(Intent.ACTION_SCREEN_ON)
        ) {
            val extras = intent.extras
            if (extras != null) {
                Toast.makeText(context, "Updating...", Toast.LENGTH_SHORT).show()
                if (context != null) {
                    updateAppWidget(
                        context,
                        AppWidgetManager.getInstance(context),
                        extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray?
    ) {
        val remoteV = RemoteViews(context.packageName, R.layout.daily_usage_widget)
        val intentSync = Intent(context, DailyUsageWidget::class.java)
        intentSync.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        intentSync.action =
            AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val pendingSync = PendingIntent.getBroadcast(
            context,
            0,
            intentSync,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        remoteV.setOnClickPendingIntent(R.id.appwidget_image, pendingSync)
        remoteV.setTextViewText(
            R.id.appwidget_text,
            DataProvider.getDayUsage(context)
        )
        appWidgetManager.updateAppWidget(
            ComponentName(context, DailyUsageWidget::class.java),
            remoteV
        )
    }
}

