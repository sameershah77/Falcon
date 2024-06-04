package com.example.falcon.view

import UserViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.falcon.databinding.ActivityDiscussBinding
import com.example.falcon.model.ChatMessageData
import com.example.falcon.recyclerview.ChatsRecyclerViewAdapter
import com.example.falcon.repositories.ChatMessageRepository
import com.example.falcon.repositories.UserRepository
import com.example.falcon.viewmodel.ChatMessageViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class DiscussActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiscussBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiscussBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backDiscuss.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
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

        //chat message view model
        val chatMessageRepository = ChatMessageRepository(firebaseDatabase)
        val chatMessageViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ChatMessageViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ChatMessageViewModel(chatMessageRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }).get(ChatMessageViewModel::class.java)

        chatMessageViewModel.fetchMyChats()

        val userReference = firebaseDatabase.reference.child("Users")

        val layoutManager = LinearLayoutManager(this)

        binding.discussRecyclerview.layoutManager = layoutManager

        chatMessageViewModel.chatMessageList.observe(this) { chatList ->
            val newChatList : MutableList<ChatMessageData> = mutableListOf()
            val myUid = Firebase.auth.currentUser!!.uid
            for(chatMessageData in chatList) {
                if(chatMessageData.isGroup) {

                }
                else {
                    var check = false
                    for (uid in chatMessageData.uidArr) {
                        if (uid != myUid) {
                            check = true
                            break
                        }
                    }
                    if(check) {
                        Log.d("jhsdvbd","ikdbnckjdsnd...........")
                        newChatList.add(chatMessageData)
                    }
                }
            }
            userViewModel.userData.observe(this){ user ->
                val adapter = ChatsRecyclerViewAdapter(this,this@DiscussActivity, newChatList,userReference,user)
                binding.discussRecyclerview.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
    }
}