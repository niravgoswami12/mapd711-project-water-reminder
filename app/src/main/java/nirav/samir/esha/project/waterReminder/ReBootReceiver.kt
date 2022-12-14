package nirav.samir.esha.project.waterReminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class ReBootReceiver : BroadcastReceiver() {
    private val alarmUtils = AlarmUtils()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.action != null) {
            if (intent.action == "android.intent.action.BOOT_COMPLETED") {
                val prefs = context!!.getSharedPreferences(Utils.USERS_SHARED_DATA_PREF,
                    AppCompatActivity.MODE_PRIVATE
                )
                val notificationFrequency = prefs.getInt(Utils.NOTIFICATION_FREQUENCY_PREFS, 60)
                val notificationsNewMessage = prefs.getBoolean("notifications_new_message", true)
                alarmUtils.cancelAlarm(context)
                if (notificationsNewMessage) {
                    alarmUtils.setRepeatingAlarm(context, notificationFrequency.toLong())
                }
            }
        }
    }
}
