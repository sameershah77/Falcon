package com.example.falcon.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.falcon.R
import com.example.falcon.databinding.ActivityChatBinding
import com.example.falcon.recyclerview.MembersRecyclerViewAdapter
import com.example.falcon.recyclerview.MessageRecyclerViewAdapter
import com.example.falcon.repositories.MemberRepository
import com.example.falcon.repositories.SIngleChatRepository
import com.example.falcon.viewmodel.SingleChatViewModel
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        //unique chat id
        val chatId = intent.getStringExtra("chatId")!!
        //my info
        val fname = intent.getStringExtra("fname")!!
        val lname = intent.getStringExtra("lname")!!
        val email = intent.getStringExtra("email")!!
        val name = fname + " " + lname

        //current user info
        val otherName = intent.getStringExtra("otherName")
        val otherImg = intent.getStringExtra("otherImg")!!

        val firebaseDatabase = FirebaseDatabase.getInstance()
        val singleChatRepository = SIngleChatRepository(firebaseDatabase,chatId)

        val singleChatViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SingleChatViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return SingleChatViewModel(singleChatRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }).get(SingleChatViewModel::class.java)

        val layoutManager = LinearLayoutManager(this)
        binding.chatMessageRecyclerView.layoutManager = layoutManager

        singleChatViewModel.messageList.observe(this@ChatActivity) { messageList ->

            val adapter = MessageRecyclerViewAdapter(this, messageList,email)
            binding.chatMessageRecyclerView.adapter = adapter
            val itemCount = adapter.itemCount
            val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

            if(itemCount != 0) {
                // Check if the last visible position is near the end of the list
                if (lastVisiblePosition == itemCount - 1 || lastVisiblePosition == RecyclerView.NO_POSITION) {
                    // Scroll to the bottom if already near the end or no items are visible
                    binding.chatMessageRecyclerView.smoothScrollToPosition(itemCount - 1)
                } else {
                    // Scroll to the bottom after adding a new item
                    binding.chatMessageRecyclerView.scrollToPosition(itemCount - 1)
                }
            }
        }

        binding.chatName.text = otherName
        Picasso.get().load(otherImg.toUri()).into(binding.chatImg)

        binding.chatBack.setOnClickListener {
            startActivity(Intent(this,DiscussActivity::class.java))
            finish()
        }

        binding.chatSend.setOnClickListener {
            val message = binding.chatText.text.toString()
            if(!message.isEmpty()) {
                binding.chatText.setText("")
                val time = getCurrentTime()
                singleChatViewModel.addMessage(message,email, name,time,chatId) {
                    if(it == true) {
                        singleChatViewModel.fetchMessage()
                    }
                }
            }
        }
    }

    fun getCurrentTime(): String {
        val currentTime = Date()
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(currentTime)
    }
}