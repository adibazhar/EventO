package com.example.evento.data.viewmodel

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.evento.data.model.*
import com.example.evento.data.repo.FirebaseRepo
import com.example.evento.data.repo.UserRepo
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.koin.ext.scope
import java.lang.Exception

class FirebaseViewModel(
    private val firebaseRepo: FirebaseRepo,
    private val firebaseAuthResponse: FirebaseAuthResponse
) : ViewModel() {

    fun signUp(email: String, password: String) = firebaseRepo.signupUserWithEmailandPassword(email,password)

    fun signIn(email: String, password: String) = firebaseRepo.signinWithEmailandPassword(email, password)

    fun signOut() {
        firebaseRepo.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = firebaseRepo.currentUser()

    fun uploadUserInfo(userInfo: UserInfo) {
        firebaseRepo.uploadUserInfoToFirebase(userInfo)
    }

    suspend fun getUserInfo() = firebaseRepo.readUserInfoFromFirebase()

//    fun uploadUserEvents(events: Events) {
//        firebaseRepo.uploadUserEventToFirebase(events)
//    }
//
//    fun uploadEvents(events: Events){
//        firebaseRepo.uploadEventToFirebase(events)
//    }


    suspend fun uploadEvents(events: Events): Boolean {

        var isSuccess = false

        if (events.imageUrl.contains("https://firebasestorage.googleapis")){
            val uploadTask = firebaseRepo.uploadEventsToFirebase(events)
            Log.d("uploadEvents","${uploadTask}")
            when (uploadTask) {
                is FirebaseResult.Failure -> {
                }
                is FirebaseResult.Success<*> -> {
                    isSuccess = true
                }
            }
            return isSuccess
        }
            val result = firebaseRepo.uploadImageToFirebase(events.imageUrl.toUri())
            when (result) {
                is FirebaseResult.Failure -> {
                    Log.d("uploadEvents", "${result.t}")
                }
                is FirebaseResult.Success<*> -> {
                    val task = result.r as Uri
                    events.imageUrl = task.toString()
                    Log.d("uploadEvents","${events.imageUrl}")
                    val uploadTask = firebaseRepo.uploadEventsToFirebase(events)
                    Log.d("uploadEvents","${uploadTask}")
                    when (uploadTask) {
                        is FirebaseResult.Failure -> {
                        }
                        is FirebaseResult.Success<*> -> {
                            isSuccess = true
                        }
                    }
                }
            }

        return isSuccess
        //firebaseRepo.uploadEventsToFirebase(events)
    }

}