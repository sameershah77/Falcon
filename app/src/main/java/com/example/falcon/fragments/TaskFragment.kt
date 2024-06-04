package com.example.falcon.fragments

import UserViewModel
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.falcon.R
import com.example.falcon.databinding.FragmentProfileBinding
import com.example.falcon.databinding.FragmentTaskBinding
import com.example.falcon.model.Project
import com.example.falcon.model.ProjectDetails
import com.example.falcon.model.Task
import com.example.falcon.model.TaskMember
import com.example.falcon.model.TaskModel
import com.example.falcon.recyclerview.AssignTaskRecyclerViewAdapter
import com.example.falcon.recyclerview.ProjectRecyclerViewAdapter
import com.example.falcon.recyclerview.TaskRecyclerViewAdapter
import com.example.falcon.repositories.ProjectRepository
import com.example.falcon.repositories.UserRepository
import com.example.falcon.viewmodel.ProjectViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class TaskFragment : Fragment() {
    private var myTaskArr:ArrayList<Triple<TaskModel,String,MutableList<String>>> = ArrayList()
    private lateinit var binding:FragmentTaskBinding
    private var email = ""
    lateinit var adapter:TaskRecyclerViewAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTaskBinding.inflate(inflater, container, false)
        val view = binding.root

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

        val recyclerView = binding.taskRecyclerview
        recyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)

        userViewModel.userData.observe(requireActivity()){ user ->
            email = user.email
            projectViewModel.fetchMyTasks(user.email)
            val adapter = TaskRecyclerViewAdapter(requireContext(),myTaskArr,email)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        projectViewModel.myTaskList.observe(viewLifecycleOwner) { myTasks ->
            myTaskArr.addAll(myTasks)
            adapter = TaskRecyclerViewAdapter(requireContext(),myTaskArr,email)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        binding.taskIllustration.visibility = View.VISIBLE
        projectViewModel.myTaskList.observe(viewLifecycleOwner) { myTasks ->
            if(myTasks.isEmpty()) {
                val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.translation_vertical)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        binding.taskIllustration.visibility = View.GONE
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                binding.taskIllustration.startAnimation(animation)
            } else {
                binding.taskIllustration.visibility = View.GONE
                binding.taskIllustration.clearAnimation() // Clear animation if it's still running
            }
        }

        return view
    }

}