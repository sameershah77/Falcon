package com.example.falcon.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.falcon.R
import com.example.falcon.databinding.ActivityAddMemberBinding
import com.example.falcon.model.MemberData
import com.example.falcon.recyclerview.AddMembersRecyclerViewAdapter
import com.example.falcon.recyclerview.MembersRecyclerViewAdapter
import com.example.falcon.repositories.MemberRepository
import com.example.falcon.viewmodel.MemberViewModel
import com.google.firebase.database.FirebaseDatabase

class AddMemberActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddMemberBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // code to fetch User_Personal_Information node data
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val userRepository = MemberRepository(firebaseDatabase)

        val memberViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MemberViewModel(userRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }).get(MemberViewModel::class.java)

        val layoutManager = LinearLayoutManager(this)
        binding.addMemberRecyclerview.layoutManager = layoutManager
        memberViewModel.memberDataList.observe(this) { memberList ->
            var arr: ArrayList<MemberData> = ArrayList()  // Creates an empty ArrayList
            for(member in memberList) {
                Log.d("addingMember","Live : ${member.name}, ${member.email}, ${member.uid}")
                arr.add(member)
            }
            val adapter = AddMembersRecyclerViewAdapter(this, arr)
            binding.addMemberRecyclerview.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}