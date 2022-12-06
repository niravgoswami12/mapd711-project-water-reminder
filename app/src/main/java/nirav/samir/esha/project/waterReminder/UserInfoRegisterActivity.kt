package nirav.samir.esha.project.waterReminder

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_user_info_register.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class UserInfoRegisterActivity : AppCompatActivity() {

    private var userName: String = ""
    private var bodyWeight: String = ""
    private var workoutTime: String = ""
    private var wakeUpTime: Long = 0
    private var sleepTime: Long = 0
    private lateinit var myPrefs: SharedPreferences
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isClock24Hours = DateFormat.is24HourFormat(this.applicationContext)

        setContentView(R.layout.activity_user_info_register)

        myPrefs = getSharedPreferences(Utils.USERS_SHARED_DATA_PREF, MODE_PRIVATE)

        wakeUpTime = myPrefs.getLong(Utils.WAKEUP_TIME_PREFS, Utils.DEFAULT_WAKEUP_TIME)
        sleepTime = myPrefs.getLong(Utils.SLEEPING_TIME_PREFS, Utils.DEFAULT_SLEEP_TIME)

        wakeUpTimeTextField.editText!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = wakeUpTime

            val mTimePicker = TimePickerDialog(
                this,
                { timePickerDialog, hour, minutes ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, hour)
                    time.set(Calendar.MINUTE, minutes)
                    wakeUpTime = time.timeInMillis

                    wakeUpTimeTextField.editText!!.setText(String.format("%02d:%02d", hour, minutes))
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), isClock24Hours
            )
            mTimePicker.show()
        }

        sleepTimeTextField.editText!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = sleepTime

            val mTimePicker = TimePickerDialog(
                this,
                { timePickerDialog, hour, minutes ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, hour)
                    time.set(Calendar.MINUTE, minutes)
                    sleepTime = time.timeInMillis

                    sleepTimeTextField.editText!!.setText(String.format("%02d:%02d", hour, minutes))
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), isClock24Hours
            )
            mTimePicker.show()
        }

        letsStartBtn.setOnClickListener {

            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(init_user_info_parent_layout.windowToken, 0)

            userName = userNameTextField.editText!!.text.toString()
            bodyWeight = weightTextField.editText!!.text.toString()
            workoutTime = workTimeTextField.editText!!.text.toString()

            when {
                TextUtils.isEmpty(userName) -> Toast.makeText(this, getString(R.string.user_name_error_msg), Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(bodyWeight) -> Toast.makeText(this, getString(R.string.weight_error_msg), Toast.LENGTH_SHORT).show()
                bodyWeight.toInt() > 200 || bodyWeight.toInt() < 20 -> Toast.makeText(this, getString(
                                    R.string.invalid_weight_error_msg), Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(workoutTime) -> Toast.makeText(this, getString(R.string.workout_time_error_msg), Toast.LENGTH_SHORT).show()
                workoutTime.toInt() > 500 || workoutTime.toInt() < 0 -> Toast.makeText(this, getString(
                                    R.string.invalid_workout_time_error_msg), Toast.LENGTH_SHORT).show()
                else -> {

                    val editor = myPrefs.edit()
                    editor.putString(Utils.USER_NAME, userName)
                    editor.putInt(Utils.WEIGHT_PREFS, bodyWeight.toInt())
                    editor.putInt(Utils.WORK_TIME_PREFS, workoutTime.toInt())
                    editor.putLong(Utils.WAKEUP_TIME_PREFS, wakeUpTime)
                    editor.putLong(Utils.SLEEPING_TIME_PREFS, sleepTime)
                    editor.putBoolean(Utils.FIRST_RUN_PREFS, false)

                    val totalWaterIntake = Utils.calculateWaterIntake(bodyWeight.toInt(), workoutTime.toInt())
                    val df = DecimalFormat("#")
                    df.roundingMode = RoundingMode.CEILING
                    editor.putInt(Utils.TOTAL_INTAKE, df.format(totalWaterIntake).toInt())

                    editor.apply()
                    startActivity(Intent(this, HomeActivity::class.java))
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
        Toast.makeText(this, getString(R.string.click_back), Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 1000)
    }
}
