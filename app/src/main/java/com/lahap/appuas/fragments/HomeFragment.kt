package com.lahap.appuas.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lahap.appuas.R
import com.lahap.appuas.adapter.ImageSliderAdapter
import com.lahap.appuas.adapter.MenuAdapter
import com.lahap.appuas.databinding.FragmentHomeBinding
import com.lahap.appuas.models.MenuItem

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Retrieve and display recommended and popular items
        retrieveAndDisplayItems()

        return binding.root
    }

    private fun retrieveAndDisplayItems() {
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menus") // Ensure this matches your Firebase collection
        menuItems = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()
                Log.d("Firebase", "DataSnapshot: $snapshot") // Debug log
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    Log.d("Firebase", "MenuItem: $menuItem") // Debug log
                    menuItem?.let { menuItems.add(it) }
                }
                if (menuItems.isNotEmpty()) {
                    displayRecommendedMenu(menuItems)
                    displayPopularItems(menuItems)
                } else {
                    Log.d("Firebase", "No items found!")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }

    private fun displayRecommendedMenu(menuItems: List<MenuItem>) {
        val recommendedItem = menuItems.random() // Pick one random menu item
        val adapter = MenuAdapter(listOf(recommendedItem), requireContext())
        binding.recommendedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recommendedRecyclerView.adapter = adapter
    }

    private fun displayPopularItems(menuItems: List<MenuItem>) {
        val subsetMenuItems = menuItems.shuffled().take(6) // Take up to 6 random items
        val adapter = MenuAdapter(subsetMenuItems, requireContext())
        binding.PopularRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.PopularRecyclerView.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // List of images for slider
        val images = listOf(
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
        )

        // Set RecyclerView as a slider
        binding.imageSlider.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.imageSlider.adapter = ImageSliderAdapter(images)

        // Auto-scroll with Handler
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            var currentIndex = 0
            override fun run() {
                if (currentIndex == images.size) currentIndex = 0
                binding.imageSlider.smoothScrollToPosition(currentIndex++) // Move to next item
                handler.postDelayed(this, 3000) // Scroll every 3 seconds
            }
        }
        handler.post(runnable)
    }
}
