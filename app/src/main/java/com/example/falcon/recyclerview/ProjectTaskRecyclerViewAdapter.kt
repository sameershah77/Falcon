package com.example.falcon.recyclerview

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.util.TypedValueCompat.dpToPx
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.example.falcon.R
import com.example.falcon.model.TaskMember
import com.example.falcon.view.ProjectActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ProjectTaskRecyclerViewAdapter(context: Context, arr:ArrayList<TaskMember>) :RecyclerView.Adapter<ProjectTaskRecyclerViewAdapter.ViewHolder>(){
    val context = context
    val arr = arr

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.project_member_task_name)
        val email = itemView.findViewById<TextView>(R.id.project_member_task_email)
        val task_count = itemView.findViewById<TextView>(R.id.project_member_task_task_number)
        val tag = itemView.findViewById<ChipGroup>(R.id.project_member_task_tag_chipgroup)
        val tech = itemView.findViewById<ChipGroup>(R.id.project_member_task_technology_chipgroup)
        val linear = itemView.findViewById<LinearLayout>(R.id.project_member_task_linear_layout)
        val progress = itemView.findViewById<ProgressBar>(R.id.project_member_task_progress_your)
        val showPercentage = itemView.findViewById<TextView>(R.id.project_member_task_percentage_your)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.project_member_task_layout,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = arr[position].currentMember!!.name
        holder.email.text = arr[position].currentMember!!.email
        holder.task_count.text = arr[position].taskArr.size.toString()

        val techArr = arr[position].technologyArr
        for(x in techArr) {
            addTechnologyChip(x,holder.tech)
        }
        val tagArr = arr[position].tagArr

        for(x in tagArr) {
            addTagChip(x,holder.tag)
        }

        var total_task = 0.0
        var total_task_completed = 0.0
        for(x in arr[position].taskArr) {
            ++total_task
            if(x.status == true) {
                ++total_task_completed
            }
            addLinearLayout(holder.linear,x.task,x.priority,x.deadline,x.status)
        }

        //your
        Log.d("udasbchj","${total_task}, ${total_task_completed}")
        holder.showPercentage.text = (total_task_completed/total_task*100).toInt().toString()

        holder.progress.setProgress((total_task_completed/total_task*100).toInt());
        holder.progress.setMax(100);

    }

    fun addLinearLayout(linearLayout:LinearLayout,task:String,priority:String,date:String,status:Boolean) {
        // Parent Linear Layout

        linearLayout.orientation = LinearLayout.VERTICAL

        // First Text View
        val textView1 = TextView(context)
        textView1.text = task
        textView1.textSize = 16f
        textView1.setTextColor(Color.WHITE)

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 10, 0, 10) // set margins top and bottom to 20 pixels
        textView1.layoutParams = layoutParams

        // Adding the line separator
        val view = View(context)
        val viewParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            1 // Setting height to 1 pixel
        )

        viewParams.setMargins(0, 10, 0, 10)
        view.layoutParams = viewParams
        view.setBackgroundResource(R.drawable.line_2)

        // Second Linear Layout (nested)
        val nestedLinearLayout = LinearLayout(context)
        nestedLinearLayout.orientation = LinearLayout.HORIZONTAL

        // Three Text Views inside the nested Linear Layout
        val nestedTextView1 = TextView(context)
        nestedTextView1.text = priority
        nestedTextView1.textSize = 12f
        val layoutParams1 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        if(priority == "Low") {
            nestedTextView1.setTextColor(Color.GREEN)
        }
        else if(priority == "Medium") {
            nestedTextView1.setTextColor(Color.YELLOW)
        }
        else {
            nestedTextView1.setTextColor(Color.RED)
        }
        layoutParams1.weight = 1.0f
        nestedTextView1.layoutParams = layoutParams1
        nestedTextView1.gravity = Gravity.START // Center text horizontally within TextView
        nestedLinearLayout.addView(nestedTextView1)

        val nestedTextView2 = TextView(context)
        nestedTextView2.text = date
        nestedTextView2.textSize = 12f
        val layoutParams2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        nestedTextView2.setTextColor(Color.WHITE)
        layoutParams2.weight = 1.0f
        nestedTextView2.layoutParams = layoutParams2
        nestedTextView2.gravity = Gravity.CENTER // Center text horizontally within TextView
        nestedLinearLayout.addView(nestedTextView2)

        val nestedTextView3 = TextView(context)
        if(status == true) {
            nestedTextView3.text = "Completed"
        }
        else{
            nestedTextView3.text = "Pending"
        }
        val layoutParams3 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        nestedTextView3.textSize = 12f
        nestedTextView3.setTextColor(Color.WHITE)
        layoutParams3.weight = 1.0f
        nestedTextView3.layoutParams = layoutParams3
        nestedTextView3.gravity = Gravity.END // Center text horizontally within TextView
        nestedLinearLayout.addView(nestedTextView3)

        // Add the first text view and the nested linear layout to the parent linear layout
        linearLayout.addView(view)
        linearLayout.addView(textView1)
        linearLayout.addView(nestedLinearLayout)
    }



    private fun addTechnologyChip(text: String,technologyChipGroup:ChipGroup) {
        val chip = Chip(context)
        chip.text = text
        chip.setTextColor(context.resources.getColor(R.color.grey7))
        // Change the background color of the chip
        chip.setChipBackgroundColorResource(R.color.grey6)

        technologyChipGroup.addView(chip)
    }

    private fun addTagChip(text: String,tagChipGroup:ChipGroup) {
        val chip = Chip(context)
        chip.text = text

        chip.setTextColor(context.resources.getColor(R.color.grey7))
        // Change the background color of the chip
        chip.setChipBackgroundColorResource(R.color.grey6)

        tagChipGroup.addView(chip)
    }

    override fun getItemCount(): Int {
        return arr.size
    }
}