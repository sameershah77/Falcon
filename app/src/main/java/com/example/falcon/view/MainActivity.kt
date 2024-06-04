package com.example.falcon.view

import UserViewModel
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.falcon.R
import com.example.falcon.databinding.ActivityMainBinding
import com.example.falcon.databinding.ActivitySignUpBinding
import com.example.falcon.fragments.HomeFragment
import com.example.falcon.fragments.ProfileFragment
import com.example.falcon.fragments.ProgressFragment
import com.example.falcon.fragments.ProjectFragment
import com.example.falcon.fragments.TaskFragment
import com.example.falcon.model.User
import com.example.falcon.repositories.UserRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding:ActivityMainBinding
    private lateinit var userData:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val FBAuth = FirebaseAuth.getInstance()
        if(FBAuth.currentUser == null) {
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }
        // code to fetch User_Personal_Information node data
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val userRepository = UserRepository(firebaseDatabase)

        val userViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return UserViewModel(userRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }).get(UserViewModel::class.java)

        //Code For drawer
        setSupportActionBar(binding.toolbar)
        // Set the title with custom font

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.OpenNavigationDrawer,
            R.string.CloseNavigationDrawer
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this@MainActivity)
        val header = binding.navigationDrawer.getHeaderView(0)
        val header_img = header.findViewById<ImageView>(R.id.header_img)
        val header_name = header.findViewById<TextView>(R.id.header_name)
        val header_email = header.findViewById<TextView>(R.id.header_email)

        userViewModel.userData.observe(this) {user ->
            Log.d("tag","${user.fName}, ${user.lName}, ${user.email}")
            val firstName = user.fName
            val lastName = user.lName
            val name = "$firstName $lastName"
            // Update header name and email
            header_name.text = name
            header_email.text = user.email
            Picasso.get().load(user.img.toUri()).into(header_img);
            userData = user
        }

        //code for bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val itemId = item.itemId
            if (itemId == R.id.bottom_home) {
                replaceFragment(HomeFragment())
            } else if (itemId == R.id.bottom_project) {
                replaceFragment(ProjectFragment())
            } else if (itemId == R.id.bottom_profile) {
                replaceFragment(ProfileFragment())
            } else if (itemId == R.id.bottom_progress) {
                replaceFragment(ProgressFragment())
            } else {
                replaceFragment(TaskFragment())
            }
            true
        }
        //default fragment
        replaceFragment(HomeFragment())


        // deleting the Temp_Member_Task which is the temporery node
        val userReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Temp_Member_Task")
        userReference.removeValue()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.drawer_storage) {
            startActivity(Intent(this, StorageActivity::class.java))
        } else if (itemId == R.id.drawer_members) {
            startActivity(Intent(this, MemberActivity::class.java))
        } else if (itemId == R.id.drawer_rewards) {
            startActivity(Intent(this, RewardActivity::class.java))
        } else if (itemId == R.id.drawer_history) {
            startActivity(Intent(this, HistoryActivity::class.java))
        } else if (itemId == R.id.drawer_scheduling_and_remainder) {
            startActivity(Intent(this, ScheduleActivity::class.java))
        } else if (itemId == R.id.drawer_discuss) {
            val intent = Intent(this, DiscussActivity::class.java)
            intent.putExtra("userData",userData)
            startActivity(intent)
        } else if (itemId == R.id.drawer_roadmap) {
            startActivity(Intent(this, RoadmapActivity::class.java))
        } else {
            //by default setting of logOutDialog
            val LogOutDialog = AlertDialog.Builder(this@MainActivity)
            LogOutDialog.setTitle("LogOut")
            LogOutDialog.setIcon(R.drawable.alert)
            LogOutDialog.setMessage("Are you sure wants to Log out")

            LogOutDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, SignUpActivity::class.java))
                finish()
            })
            LogOutDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(this@MainActivity, "Log out canceled", Toast.LENGTH_SHORT).show()
            })
            LogOutDialog.show()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setActionBarTitleWithCustomFont() {
        val typeface = Typeface.createFromAsset(assets, "rakkas_regular.ttf")
        val spannableString = SpannableString("Falcon")
        spannableString.setSpan(
            CustomTypefaceSpan("", typeface),
            0,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        supportActionBar?.title = spannableString
    }

    private inner class CustomTypefaceSpan(family: String?, private val newType: Typeface) :
        TypefaceSpan(family) {
        override fun updateDrawState(ds: TextPaint) {
            applyCustomTypeFace(ds, newType)
        }

        override fun updateMeasureState(paint: TextPaint) {
            applyCustomTypeFace(paint, newType)
        }

        private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
            paint.typeface = tf
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val fragManager = supportFragmentManager
        val fragTransaction = fragManager.beginTransaction()
        fragTransaction.replace(R.id.frame_layout, fragment)
        fragTransaction.commit()
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START) == true) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}