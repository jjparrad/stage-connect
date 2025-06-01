package com.montpellier.stageconnect.universities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.google.firebase.firestore.FirebaseFirestore
import com.montpellier.stageconnect.HomeFragment
import com.montpellier.stageconnect.R
import com.montpellier.stageconnect.offers.JobApplication
import com.montpellier.stageconnect.students.JobApplicationAdapter
import com.montpellier.stageconnect.students.Student

class UniversityHomeFragment : Fragment() {
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.fragment_university_home, container, false)

        val universityId = arguments?.getString("id")
        if (universityId != null) {
            getUniversityInformation(universityId) { delegate, university, code ->
                val delegateTextView: TextView = view.findViewById(R.id.name)
                val universityTextView: TextView = view.findViewById(R.id.university)
                delegateTextView.text = delegate
                universityTextView.text = university

                val codeTextView: TextView = view.findViewById(R.id.code)
                codeTextView.setOnClickListener {
                    val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("label", code)
                    clipboard.setPrimaryClip(clip)

                    activity?.runOnUiThread {
                        Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val logout = view.findViewById<ImageView>(R.id.logout)
            logout.setOnClickListener {
                val fragment = HomeFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
            }

            getStudentsByUniversity(universityId) { data -> updateList(view, data) }
        }

        return view
    }

    private fun updateList(view: View, data: ArrayList<Student>) {
        val emptyTextView = view.findViewById<TextView>(R.id.empty)
        if (data.isEmpty()) {
            emptyTextView.text = "No students currently enrolled."
        } else {
            emptyTextView.visibility = View.INVISIBLE
        }

        val adapter = EnrolledStudentAdapter(data) { offer -> goToStudent(offer) }
        val recyclerview: RecyclerView = view.findViewById(R.id.list)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = adapter
    }

    private fun goToStudent(student: Student) {
        val bundle = Bundle()
        bundle.putString("studentId", student.id)
        bundle.putString("name", student.name)
        bundle.putString("email", student.email)
        bundle.putString("university", student.university)
        bundle.putString("cv", student.cv)

        val fragment = UniversityStudentProfileFragment()
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getUniversityInformation(id: String, callback: (String, String, String) -> Unit) {
        db.collection("Universities").document(id).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val delegate = document.getString("delegate") ?: ""
                    val name = document.getString("name") ?: ""
                    val code = document.getString("universityCode") ?: ""

                    callback(delegate, name, code)
                }
            }
    }

    private fun getStudentsByUniversity(universityId: String, callback: (ArrayList<Student>) -> Unit) {
        db.collection("Candidates")
            .whereEqualTo("universityId", universityId)
            .get()
            .addOnSuccessListener { result ->
                val data = ArrayList<Student>()
                for (student in result) {
                    val applicationsCount = (student.getLong("applicationsCount") ?: 0L).toInt()
                    val cv = student.getString("cv") ?: "Sin título"
                    val email = student.getString("email") ?: "Sin título"
                    val name = student.getString("name") ?: "Sin título"
                    val university = student.getString("university") ?: "Sin título"
                    val id = student.id

                    val newStudent = Student(email, name, university, id, cv, applicationsCount)
                    data.add(newStudent)
                }
                callback(data)
            }
    }

}