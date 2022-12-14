package nirav.samir.esha.project.waterReminder

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbUtils(val context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "Water_DB"
        private val TABLE_STATS = "water_statistics"
        private val KEY_ID = "id"
        private val KEY_DATE = "date"
        private val KEY_TAKEN_INTAKE = "takenintake"
        private val KEY_TOTAL_INTAKE = "totalintake"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_STATS_TABLE = ("CREATE TABLE " + TABLE_STATS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE + " TEXT UNIQUE,"
                + KEY_TAKEN_INTAKE + " INT," + KEY_TOTAL_INTAKE + " INT" + ")")
        db?.execSQL(CREATE_STATS_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_STATS")
        onCreate(db)
    }

    fun addAll(date: String, intook: Int, totalintake: Int): Long {
        if (checkExistance(date) == 0) {
            val values = ContentValues()
            values.put(KEY_DATE, date)
            values.put(KEY_TAKEN_INTAKE, intook)
            values.put(KEY_TOTAL_INTAKE, totalintake)
            val db = this.writableDatabase
            val response = db.insert(TABLE_STATS, null, values)
            db.close()
            return response
        }
        return -1
    }

    @SuppressLint("Range")
    fun getWaterIntook(date: String): Int {
        val selectQuery = "SELECT $KEY_TAKEN_INTAKE FROM $TABLE_STATS WHERE $KEY_DATE = ?"
        val db = this.readableDatabase
        db.rawQuery(selectQuery, arrayOf(date)).use {
            if (it.moveToFirst()) {
                return it.getInt(it.getColumnIndex(KEY_TAKEN_INTAKE))
            }
        }
        return 0
    }

    fun addIntook(date: String, selectedWaterQuantityOption: Int): Int {
        val intook = getWaterIntook(date)
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TAKEN_INTAKE, intook + selectedWaterQuantityOption)

        val response = db.update(TABLE_STATS, contentValues, "$KEY_DATE = ?", arrayOf(date))
        db.close()
        return response
    }

     private fun checkExistance(date: String): Int {
        val selectQuery = "SELECT $KEY_TAKEN_INTAKE FROM $TABLE_STATS WHERE $KEY_DATE = ?"
        val db = this.readableDatabase
        db.rawQuery(selectQuery, arrayOf(date)).use {
            if (it.moveToFirst()) {
                return it.count
            }
        }
        return 0
    }

    fun getAllStats(): Cursor {
        val selectQuery = "SELECT * FROM $TABLE_STATS"
        val db = this.readableDatabase
        return db.rawQuery(selectQuery, null)

    }

    fun updateTotalIntake(date: String, totalintake: Int): Int {
        getWaterIntook(date)
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TOTAL_INTAKE, totalintake)

        val response = db.update(TABLE_STATS, contentValues, "$KEY_DATE = ?", arrayOf(date))
        db.close()
        return response
    }

}
