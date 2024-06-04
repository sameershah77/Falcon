package com.example.falcon.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.falcon.R
import com.example.falcon.model.SingleChat

class MessageRecyclerViewAdapter(private val context: Context, private val messageList: List<SingleChat>,private val myEmail:String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // View type constants
    private val VIEW_TYPE_SENDER = 1
    private val VIEW_TYPE_RECEIVER = 2

    override fun getItemViewType(position: Int): Int {
        val email = messageList[position].email
        return if (email == myEmail) {
            VIEW_TYPE_SENDER
        } else {
            VIEW_TYPE_RECEIVER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENDER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_box_you, parent, false)
                SenderMessageViewHolder(view)
            }
            VIEW_TYPE_RECEIVER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_box_other, parent, false)
                ReceiverMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_SENDER -> {
                val senderHolder = holder as SenderMessageViewHolder
                senderHolder.bind(message)
            }
            VIEW_TYPE_RECEIVER -> {
                val receiverHolder = holder as ReceiverMessageViewHolder
                receiverHolder.bind(message)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    // ViewHolder for sender message
    class SenderMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderMessageTextView: TextView = itemView.findViewById(R.id.sender_message)
        private val senderNameTextView: TextView = itemView.findViewById(R.id.sender_name)
        private val senderTimeTextView: TextView = itemView.findViewById(R.id.sender_time)

        fun bind(message: SingleChat) {
            // Bind data to sender message layout views
            senderMessageTextView.text = message.message
//            senderNameTextView.text = message.name  simply adding (you)
            senderTimeTextView.text = message.time
        }
    }

    // ViewHolder for receiver message
    class ReceiverMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val receiverMessageTextView: TextView = itemView.findViewById(R.id.receiver_message)
        private val receiverNameTextView: TextView = itemView.findViewById(R.id.receiver_name)
        private val receiverTimeTextView: TextView = itemView.findViewById(R.id.receiver_time)

        fun bind(message: SingleChat) {
            // Bind data to receiver message layout views
            receiverMessageTextView.text = message.message
            receiverNameTextView.text = message.name
            receiverTimeTextView.text = message.time
        }
    }

}
