package com.example.falcon.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.falcon.R
import com.example.falcon.databinding.FragmentProjectBinding
import com.example.falcon.model.MemberData
import com.example.falcon.model.Project
import com.example.falcon.model.ProjectDetails
import com.example.falcon.model.TaskMember
import com.example.falcon.recyclerview.AddMembersRecyclerViewAdapter
import com.example.falcon.recyclerview.MembersRecyclerViewAdapter
import com.example.falcon.recyclerview.ProjectRecyclerViewAdapter
import com.example.falcon.repositories.ProjectRepository
import com.example.falcon.view.MakeProjectActivity
import com.example.falcon.viewmodel.ProjectViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProjectFragment : Fragment() {
    private lateinit var binding:FragmentProjectBinding
    private val projectArr : ArrayList<Project> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProjectBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.addProjectId.setOnClickListener {
            startActivity(Intent(view.context,MakeProjectActivity::class.java))
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.projectRecyclerview.layoutManager = layoutManager
        val adapter = ProjectRecyclerViewAdapter(requireContext(), projectArr)
        binding.projectRecyclerview.adapter = adapter
        adapter.notifyDataSetChanged()

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

        // Observe project data list
        projectViewModel.projectDataList.observe(viewLifecycleOwner) { projects ->
            // Update UI with the new project list
            projectArr.clear()
            projectArr.addAll(projects)
            val adapter = ProjectRecyclerViewAdapter(requireContext(), projectArr)
            binding.projectRecyclerview.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        binding.projectIllustration.visibility = View.VISIBLE
        projectViewModel.projectDataList.observe(viewLifecycleOwner) { projects ->
            if(projects.isEmpty()) {
                val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.translation_vertical)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        binding.projectIllustration.visibility = View.GONE
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                binding.projectIllustration.startAnimation(animation)
            } else {
                binding.projectIllustration.visibility = View.GONE
                binding.projectIllustration.clearAnimation() // Clear animation if it's still running
            }
        }
        // Fetch projects from Firebase
        projectViewModel.fetchProjects()

        // deleting the Temp_Member_Task which is the temporery node
        val userReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Temp_Member_Task")
        userReference.removeValue()
        return view
    }
}