package com.lahap.appuas.models

data class MenuItem(
    val foodName: String? = null,
    val foodPrice: String? = null,
    val foodDescription: String? = null,
    val foodImage: String? = null,
    val foodIngredient: String? = null,
    var id: String = ""  // Add the ID field to hold Firestore document ID
)
