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

package org.skaggsm.kpi3

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.text.format.Formatter
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.skaggsm.kpi3.cards.CalendarCard
import org.skaggsm.kpi3.cards.HasSpanSize
import org.skaggsm.kpi3.cards.WeatherCard


class MainActivity : AppCompatActivity(), AnkoLogger {
    lateinit var adapter: FlexibleAdapter<IFlexible<out FlexibleViewHolder>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.categories.contains("android.intent.category.IOT_LAUNCHER"))
            SystemClock.sleep(5000) // Prevent startup breaking

        setTitleIP()

        adapter = FlexibleAdapter(null)

        recycler_view.adapter = adapter

        val layoutManager = GridLayoutManager(this, 3)

        recycler_view.layoutManager = layoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = adapter.getItem(position)
                if (item is HasSpanSize)
                    return item.getRequestedSize()
                else return 1
            }
        }

        adapter.addItem(CalendarCard())
        adapter.addItem(WeatherCard())

        //Handler(Looper.getMainLooper()).postDelayed({ setTitleIP() }, 3000)
    }

    private fun setTitleIP(): Unit {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        @Suppress("DEPRECATION")
        title = "IP: \"${Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)}\""
    }
}
