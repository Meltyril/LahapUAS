package com.lahap.appuas.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.lahap.appuas.models.MenuItem

object FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()

    // Get recommended menu items from Firestore
    fun getRecommendedMenuItems(callback: (List<MenuItem>) -> Unit) {
        db.collection("menu")
            .get()
            .addOnSuccessListener { documents ->
                val menuItems = mutableListOf<MenuItem>()
                for (document in documents) {
                    val item = document.toObject(MenuItem::class.java)
                    menuItems.add(item)
                }
                callback(menuItems)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}
