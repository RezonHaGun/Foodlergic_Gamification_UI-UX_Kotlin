package com.maranatha.foodlergic.presentation.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.maranatha.foodlergic.databinding.FragmentLeaderboardBinding
import com.maranatha.foodlergic.presentation.viewmodel.LeaderboardViewModel
import com.maranatha.foodlergic.utils.Resource
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaderboardFragment : Fragment() {
    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LeaderboardViewModel by viewModels()
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private lateinit var leaderboardAdaptertop3: LeaderboardAdapterTop3

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        leaderboardAdaptertop3 = LeaderboardAdapterTop3()
        binding.leaderboardRecyclerViewTop3.layoutManager = LinearLayoutManager(requireContext())
        binding.leaderboardRecyclerViewTop3.adapter = leaderboardAdaptertop3

        leaderboardAdapter = LeaderboardAdapter()
        binding.leaderboardRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.leaderboardRecyclerView.adapter = leaderboardAdapter

        observeLeaderboard()
        viewModel.fetchLeaderboard()
    }

    private fun observeLeaderboard() {
        viewModel.leaderboardItems.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading state
                    logAnalyticsEvent("loading_leaderboard", "status", "loading")
                }
                is Resource.Success -> {

                    val topThreeUsers: MutableList<LeaderboardItem> = ArrayList()
                    val normalUsers: MutableList<LeaderboardItem> = ArrayList()

                    val leaderboardme = result.data
                    if (!leaderboardme.isNullOrEmpty()) {
                        for (i in leaderboardme.indices) {
                            if (i < 3) {
                                topThreeUsers.add(leaderboardme[i])
                            } else {
                                normalUsers.add(leaderboardme[i])
                            }
                        }
                    }
                    leaderboardAdaptertop3.submitList(topThreeUsers)
                    leaderboardAdapter.submitList(normalUsers)

                    // Log success event
                    logAnalyticsEvent("leaderboard_success", "status", "success")
                }
                is Resource.Error -> {
                    // Show error message
                    logAnalyticsEvent("loading_leaderboard", "status", "error")
                }
            }
        }
    }

    private fun logAnalyticsEvent(eventName: String, paramKey: String, paramValue: String) {
        val bundle = Bundle().apply {
            putString(paramKey, paramValue)
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
