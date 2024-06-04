package com.example.falcon.recyclerview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.falcon.R
import com.example.falcon.view.PDFActivity

class RoadmapRecyclerViewAdapter(context: Context, pdfArr: MutableList<Pair<String, String>>):RecyclerView.Adapter<RoadmapRecyclerViewAdapter.ViewHolder>() {

    private val context = context
    private val pdfArr = pdfArr

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val pdfName = itemView.findViewById<TextView>(R.id.pdf_name)
        val pdfLayout = itemView.findViewById<ConstraintLayout>(R.id.pdf_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.roadmap_layout_design,parent,false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.pdfName.text = pdfArr[position].first
        holder.pdfLayout.setOnClickListener {
            val intent = Intent(context, PDFActivity::class.java)
            intent.putExtra("pdf_name",pdfArr[position].first)
            intent.putExtra("pdf_url",pdfArr[position].second)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return pdfArr.size
    }
}