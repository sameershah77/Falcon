package com.example.falcon.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.falcon.model.ChatMessageData
import com.example.falcon.model.MemberData
import com.example.falcon.model.User
import com.example.falcon.repositories.ChatMessageRepository


class ChatMessageViewModel(private val repository: ChatMessageRepository): ViewModel()  {
    private val _chatMessageList = MutableLiveData<List<ChatMessageData>>(emptyList())
    val chatMessageList: LiveData<List<ChatMessageData>> = _chatMessageList

    fun addChat(chat: ChatMessageData, callback: (Boolean) -> Unit) {
        repository.addChat(chat) { success ->
            if (success) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    fun fetchMyChats() {
        repository.fetchMyChats() { chats ->
            _chatMessageList.value = chats
        }
    }
}