package com.example.falcon.recyclerview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.falcon.R
import com.example.falcon.model.ChatMessageData
import com.example.falcon.model.User
import com.example.falcon.repositories.ChatMessageRepository
import com.example.falcon.view.ChatActivity
import com.example.falcon.viewmodel.ChatMessageViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ChatsRecyclerViewAdapter(context: Context,activity: Activity, arr:List<ChatMessageData>, userRef : DatabaseReference, user:User) : RecyclerView.Adapter<ChatsRecyclerViewAdapter.ViewHolder>() {
    val context = context
    val activity = activity
    val arr = arr
    val userRef = userRef
    val user = user
    val myUid = Firebase.auth.currentUser!!.uid

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.layout_chat_img)
        val name = itemView.findViewById<TextView>(R.id.layout_chat_name)
        val email = itemView.findViewById<TextView>(R.id.layout_chat_email)
        val plate = itemView.findViewById<CardView>(R.id.layout_chat_plate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_layout,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatMessageData = arr[position]

        //current user information
        var otherImg:String = user.img
        var otherName:String = user.fName

        for(uid in chatMessageData.uidArr) {
            if(uid != myUid) {
                val user = userRef.child(uid).child("User_Personal_Information")
                user.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Here you can access the data
                        val user = dataSnapshot.getValue(User::class.java)
                        if (user != null) {
                            //Setting current user information
                            val name = user.fName +" "+ user.lName
                            holder.name.text = name
                            holder.email.text = user.email
                            Picasso.get().load(user.img.toUri()).into(holder.img);

                            otherImg = user.img
                            otherName = name
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
            }
        }

        holder.plate.setOnClickListener {
            val intent = Intent(context,ChatActivity::class.java)
            val chatId = chatMessageData.chatId

            // unique chat id
            intent.putExtra("chatId",chatId)

            //My information
            intent.putExtra("fname",user.fName)
            intent.putExtra("lname",user.lName)
            intent.putExtra("email",user.email)

            //current user information
            intent.putExtra("otherImg",otherImg)
            intent.putExtra("otherName",otherName)
            context.startActivity(intent)

        }
    }
    override fun getItemCount(): Int {
        return arr.size
    }
}