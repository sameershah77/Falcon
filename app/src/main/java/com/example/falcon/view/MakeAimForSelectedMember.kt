package com.example.falcon.view

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.falcon.R
import com.example.falcon.databinding.ActivityMakeAimForSelectedMemberBinding
import com.example.falcon.model.MemberData
import com.example.falcon.model.Task
import com.example.falcon.model.TaskMember
import com.google.android.material.chip.Chip
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MakeAimForSelectedMember : AppCompatActivity() {
    private lateinit var binding:ActivityMakeAimForSelectedMemberBinding
    var arr: ArrayList<Task> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeAimForSelectedMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tasks: ArrayList<Task>? = intent.getParcelableArrayListExtra<Task>("taskList")

        if (tasks != null) {
            arr = tasks
            Log.d("tasks","${arr.size}")
            binding.numberOfTask.setText(arr.size.toString())
        }
        val currentMember = intent.getParcelableExtra<MemberData>("currentMember")

        binding.name.setText("Make aim for ${currentMember?.name}")
        binding.email.setText(currentMember?.email)

        binding.viewTask.setOnClickListener {
            val intent = Intent(this, AssignTaskActivity::class.java)
            intent.putParcelableArrayListExtra("taskList", arr)
            startActivity(intent)
        }
        binding.assignTask.setOnClickListener {
            val intent = Intent(this, AssignTaskActivity::class.java)
            intent.putParcelableArrayListExtra("taskList", arr)
            intent.putExtra("currentMember", currentMember)
            startActivity(intent)
        }

        binding.addTechnology.setOnClickListener{
            if(!binding.enterTechnology.text.toString().isEmpty()){
                addTechnologyChip(binding.enterTechnology.text.toString())
                binding.enterTechnology.setText("")
            }
        }

        binding.addTag.setOnClickListener{
            if(!binding.enterTags.text.toString().isEmpty()){
                addTagChip(binding.enterTags.text.toString())
                binding.enterTags.setText("")
            }
        }

        binding.saveTask.setOnClickListener {
            val technologyArr : ArrayList<String> = ArrayList()
            for (i in 0 until binding.technologyChipGroup.childCount) {
                val child = binding.technologyChipGroup.getChildAt(i)
                if (child is Chip) {  // Ensure it's a Chip
                    technologyArr.add(child.text.toString())
                }
            }

            val tagArr : ArrayList<String> = ArrayList()
            for (i in 0 until binding.tagChipGroup.childCount) {
                val child = binding.tagChipGroup.getChildAt(i)
                if (child is Chip) {  // Ensure it's a Chip
                    tagArr.add(child.text.toString())
                }
            }

            if(tasks!!.isEmpty() && technologyArr.isEmpty() && tagArr.isEmpty()) {
                showToast(R.drawable.alert,"Please fill all necessory fields",R.color.toast_yello)
            }
            else if(tasks!!.isEmpty()){
                showToast(R.drawable.alert,"Please add tasks",R.color.toast_yello)
            }
            else if(tagArr.isEmpty()) {
                showToast(R.drawable.alert,"Please add some tags",R.color.toast_yello)
            }
            else if(technologyArr.isEmpty()){
                showToast(R.drawable.alert, "Please add some technologies",R.color.toast_yello)
            }
            else {
                val taskMember = TaskMember(currentMember, tasks!!, tagArr, technologyArr)
                val userReference = FirebaseDatabase.getInstance().reference.child("Users")
                    .child(Firebase.auth.currentUser!!.uid).child("Temp_Member_Task")

                // Query to check if a similar taskMember already exists
                val query = userReference.orderByChild("currentMember/email").equalTo(taskMember.currentMember?.email)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Similar taskMember exists, handle accordingly
                                showToast(R.drawable.alert,"User Already Added", R.color.toast_yello)
                            } else {
                                // No similar taskMember exists, add it to the database
                                userReference.push().setValue(taskMember)
                                    .addOnSuccessListener {
                                        showToast(R.drawable.done,"User Successfully Added", R.color.toast_green)
                                    }
                                    .addOnFailureListener { e ->

                                    }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle database error
                            Log.w(TAG, "Failed to read value.", databaseError.toException())
                        }
                    })
                val intent = Intent(this, MakeProjectActivity::class.java)
                intent.putExtra("taskMember", taskMember)
                startActivity(intent)
                finish()
            }
        }
    }


    private fun addTechnologyChip(text: String) {
        val chip = Chip(this)
        chip.text = text
        chip.setTextColor(resources.getColor(R.color.grey7))
        chip.isCloseIconVisible = true
        // Change the background color of the chip
        chip.setChipBackgroundColorResource(R.color.grey6)

        chip.setOnCloseIconClickListener {
            binding.technologyChipGroup.removeView(chip)
        }
        binding.technologyChipGroup.addView(chip)
    }


    private fun addTagChip(text: String) {
        val chip = Chip(this)
        chip.text = text

        chip.setTextColor(resources.getColor(R.color.grey7))
        chip.isCloseIconVisible = true
        // Change the background color of the chip
        chip.setChipBackgroundColorResource(R.color.grey6)

        chip.setOnCloseIconClickListener {
            binding.tagChipGroup.removeView(chip)
        }
        binding.tagChipGroup.addView(chip)
    }

    private fun showToast(iconResId: Int, message: String, textColor: Int) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val toastView = inflater.inflate(R.layout.custom_toast, null)

        val iconImageView = toastView.findViewById<ImageView>(R.id.toast_icon)
        val messageTextView = toastView.findViewById<TextView>(R.id.toast_message)

        iconImageView.setImageResource(iconResId)
        messageTextView.text = message
        messageTextView.setTextColor(ContextCompat.getColor(this, textColor))

        val toast = Toast(this)
        toast.view = toastView
        toast.setGravity(Gravity.BOTTOM, 0, 400)
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }
}