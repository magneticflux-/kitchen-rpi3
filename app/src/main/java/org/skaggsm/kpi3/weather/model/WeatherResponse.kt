package org.skaggsm.kpi3.weather.model

import com.squareup.moshi.Json


/**
 * Created by Mitchell on 6/25/2017.
 */
@Suppress("unused")
data class WeatherResponse(
        val response: Response,
        val current_observation: CurrentObservation,
        val forecast: Forecast,
        val hourly_forecast: List<HourlyForecast>
) {

    data class Response(
            val version: String,
            val termsofService: String,
            val features: Features
    ) {

        /**
         * 1 denotes a present feature, 0 denotes an absent feature. values may be missing, so default is 0 (absent).
         */
        data class Features(
                val alerts: Int = 0,
                val almanac: Int = 0,
                val astronomy: Int = 0,
                val conditions: Int = 0,
                val currenthurricane: Int = 0,
                val forecast: Int = 0,
                val forecast10day: Int = 0,
                val geolookup: Int = 0,
                val history: Int = 0,
                val hourly: Int = 0,
                val hourly10day: Int = 0,
                val planner: Int = 0,
                val rawtide: Int = 0,
                val satellite: Int = 0,
                val tide: Int = 0,
                val webcams: Int = 0,
                val yesterday: Int = 0
        )
    }

    data class CurrentObservation(
            val image: Image,
            val display_location: DisplayLocation,
            val observation_location: ObservationLocation,
            val estimated: Estimated,
            val station_id: String,
            val observation_time: String,
            val observation_time_rfc822: String,
            val observation_epoch: Long,
            val weather: String,
            val temperature_string: String,
            val temp_f: Double = 0.0,
            val temp_c: Double = 0.0,
            val relative_humidity: String,
            val wind_string: String,
            val wind_dir: String,
            val wind_degrees: Int = 0,
            val wind_mph: Double = 0.0,
            val wind_gust_mph: Double = 0.0,
            val wind_kph: Double = 0.0,
            val wind_gust_kph: Double = 0.0,
            val pressure_mb: Double = 0.0,
            val pressure_in: Double = 0.0,
            val pressure_trend: String,
            val dewpoint_string: String,
            val dewpoint_f: Int = 0,
            val dewpoint_c: Int = 0,
            val feelslike_string: String,
            val feelslike_f: Double = 0.0,
            val feelslike_c: Double = 0.0,
            val solarradiation: String,
            val UV: Double = 0.0,
            val precip_1hr_string: String,
            val precip_1hr_in: Double = 0.0,
            val precip_1hr_metric: Double = 0.0,
            val precip_today_string: String,
            val precip_today_in: Double = 0.0,
            val precip_today_metric: Double = 0.0
    ) {

        data class Image(
                val url: String,
                val title: String,
                val link: String
        )

        data class DisplayLocation(
                val full: String,
                val city: String,
                val state: String,
                val state_name: String,
                val country: String,
                val country_iso3166: String,
                val zip: String,
                val magic: String,
                val wmo: String,
                val latitude: Double = 0.0,
                val longitude: Double = 0.0,
                val elevation: String
        )

        data class ObservationLocation(
                val full: String,
                val city: String,
                val state: String,
                val country: String,
                val country_iso3166: String,
                val latitude: Double = 0.0,
                val longitude: Double = 0.0,
                val elevation: String
        )

        class Estimated
    }

    data class Forecast(
            val txt_forecast: TxtForecast,
            val simpleforecast: SimpleForecast
    ) {
        data class TxtForecast(
                val date: String,
                val forecastday: List<ForecastDay>
        ) {
            data class ForecastDay(
                    val period: Int = 0,
                    val title: String,
                    val fcttext: String,
                    val fcttext_metric: String,
                    val pop: String
            )
        }

        data class SimpleForecast(
                val forecastday: List<ForecastDay>
        ) {
            data class ForecastDay(
                    val date: Date,
                    val period: Int = 0,
                    val high: Temp,
                    val low: Temp,
                    val conditions: String,
                    val qpf_allday: PrecipMM, //Quantitative Precipitation Forecast
                    val qpf_day: PrecipMM,
                    val qpf_night: PrecipMM,
                    val snow_allday: PrecipCM,
                    val snow_day: PrecipCM,
                    val snow_night: PrecipCM,
                    val maxwind: Wind,
                    val avewind: Wind,
                    val avehumidity: Int = 0,
                    val maxhumidity: Int = 0,
                    val minhumidity: Int = 0
            ) {
                data class Date(
                        val epoch: Long = 0,
                        val pretty: String,
                        val day: Int = 0,
                        val month: Int = 0,
                        val year: Int = 0,
                        val yday: Int = 0,
                        val hour: Int = 0,
                        val min: Int = 0,
                        val sec: Int = 0,
                        val monthname: String,
                        val monthname_short: String,
                        val weekday: String,
                        val weekday_short: String,
                        val ampm: String
                )

                data class Temp(
                        val fahrenheit: Int = 0,
                        val celsius: Int = 0
                )

                data class PrecipMM(
                        @Json(name = "in") val _in: Double?,
                        val mm: Double?
                )

                data class PrecipCM(
                        @Json(name = "in") val _in: Double?,
                        val cm: Double?
                )

                data class Wind(
                        val mph: Int = 0,
                        val kph: Int = 0,
                        val dir: String,
                        val degrees: Int = 0
                )
            }
        }
    }

    data class HourlyForecast(
            val FCTTIME: FctTime,
            val temp: UnitPair,
            val dewpoint: UnitPair,
            val condition: String,
            val wspd: UnitPair, // Wind Speed
            val wdir: WindDir,
            val wx: String, // "Weather"
            val uvi: Int = 0, // Ultraviolet Index
            val humidity: Int = 0,
            val windchill: UnitPair,
            val heatindex: UnitPair,
            val feelslike: UnitPair,
            val qpf: UnitPair,
            val snow: UnitPair,
            val mslp: UnitPair // Mean Sea Level Pressure
    ) {
        data class FctTime(
                val year: Int = 0,
                val yday: Int = 0,
                val mon: Int = 0,
                val mday: Int = 0,
                val hour: Int = 0,
                val min: Int = 0,
                val sec: Int = 0,
                val epoch: Long = 0,
                val pretty: String,
                val month_name: String,
                val weekday_name: String,
                val ampm: String
        )

        data class UnitPair(
                val english: Double = 0.0,
                val metric: Double = 0.0
        )

        data class WindDir(
                val dir: String,
                val degrees: Int = 0
        )
    }
}

