package com.example.falcon.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.falcon.R
import com.example.falcon.databinding.ActivityOtpactivityBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import papaya.`in`.sendmail.SendMail
import kotlin.random.Random

class OTPActivity : AppCompatActivity() {
    lateinit var email : String
    lateinit var pass : String
    lateinit var fname : String
    lateinit var lname : String

    var random: Int = 0

    private lateinit var binding : ActivityOtpactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent

        email = intent.getStringExtra("email").toString()
        pass = intent.getStringExtra("pass").toString()
        fname = intent.getStringExtra("fname").toString()
        lname = intent.getStringExtra("lname").toString()
        binding.emailSet.text = email

        //animation on illustration image
        val animation = AnimationUtils.loadAnimation(this, R.anim.translation)
        binding.illustrationImg.startAnimation(animation)

        random()


        binding.resendOtp.setOnClickListener {
            binding.otp1.text.clear()
            binding.otp2.text.clear()
            binding.otp3.text.clear()
            binding.otp4.text.clear()
            random()
        }

        binding.otp1.doOnTextChanged { text, start, before, count ->
            if(!binding.otp1.text.toString().isEmpty()) {
                binding.otp2.requestFocus()
            }
            if(!binding.otp2.text.toString().isEmpty()) {
                binding.otp2.requestFocus()
            }
        }
        binding.otp2.doOnTextChanged { text, start, before, count ->
            if(!binding.otp2.text.toString().isEmpty()) {
                binding.otp3.requestFocus()
            }else {
                binding.otp1.requestFocus()
            }
        }
        binding.otp3.doOnTextChanged { text, start, before, count ->
            if(!binding.otp3.text.toString().isEmpty()) {
                binding.otp4.requestFocus()
            }else {
                binding.otp2.requestFocus()
            }
        }
        binding.otp4.doOnTextChanged { text, start, before, count ->
            if(!binding.otp4.text.toString().isEmpty()) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.otp4.windowToken, 0)
            }
            val done_otp = findViewById<Button>(R.id.done_otp)
            done_otp.setOnClickListener {
                val Sotp1 = binding.otp1.text.toString()
                val Sotp2 = binding.otp2.text.toString()
                val Sotp3 = binding.otp3.text.toString()
                val Sotp4 = binding.otp4.text.toString()

                val otp = Sotp1+Sotp2+Sotp3+Sotp4

                if(binding.otp1.text.toString().length == 0 && binding.otp2.text.toString().length == 0 && binding.otp3.text.toString().length == 0 && binding.otp4.text.toString().length == 0) {
                    Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show()
                }
                else if(!otp.equals(random.toString())) {
                    Toast.makeText(this, "Wrong OTP", Toast.LENGTH_SHORT).show()
                }
                else {
                    val intent = Intent(this, SignUpImageActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("pass", pass)
                    intent.putExtra("fname", fname)
                    intent.putExtra("lname", lname)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    fun random() {
        random = Random.nextInt(1000,9999)
        val mail = SendMail("sameershah9167@gmail.com","zvjq psol hmbc xukp",email,"OTP Verification","Welcome to Falcon. Complete your email verification by using the following One-Time Password (OTP):\n" +
                "OTP :  ${random}  \n " +
                "If you find this email in your spam/promotion folder, please click on Report Not as Spam.\n" +
                "\n" +
                "If you encounter any issues, please raise a support ticket here.\n" +
                "\n" +
                "This is a system-generated email; please do not reply to this message.")
        mail.execute()
    }
}