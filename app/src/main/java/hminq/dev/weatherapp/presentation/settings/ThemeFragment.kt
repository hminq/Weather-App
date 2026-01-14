package hminq.dev.weatherapp.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import hminq.dev.weatherapp.databinding.FragmentThemeBinding
import hminq.dev.weatherapp.domain.entity.enum.Theme
import hminq.dev.weatherapp.presentation.extensions.MessageType
import hminq.dev.weatherapp.presentation.extensions.showMessage
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ThemeFragment : Fragment() {
    private var _binding: FragmentThemeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ThemeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is ThemeUiState.Success -> {
                                syncThemeSelection(state.userSetting.theme)
                            }
                        }
                    }
                }

                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            is ThemeEvent.ThemeChanged -> {
                                // Apply theme immediately
                                val currentTheme = when (val state = viewModel.uiState.value) {
                                    is ThemeUiState.Success -> state.userSetting.theme
                                }
                                applyTheme(currentTheme)
                            }
                            is ThemeEvent.SaveSuccess -> {
                                binding.cardMessage.showMessage(
                                    message = event.message,
                                    type = MessageType.SUCCESS,
                                    scope = viewLifecycleOwner.lifecycleScope
                                )
                            }
                            is ThemeEvent.SaveError -> {
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

    private fun syncThemeSelection(theme: Theme) {
        with(binding) {
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

            // Reset all options
            includeLight.tvThemeValue.setTextColor(unselectedColor)
            includeLight.ivCheck.visibility = View.GONE
            includeDark.tvThemeValue.setTextColor(unselectedColor)
            includeDark.ivCheck.visibility = View.GONE
            includeSystem.tvThemeValue.setTextColor(unselectedColor)
            includeSystem.ivCheck.visibility = View.GONE

            // Set selected option
            when (theme) {
                Theme.LIGHT -> {
                    includeLight.tvThemeValue.setTextColor(selectedColor)
                    includeLight.ivCheck.visibility = View.VISIBLE
                }
                Theme.DARK -> {
                    includeDark.tvThemeValue.setTextColor(selectedColor)
                    includeDark.ivCheck.visibility = View.VISIBLE
                }
                Theme.SYSTEM -> {
                    includeSystem.tvThemeValue.setTextColor(selectedColor)
                    includeSystem.ivCheck.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupListeners() {
        with(binding) {
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            includeLight.itemLight.setOnClickListener {
                viewModel.updateTheme(Theme.LIGHT)
            }

            includeDark.itemDark.setOnClickListener {
                viewModel.updateTheme(Theme.DARK)
            }

            includeSystem.itemSystem.setOnClickListener {
                viewModel.updateTheme(Theme.SYSTEM)
            }
        }
    }

    private fun applyTheme(theme: Theme) {
        val mode = when (theme) {
            Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            Theme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}