package com.example.falcon.view

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.falcon.databinding.ActivityRoadmapBinding
import com.example.falcon.recyclerview.RoadmapRecyclerViewAdapter
import com.google.firebase.storage.FirebaseStorage

class RoadmapActivity : AppCompatActivity() {
    private lateinit var binding:ActivityRoadmapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoadmapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.roadmapRecyclerview.layoutManager = layoutManager

// Initialize Firebase Storage
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val roadmapsRef = storageRef.child("Roadmaps")

        roadmapsRef.listAll()
            .addOnSuccessListener { result ->
                val pdfARR = mutableListOf<Pair<String, String>>() // Initialize the list here
                for (pdfRef in result.items) {
                    // Get the download URL for each PDF file
                    val pdfName = pdfRef.name
                    pdfRef.downloadUrl.addOnSuccessListener { pdfUrl ->
                        // Add the PDF name and URL to the list
                        pdfARR.add(Pair(pdfName, pdfUrl.toString()))

                        // Check if this is the last PDF file, then update the RecyclerView
                        if (pdfARR.size == result.items.size) {
                            val adapter = RoadmapRecyclerViewAdapter(this, pdfARR)
                            binding.roadmapRecyclerview.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }
                    }.addOnFailureListener { exception ->
                        // Handle any errors while retrieving download URL
                        Log.e(TAG, "Error getting download URL: ${exception.message}")
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors while listing PDF files
                Log.e(TAG, "Error listing PDF files: ${exception.message}")
            }

    }



}