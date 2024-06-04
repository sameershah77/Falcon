package com.example.falcon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.falcon.model.ChatMessageData
import com.example.falcon.model.SingleChat
import com.example.falcon.repositories.MemberRepository
import com.example.falcon.repositories.SIngleChatRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SingleChatViewModel(private val repository: SIngleChatRepository) :ViewModel() {
    private val _messageList = MutableLiveData<List<SingleChat>>(emptyList())
    val messageList: LiveData<List<SingleChat>> = _messageList

    init {
        fetchMessage()
    }

    fun addMessage(message:String,email:String,name:String,time:String,chatId:String, callback: (Boolean) -> Unit)  {
        repository.addMessage(message,email,name,time,chatId) {
            if(it == true) {
                callback(true)
            }
            else {
                callback(false)
            }
        }
    }

    fun fetchMessage() {
        repository.fetchMessage() { messageList ->
            _messageList.value = messageList
        }
    }


}