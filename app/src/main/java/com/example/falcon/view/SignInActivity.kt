package com.example.falcon.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.falcon.R
import com.example.falcon.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.createAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        val animation = AnimationUtils.loadAnimation(this, R.anim.translation_vertical)
        binding.signInLogo.startAnimation(animation)

        updateGlowOfEditText(binding.signInEmail)
        updateGlowOfEditText(binding.signInPassword)

        binding.parent.setOnClickListener {
            hideKeyboardAndFocuse(binding.signInEmail)
            hideKeyboardAndFocuse(binding.signInPassword)
        }

        val sign_in = findViewById<Button>(R.id.sign_in)
        sign_in.setOnClickListener {
            val email = binding.signInEmail.text.toString()
            val pass = binding.signInPassword.text.toString()

            if(email.length == 0 && pass.length == 0) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
            else if(email.length == 0) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
            }
            else if(pass.length == 0) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
            }
            else {
                val progressBar = findViewById<ProgressBar>(R.id.sign_in_progressBar)
                progressBar.visibility = View.VISIBLE
                //FireBase Work
                val FBAuth = FirebaseAuth.getInstance()
                FBAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(this, "Login Succesfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    else {
                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(this, "Error ! ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateGlowOfEditText(address: EditText) {
        address.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus == true) {
                address.setBackgroundResource(R.drawable.new_edit_text_design)
            }
            else {
                address.setBackgroundResource(R.drawable.edit_text_design)
            }
        }
    }
    private fun hideKeyboardAndFocuse(address: EditText) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(address.windowToken, 0)
        address.clearFocus()
    }


}