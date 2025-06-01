package com.montpellier.stageconnect.students

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.montpellier.stageconnect.R
import com.montpellier.stageconnect.offers.JobOfferAdapter
import com.montpellier.stageconnect.offers.JobOfferFragment
import com.montpellier.stageconnect.offers.Offer

class SearchFragment : Fragment() {
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val studentId = arguments?.getString("studentId")
        val candidate = arguments?.getString("candidate")
        val university = arguments?.getString("university")
        getOffers { data ->
            val adapter = JobOfferAdapter(data) { offer -> goToOffer(offer, candidate, university, studentId) }
            val recyclerview: RecyclerView = view.findViewById(R.id.list)
            recyclerview.layoutManager = LinearLayoutManager(requireContext())
            recyclerview.adapter = adapter
        }

        return view
    }

    private fun goToOffer(offer: Offer, candidate: String?, university: String?, studentId: String?) {
        val bundle = Bundle()
        bundle.putString("company", offer.company)
        bundle.putString("companyId", offer.companyId)
        bundle.putString("position", offer.position)
        bundle.putString("city", offer.city)
        bundle.putString("date", offer.date)
        bundle.putString("description", offer.description)
        bundle.putString("candidateId", studentId)
        bundle.putString("candidate", candidate)
        bundle.putString("university", university)
        bundle.putString("offerId", "/"+offer.id)

        val fragment = JobOfferFragment()
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getOffers(callback: (ArrayList<Offer>) -> Unit) {
        db.collection("Offers")
            .whereEqualTo("status", "OPEN")
            .get()
            .addOnSuccessListener { result ->
                val data = ArrayList<Offer>()
                for (offer in result) {
                    val company = offer.getString("company") ?: "Sin título"
                    val position = offer.getString("position") ?: "Sin título"
                    val city = offer.getString("city") ?: "Sin título"
                    val date = offer.getString("date") ?: "Sin título"
                    val description = offer.getString("description") ?: "Sin título"
                    val candidateCount = (offer.getLong("candidateCount") ?: 0L).toInt()
                    val status = offer.getString("status") ?: "Sin título"
                    val companyId = offer.getString("companyId") ?: "Sin título"
                    val jobOffer = Offer(company, position, city, date, description, candidateCount, status, offer.id)
                    jobOffer.companyId = companyId
                    data.add(jobOffer)
                }
                callback(data)
            }
    }
}