package nirav.samir.esha.project.waterReminder

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_user_information.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class UserInformationActivity : AppCompatActivity() {

    private var bodyWeight: String = ""
    private var totalWorkTime: String = ""
    private var userWakeUpTime: Long = 0
    private var userSleepingTime: Long = 0
    private lateinit var myPrefs: SharedPreferences
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isClock24Hours = DateFormat.is24HourFormat(this.applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setContentView(R.layout.activity_user_information)

        myPrefs = getSharedPreferences(Utils.USERS_SHARED_DATA_PREF, Utils.PRIVATE_MODE)

        userWakeUpTime = myPrefs.getLong(Utils.WAKEUP_TIME_PREFS, 1558323000000)
        userSleepingTime = myPrefs.getLong(Utils.SLEEPING_TIME_PREFS, 1558369800000)

        wakeUpTimeTextField.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = userWakeUpTime

            val mTimePicker = TimePickerDialog(
                this,
                { timePickerDialog, hour, minutes ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, hour)
                    time.set(Calendar.MINUTE, minutes)
                    userWakeUpTime = time.timeInMillis

                    wakeUpTimeTextField.setText(
                        String.format("%02d:%02d", hour, minutes)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), isClock24Hours
            )
            mTimePicker.setTitle("Select Your Wakeup Time")
            mTimePicker.show()
        }


        sleepTimeTextField.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = userSleepingTime

            val mTimePicker: TimePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { timePickerDialog, hour, minutes ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, hour)
                    time.set(Calendar.MINUTE, minutes)
                    userSleepingTime = time.timeInMillis

                    sleepTimeTextField.setText(
                        String.format("%02d:%02d", hour, minutes)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), isClock24Hours
            )
            mTimePicker.setTitle("Select Your Sleeping Time")
            mTimePicker.show()
        }

        letsStartBtn.setOnClickListener {

            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(init_user_info_parent_layout.windowToken, 0)

            bodyWeight = weightTextField.text.toString()
            totalWorkTime = workTimeTextField.text.toString()

            when {
                TextUtils.isEmpty(bodyWeight) -> Toast.makeText(this, "Please enter body weight", Toast.LENGTH_SHORT).show()
                bodyWeight.toInt() > 200 || bodyWeight.toInt() < 20 -> Toast.makeText(this, "Please enter a valid body weight", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(totalWorkTime) -> Toast.makeText(this, "Please enter your workout time", Toast.LENGTH_SHORT).show()
                totalWorkTime.toInt() > 500 || totalWorkTime.toInt() < 0 -> Toast.makeText(this, "Please enter a valid workout time", Toast.LENGTH_SHORT).show()
                else -> {

                    val editor = myPrefs.edit()
                    editor.putInt(Utils.WEIGHT_PREFS, bodyWeight.toInt())
                    editor.putInt(Utils.WORK_TIME_PREFS, totalWorkTime.toInt())
                    editor.putLong(Utils.WAKEUP_TIME_PREFS, userWakeUpTime)
                    editor.putLong(Utils.SLEEPING_TIME_PREFS, userSleepingTime)
                    editor.putBoolean(Utils.FIRST_RUN_PREFS, false)

                    val totalWaterIntake = Utils.calculateWaterIntake(bodyWeight.toInt(), totalWorkTime.toInt())
                    val df = DecimalFormat("#")
                    df.roundingMode = RoundingMode.CEILING
                    editor.putInt(Utils.TOTAL_INTAKE, df.format(totalWaterIntake).toInt())

                    editor.apply()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()

                }
            }
        }

    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 1000)
    }
}
