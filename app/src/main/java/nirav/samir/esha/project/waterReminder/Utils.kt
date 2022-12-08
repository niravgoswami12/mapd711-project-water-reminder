package nirav.samir.esha.project.waterReminder

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


class Utils {
    companion object {
        val LBS_IN_ONE_KG = 2.205
        val ML_IN_ONE_Oz = 29.574
        // Formula Ref. https://www.umsystem.edu/totalrewards/wellness/how-to-calculate-how-much-water-you-should-drink
        fun calculateWaterIntake(weight: Int, totalWorkTime: Int): Double {
            val weightInLbs = weight * LBS_IN_ONE_KG
            val intakeInOunce = (weightInLbs * 0.5) + ((totalWorkTime/30)*12)
            return intakeInOunce * ML_IN_ONE_Oz
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