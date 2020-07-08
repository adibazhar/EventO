package com.example.evento.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "user")
data class UserInfo(
    @PrimaryKey var uid: String = "",
    var username: String = "",
    var email: String = "",

    @Ignore var eventsid: String? = null
)