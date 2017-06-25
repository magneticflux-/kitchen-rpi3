package org.skaggsm.kpi3

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
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

        val wm = getSystemService(Context.WIFI_SERVICE) as WifiManager
        @Suppress("DEPRECATION")
        title = "IP: \"${Formatter.formatIpAddress(wm.connectionInfo.ipAddress)}\""

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
    }
}
