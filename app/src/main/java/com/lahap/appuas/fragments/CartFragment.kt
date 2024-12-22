package com.lahap.appuas.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lahap.appuas.activities.PayOutActivity
import com.lahap.appuas.adapter.CartAdapter
import com.lahap.appuas.databinding.FragmentCartBinding
import com.lahap.appuas.models.CartItems

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDescriptions: MutableList<String>
    private lateinit var foodImagesUri: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var quantity: MutableList<Int>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        setupRecyclerView()
        retrieveCartItems()

        binding.proceedbutton.setOnClickListener {
            getOrderItemsDetails()
        }
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.cartRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun retrieveCartItems() {
        val foodReference: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")

        // Initialize lists
        foodNames = mutableListOf()
        foodPrices = mutableListOf()
        foodDescriptions = mutableListOf()
        foodImagesUri = mutableListOf()
        foodIngredients = mutableListOf()
        quantity = mutableListOf()

        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val cartItem = foodSnapshot.getValue(CartItems::class.java)

                    cartItem?.let {
                        it.foodName?.let { name -> foodNames.add(name) }
                        it.foodPrice?.let { price -> foodPrices.add(price) }
                        it.foodDescription?.let { desc -> foodDescriptions.add(desc) }
                        it.foodImage?.let { image -> foodImagesUri.add(image) }
                        it.foodIngredients?.let { ing -> foodIngredients.add(ing) }
                        it.foodQuantity?.let { qty -> quantity.add(qty) }
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Data not fetched: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAdapter() {
        cartAdapter = CartAdapter(
            requireContext(),
            foodNames,
            foodPrices,
            foodImagesUri,
            foodDescriptions,
            quantity,
            foodIngredients
        )
        binding.cartRecyclerView.adapter = cartAdapter
    }

    private fun getOrderItemsDetails() {
        val orderIdReference: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")

        val foodName = mutableListOf<String>()
        val foodImage = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodDescription = mutableListOf<String>()
        val foodIngredients = mutableListOf<String>()
        val foodQuantity = cartAdapter.getUpdatedItemsQuantities()

        orderIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val orderItem = foodSnapshot.getValue(CartItems::class.java)

                    orderItem?.let {
                        it.foodName?.let { name -> foodName.add(name) }
                        it.foodPrice?.let { price -> foodPrice.add(price) }
                        it.foodDescription?.let { desc -> foodDescription.add(desc) }
                        it.foodImage?.let { image -> foodImage.add(image) }
                        it.foodIngredients?.let { ing -> foodIngredients.add(ing) }
                    }
                }

                orderNow(
                    foodName,
                    foodPrice,
                    foodIngredients,
                    foodDescription,
                    foodImage,
                    foodQuantity
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Order making failed. Please try again: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodIngredients: MutableList<String>,
        foodDescription: MutableList<String>,
        foodImage: MutableList<String>,
        foodQuantity: MutableList<Int>
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            intent.putStringArrayListExtra("FoodItemsName", ArrayList(foodName))
            intent.putStringArrayListExtra("FoodItemsPrice", ArrayList(foodPrice))
            intent.putStringArrayListExtra("FoodItemsIngredients", ArrayList(foodIngredients))
            intent.putStringArrayListExtra("FoodItemsDescription", ArrayList(foodDescription))
            intent.putStringArrayListExtra("FoodItemsImage", ArrayList(foodImage))
            intent.putIntegerArrayListExtra("FoodItemsQuantity", ArrayList(foodQuantity))
            startActivity(intent)
        }
    }
}