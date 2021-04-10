package com.example.smokedetectionsystem

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        if(sharedPref.contains(("pref_email")) && sharedPref.contains("pref_pass")){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btnLogin = findViewById<Button>(R.id.button_login)
        val btnRegister = findViewById<Button>(R.id.button_sign_up)
        btnLogin.setOnClickListener {
            val email = findViewById<EditText>(R.id.input_email).text.toString().trim()
            val password = findViewById<EditText>(R.id.input_password).text.toString().trim()
            val docRef = db.collection("users").document(email)
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (password == document!!.getString("password")) {
                        Toast.makeText(this, "Login successfully", Toast.LENGTH_LONG).show()
                        with(sharedPref.edit()) {
                            putString("pref_email", email)
                            putString("pref_pass", password)
                            apply()
                        }
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.exception)
                }
            }

        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
