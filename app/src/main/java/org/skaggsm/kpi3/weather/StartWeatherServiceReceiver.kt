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