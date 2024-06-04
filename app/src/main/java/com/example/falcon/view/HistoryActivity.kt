package com.example.falcon.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.falcon.R
import com.example.falcon.databinding.ActivityHistoryBinding
import com.example.falcon.databinding.FragmentProjectBinding
import com.example.falcon.model.Project
import com.example.falcon.model.ProjectDetails
import com.example.falcon.model.TaskMember
import com.example.falcon.model.TaskModel
import com.example.falcon.model.coinModel
import com.example.falcon.recyclerview.HistoryRecyclerViewAdapter
import com.example.falcon.recyclerview.ProjectRecyclerViewAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.layoutManager = layoutManager

        val firebaseDatabase = FirebaseDatabase.getInstance()
        val falcoinList = firebaseDatabase.reference.child("Users")
            .child(Firebase.auth.currentUser!!.uid).child("Falcoins_List")

        falcoinList.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val coinModelList: MutableList<coinModel> = mutableListOf()

                for (coinSnapshot in dataSnapshot.children) {
                    val coinId = coinSnapshot.key // Assuming coinId is the key
                    val coin = coinSnapshot.getValue(coinModel::class.java)
                    if (coin!!.coin != null && coin!!.status != null) {
                        val coinModel = coinModel(coin!!.coin,coin!!.status)
                        coinModelList.add(coinModel)
                    }
                }
                val adapter = HistoryRecyclerViewAdapter(this@HistoryActivity, coinModelList)
                binding.historyRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
                // Now you have the list of CoinModel objects, you can use it as needed
                // For example, you can pass it to an adapter for display in a RecyclerView
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase Database", "Failed to read value.", databaseError.toException())
            }
        })


        val falcoin = firebaseDatabase.reference.child("Users")
            .child(Firebase.auth.currentUser!!.uid).child("Falcoins")

        falcoin.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the value of Falcoins
                var falcoinValue = dataSnapshot.getValue(Int::class.java)
                // Check if falcoinValue is null
                if (falcoinValue == null) {
                    falcoinValue = 0
                }
                binding.showFalconHistory.text = falcoinValue.toString()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("TAG", "Failed to read value.", databaseError.toException())
                binding.showFalconHistory.text = "0"
            }
        })

    }

}