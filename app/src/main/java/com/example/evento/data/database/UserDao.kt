package com.example.evento.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.evento.data.model.UserInfo

@Dao
interface UserDao {

    @Insert
    suspend fun insertUserData(userInfo: UserInfo):Long

    @Query("SELECT * FROM user")
    fun getUserInfo():LiveData<UserInfo>

    @Query("DELETE FROM user")
    suspend fun deleteUserInfo():Int
}