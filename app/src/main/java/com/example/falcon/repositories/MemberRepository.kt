package com.example.falcon.repositories

import android.util.Log
import android.widget.Toast
import com.example.falcon.model.MemberData
import com.example.falcon.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MemberRepository(firebaseDatabase: FirebaseDatabase) {

    val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
    val usersRef = firebaseDatabase.getReference("Users")
    val memberDataRef = usersRef.child(currentUserUid).child("Member_Data")

    fun addMember(member: MemberData, callback: (Boolean) -> Unit) {
        memberDataRef.push().setValue(member)
            .addOnSuccessListener {
                callback(true)  // Data set successfully
            }
            .addOnFailureListener { exception ->
                callback(false) // Data setting failed
            }
    }

    fun fetchMembersList(callback: (List<MemberData>) -> Unit) {
        memberDataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val members = mutableListOf<MemberData>()
                for (childSnapshot in dataSnapshot.children) {
                    val member = childSnapshot.getValue(MemberData::class.java)
                    if (member != null) {
                        members.add(member)
                    }
                }
                callback(members)
                for(x in members) {
                    Log.d("uid","${x.uid}, ${x.name}, ${x.email}, ${members.size}")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }

    fun updateAllMembers() {
        memberDataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val memberKey = childSnapshot.key as String
                    val member = childSnapshot.getValue(MemberData::class.java)

                    val uid = member!!.uid
                    val currentMember = usersRef.child(uid).child("User_Personal_Information")
                    currentMember.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // Here you can access the data
                            val user = dataSnapshot.getValue(User::class.java) as User
                            val name = ("${user.fName} ${user.lName}")
                            val email = user.email
                            val registerDate = user.registerDate
                            val img = user.img
                            val desc = user.desc

                            val updatedMember = MemberData(name,desc,email,registerDate,img,uid)
                            memberDataRef.child(memberKey).setValue(updatedMember)
                            Log.d("newUid","${uid}, ${memberKey}, ${name}, ${email} , ")
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle errors
                        }
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }
}