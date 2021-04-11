package com.example.smokedetectionsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(FirebaseInstanceIdService.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
            val email = sharedPref.getString("pref_email", "").toString()
            db.collection("users")
                .document(email).update("token", token)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully updated token")
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed updating token")
                }
        })

        val database = Firebase.database
        val myRef = database.getReference("GasSensor")
        val gasText = findViewById<TextView>(R.id.text_gas)
        val tempText = findViewById<TextView>(R.id.text_temp)
        val humText = findViewById<TextView>(R.id.text_hum)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val map = dataSnapshot.value as Map<String, Any>?
                Log.d(TAG, "Value is: $map")
                if (map != null) {
                    gasText.text = map["GasValue"].toString()
                    tempText.text = map["Temperature"].toString()
                    humText.text = map["Humidity"].toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_logout -> {
                val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
                val email = sharedPref.getString("pref_email", "").toString()
                db.collection("users")
                    .document(email).update("token", "")
                    .addOnSuccessListener {
                        Log.d(TAG, "Cleared token successfully")
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Failed clearing token")
                    }

                with(sharedPref.edit()) {
                    clear()
                    apply()
                }
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}