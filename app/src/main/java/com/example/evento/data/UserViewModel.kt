package com.example.evento.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.evento.data.model.UserInfo
import com.example.evento.data.repo.FirebaseRepo
import com.example.evento.data.repo.UserRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application, private val userRepo: UserRepo) :
    AndroidViewModel(application) {

     fun saveUserData(userInfo: UserInfo){
       CoroutineScope(Dispatchers.IO).launch{
           userRepo.saveUserData(userInfo)
       }
     }

    fun getUserData(): LiveData<UserInfo> {
        return userRepo.getUserData()
    }

    suspend fun deleteUserData(): Int = userRepo.deleteUserData()
}