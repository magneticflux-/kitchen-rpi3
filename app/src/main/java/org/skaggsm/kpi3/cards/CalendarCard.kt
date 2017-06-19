package org.skaggsm.kpi3.cards

import android.support.v7.widget.CardView
import android.view.View
import com.example.androidthings.myproject.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import java.util.*

/**
 * Created by Mitchell on 6/15/2017.
 */

class CalendarCard : AbstractFlexibleItem<CalendarCardViewHolder>(), HasSpanSize {
    override fun getRequestedSize(): Int = 3

    override fun equals(other: Any?): Boolean = Objects.equals(this, other)

    override fun hashCode(): Int = Objects.hashCode(this)

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: CalendarCardViewHolder, position: Int, payloads: MutableList<Any?>?) {
        val context = adapter.recyclerView.context
    }

    override fun unbindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: CalendarCardViewHolder?, position: Int) {
        super.unbindViewHolder(adapter, holder, position)
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): CalendarCardViewHolder {
        return CalendarCardViewHolder(view, adapter)
    }

    override fun getLayoutRes(): Int = R.layout.item_calendar_card
}

class CalendarCardViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>) : FlexibleViewHolder(view, adapter) {
    val card: CardView = view as CardView
}