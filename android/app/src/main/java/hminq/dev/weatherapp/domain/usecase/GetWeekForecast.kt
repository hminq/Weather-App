package hminq.dev.weatherapp.domain.usecase

import hminq.dev.weatherapp.domain.entity.DayForecast
import hminq.dev.weatherapp.domain.entity.ForecastItem
import hminq.dev.weatherapp.domain.exception.DomainException
import hminq.dev.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject

/**
 * UseCase to get week forecast (Mon-Sun)
 * Contains all business logic for date range calculation, data combination, and formatting
 */
class GetWeekForecast @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): List<ForecastItem> = coroutineScope {
        // Get forecast first to obtain timezone
        val forecastData = weatherRepository.getForecast(lat, lon, days = 7)
        val timeZone = ZoneId.of(forecastData.timeZoneId)

        // Calculate week range in location's timezone
        val today = LocalDate.now(timeZone)
        val mondayOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sundayOfWeek = mondayOfWeek.plusDays(6)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Fetch history for past days (Monday to yesterday) in parallel
        val historyDeferred = (0 until today.dayOfWeek.value - 1).map { offset ->
            val date = mondayOfWeek.plusDays(offset.toLong())
            async {
                try {
                    weatherRepository.getHistory(lat, lon, date.format(dateFormatter))
                } catch (e: DomainException) {
                    // Skip history item if error occurs (e.g., network error, no data available)
                    null
                }
            }
        }

        // Map forecast days (today onwards within the week)
        val forecastItems = forecastData.days.mapNotNull { dayWithHours ->
            val date = Instant.ofEpochSecond(dayWithHours.day.dateEpoch)
                .atZone(timeZone)
                .toLocalDate()

            if (date in today..sundayOfWeek) {
                dayWithHours.day.toForecastItem(timeZone, isToday = date == today)
            } else null
        }

        // Await history results and map to ForecastItems
        val historyItems = historyDeferred.awaitAll()
            .filterNotNull()
            .map { it.toForecastItem(timeZone, isToday = false) }

        // Combine and sort by day of week order
        (historyItems + forecastItems).sortedBy { getDayOfWeekOrder(it.label) }
    }

    private fun DayForecast.toForecastItem(timeZone: ZoneId, isToday: Boolean): ForecastItem {
        val dayOfWeek = Instant.ofEpochSecond(dateEpoch)
            .atZone(timeZone)
            .dayOfWeek
        return ForecastItem(
            label = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
            condition = condition,
            tempC = avgTempC,
            tempF = avgTempF,
            isToday = isToday
        )
    }

    private fun getDayOfWeekOrder(label: String): Int = when (label) {
        "Mon" -> 1
        "Tue" -> 2
        "Wed" -> 3
        "Thu" -> 4
        "Fri" -> 5
        "Sat" -> 6
        "Sun" -> 7
        else -> 8
    }
}
