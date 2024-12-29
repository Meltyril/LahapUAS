package com.lahap.appuas.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
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

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance()
        menuItems = mutableListOf()

        // Load data dari Firebase
        loadMenuData()

        // Atur Image Slider
        setupImageSlider()

        return binding.root
    }

    private fun loadMenuData() {
        val foodRef: DatabaseReference = database.reference.child("menus")
        foodRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()
                for (child in snapshot.children) {
                    val menuItem = child.getValue(MenuItem::class.java)
                    menuItem?.let { menuItems.add(it) }
                }

                // Tampilkan data
                setupRecommendedRecyclerView(menuItems)
                setupPopularRecyclerView(menuItems)
            }

            override fun onCancelled(error: DatabaseError) {
                // Log atau tampilkan error
            }
        })
    }

    private fun setupRecommendedRecyclerView(items: List<MenuItem>) {
        val recommendedItems = items.shuffled().take(3) // 3 item acak
        val adapter = MenuAdapter(recommendedItems, requireContext())
        binding.recommendedRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recommendedRecyclerView.adapter = adapter
    }

    private fun setupPopularRecyclerView(items: List<MenuItem>) {
        val popularItems = items.shuffled().take(6) // 6 item acak
        val adapter = MenuAdapter(popularItems, requireContext())
        binding.PopularRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.PopularRecyclerView.adapter = adapter
    }

    private fun setupImageSlider() {
        val images = listOf(
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
        )

        val adapter = ImageSliderAdapter(images)
        binding.imageSlider.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.imageSlider.adapter = adapter

        // Auto-scroll
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            var index = 0
            override fun run() {
                if (index == images.size) index = 0
                binding.imageSlider.smoothScrollToPosition(index++)
                handler.postDelayed(this, 3000)
            }
        }
        handler.post(runnable)
    }
}
