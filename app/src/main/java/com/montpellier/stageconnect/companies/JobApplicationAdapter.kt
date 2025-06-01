package com.montpellier.stageconnect.companies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.montpellier.stageconnect.R
import com.montpellier.stageconnect.offers.JobApplication

class JobApplicationAdapter(
    private val list: List<JobApplication>,
    private val onItemClick: (JobApplication) -> Unit,
    private val onPositiveClick: (JobApplication) -> Unit,
    private val onNegativeClick: (JobApplication) -> Unit,
) : RecyclerView.Adapter<JobApplicationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_candidate, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.name.text = item.candidate
        holder.university.text = item.university
        holder.applicationDate.text = item.date

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        holder.positiveButton.setOnClickListener {
            onPositiveClick(item)
        }
        holder.negativeButton.setOnClickListener {
            onNegativeClick(item)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = itemView.findViewById(R.id.name)
        val university: TextView = itemView.findViewById(R.id.university)
        val applicationDate: TextView = itemView.findViewById(R.id.date)

        val positiveButton: TextView = itemView.findViewById(R.id.positive)
        val negativeButton: TextView = itemView.findViewById(R.id.negative)
    }
}