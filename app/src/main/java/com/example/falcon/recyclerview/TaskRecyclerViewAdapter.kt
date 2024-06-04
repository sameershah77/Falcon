package com.example.falcon.recyclerview

import UserViewModel
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.falcon.R
import com.example.falcon.model.Project
import com.example.falcon.model.ProjectDetails
import com.example.falcon.model.TaskMember
import com.example.falcon.model.TaskModel
import com.example.falcon.repositories.UserRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Collections


class TaskRecyclerViewAdapter(
    context: Context,
    myTaskArr: ArrayList<Triple<TaskModel, String, MutableList<String>>>,
    email: String
) : RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder>() {
    val myTaskArr: ArrayList<Triple<TaskModel, String, MutableList<String>>> = myTaskArr
    val context: Context = context
    val email: String = email

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val task_plate = itemView.findViewById<LinearLayout>(R.id.task_plate)
        val task_project = itemView.findViewById<TextView>(R.id.task_project)
        val task_task = itemView.findViewById<TextView>(R.id.task_task)
        val task_status = itemView.findViewById<TextView>(R.id.task_status)
        val task_priority = itemView.findViewById<TextView>(R.id.task_priority)
        val task_start_date = itemView.findViewById<TextView>(R.id.task_start_date)
        val task_end_date = itemView.findViewById<TextView>(R.id.task_end_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_layout, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.task_project.text = myTaskArr[position].first.projectDetails.projectName
        holder.task_task.text = myTaskArr[position].first.myTask.task
        holder.task_start_date.text = myTaskArr[position].first.myTask.startDate
        holder.task_end_date.text = myTaskArr[position].first.myTask.deadline
        if (myTaskArr[position].first.myTask.status == true) {
            holder.task_status.text = "Done"
        } else {
            holder.task_status.text = "Not Done Yet"
            holder.task_status.setTextColor(Color.parseColor("#D7EFCB"))
        }
        holder.task_priority.text = myTaskArr.get(position).first.myTask.priority
        if (myTaskArr.get(position).first.myTask.priority == "Low") {
            holder.task_priority.setTextColor(Color.GREEN)
        } else if (myTaskArr.get(position).first.myTask.priority == "Medium") {
            holder.task_priority.setTextColor(Color.YELLOW)
        } else {
            holder.task_priority.setTextColor(Color.RED)
        }
        if (holder.task_status.text == "Done") {
            holder.task_plate.setBackgroundResource(R.drawable.task_design_new)
            holder.task_status.setTextColor(Color.GREEN)

        } else {
            holder.task_plate.setBackgroundResource(R.drawable.task_design)
            holder.task_status.setTextColor(Color.parseColor("#D7EFCB"))

        }
        holder.task_plate.setOnLongClickListener {
            if (holder.task_status.text != "Done") {
                holder.task_plate.setBackgroundResource(R.drawable.task_design_new)
                holder.task_status.text = "Done"
                holder.task_status.setTextColor(Color.GREEN)
                changeStatus(position, true)
            } else {
                holder.task_plate.setBackgroundResource(R.drawable.task_design)
                holder.task_status.text = "Not Done Yet"
                holder.task_status.setTextColor(Color.parseColor("#D7EFCB"))
                changeStatus(position, false)
            }
            true
        }

    }

    override fun getItemCount(): Int {
        return myTaskArr.size
    }

    fun changeStatus(position: Int, flag: Boolean) {
        for (x in myTaskArr[position].third) {

            val projectDataRef =
                FirebaseDatabase.getInstance().getReference("Users").child(x).child("Project_Data")
                    .child(myTaskArr[position].second).child("Project_Tasks")
            projectDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (projectSnapshot in dataSnapshot.children) {
                        val taskMember = projectSnapshot.getValue(TaskMember::class.java)!!
                        if (taskMember.currentMember?.email == email) {
                            val taskArr = taskMember.taskArr
                            for (i in 0 until taskArr.size) {
                                if (taskArr[i].task == myTaskArr[position].first.myTask.task
                                    && taskArr[i].deadline == myTaskArr[position].first.myTask.deadline
                                    && taskArr[i].startDate == myTaskArr[position].first.myTask.startDate
                                    && taskArr[i].priority == myTaskArr[position].first.myTask.priority
                                ) {
                                    taskArr[i].status = flag
                                    taskMember.taskArr = taskArr
                                    projectSnapshot.ref.setValue(taskMember)
                                    break
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("Firebase Database", "Failed to read value.", databaseError.toException())
                }
            })
        }
    }
}