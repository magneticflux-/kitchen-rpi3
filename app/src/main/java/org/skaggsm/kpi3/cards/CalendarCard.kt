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

import android.content.Context
import android.support.v7.widget.CardView
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.squareup.timessquare.CalendarCellDecorator
import com.squareup.timessquare.CalendarPickerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.find
import org.skaggsm.kpi3.R
import java.util.*


/**
 * Created by Mitchell on 6/15/2017.
 */

class CalendarCard : AbstractFlexibleItem<CalendarCardViewHolder>(), HasSpanSize, AnkoLogger {
    companion object {
        const val PRIOR_YEAR_SPAN = 1
        const val FUTURE_YEAR_SPAN = 1
    }

    override fun getRequestedSize(): Int = 2

    override fun equals(other: Any?): Boolean = Objects.equals(this, other)

    override fun hashCode(): Int = Objects.hashCode(this)

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: CalendarCardViewHolder, position: Int, payloads: MutableList<Any?>?) {
        val context = adapter.recyclerView.context

        setUpCalendar(context, holder.calendarPickerView)
    }

    private fun setUpCalendar(context: Context, calendarPickerView: CalendarPickerView) {
        val space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 4f, context.displayMetrics).toInt()

        calendarPickerView.setCustomDayView { parent ->
            debug("Initializing DayView")

            parent.addView(
                    LinearLayout(parent.context).apply {
                        orientation = LinearLayout.VERTICAL
                        addView(
                                TextView(ContextThemeWrapper(parent.context, com.squareup.timessquare.R.style.CalendarCell_CalendarDate)).apply {
                                    isDuplicateParentStateEnabled = true
                                    parent.dayOfMonthTextView = this
                                    layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                                        setMargins(space, space, space, space)
                                    }
                                })
                    })
        }

        calendarPickerView.decorators = listOf(
                CalendarCellDecorator { cellView, date ->
                    val cellViewContext = cellView.context
                    debug("Running decorator; Date: $date, isSelectable: ${cellView.isSelectable}, Text: ${((cellView.getChildAt(0) as LinearLayout).getChildAt(0) as TextView).text}")

                    val linearLayout = cellView.getChildAt(0) as LinearLayout

                    val calendar = Calendar.getInstance().apply { time = date }

                    if (calendar.get(Calendar.DAY_OF_MONTH) == 30) {
                        debug("Adding view to LinearLayout with ${linearLayout.childCount} children")

                        linearLayout.addView(
                                CardView(cellViewContext).apply {
                                    layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                                        setMargins(space, space, space, space)
                                    }
                                    radius = space.toFloat()
                                    setCardBackgroundColor(cellViewContext.getColor(R.color.colorPrimary))
                                    addView(
                                            TextView(cellViewContext).apply {
                                                text = "Text: ${(linearLayout.getChildAt(0) as TextView).text}" //"Day: ${calendar.get(Calendar.DAY_OF_MONTH)}, ${cellView.isSelectable}"
                                                setTextAppearance(android.R.style.TextAppearance_Material_Small_Inverse)
                                                layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                                                    setMargins(space, space, space, space)
                                                }
                                            })
                                })

                    }
                })

        val prevYear = Calendar.getInstance().apply { add(Calendar.YEAR, -PRIOR_YEAR_SPAN) }
        val nextYear = Calendar.getInstance().apply { add(Calendar.YEAR, FUTURE_YEAR_SPAN) }
        val today = Calendar.getInstance()

        calendarPickerView.init(prevYear.time, nextYear.time)
        calendarPickerView.scrollToDate(today.time)
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
    val calendarPickerView: CalendarPickerView = view.find<CalendarPickerView>(R.id.calendar_view)
}