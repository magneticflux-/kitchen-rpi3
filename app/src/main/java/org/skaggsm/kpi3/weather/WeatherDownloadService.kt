package org.skaggsm.kpi3.weather

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import okhttp3.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.skaggsm.kpi3.BuildConfig.API_KEY
import java.io.IOException

/**
 * Created by Mitchell on 6/20/2017.
 */
class WeatherDownloadService : JobService(), AnkoLogger, Callback {

    private var params: JobParameters? = null

    companion object {
        const val FEATURES = "conditions/forecast/hourly"
        const val QUERY = "MO/Saint_Charles.json"
    }

    override fun onFailure(call: Call, e: IOException) {
        info("Failure! Exception: $e")
        jobFinished(params, true)
        params = null
    }

    override fun onResponse(call: Call, response: Response) {
        info("Success! Response: ${response.body()!!.string()}")
        jobFinished(params, false)
        params = null
    }

    override fun onStopJob(params: JobParameters): Boolean {
        info("Job stopped!")
        return true
    }

    override fun onStartJob(params: JobParameters): Boolean {
        info("Job started!")

        val client: OkHttpClient = OkHttpClient()

        val request: Request = Request.Builder()
                .url(HttpUrl.parse("http://api.wunderground.com/api/$API_KEY/$FEATURES/q/$QUERY"))
                .build()

        this.params = params
        client.newCall(request).enqueue(this)

        return true
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_REDELIVER_INTENT
    }
}