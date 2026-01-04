package tees.habittracker.vishnus3358684.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

object NotificationHelper {
    fun createHabitChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                HabitNotificationScheduler.CHANNEL_ID,
                "Habit Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}


object HabitNotificationScheduler {

    const val CHANNEL_ID = "habit_channel"

    fun cancelHabitReminder(context: Context, habitId: Int) {
        val intent = Intent(context, HabitReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        pendingIntent?.let {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            alarmManager.cancel(it)
        }
    }


    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            alarmManager.canScheduleExactAlarms()
        } else true
    }

    fun scheduleHabitNotification(
        context: Context,
        habitId: Int,
        title: String,
        description: String,
        reminderTime: String,
        frequency: String
    ): Boolean {

        Log.e("Test","Set at - $reminderTime")

        if (!canScheduleExactAlarms(context)) {
            Toast.makeText(context, "Enable 'Alarms & reminders' permission.", Toast.LENGTH_LONG).show()
            return false
        }

        val (hour, minute) = parseTime(reminderTime) ?: run {
            Toast.makeText(context, "Invalid time format.", Toast.LENGTH_SHORT).show()
            return false
        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, HabitReminderReceiver::class.java).apply {
            action = "HABIT_REMINDER"
            putExtra("habitId", habitId)
            putExtra("title", title)
            putExtra("description", description)
            putExtra("frequency", frequency)
            putExtra("reminderTime", reminderTime)
        }

        val requestCode = habitId

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(AlarmManager::class.java)
            ?: run {
                Toast.makeText(context, "Unable to access AlarmManager.", Toast.LENGTH_SHORT).show()
                return false
            }

        try {
            when (frequency) {
                "Daily" -> {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                }

                "Weekly" -> {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingIntent
                    )
                }

                else -> {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to schedule reminder: ${e.message}", Toast.LENGTH_LONG).show()
            return false
        }

        val createdPending = PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, HabitReminderReceiver::class.java).apply { action = "HABIT_REMINDER" },
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        return if (createdPending != null) {
            Toast.makeText(context, "Habit reminder set!", Toast.LENGTH_SHORT).show()
            true
        } else {
            Toast.makeText(context, "Failed to set reminder.", Toast.LENGTH_LONG).show()
            false
        }
    }


    private fun parseTime(timeV: String): Pair<Int, Int>? {
        return try {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = sdf.parse(timeV) ?: return null
            val c = Calendar.getInstance().apply { time = date }
            c.get(Calendar.HOUR_OF_DAY) to c.get(Calendar.MINUTE)
        } catch (e: Exception) {
            null
        }
    }
}
