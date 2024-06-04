package com.example.falcon.recyclerview

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.falcon.R
import com.example.falcon.model.Task

class AssignTaskRecyclerViewAdapter(context: Context, arr: ArrayList<Task>):RecyclerView.Adapter<AssignTaskRecyclerViewAdapter.ViewHolder>() {

    val arr: ArrayList<Task> = arr
    val context:Context = context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val task = itemView.findViewById<TextView>(R.id.rv_design_task)
        val priority = itemView.findViewById<TextView>(R.id.rv_design_priority)
        val deadline = itemView.findViewById<TextView>(R.id.rv_design_deadline)
        val plate  = itemView.findViewById<LinearLayout>(R.id.assign_task_plate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.assign_task_recyclerview_design,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.task.setText(arr.get(position).task)
        holder.deadline.setText(arr.get(position).deadline)
        holder.priority.setText(arr.get(position).priority)
        if(arr.get(position).priority == "Low") {
            holder.priority.setTextColor(Color.GREEN)
        }
        else if(arr.get(position).priority.toString() == "Medium") {
            holder.priority.setTextColor(Color.YELLOW)
        }
        else {
            holder.priority.setTextColor(Color.RED)
        }

        holder.plate.setOnLongClickListener {
            holder.plate.setBackgroundResource(R.drawable.new_edit_text_design)
            Handler().postDelayed({
                holder.plate.setBackgroundResource(R.drawable.assign_task_design)
                arr.removeAt(position)
                notifyDataSetChanged()
            },3000)
            true
        }
    }

    override fun getItemCount(): Int {
        return arr.size
    }
}