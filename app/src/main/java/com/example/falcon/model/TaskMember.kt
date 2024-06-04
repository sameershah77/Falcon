package com.example.falcon.model

import android.os.Parcel
import android.os.Parcelable

class TaskMember() : Parcelable {
    var taskArr: ArrayList<Task> = ArrayList()
    var tagArr: ArrayList<String> = ArrayList()
    var technologyArr: ArrayList<String> = ArrayList()
    var currentMember: MemberData? = null

    constructor(
        currentMember: MemberData?,
        taskArr: ArrayList<Task>,
        tagArr: ArrayList<String>,
        technologyArr: ArrayList<String>
    ) : this() {
        this.currentMember = currentMember
        this.technologyArr = technologyArr
        this.tagArr = tagArr
        this.taskArr = taskArr
    }

    // Implementing Parcelable methods
    constructor(parcel: Parcel) : this() {
        currentMember = parcel.readParcelable(MemberData::class.java.classLoader)
        parcel.readTypedList(taskArr, Task.CREATOR)
        parcel.readStringList(tagArr)
        parcel.readStringList(technologyArr)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(currentMember, flags)
        parcel.writeTypedList(taskArr)
        parcel.writeStringList(tagArr)
        parcel.writeStringList(technologyArr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaskMember> {
        override fun createFromParcel(parcel: Parcel): TaskMember {
            return TaskMember(parcel)
        }

        override fun newArray(size: Int): Array<TaskMember?> {
            return arrayOfNulls(size)
        }
    }
}
