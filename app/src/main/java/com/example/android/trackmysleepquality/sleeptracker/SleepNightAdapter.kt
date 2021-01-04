package com.example.android.trackmysleepquality.sleeptracker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.TextItemViewHolder
import com.example.android.trackmysleepquality.database.SleepNight

class SleepNightAdapter(
        val data: List<SleepNight> = listOf()
) : RecyclerView.Adapter<TextItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        holder.textView.text = data[position].sleepQuality.toString()
    }

    override fun getItemCount(): Int = data.size
}