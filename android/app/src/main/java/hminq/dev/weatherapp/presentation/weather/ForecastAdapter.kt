package hminq.dev.weatherapp.presentation.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hminq.dev.weatherapp.R
import hminq.dev.weatherapp.databinding.ItemHourlyWeatherBinding
import hminq.dev.weatherapp.databinding.ItemHourlyWeatherLoadingBinding
import hminq.dev.weatherapp.databinding.ItemHourlyWeatherTodayBinding
import hminq.dev.weatherapp.domain.entity.ForecastItem
import hminq.dev.weatherapp.domain.entity.enum.Condition
import hminq.dev.weatherapp.domain.entity.enum.Temperature

class ForecastAdapter(
    private val itemSpacing: Int = 0
) : ListAdapter<ForecastItem, RecyclerView.ViewHolder>(DiffCallback) {

    private var temperatureUnit: Temperature = Temperature.CELSIUS
    private var isLoading = false
    private var loadingItemCount = 4

    fun setTemperatureUnit(unit: Temperature) {
        if (temperatureUnit != unit) {
            temperatureUnit = unit
            if (!isLoading) notifyDataSetChanged()
        }
    }

    fun setLoading(loading: Boolean, itemCount: Int = 4) {
        if (isLoading != loading || loadingItemCount != itemCount) {
            isLoading = loading
            loadingItemCount = itemCount
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading) loadingItemCount else super.getItemCount()
    }

    override fun getItemViewType(position: Int): Int {
        if (isLoading) return VIEW_TYPE_LOADING
        val item = getItem(position)
        return if (item.isToday) VIEW_TYPE_TODAY else VIEW_TYPE_DATA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                LoadingViewHolder(ItemHourlyWeatherLoadingBinding.inflate(inflater, parent, false))
            }
            VIEW_TYPE_TODAY -> {
                TodayViewHolder(ItemHourlyWeatherTodayBinding.inflate(inflater, parent, false))
            }
            else -> {
                ForecastViewHolder(ItemHourlyWeatherBinding.inflate(inflater, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ForecastViewHolder -> holder.bind(getItem(position), temperatureUnit)
            is TodayViewHolder -> holder.bind(getItem(position), temperatureUnit)
            is LoadingViewHolder -> { /* Skeleton already has placeholder text */ }
        }
    }

    class ForecastViewHolder(
        private val binding: ItemHourlyWeatherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ForecastItem, unit: Temperature) {
            binding.tvTime.text = item.label
            binding.ivCondition.setImageResource(getConditionIcon(item.condition))
            binding.tvTemperature.text = formatTemperature(item, unit)
        }
    }

    class TodayViewHolder(
        private val binding: ItemHourlyWeatherTodayBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ForecastItem, unit: Temperature) {
            binding.tvTime.text = item.label
            binding.ivCondition.setImageResource(getConditionIcon(item.condition))
            binding.tvTemperature.text = formatTemperature(item, unit)
        }
    }

    class LoadingViewHolder(
        binding: ItemHourlyWeatherLoadingBinding
    ) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_DATA = 1
        private const val VIEW_TYPE_TODAY = 2

        private val DiffCallback = object : DiffUtil.ItemCallback<ForecastItem>() {
            override fun areItemsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
                return oldItem.label == newItem.label
            }

            override fun areContentsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
                return oldItem == newItem
            }
        }

        private fun getConditionIcon(condition: Condition): Int {
            return when (condition) {
                Condition.CLEAR_DAY -> R.drawable.ic_clear_day
                Condition.CLEAR_NIGHT -> R.drawable.ic_clear_night
                Condition.CLOUDY -> R.drawable.ic_cloudy
                Condition.RAIN -> R.drawable.ic_rain
                Condition.SNOW -> R.drawable.ic_snow
                Condition.ICE -> R.drawable.ic_ice
                Condition.THUNDER -> R.drawable.ic_thunder
                Condition.FOG -> R.drawable.ic_fog
                Condition.UNKNOWN -> R.drawable.ic_cloudy
            }
        }

        private fun formatTemperature(item: ForecastItem, unit: Temperature): String {
            return if (unit == Temperature.CELSIUS) {
                "${item.tempC.toInt()}°C"
            } else {
                "${item.tempF.toInt()}°F"
            }
        }
    }
}
