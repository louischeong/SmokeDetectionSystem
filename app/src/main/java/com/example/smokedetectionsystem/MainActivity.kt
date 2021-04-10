package com.example.smokedetectionsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
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
            val email = sharedPref.getString("pref_email","").toString()
            db.collection("users")
                .document(email).update("token", token)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully updated token")
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed updating token")
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
                val email = sharedPref.getString("pref_email","").toString()
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