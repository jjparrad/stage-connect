package com.montpellier.stageconnect.companies

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.montpellier.stageconnect.R
import com.montpellier.stageconnect.students.Student

class StudentProfileFragment : Fragment() {
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.student_profile, container, false)

        val studentId = arguments?.getString("studentId")

        if (studentId != null) {
            getStudentInformation(studentId) { student ->
                val nameTextView = view.findViewById<TextView>(R.id.name)
                val emailTextView = view.findViewById<TextView>(R.id.email)
                val universityTextView = view.findViewById<TextView>(R.id.university)

                nameTextView.text = student.name
                emailTextView.text = student.email
                universityTextView.text = student.university

                val cvButton = view.findViewById<Button>(R.id.cv)
                cvButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(student.cv))
                    requireContext().startActivity(intent)
                }
            }
        }

        return view
    }

    private fun getStudentInformation(id: String, callback: (Student) -> Unit) {
        db.collection("Candidates").document(id).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val email = document.getString("email") ?: ""
                    val name = document.getString("name") ?: ""
                    val university = document.getString("university") ?: ""
                    val cv = document.getString("cv") ?: ""

                    val student = Student(email, name, university, document.id, cv)
                    callback(student)
                }
            }
    }
}