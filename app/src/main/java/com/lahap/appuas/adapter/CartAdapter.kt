package com.lahap.appuas.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lahap.appuas.databinding.CartItemBinding

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemPrice: MutableList<String>,
    private val cartImageUris: MutableList<String>, // Renamed to avoid conflict
    private val cartDescription: MutableList<String>,
    private val cartQuantity: MutableList<Int>,
    private val cartIngredient: MutableList<String>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val cartItemsReference: DatabaseReference
    private var itemQuantities: IntArray

    init {
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        itemQuantities = IntArray(cartItems.size) { 1 }
        cartItemsReference = database.reference.child("user").child(userId).child("CartItems")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    fun getUpdatedItemsQuantities(): MutableList<Int> {
        return cartQuantity.toMutableList()
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.apply {
                cartFoodName.text = cartItems[position]
                cartitemPrice.text = cartItemPrice[position]
                cartItemQuantity.text = itemQuantities[position].toString()

                val uri = Uri.parse(cartImageUris[position]) // Correct list reference
                Glide.with(context).load(uri).into(cartImage) // Correctly bind to ImageView

                minusButton.setOnClickListener {
                    decreaseQuantity(position)
                }
                plusbutton.setOnClickListener {
                    increaseQuantity(position)
                }
                deleteButton.setOnClickListener {
                    deleteItem(position)
                }
            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                cartQuantity[position] = itemQuantities[position]
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                cartQuantity[position] = itemQuantities[position]
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun deleteItem(position: Int) {
            if (position !in cartItems.indices) {
                Toast.makeText(context, "Invalid position", Toast.LENGTH_SHORT).show()
                return
            }

            getUniqueKeyAtPosition(position) { uniqueKey ->
                if (uniqueKey != null) {
                    cartItemsReference.child(uniqueKey).removeValue()
                        .addOnSuccessListener {
                            cartItems.removeAt(position)
                            cartImageUris.removeAt(position)
                            cartDescription.removeAt(position)
                            cartQuantity.removeAt(position)
                            cartItemPrice.removeAt(position)
                            cartIngredient.removeAt(position)
                            itemQuantities = itemQuantities.filterIndexed { index, _ -> index != position }.toIntArray()
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, cartItems.size)
                            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    private fun getUniqueKeyAtPosition(position: Int, onComplete: (String?) -> Unit) {
        cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val uniqueKey = snapshot.children.elementAtOrNull(position)?.key
                onComplete(uniqueKey)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Data not fetched: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
