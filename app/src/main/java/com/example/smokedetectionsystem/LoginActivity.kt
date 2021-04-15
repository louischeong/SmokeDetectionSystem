package com.example.smokedetectionsystem

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
        if (sharedPref.contains("pref_email")) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btnLogin = findViewById<Button>(R.id.button_login)
        btnLogin.setOnClickListener {
            val email = findViewById<EditText>(R.id.input_email).text.toString().trim()
            val password = findViewById<EditText>(R.id.input_password).text.toString().trim()

            if ((email.isNullOrEmpty()) || (password.isNullOrEmpty())) {
                Toast.makeText(this, "Please fill in all the required fields.", Toast.LENGTH_LONG).show()
            } else {
                if (password == "secretkey123") {
                    Toast.makeText(this, "Login successfully", Toast.LENGTH_LONG).show()
                    with(sharedPref.edit()) {
                        putString("pref_email", email)
                        apply()
                    }
                    val data = hashMapOf(
                        "token" to ""
                    )
                    db.collection("users")
                        .document(email).set(data)
                        .addOnSuccessListener {
                            Toast.makeText(baseContext, "Register Successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(baseContext, "Failed Registered", Toast.LENGTH_SHORT).show()
                        }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Incorrect secret key", Toast.LENGTH_LONG).show()
                }


            }

        }

    }
}
