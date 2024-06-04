package com.example.falcon.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.falcon.R
import com.example.falcon.databinding.ActivityProjectBinding
import com.example.falcon.model.Project
import com.example.falcon.recyclerview.ProjectRecyclerViewAdapter
import com.example.falcon.recyclerview.ProjectTaskRecyclerViewAdapter
import com.example.falcon.repositories.ProjectRepository
import com.example.falcon.viewmodel.ProjectViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.database.FirebaseDatabase


class ProjectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProjectBinding
    private val projectArr : ArrayList<Project> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val project = intent.getParcelableExtra<Project>("project")!!
        binding.showProjectName.text = project.projectDetails.projectName
        binding.showProjectDesc.text = project.projectDetails.projectDesc
        binding.showProjectStartDate.text = project.projectDetails.projectStart
        binding.showProjectDeadline.text = project.projectDetails.projectDeadline
        binding.showProjectMembers.text = project.taskList.size.toString()

        val totalMember = project.taskList.size
        var totalTask = 0
        val memberTaskArr = ArrayList<Pair<String,Int>>()
        for(i in 0 until totalMember) {
            val task = project.taskList[i].taskArr.size
            totalTask += task
            val email = project.taskList[i].currentMember!!.email
            val p = Pair(email,task)
            memberTaskArr.add(p)
        }

        binding.showProjectTask.text = totalTask.toString()
        for(x in memberTaskArr) {
            Log.d("karde", "${totalTask}, ${x.first}, ${x.second}")
        }

        val pieChart: PieChart = findViewById(R.id.pieChart)

        val entries = ArrayList<PieEntry>()
        for (x in memberTaskArr) {
            entries.add(PieEntry((x.second * 100 / totalTask).toFloat()))
        }

        val colors = ArrayList<Int>()

        val colorArr = listOf("#D7EFCB", "#596412", "#DC55B7CF", "#DCCF5555", "#FFF455","#FFF455","#FFF455","#FFF455")

        for (i in 0 until memberTaskArr.size) {
            colors.add(Color.parseColor(colorArr[i]))
            addLagend(memberTaskArr[i].first,colorArr[i])
        }

        val dataSet = PieDataSet(entries, "Colors")
        dataSet.colors = colors
        dataSet.valueTextSize = 8f // Set the text size here

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = false
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleRadius(61f)

        pieChart.setEntryLabelColor(Color.TRANSPARENT)

        val legend = pieChart.legend
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.setDrawInside(false)
        pieChart.legend.isEnabled = false
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        var total_task = 0.0
        var total_task_completed = 0.0
        for(x in project.taskList) {
            for (y in x.taskArr) {
                total_task++
                if(y.status == true) {
                    total_task_completed++
                }
            }
        }
        binding.projectPercentageOverral.text = (total_task_completed/total_task*100).toInt().toString()

        binding.projectProgressOverral.setProgress((total_task_completed/total_task*100).toInt());
        binding.projectProgressOverral.setMax(100);

        val layoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)
        binding.showProjectRecyclerview.layoutManager = layoutManager
        val adapter = ProjectTaskRecyclerViewAdapter(this,project.taskList)
        binding.showProjectRecyclerview.adapter = adapter

    }

    fun addLagend(email:String,color:String) {
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.gravity = Gravity.CENTER_VERTICAL

        val params = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        linearLayout.layoutParams = params
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.svg_square)

        // Set tint color
        val tintColor = Color.parseColor(color)
        imageView.setColorFilter(tintColor)

        val textView = TextView(this)

        textView.text = email
        textView.textSize = 12f

        val textViewLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textViewLayoutParams.setMargins(16, 0, 0, 0) // Adjust left margin as needed
        textView.layoutParams = textViewLayoutParams

        textView.setTextColor(Color.parseColor("#D7EFCB"))

        linearLayout.addView(imageView)
        linearLayout.addView(textView)

        imageView.layoutParams?.width = 30
        imageView.layoutParams?.height = 30

        val parentLayout = findViewById<LinearLayout>(R.id.add_legend) // Replace "parent_layout_id" with the ID of the layout where you want to add the LinearLayout
        parentLayout.addView(linearLayout)
    }
}