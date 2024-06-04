package com.example.falcon.model

import android.os.Parcel
import android.os.Parcelable

class Project() : Parcelable {
    var projectDetails: ProjectDetails = ProjectDetails()
    var taskList: ArrayList<TaskMember> = ArrayList()

    constructor(projectDetails: ProjectDetails, taskList: ArrayList<TaskMember>) : this() {
        this.projectDetails = projectDetails
        this.taskList = taskList
    }

    constructor(parcel: Parcel) : this() {
        projectDetails = parcel.readParcelable(ProjectDetails::class.java.classLoader)!!
        parcel.readTypedList(taskList, TaskMember.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(projectDetails, flags)
        parcel.writeTypedList(taskList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Project> {
        override fun createFromParcel(parcel: Parcel): Project {
            return Project(parcel)
        }

        override fun newArray(size: Int): Array<Project?> {
            return arrayOfNulls(size)
        }
    }
}
