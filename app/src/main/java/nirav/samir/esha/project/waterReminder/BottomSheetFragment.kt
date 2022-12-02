package nirav.samir.esha.project.waterReminder

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_fragment.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


class BottomSheetFragment(val mCtx: Context) : BottomSheetDialogFragment() {

    private var totalWorkTime: String = ""
    private lateinit var myPrefs: SharedPreferences
    private var userWeight: String = ""
    private var customWaterTarget: String = ""
    private var userSleepingTime: Long = 0
    private var userWakeUpTime: Long = 0
    private var notificationMessage: String = ""
    private var currentRingtoneUri: String? = ""
    private var notificationFrequency: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_fragment, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isClock24Hours = android.text.format.DateFormat.is24HourFormat(mCtx)

        myPrefs = mCtx.getSharedPreferences(Utils.USERS_SHARED_DATA_PREF, Utils.PRIVATE_MODE)

        weightTextField.editText!!.setText("" + myPrefs.getInt(Utils.WEIGHT_PREFS, 0))
        workTimeTextField.editText!!.setText("" + myPrefs.getInt(Utils.WORK_TIME_PREFS, 0))
        targetTextField.editText!!.setText("" + myPrefs.getInt(Utils.TOTAL_INTAKE, 0))
        notificationTextTextField.editText!!.setText(
            myPrefs.getString(
                Utils.NOTIFICATION_MSG_PREFS,
                "Hey... It's time to  drink some water...."
            )
        )
        currentRingtoneUri = myPrefs.getString(
            Utils.NOTIFICATION_TONE_URI_PREFS,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()
        )
        ringtoneTextField.editText!!.setText(
            RingtoneManager.getRingtone(
                mCtx,
                Uri.parse(currentRingtoneUri)
            ).getTitle(mCtx)
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
                "Select ringtone for notifications:"
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
                mCtx,
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
            mTimePicker.setTitle("Select Wakeup Time")
            mTimePicker.show()
        }


        sleepTimeTextField.editText!!.setOnClickListener {

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = userSleepingTime

            val mTimePicker = TimePickerDialog(
                mCtx,
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
            mTimePicker.setTitle("Select Sleeping Time")
            mTimePicker.show()
        }

        updateDataBtn.setOnClickListener {

            val currentTarget = myPrefs.getInt(Utils.TOTAL_INTAKE, 0)

            userWeight = weightTextField.editText!!.text.toString()
            totalWorkTime = workTimeTextField.editText!!.text.toString()
            notificationMessage = notificationTextTextField.editText!!.text.toString()
            customWaterTarget = targetTextField.editText!!.text.toString()

            when {
                TextUtils.isEmpty(notificationMessage) -> Toast.makeText(
                    mCtx,
                    "Please a notification message",
                    Toast.LENGTH_SHORT
                ).show()
                notificationFrequency == 0 -> Toast.makeText(
                    mCtx,
                    "Please select a notification frequency",
                    Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(userWeight) -> Toast.makeText(
                    mCtx, "Please input your userWeight", Toast.LENGTH_SHORT
                ).show()
                userWeight.toInt() > 200 || userWeight.toInt() < 20 -> Toast.makeText(
                    mCtx,
                    "Please input a valid userWeight",
                    Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(totalWorkTime) -> Toast.makeText(
                    mCtx,
                    "Please input your workout time",
                    Toast.LENGTH_SHORT
                ).show()
                totalWorkTime.toInt() > 500 || totalWorkTime.toInt() < 0 -> Toast.makeText(
                    mCtx,
                    "Please input a valid workout time",
                    Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(customWaterTarget) -> Toast.makeText(
                    mCtx,
                    "Please input your custom target",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {

                    val editor = myPrefs.edit()
                    editor.putInt(Utils.WEIGHT_PREFS, userWeight.toInt())
                    editor.putInt(Utils.WORK_TIME_PREFS, totalWorkTime.toInt())
                    editor.putLong(Utils.WAKEUP_TIME_PREFS, userWakeUpTime)
                    editor.putLong(Utils.SLEEPING_TIME_PREFS, userSleepingTime)
                    editor.putString(Utils.NOTIFICATION_MSG_PREFS, notificationMessage)
                    editor.putInt(Utils.NOTIFICATION_FREQUENCY_PREFS, notificationFrequency)

                    val sqliteUtils = SqliteUtils(mCtx)

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

                    Toast.makeText(mCtx, "Values updated successfully", Toast.LENGTH_SHORT).show()
                    val alarmUtils = AlarmUtils()
                    alarmUtils.cancelAlarm(mCtx)
                    alarmUtils.setAlarm(
                        mCtx,
                        myPrefs.getInt(Utils.NOTIFICATION_FREQUENCY_PREFS, 30).toLong()
                    )
                    dismiss()
                    (activity as MainActivity?)!!.updateDataValues()

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 999) {

            val uri = data!!.getParcelableArrayExtra( RingtoneManager.EXTRA_RINGTONE_PICKED_URI) as Uri
            currentRingtoneUri = uri.toString()
            myPrefs.edit().putString(Utils.NOTIFICATION_TONE_URI_PREFS, currentRingtoneUri).apply()
            val ringtone = RingtoneManager.getRingtone(mCtx, uri)
            ringtoneTextField.editText!!.setText(ringtone.getTitle(mCtx))

        }
    }
}