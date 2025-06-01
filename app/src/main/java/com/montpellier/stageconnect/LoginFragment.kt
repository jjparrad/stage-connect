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

import com.montpellier.stageconnect.companies.CompanyHomeFragment
import com.montpellier.stageconnect.students.StudentHomeFragment
import com.montpellier.stageconnect.universities.UniversityHomeFragment

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val loginButton = view.findViewById<TextView>(R.id.login)
        loginButton.setOnClickListener {
            val email = view.findViewById<EditText>(R.id.email).text.toString()
            val password = view.findViewById<EditText>(R.id.password).text.toString()
            login(email, password)
        }

        return view
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val userId = user?.uid
                if (userId != null) {
                    getUser(userId) { userData ->
                        onLoginSuccesful(userData)
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Error: user not found", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Wrong username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onLoginSuccesful(user: User) {
        val fragment: Fragment = when (user.role) {
            "Student" -> StudentHomeFragment()
            "Company" -> CompanyHomeFragment()
            else -> UniversityHomeFragment()
        }

        val bundle = Bundle()
        bundle.putString("id", user.entityId)
        fragment.arguments = bundle
        parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun getUser(userId: String, callback: (User) -> Unit) {
        db.collection("Users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("role") ?: ""
                    val entityId = document.getString("entityId") ?: ""
                    val user = User(role, entityId)
                    callback(user)
                } else {
                    Toast.makeText(context, "Error: user not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

}