package com.montpellier.stageconnect.universities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.montpellier.stageconnect.R
import com.montpellier.stageconnect.offers.JobApplication
import com.montpellier.stageconnect.offers.JobOfferFragment
import com.montpellier.stageconnect.students.JobApplicationAdapter

class UniversityStudentProfileFragment : Fragment() {
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.fragment_university_student_profile, container, false)

        var textView = view.findViewById<TextView>(R.id.name)
        textView.text = arguments?.getString("name")

        textView = view.findViewById(R.id.email)
        textView.text = arguments?.getString("email")

        textView = view.findViewById(R.id.university)
        textView.text = arguments?.getString("university")

        val studentId = arguments?.getString("studentId")
        val cv = arguments?.getString("cv")

        if (studentId != null) {
            getOffersByStudentId(studentId) { data -> updateList(view, data) }

            val detachButton = view.findViewById<Button>(R.id.detach)
            detachButton.setOnClickListener {
                detachStudent(studentId)
            }
        }

        val cvButton = view.findViewById<Button>(R.id.cv)
        cvButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cv))
            requireContext().startActivity(intent)
        }

        return view
    }

    private fun updateList(view: View, data: ArrayList<JobApplication>) {
        val emptyTextView = view.findViewById<TextView>(R.id.empty)
        if (data.isEmpty()) {
            emptyTextView.text = "No active applications"
        } else {
            emptyTextView.visibility = View.INVISIBLE
        }

        val adapter = JobApplicationAdapter(data) { offer -> goToOffer(offer) }
        val recyclerview: RecyclerView = view.findViewById(R.id.list)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = adapter
    }

    private fun goToOffer(application: JobApplication) {
        val bundle = Bundle()
        bundle.putString("company", application.company)
        bundle.putString("position", application.position)
        bundle.putString("city", application.city)
        bundle.putString("date", application.date)
        bundle.putString("description", application.description)
        bundle.putString("candidateId", application.candidateId)
        bundle.putString("companyId", application.companyId)
        bundle.putString("offerId", application.offerId)
        bundle.putString("viewer", "true")

        val fragment = JobOfferFragment()
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getOffersByStudentId(studentId: String, callback: (ArrayList<JobApplication>) -> Unit) {
        db.collection("JobApplication")
            .whereEqualTo("candidateId", "/$studentId")
            .get()
            .addOnSuccessListener { result ->
                val data = ArrayList<JobApplication>()
                for (application in result) {
                    val status = application.getString("status") ?: "Sin título"
                    val company = application.getString("company") ?: "Sin título"
                    val position = application.getString("position") ?: "Sin título"
                    val city = application.getString("city") ?: "Sin título"
                    val description = application.getString("description") ?: "Sin título"
                    val date = application.getString("date") ?: "Sin título"
                    val offerId = application.getString("offerId") ?: "Sin título"
                    val companyId = application.getString("companyId") ?: "Sin título"
                    val id = application.id
                    val jobApplication = JobApplication(company, position, city, date, description, status)
                    jobApplication.offerId = offerId
                    jobApplication.id = id
                    jobApplication.candidateId = studentId
                    jobApplication.companyId = companyId
                    data.add(jobApplication)
                }
                callback(data)
            }
    }

    private fun detachStudent(studentId: String) {
        val updates = mapOf(
            "university" to "",
            "universityId" to "",
        )

        db.collection("Candidates").document(studentId)
            .update(updates)
            .addOnSuccessListener {
                parentFragmentManager.popBackStack()
            }
    }
}