package com.example.storyboardapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterUser : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mEditTextName: EditText
    private lateinit var mEditTextEmail: EditText
    private lateinit var mEditTextPassword: EditText
    private lateinit var mButtonCreateAccount: Button
    private lateinit var mButtonReturnToMain: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        mAuth = FirebaseAuth.getInstance()

        mEditTextName = findViewById(R.id.editTextLoginEmail)
        mEditTextEmail = findViewById(R.id.editTextLoginPassword)
        mEditTextPassword = findViewById(R.id.editTextRegisterPassword)
        mButtonReturnToMain = findViewById(R.id.buttonRegisterBackToMain)
        mButtonCreateAccount = findViewById(R.id.buttonSignIn)

        mButtonReturnToMain.setOnClickListener {
            finish()
        }
        mButtonCreateAccount.setOnClickListener {
            createAccount()
        }
    }


    fun createAccount() {
        val name: String = mEditTextName.text.toString().trim()
        val email: String = mEditTextEmail.text.toString().trim()
        val password: String = mEditTextPassword.text.toString().trim()

        if (name.isNullOrEmpty()) {
            mEditTextName.error = "name is required"
            return
        }

        if (email.isNullOrEmpty()) {
            mEditTextEmail.error = "email is required"
            return
        }
        val emailRegex = Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'" +
                "*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x" +
                "5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z" +
                "0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4" +
                "][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z" +
                "0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|" +
                "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")
        if (!emailRegex.matches(email) ){
            mEditTextEmail.error = "please enter a valid email"
            return
        }

        if (password.isNullOrEmpty()) {
            mEditTextPassword.error = "password cannot be empty"
            return
        }
        if (password.length < 6) {
            mEditTextPassword.error = "password must have at least 6 characters"
            return
        }

        if (!password.contains(Regex("[0123456789]"))) {
            mEditTextPassword.error = "password must have at least 1 number"
            return
        }

        // Register user to firebase
        val x = mAuth!!.createUserWithEmailAndPassword(email, password)

        x.addOnCompleteListener { task ->
//            progressBar.visibility = View.GONE
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Registration successful", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@RegisterUser, Home::class.java))
            } else {
                Toast.makeText(applicationContext, "Registration Failed: " + task.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }


    }
}