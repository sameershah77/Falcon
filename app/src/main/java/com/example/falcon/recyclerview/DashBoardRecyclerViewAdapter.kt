package com.example.falcon.recyclerview

import android.content.Context
import android.content.Intent
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

open class DashBoardRecyclerViewAdapter(context: Context, arr: ArrayList<Project>,email:String) : RecyclerView.Adapter<DashBoardRecyclerViewAdapter.ViewHolder>(){

    val arr: ArrayList<Project> = arr
    val context:Context = context
    val email:String = email

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val project_name : TextView = itemView.findViewById(R.id.dashboard_project_name)
        val deadline : TextView = itemView.findViewById(R.id.dashboard_project_date)
        val totalMember : TextView = itemView.findViewById(R.id.dashboard_project_member)
        val project_project_percentage_overall:TextView = itemView.findViewById(R.id.dashboard_percentage_overall)
        val project_project_percentage_your:TextView = itemView.findViewById(R.id.dashboard_percentage_your)
        val progressOverall : ProgressBar = itemView.findViewById(R.id.dashboard_progress_overall)
        val progressYour : ProgressBar = itemView.findViewById(R.id.dashboard_progress_your)
        val project_plate : LinearLayout = itemView.findViewById(R.id.dashboard_project_plate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dashboard_project_design,parent,false)
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
        var total_task_completed_your = 0.0
        var total_task_your = 0.0

        for(x in arr.get(position).taskList) {
            for (y in x.taskArr) {
                total_task++
                if(y.status == true) {
                    total_task_completed++
                }
            }
            if(x.currentMember!!.email == email) {
                for (y in x.taskArr) {
                    total_task_your++
                    if(y.status == true) {
                        total_task_completed_your++
                    }
                }
            }
        }

        //overall
        holder.project_project_percentage_overall.text = (total_task_completed/total_task*100).toInt().toString()

        holder.progressOverall.setProgress((total_task_completed/total_task*100).toInt());
        holder.progressOverall.setMax(100);

        //your
        holder.project_project_percentage_your.text = (total_task_completed_your/total_task_your*100).toInt().toString()

        holder.progressYour.setProgress((total_task_completed_your/total_task_your*100).toInt());
        holder.progressYour.setMax(100);

        holder.project_plate.setOnClickListener {
            val intent = Intent(context,ProjectActivity::class.java)
            intent.putExtra("project", arr[position])
            context.startActivity(intent)
        }


    }
}
