package com.example.falcon.view

import UserViewModel
import android.animation.ValueAnimator
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.falcon.R
import com.example.falcon.databinding.ActivitySignUpImageBinding
import com.example.falcon.model.User
import com.example.falcon.repositories.UserRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale

class SignUpImageActivity : AppCompatActivity() {
    lateinit var email: String
    lateinit var pass: String
    lateinit var fname: String
    lateinit var lname: String

    lateinit var img : String
    lateinit var imgURI: Uri

    companion object{
        val CHANNEL_ID = "my_channel"
        val NOTIFICATION_ID = 1
    }

    val aboutArr: ArrayList<String> = ArrayList()
    private lateinit var binding:ActivitySignUpImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivitySignUpImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newIntent = intent
        fname = newIntent.getStringExtra("fname").toString()
        lname = newIntent.getStringExtra("lname").toString()
        email = newIntent.getStringExtra("email").toString()
        pass = newIntent.getStringExtra("pass").toString()


        //default image in profileImageView
        addSTicker("default_user", binding.signUpImgId)
        val takePicture =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    imgURI = result.data?.data!!
                    binding.signUpImgId.setImageURI(imgURI)
                }
            }

        val pickFromGallery =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    imgURI = result.data?.data!!
                    binding.signUpImgId.setImageURI(imgURI)
                }
            }

        binding.uploadImgId.setOnClickListener {
            val uploadImgDialog = Dialog(this@SignUpImageActivity)
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
            val large_woman =
                uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_large_woman)
            val muslim_boy = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_muslim_boy)
            val muslim_girl =
                uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_muslim_girl)
            val muslim_man = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_muslim_man)
            val muslim_woman =
                uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_muslim_woman)
            val thief = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_thief)
            val scientist = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_scientist)
            val boy2 = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_boy2)
            val boy3 = uploadImgDialog.findViewById<ImageView>(R.id.upload_img_of_boy3)
            
            default.setOnClickListener {
                addSTicker("default_user", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }
            boy.setOnClickListener {
                addSTicker("boy", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }
            girl.setOnClickListener {
                addSTicker("girl", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }

            man.setOnClickListener {
                addSTicker("man", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }
            woman.setOnClickListener {
                addSTicker("woman", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }
            large_man.setOnClickListener {
                addSTicker("large_man", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }
            large_woman.setOnClickListener {
                addSTicker("large_woman", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }
            muslim_boy.setOnClickListener {
                addSTicker("muslim_boy", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }
            muslim_girl.setOnClickListener {
                addSTicker("muslim_girl", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }
            muslim_man.setOnClickListener {
                addSTicker("muslim_man", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }
            muslim_woman.setOnClickListener {
                addSTicker("muslim_woman", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }
            thief.setOnClickListener {
                addSTicker("thief", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }
            scientist.setOnClickListener {
                addSTicker("scientist", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }
            boy2.setOnClickListener {
                addSTicker("user", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }
            boy3.setOnClickListener {
                addSTicker("showman", binding.signUpImgId)
                uploadImgDialog.dismiss()
            }
        }


        val about_list = findViewById<ListView>(R.id.about_list)

        binding.aboutEditTextId.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                about_list.visibility = View.VISIBLE
                changeCardHeight(65, 224)
            }
        }

        aboutArr.add("Thinking...")
        aboutArr.add("Building something awesome, stay tuned!")
        aboutArr.add("Do Not Disturb")
        aboutArr.add("Brainstorming new ideas")
        aboutArr.add("Fueled by coffee and determination")
        aboutArr.add("Taking a break to recharge")
        aboutArr.add("Feeling grateful for this awesome project management app! (Falcon)")
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_activated_1,aboutArr)
        about_list.adapter = adapter
        about_list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            binding.aboutEditTextId.setText(parent.getItemAtPosition(position) as String)
        }
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


        binding.goToDashboard.setOnClickListener {

            var about = binding.aboutEditTextId.text.toString()
            if(about.isEmpty()) {
                showToast(R.drawable.alert,"Please select or choose about", R.color.toast_yello)
            }
            else {
                loaderDialog.show()

                if(binding.signUpImgId.getDrawable() == null) {
                    addSTicker("default_user", binding.signUpImgId)
                }

                Firebase.auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                    if(it.isSuccessful) {
                        //storage
                        val storageReference = com.google.firebase.Firebase.storage.reference.child(
                            "Photos/" + "Image" + System.currentTimeMillis() + "." + getFileType(imgURI)
                        )
                        storageReference.putFile(imgURI).addOnSuccessListener {
                            storageReference.downloadUrl.addOnSuccessListener {
                                img = it.toString()
                                val currentTime = getCurrentDate()
                                val user = User(fname,lname,email,pass,img,about,currentTime)
                                Picasso.get().load(img.toUri()).into(binding.signUpImgId);

                                val firebaseDatabase = FirebaseDatabase.getInstance() // Assuming you're using Firebase
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

                                userViewModel.addUser(user){
                                    showNotification(fname)
                                    startActivity(Intent(this@SignUpImageActivity, MainActivity::class.java))
                                    finish()
                                }
                            }
                        }
                    }
                    else {
                        loaderDialog.dismiss()
                        Toast.makeText(this, "Error ! ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }



    }

    fun addSTicker(imgName: String, profileImageView: ImageView) {
        val resourceId = resources.getIdentifier(imgName, "drawable", packageName)
        imgURI = Uri.parse("android.resource://$packageName/$resourceId")
        profileImageView.setImageURI(imgURI)
    }

    private fun getCurrentDate(): String {
        // Get the current date and time
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        // Format the date as a string
        return dateFormat.format(calendar.time)
    }

    private fun getFileType(data: Uri?): String? {
        val r = contentResolver
        val mimiType = MimeTypeMap.getSingleton()
        return mimiType.getMimeTypeFromExtension(r.getType(data!!))
    }

    private fun showToast(iconResId: Int, message: String, textColor: Int) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val toastView = inflater.inflate(R.layout.custom_toast, null)

        val iconImageView = toastView.findViewById<ImageView>(R.id.toast_icon)
        val messageTextView = toastView.findViewById<TextView>(R.id.toast_message)

        iconImageView.setImageResource(iconResId)
        messageTextView.text = message
        messageTextView.setTextColor(ContextCompat.getColor(this, textColor))

        val toast = Toast(this)
        toast.view = toastView
        toast.setGravity(Gravity.BOTTOM, 0, 400)
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }

    private fun changeCardHeight(initialHeightInDP: Int, finalHeightInDP: Int) {
        val about_card = findViewById<CardView>(R.id.about_card)

        val scale = resources.displayMetrics.density
        val initialHeight = (initialHeightInDP * scale + 0.5f).toInt()
        val finalHeight = (finalHeightInDP * scale + 0.5f).toInt()
        val layoutParams = about_card.layoutParams
        layoutParams.height = finalHeight
        about_card.layoutParams = layoutParams
        val animator = ValueAnimator.ofInt(initialHeight, finalHeight)
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            layoutParams.height = animatedValue
            about_card.layoutParams = layoutParams
        }
        animator.duration = 500
        animator.start()
    }

    fun showNotification(name: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Message Channel"
            val channelDesc = "It is the channel for message notification"
            val notificationChannel = NotificationChannel(CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH)
            //we have to provide channel description externally
            notificationChannel.description = channelDesc
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val bitMapImg = (resources.getDrawable(R.drawable.eagle_2_plan) as BitmapDrawable).bitmap

        //Inbox Style
        val inboxStyle = NotificationCompat.InboxStyle()

            .addLine("You've taken the first step towards Falcon")
            .addLine("Embark on a journey of productivity and achievement.")
            .addLine("")
            .addLine("")
            .addLine("Let's transform your vision into reality.")
            .addLine("Get ready to spread your wings and soar!")
            .addLine("Warm regards, Team Falcon")

            .setBigContentTitle("Welcome aboard to Falcon")
            .setSummaryText("notification")


        val notificationalIntent = Intent(this,MainActivity::class.java)
//        //we can use any request code as we want . 101 is just random number
        val pendingIntent = PendingIntent.getActivity(this,200,notificationalIntent, PendingIntent.FLAG_MUTABLE)

        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("\uD83C\uDF89 Congratulations , ${name} \uD83D\uDE80")
            .setContentText("Welcome aboard to Falcon - Your Ultimate Project Management Companion!")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.eagle_1)
            .setLargeIcon(bitMapImg)
            .setStyle(inboxStyle)
            .setChannelId(CHANNEL_ID)
            .build()

        notificationManager.notify(NOTIFICATION_ID,notification)

    }

}