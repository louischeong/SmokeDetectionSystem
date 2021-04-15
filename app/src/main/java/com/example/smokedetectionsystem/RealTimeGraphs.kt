package com.example.smokedetectionsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class RealTimeGraphs : AppCompatActivity() {

    private var series2 : LineGraphSeries<DataPoint>? = null
    private var lastX2 = 0.0
    private var mTimer2: Runnable? = null
    private var series3 : LineGraphSeries<DataPoint>? = null
    private var lastX3 = 0.0
    private var mTimer3: Runnable? = null
    private val mHandler = Handler(Looper.getMainLooper())
    private val TAG = "RealTimeGraphs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_graphs)

        val button = findViewById<Button>(R.id.button_back)
        button.setOnClickListener{
            finish()
        }

        val database = Firebase.database
        val myRef = database.getReference("GasSensor")
        val tempText = findViewById<TextView>(R.id.text_temp2)
        val humText = findViewById<TextView>(R.id.text_hum2)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val map = dataSnapshot.value as Map<String, Any>?
                Log.d(TAG, "Value is: $map")
                if (map != null) {
                    tempText.text = map["Temperature"].toString()
                    humText.text = map["Humidity"].toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })



        val graph2 = findViewById<GraphView>(R.id.graph2)
        series2 = LineGraphSeries()
        graph2.addSeries(series2);
        val viewport2 = graph2.viewport

        viewport2.isXAxisBoundsManual = true
        viewport2.isYAxisBoundsManual = true
        viewport2.setMinX(0.0)
        viewport2.setMaxY(100.0)
        viewport2.setMaxX(40.0)
        viewport2.isScrollable = false

        val graph3 = findViewById<GraphView>(R.id.graph3)
        series3 = LineGraphSeries()
        graph3.addSeries(series3);
        val viewport3 = graph3.viewport

        viewport3.isXAxisBoundsManual = true
        viewport3.isYAxisBoundsManual = true
        viewport3.setMinX(0.0)
        viewport3.setMaxY(100.0)
        viewport3.setMaxX(40.0)
        viewport3.isScrollable = false
    }

    override fun onResume() {
        super.onResume()

        mTimer2 = object : Runnable {
            override fun run() {
                lastX2 += 1.0
                val temp = findViewById<TextView>(R.id.text_temp2).text.toString().toDouble()
                series2?.appendData(DataPoint(lastX2, temp), true, 40)
                mHandler.postDelayed(this, 1000)
            }
        }
        mHandler.postDelayed(mTimer2 as Runnable, 100)
        mTimer3 = object : Runnable {
            override fun run() {
                lastX3 += 1.0
                val hum = findViewById<TextView>(R.id.text_hum2).text.toString().toDouble()
                series3?.appendData(DataPoint(lastX3, hum), true, 40)
                mHandler.postDelayed(this, 1000)
            }
        }
        mHandler.postDelayed(mTimer3 as Runnable, 100)
    }

    override fun onPause() {
        mHandler.removeCallbacks(mTimer2!!)
        super.onPause()
    }

}