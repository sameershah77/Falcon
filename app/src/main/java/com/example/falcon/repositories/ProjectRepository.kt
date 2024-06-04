package com.example.falcon.repositories

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.falcon.model.MemberData
import com.example.falcon.model.Project
import com.example.falcon.model.ProjectDetails
import com.example.falcon.model.TaskMember
import com.example.falcon.model.TaskModel
import com.example.falcon.recyclerview.ProjectRecyclerViewAdapter
import com.example.falcon.recyclerview.TaskRecyclerViewAdapter
import com.example.falcon.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class ProjectRepository(projectDataBase: FirebaseDatabase) {
    val projectRef = projectDataBase.getReference("Users")
    val newChildKey = projectRef.push().key!!
    var count:Int

    val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
    val projectDataRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserUid).child("Project_Data")
    init {
        count = 0;
    }
    fun addProject(uid:String,taskMemberArr: MutableList<TaskMember>,projectDetails:ProjectDetails,numOfMembers:Int,idArr: MutableList<String>, callback: (Boolean) -> Unit) {
        val memberProjectDataRef = projectRef.child(uid).child("Project_Data").child(newChildKey)
        memberProjectDataRef.child("Project_Member_UID").setValue(idArr)
        memberProjectDataRef.child("Project_Details").setValue(projectDetails)
        memberProjectDataRef.child("Project_Tasks").setValue(taskMemberArr)
            .addOnSuccessListener {
                ++count
                if(count == numOfMembers) {
                    callback(true)
                }
            }
    }

    fun fetchAllProjects(callback: (List<Project>) -> Unit) {
        projectDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val projectData = mutableListOf<Project>()
                for (projectSnapshot in dataSnapshot.children) {
                    val projectDetails = projectSnapshot.child("Project_Details").getValue(ProjectDetails::class.java)!!
                    val projectTasksSnapshot = projectSnapshot.child("Project_Tasks")
                    val taskList = ArrayList<TaskMember>()
                    for (taskSnapshot in projectTasksSnapshot.children) {
                        val task = taskSnapshot.getValue(TaskMember::class.java)
                        task?.let {
                            taskList.add(it)
                        }
                    }
                    val project = Project(projectDetails,taskList)
                    projectData.add(project)
                }
                callback(projectData)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase Database", "Failed to read value.", databaseError.toException())
            }
        })
    }

    fun fetchMyTasks(email:String,callback: (List<Triple<TaskModel,String,MutableList<String>>>) -> Unit) {
        projectDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val arr:ArrayList<Triple<TaskModel,String,MutableList<String>>> = ArrayList()
                for (projectSnapshot in dataSnapshot.children) {
                    val projectDetails = projectSnapshot.child("Project_Details").getValue(
                        ProjectDetails::class.java)!!
                    val projectTasksSnapshot = projectSnapshot.child("Project_Tasks")
                    val idArr: MutableList<String> = mutableListOf()
                    for (snapshot in projectSnapshot.child("Project_Member_UID").children) {
                        val id = snapshot.getValue(String::class.java)
                        id?.let {
                            idArr.add(it)
                        }
                    }
                    for (taskSnapshot in projectTasksSnapshot.children) {
                        val task = taskSnapshot.getValue(TaskMember::class.java)
                        if(task!!.currentMember!!.email == email) {
                            for(x in task.taskArr) {
                                val makeTask = TaskModel(projectDetails,x)
                                arr.add(Triple(makeTask,projectSnapshot.key!!,idArr))
                            }
                            break
                        }
                    }
                }
                callback(arr)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase Database", "Failed to read value.", databaseError.toException())
            }
        })
    }

}