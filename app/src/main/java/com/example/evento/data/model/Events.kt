package com.example.evento.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.ServerValue
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*
import kotlin.collections.HashMap

@Parcelize
@Entity(tableName = "events")
data class Events (

    @PrimaryKey(autoGenerate = false)
    var id:String = "",
    var title:String = "",
    var date:String = "",
    var imageUrl:String = "",
    var description:String = "",
    var favourites:Boolean = false,
    @Ignore
    var timeCreated: @RawValue Any? = null
) : Parcelable {

}