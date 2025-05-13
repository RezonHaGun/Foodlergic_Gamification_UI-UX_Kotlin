package com.maranatha.foodlergic.presentation.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.maranatha.foodlergic.R
import com.maranatha.foodlergic.databinding.FragmentUserProfileBinding
import com.maranatha.foodlergic.presentation.predict.PredictResultFragmentDirections
import com.maranatha.foodlergic.presentation.viewmodel.AllergyViewModel
import com.maranatha.foodlergic.presentation.viewmodel.UserProfileViewModel
import com.maranatha.foodlergic.utils.Resource
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllergyViewModel by viewModels()
    private val profileViewModel: UserProfileViewModel by viewModels()

    private lateinit var achievementAdapter: UserProfileAchievementAdapter
    private lateinit var allergyAdapter: UserProfileAllergicAdapter

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        // Initialize Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserProfile()

        profileViewModel.getUserData()
        profileViewModel.getAchievement()
        viewModel.getUserAllergies()

        achievementAdapter = UserProfileAchievementAdapter()
        allergyAdapter = UserProfileAllergicAdapter()
        binding.rvAchievments.layoutManager = GridLayoutManager(context, 3)
        binding.rvAchievments.adapter = achievementAdapter
        binding.selectedAllergiesRecyclerView.layoutManager = GridLayoutManager(context, 4)
        binding.selectedAllergiesRecyclerView.adapter = allergyAdapter
        observeUserAchievement()
        observeGetAllergiesFromAPI()
        observeCLearUserSession()

        binding.tvAchievementEditText.setOnClickListener {
            findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToAchievementFragment())
            logAnalyticsEvent("navigate_achievement", "action", "clicked")
        }
        binding.viewMyalergies.setOnClickListener {
            // Navigate to Manage Allergies Fragment
            findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToManageAllergiesFragment(isProfile = true))
            logAnalyticsEvent("navigate_manage_allergies", "action", "clicked")
        }

        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->
                    profileViewModel.clearSession()
                    logAnalyticsEvent("logout", "status", "clicked")
                }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.user_profile_menu, menu) // Inflating menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                profileViewModel.clearSession()
                logAnalyticsEvent("logout", "status", "clicked")
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun observeGetAllergiesFromAPI() {
        viewModel.userAllergies.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading state
                    logAnalyticsEvent("loading_allergies", "status", "loading")
                }

                is Resource.Success -> {
                    // Extract allergy names into a list
                    allergyAdapter.submitList(result.data)
                    logAnalyticsEvent("allergies_fetched", "status", "success")
                }

                is Resource.Error -> {
                    Log.d("rezon-dbg", "error: ${result.message}")
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                    logAnalyticsEvent("allergies_fetched", "status", "error")
                }
            }
        }
    }

    private fun observeCLearUserSession() {
        profileViewModel.clearUserStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading state
                }

                is Resource.Success -> {
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    logAnalyticsEvent("user_logout_success", "status", "success")

                    findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToLoginFragment())
                }

                is Resource.Error -> {
                    Log.d("rezon-dbg", "error: ${result.message}")
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                    logAnalyticsEvent("user_logout_error", "status", "error")
                }
            }
        }
    }

    private fun observeUserAchievement() {
        profileViewModel.userAchievements.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading state
                }

                is Resource.Success -> {
                    achievementAdapter.submitList(result.data)
                    logAnalyticsEvent("achievements_fetched", "status", "success")
                }

                is Resource.Error -> {
                    Log.d("rezon-dbg", "error: ${result.message}")
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                    logAnalyticsEvent("achievements_fetched", "status", "error")
                }
            }
        }
    }

    private fun loadUserProfile() {
        profileViewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.usernameText.text = user.username
            binding.tvLevelTitle.text = user.level
            logAnalyticsEvent("user_profile_loaded", "status", "success")
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
