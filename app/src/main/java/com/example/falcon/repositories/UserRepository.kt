package com.example.falcon.repositories

import android.util.Log
import com.example.falcon.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserRepository(firebaseDatabase: FirebaseDatabase) {

    private val userReference = firebaseDatabase.reference.child("Users")
    .child(Firebase.auth.currentUser!!.uid).child("User_Personal_Information")

    fun addUser(user: User, callback: (Boolean) -> Unit) {
        userReference.setValue(user)
            .addOnSuccessListener {
                callback(true)  // Data set successfully
            }
            .addOnFailureListener { exception ->
                callback(false) // Data setting failed
            }
    }
    fun updateUser(user: User, callback: (Boolean) -> Unit) {
        userReference.setValue(user)
            .addOnSuccessListener {
                callback(true)  // Data set successfully
            }
            .addOnFailureListener { exception ->
                callback(false) // Data setting failed
            }
    }

    fun fetchUsers(callback: (User) -> Unit) {
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Here you can access the data
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    callback(user)
                    Log.d("UserRepository", "Data Mill gaya")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.d("UserRepository", "Data nahi Mila")
            }
        })
    }
}