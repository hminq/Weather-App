package hminq.dev.weatherapp.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.toColorInt
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import hminq.dev.weatherapp.databinding.FragmentLanguageBinding
import hminq.dev.weatherapp.domain.entity.enum.Language
import hminq.dev.weatherapp.presentation.extensions.MessageType
import hminq.dev.weatherapp.presentation.extensions.showMessage
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LanguageFragment : Fragment() {
    private var _binding: FragmentLanguageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LanguageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLanguageBinding.inflate(inflater, container, false)
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
                // Observe UI state to sync language selection
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is LanguageUiState.Success -> {
                                syncLanguageSelection(state.userSetting.language)
                            }
                        }
                    }
                }

                // Observe one-time events
                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            is LanguageEvent.LanguageChanged -> {
                                // Use AppCompatDelegate API for per-app language preferences
                                // This API handles activity recreation automatically when needed
                                applyLanguage(event.language)
                            }
                            is LanguageEvent.SaveSuccess -> {
                                binding.cardMessage.showMessage(
                                    message = event.message,
                                    type = MessageType.SUCCESS,
                                    scope = viewLifecycleOwner.lifecycleScope
                                )
                            }
                            is LanguageEvent.SaveError -> {
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

    private fun syncLanguageSelection(language: Language) {
        with(binding) {
            // Get colors from theme attributes
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

            when (language) { // change the textColor of options & enable Check icon if selected
                Language.ENGLISH -> {
                    // change color
                    includeEnglish.tvLanguageValue.setTextColor(selectedColor)
                    includeEnglish.ivCheck.visibility = View.VISIBLE

                    includeVietnamese.tvLanguageValue.setTextColor(unselectedColor)

                    includeVietnamese.ivCheck.visibility = View.GONE
                }
                Language.VIETNAMESE -> {
                    includeVietnamese.tvLanguageValue.setTextColor(selectedColor)

                    includeVietnamese.ivCheck.visibility = View.VISIBLE

                    includeEnglish.tvLanguageValue.setTextColor(unselectedColor)
                    includeEnglish.ivCheck.visibility = View.GONE
                }
            }
        }
    }

    private fun setupListeners() {
        with(binding) {
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            includeEnglish.itemEnglish.setOnClickListener {
                viewModel.updateLanguage(Language.ENGLISH)
            }

            includeVietnamese.itemVietnamese.setOnClickListener {
                viewModel.updateLanguage(Language.VIETNAMESE)
            }
        }
    }

    /**
     * Apply language using AppCompatDelegate.setApplicationLocales()
     * This is the recommended API for per-app language preferences (Appcompat 1.6.0+)
     * See: https://developer.android.com/guide/topics/resources/app-languages
     */
    private fun applyLanguage(language: Language) {
        val localeTag = when (language) {
            Language.ENGLISH -> "en"
            Language.VIETNAMESE -> "vi"
        }
        val appLocale = LocaleListCompat.forLanguageTags(localeTag)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}