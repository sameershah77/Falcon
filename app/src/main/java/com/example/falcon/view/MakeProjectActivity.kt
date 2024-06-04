package com.example.falcon.view

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.falcon.R
import com.example.falcon.databinding.ActivityMakeProjectBinding
import com.example.falcon.model.ProjectDetails
import com.example.falcon.model.TaskMember
import com.example.falcon.recyclerview.selectedMemberRecyclerViewAdapter
import com.example.falcon.repositories.MemberRepository
import com.example.falcon.repositories.ProjectRepository
import com.example.falcon.viewmodel.MemberViewModel
import com.example.falcon.viewmodel.ProjectViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MakeProjectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMakeProjectBinding
    private val taskMemberArr: MutableList<TaskMember> = mutableListOf()
    private val idArr: MutableList<String> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.memberRecyclerview.layoutManager = layoutManager

        val userReference = FirebaseDatabase.getInstance().reference.child("Users")
            .child(Firebase.auth.currentUser!!.uid).child("Temp_Member_Task")

        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val arr: MutableList<TaskMember> = mutableListOf()
                for (snapshot in dataSnapshot.children) {
                    val taskMember = snapshot.getValue(TaskMember::class.java)
                    taskMember?.let {
                        arr.add(taskMember)
                        idArr.add(taskMember.currentMember!!.uid)
                    }
                }
                taskMemberArr.addAll(arr)
                for (mem in arr) {
                    for (x in mem.tagArr) {
                        addTagChip(x)
                    }
                    for (x in mem.technologyArr) {
                        addTechnologyChip(x)
                    }
                }
                binding.numberOfMembers.setText(taskMemberArr.size.toString())
                val adapter =
                    selectedMemberRecyclerViewAdapter(this@MakeProjectActivity, taskMemberArr)
                adapter.notifyDataSetChanged()
                binding.memberRecyclerview.adapter = adapter
                // Handle the list of TaskMember objects here
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Log.w(TAG, "Failed to read value.", databaseError.toException())
            }
        })



        binding.projectSelectDeadlineBtn.setOnClickListener {
            // MaterialDatePicker ko initialize karo
            val builder = MaterialDatePicker.Builder.datePicker()

            // Apply custom style
            builder.setTheme(R.style.CustomMaterialDatePicker)

            // DatePickerDialog ke tarah, yaha par bhi set karein initial date
            val calendar = Calendar.getInstance()
            val initialDate = MaterialDatePicker.todayInUtcMilliseconds()
            builder.setSelection(initialDate)

            // MaterialDatePicker ke liye range set karein current date se pehle
            val constraintsBuilder = CalendarConstraints.Builder()
            constraintsBuilder.setValidator(DateValidatorPointForward.now())
            builder.setCalendarConstraints(constraintsBuilder.build())

            // MaterialDatePicker ko create karein
            val datePicker = builder.build()

            // Date select hone par callback set karein
            datePicker.addOnPositiveButtonClickListener { selection ->
                selection?.let {
                    // Yahan par date format karein jaise aap chahein
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = sdf.format(Date(it))
                    // Selected date ko use karne ke liye yahan par kuch aur code likhein
                    binding.projectDeadline.text = formattedDate
                }
            }

            // MaterialDatePicker ko show karein
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        binding.addMemberBtnInProjectId.setOnClickListener {
            startActivity(Intent(this, AddMemberActivity::class.java))
        }
        val loaderDialog = Dialog(this)
        loaderDialog.setContentView(R.layout.loader_dialog)

        val window = loaderDialog.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)

        loaderDialog.setCanceledOnTouchOutside(false)
        loaderDialog.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                loaderDialog.show()
                true
            } else {
                false
            }
        }

        val loaderGif = loaderDialog.findViewById<ImageView>(R.id.loaderGif)

        // Load the GIF with Glide
        Glide.with(this)
            .load(com.example.falcon.R.drawable.loader) // Replace with your GIF resource
            .into(loaderGif)

        binding.makeProjectBtn.setOnClickListener {
            loaderDialog.show()
            val projectName = binding.makeProjectName.text.toString()
            val projectDesc = binding.makeProjectDescription.text.toString()
            val projectNumberOfMember = binding.numberOfMembers.text.toString()
            var projectDeadline = binding.projectDeadline.text.toString()
            if (projectName.isEmpty() &&
                projectDesc.isEmpty() &&
                taskMemberArr.isEmpty() &&
                projectDeadline == ""
            ) {
                showToast(R.drawable.alert, "Please work on above feilds", R.color.toast_yello)
            } else if (projectName.isEmpty()) {
                showToast(R.drawable.alert, "Please write project name", R.color.toast_yello)
            } else if (projectDesc.isEmpty()) {
                showToast(R.drawable.alert, "Please write project description", R.color.toast_yello)
            } else if (taskMemberArr.isEmpty()) {
                showToast(R.drawable.alert, "Please add members in project", R.color.toast_yello)
            } else if (projectDeadline == "") {
                showToast(
                    R.drawable.alert,
                    "Please select deadline for your project",
                    R.color.toast_yello
                )
            } else if(taskMemberArr.size<=1){
                showToast(R.drawable.alert, "Please add more then members", R.color.toast_yello)
            } else {
                val startDate = getCurrentDate()
                val projectDetails =
                    ProjectDetails(projectName, projectDesc, projectDeadline, startDate)
                val projectDataBase = FirebaseDatabase.getInstance()

                val projectRepository = ProjectRepository(projectDataBase)

                val projectViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(ProjectViewModel::class.java)) {
                            @Suppress("UNCHECKED_CAST")
                            return ProjectViewModel(projectRepository) as T
                        }
                        throw IllegalArgumentException("Unknown ViewModel class")
                    }
                }).get(ProjectViewModel::class.java)

                val numOfMembers = idArr.size
                val userUID = Firebase.auth.currentUser!!.uid

                if(idArr.contains(userUID)) {
                    for (x in idArr) {
                        projectViewModel.addProject(x, taskMemberArr, projectDetails,numOfMembers,idArr){
                            if(it == true) {
                                loaderDialog.dismiss()
                                startActivity(Intent(this, CongratsProjectActivity::class.java))
                                finish()
                            }
                        }
                    }
                }
                else {
                    showToast(R.drawable.alert,"Please add you as a member",R.color.toast_yello)
                }
            }
        }
    }

    private fun addTechnologyChip(text: String) {
        val chip = Chip(this)
        chip.text = text
        chip.setTextColor(resources.getColor(R.color.grey7))
        // Change the background color of the chip
        chip.setChipBackgroundColorResource(R.color.grey6)

        binding.technologyChipGroup.addView(chip)
    }

    private fun addTagChip(text: String) {
        val chip = Chip(this)
        chip.text = text

        chip.setTextColor(resources.getColor(R.color.grey7))
        // Change the background color of the chip
        chip.setChipBackgroundColorResource(R.color.grey6)

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

    private fun getCurrentDate(): String {
        // Get the current date and time
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Format the date as a string
        return dateFormat.format(calendar.time)
    }

}