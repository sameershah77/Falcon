package com.example.falcon.repositories

import com.example.falcon.model.ChatMessageData
import com.example.falcon.model.MemberData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ChatMessageRepository(firebaseDatabase: FirebaseDatabase) {

    private val usersRef = firebaseDatabase.getReference("Users")
    private val chatRef = usersRef.child("Chats")

    private val myUid = Firebase.auth.currentUser!!.uid

    fun addChat(chat: ChatMessageData, callback: (Boolean) -> Unit) {
        val key  = chatRef.push().key!!
        chat.chatId = key
        chatRef.child(key).setValue(chat)
            .addOnSuccessListener {
                callback(true)  // Data set successfully
            }
            .addOnFailureListener { exception ->
                callback(false) // Data setting failed
            }
    }

    fun fetchMyChats(callback: (List<ChatMessageData>) -> Unit) {
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chatsArr = mutableListOf<ChatMessageData>()
                for (childSnapshot in dataSnapshot.children) {
                    val chat = childSnapshot.getValue(ChatMessageData::class.java)
                    if (chat != null) {
                        for(x in chat.uidArr) {
                            if(x == myUid) {
                                chatsArr.add(chat)
                                break
                            }
                        }
                    }
                }
                callback(chatsArr)
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }
}