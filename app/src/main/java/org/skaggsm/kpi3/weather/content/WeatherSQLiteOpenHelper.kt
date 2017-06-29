package org.skaggsm.kpi3.weather.content

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Mitchell Skaggs on 2/23/17.
 */

class WeatherSQLiteOpenHelper(context: Context) : SQLiteOpenHelper(context, NAME, null, VERSION) {

    companion object {
        const val NAME = "weather.db"
        const val VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(WeatherDataContract.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(WeatherDataContract.SQL_DELETE_ENTRIES)
        onCreate(db)
    }
}
