package com.example.falcon.recyclerview

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.falcon.R
import com.example.falcon.model.coinModel


class HistoryRecyclerViewAdapter(context: Context, arr: MutableList<coinModel>) : RecyclerView.Adapter<HistoryRecyclerViewAdapter.Holder>(){
    var context = context
    var arr = arr

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val coin_transaction = itemView.findViewById<TextView>(R.id.coin_transaction)
        val coin_transaction_number = itemView.findViewById<TextView>(R.id.coin_transaction_number)
        val coin_transaction_status = itemView.findViewById<TextView>(R.id.coin_transaction_status)
        val coin_img = itemView.findViewById<ImageView>(R.id.recycler_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.history_recyclerview_layoutt,parent,false)
        val viewHolder = Holder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        animateFalcoin(holder.coin_img)
        if(arr[position].status == true) {
            holder.coin_transaction.text = "Earned Falcoins"

            holder.coin_transaction_number.text = arr[position].coin.toString()
            holder.coin_transaction_number.setTextColor(Color.GREEN)

            holder.coin_transaction_status.text = "+"
            holder.coin_transaction_status.setTextColor(Color.GREEN)
        }
        else {
            holder.coin_transaction.text = "Spended Falcoins"

            holder.coin_transaction_number.text = arr[position].coin.toString()
            holder.coin_transaction_number.setTextColor(Color.RED)

            holder.coin_transaction_status.text = "-"
            holder.coin_transaction_status.setTextColor(Color.RED)
        }
    }

    private fun animateFalcoin(imageView: ImageView) {

        val rotationAnimator = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 360f)
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE // Set the repeat count to infinite for rotation
        rotationAnimator.duration = 3000 // Set the duration of the rotation in milliseconds
        rotationAnimator.interpolator = AccelerateDecelerateInterpolator() // Set an interpolator for smooth acceleration and deceleration
        rotationAnimator.start()

    }
}