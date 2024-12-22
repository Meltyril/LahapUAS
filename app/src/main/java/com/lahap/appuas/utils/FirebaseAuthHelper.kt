package com.lahap.appuas.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FirebaseAuthHelper {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Login method
    fun login(
        email: String,
        password: String,
        onSuccess: (user: FirebaseUser) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onFailure("User not found after login.")
                    }
                } else {
                    onFailure(task.exception?.localizedMessage ?: "Login failed")
                }
            }
    }

    // Sign-up method
    fun signUp(
        email: String,
        password: String,
        onSuccess: (user: FirebaseUser) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onFailure("User not found after sign-up.")
                    }
                } else {
                    onFailure(task.exception?.localizedMessage ?: "Sign-up failed")
                }
            }
    }

    // Optional: Update user profile (e.g., display name)
    fun updateUserProfile(
        name: String,
        onSuccess: () -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        val user = auth.currentUser
        if (user != null) {
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure(task.exception?.localizedMessage ?: "Failed to update profile")
                    }
                }
        } else {
            onFailure("No user is currently logged in.")
        }
    }
}
