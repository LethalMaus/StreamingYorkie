package com.lethalmaus.streaming_yorkie.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.lethalmaus.streaming_yorkie.MainActivity
import com.lethalmaus.streaming_yorkie.R
import com.lethalmaus.streaming_yorkie.common.BaseFragment
import com.lethalmaus.streaming_yorkie.common.SuccessResponse
import com.lethalmaus.streaming_yorkie.dashboard.follow.FollowersFragment
import com.lethalmaus.streaming_yorkie.dashboard.host.HostFragment
import com.lethalmaus.streaming_yorkie.dashboard.info.InfoFragment
import com.lethalmaus.streaming_yorkie.dashboard.lurk.LurkFragment
import com.lethalmaus.streaming_yorkie.dashboard.multi_view.MultiViewFragment
import com.lethalmaus.streaming_yorkie.dashboard.settings.SettingsFragment
import com.lethalmaus.streaming_yorkie.dashboard.shop.ShopFragment
import com.lethalmaus.streaming_yorkie.dashboard.user.UserInfoFragment
import com.lethalmaus.streaming_yorkie.dashboard.user.UserManager
import com.lethalmaus.streaming_yorkie.dashboard.user.UserSwitchFragment
import com.lethalmaus.streaming_yorkie.dashboard.vods.VodsFragment
import com.lethalmaus.streaming_yorkie.databinding.DashboardFragmentBinding
import com.lethalmaus.streaming_yorkie.dialog.RequestErrorDialog
import com.lethalmaus.streaming_yorkie.repository.Endpoint

class DashboardFragment : BaseFragment() {

    private val viewModel: DashboardViewModel by viewModels()
    private var viewBinding: DashboardFragmentBinding? = null
    private val binding get() = viewBinding!!

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = DashboardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeUser()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val logo = UserManager.getCurrentUserLogo()
        if (logo.isNotEmpty()) {
            loadLogo(logo)
            viewBinding!!.userSwitch.visibility = View.VISIBLE
        }
        viewBinding!!.userName.text = UserManager.getCurrentUserName()
        viewModel.getUser(requireActivity())
        setTileSize()
        viewBinding!!.followers.setOnClickListener { navigate(FollowersFragment()) }
        viewBinding!!.following.setOnClickListener { navigate(FollowersFragment()) }
        viewBinding!!.f4f.setOnClickListener { navigate(FollowersFragment()) }
        viewBinding!!.multiView.setOnClickListener { navigate(MultiViewFragment()) }
        viewBinding!!.vods.setOnClickListener { navigate(VodsFragment()) }
        viewBinding!!.lurk.setOnClickListener { navigate(LurkFragment()) }
        viewBinding!!.host.setOnClickListener { navigate(HostFragment()) }
        viewBinding!!.userInfo.setOnClickListener { navigate(UserInfoFragment()) }
        viewBinding!!.shop.setOnClickListener { navigate(ShopFragment()) }
        viewBinding!!.info.setOnClickListener { navigate(InfoFragment()) }
        viewBinding!!.settings.setOnClickListener { navigate(SettingsFragment()) }
        viewBinding!!.userSwitch.setOnClickListener { navigate(UserSwitchFragment()) }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).binding.pageTitle.text = getString(R.string.dashboard)
    }

    private fun setTileSize() {
        setTileSize(viewBinding!!.followers)
        setTileSize(viewBinding!!.following)
        setTileSize(viewBinding!!.f4f)
        setTileSize(viewBinding!!.multiView)
        setTileSize(viewBinding!!.vods)
        setTileSize(viewBinding!!.lurk)
        setTileSize(viewBinding!!.host)
        setTileSize(viewBinding!!.userInfo)
        setTileSize(viewBinding!!.shop)
        setTileSize(viewBinding!!.info)
        setTileSize(viewBinding!!.settings)
    }

    private fun observeUser() {
        viewModel.userLiveData.observe(this, {
            if (it is SuccessResponse) {
                it.data?.logo?.let { logo ->
                    loadLogo(logo)
                }
                viewBinding!!.userName.text = it.data?.displayName
                viewBinding!!.userSwitch.visibility = View.VISIBLE
                UserManager.setCurrentUserName(it.data?.displayName)
                UserManager.setCurrentUserLogo(it.data?.logo)
            } else {
                logResponse(it)
                showDialog(RequestErrorDialog(Endpoint.KRAKEN_USER))
            }
        })
    }

    private fun loadLogo(logo: String) {
        Glide.with(this)
            .load(logo)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(viewBinding!!.userLogo)
    }
}