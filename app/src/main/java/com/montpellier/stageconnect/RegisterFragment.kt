package com.montpellier.stageconnect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.montpellier.stageconnect.students.Student
import com.montpellier.stageconnect.students.StudentHomeFragment

class RegisterFragment : Fragment() {
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val button = view.findViewById<TextView>(R.id.save)
        button.setOnClickListener {
            validateStudent(view)
        }

        return view
    }

    private fun validateStudent(view: View) {
        val universityCode = view.findViewById<EditText>(R.id.university).text.toString()
        db.collection("Universities")
            .whereEqualTo("universityCode", universityCode)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "The university code is invalid", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val universityId = documents.documents[0].id
                    val universityName = documents.documents[0].getString("name").toString()
                    createStudent(view, universityName, universityId)
                }
            }
    }

    private fun createStudent(view: View, universityName: String, universityId: String) {
        val name = view.findViewById<EditText>(R.id.name).text.toString()
        val cv = view.findViewById<EditText>(R.id.cv).text.toString()
        val email = view.findViewById<EditText>(R.id.email).text.toString()
        val password = view.findViewById<EditText>(R.id.password).text.toString()

        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val studentId = authResult.user?.uid ?: return@addOnSuccessListener
                val newStudent = Student(email, name, universityName, "", cv, 0, "/$universityId")
                db.collection("Candidates")
                    .add(newStudent)
                    .addOnSuccessListener { createdStudent ->
                        val user = User("Student", createdStudent.id)
                        db.collection("Users").document(studentId)
                            .set(user)
                            .addOnSuccessListener {
                                goToStudentHome(createdStudent.id)
                            }
                    }
            }

    }

    private fun goToStudentHome(id: String) {
        val fragment = StudentHomeFragment()
        val bundle = Bundle()
        bundle.putString("id", id)
        fragment.arguments = bundle
        parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}