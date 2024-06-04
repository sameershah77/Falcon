package com.example.falcon.model

import android.os.Parcel
import android.os.Parcelable

class User(
    var fName: String = "",
    var lName: String = "",
    var email: String = "",
    var password: String = "",
    var img: String = "",
    var desc: String = "",
    var registerDate: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fName)
        parcel.writeString(lName)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(img)
        parcel.writeString(desc)
        parcel.writeString(registerDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
