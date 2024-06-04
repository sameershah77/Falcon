package com.example.falcon.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.example.falcon.R
import com.example.falcon.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        //default focus
        binding.signUpFname.requestFocus()
        binding.signUpFname.setBackgroundResource(R.drawable.new_edit_text_design)


        updateGlowOfEditText(binding.signUpFname)
        updateGlowOfEditText(binding.signUpLname)
        updateGlowOfEditText(binding.signUpEmail)
        updateGlowOfEditText(binding.signUpPassword)
        updateGlowOfEditText(binding.signUpConfirmPassword)

        binding.parent.setOnClickListener {
            hideKeyboardAndFocuse(binding.signUpFname)
            hideKeyboardAndFocuse(binding.signUpLname)
            hideKeyboardAndFocuse(binding.signUpEmail)
            hideKeyboardAndFocuse(binding.signUpPassword)
            hideKeyboardAndFocuse(binding.signUpConfirmPassword)
        }


        binding.submit.setOnClickListener {
            val fname = binding.signUpFname.text.toString()
            val lname = binding.signUpLname.text.toString()
            val email = binding.signUpEmail.text.toString()
            val pass1 = binding.signUpPassword.text.toString()
            val pass2 = binding.signUpConfirmPassword.text.toString()

            if (fname.length == 0 && lname.length == 0 && email.length == 0 && pass1.length == 0 && pass2.length == 0) {
                Toast.makeText(this, "Please Fill All the Fields", Toast.LENGTH_SHORT).show()
            } else if (fname.length == 0) {
                Toast.makeText(this, "Please Fill First Name", Toast.LENGTH_SHORT).show()
            } else if (lname.length == 0) {
                Toast.makeText(this, "Please Fill Last Name", Toast.LENGTH_SHORT).show()
            } else if (email.length == 0) {
                Toast.makeText(this, "Please Fill Email", Toast.LENGTH_SHORT).show()
            } else if (pass1.length == 0) {
                Toast.makeText(this, "Please Fill password", Toast.LENGTH_SHORT).show()
            } else if (pass2.length == 0) {
                Toast.makeText(this, "Please Fill confirm password", Toast.LENGTH_SHORT).show()
            } else if (pass1 != pass2) {
                Toast.makeText(this, "Password are doesn't match", Toast.LENGTH_SHORT).show()
            } else if (pass1 == pass2 && pass1.length < 8) {
                Toast.makeText(
                    this,
                    "Password should contains at least 8 digits",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(this, OTPActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("pass", pass1)
                intent.putExtra("fname", fname)
                intent.putExtra("lname", lname)
                startActivity(intent)
                finish()
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