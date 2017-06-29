/*
 * Copyright (C) 2017  Mitchell Skaggs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.skaggsm.kpi3.weather.content

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.verbose

/**
 * Created by Mitchell Skaggs on 6/24/17.

 * @see [Android Tutorial: Writing your own Content Provider | Grokking Android](http://www.grokkingandroid.com/android-tutorial-writing-your-own-content-provider/)
 */

class WeatherContentProvider : ContentProvider(), AnkoLogger {

    private lateinit var weatherSQLiteOpenHelper: WeatherSQLiteOpenHelper
    @Volatile var isInBatchMode = false

    override fun onCreate(): Boolean {
        weatherSQLiteOpenHelper = WeatherSQLiteOpenHelper(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        verbose("Querying")
        val database = weatherSQLiteOpenHelper.readableDatabase
        val queryBuilder = SQLiteQueryBuilder()
        when (URI_MATCHER.match(uri)) {
            WEATHER_DATA -> queryBuilder.tables = WeatherDataContract.WeatherDataTable.TABLE_NAME
            else -> throw IllegalArgumentException("Unsupported URI: " + uri)
        }
        val cursor = queryBuilder.query(
                database,
                projection,
                selection,
                selectionArgs, null, null,
                sortOrder)

        // if we want to be notified of any changes:
        cursor.setNotificationUri(context.contentResolver, uri)

        return cursor
    }

    override fun getType(uri: Uri): String? {
        when (URI_MATCHER.match(uri)) {
            WEATHER_DATA -> return WeatherDataContract.WeatherDataTable.CONTENT_TYPE
            else -> return null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        verbose("Inserting")
        val id: Long
        val database = weatherSQLiteOpenHelper.writableDatabase
        when (URI_MATCHER.match(uri)) {
            WEATHER_DATA -> id = database.insert(WeatherDataContract.WeatherDataTable.TABLE_NAME, null, values)
            else -> throw IllegalArgumentException("Uri not supported! $uri")
        }
        return getUriForIdAndUpdate(id, uri)
    }

    private fun getUriForIdAndUpdate(id: Long, uri: Uri): Uri {
        if (id > 0) {
            val itemUri = ContentUris.withAppendedId(uri, id)
            if (!isInBatchMode) {
                // notify all listeners of changes:
                context.contentResolver.notifyChange(itemUri, null)
            }
            return itemUri
        } else
        // Something went wrong inserting into the database, id is < 0
            throw SQLException("Problem while inserting into uri: $uri")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        verbose("Deleting")
        val db = weatherSQLiteOpenHelper.writableDatabase
        val delCount: Int
        when (URI_MATCHER.match(uri)) {
            WEATHER_DATA -> delCount = db.delete(
                    WeatherDataContract.WeatherDataTable.TABLE_NAME,
                    selection,
                    selectionArgs)
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
        // Notify all listeners of changes:
        if (delCount > 0 && !isInBatchMode) {
            context.contentResolver.notifyChange(uri, null)
        }
        return delCount
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        verbose("Updating")
        val database = weatherSQLiteOpenHelper.writableDatabase
        val updateCount: Int
        when (URI_MATCHER.match(uri)) {
            WEATHER_DATA -> updateCount = database.update(
                    WeatherDataContract.WeatherDataTable.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs)
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
        // Notify all listeners of changes:
        if (updateCount > 0 && !isInBatchMode) {
            context.contentResolver.notifyChange(uri, null)
        }
        return updateCount
    }

    companion object {
        private val WEATHER_DATA = 1
        private val URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH)
                .apply {
                    addURI(WeatherDataContract.AUTHORITY, WeatherDataContract.WeatherDataTable.CONTENT_PATH, WEATHER_DATA)
                }
    }
}
