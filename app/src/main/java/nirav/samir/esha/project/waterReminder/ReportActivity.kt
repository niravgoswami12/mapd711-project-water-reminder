package nirav.samir.esha.project.waterReminder

import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_report.*
import kotlin.math.max


class ReportActivity : AppCompatActivity() {

    private lateinit var myPrefs: SharedPreferences
    private lateinit var sqliteUtils: SqliteUtils
    private var totalWaterIntakePercentage: Float = 0f
    private var numberOfGlasses: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        myPrefs = getSharedPreferences(Utils.USERS_SHARED_DATA_PREF, MODE_PRIVATE)
        sqliteUtils = SqliteUtils(this)

        backBtn.setOnClickListener {
            finish()
        }

        val waterDataEntries = ArrayList<Entry>()
        val waterDataArray = ArrayList<String>()

        val cursor: Cursor = sqliteUtils.getAllStats()

        if (cursor.moveToFirst()) {

            for (i in 0 until cursor.count) {
                Log.d("data1----", cursor.getString(1))
                Log.d("data2----", cursor.getString(2))
                Log.d("data3----", cursor.getString(3))
                waterDataArray.add(cursor.getString(1))
                val percent = cursor.getInt(2) / cursor.getInt(3).toFloat() * 100
                totalWaterIntakePercentage += percent
                numberOfGlasses += cursor.getInt(2)
                waterDataEntries.add(Entry(i.toFloat(), percent))
                cursor.moveToNext()
            }

        } else {
            Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
        }

        if (waterDataEntries.isNotEmpty()) {

            waterLineChart.description.isEnabled = false
            waterLineChart.animateY(1000, Easing.Linear)
            waterLineChart.viewPortHandler.setMaximumScaleX(1.5f)
            waterLineChart.xAxis.setDrawGridLines(false)
            waterLineChart.xAxis.position = XAxis.XAxisPosition.TOP
            waterLineChart.xAxis.isGranularityEnabled = true
            waterLineChart.legend.isEnabled = false
            waterLineChart.fitScreen()
            waterLineChart.isAutoScaleMinMaxEnabled = true
            waterLineChart.scaleX = 1f
            waterLineChart.setPinchZoom(true)
            waterLineChart.isScaleXEnabled = true
            waterLineChart.isScaleYEnabled = false
            waterLineChart.axisLeft.textColor = Color.BLACK
            waterLineChart.xAxis.textColor = Color.BLACK
            waterLineChart.axisLeft.setDrawAxisLine(false)
            waterLineChart.xAxis.setDrawAxisLine(false)
            waterLineChart.setDrawMarkers(false)
            waterLineChart.xAxis.labelCount = 5

            val leftAxis = waterLineChart.axisLeft
            leftAxis.axisMinimum = 0f // always start at zero
            val maxObject: Entry = waterDataEntries.maxBy { it.y }!! // waterDataEntries is not empty here
            leftAxis.axisMaximum = max(a = maxObject.y, b = 100f) + 15f // 15% margin on top
            val targetLine = LimitLine(100f, "")
            targetLine.enableDashedLine(5f, 5f, 0f)
            leftAxis.addLimitLine(targetLine)

            val rightAxis = waterLineChart.axisRight
            rightAxis.setDrawGridLines(false)
            rightAxis.setDrawZeroLine(false)
            rightAxis.setDrawAxisLine(false)
            rightAxis.setDrawLabels(false)

            val dataSet = LineDataSet(waterDataEntries, "Label")
            dataSet.setDrawCircles(false)
            dataSet.lineWidth = 2.5f
            dataSet.color = ContextCompat.getColor(this, R.color.colorSecondaryDark)
            dataSet.setDrawFilled(true)
            dataSet.fillDrawable = getDrawable(R.drawable.graph_fill_gradiant)
            dataSet.setDrawValues(false)
            dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

            val lineData = LineData(dataSet)
            waterLineChart.xAxis.valueFormatter = (ChartXValueFormatter(waterDataArray))
            waterLineChart.data = lineData
            waterLineChart.invalidate()

            val remaining = myPrefs.getInt(
                Utils.TOTAL_INTAKE,
                0
            ) - sqliteUtils.getWaterIntook(Utils.getCurrentDateHumanReadable()!!)

            if (remaining > 0) {
                remainingIntake.text = "$remaining ml"
            } else {
                remainingIntake.text = "0 ml"
            }

            targetIntake.text = "${myPrefs.getInt(
                Utils.TOTAL_INTAKE,
                0
            )
            } ml"

            val percentage = sqliteUtils.getWaterIntook(Utils.getCurrentDateHumanReadable()!!) * 100 / myPrefs.getInt(
                Utils.TOTAL_INTAKE,
                0
            )
            waterLevelView.centerTitle = "$percentage%"
            waterLevelView.progressValue = percentage

        }
    }
}