package hminq.dev.weatherapp.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import hminq.dev.weatherapp.domain.entity.enum.Language
import hminq.dev.weatherapp.domain.entity.enum.SpeedType
import hminq.dev.weatherapp.domain.entity.enum.Temperature
import hminq.dev.weatherapp.domain.entity.enum.Theme
import hminq.dev.weatherapp.presentation.extensions.MessageType
import hminq.dev.weatherapp.presentation.extensions.showMessage
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
                            is SettingUiState.Success -> {
                                syncTogglesWithSettings(state.userSetting)
                            }
                        }
                    }
                }

                // Observe one-time events
                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            is SettingEvent.SaveSuccess -> {
                                binding.cardMessage.showMessage(
                                    message = event.message,
                                    type = MessageType.SUCCESS,
                                    scope = viewLifecycleOwner.lifecycleScope
                                )
                            }
                            is SettingEvent.SaveError -> {
                                binding.cardMessage.showMessage(
                                    message = event.message,
                                    type = MessageType.ERROR,
                                    scope = viewLifecycleOwner.lifecycleScope
                                )
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

        // Sync language display
        binding.includeLanguage.tvLanguageValue.text = when (setting.language) {
            Language.ENGLISH -> getString(R.string.language_english)
            Language.VIETNAMESE -> getString(R.string.language_vietnamese)
        }

        // Sync theme display
        binding.includeTheme.tvThemeValue.text = when (setting.theme) {
            Theme.LIGHT -> getString(R.string.theme_light)
            Theme.DARK -> getString(R.string.theme_dark)
            Theme.SYSTEM -> getString(R.string.theme_system)
        }
    }

    private fun setupListeners() {
        with(binding) {
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            includeContact.itemContact.setOnClickListener {
                findNavController().navigate(R.id.action_settingFragment_to_contactFragment)
            }

            includeLanguage.itemLanguage.setOnClickListener {
                findNavController().navigate(R.id.action_settingFragment_to_languageFragment)
            }

            includeTheme.itemTheme.setOnClickListener {
                findNavController().navigate(R.id.action_settingFragment_to_themeFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}