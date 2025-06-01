package com.montpellier.stageconnect.companies

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.montpellier.stageconnect.R

class JobOfferFragment : Fragment() {
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.fragment_job_offer2, container, false)

        val position = arguments?.getString("position")
        val city = arguments?.getString("city")
        val status = arguments?.getString("status")
        val date = arguments?.getString("date")
        val description = arguments?.getString("description")
        val id = arguments?.getString("id")

        var textView: TextView = view.findViewById(R.id.position)
        textView.text = position

        textView = view.findViewById(R.id.city)
        textView.text = city

        val statusTextView = view.findViewById<TextView>(R.id.status)
        statusTextView.text = status

        textView = view.findViewById(R.id.date)
        textView.text = date

        textView = view.findViewById(R.id.description)
        textView.text = description

        val closeOffer = view.findViewById<TextView>(R.id.close_button)
        if (status.equals("OPEN") && id != null) {
            closeOffer.visibility = View.VISIBLE
            closeOffer.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Close offer")
                    .setMessage("Are you sure you want to close this job offer?\n" +
                            "This action will prevent candidates from applying and can't be undone.\n")
                    .setPositiveButton("Sí") { _, _ ->
                        closeOffer(id)
                        closeOffer.visibility = View.INVISIBLE
                        statusTextView.text = "CLOSED"
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }

        val button = view.findViewById<View>(R.id.button)
        button.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("position", position)
            bundle.putString("id", id)
            val fragment = OfferCandidatesFragment()
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun closeOffer(id: String) {
        db.collection("Offers").document(id)
            .update("status", "CLOSED")
    }
}