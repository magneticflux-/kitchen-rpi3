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

package org.skaggsm.kpi3.cards

import android.app.Activity
import android.app.Fragment
import android.app.LoaderManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.widget.CardView
import android.view.View
import android.widget.ImageView
import android.widget.TextSwitcher
import android.widget.TextView
import com.github.jinatonic.confetti.ConfettiManager
import com.github.jinatonic.confetti.ConfettiSource
import com.github.jinatonic.confetti.ConfettoGenerator
import com.github.jinatonic.confetti.confetto.BitmapConfetto
import com.github.jinatonic.confetti.confetto.Confetto
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.picasso.Picasso
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.info
import org.skaggsm.kpi3.R
import org.skaggsm.kpi3.weather.StartWeatherServiceReceiver
import org.skaggsm.kpi3.weather.WeatherDownloadService
import org.skaggsm.kpi3.weather.content.WeatherDataContract.WeatherDataTable.COLUMN_NAME_JSON
import org.skaggsm.kpi3.weather.content.WeatherDataContract.WeatherDataTable.CONTENT_URI
import org.skaggsm.kpi3.weather.model.WeatherResponse
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Mitchell on 6/15/2017.
 */

class WeatherCard : AbstractFlexibleItem<WeatherCardViewHolder>(), HasSpanSize, AnkoLogger, LoaderManager.LoaderCallbacks<Cursor> {
    companion object {
        const val WEATHER_LOADER_ID = 0
        val MOSHI = object : ThreadLocal<Moshi>() {
            override fun initialValue(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        }
    }

    private lateinit var context: Context
    private lateinit var holder: WeatherCardViewHolder

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(context, CONTENT_URI, arrayOf(COLUMN_NAME_JSON), null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor) {
        val startTime = System.nanoTime()

        val jsonAdapter = MOSHI.get().adapter(WeatherResponse::class.java)

        if (data.moveToNext()) {
            val value = data.getString(data.getColumnIndex(COLUMN_NAME_JSON))

            val weatherResponse = jsonAdapter.fromJson(value)!!

            val elapsed = System.nanoTime() - startTime
            info("Elapsed time: ${TimeUnit.NANOSECONDS.toMillis(elapsed)}ms")

            setWeatherResponse(weatherResponse)
        }
    }

    private fun setWeatherResponse(weatherResponse: WeatherResponse) {
        holder.temperatureText.setText("${weatherResponse.current_observation.temp_f}° F")
        holder.weatherDescriptionText.setText(weatherResponse.current_observation.weather)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
    }

    override fun getRequestedSize(): Int = 1

    var rainBitmap: Bitmap? = null
    var confettiManager: ConfettiManager? = null

    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int = System.identityHashCode(this)

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: WeatherCardViewHolder, position: Int, payloads: MutableList<Any?>?) {
        this.context = adapter.recyclerView.context
        this.holder = holder

        val factory = { TextView(context).apply { setTextAppearance(android.R.style.TextAppearance_Material_Large_Inverse) } }
        holder.temperatureText.setFactory(factory)
        holder.weatherDescriptionText.setFactory(factory)

        val loaderManager = context.findActivity.loaderManager
        loaderManager.initLoader(WEATHER_LOADER_ID, null, this)

        Picasso.with(context)
                .load(R.drawable.wunderground_logo_horizontal)
                .into(holder.logo)

        holder.logo.setOnClickListener {
            info("Starting WeatherDownloadService...")
            val jobScheduler = context.getSystemService(JobScheduler::class.java)
            jobScheduler.schedule(
                    JobInfo.Builder(StartWeatherServiceReceiver.JOB_ID + 1, ComponentName(context, WeatherDownloadService::class.java))
                            .setOverrideDeadline(0)
                            .build())
        }

        if (rainBitmap == null)
            rainBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.rain)

        holder.card.post {
            val angle = 5
            val angleRads = Math.toRadians(angle.toDouble())
            val speed = 500

            val yVelocity = Math.cos(angleRads).toFloat() * speed
            val xVelocity = Math.sin(angleRads).toFloat() * speed

            val extraDistanceLeft = Math.max(Math.tan(angleRads) * holder.card.height, 0.toDouble()).toInt()
            val extraDistanceRight = Math.max(Math.tan(Math.PI - angleRads) * holder.card.height, 0.toDouble()).toInt()

            confettiManager = ConfettiManager(
                    context,
                    BitmapConfettoGenerator(rainBitmap!!),
                    ConfettiSource(0 - extraDistanceLeft, 0, holder.card.width + extraDistanceRight, 0),
                    holder.card)
                    .setEmissionDuration(1000)
                    .setEmissionRate(5F)
                    .setVelocityY(yVelocity, yVelocity * .05F)
                    .setVelocityX(xVelocity, xVelocity * .05F)
                    .setInitialRotation(-angle)
                    .animate()
        }
    }

    override fun unbindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: WeatherCardViewHolder?, position: Int) {
        super.unbindViewHolder(adapter, holder, position)
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): WeatherCardViewHolder {
        return WeatherCardViewHolder(view, adapter)
    }

    override fun getLayoutRes(): Int = R.layout.item_weather_card
}

val Context.findActivity: Activity
    get() {
        if (this is Activity)
            return this
        else if (this is Fragment)
            return this.activity
        else
            throw ClassCastException("Unable to find Activity! Are you in a Service?")
    }

class WeatherCardViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>) : FlexibleViewHolder(view, adapter) {
    val card: CardView = view as CardView
    val weather: TextView = view.find<TextView>(R.id.current_weather_icon)
    val logo: ImageView = view.find<ImageView>(R.id.weather_underground_logo)
    val temperatureText: TextSwitcher = view.find<TextSwitcher>(R.id.temperature_text)
    val weatherDescriptionText: TextSwitcher = view.find<TextSwitcher>(R.id.weather_description_text)
}

class BitmapConfettoGenerator(val bitmap: Bitmap) : ConfettoGenerator {
    override fun generateConfetto(random: Random?): Confetto {
        return BitmapConfetto(bitmap)
    }
}