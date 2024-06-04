package com.example.falcon.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import com.example.falcon.R
import com.example.falcon.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //animation for splash
        binding.splashLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale))
        binding.splashImg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation))

//        FireBase Work
        val FBAuth = FirebaseAuth.getInstance()
        Handler().postDelayed({
            if(FBAuth.currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else {
                startActivity(Intent(this, SignUpActivity::class.java))
                finish()
            }
        },2800)
    }
}