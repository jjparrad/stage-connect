package com.montpellier.stageconnect.students

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.montpellier.stageconnect.R
import com.montpellier.stageconnect.offers.JobApplication

class JobApplicationAdapter(
    private val list: List<JobApplication>,
    private val onItemClick: (JobApplication) -> Unit
) : RecyclerView.Adapter<JobApplicationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_job_application, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.company.text = item.company
        holder.position.text = item.position
        holder.city.text = item.city
        holder.date.text = item.date
        holder.status.text = item.status

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val company: TextView = itemView.findViewById(R.id.company)
        val position: TextView = itemView.findViewById(R.id.position)
        val city: TextView = itemView.findViewById(R.id.city)
        val date: TextView = itemView.findViewById(R.id.date)
        val status: TextView = itemView.findViewById(R.id.status)
    }
}