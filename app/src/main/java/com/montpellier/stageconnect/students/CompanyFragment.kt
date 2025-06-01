package com.montpellier.stageconnect.students

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.montpellier.stageconnect.R
import com.montpellier.stageconnect.offers.JobOfferCompactAdapter
import com.montpellier.stageconnect.offers.JobOfferFragment
import com.montpellier.stageconnect.offers.Offer

class CompanyFragment : Fragment() {
    private lateinit var db: FirebaseFirestore
    private var candidate = ""
    private var university = ""
    private var candidateId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.fragment_company, container, false)

        val companyId = arguments?.getString("companyId")
        candidate = arguments?.getString("candidate").toString()
        university = arguments?.getString("university").toString()
        candidateId = arguments?.getString("candidateId").toString()

        val viewer = arguments?.getString("viewer")

        if (companyId != null) {
            getCompanyInformation(companyId) { address, email, name ->
                val nameTextView = view.findViewById<TextView>(R.id.company)
                val emailTextView = view.findViewById<TextView>(R.id.email)
                val addressTextView = view.findViewById<TextView>(R.id.address)

                nameTextView.text = name
                emailTextView.text = email
                addressTextView.text = address
            }

            getOffersByCompanyId(companyId) { offers ->
                val data = ArrayList<Offer>()
                for (offer in offers) {
                    val company = offer.getString("company") ?: "Sin título"
                    val position = offer.getString("position") ?: "Sin título"
                    val city = offer.getString("city") ?: "Sin título"
                    val date = offer.getString("date") ?: "Sin título"
                    val description = offer.getString("description") ?: "Sin descripción"
                    val count = (offer.getLong("candidateCount") ?: 0L).toInt()
                    val status = offer.getString("status") ?: "Sin descripción"
                    val id = offer.id
                    data.add(Offer(company, position, city, date, description, count, status, id, companyId))
                }
                val offerCount: TextView = view.findViewById(R.id.offer_count)
                offerCount.text = data.size.toString()
                updateList(view, data, viewer)
            }
        }

        return view
    }

    private fun updateList(view: View, data: ArrayList<Offer>, viewer: String?) {
        if (data.isEmpty()) {
            val emptyTextView: TextView = view.findViewById(R.id.empty)
            emptyTextView.text = "This company doesn't have any offers yet"
        } else {
            val emptyTextView: TextView = view.findViewById(R.id.empty)
            emptyTextView.visibility = View.INVISIBLE
        }

        val adapter = JobOfferCompactAdapter(data) { offer -> goToOffer(offer, viewer) }
        val recyclerview: RecyclerView = view.findViewById(R.id.list)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = adapter
    }

    private fun getCompanyInformation(id: String, callback: (String, String, String) -> Unit) {
        db.collection("Companies").document(id).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val address = document.getString("address") ?: ""
                    val email = document.getString("email") ?: ""
                    val name = document.getString("name") ?: ""
                    callback(address, email, name)
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Error ", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun getOffersByCompanyId(companyId: String, callback: (List<DocumentSnapshot>) -> Unit) {
        db.collection("Offers")
            .whereEqualTo("companyId", companyId)
            .whereEqualTo("status", "OPEN")
            .get()
            .addOnSuccessListener { result ->
                callback(result.documents)
            }
    }

    private fun goToOffer(offer: Offer, viewer: String?) {
        val bundle = Bundle()
        bundle.putString("company", offer.company)
        bundle.putString("companyId", offer.companyId)
        bundle.putString("position", offer.position)
        bundle.putString("city", offer.city)
        bundle.putString("date", offer.date)
        bundle.putString("description", offer.description)
        bundle.putString("offerId", "/"+offer.id)
        bundle.putString("candidateId", candidateId)
        bundle.putString("candidate", candidate)
        bundle.putString("university", university)
        bundle.putString("viewer", viewer)

        val fragment = JobOfferFragment()
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}