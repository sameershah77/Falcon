package com.example.falcon.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.falcon.R
import com.example.falcon.model.BadgeModel
import com.example.falcon.model.coinModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CongratsProjectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congrats_project)
        val rootView = findViewById<View>(android.R.id.content)
        rootView.alpha = 0f

        val fadeInDuration = 2000L // Adjust the duration as needed

        rootView.animate()
            .alpha(1f)
            .setDuration(fadeInDuration)
            .setListener(null)
            .start()

        val congrats_logo = findViewById<ImageView>(R.id.congrats_logo)
        // Create ObjectAnimator for rotation along the y-axis
        val rotationAnimator = ObjectAnimator.ofFloat(congrats_logo, "rotationY", 0f, 360f)
        rotationAnimator.duration = 3000
        rotationAnimator.interpolator = AccelerateDecelerateInterpolator()

        // Create ObjectAnimator for scale
        val scaleAnimatorX = ObjectAnimator.ofFloat(congrats_logo, "scaleX", 0f, 0.5f, 1f)
        val scaleAnimatorY = ObjectAnimator.ofFloat(congrats_logo, "scaleY", 0f, 0.5f, 1f)
        val scaleAnimatorSet = AnimatorSet()
        scaleAnimatorSet.playTogether(scaleAnimatorX, scaleAnimatorY)
        scaleAnimatorSet.duration = 2000

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(rotationAnimator, scaleAnimatorSet)
        animatorSet.start()


        val congrats = findViewById<LinearLayout>(R.id.linearLayout18)
        // Create ObjectAnimator for alpha property
        val fadeInAnimator = ObjectAnimator.ofFloat(congrats, "alpha", 0f, 1f)
        fadeInAnimator.duration = 2000 // Set the duration of the fade-in animation in milliseconds
        fadeInAnimator.interpolator = AccelerateDecelerateInterpolator() // Set an interpolator for smooth acceleration and deceleration

        // Create ObjectAnimator for translation along the y-axis
        val translationAnimator = ObjectAnimator.ofFloat(congrats, "translationY", 100f, 0f)
        translationAnimator.duration = 2000 // Set the duration of the translation animation in milliseconds
        translationAnimator.interpolator = AccelerateDecelerateInterpolator() // Set an interpolator for smooth acceleration and deceleration

        // Create AnimatorSet to play both animations together
        val newanimatorSet = AnimatorSet()
        newanimatorSet.playTogether(fadeInAnimator, translationAnimator)
        newanimatorSet.start()
        val firebaseDatabase = FirebaseDatabase.getInstance()

        val falcoin = firebaseDatabase.reference.child("Users")
            .child(Firebase.auth.currentUser!!.uid).child("Falcoins")

        val falcoinList = firebaseDatabase.reference.child("Users")
            .child(Firebase.auth.currentUser!!.uid).child("Falcoins_List")

        //falcoin dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.got_coins_layout)
        val window1 = dialog.window
        window1?.setBackgroundDrawableResource(android.R.color.transparent) // Make the background transparent if needed
        window1?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Adjust dialog size if needed
        window1?.setGravity(Gravity.CENTER) // Center the dialog on the screen
        dialog.setCanceledOnTouchOutside(false)

        //badge dialog
        val dialogBadge = Dialog(this)
        dialogBadge.setContentView(R.layout.got_badge_layout)
        val window = dialogBadge.window
        window?.setBackgroundDrawableResource(android.R.color.transparent) // Make the background transparent if needed
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Adjust dialog size if needed
        window?.setGravity(Gravity.CENTER) // Center the dialog on the screen
        dialogBadge.setCanceledOnTouchOutside(false)

        //adding 50 coins to user
        falcoinList.push().setValue(coinModel(50,true)).addOnSuccessListener {
            dialog.show()
            val got_coins = dialog.findViewById<TextView>(R.id.got_coin)
            val cancel_coin_layout = dialog.findViewById<ImageView>(R.id.cancel_coin_layout)
            val falcoin_image = dialog.findViewById<ImageView>(R.id.badge_id)

            animateFalcoin(falcoin_image)
            got_coins.text = "50"

            cancel_coin_layout.setOnClickListener {

                dialogBadge.show()

                val cancel_badge_layout = dialogBadge.findViewById<ImageView>(R.id.cancel_badge_layout)
                val badge_image = dialogBadge.findViewById<ImageView>(R.id.badge_id)
                val got_badge = dialogBadge.findViewById<TextView>(R.id.got_badge)
                val badge_desc = dialogBadge.findViewById<TextView>(R.id.badge_desc)
                got_badge.text = "Creation Badge"
                badge_desc.text = "creation badge is allocated to those users who creates an project from Falcon"

                badge_image.setImageResource(R.drawable.creation_badge)
                animateBadge(badge_image)
                cancel_badge_layout.setOnClickListener {
                    dialogBadge.dismiss()
                }

                dialog.dismiss()
            }
            dialog.show()
        }

        // adding 50 coins in total
        falcoin.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the value of Falcoins
                var falcoinValue = dataSnapshot.getValue(Int::class.java)
                // Check if falcoinValue is null
                if (falcoinValue == null) {
                    falcoinValue = 0
                }
                // Increment the value by 50
                falcoinValue += 50
                // Set the updated value back to the database
                falcoin.setValue(falcoinValue)
                    .addOnSuccessListener {
                        // Handle success
                        Log.d("TAG", "Falcoins updated successfully.")
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        Log.e("TAG", "Error updating Falcoins: $e")
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("TAG", "Failed to read value.", databaseError.toException())
            }
        })

        val badges = firebaseDatabase.reference.child("Users")
            .child(Firebase.auth.currentUser!!.uid).child("Badges")

        val currDate = getCurrentDate()
        val newBadge = BadgeModel("creation_badge",currDate)
        badges.push().setValue(newBadge)


        val save_project = findViewById<Button>(R.id.save_project)
        save_project.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    private fun animateFalcoin(imageView: ImageView) {

        val rotationAnimator = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 360f)
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE // Set the repeat count to infinite for rotation
        rotationAnimator.duration = 3000 // Set the duration of the rotation in milliseconds
        rotationAnimator.interpolator = AccelerateDecelerateInterpolator() // Set an interpolator for smooth acceleration and deceleration
        rotationAnimator.start()

    }

    private fun animateBadge(imageView: ImageView) {

        val rotationAnimator = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 360f)
        rotationAnimator.duration = 3000 // Set the duration of the rotation in milliseconds
        rotationAnimator.interpolator = AccelerateDecelerateInterpolator() // Set an interpolator for smooth acceleration and deceleration

        // Create ObjectAnimator for scale
        val scaleAnimatorX = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 0.5f, 1f)
        val scaleAnimatorY = ObjectAnimator.ofFloat(imageView, "scaleY", 0f, 0.5f, 1f)
        val scaleAnimatorSet = AnimatorSet()
        scaleAnimatorSet.playTogether(scaleAnimatorX, scaleAnimatorY)
        scaleAnimatorSet.duration = 2000 // Set the duration of the scale animation

        // Create an AnimatorSet to play both animations together
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(rotationAnimator, scaleAnimatorSet)
        // Start the animation
        animatorSet.start()
    }

    private fun getCurrentDate(): String {
        // Get the current date and time
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Format the date as a string
        return dateFormat.format(calendar.time)
    }
}