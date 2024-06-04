package com.example.falcon.model

import android.os.Parcel
import android.os.Parcelable

class ProjectDetails() : Parcelable {
    var projectName = ""
    var projectDesc = ""
    var projectDeadline = ""
    var projectStart = ""

    constructor(projectName: String, projectDesc: String, projectDeadline: String, projectStart: String) : this() {
        this.projectName = projectName
        this.projectDesc = projectDesc
        this.projectDeadline = projectDeadline
        this.projectStart = projectStart
    }

    constructor(parcel: Parcel) : this() {
        projectName = parcel.readString() ?: ""
        projectDesc = parcel.readString() ?: ""
        projectDeadline = parcel.readString() ?: ""
        projectStart = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(projectName)
        parcel.writeString(projectDesc)
        parcel.writeString(projectDeadline)
        parcel.writeString(projectStart)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProjectDetails> {
        override fun createFromParcel(parcel: Parcel): ProjectDetails {
            return ProjectDetails(parcel)
        }

        override fun newArray(size: Int): Array<ProjectDetails?> {
            return arrayOfNulls(size)
        }
    }
}
