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

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.concurrent.TimeUnit


/**
 * Created by Mitchell on 6/20/2017.
 */
class StartWeatherServiceReceiver : BroadcastReceiver(), AnkoLogger {
    companion object {
        val JOB_ID = 0
    }

    override fun onReceive(context: Context, intent: Intent) {
        info("onReceive called!")

        val serviceComponent = ComponentName(context, WeatherDownloadService::class.java)

        val jobScheduler = context.getSystemService(JobScheduler::class.java)

        jobScheduler.schedule(JobInfo.Builder(JOB_ID, serviceComponent).setPeriodic(TimeUnit.MINUTES.toMillis(15)).build())
    }
}