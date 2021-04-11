package com.example.smokedetectionsystem

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat

class NotificationActivity : AppCompatActivity() {
    private val TAG = "NotificationActivity"
    private val db = FirebaseFirestore.getInstance()
    private var dateTimes: ArrayList<String> = ArrayList()
    val placesRef = db.collection("notifications")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val back = findViewById<ImageView>(R.id.back_button)
        back.setOnClickListener{
            finish()
        }

        db.collection("notifications").orderBy("dateTime", Query.Direction.DESCENDING).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        dateTimes.add(document.getTimestamp("dateTime")!!.toDate().toString())
                    }
                    val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
                    val adapter = RecyclerViewAdapter(this, dateTimes)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this)
                }
            }


    }
}