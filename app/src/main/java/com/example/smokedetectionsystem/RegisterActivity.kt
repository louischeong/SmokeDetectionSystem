package com.example.smokedetectionsystem

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class RegisterActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister = findViewById<Button>(R.id.button_register)
        val btnBack = findViewById<Button>(R.id.button_back)
        btnRegister.setOnClickListener {
            val email = findViewById<EditText>(R.id.res_input_email).text.toString().trim()
            val password = findViewById<EditText>(R.id.res_input_password).text.toString().trim()
            val conPassword = findViewById<EditText>(R.id.res_input_confirm_password).text.toString().trim()

            if((email.isNullOrEmpty()) || (password.isNullOrEmpty()) || (conPassword.isNullOrEmpty())){
                Toast.makeText(this, "Please fill in all the required fields.", Toast.LENGTH_LONG).show()
            }else {
                val docRef = db.collection("users").document(email)
                docRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document!!.exists()) {
                            Toast.makeText(this, "Email has been used, please use other email address.", Toast.LENGTH_LONG).show()
                        } else {
                            if (password == conPassword) {
                                val data = hashMapOf(
                                    "password" to password,
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
                                finish()
                            } else {
                                Toast.makeText(this, "Password and Confirm password should be the same.", Toast.LENGTH_LONG).show()
                            }

                        }
                    } else {
                        Log.d(TAG, "Failed with: ", task.exception)
                    }
                }
            }
        }
        btnBack.setOnClickListener{
            finish()
        }
    }
}