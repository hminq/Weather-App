package hminq.dev.weatherapp.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hminq.dev.weatherapp.databinding.FragmentContactBinding
import hminq.dev.weatherapp.presentation.constants.ContactInfo
import hminq.dev.weatherapp.presentation.extensions.copyToClipboard
import hminq.dev.weatherapp.presentation.extensions.openUrlInApp

@AndroidEntryPoint
class ContactFragment : Fragment() {
    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {
        with(binding) {

            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            includeEmail.itemEmail.setOnClickListener {
                copyEmail()
            }

            includeGithub.itemGithub.setOnClickListener {
                openLink(ContactInfo.GITHUB_URL, ContactInfo.GITHUB_PACKAGE_NAME)
            }

            includeInstagram.itemInstagram.setOnClickListener {
                openLink(ContactInfo.INSTAGRAM_URL, ContactInfo.INSTAGRAM_PACKAGE_NAME)
            }

            includeTwitter.itemTwitter.setOnClickListener {
                openLink(ContactInfo.TWITTER_URL, ContactInfo.TWITTER_PACKAGE_NAME)
            }
        }
    }

    private fun copyEmail() {
        requireContext().copyToClipboard(ContactInfo.EMAIL_ADDRESS)
    }

    private fun openLink(url: String, pkg: String) {
        requireContext().openUrlInApp(url, pkg)
    }
}