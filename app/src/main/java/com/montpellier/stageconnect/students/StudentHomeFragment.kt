package com.montpellier.stageconnect.students

import android.content.Intent
import android.net.Uri
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
import com.montpellier.stageconnect.offers.JobOfferFragment

class StudentHomeFragment : Fragment() {
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_home, container, false)
        db = FirebaseFirestore.getInstance()

        val studentId = arguments?.getString("id")

        val searchButton = view.findViewById<View>(R.id.search)
        val editButton = view.findViewById<View>(R.id.edit)
        val cvButton = view.findViewById<View>(R.id.cv)

        if (studentId != null) {
            getStudentInformation(studentId, view) { student ->
                getOffersByStudentId(studentId, student.name, student.university) { data -> updateList(view, data) }

                searchButton.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("studentId", studentId)
                    bundle.putString("candidate", student.name)
                    bundle.putString("university", student.university)
                    val fragment = SearchFragment()
                    fragment.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }

                editButton.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("name", student.name)
                    bundle.putString("cv", student.cv)
                    bundle.putString("email", student.email)
                    bundle.putString("id", studentId)
                    val fragment = EditProfileFragment()
                    fragment.arguments = bundle
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }

                cvButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(student.cv))
                    requireContext().startActivity(intent)
                }
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

    private fun updateList(view: View, data: ArrayList<JobApplication>) {
        val emptyTextView = view.findViewById<TextView>(R.id.empty)
        if (data.isEmpty()) {
            emptyTextView.text = "You don't have any applications yet"
        } else {
            emptyTextView.visibility = View.INVISIBLE
        }

        val adapter = JobApplicationAdapter(data) { offer -> goToOffer(offer) }
        val recyclerview: RecyclerView = view.findViewById(R.id.list)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = adapter
    }

    private fun getOffersByStudentId(studentId: String, candidate: String, university: String, callback: (ArrayList<JobApplication>) -> Unit) {
        db.collection("JobApplication")
            .whereEqualTo("candidateId", "/$studentId")
            .get()
            .addOnSuccessListener { result ->
                val data = ArrayList<JobApplication>()
                for (application in result) {
                    val status = application.getString("status") ?: "Sin título"
                    val company = application.getString("company") ?: "Sin título"
                    val position = application.getString("position") ?: "Sin título"
                    val city = application.getString("city") ?: "Sin título"
                    val description = application.getString("description") ?: "Sin título"
                    val date = application.getString("date") ?: "Sin título"
                    val offerId = application.getString("offerId") ?: "Sin título"
                    val companyId = application.getString("companyId") ?: "Sin título"
                    val id = application.id
                    val jobApplication = JobApplication(company, position, city, date, description, status, candidate, university)
                    jobApplication.offerId = offerId
                    jobApplication.id = id
                    jobApplication.candidateId = studentId
                    jobApplication.companyId = companyId
                    data.add(jobApplication)
                }
                callback(data)
            }
    }


    private fun goToOffer(application: JobApplication) {
        val bundle = Bundle()
        bundle.putString("company", application.company)
        bundle.putString("position", application.position)
        bundle.putString("city", application.city)
        bundle.putString("date", application.date)
        bundle.putString("description", application.description)
        bundle.putString("candidateId", application.candidateId)
        bundle.putString("companyId", application.companyId)
        bundle.putString("offerId", application.offerId)
        bundle.putString("candidate", application.candidate)
        bundle.putString("university", application.university)

        val fragment = JobOfferFragment()
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getStudentInformation(id: String, view: View, callback: (Student) -> Unit) {
        db.collection("Candidates").document(id).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val email = document.getString("email") ?: ""
                    val name = document.getString("name") ?: ""
                    val university = document.getString("university") ?: ""
                    val cv = document.getString("cv") ?: ""

                    val nameTextView = view.findViewById<TextView>(R.id.name)
                    val emailTextView = view.findViewById<TextView>(R.id.email)
                    val universityTextView = view.findViewById<TextView>(R.id.university)

                    nameTextView.text = name
                    emailTextView.text = email
                    universityTextView.text = university
                    val student = Student(email, name, university, document.id, cv)
                    callback(student)
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Error ", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}