package com.lahap.appuas.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lahap.appuas.R // Import for the default placeholder image
import com.lahap.appuas.databinding.MenuItemBinding
import com.lahap.appuas.models.MenuItem

class MenuAdapter(
    private var menuItems: List<MenuItem>,
    private val context: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val menuItem = menuItems[position]
            binding.apply {
                menuFoodName.text = menuItem.foodName ?: "Unknown Name"
                menuFoodDescription.text = menuItem.foodDescription ?: "No description"
                menuPrice.text = menuItem.foodPrice ?: "0"

                // Handle missing or invalid foodImage values
                if (!menuItem.foodImage.isNullOrEmpty()) {
                    try {
                        val uri = Uri.parse(menuItem.foodImage)
                        Glide.with(context)
                            .load(uri)
                            .placeholder(R.drawable.ic_default_image) // Default placeholder while loading
                            .into(menuImage)
                    } catch (e: Exception) {
                        // Fallback for invalid image URI
                        menuImage.setImageResource(R.drawable.ic_default_image)
                    }
                } else {
                    // Fallback for null or empty foodImage
                    menuImage.setImageResource(R.drawable.ic_default_image)
                }
            }
        }
    }
}
