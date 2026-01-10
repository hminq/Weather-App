package hminq.dev.weatherapp.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hminq.dev.weatherapp.R
import hminq.dev.weatherapp.databinding.FragmentSettingBinding
import hminq.dev.weatherapp.domain.entity.UserSetting
import hminq.dev.weatherapp.domain.entity.enum.SpeedType
import hminq.dev.weatherapp.domain.entity.enum.Temperature
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToggles()
        setupListeners()
        observeViewModel()
    }

    private fun setupToggles() {
        with(binding) {
            // Wind Speed Toggle
            includeWindspeed.toggleWindSpeed.apply {
                setOptions(
                    getString(R.string.unit_kmh),
                    getString(R.string.unit_mph)
                )
                setOnToggleListener { isLeft ->
                    val speedType = if (isLeft) SpeedType.KMH else SpeedType.MPH
                    viewModel.updateWindSpeedUnit(speedType)
                }
            }

            // Temperature Toggle
            includeTemperature.toggleTemperature.apply {
                setOptions(
                    getString(R.string.unit_celsius),
                    getString(R.string.unit_fahrenheit)
                )
                setOnToggleListener { isLeft ->
                    val tempUnit = if (isLeft) Temperature.CELSIUS else Temperature.FAHRENHEIT
                    viewModel.updateTemperatureUnit(tempUnit)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe UI state to sync toggle positions
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is SettingViewModel.SettingUiState.Success -> {
                                syncTogglesWithSettings(state.userSetting)
                            }
                            is SettingViewModel.SettingUiState.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    state.exception.message ?: "Error loading settings",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is SettingViewModel.SettingUiState.Loading -> {
                                // Could show loading indicator if needed
                            }
                        }
                    }
                }

                // Observe one-time events
                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            is SettingViewModel.SettingEvent.SaveSuccess -> {
                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                            }
                            is SettingViewModel.SettingEvent.SaveError -> {
                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun syncTogglesWithSettings(setting: UserSetting) {
        // Sync wind speed toggle
        binding.includeWindspeed.toggleWindSpeed.setSelectedPosition(
            isLeft = setting.windSpeedType == SpeedType.KMH,
            animate = false
        )

        // Sync temperature toggle
        binding.includeTemperature.toggleTemperature.setSelectedPosition(
            isLeft = setting.temperature == Temperature.CELSIUS,
            animate = false
        )
    }

    private fun setupListeners() {
        with(binding) {
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            includeContact.itemContact.setOnClickListener {
                findNavController().navigate(R.id.action_settingFragment_to_contactFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}