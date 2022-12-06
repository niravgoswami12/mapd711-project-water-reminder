package nirav.samir.esha.project.waterReminder

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


class Utils {
    companion object {
        fun calculateWaterIntake(weight: Int, totalWorkTime: Int): Double {
            return ((weight * 100 / 3.0) + (totalWorkTime / 6 * 7))
        }

        @SuppressLint("SimpleDateFormat")
        fun getCurrentDateHumanReadable(): String? {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MM-yyyy")
            return df.format(c)
        }

        val DEFAULT_WAKEUP_TIME = 1670239800000 // 6:30 AM
        val DEFAULT_SLEEP_TIME = 1670297400000 // 10:30 PM
        val USERS_SHARED_DATA_PREF = "drink_app_prefs"
        val USER_NAME = "username"
        val WEIGHT_PREFS = "weight"
        val WORK_TIME_PREFS = "worktime"
        val TOTAL_INTAKE = "totalintake"
        val NOTIFICATION_STATUS_PREFS = "notificationstatus"
        val NOTIFICATION_FREQUENCY_PREFS = "notificationfrequency"
        val NOTIFICATION_MSG_PREFS = "notificationmsg"
        val SLEEPING_TIME_PREFS = "sleepingtime"
        val WAKEUP_TIME_PREFS = "wakeuptimekey"
        val NOTIFICATION_TONE_URI_PREFS = "notificationtone"
        val FIRST_RUN_PREFS = "firstrunkey"
    }
}