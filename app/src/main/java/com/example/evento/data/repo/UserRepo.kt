package com.example.evento.data.repo

import android.app.Application
import com.example.evento.data.database.EventDb
import com.example.evento.data.model.UserInfo

class UserRepo(application: Application) {

    private val userDao = EventDb.getInstance(application).userDao()

    suspend fun saveUserData(userInfo: UserInfo):Long{
       return userDao.insertUserData(userInfo)
    }

     fun getUserData() = userDao.getUserInfo()

    suspend fun deleteUserData():Int = userDao.deleteUserInfo()
}