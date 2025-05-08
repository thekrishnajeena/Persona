package com.krishnajeena.persona.other

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.krishnajeena.persona.R
import com.krishnajeena.persona.data_layer.DailyQuote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

object NotificationHandler {

    private const val CHANNEL_ID = "channel_id"
    private const val CHANNEL_NAME = "Daily Quote Notifications"

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun createQuoteNotification(context: Context, quote: DailyQuote) {
        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.log__1_) // Replace with your icon
            .setContentTitle("Quote of the day")
            .setContentText(quote.text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(quote.text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(1001, builder.build())
        scheduleDailyAlarm(context)
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for daily inspirational quotes"
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun scheduleDailyAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, DailyQuoteReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 6) // 9 AM
            set(Calendar.MINUTE, 2)
            set(Calendar.SECOND, 0)
        }

        // If the time has already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

    }

}


class DailyQuoteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Launch coroutine to fetch data from repo and show notification
        CoroutineScope(Dispatchers.IO).launch {
            val repository = QuoteRepository(context) // or inject via Hilt if using DI
            val quote = repository.fetchQuote()


            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) { Log.i("Permission::::::", "Given")
                NotificationHandler.createQuoteNotification(context, quote)

            }
//
        }
    }
}
