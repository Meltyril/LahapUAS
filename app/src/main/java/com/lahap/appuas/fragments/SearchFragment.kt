package com.lahap.appuas.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.lahap.appuas.adapter.MenuAdapter
import com.lahap.appuas.databinding.FragmentSearchBinding
import com.lahap.appuas.models.MenuItem

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val db = FirebaseFirestore.getInstance()
    private val menuList = mutableListOf<MenuItem>()
    private val filteredList = mutableListOf<MenuItem>()
    private lateinit var menuAdapter: MenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Setup RecyclerView
        menuAdapter = MenuAdapter(filteredList, requireContext())
        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResults.adapter = menuAdapter

        // Fetch menu items and setup SearchView
        fetchMenuItems()
        setupSearchView()

        return binding.root
    }

    private fun fetchMenuItems() {
        db.collection("menus")
            .get()
            .addOnSuccessListener { result ->
                menuList.clear()
                filteredList.clear()
                for (document in result) {
                    val menuItem = document.toObject(MenuItem::class.java)
                    menuList.add(menuItem)
                }
                filteredList.addAll(menuList)
                menuAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load menus: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterMenu(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMenu(newText)
                return true
            }
        })
    }

    private fun filterMenu(query: String?) {
        filteredList.clear()
        val searchText = query?.lowercase() ?: ""
        if (searchText.isEmpty()) {
            filteredList.addAll(menuList)
        } else {
            menuList.forEach { menuItem ->
                if (menuItem.foodName?.lowercase()?.contains(searchText) == true ||
                    menuItem.foodDescription?.lowercase()?.contains(searchText) == true
                ) {
                    filteredList.add(menuItem)
                }
            }
        }
        menuAdapter.notifyDataSetChanged()
    }
}
