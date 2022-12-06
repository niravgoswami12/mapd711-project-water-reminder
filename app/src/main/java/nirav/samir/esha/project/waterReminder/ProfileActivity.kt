package nirav.samir.esha.project.waterReminder

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_report.backBtn
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private var totalWorkTime: String = ""
    private lateinit var myPrefs: SharedPreferences

    private var userName: String = ""
    private var userWeight: String = ""
    private var customWaterTarget: String = ""
    private var userSleepingTime: Long = 0
    private var userWakeUpTime: Long = 0
    private var notificationMessage: String = ""
    private var currentRingtoneUri: String? = ""
    private var notificationFrequency: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    override fun onStart() {
        super.onStart()
        val isClock24Hours = android.text.format.DateFormat.is24HourFormat(this)

        myPrefs = getSharedPreferences(Utils.USERS_SHARED_DATA_PREF,
            AppCompatActivity.MODE_PRIVATE
        )
        backBtn.setOnClickListener {
            finish()
        }

        userNameTextField.editText!!.setText("" + myPrefs.getString(Utils.USER_NAME, ""))
        weightTextField.editText!!.setText("" + myPrefs.getInt(Utils.WEIGHT_PREFS, 0))
        workTimeTextField.editText!!.setText("" + myPrefs.getInt(Utils.WORK_TIME_PREFS, 0))
        targetTextField.editText!!.setText("" + myPrefs.getInt(Utils.TOTAL_INTAKE, 0))
        notificationTextTextField.editText!!.setText(
            myPrefs.getString(
                Utils.NOTIFICATION_MSG_PREFS,
                getString(R.string.notification_msg)
            )
        )
        currentRingtoneUri = myPrefs.getString(
            Utils.NOTIFICATION_TONE_URI_PREFS,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()
        )
        ringtoneTextField.editText!!.setText(
            RingtoneManager.getRingtone(
                this,
                Uri.parse(currentRingtoneUri)
            ).getTitle(this)
        )

        notificationIntervalRadioBtn.setOnClickedButtonListener { button, position ->
            notificationFrequency = when (position) {
                0 -> 30
                1 -> 45
                2 -> 60
                else -> 30
            }
        }
        notificationFrequency = myPrefs.getInt(Utils.NOTIFICATION_FREQUENCY_PREFS, 30)
        when (notificationFrequency) {
            30 -> notificationIntervalRadioBtn.position = 0
            45 -> notificationIntervalRadioBtn.position = 1
            60 -> notificationIntervalRadioBtn.position = 2
            else -> {
                notificationIntervalRadioBtn.position = 0
                notificationFrequency = 30
            }
        }

        ringtoneTextField.editText!!.setOnClickListener {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
            intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_TITLE,
                getString(R.string.select_rt)
            )
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentRingtoneUri)
            startActivityForResult(intent, 999)
        }

        userWakeUpTime = myPrefs.getLong(Utils.WAKEUP_TIME_PREFS, 1558323000000)
        userSleepingTime = myPrefs.getLong(Utils.SLEEPING_TIME_PREFS, 1558369800000)
        val cal = Calendar.getInstance()
        cal.timeInMillis = userWakeUpTime
        wakeUpTimeTextField.editText!!.setText(
            String.format(
                "%02d:%02d",
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
            )
        )
        cal.timeInMillis = userSleepingTime
        sleepTimeTextField.editText!!.setText(
            String.format(
                "%02d:%02d",
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
            )
        )

        wakeUpTimeTextField.editText!!.setOnClickListener {

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = userWakeUpTime

            val mTimePicker = TimePickerDialog(
                this,
                { timePickerDialog, hour, minutes ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, hour)
                    time.set(Calendar.MINUTE, minutes)
                    userWakeUpTime = time.timeInMillis

                    wakeUpTimeTextField.editText!!.setText(
                        String.format("%02d:%02d", hour, minutes)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), isClock24Hours
            )
            mTimePicker.show()
        }


        sleepTimeTextField.editText!!.setOnClickListener {

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = userSleepingTime

            val mTimePicker = TimePickerDialog(
                this,
                { timePickerDialog, hour, minutes ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, hour)
                    time.set(Calendar.MINUTE, minutes)
                    userSleepingTime = time.timeInMillis

                    sleepTimeTextField.editText!!.setText(
                        String.format("%02d:%02d", hour, minutes)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), isClock24Hours
            )
            mTimePicker.show()
        }

        updateDataBtn.setOnClickListener {

            val currentTarget = myPrefs.getInt(Utils.TOTAL_INTAKE, 0)

            userName = userNameTextField.editText!!.text.toString()
            userWeight = weightTextField.editText!!.text.toString()
            totalWorkTime = workTimeTextField.editText!!.text.toString()
            notificationMessage = notificationTextTextField.editText!!.text.toString()
            customWaterTarget = targetTextField.editText!!.text.toString()

            when {
                TextUtils.isEmpty(userName) -> Toast.makeText(
                    this,
                    getString(R.string.user_name_error_msg),
                    Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(notificationMessage) -> Toast.makeText(
                    this,
                    "Please add a notification message",
                    Toast.LENGTH_SHORT
                ).show()
                notificationFrequency == 0 -> Toast.makeText(
                    this,
                    "Please select a notification frequency",
                    Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(userWeight) -> Toast.makeText(
                    this, getString(R.string.weight_error_msg), Toast.LENGTH_SHORT
                ).show()
                userWeight.toInt() > 200 || userWeight.toInt() < 20 -> Toast.makeText(
                    this,
                    getString(R.string.invalid_weight_error_msg),
                    Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(totalWorkTime) -> Toast.makeText(
                    this,
                    getString(R.string.workout_time_error_msg),
                    Toast.LENGTH_SHORT
                ).show()
                totalWorkTime.toInt() > 500 || totalWorkTime.toInt() < 0 -> Toast.makeText(
                    this,
                    getString(R.string.invalid_workout_time_error_msg),
                    Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(customWaterTarget) -> Toast.makeText(
                    this,
                    getString(R.string.custom_target_err_msg),
                    Toast.LENGTH_SHORT
                ).show()
                else -> {

                    val editor = myPrefs.edit()
                    editor.putString(Utils.USER_NAME, userName)
                    editor.putInt(Utils.WEIGHT_PREFS, userWeight.toInt())
                    editor.putInt(Utils.WORK_TIME_PREFS, totalWorkTime.toInt())
                    editor.putLong(Utils.WAKEUP_TIME_PREFS, userWakeUpTime)
                    editor.putLong(Utils.SLEEPING_TIME_PREFS, userSleepingTime)
                    editor.putString(Utils.NOTIFICATION_MSG_PREFS, notificationMessage)
                    editor.putInt(Utils.NOTIFICATION_FREQUENCY_PREFS, notificationFrequency)
                    val sqliteUtils = DbUtils(this)
                    if (currentTarget != customWaterTarget.toInt()) {
                        editor.putInt(Utils.TOTAL_INTAKE, customWaterTarget.toInt())

                        sqliteUtils.updateTotalIntake(
                            Utils.getCurrentDateHumanReadable()!!,
                            customWaterTarget.toInt()
                        )
                    } else {
                        val totalWaterIntake = Utils.calculateWaterIntake(userWeight.toInt(), totalWorkTime.toInt())
                        val df = DecimalFormat("#")
                        df.roundingMode = RoundingMode.CEILING
                        editor.putInt(Utils.TOTAL_INTAKE, df.format(totalWaterIntake).toInt())

                        sqliteUtils.updateTotalIntake(
                            Utils.getCurrentDateHumanReadable()!!,
                            df.format(totalWaterIntake).toInt()
                        )
                    }

                    editor.apply()

                    Toast.makeText(this, getString(R.string.update_success_msg), Toast.LENGTH_SHORT).show()
                    val alarmUtils = AlarmUtils()
                    alarmUtils.cancelAlarm(this)
                    alarmUtils.setRepeatingAlarm(
                        this,
                        myPrefs.getInt(Utils.NOTIFICATION_FREQUENCY_PREFS, 30).toLong()
                    )
                    finish()

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 999) {

            val uri = data?.extras?.get( RingtoneManager.EXTRA_RINGTONE_PICKED_URI) as Uri

            currentRingtoneUri = uri.toString()
            myPrefs.edit().putString(Utils.NOTIFICATION_TONE_URI_PREFS, currentRingtoneUri).apply()
            val ringtone = RingtoneManager.getRingtone(this, uri)
            ringtoneTextField.editText!!.setText(ringtone.getTitle(this))

        }
    }

}