package com.example.falcon.recyclerview

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.falcon.R
import com.example.falcon.model.Project
import com.example.falcon.view.ProjectActivity

open class ProjectRecyclerViewAdapter(context: Context, arr: ArrayList<Project>) : RecyclerView.Adapter<ProjectRecyclerViewAdapter.ViewHolder>(){

    val arr: ArrayList<Project> = arr
    val context: Context = context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val project_name : TextView = itemView.findViewById(R.id.project_project_name)
        val deadline : TextView = itemView.findViewById(R.id.project_project_date)
        val progress : ProgressBar = itemView.findViewById(R.id.project_project_progress)
        val totalMember : TextView = itemView.findViewById(R.id.project_project_member)
        val project_project_percentage:TextView = itemView.findViewById(R.id.project_project_percentage)
        val project_plate :LinearLayout = itemView.findViewById(R.id.project_plate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.project_layout_design,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.project_name.setText(arr.get(position).projectDetails.projectName)
        holder.deadline.setText(arr.get(position).projectDetails.projectDeadline)
        holder.totalMember.setText(arr.get(position).taskList.size.toString())

        var total_task = 0.0
        var total_task_completed = 0.0

        for(x in arr.get(position).taskList) {
            for (y in x.taskArr) {
                total_task++
                if(y.status == true) {
                    total_task_completed++
                }
            }
        }

        holder.project_project_percentage.text = (total_task_completed/total_task*100).toInt().toString()
        Log.d("owinje","${total_task}   ${total_task_completed}")
        holder.project_plate.setOnClickListener {
            val intent = Intent(context,ProjectActivity::class.java)
            intent.putExtra("project", arr[position])
            context.startActivity(intent)
        }

        holder.progress.setProgress((total_task_completed/total_task*100).toInt());
        holder.progress.setMax(100);

    }
}
