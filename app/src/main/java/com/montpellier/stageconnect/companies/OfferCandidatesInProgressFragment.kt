package com.montpellier.stageconnect.companies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.montpellier.stageconnect.R
import com.montpellier.stageconnect.offers.JobApplication

class OfferCandidatesInProgressFragment : Fragment() {
    private lateinit var db: FirebaseFirestore

    private var offerId: String = ""
    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = FirebaseFirestore.getInstance()
        view = inflater.inflate(R.layout.fragment_offer_candidates_progress, container, false)

        val position = arguments?.getString("position")
        val textView: TextView = view.findViewById(R.id.position)
        textView.text = position
        textView.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val button = view.findViewById<LinearLayout>(R.id.button)
        button.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        offerId = arguments?.getString("id").toString()
        val newTextView = view.findViewById<View>(R.id.new_offers)
        newTextView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("position", position)
            bundle.putString("id", offerId)
            val fragment = OfferCandidatesFragment()
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

        if (offerId != "") {
            loadNewCandidates()
        }

        return view
    }

    private fun updateList(view: View, data: ArrayList<JobApplication>) {
        val adapter = JobApplicationAdapter(
            data,
            onItemClick = { application -> goToCandidate(application.candidateId) },
            onPositiveClick = { application -> hireCandidate(application) },
            onNegativeClick = { application -> endProcess(application) },
        )
        val recyclerview: RecyclerView = view.findViewById(R.id.list)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = adapter
    }

    private fun loadNewCandidates() {
        db.collection("JobApplication")
            .whereEqualTo("status", "INTERVIEW")
            .whereEqualTo("offerId", "/$offerId")
            .get()
            .addOnSuccessListener { documents ->
                val data = ArrayList<JobApplication>()
                for (candidate in documents) {
                    val application = candidate.toObject(JobApplication::class.java)
                    application.id = candidate.id
                    data.add(application)
                }
                updateList(view, data)
            }
    }

    private fun goToCandidate(studentId: String?) {
        val bundle = Bundle()
        bundle.putString("studentId", studentId)

        val fragment = StudentProfileFragment()
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun hireCandidate(application: JobApplication) {
        val id = application.id
        if (id != null) {
            db.collection("JobApplication").document(id)
                .update("status", "HIRED")
                .addOnSuccessListener {
                    loadNewCandidates()
                }
        }
    }

    private fun endProcess(application: JobApplication) {
        val id = application.id
        val candidateId = application.candidateId
        if (id != null && candidateId != null) {
            db.collection("JobApplication").document(id)
                .update("status", "REJECTED")
                .addOnSuccessListener {
                    loadNewCandidates()
                }

            db.collection("Candidates").document(candidateId)
                .update("applicationsCount", FieldValue.increment(-1))
        }
    }
}