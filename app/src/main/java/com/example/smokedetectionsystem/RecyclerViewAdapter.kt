package com.example.smokedetectionsystem

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAdapter(context: Context, dateTime: ArrayList<String>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private var mDateTime: ArrayList<String> = ArrayList()
    private val mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: called.")

        holder.dateTime?.text = mDateTime[position]

    }

    override fun getItemCount(): Int {
        return mDateTime.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateTime: TextView? = null
        var parentLayout: RelativeLayout

        init {
            dateTime = itemView.findViewById(R.id.text_dateTime)
            parentLayout = itemView.findViewById(R.id.parent_layout)
        }
    }

    companion object {
        private const val TAG = "RecyclerViewAdapter"
    }

    init {
        mDateTime = dateTime
        mContext = context
    }
}