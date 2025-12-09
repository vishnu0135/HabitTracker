package tees.habittracker.vishnus3358684.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import tees.habittracker.vishnus3358684.R

class HabitReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        val title = intent?.getStringExtra("title") ?: "Habit Reminder"
        val description = intent?.getStringExtra("description") ?: "Time to work on your habit!"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, HabitNotificationScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_habit_tracker_app) // change icon
            .setContentTitle(title)
            .setContentText(description)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
