package com.example.falcon.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.falcon.R
import com.example.falcon.model.BadgeModel


class BadgeRecyclerViewAdapter(context: Context,badgeArr:MutableList<BadgeModel>):RecyclerView.Adapter<BadgeRecyclerViewAdapter.ViewHolder>(){
    val context = context
    val badgeArr = badgeArr
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val setBadge = itemView.findViewById<ImageView>(R.id.setBadge)
        val setBadgeName = itemView.findViewById<TextView>(R.id.setBadgeName)
        val setBadgeDate = itemView.findViewById<TextView>(R.id.setBadgeDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.badge_list_layout,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }
//    creation_badge
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setBadgeName.text = badgeArr[position].badge
        holder.setBadgeDate.text = badgeArr[position].date
        if(badgeArr[position].badge == "completion_badge") {
            holder.setBadge.setImageResource(R.drawable.completion_badge)
        }
        if(badgeArr[position].badge == "creation_badge") {
            holder.setBadge.setImageResource(R.drawable.creation_badge)
        }
    }

    override fun getItemCount(): Int {
        return badgeArr.size
    }

}