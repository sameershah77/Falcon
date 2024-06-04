package com.example.falcon.model

import android.os.Parcel
import android.os.Parcelable

class MemberData() : Parcelable {
    var name = ""
    var email = ""
    var img = ""
    var desc = ""
    var registerDate = ""
    var uid = ""

    constructor(
        name: String,
        desc: String,
        email: String,
        registerDate: String,
        img: String,
        uid: String
    ) : this() {
        this.name = name
        this.desc = desc
        this.email = email
        this.registerDate = registerDate
        this.img = img
        this.uid = uid
    }

    // Implementing Parcelable methods
    constructor(parcel: Parcel) : this() {
        name = parcel.readString() ?: ""
        email = parcel.readString() ?: ""
        img = parcel.readString() ?: ""
        desc = parcel.readString() ?: ""
        registerDate = parcel.readString() ?: ""
        uid = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(img)
        parcel.writeString(desc)
        parcel.writeString(registerDate)
        parcel.writeString(uid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MemberData> {
        override fun createFromParcel(parcel: Parcel): MemberData {
            return MemberData(parcel)
        }

        override fun newArray(size: Int): Array<MemberData?> {
            return arrayOfNulls(size)
        }
    }
}
