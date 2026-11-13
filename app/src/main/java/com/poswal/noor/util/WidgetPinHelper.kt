package com.poswal.noor.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.poswal.noor.widget.NoorWidgetReceiver

object WidgetPinHelper {
    fun pinWidget(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
            val myProvider = ComponentName(context, NoorWidgetReceiver::class.java)
            if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
                val successCallback = PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(context, NoorWidgetReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
                return
            }
        }
        Toast.makeText(
            context,
            "Long-press your phone's home screen, tap 'Widgets', and select 'Poswal Noor'",
            Toast.LENGTH_LONG
        ).show()
    }
}
