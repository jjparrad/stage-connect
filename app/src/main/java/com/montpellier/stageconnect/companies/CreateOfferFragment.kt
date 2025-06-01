package com.montpellier.stageconnect.companies

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.montpellier.stageconnect.R
import com.montpellier.stageconnect.offers.Offer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateOfferFragment : Fragment() {
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_offer, container, false)

        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        val companyId = arguments?.getString("id")
        val company = arguments?.getString("company")

        val button = view.findViewById<TextView>(R.id.button)
        button.setOnClickListener {
            if (companyId == null) return@setOnClickListener
            if (company == null) return@setOnClickListener
            val positionField = view.findViewById<EditText>(R.id.position)
            val cityField = view.findViewById<EditText>(R.id.city)
            val descriptionField = view.findViewById<EditText>(R.id.description)
            createOffer(companyId, company, positionField.text.toString(), cityField.text.toString(), descriptionField.text.toString())
        }
        return view
    }

    private fun createOffer(companyId: String, company: String, position: String, city: String, description: String) {
        val today = getCurrentDate()
        val newOffer = Offer(company, position, city, today, description, 0, "OPEN", "", companyId)

        db.collection("Offers")
            .add(newOffer)
            .addOnSuccessListener {
                parentFragmentManager.popBackStack()
            }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

}