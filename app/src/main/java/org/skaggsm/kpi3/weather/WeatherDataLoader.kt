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

import android.content.Context
import android.content.CursorLoader
import android.database.Cursor
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import org.skaggsm.kpi3.weather.content.WeatherDataContract.WeatherDataTable.COLUMN_NAME_JSON
import org.skaggsm.kpi3.weather.content.WeatherDataContract.WeatherDataTable.CONTENT_URI
import org.skaggsm.kpi3.weather.model.WeatherResponse

/**
 * Created by Mitchell on 6/30/2017.
 */
class WeatherDataLoader(context: Context) : CursorLoader(context, CONTENT_URI, arrayOf(COLUMN_NAME_JSON), null, null, null) {
    companion object {
        val MOSHI: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    var weatherResponse: WeatherResponse? = null

    override fun loadInBackground(): Cursor {
        val result = super.loadInBackground()

        val jsonAdapter = MOSHI.adapter(WeatherResponse::class.java)
        if (result.moveToNext()) {
            val value = result.getString(result.getColumnIndex(COLUMN_NAME_JSON))
            weatherResponse = jsonAdapter.fromJson(value)
        }

        return result
    }
}