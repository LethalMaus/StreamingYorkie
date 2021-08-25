package com.lethalmaus.streaming_yorkie.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import com.lethalmaus.streaming_yorkie.MainActivity
import com.lethalmaus.streaming_yorkie.R
import com.lethalmaus.streaming_yorkie.common.BaseFragment
import com.lethalmaus.streaming_yorkie.common.SuccessResponse
import com.lethalmaus.streaming_yorkie.dashboard.DashboardFragment
import com.lethalmaus.streaming_yorkie.dashboard.user.UserManager
import com.lethalmaus.streaming_yorkie.databinding.LoginFragmentBinding
import com.lethalmaus.streaming_yorkie.dialog.*
import com.lethalmaus.streaming_yorkie.intro.DashboardIntroFragment
import com.lethalmaus.streaming_yorkie.intro.IntroManager
import com.lethalmaus.streaming_yorkie.repository.models.TokenType

class LoginFragment(private val initialLogin: Boolean = false) : BaseFragment() {

    private val viewModel: LoginViewModel by viewModels()
    private var viewBinding: LoginFragmentBinding? = null
    private val binding get() = viewBinding!!

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeClientTokenValidation()
        observeWebsiteTokenValidation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (hasInternetAccess()) {
            setWebView()
        } else {
            showDialog(NoInternetDialog())
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).binding.pageHeader.visibility = View.GONE
    }

    private fun observeClientTokenValidation() {
        viewModel.clientValidationLiveData.observe(this, {
            if (it is SuccessResponse) {
                UserManager.setTwitchToken(it.data)
                viewBinding!!.authWebView.loadUrl(getString(R.string.twitch_url_twitch_oauth2))
            } else {
                showDialog(TokenValidationErrorDialog(TokenType.CLIENT))
            }
        })
    }

    private fun observeWebsiteTokenValidation() {
        viewModel.websiteValidationLiveData.observe(this, {
            if (it is SuccessResponse) {
                UserManager.setTwitchWebsiteToken(it.data)
                handleNavigation()
            } else {
                showDialog(TokenValidationErrorDialog(TokenType.WEBSITE))
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView() {
        viewBinding!!.authWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                if (url.contains(getString(R.string.twitch_url))) {
                    return handleTwitchUrl(url)
                } else if (url.contains(getString(R.string.twitch_access_denied))) {
                    showDialog(AccessDeniedDialog())
                }
                return true
            }

            private fun handleTwitchUrl(url: String) : Boolean {
                if (url.contains(getString(R.string.twitch_url_no_reload))) {
                    handleNavigation()
                }
                if (url.contains(getString(R.string.twitch_url_passport_callback))) {
                    val accessToken = getString(R.string.twitch_url_parameter_access_token)
                    viewModel.validateWebsiteToken(
                        requireContext(),
                        url.substring(
                            url.indexOf(accessToken) + 13,
                            url.indexOf(accessToken) + 43
                        )
                    )
                }
                viewBinding!!.authWebView.loadUrl(url)
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (url.contains(getString(R.string.localhost))
                    && url.contains(getString(R.string.twitch_url_parameter_access_token))
                    && !url.contains(getString(R.string.twitch_url))) {
                    val accessToken = getString(R.string.twitch_url_parameter_access_token)
                    viewModel.validateClientToken(
                        requireContext(),
                        url.substring(
                            url.indexOf(accessToken) + 13,
                            url.indexOf(accessToken) + 43
                        )
                    )
                } else if (!url.contains(getString(R.string.twitch_url))) {
                    showDialog(NonTwitchWebsiteDialog())
                }
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (request.isForMainFrame) {
                    showDialog(LoginErrorDialog())
                }
                super.onReceivedError(view, request, error)
            }
        }
        viewBinding!!.authWebView.settings.javaScriptEnabled = true
        viewBinding!!.authWebView.loadUrl(getString(R.string.twitch_url_client_oauth2))
    }

    private fun handleNavigation() {
        when {
            !IntroManager.hasShownDashboardIntro(requireContext()) -> navigate(DashboardIntroFragment())
            initialLogin -> navigate(DashboardFragment())
            else -> parentFragmentManager.popBackStack()
        }
    }
}