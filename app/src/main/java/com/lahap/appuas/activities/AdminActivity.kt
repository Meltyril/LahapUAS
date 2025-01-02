package com.lahap.appuas.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.lahap.appuas.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private val db = FirebaseFirestore.getInstance()
    private var imageUri: Uri? = null // Store selected image URI

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent> // For new Activity Result API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add new menu item
        binding.btnAddMenu.setOnClickListener {
            val name = binding.etMenuName.text.toString()
            val price = binding.etMenuPrice.text.toString().toDoubleOrNull()
            val description = binding.etMenuDescription.text.toString()

            if (name.isNotEmpty() && price != null && description.isNotEmpty() ) {
                addMenuToFirestore(name, price, description, imageUri.toString())
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMenuToFirestore(name: String, price: Double, description: String, imageUri: String) {
        val newItem = hashMapOf(
            "foodName" to name,
            "foodPrice" to price.toString(),
            "foodDescription" to description,
        )
        db.collection("menus")
            .add(newItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Menu added successfully!", Toast.LENGTH_SHORT).show()
                clearInputFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add menu: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearInputFields() {
        binding.etMenuName.text.clear()
        binding.etMenuPrice.text.clear()
        binding.etMenuDescription.text.clear()
        imageUri = null
    }
}
