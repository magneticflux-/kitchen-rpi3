package org.skaggsm.kpi3.cards

import android.support.v7.widget.CardView
import android.view.View
import com.squareup.timessquare.CalendarPickerView
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

class CalendarCard : AbstractFlexibleItem<CalendarCardViewHolder>(), HasSpanSize {
    override fun getRequestedSize(): Int = 2

    override fun equals(other: Any?): Boolean = Objects.equals(this, other)

    override fun hashCode(): Int = Objects.hashCode(this)

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: CalendarCardViewHolder, position: Int, payloads: MutableList<Any?>?) {
        val context = adapter.recyclerView.context

        val nextYear = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }
        val today = Date()

        holder.picker.init(today, nextYear.time)
                .withSelectedDate(today)
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
    val picker: CalendarPickerView = view.find(R.id.calendar_view)
}