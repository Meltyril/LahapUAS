package com.lahap.appuas.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lahap.appuas.R
import com.lahap.appuas.databinding.ActivityLoginBinding
import com.lahap.appuas.databinding.ActivityLoginBinding.*
import com.lahap.appuas.fragments.HomeFragment
import com.lahap.appuas.utils.FirebaseAuthHelper

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Gunakan Data Binding untuk mengatur layout
        binding = inflate(layoutInflater)
        setContentView(binding.root) // Pastikan menggunakan binding.root

        binding.loginbutton.setOnClickListener {
            val email = binding.emailLogin.text.toString()
            val password = binding.passwordLogin.text.toString()

            // Validasi input kosong
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email atau Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Login menggunakan FirebaseAuthHelper
            FirebaseAuthHelper.login(email, password, { user ->
                // Berhasil login
                startActivity(Intent(this, HomeFragment::class.java))
                finish()
            }, { error ->
                // Gagal login
                Toast.makeText(this, "Login gagal: $error", Toast.LENGTH_SHORT).show()
            })
        }

        val dontHaveAccountTextView = findViewById<TextView>(R.id.donthavebutton)
        dontHaveAccountTextView.setOnClickListener {
            // Navigasi ke SignUpActivity
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}

