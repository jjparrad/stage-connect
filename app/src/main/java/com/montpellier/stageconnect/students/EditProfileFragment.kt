package com.montpellier.stageconnect.students

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.montpellier.stageconnect.R

class EditProfileFragment : Fragment() {
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        db = FirebaseFirestore.getInstance()

        var name = arguments?.getString("name")
        var email = arguments?.getString("email")
        var cv = arguments?.getString("cv")

        val nameEditText = view.findViewById<EditText>(R.id.name)
        val emailEditText = view.findViewById<EditText>(R.id.email)
        val cvEditText = view.findViewById<EditText>(R.id.cv)

        nameEditText.setText(name)
        emailEditText.setText(email)
        cvEditText.setText(cv)

        val id = arguments?.getString("id")
        val saveButton = view.findViewById<TextView>(R.id.save)
        saveButton.setOnClickListener {
            name = nameEditText.text.toString()
            email = emailEditText.text.toString()
            cv = cvEditText.text.toString()

            if (id != null) {
                val updates = mapOf(
                    "name" to name,
                    "email" to email,
                    "cv" to cv
                )

                db.collection("Candidates").document(id)
                    .update(updates)
                    .addOnSuccessListener {
                        parentFragmentManager.popBackStack()
                    }
            }
        }

        return view
    }

}