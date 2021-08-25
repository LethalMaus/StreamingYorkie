package com.lethalmaus.streaming_yorkie.dashboard.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lethalmaus.streaming_yorkie.MainActivity
import com.lethalmaus.streaming_yorkie.R
import com.lethalmaus.streaming_yorkie.dashboard.follow.common.FollowFragment
import com.lethalmaus.streaming_yorkie.databinding.FollowFragmentBinding

class FollowersFragment : FollowFragment() {

    private val viewModel: FollowViewModel by viewModels()
    private var viewBinding: FollowFragmentBinding? = null
    private val binding get() = viewBinding!!

    private lateinit var linearLayoutManager: LinearLayoutManager
    private var debounce: Long = 0

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = FollowFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeFollowers()
        observeErrors()
        observeWrongCount()
    }

    override fun onResume() {
        super.onResume()
        activityBinding.pageTitle.text = getString(R.string.followers)
        activityBinding.pageActionIcon.let { view ->
            view.setImageResource(R.drawable.icon_settings)
            view.setOnClickListener {
                //TODO show follower settings ie quick actions & maybe the service too?
            }
            view.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(requireContext())
        viewBinding!!.recyclerView.layoutManager = linearLayoutManager
        //viewBinding!!.recyclerView.adapter = FAQAdapter(this, topics)
        //viewBinding!!.recyclerView.adapter?.notifyDataSetChanged()
        viewModel.getFollowers(requireActivity())
    }
}