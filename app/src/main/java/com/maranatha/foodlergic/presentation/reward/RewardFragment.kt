package com.maranatha.foodlergic.presentation.reward

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.maranatha.foodlergic.R
import com.maranatha.foodlergic.databinding.FragmentRewardBinding
import com.maranatha.foodlergic.domain.models.Book
import com.google.firebase.analytics.FirebaseAnalytics

class RewardFragment : Fragment(R.layout.fragment_reward) {

    private lateinit var binding: FragmentRewardBinding
    private lateinit var bookList: List<Book>
    private lateinit var rewardAdapter: RewardAdapter

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRewardBinding.bind(view)

        // Initialize Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        // Set RecyclerView with GridLayoutManager (2 columns)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns
        binding.recyclerView.setHasFixedSize(true)

        bookList = getListBook()  // Get the list of books
        rewardAdapter = RewardAdapter(bookList)
        binding.recyclerView.adapter = rewardAdapter

        // Handle item click in the RecyclerView
        rewardAdapter.onItemClick = { book ->
            val action = RewardFragmentDirections.actionRewardFragmentToDetailRewardFragment(book)
            findNavController().navigate(action)  // Navigating to the DetailRewardFragment with the selected book

            // Log the event when a user clicks on a book item
            logAnalyticsEvent("reward_item_clicked", "book_title", book.name)
        }
    }

    // Sample list of books
    fun getListBook(): List<Book> {
        return listOf(
            Book(
                R.drawable.a_002_book,
                "Easy Allergy-Free Cooking",
                "a_001_book",
                "https://drive.google.com/file/d/1X0oO8vfNodrtvMzM1AhUupLhfqc187OY/view?usp=sharing",
                2500,
                "In Easy Allergy-Free Cooking, Kayla Cappiello's recipes adhere to any allergies or food intolerances while still providing healthy, flavorful meals. It's all-inclusive, letting the reader choose from a variety of milks, grain substitutes, and meat replacements that work for them. Gluten-free, dairy-free, vegan, and vegetarians—this book welcomes everyone. Kayla’s recipes focus on resourceful ingredient substitutes to satisfy any dietary need so you never have to miss out on your favorite comfort meals while still providing new and innovative recipes to keep things fresh. She includes inventive rice bowls, artisanal cauliflower pizzas, out-of-the-box baked pastas, and one-pan easy weeknight casseroles, while still keeping allergies and healthiness in mind. Featuring sections on how to stock your pantry with healthy options, outlines for food substitutes based on allergies or dietary restrictions, and meal maps, this book is a friendly guide to getting your nutrition on track without cutting out the foods you love. Packed with easy-to-follow diagrams and vibrant photos, you’ll be making these recipes over and over again."
            ),
            Book(
                R.drawable.a_001_book,
                "The Autoimmune Protocol Cookbook",
                "a_002_book",
                "https://drive.google.com/file/d/198glwmmQXSxMjL8rB9y7S2hJOVPJnrM8/view?usp=sharing",
                3100,
                "The Autoimmune Protocol Comfort Food Cookbook features over 100 recipes that are free of gluten, grains, eggs, dairy, nightshades, legumes, seeds, and refined sugars—but still taste like the foods you crave. Including classics you know and love, like cupcakes, lasagna, and french toast, enjoy these amazing dishes with nostalgic family members, picky kids, or oblivious party guests that will be none the wiser! After an introduction to the autoimmune protocol that includes lists of foods to enjoy and avoid, you’ll find recipes for breakfasts, appetizers, soups and salads, crazy good sides, classic Sunday night dinners, holiday favorites, easy one-pan meals, decadent desserts, and more."
            ),
            Book(
                R.drawable.a_003_book,
                "Allergic: A Graphic Novel",
                "a_003_book",
                "https://drive.google.com/file/d/16Sp-uFLHVP-CO3XTwhwOjRPcdz_dQOcf/view?usp=sharing",
                4000,
                "A coming-of-age middle-grade graphic novel featuring a girl with severe allergies who just wants to find the perfect pet!\n" +
                        "At home, Maggie is the odd one out. Her parents are preoccupied with getting ready for a new baby, and her younger brothers are twins and always in their own world. Maggie loves animals and thinks a new puppy is the answer, but when she goes to select one on her birthday, she breaks out in hives and rashes. She's severely allergic to anything with fur! Can Maggie outsmart her allergies and find the perfect pet? With illustrations by Michelle Mee Nutter, Megan Wagner Lloyd draws on her own experiences with allergies to tell a heartfelt story of family, friendship, and finding a place to belong."
            ),
            Book(
                R.drawable.a_004_book,
                "Deep Medicine",
                "a_004_book",
                "https://drive.google.com/file/d/1bAjHSukZbOjAfQBJBfyAMoFj13npOIHT/view?usp=sharing",
                2900,
                "One of America's top doctors reveals how AI will empower physicians and revolutionize patient care Medicine has become inhuman, to disastrous effect. The doctor-patient relationship--the heart of medicine--is broken: doctors are too distracted and overwhelmed to truly connect with their patients, and medical errors and misdiagnoses abound. In Deep Medicine, leading physician Eric Topol reveals how artificial intelligence can help. AI has the potential to transform everything doctors do, from notetaking and medical scans to diagnosis and treatment, greatly cutting down the cost of medicine and reducing human mortality. By freeing physicians from the tasks that interfere with human connection, AI will create space for the real healing that takes place between a doctor who can listen and a patient who needs to be heard."
            ),
            Book(
                R.drawable.a_005_book,
                "Anti-Inflammatory Diet",
                "a_005_book",
                "https://drive.google.com/file/d/1XCuLeMfDH7oWZgagyGOSnPDoW84uA73j/view?usp=sharing",
                3300,
                "The no-stress guide to boosting energy and relieving pain with the anti-inflammatory diet It's possible to reverse chronic inflammation and improve overall health through simple dietary changes, and The Complete Anti-Inflammatory Diet for Beginners breaks the process down into simple, actionable steps that anyone can take―starting today. With easy recipes and weekly shopping lists, this essential anti-inflammatory diet cookbook makes it easy for you to start and follow an anti-inflammatory diet that can help you strengthen your immune system, relieve pain, ease healing, and feel more satisfied and energized every day! Learn the basics of an anti-inflammatory diet―Find guidelines from a registered dietician, including an anti-inflammatory foods list that covers which foods can soothe inflammation and which ones to avoid. Easy meal prep and planning―A 2-week meal plan takes you through exactly what to cook and eat for every meal as you get started, so there's no stress or guesswork. Quick and tasty cooking―Beyond the meal plan, you'll find plenty of healthy recipes to try, most of which only require about 5 main ingredients―from roast chicken with a side of white beans to a hearty lentil and beet salad. Eat and live better with The Complete Anti-Inflammatory Diet for Beginners."
            )
        )
    }

    // Log the event to Firebase Analytics
    private fun logAnalyticsEvent(eventName: String, paramKey: String, paramValue: String) {
        val bundle = Bundle().apply {
            putString(paramKey, paramValue)
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }
}
