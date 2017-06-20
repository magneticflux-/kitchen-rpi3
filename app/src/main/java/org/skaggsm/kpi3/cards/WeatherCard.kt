package org.skaggsm.kpi3.cards

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.widget.CardView
import android.view.View
import android.widget.TextView
import com.github.jinatonic.confetti.ConfettiManager
import com.github.jinatonic.confetti.ConfettiSource
import com.github.jinatonic.confetti.ConfettoGenerator
import com.github.jinatonic.confetti.confetto.BitmapConfetto
import com.github.jinatonic.confetti.confetto.Confetto
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import org.jetbrains.anko.find
import org.skaggsm.kpi3.R
import java.util.*


/**
 * Created by Mitchell on 6/15/2017.
 */

class WeatherCard : AbstractFlexibleItem<WeatherCardViewHolder>(), HasSpanSize {
    override fun getRequestedSize(): Int = 1

    var rainBitmap: Bitmap? = null
    var confettiManager: ConfettiManager? = null

    override fun equals(other: Any?): Boolean = Objects.equals(this, other)

    override fun hashCode(): Int = Objects.hashCode(this)

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: WeatherCardViewHolder, position: Int, payloads: MutableList<Any?>?) {
        val context = adapter.recyclerView.context

        if (rainBitmap == null)
            rainBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.rain)

        holder.card.post {
            val angle = -15
            val angleRads = Math.toRadians(angle.toDouble())
            val speed = 1500

            val yVelocity = Math.cos(angleRads).toFloat() * speed
            val xVelocity = Math.sin(angleRads).toFloat() * speed

            val extraDistanceLeft = Math.max(Math.tan(angleRads) * holder.card.height, 0.toDouble()).toInt()
            val extraDistanceRight = Math.max(Math.tan(Math.PI - angleRads) * holder.card.height, 0.toDouble()).toInt()

            confettiManager = ConfettiManager(
                    context,
                    BitmapConfettoGenerator(rainBitmap!!),
                    ConfettiSource(0 - extraDistanceLeft, 0, holder.card.width + extraDistanceRight, 0),
                    holder.card)
                    .setEmissionDuration(ConfettiManager.INFINITE_DURATION)
                    .setEmissionRate(75F)
                    .setVelocityY(yVelocity, yVelocity * .05F)
                    .setVelocityX(xVelocity, xVelocity * .05F)
                    .setInitialRotation(-angle)
                    .animate()
        }

        holder.darkSky.setOnClickListener {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://darksky.net/poweredby/")))
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

class WeatherCardViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>) : FlexibleViewHolder(view, adapter) {
    val card: CardView = view as CardView
    val weather: TextView = view.find<TextView>(R.id.weather)
    val darkSky: TextView = view.find<TextView>(R.id.dark_sky)
}

class BitmapConfettoGenerator(val bitmap: Bitmap) : ConfettoGenerator {
    override fun generateConfetto(random: Random?): Confetto {
        return BitmapConfetto(bitmap)
    }
}