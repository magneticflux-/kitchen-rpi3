package org.skaggsm.kpi3.weather.content

import android.content.ContentResolver.CURSOR_DIR_BASE_TYPE
import android.net.Uri
import android.provider.BaseColumns
import android.provider.BaseColumns._ID
import org.skaggsm.kpi3.BuildConfig.APPLICATION_ID
import org.skaggsm.kpi3.weather.content.WeatherDataContract.WeatherDataTable.COLUMN_NAME_JSON
import org.skaggsm.kpi3.weather.content.WeatherDataContract.WeatherDataTable.TABLE_NAME

/**
 * Created by Mitchell Skaggs on 2/23/17.
 */

object WeatherDataContract {
    const val AUTHORITY = "$APPLICATION_ID.weather"
    val CONTENT_URI = Uri.parse("content://$AUTHORITY")!!

    const val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME ($_ID INTEGER PRIMARY KEY,$COLUMN_NAME_JSON BLOB)"
    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"

    object WeatherDataTable : BaseColumns {
        const val CONTENT_PATH = "weather"
        val CONTENT_URI = Uri.withAppendedPath(WeatherDataContract.CONTENT_URI, CONTENT_PATH)!!
        const val TABLE_NAME = "weather_data"
        const val COLUMN_NAME_JSON = "weather_data_json"

        const val CONTENT_TYPE = "$CURSOR_DIR_BASE_TYPE/vnd.$AUTHORITY.$CONTENT_PATH"
    }
}
