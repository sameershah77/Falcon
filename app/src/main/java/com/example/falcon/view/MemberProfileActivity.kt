package com.example.falcon.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.falcon.R
import com.squareup.picasso.Picasso

class MemberProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_profile)



        val memberIntent = intent
        val name = memberIntent.getStringExtra("name").toString()
        val email = memberIntent.getStringExtra("email").toString()
        val img = memberIntent.getStringExtra("img").toString()
        val date = memberIntent.getStringExtra("date").toString()
        val desc = memberIntent.getStringExtra("desc").toString()
        val uid = memberIntent.getStringExtra("uid").toString()

        val member_profile_name = findViewById<TextView>(R.id.member_profile_name)
        val member_profile_email = findViewById<TextView>(R.id.member_profile_email)
        val member_profile_date = findViewById<TextView>(R.id.member_profile_date)
        val member_profile_desc = findViewById<TextView>(R.id.member_profile_desc)
        val member_profile_img = findViewById<ImageView>(R.id.member_profile_img)

        member_profile_name.text = name
        member_profile_date.text = date
        member_profile_email.text = email
        member_profile_desc.text = desc
        Picasso.get().load(img).into(member_profile_img)
    }


}