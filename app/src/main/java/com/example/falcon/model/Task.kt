package com.example.falcon.model

import android.os.Parcel
import android.os.Parcelable

class Task : Parcelable {
    var task: String = ""
    var priority: String = ""
    var deadline: String = ""
    var startDate :String = ""
    var status: Boolean = false

    constructor() {}

    constructor(task: String, priority: String, deadline: String,startDate:String, status: Boolean = false) {
        this.task = task
        this.priority = priority
        this.deadline = deadline
        this.status = status
        this.startDate = startDate
    }

    private constructor(parcel: Parcel) {
        task = parcel.readString() ?: ""
        priority = parcel.readString() ?: ""
        deadline = parcel.readString() ?: ""
        startDate = parcel.readString() ?: ""
        status = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(task)
        parcel.writeString(priority)
        parcel.writeString(deadline)
        parcel.writeString(startDate)
        parcel.writeByte(if (status) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
