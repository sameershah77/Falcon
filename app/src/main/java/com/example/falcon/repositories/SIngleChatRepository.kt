package com.example.falcon.repositories

import android.util.Log
import com.example.falcon.model.ChatMessageData
import com.example.falcon.model.MemberData
import com.example.falcon.model.SingleChat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SIngleChatRepository(firebaseDatabase: FirebaseDatabase,chatId: String) {

    val userReference = firebaseDatabase.reference.child("Users")
    val messageRef = userReference.child("Chats")

    val chatId = chatId
    fun addMessage(message:String,email:String,name:String,time:String,chatId:String, callback: (Boolean) -> Unit) {
        messageRef.child(chatId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Here you can access the data
                val chat = dataSnapshot.getValue(ChatMessageData::class.java)!!
                chat.messageArr.add(SingleChat(email,name,message,time))
                messageRef.child(chatId).setValue(chat)
                    .addOnSuccessListener {
                        callback(true)
                    }
                    .addOnFailureListener {
                        callback(false)
                    }

                Log.d("kjlsdnlkcnkls","UID is : ${chatId}")
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun fetchMessage(callback: (List<SingleChat>) -> Unit) {
        messageRef.child(chatId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val messageList = mutableListOf<SingleChat>()
                // Here you can access the data
                val chat = dataSnapshot.getValue(ChatMessageData::class.java)!!
                for(x in chat.messageArr) {
                    messageList.add(x)
                }
                Log.d("Repository", "Fetched message list: $messageList")
                callback(messageList)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Repository", "fetchMessage cancelled: ${databaseError.message}")
            }
        })
    }

}