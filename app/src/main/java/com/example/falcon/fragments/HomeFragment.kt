package com.example.falcon.fragments

import UserViewModel

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.falcon.R
import com.example.falcon.databinding.FragmentHomeBinding
import com.example.falcon.model.Project
import com.example.falcon.model.User
import com.example.falcon.recyclerview.DashBoardRecyclerViewAdapter
import com.example.falcon.repositories.ProjectRepository
import com.example.falcon.repositories.UserRepository
import com.example.falcon.view.DiscussActivity
import com.example.falcon.view.MemberActivity
import com.example.falcon.view.RewardActivity
import com.example.falcon.view.RoadmapActivity
import com.example.falcon.view.ScheduleActivity
import com.example.falcon.view.StorageActivity
import com.example.falcon.viewmodel.ProjectViewModel
import com.google.firebase.database.FirebaseDatabase


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var projectArr : ArrayList<Project> = ArrayList()
    private lateinit var userData:User
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.allProjects.setOnClickListener {
            replaceFragment(ProjectFragment(), requireActivity().supportFragmentManager)
        }

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

        val loaderDialog = Dialog(requireContext())
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

        loaderDialog.show()
        //User details from view model
        userViewModel.userData.observe(requireActivity()) {user ->
            binding.dashboardName.setText(user.fName)
            if(user != null) {
                loaderDialog.dismiss()
            }
            userData = user
        }

        val dashboard_logo = view.findViewById<ImageView>(R.id.dashboard_logo)
        val animation = AnimationUtils.loadAnimation(context, R.anim.translation_vertical)
        dashboard_logo.startAnimation(animation)

        // Shortcut handle
        binding.shortcutMembers.setOnClickListener {
            startActivity(Intent(requireContext(),MemberActivity::class.java))
        }
        binding.shortcutDiscuss.setOnClickListener {
            val intent = Intent(requireContext(), DiscussActivity::class.java)
            intent.putExtra("userData",userData)
            startActivity(intent)
        }
        binding.shortcutRewards.setOnClickListener {
            startActivity(Intent(requireContext(),RewardActivity::class.java))
        }
        binding.shortcutRoadmap.setOnClickListener {
            startActivity(Intent(requireContext(),RoadmapActivity::class.java))
        }
        binding.shortcutSchedule.setOnClickListener {
            startActivity(Intent(requireContext(),ScheduleActivity::class.java))
        }
        binding.shortcutStorage.setOnClickListener {
            startActivity(Intent(requireContext(),StorageActivity::class.java))
        }

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.dashboardRecyclerView.layoutManager = layoutManager

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
            userViewModel.userData.observe(requireActivity()) {user ->
            val adapter = DashBoardRecyclerViewAdapter(requireContext(), projectArr,user.email)
            binding.dashboardRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            }
        }

        binding.dashboardProjectPreview.dashboardProjectPreview.visibility = View.VISIBLE
        binding.dashboardProjectPreview.dashboardProjectPreview.setOnClickListener {
            replaceFragment(ProjectFragment(), requireActivity().supportFragmentManager)
        }
        projectViewModel.projectDataList.observe(viewLifecycleOwner) { projects ->
            if(projects.isEmpty()) {
                val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.translation)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {

                    }
                    override fun onAnimationEnd(animation: Animation?) {
                        binding.dashboardProjectPreview.dashboardProjectPreview.visibility = View.GONE
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                })

            } else {
                binding.dashboardRecyclerView.visibility = View.VISIBLE
                binding.dashboardProjectPreview.dashboardProjectPreview.visibility = View.GONE
                binding.dashboardProjectPreview.dashboardProjectPreview.clearAnimation() // Clear animation if it's still running
            }
        }



        // Fetch projects from Firebase
        projectViewModel.fetchProjects()
        return view
    }
    fun replaceFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        val fragTransaction = fragmentManager.beginTransaction()
        fragTransaction.replace(R.id.frame_layout, fragment)
        fragTransaction.commit()
    }
}