package com.montpellier.stageconnect.universities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.montpellier.stageconnect.R
import com.montpellier.stageconnect.students.Student

class EnrolledStudentAdapter(
    private val list: List<Student>,
    private val onItemClick: (Student) -> Unit
) : RecyclerView.Adapter<EnrolledStudentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_enrolled_student, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.name.text = item.name
        holder.applications.text = item.applicationsCount.toString() + " active job applications"

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = itemView.findViewById(R.id.name)
        val applications: TextView = itemView.findViewById(R.id.applications)
    }
}