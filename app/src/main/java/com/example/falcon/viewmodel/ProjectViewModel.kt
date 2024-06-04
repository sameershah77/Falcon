package com.example.falcon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.falcon.model.MemberData
import com.example.falcon.model.Project
import com.example.falcon.model.ProjectDetails
import com.example.falcon.model.TaskMember
import com.example.falcon.model.TaskModel
import com.example.falcon.repositories.MemberRepository
import com.example.falcon.repositories.ProjectRepository


class ProjectViewModel(private val repository: ProjectRepository) : ViewModel() {
    private val _projectDataList = MutableLiveData<List<Project>>(emptyList())
    val projectDataList: LiveData<List<Project>> = _projectDataList

    private val _myTaskList = MutableLiveData<List<Triple<TaskModel,String,MutableList<String>>>>(emptyList())
    val myTaskList: LiveData<List<Triple<TaskModel,String,MutableList<String>>>> = _myTaskList

    init {
        fetchProjects()
    }

    fun fetchProjects() {
        repository.fetchAllProjects { project ->
            _projectDataList.value = project
        }
    }

    fun addProject(uid:String, taskMemberArr: MutableList<TaskMember>, projectDetails: ProjectDetails,numOfMembers:Int,idArr: MutableList<String>,callback: (Boolean) -> Unit) {
        repository.addProject(uid,taskMemberArr, projectDetails,numOfMembers,idArr) { success ->
            if (success) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    fun fetchMyTasks(email:String) {
        repository.fetchMyTasks(email) { myTaks->
        _myTaskList.value = myTaks
        }
    }
}
