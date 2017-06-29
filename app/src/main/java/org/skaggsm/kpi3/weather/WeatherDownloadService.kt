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

package org.skaggsm.kpi3.weather

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.provider.BaseColumns
import okhttp3.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.verbose
import org.skaggsm.kpi3.BuildConfig.API_KEY
import org.skaggsm.kpi3.weather.content.WeatherDataContract.WeatherDataTable.COLUMN_NAME_JSON
import org.skaggsm.kpi3.weather.content.WeatherDataContract.WeatherDataTable.CONTENT_URI
import java.io.IOException

/**
 * Created by Mitchell on 6/20/2017.
 */
class WeatherDownloadService : JobService(), AnkoLogger, Callback {

    private var params: JobParameters? = null


    companion object {
        const val FEATURES = "conditions/forecast/hourly"
        const val QUERY = "pws:KMOSTCHA15"//"MO/Saint_Charles"
        const val FORMAT = "json"
    }

    override fun onFailure(call: Call, e: IOException) {
        info("Request failure! Exception: $e")
        jobFinished(params, true)
        params = null
    }

    override fun onResponse(call: Call, response: Response) {
        info("Request success!")

        val body: String? = response.body()?.string()
        verbose("Body: $body")

        if (body != null) {
            val rows = contentResolver.query(CONTENT_URI, arrayOf(BaseColumns._ID), null, null, null).use { it.count }

            when (rows) {
                0 -> contentResolver.insert(CONTENT_URI,
                        ContentValues().apply { put(COLUMN_NAME_JSON, body) })

                1 -> contentResolver.update(CONTENT_URI,
                        ContentValues().apply { put(COLUMN_NAME_JSON, body) },
                        null, // Update everything
                        null)

                else -> throw SQLiteException("More than 1 row present in the Weather table!")
            }
        }

        jobFinished(params, false)
        params = null
    }

    override fun onStopJob(params: JobParameters): Boolean {
        info("Job stopped!")
        return true
    }

    override fun onStartJob(params: JobParameters): Boolean {
        info("Job started!")

        val client: OkHttpClient = OkHttpClient()

        val request: Request = Request.Builder()
                .url(HttpUrl.parse("http://api.wunderground.com/api/$API_KEY/$FEATURES/q/$QUERY.$FORMAT"))
                .build()

        this.params = params
        client.newCall(request).enqueue(this)

        return true
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_REDELIVER_INTENT
    }
}