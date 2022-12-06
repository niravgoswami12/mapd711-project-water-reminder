package nirav.samir.esha.project.waterReminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import java.util.concurrent.TimeUnit

class AlarmUtils {
    private var alarmManager: AlarmManager? = null

    private val ACTION_BD_NOTIFICATION = "water.NOTIFICATION"

    fun setRepeatingAlarm(context: Context, notificationFrequency: Long) {
        val notificationFrequencyMs = TimeUnit.MINUTES.toMillis(notificationFrequency)

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, NotificationReceiver::class.java)
        alarmIntent.action = ACTION_BD_NOTIFICATION

        val pendingAlarmIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        alarmManager!!.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            notificationFrequencyMs,
            pendingAlarmIntent
        )

        /* Restart if rebooted */
        val receiver = ComponentName(context, ReBootReceiver::class.java)
        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun cancelAlarm(context: Context) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, NotificationReceiver::class.java)
        alarmIntent.action = ACTION_BD_NOTIFICATION

        val pendingAlarmIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager!!.cancel(pendingAlarmIntent)

        /* Alarm won't start again if device is rebooted */
        val receiver = ComponentName(context, ReBootReceiver::class.java)
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun checkAlarm(context: Context): Boolean {

        val alarmIntent = Intent(context, NotificationReceiver::class.java)
        alarmIntent.action = ACTION_BD_NOTIFICATION

        return PendingIntent.getBroadcast(
            context, 0,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE
        ) != null
    }
}