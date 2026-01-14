package hminq.dev.weatherapp.presentation.weather

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import hminq.dev.weatherapp.R
import hminq.dev.weatherapp.databinding.FragmentWeatherBinding
import hminq.dev.weatherapp.domain.entity.CurrentWeather
import hminq.dev.weatherapp.domain.entity.UserSetting
import hminq.dev.weatherapp.domain.entity.enum.Temperature
import hminq.dev.weatherapp.presentation.extensions.formatHumidity
import hminq.dev.weatherapp.presentation.extensions.formatRain
import hminq.dev.weatherapp.presentation.extensions.formatTemperature
import hminq.dev.weatherapp.presentation.extensions.formatWindSpeed
import hminq.dev.weatherapp.presentation.extensions.getDisplayText
import hminq.dev.weatherapp.presentation.extensions.getIconResource
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class WeatherFragment : Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()
    private lateinit var forecastAdapter: ForecastAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        initListeners()
        setupDefaultUI()
        observeViewModel()
    }

    private fun setupDefaultUI() {
        with(binding) {
            // Set default values for all fields
            tvCountry.text = "--"
            tvCity.text = "--"
            tvCurrentTime.text = "--"
            tvCurrentCondition.text = "--"
            tvCurrentTemp.text = "--°C"
            includeWindSpeed.tvWindSpeed.text = "--"
            includeHumidity.tvHumidity.text = "--"
            includeRain.tvRain.text = "--"
        }
    }

    private fun setupRecyclerView() {
        val itemSpacing = resources.getDimensionPixelSize(R.dimen.very_close_margin)
        val itemWidthPx = resources.getDimensionPixelSize(R.dimen.hourly_item_size)
        val itemWidthDp = (itemWidthPx / resources.displayMetrics.density).toInt()
        forecastAdapter = ForecastAdapter(itemSpacing = itemSpacing)
        forecastAdapter.setTemperatureUnit(Temperature.CELSIUS) // Set default unit
        forecastAdapter.setLoading(true, 4) // Show 4 loading items initially
        binding.rvWeatherPrediction.apply {
            adapter = forecastAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            // Add ItemDecoration for proper spacing distribution
            addItemDecoration(
                ForecastItemDecoration(
                    itemSpacing = itemSpacing,
                    itemWidthDp = itemWidthDp
                )
            )
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: WeatherUiState) {
        with(binding) {
            // Update weather data if available
            if (state.isLoading) {
                // Show default/loading state
                setupDefaultUI()
            } else {
                state.currentWeather?.let { weather ->
                    bindCurrentWeather(weather, state.userSetting)
                } ?: run {
                    // If no data but not loading, show default
                    setupDefaultUI()
                }
            }

            // Update chart with hourly data or default
            if (state.isLoading || state.hourlyForecast.isEmpty()) {
                weatherChart.setDefaultData(state.userSetting.temperature)
            } else {
                weatherChart.setData(
                    data = state.hourlyForecast,
                    timeZoneId = state.hourlyTimeZoneId,
                    unit = state.userSetting.temperature
                )

                // Set current hour based on location's timezone
                val locationZone = ZoneId.of(state.hourlyTimeZoneId)
                val now = ZonedDateTime.now(locationZone)
                weatherChart.setSelectedHour(now.hour, now.minute)
            }

            // Update forecast RecyclerView based on loading state and selected tab
            forecastAdapter.setTemperatureUnit(state.userSetting.temperature)

            if (state.isForecastLoading || state.isLoading) {
                // Show loading skeleton (4 items for Tomorrow, 7 for This Week)
                val loadingCount = when (state.selectedTab) {
                    ForecastTab.TOMORROW -> 4
                    ForecastTab.THIS_WEEK -> 7
                }
                forecastAdapter.setLoading(true, loadingCount)
            } else {
                forecastAdapter.setLoading(false)
                val forecastData = when (state.selectedTab) {
                    ForecastTab.TOMORROW -> state.tomorrowForecast
                    ForecastTab.THIS_WEEK -> state.weekForecast
                }
                forecastAdapter.submitList(forecastData)
            }

            // Update tab styling
            updateTabStyle(state.selectedTab)
        }
    }

    private fun updateTabStyle(selectedTab: ForecastTab) {
        val selectedColor = MaterialColors.getColor(
            requireContext(),
            com.google.android.material.R.attr.colorOnSurface,
            "#09090B".toColorInt()
        )
        val unselectedColor = MaterialColors.getColor(
            requireContext(),
            com.google.android.material.R.attr.colorOnSurfaceVariant,
            "#71717A".toColorInt()
        )

        with(binding) {
            when (selectedTab) {
                ForecastTab.TOMORROW -> {
                    tvTomorrow.apply {
                        setTextColor(selectedColor)
                        typeface = Typeface.create(typeface, Typeface.BOLD)
                    }
                    tvThisWeek.apply {
                        setTextColor(unselectedColor)
                        typeface = Typeface.create(typeface, Typeface.NORMAL)
                    }
                }
                ForecastTab.THIS_WEEK -> {
                    tvThisWeek.apply {
                        setTextColor(selectedColor)
                        typeface = Typeface.create(typeface, Typeface.BOLD)
                    }
                    tvTomorrow.apply {
                        setTextColor(unselectedColor)
                        typeface = Typeface.create(typeface, Typeface.NORMAL)
                    }
                }
            }
        }
    }

    private fun bindCurrentWeather(weather: CurrentWeather, setting: UserSetting) {
        with(binding) {
            // Location
            tvCountry.text = weather.country
            tvCity.text = weather.city

            // Date/Time - format localTime
            tvCurrentTime.text = formatLocalTime(weather.localTime)

            // Condition
            tvCurrentCondition.text = weather.condition.getDisplayText()
            ivCondition.setImageResource(weather.condition.getIconResource())

            // Temperature
            tvCurrentTemp.text = weather.formatTemperature(setting.temperature)

            // Wind speed
            includeWindSpeed.tvWindSpeed.text = weather.formatWindSpeed(setting.windSpeedType)

            // Humidity
            includeHumidity.tvHumidity.text = weather.formatHumidity()

            // Rain
            includeRain.tvRain.text = weather.formatRain()
        }
    }

    private fun formatLocalTime(localTime: String): String {
        return try {
            // localTime format
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault())
            val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
            val dateTime = LocalDateTime.parse(localTime, inputFormatter)
            dateTime.format(outputFormatter)
        } catch (_: Exception) {
            localTime
        }
    }



    private fun initListeners() {
        with(binding) {
            btnSetting.setOnClickListener {
                findNavController().navigate(R.id.action_weatherFragment_to_settingFragment)
            }

            tvTomorrow.setOnClickListener {
                viewModel.selectTab(ForecastTab.TOMORROW)
            }

            tvThisWeek.setOnClickListener {
                viewModel.selectTab(ForecastTab.THIS_WEEK)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}