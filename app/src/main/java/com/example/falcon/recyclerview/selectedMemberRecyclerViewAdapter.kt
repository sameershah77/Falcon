package com.example.falcon.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.falcon.R
import com.example.falcon.model.TaskMember
import com.squareup.picasso.Picasso

open class selectedMemberRecyclerViewAdapter(context: Context, arr: MutableList<TaskMember>) :RecyclerView.Adapter<selectedMemberRecyclerViewAdapter.ViewHolder>(){

    var arr: MutableList<TaskMember> = arr
    val context: Context = context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.task_member_name)
        val email = itemView.findViewById<TextView>(R.id.task_member_email)
        val image = itemView.findViewById<ImageView>(R.id.task_member_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_member_layout_design,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.setText(arr.get(position).currentMember?.name)
        holder.email.setText(arr.get(position).currentMember?.email)
        Picasso.get().load(arr.get(position).currentMember?.img!!.toUri()).into(holder.image)
    }

    override fun getItemCount(): Int {
        return arr.size
    }
}