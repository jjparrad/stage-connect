package com.montpellier.stageconnect.offers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.montpellier.stageconnect.R
import com.montpellier.stageconnect.students.CompanyFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JobOfferFragment : Fragment() {
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.fragment_job_offer, container, false)

        val company = arguments?.getString("company")
        val companyId = arguments?.getString("companyId")
        val position = arguments?.getString("position")
        val city = arguments?.getString("city")
        val date = arguments?.getString("date")
        val description = arguments?.getString("description")
        val offerId = arguments?.getString("offerId")

        val offer = Offer(company, position, city, "", description, offerId)
        offer.companyId = companyId

        var textView: TextView = view.findViewById(R.id.position)
        textView.text = position

        textView = view.findViewById(R.id.city)
        textView.text = city

        textView = view.findViewById(R.id.date)
        textView.text = date

        textView = view.findViewById(R.id.description)
        textView.text = description

        val companyTextView = view.findViewById<TextView>(R.id.company)
        companyTextView.text = company

        val candidateId = arguments?.getString("candidateId")
        val candidate = arguments?.getString("candidate")
        val university = arguments?.getString("university")
        val viewer = arguments?.getString("viewer")

        companyTextView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("companyId", companyId)
            bundle.putString("candidate", candidate)
            bundle.putString("university", university)
            bundle.putString("candidateId", candidateId)
            bundle.putString("viewer", viewer)
            val fragment = CompanyFragment()
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val button = view.findViewById<TextView>(R.id.button)

        if (candidateId != null && offerId != null && viewer != "true") {
            getStudentStatusOnOffer(candidateId, offerId) { status ->
                if (status == "") {
                    button.visibility = View.VISIBLE
                    button.setOnClickListener {
                        if (candidate != null && university != null) {
                            applyToOffer(offer, candidate, university, candidateId)
                        }
                    }
                }
            }
        }

        return view
    }

    private fun getStudentStatusOnOffer(studentId: String, offerId: String, callback: (String) -> Unit) {
        db.collection("JobApplication")
            .whereEqualTo("candidateId", "/$studentId")
            .whereEqualTo("offerId", offerId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    callback(documents.documents[0].getString("status") ?: "Sin título")
                } else {
                    callback("")
                }
            }
    }

    private fun applyToOffer(offer: Offer, candidate: String, university: String, candidateId: String) {
        val today = getCurrentDate()
        val application = JobApplication(offer.company, offer.position, offer.city, today, offer.description, "APPLIED", candidate, university,
            "/$candidateId", offer.id)
        application.companyId = offer.companyId

        db.collection("JobApplication")
            .add(application)
            .addOnSuccessListener {
                parentFragmentManager.popBackStack()
            }

        if (offer.id != null) {
            db.collection("Offers").document(offer.id)
                .update("candidateCount", FieldValue.increment(1))
        }
        db.collection("Candidates").document(candidateId)
            .update("applicationsCount", FieldValue.increment(1))
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
}