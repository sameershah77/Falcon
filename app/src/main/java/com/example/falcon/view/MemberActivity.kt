package com.example.falcon.view

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.falcon.R
import com.example.falcon.databinding.ActivityMemberBinding
import com.example.falcon.model.ChatMessageData
import com.example.falcon.model.MemberData
import com.example.falcon.model.SingleChat
import com.example.falcon.model.User
import com.example.falcon.recyclerview.MembersRecyclerViewAdapter
import com.example.falcon.repositories.ChatMessageRepository
import com.example.falcon.repositories.MemberRepository
import com.example.falcon.repositories.UserRepository
import com.example.falcon.viewmodel.ChatMessageViewModel
import com.example.falcon.viewmodel.MemberViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MemberActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMemberBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberBinding.inflate(layoutInflater)
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

        val recyclerView = findViewById<RecyclerView>(R.id.member_recyclerview)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        memberViewModel.memberDataList.observe(this) { memberList ->
            var arr: ArrayList<MemberData> = ArrayList()  // Creates an empty ArrayList
            for(member in memberList) {
                arr.add(member)
            }
            val adapter = MembersRecyclerViewAdapter(this, arr)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        binding.memberIllustration.visibility = View.VISIBLE

        memberViewModel.memberDataList.observe(this) { memberList ->
            if(memberList.isEmpty()) {
                val animation = AnimationUtils.loadAnimation(this, R.anim.translation_vertical)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        binding.memberIllustration.visibility = View.GONE
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                binding.memberIllustration.startAnimation(animation)
            } else {
                binding.memberIllustration.visibility = View.GONE
                binding.memberIllustration.clearAnimation() // Clear animation if it's still running
            }
        }

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

        //load Dialog
        val loaderDialog = Dialog(this)
        loaderDialog.setContentView(R.layout.loader_dialog)

        val window = loaderDialog.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
        loaderDialog.setCanceledOnTouchOutside(false)

        loaderDialog.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                loaderDialog.show()
                true
            } else {
                false
            }
        }

        val loaderGif = loaderDialog.findViewById<ImageView>(R.id.loaderGif)

        // Load the GIF with Glide
        Glide.with(this)
            .load(com.example.falcon.R.drawable.loader) // Replace with your GIF resource
            .into(loaderGif)

        
        val add_members_id = findViewById<FloatingActionButton>(R.id.add_members_id)
        add_members_id.setOnClickListener {
            val addMemberDialog = Dialog(this)
            addMemberDialog.setContentView(R.layout.add_members_dialog_design)
            val windo1 = addMemberDialog.window
            windo1?.setBackgroundDrawableResource(android.R.color.transparent)
            windo1?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            windo1?.setGravity(Gravity.CENTER)
            addMemberDialog.show()

            val member_email_id = addMemberDialog.findViewById<EditText>(R.id.member_email_id)
            val add_member_btn_id = addMemberDialog.findViewById<Button>(R.id.add_member_btn_id)

            add_member_btn_id.setOnClickListener {
                val emailToSearch = member_email_id.text.toString()
                if (emailToSearch == "") {
                    showToast(R.drawable.alert, "Please first enter email", R.color.toast_yello)
                } else {
                    loaderDialog.show()
                    // Assuming you have the Firebase database reference
                    val usersRef = FirebaseDatabase.getInstance().getReference("Users")

                    val query: Query = usersRef.orderByChild("User_Personal_Information/email").equalTo(emailToSearch)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if(dataSnapshot.exists()) {


                            for (userSnapshot in dataSnapshot.children) {
                                // Get the user's unique ID (UID)
                                val memberUid = userSnapshot.key
                                // Access the User_Personal_Information node
                                val userInfoSnapshot = userSnapshot.child("User_Personal_Information")
                                // Extract user information
                                val member = userInfoSnapshot.getValue(User::class.java)
                                val firstName = member!!.fName
                                val lastName = member.lName
                                val name = (firstName + " " + lastName)
                                val email = member.email
                                val desc = member.desc
                                val date = member.registerDate
                                val img = member.img
                                val uid = memberUid.toString()

                                //code to the way of data
                                val addMember = MemberData(name,desc,email,date,img,uid)
                                //code to check existence of data
                                val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
                                val usersRef = FirebaseDatabase.getInstance().getReference("Users")
                                val memberDataRef = usersRef.child(currentUserUid).child("Member_Data")

                                val query = memberDataRef.orderByChild("email").equalTo(email)
                                query.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        loaderDialog.show()
                                        if (dataSnapshot.exists()) {
                                            showToast(R.drawable.alert,"Member already exists",R.color.toast_yello)
                                            loaderDialog.dismiss()
                                        } else {
                                            // Data does not exist, proceed to add
                                            memberViewModel.addMember(addMember){
                                                val messageArr : MutableList<SingleChat> = mutableListOf()
                                                val myUid = Firebase.auth.currentUser!!.uid
                                                val uidArr : MutableList<String> = mutableListOf()
                                                uidArr.add(myUid)
                                                uidArr.add(uid)
                                                val x = ChatMessageData(false,"hgchch",messageArr,uidArr)
                                                chatMessageViewModel.addChat(x){

                                                }

                                                if(it) {
                                                    addMemberDialog.dismiss()
                                                    loaderDialog.dismiss()
                                                    showToast(R.drawable.done,"Member succesfully added",R.color.toast_green)
                                                }
                                            }
                                        }
                                    }
                                    override fun onCancelled(databaseError: DatabaseError) {
                                        loaderDialog.dismiss()
                                    }
                                })
                            }
                            }
                            else {
                                showToast(R.drawable.alert,"User not found ",R.color.toast_yello)
                                loaderDialog.dismiss()
                            }
                        }
                        override fun onCancelled( databaseError: DatabaseError) {
                            // Handle errors
                        }
                    })

                }
            }
        }
    }
    private fun showToast(iconResId: Int, message: String, textColor: Int) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val toastView = inflater.inflate(R.layout.custom_toast, null)

        val iconImageView = toastView.findViewById<ImageView>(R.id.toast_icon)
        val messageTextView = toastView.findViewById<TextView>(R.id.toast_message)

        iconImageView.setImageResource(iconResId)
        messageTextView.text = message
        messageTextView.setTextColor(resources.getColor(textColor))

        val toast = Toast(this)
        toast.view = toastView
        toast.setGravity(Gravity.BOTTOM, 0, 400)
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }
}