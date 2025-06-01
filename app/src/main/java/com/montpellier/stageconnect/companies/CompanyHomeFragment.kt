package com.montpellier.stageconnect.companies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.montpellier.stageconnect.HomeFragment
import com.montpellier.stageconnect.R
import com.montpellier.stageconnect.offers.JobOfferCompanyAdapter
import com.montpellier.stageconnect.offers.Offer

class CompanyHomeFragment : Fragment() {
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_company_home, container, false)
        db = FirebaseFirestore.getInstance()

        val companyId = arguments?.getString("id")
        if (companyId != null) {
            getCompanyInformation(companyId) { address, email, name ->
                val nameTextView = view.findViewById<TextView>(R.id.name)
                val emailTextView = view.findViewById<TextView>(R.id.email)
                val addressTextView = view.findViewById<TextView>(R.id.address)

                nameTextView.text = name
                emailTextView.text = email
                addressTextView.text = address

                val button = view.findViewById<View>(R.id.new_offer)
                button.setOnClickListener {
                    val fragment = CreateOfferFragment()
                    val bundle = Bundle()
                    bundle.putString("company", name)
                    bundle.putString("id", companyId)
                    fragment.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
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
                    data.add(Offer(company, position, city, date, description, count, status, id))
                }
                val offerCount: TextView = view.findViewById(R.id.offer_count)
                offerCount.text = data.size.toString()
                updateList(view, data)
            }

            val logout = view.findViewById<ImageView>(R.id.logout)
            logout.setOnClickListener {
                val fragment = HomeFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
            }
        }

        return view
    }

    private fun updateList(view: View, data: ArrayList<Offer>) {
        val emptyTextView = view.findViewById<TextView>(R.id.empty)
        if (data.isEmpty()) {
            emptyTextView.text = "You don't have any active offers"
        } else {
            emptyTextView.visibility = View.INVISIBLE
        }

        val adapter = JobOfferCompanyAdapter(data) { offer -> goToOffer(offer) }
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
            .get()
            .addOnSuccessListener { result ->
                callback(result.documents)
            }
    }

    private fun goToOffer(offer: Offer) {
        val bundle = Bundle()
        bundle.putString("position", offer.position)
        bundle.putString("city", offer.city)
        bundle.putString("status", offer.status)
        bundle.putString("date", offer.date)
        bundle.putString("description", offer.description)
        bundle.putString("id", offer.id)

        val fragment = JobOfferFragment()
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}