package com.example.storyboardapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


// All register/login stuff from here https://www.youtube.com/watch?v=Z-RE1QuUWPg
class MainActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mButtonRegister : Button
    private lateinit var mButtonLogin : Button
    private lateinit var mEditTextEmail: EditText
    private lateinit var mEditTextPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance();

        mButtonRegister = findViewById(R.id.buttonRegisterBackToMain)
        mButtonLogin = findViewById(R.id.buttonSignIn)
        mEditTextEmail = findViewById(R.id.editTextLoginEmail)
        mEditTextPassword = findViewById(R.id.editTextLoginPassword)
        mButtonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterUser::class.java))
        }

        mButtonLogin.setOnClickListener {
            loginUserAccount()
        }


    }

    private fun loginUserAccount() {

        val email: String = mEditTextEmail.text.toString()
        val password: String = mEditTextPassword.text.toString()

        if (TextUtils.isEmpty(email)) {
            mEditTextEmail.error = "please enter an email"
            return
        }
        if (TextUtils.isEmpty(password)) {
            mEditTextEmail.error = "Please enter a password"
            return
        }

        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Login successful!", Toast.LENGTH_LONG)
                        .show()
                    startActivity(Intent(this, Home::class.java))
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Login failed! Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser: FirebaseUser? = mAuth.currentUser
        if (currentUser != null) {
            // TODO Uncomment this, if user was already logged in before, automatically signs them in
//            startActivity(Intent(this, Home::class.java))
        }
//        updateUI(currentUser)
    }


}