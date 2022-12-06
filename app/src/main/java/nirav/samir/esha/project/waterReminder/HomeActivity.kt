package nirav.samir.esha.project.waterReminder

import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {

    private lateinit var myPrefs: SharedPreferences
    private lateinit var sqliteUtils: DbUtils
    private var userName: String = ""
    private var totalWaterIntake: Int = 0
    private lateinit var currentDate: String
    private var numberOfInTook: Int = 0
    private var notificationStatus: Boolean = false
    private var selectedWaterQuantityOption: Int? = null
    private var doubleBackToExitPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        myPrefs = getSharedPreferences(Utils.USERS_SHARED_DATA_PREF, MODE_PRIVATE)
        sqliteUtils = DbUtils(this)



    }

    override fun onStart() {
        super.onStart()
        totalWaterIntake = myPrefs.getInt(Utils.TOTAL_INTAKE, 0)
        userName = myPrefs.getString(Utils.USER_NAME, "").toString()

        username_tv.text = getString(R.string.hello)+ " "+ userName
        if (myPrefs.getBoolean(Utils.FIRST_RUN_PREFS, true)) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        } else if (totalWaterIntake <= 0) {
            startActivity(Intent(this, UserInfoRegisterActivity::class.java))
            finish()
        }

        currentDate = Utils.getCurrentDateHumanReadable()!!
        val outValue = TypedValue()
        applicationContext.theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValue,
            true
        )

        notificationStatus = myPrefs.getBoolean(Utils.NOTIFICATION_STATUS_PREFS, true)
        val alarm = AlarmUtils()
        if (!alarm.checkAlarm(this) && notificationStatus) {
            btnNotific.setImageDrawable(getDrawable(R.drawable.bell_icon))
            alarm.setRepeatingAlarm(
                this,
                myPrefs.getInt(Utils.NOTIFICATION_FREQUENCY_PREFS, 30).toLong()
            )
        }

        if (notificationStatus) {
            btnNotific.setImageDrawable(getDrawable(R.drawable.bell_icon))
        } else {
            btnNotific.setImageDrawable(getDrawable(R.drawable.bell_disabled_icon))
        }

        sqliteUtils.addAll(currentDate, 0, totalWaterIntake)

        updateDataValues()

        btnMenu.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        fabAdd.setOnClickListener {
            if (selectedWaterQuantityOption != null) {
                if ((numberOfInTook * 100 / totalWaterIntake) <= 110) {
                    if (sqliteUtils.addIntook(currentDate, selectedWaterQuantityOption!!) > 0) {
                        numberOfInTook += selectedWaterQuantityOption!!
                        setWaterLevel(numberOfInTook, totalWaterIntake)

                        Toast.makeText(this, getString(R.string.intake_saved_msg), Toast.LENGTH_SHORT)
                            .show()

                    }
                } else {
                    Toast.makeText(this, getString(R.string.already_achieve_msg), Toast.LENGTH_SHORT).show()
                }
                selectedWaterQuantityOption = null
                tvCustom.text = "Custom"
                fiftyMilliliterOption.background = getDrawable(outValue.resourceId)
                hundredMilliliterOption.background = getDrawable(outValue.resourceId)
                oneFiftyMilliliterOption.background = getDrawable(outValue.resourceId)
                twoHundredMilliliterOption.background = getDrawable(outValue.resourceId)
                twoFiftyMilliliterOption.background = getDrawable(outValue.resourceId)
                customMilliliterOption.background = getDrawable(outValue.resourceId)

                // remove pending notifications
                val notificationManager : NotificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            } else {
                YoYo.with(Techniques.Shake)
                    .duration(700)
                    .playOn(cardView)
                Toast.makeText(this, getString(R.string.select_opt), Toast.LENGTH_SHORT).show()
            }
        }

        btnNotific.setOnClickListener {
            notificationStatus = !notificationStatus
            myPrefs.edit().putBoolean(Utils.NOTIFICATION_STATUS_PREFS, notificationStatus).apply()
            if (notificationStatus) {
                btnNotific.setImageDrawable(getDrawable(R.drawable.bell_icon))
                Toast.makeText(this, getString(R.string.notification_enabled), Toast.LENGTH_SHORT).show()
                alarm.setRepeatingAlarm(
                    this,
                    myPrefs.getInt(Utils.NOTIFICATION_FREQUENCY_PREFS, 30).toLong()
                )
            } else {
                btnNotific.setImageDrawable(getDrawable(R.drawable.bell_disabled_icon))
                Toast.makeText(this, getString(R.string.notifaication_disabled), Toast.LENGTH_SHORT).show()
                alarm.cancelAlarm(this)
            }
        }

        btnStats.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        fiftyMilliliterOption.setOnClickListener {
            
            selectedWaterQuantityOption = 50
            fiftyMilliliterOption.background = getDrawable(R.drawable.option_select_bg)
            hundredMilliliterOption.background = getDrawable(outValue.resourceId)
            oneFiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            twoHundredMilliliterOption.background = getDrawable(outValue.resourceId)
            twoFiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            customMilliliterOption.background = getDrawable(outValue.resourceId)

        }

        hundredMilliliterOption.setOnClickListener {
            
            selectedWaterQuantityOption = 100
            fiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            hundredMilliliterOption.background = getDrawable(R.drawable.option_select_bg)
            oneFiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            twoHundredMilliliterOption.background = getDrawable(outValue.resourceId)
            twoFiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            customMilliliterOption.background = getDrawable(outValue.resourceId)

        }

        oneFiftyMilliliterOption.setOnClickListener {
            
            selectedWaterQuantityOption = 150
            fiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            hundredMilliliterOption.background = getDrawable(outValue.resourceId)
            oneFiftyMilliliterOption.background = getDrawable(R.drawable.option_select_bg)
            twoHundredMilliliterOption.background = getDrawable(outValue.resourceId)
            twoFiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            customMilliliterOption.background = getDrawable(outValue.resourceId)

        }

        twoHundredMilliliterOption.setOnClickListener {
            
            selectedWaterQuantityOption = 200
            fiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            hundredMilliliterOption.background = getDrawable(outValue.resourceId)
            oneFiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            twoHundredMilliliterOption.background = getDrawable(R.drawable.option_select_bg)
            twoFiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            customMilliliterOption.background = getDrawable(outValue.resourceId)

        }

        twoFiftyMilliliterOption.setOnClickListener {
            
            selectedWaterQuantityOption = 250
            fiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            hundredMilliliterOption.background = getDrawable(outValue.resourceId)
            oneFiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            twoHundredMilliliterOption.background = getDrawable(outValue.resourceId)
            twoFiftyMilliliterOption.background = getDrawable(R.drawable.option_select_bg)
            customMilliliterOption.background = getDrawable(outValue.resourceId)

        }

        customMilliliterOption.setOnClickListener {
            

            val li = LayoutInflater.from(this)
            val promptsView = li.inflate(R.layout.custom_input_dialog, null)

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setView(promptsView)

            val userInput = promptsView
                .findViewById(R.id.etCustomInput) as TextInputLayout

            alertDialogBuilder.setPositiveButton("OK") { dialog, id ->
                val inputText = userInput.editText!!.text.toString()
                if (!TextUtils.isEmpty(inputText)) {
                    tvCustom.text = "${inputText} ml"
                    selectedWaterQuantityOption = inputText.toInt()
                }
            }.setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            fiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            hundredMilliliterOption.background = getDrawable(outValue.resourceId)
            oneFiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            twoHundredMilliliterOption.background = getDrawable(outValue.resourceId)
            twoFiftyMilliliterOption.background = getDrawable(outValue.resourceId)
            customMilliliterOption.background = getDrawable(R.drawable.option_select_bg)

        }

    }

    fun updateDataValues() {
        totalWaterIntake = myPrefs.getInt(Utils.TOTAL_INTAKE, 0)

        numberOfInTook = sqliteUtils.getWaterIntook(currentDate)

        setWaterLevel(numberOfInTook, totalWaterIntake)
    }

    private fun setWaterLevel(inTook: Int, totalWaterIntake: Int) {

        YoYo.with(Techniques.SlideInDown)
            .duration(500)
            .playOn(tvIntook)
        tvIntook.text = "$inTook"
        tvTotalIntake.text = "/$totalWaterIntake ml"
        val progress = ((inTook / totalWaterIntake.toFloat()) * 100).toInt()
        YoYo.with(Techniques.Pulse)
            .duration(500)
            .playOn(intakeProgress)
        intakeProgress.currentProgress = progress
        if ((inTook * 100 / totalWaterIntake) > 110) {
            Toast.makeText(this, getString(R.string.achieve_msg), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            getString(R.string.exit_msg),
            Toast.LENGTH_SHORT
        ).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 1000)
    }

}
