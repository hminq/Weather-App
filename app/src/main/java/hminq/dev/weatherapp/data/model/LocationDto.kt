package hminq.dev.weatherapp.data.model

import com.squareup.moshi.Json

data class LocationDto(
    val name: String,
    val country: String,
    @Json(name = "tz_id")
    val tzId: String,
    @Json(name = "localtime")
    val localTime: String
)