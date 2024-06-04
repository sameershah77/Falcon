package com.example.falcon.fragments

import UserViewModel
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.falcon.R
import com.example.falcon.databinding.FragmentProfileBinding
import com.example.falcon.model.BadgeModel
import com.example.falcon.model.MemberData
import com.example.falcon.model.User
import com.example.falcon.model.coinModel
import com.example.falcon.recyclerview.BadgeRecyclerViewAdapter
import com.example.falcon.recyclerview.HistoryRecyclerViewAdapter
import com.example.falcon.recyclerview.MembersRecyclerViewAdapter
import com.example.falcon.repositories.MemberRepository
import com.example.falcon.repositories.UserRepository
import com.example.falcon.view.MainActivity
import com.example.falcon.viewmodel.MemberViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
    private var cardFlag = false
    var newUri: Uri? = null

    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

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

        val loaderDialog = Dialog(requireContext())
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

        loaderDialog.show()


        //User details from view model
        var firstName = ""
        var lastName = ""
        var description = ""
        var prevImgUri = ""
        userViewModel.userData.observe(requireActivity()){ user ->
            firstName = user.fName
            lastName = user.lName
            description = user.desc
            prevImgUri = user.img
            val name = firstName + " " + lastName
            binding.profileNameId.setText(name)
            binding.profileEmailId.setText(user.email)
            binding.profileDescId.setText(description)

            Picasso.get().load(user.img.toUri()).into(binding.profileImgId)
            Log.d("ujdwszxbisbn","${user.img.toString()}")

            binding.editProfileFname.setText(firstName)
            binding.editProfileLname.setText(lastName)
            binding.editProfileDesc.setText(description)


            if(user != null) {
                loaderDialog.dismiss()
            }
        }

        binding.editProfileId.setOnClickListener {
            val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            val fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)

            if (cardFlag == false) {
                changeCardHeight(390, 520, view)
                binding.editName.visibility = View.VISIBLE
                binding.editImgId.visibility = View.VISIBLE
                binding.editProfileDesc.visibility = View.VISIBLE

                binding.profileDescId.visibility = View.GONE
                binding.profileNameId.visibility = View.GONE
                binding.profileEmailId.visibility = View.GONE

                binding.showBadgeId.visibility = View.GONE
                binding.showCoinId.visibility = View.GONE
                binding.showMemberId.visibility = View.GONE

                binding.profileNameId.startAnimation(fadeOut)
                binding.profileEmailId.startAnimation(fadeOut)
                binding.profileEmailId.startAnimation(fadeOut)

                binding.showBadgeId.startAnimation(fadeOut)
                binding.showCoinId.startAnimation(fadeOut)
                binding.showMemberId.startAnimation(fadeOut)

                binding.editName.startAnimation(fadeIn)
                binding.editImgId.startAnimation(fadeIn)
                binding.editProfileDesc.startAnimation(fadeIn)

                binding.editProfileId.startAnimation(fadeOut)

                Handler().postDelayed({
                    binding.editProfileId.setImageResource(R.drawable.edit_done)
                    binding.editProfileId.startAnimation(fadeIn)
                },800)
                cardFlag = true

            } else {
                if(newUri != null) {
                    replaceImageWithPreviousUri(prevImgUri.toUri(),newUri,binding.profileImgId)

                }
                if(newUri != null || firstName != binding.editProfileFname.text.toString() || lastName != binding.editProfileLname.text.toString() || description != binding.editProfileDesc.text.toString()) {
                    Log.d("show","${prevImgUri} + Here i am")
                    userViewModel.userData.observe(requireActivity()) {user ->
                        user?.apply {
                            fName = binding.editProfileFname.text.toString() // Update fname with the new first name
                            lName = binding.editProfileLname.text.toString() // Update lname with the new last name
                            desc = binding.editProfileDesc.text.toString()
                        }
                        userViewModel.updateUser(user){
                            showToast(R.drawable.done, "Profile changes successfully", R.color.toast_green)
                        }

                        userViewModel.userData.observe(requireActivity()){ user ->
                            firstName = user.fName
                            lastName = user.lName
                            description = user.desc
                            prevImgUri = user.img
                            val name = firstName + " " + lastName
                            binding.profileNameId.setText(name)
                            binding.profileEmailId.setText(user.email)
                            binding.profileDescId.setText(user.desc)
                            Picasso.get().load(user.img.toUri()).into(binding.profileImgId);

                            binding.editProfileFname.setText(firstName)
                            binding.editProfileLname.setText(lastName)
                            binding.editProfileDesc.setText(description)
                        }

                    }
                }
                else {
                    showToast(R.drawable.alert, "It seems that nothing changed", R.color.toast_yello)
                }

                changeCardHeight(520, 390, view)
                binding.editName.visibility = View.GONE
                binding.editImgId.visibility = View.GONE
                binding.editProfileDesc.visibility = View.GONE

                binding.profileNameId.visibility = View.VISIBLE
                binding.profileEmailId.visibility = View.VISIBLE
                binding.profileDescId.visibility = View.VISIBLE

                binding.showBadgeId.visibility = View.VISIBLE
                binding.showCoinId.visibility = View.VISIBLE
                binding.showMemberId.visibility = View.VISIBLE

                binding.profileNameId.startAnimation(fadeIn)
                binding.profileEmailId.startAnimation(fadeIn)
                binding.profileDescId.startAnimation(fadeIn)

                binding.showBadgeId.startAnimation(fadeIn)
                binding.showCoinId.startAnimation(fadeIn)
                binding.showMemberId.startAnimation(fadeIn)

                binding.editName.startAnimation(fadeOut)
                binding.editImgId.startAnimation(fadeOut)
                binding.editProfileDesc.startAnimation(fadeOut)
                binding.editProfileId.startAnimation(fadeOut)
                Handler().postDelayed({
                    binding.editProfileId.setImageResource(R.drawable.edit_profile_img)
                    binding.editProfileId.startAnimation(fadeIn)
                },800)
                cardFlag = false
            }
        }

        val takePicture =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    newUri = result.data?.data!!
                    Picasso.get().load(newUri).into(binding.profileImgId)
                }
            }

        val pickFromGallery =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    newUri = result.data?.data!!
                    Picasso.get().load(newUri).into(binding.profileImgId)
                }
            }

        binding.editImgId.setOnClickListener {
            val uploadImgDialog = Dialog(requireContext())
            uploadImgDialog.setContentView(R.layout.upload_image_design)
            val window = uploadImgDialog.window
            window?.setBackgroundDrawableResource(android.R.color.transparent) // Make the background transparent if needed
            window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Adjust dialog size if needed
            window?.setGravity(Gravity.CENTER) // Center the dialog on the screen


            uploadImgDialog.show()


            val cameraBtn = uploadImgDialog.findViewById<CardView>(R.id.upload_img_of_camera)
            val galleryBtn = uploadImgDialog.findViewById<CardView>(R.id.upload_img_of_gallery)

            cameraBtn.setOnClickListener {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePicture.launch(takePictureIntent)
                uploadImgDialog.dismiss()
            }

            galleryBtn.setOnClickListener {
                val pickFromGalleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickFromGallery.launch(pickFromGalleryIntent)
                uploadImgDialog.dismiss()
            }
            val default = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_default)
            val boy = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_boy)
            val girl = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_girl)
            val man = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_man)
            val woman = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_woman)
            val large_man = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_large_man)
            val large_woman = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_large_woman)
            val muslim_boy = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_muslim_boy)
            val muslim_girl = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_muslim_girl)
            val muslim_man = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_muslim_man)
            val muslim_woman = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_muslim_woman)
            val thief = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_thief)
            val scientist = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_scientist)
            val boy2 = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_boy2)
            val boy3 = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_boy3)

            default.setOnClickListener {
                addSTicker("default_user", binding.profileImgId)
                uploadImgDialog.dismiss()
            }
            boy.setOnClickListener {
                addSTicker("boy", binding.profileImgId)
                uploadImgDialog.dismiss()
            }
            girl.setOnClickListener {
                addSTicker("girl", binding.profileImgId)
                uploadImgDialog.dismiss()
            }

            man.setOnClickListener {
                addSTicker("man", binding.profileImgId)
                uploadImgDialog.dismiss()
            }
            woman.setOnClickListener {
                addSTicker("woman", binding.profileImgId)
                uploadImgDialog.dismiss()
            }
            large_man.setOnClickListener {
                addSTicker("large_man", binding.profileImgId)
                uploadImgDialog.dismiss()
            }
            large_woman.setOnClickListener {
                addSTicker("large_woman", binding.profileImgId)
                uploadImgDialog.dismiss()
            }
            muslim_boy.setOnClickListener {
                addSTicker("muslim_boy", binding.profileImgId)
                uploadImgDialog.dismiss()
            }
            muslim_girl.setOnClickListener {
                addSTicker("muslim_girl", binding.profileImgId)
                uploadImgDialog.dismiss()
            }
            muslim_man.setOnClickListener {
                addSTicker("muslim_man", binding.profileImgId)
                uploadImgDialog.dismiss()
            }
            muslim_woman.setOnClickListener {
                addSTicker("muslim_woman", binding.profileImgId)
                uploadImgDialog.dismiss()
            }
            thief.setOnClickListener {
                addSTicker("thief", binding.profileImgId)
                uploadImgDialog.dismiss()
            }
            scientist.setOnClickListener {
                addSTicker("scientist", binding.profileImgId)
                uploadImgDialog.dismiss()
            }
            boy2.setOnClickListener {
                addSTicker("user", binding.profileImgId)
                uploadImgDialog.dismiss()
            }
            boy3.setOnClickListener {
                addSTicker("showman", binding.profileImgId)
                uploadImgDialog.dismiss()
            }
        }

        //update coins
        val falcoin = firebaseDatabase.reference.child("Users")
            .child(Firebase.auth.currentUser!!.uid).child("Falcoins")

        falcoin.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the value of Falcoins
                var falcoinValue = dataSnapshot.getValue(Int::class.java)
                // Check if falcoinValue is null
                if (falcoinValue == null) {
                    falcoinValue = 0
                }

                binding.showCoinNumber.text = falcoinValue.toString()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("TAG", "Failed to read value.", databaseError.toException())
                binding.showCoinNumber.text = "0"
            }
        })

        //update members

        // code to fetch User_Personal_Information node data
        val memRepository = MemberRepository(firebaseDatabase)

        val memberViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MemberViewModel(memRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }).get(MemberViewModel::class.java)

        memberViewModel.memberDataList.observe(requireActivity()) { memberList ->
            binding.showMemberNumber.text = memberList.size.toString()
        }

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.badge_list_dialog)
        val window1 = dialog.window
        window1?.setBackgroundDrawableResource(android.R.color.transparent) // Make the background transparent if needed
        window1?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Adjust dialog size if needed
        window1?.setGravity(Gravity.CENTER) // Center the dialog on the screen

        val badgeRecyclerView = dialog.findViewById<RecyclerView>(R.id.badgeRecyclerView)
        val layoutManager = GridLayoutManager(requireContext(),2)
        badgeRecyclerView.layoutManager = layoutManager

        val badgeCount = dialog.findViewById<TextView>(R.id.badge_count)
        val closeDialog = dialog.findViewById<ImageView>(R.id.close_badge_layout)


        //adding badges
        val badgeList = firebaseDatabase.reference.child("Users")
            .child(Firebase.auth.currentUser!!.uid).child("Badges")

        badgeList.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val badgeListArr: MutableList<BadgeModel> = mutableListOf()
                for (badgeSnapshot in dataSnapshot.children) {
                    val badgeId = badgeSnapshot.key // Assuming coinId is the key
                    val badge = badgeSnapshot.getValue(BadgeModel::class.java)
                    badgeListArr.add(badge!!)
                }
                badgeCount.text = badgeListArr.size.toString()
                val adapter = BadgeRecyclerViewAdapter(requireContext(),badgeListArr)
                badgeRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()

                if(badgeListArr.size == 0) {
                    binding.badgeNotFound.visibility = View.VISIBLE
                }
                else if(badgeListArr.size == 1) {
                    if(badgeListArr[0].badge == "completion_badge"){
                        binding.showBadge1.setImageResource(R.drawable.completion_badge)
                    }
                    if(badgeListArr[0].badge == "creation_badge"){
                        binding.showBadge1.setImageResource(R.drawable.creation_badge)
                    }
                    binding.showBadge2.setImageResource(R.drawable.dummy_badge)
                    binding.showBadge3.setImageResource(R.drawable.dummy_badge)

                }
                else if(badgeListArr.size == 2) {
                    if(badgeListArr[0].badge == "completion_badge"){
                        binding.showBadge2.setImageResource(R.drawable.completion_badge)
                    }
                    if(badgeListArr[0].badge == "creation_badge"){
                        binding.showBadge2.setImageResource(R.drawable.creation_badge)
                    }

                    if(badgeListArr[1].badge == "completion_badge"){
                        binding.showBadge1.setImageResource(R.drawable.completion_badge)
                    }
                    if(badgeListArr[1].badge == "creation_badge"){
                        binding.showBadge1.setImageResource(R.drawable.creation_badge)
                    }
                    binding.showBadge3.setImageResource(R.drawable.dummy_badge)
                }
                else if(badgeListArr.size == 3) {
                    if(badgeListArr[0].badge == "completion_badge"){
                        binding.showBadge3.setImageResource(R.drawable.completion_badge)
                    }
                    if(badgeListArr[0].badge == "creation_badge"){
                        binding.showBadge3.setImageResource(R.drawable.creation_badge)
                    }

                    if(badgeListArr[1].badge == "completion_badge"){
                        binding.showBadge2.setImageResource(R.drawable.completion_badge)
                    }
                    if(badgeListArr[1].badge == "creation_badge"){
                        binding.showBadge2.setImageResource(R.drawable.creation_badge)
                    }
                    if(badgeListArr[2].badge == "completion_badge"){
                        binding.showBadge1.setImageResource(R.drawable.completion_badge)
                    }
                    if(badgeListArr[2].badge == "creation_badge"){
                        binding.showBadge1.setImageResource(R.drawable.creation_badge)
                    }
                }
                else {
                    if(badgeListArr[badgeListArr.size-3].badge == "completion_badge"){
                        binding.showBadge3.setImageResource(R.drawable.completion_badge)
                    }
                    if(badgeListArr[badgeListArr.size-3].badge == "creation_badge"){
                        binding.showBadge3.setImageResource(R.drawable.creation_badge)
                    }

                    if(badgeListArr[badgeListArr.size-2].badge == "completion_badge"){
                        binding.showBadge2.setImageResource(R.drawable.completion_badge)
                    }
                    if(badgeListArr[badgeListArr.size-2].badge == "creation_badge"){
                        binding.showBadge2.setImageResource(R.drawable.creation_badge)
                    }
                    if(badgeListArr[badgeListArr.size-1].badge == "completion_badge"){
                        binding.showBadge1.setImageResource(R.drawable.completion_badge)
                    }
                    if(badgeListArr[badgeListArr.size-1].badge == "creation_badge"){
                        binding.showBadge1.setImageResource(R.drawable.creation_badge)
                    }
                }
                // Now you have the list of CoinModel objects, you can use it as needed
                // For example, you can pass it to an adapter for display in a RecyclerView
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase Database", "Failed to read value.", databaseError.toException())
            }
        })


        binding.showAllBadge.setOnClickListener {
            dialog.show()
            closeDialog.setOnClickListener {
                dialog.dismiss()
            }
        }
        return view
    }

    private fun changeCardHeight(initialHeightInDP: Int, finalHeightInDP: Int, view: View) {
        val profile_card = view.findViewById<CardView>(R.id.profile_card)

        val scale = resources.displayMetrics.density
        val initialHeight = (initialHeightInDP * scale + 0.5f).toInt()
        val finalHeight = (finalHeightInDP * scale + 0.5f).toInt()
        val layoutParams = profile_card.layoutParams
        layoutParams.height = finalHeight
        profile_card.layoutParams = layoutParams
        val animator = ValueAnimator.ofInt(initialHeight, finalHeight)
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            layoutParams.height = animatedValue
            profile_card.layoutParams = layoutParams
        }
        animator.duration = 500
        animator.start()
    }

    fun replaceImageWithPreviousUri(previousImageUri: Uri, newImageUri: Uri?, profileImageView: ImageView) {
        // Null check for newImageUri
        if (newImageUri == null) {
            // Handle the case when newImageUri is null (e.g., display a message, do nothing, etc.)
            Toast.makeText(requireContext(), "Hello", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the reference to the Firebase Storage location of the previous image
        val storageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(previousImageUri.toString())
        // Upload the new image to replace the previous one
        storageReference.putFile(newImageUri)
            .addOnSuccessListener { _ ->
                // Image uploaded successfully
                // Load the new image into the ImageView using Picasso
                Picasso.get().load(newImageUri).into(profileImageView)
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during the upload process
                // You can log the error or display a toast to the user
                Toast.makeText(requireContext(), "Error uploading image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }





    }

    fun addSTicker(imgName: String, profileImageView: ImageView) {
        val resourceId = resources.getIdentifier(imgName, "drawable", context?.packageName)
        newUri = Uri.parse("android.resource://${context?.packageName}/$resourceId")
        Picasso.get().load(newUri).into(profileImageView)
    }

    private fun showToast(iconResId: Int, message: String, textColor: Int) {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val toastView = inflater.inflate(R.layout.custom_toast, null)

        val iconImageView = toastView.findViewById<ImageView>(R.id.toast_icon)
        val messageTextView = toastView.findViewById<TextView>(R.id.toast_message)

        iconImageView.setImageResource(iconResId)
        messageTextView.text = message
        messageTextView.setTextColor(ContextCompat.getColor(requireContext(), textColor))

        val toast = Toast(requireContext())
        toast.view = toastView
        toast.setGravity(Gravity.BOTTOM, 0, 400)
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }
}