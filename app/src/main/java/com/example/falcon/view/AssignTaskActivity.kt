package com.example.falcon.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.falcon.R
import com.example.falcon.databinding.ActivityAssignTaskBinding
import com.example.falcon.fragments.AssignTaskFragment
import com.example.falcon.model.MemberData
import com.example.falcon.model.Task
import com.example.falcon.recyclerview.AssignTaskRecyclerViewAdapter
import com.example.falcon.recyclerview.DashBoardRecyclerViewAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AssignTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssignTaskBinding
    var arr: ArrayList<Task> = ArrayList()
    lateinit var taskCallback: (Task) -> Unit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssignTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tasks: ArrayList<Task>? = intent.getParcelableArrayListExtra<Task>("taskList")
        val currentMember = intent.getParcelableExtra<MemberData>("currentMember")

        if(tasks != null) {
            arr = tasks
        }

        val recyclerView = binding.assignTaskRecyclerview
        recyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        val adapter = AssignTaskRecyclerViewAdapter(this,arr)
        recyclerView.adapter = adapter

        // Set click listener for the makeTask button
        binding.makeTask.setOnClickListener {
            val bottomSheetDialog : BottomSheetDialogFragment = AssignTaskFragment()
            addTaskCallback { task ->
                arr.add(task)
                adapter.notifyItemInserted(arr.size-1)
            }
            bottomSheetDialog.show(supportFragmentManager,"TEST")
            bottomSheetDialog.enterTransition
        }

//        binding.taskIllustration.visibility = View.VISIBLE
//        if(arr.isEmpty()) {
//            val animation = AnimationUtils.loadAnimation(this, R.anim.translation_vertical)
//            binding.taskIllustration.startAnimation(animation)
//        } else {
//            binding.taskIllustration.visibility = View.GONE
//        }

        binding.doneTask.setOnClickListener {
            val intent = Intent(this, MakeAimForSelectedMember::class.java)
            intent.putParcelableArrayListExtra("taskList", arr)
            intent.putExtra("currentMember", currentMember)
            startActivity(intent)
            finish()
        }
    }
    fun addTaskCallback(callback: (Task) -> Unit) {
        taskCallback = callback
    }
}