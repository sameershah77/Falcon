package com.example.falcon.view

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.children
import com.example.falcon.R
import com.example.falcon.databinding.ActivityRewardBinding
import com.example.falcon.model.coinModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class RewardActivity : AppCompatActivity() {
    private lateinit var binding:ActivityRewardBinding
    var myFalcoin = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseDatabase = FirebaseDatabase.getInstance()
        val falcoin = firebaseDatabase.reference.child("Users")
            .child(Firebase.auth.currentUser!!.uid).child("Falcoins")

        falcoin.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the value of Falcoins
                var falcoinValue = dataSnapshot.getValue(Int::class.java)
                // Check if falcoinValue is null
                myFalcoin = falcoinValue!!
                if (falcoinValue == null) {
                    falcoinValue = 0
                }
                binding.showFalconReward.text = falcoinValue.toString()

                buyProduct(binding.buyTshirt)
                buyProduct(binding.buyCup)
                buyProduct(binding.buyNotebook)
                buyProduct(binding.buyLaptopSleeve)
                buyProduct(binding.buyBottle)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("TAG", "Failed to read value.", databaseError.toException())
                binding.showFalconReward.text = "0"
            }
        })

    }

    private fun buyProduct(button:LinearLayout) {
            // Find the TextView by its index
            val textView: TextView = button.getChildAt(0) as TextView
            val amount = textView.text.toString().toInt()

            if(amount > myFalcoin) {
                button.setBackgroundResource(R.drawable.reward_unbuy_button)
            }
            else {
                button.setBackgroundResource(R.drawable.reward_buy_button)
            }

        button.setOnClickListener {
            if(amount < myFalcoin) {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.buy_product_layout)
                val window = dialog.window
                window?.setBackgroundDrawableResource(android.R.color.transparent) // Make the background transparent if needed
                window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Adjust dialog size if needed
                window?.setGravity(Gravity.CENTER) // Center the dialog on the screen
                dialog.show()

                val product_image = dialog.findViewById<ImageView>(R.id.product_image)
                val product_amount = dialog.findViewById<TextView>(R.id.product_amount)
                val product_denied = dialog.findViewById<Button>(R.id.product_denied)
                val product_redeem = dialog.findViewById<Button>(R.id.product_redeem)

                if(amount == 6000) {
                    product_image.setImageResource(R.drawable.reward_tshirt)
                }
                else if(amount == 5000) {
                    product_image.setImageResource(R.drawable.reward_cup)
                }
                else if(amount == 7000) {
                    product_image.setImageResource(R.drawable.reward_notebook)
                }
                else if(amount == 8000) {
                    product_image.setImageResource(R.drawable.reward_laptop_sleeve)
                }
                else {
                    product_image.setImageResource(R.drawable.reward_bottle)
                }

                product_amount.text = amount.toString()

                product_denied.setOnClickListener {
                    dialog.dismiss()
                }

                product_redeem.setOnClickListener {
                    val firebaseDatabase = FirebaseDatabase.getInstance()

                    val falcoin = firebaseDatabase.reference.child("Users")
                        .child(Firebase.auth.currentUser!!.uid).child("Falcoins")
                    loadFalcoins(falcoin,amount)


                    val falcoinList = firebaseDatabase.reference.child("Users")
                        .child(Firebase.auth.currentUser!!.uid).child("Falcoins_List")
                    loadFalcoinList(falcoinList,amount)

                    dialog.dismiss()
                }
            }
            else {
                showToast(R.drawable.alert,"You do not have enough coins to buy this product",R.color.toast_yello)
            }
        }
    }

    private fun loadFalcoins(falcoin:DatabaseReference,amount:Int) {
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
                falcoinValue -= amount
                // Set the updated value back to the database
                falcoin.setValue(falcoinValue)
                    .addOnSuccessListener {
                        // Handle success
                        binding.showFalconReward.text = falcoinValue.toString()
                        myFalcoin = falcoinValue
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
    }

    private fun loadFalcoinList(falcoinList:DatabaseReference,amount:Int) {
        //adding 50 coins to user
        falcoinList.push().setValue(coinModel(amount,false)).addOnSuccessListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.order_placed_dialog)
            val window1 = dialog.window
            window1?.setBackgroundDrawableResource(android.R.color.transparent) // Make the background transparent if needed
            window1?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Adjust dialog size if needed
            window1?.setGravity(Gravity.CENTER) // Center the dialog on the screen
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()

            val cancel_order_layout = dialog.findViewById<ImageView>(R.id.cancel_order_layout)
            val order_placed_id = dialog.findViewById<ImageView>(R.id.order_placed_id)

            animateFalcoin(order_placed_id)
            cancel_order_layout.setOnClickListener {
                dialog.dismiss()
            }

        }
    }
    private fun animateFalcoin(imageView: ImageView) {
        val rotationAnimator = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 360f)
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE // Set the repeat count to infinite for rotation
        rotationAnimator.duration = 3000 // Set the duration of the rotation in milliseconds
        rotationAnimator.interpolator = AccelerateDecelerateInterpolator() // Set an interpolator for smooth acceleration and deceleration
        rotationAnimator.start()

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

}