package com.montpellier.stageconnect.offers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.montpellier.stageconnect.R

class JobOfferCompactAdapter(
    private val list: List<Offer>,
    private val onItemClick: (Offer) -> Unit
) : RecyclerView.Adapter<JobOfferCompactAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_offer_compact, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.position.text = item.position
        holder.city.text = item.city
        holder.date.text = item.date

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val position: TextView = itemView.findViewById(R.id.position)
        val city: TextView = itemView.findViewById(R.id.city)
        val date: TextView = itemView.findViewById(R.id.date)
    }
}