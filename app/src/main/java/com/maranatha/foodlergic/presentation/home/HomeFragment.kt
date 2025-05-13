package com.maranatha.foodlergic.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.maranatha.foodlergic.R
import com.maranatha.foodlergic.databinding.FragmentHomeBinding
import com.maranatha.foodlergic.domain.models.LevelInfo
import com.maranatha.foodlergic.domain.models.Book
import com.maranatha.foodlergic.presentation.profile.FoodScanHistoryAdapter
import com.maranatha.foodlergic.presentation.viewmodel.UserProfileViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodScanHistoryAdapter: FoodScanHistoryAdapter
    private lateinit var bookAdapter: BookAdapter

    private val profileViewModel: UserProfileViewModel by viewModels()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        profileViewModel.getUserData()
        profileViewModel.fetchRecentPredictions()

        // Set up BookAdapter
        val books = getBooks()  // Get your book list
        bookAdapter = BookAdapter(books)
        binding.bookhorizontalview.adapter = bookAdapter
        binding.bookhorizontalview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        foodScanHistoryAdapter = FoodScanHistoryAdapter()
        binding.rvFoodScanHistory.adapter = foodScanHistoryAdapter
        binding.rvFoodScanHistory.layoutManager = LinearLayoutManager(context)

        loadUserProfile()
        loadFoodScanHistory()

        // Log navigation to HistoryFoodScanFragment
        binding.foodScanViewAll.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToHistoryFoodScanFragment())
            logAnalyticsEvent("navigate_to_history_food_scan", "action", "clicked")
        }

        // Log navigation to RewardFragment
        binding.rewardViewAll.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRewardFragment())
            logAnalyticsEvent("navigate_to_reward", "action", "clicked")
        }
    }

    private fun getBooks(): List<Book> {
        return listOf(
            Book(image = R.drawable.a_001_book, name = "", code = "B001", urlBook = "url", threshold = 100, summary = "Summary of book 1"),
            Book(image = R.drawable.a_002_book, name = "", code = "B002", urlBook = "url", threshold = 100, summary = "Summary of book 2"),
            Book(image = R.drawable.a_003_book, name = "", code = "B003", urlBook = "url", threshold = 100, summary = "Summary of book 3"),
            Book(image = R.drawable.a_004_book, name = "", code = "B002", urlBook = "url", threshold = 100, summary = "Summary of book 4"),
            Book(image = R.drawable.a_005_book, name = "", code = "B003", urlBook = "url", threshold = 100, summary = "Summary of book 5")
        )
    }

    private fun loadUserProfile() {
        profileViewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.helloText.text = "Hello, ${user.username}"
            val scanCount = user.scanCount
            val levelInfo = getLevelInfo(scanCount)
            val progress = getLevelProgress(scanCount)
            binding.levelProgressBar.max = 100
            binding.levelProgressBar.progress = (progress * 100).toInt()

            binding.rewardProgress.text = "$scanCount / ${levelInfo.maxScan} Points"
            binding.tvLevelTitle.text = levelInfo.name

            // Log level progression event
            logAnalyticsEvent("user_level_progress", "level", levelInfo.name)
        }
    }

    private fun loadFoodScanHistory() {
        profileViewModel.recentPredictions.observe(viewLifecycleOwner) { listFoodScan ->
            Log.d("rezon-dbg", "loadFoodScanHistory: ${listFoodScan.toString()}")
            foodScanHistoryAdapter.submitList(listFoodScan)

            // Log event for loading food scan history
            logAnalyticsEvent("food_scan_history_loaded", "item_count", listFoodScan.size.toString())
        }
    }

    fun getLevelInfo(scanCount: Int): LevelInfo {
        return when {
            scanCount < 100 -> LevelInfo("Rookie", 0, 100)
            scanCount < 1000 -> LevelInfo("Beginner", 100, 1000)
            scanCount < 30000 -> LevelInfo("Explorer", 1000, 30000)
            scanCount < 60000 -> LevelInfo("Expert", 30000, 60000)
            else -> LevelInfo("Master Scanner", 60000, 90000)
        }
    }

    fun getLevelProgress(scanCount: Int): Float {
        val levelInfo = getLevelInfo(scanCount)
        val clamped = (scanCount - levelInfo.minScan).coerceAtLeast(0)
        val total = (levelInfo.maxScan - levelInfo.minScan).toFloat()
        return (clamped / total).coerceIn(0f, 1f)
    }

    // Log Firebase Analytics event
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
