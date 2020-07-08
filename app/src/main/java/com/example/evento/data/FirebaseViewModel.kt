package com.example.evento.data

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
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.koin.ext.scope

class FirebaseViewModel(
    private val firebaseRepo: FirebaseRepo,
    private val firebaseAuthResponse: FirebaseAuthResponse
) : ViewModel() {

    fun signUp(email: String, password: String): LiveData<FirebaseAuthResponse> {
        // return firebaseRepo.signupUserWithEmailandPassword(email, password)
        val data = MutableLiveData<FirebaseAuthResponse>()
        CoroutineScope(IO).launch {
            val signUpStatus = firebaseRepo.signupUserWithEmailandPassword2(email, password)
            withContext(Main) {
                when (signUpStatus) {
                    is FirebaseResult.Failure -> {
                        firebaseAuthResponse.errorMessages = signUpStatus.t
                        data.value = firebaseAuthResponse
                    }
                    is FirebaseResult.Success<*> -> {
                        firebaseAuthResponse.authResult = signUpStatus.r as AuthResult
                        data.value = firebaseAuthResponse
                        signOut()
                    }
                }
            }

        }
        return data
    }

    fun signIn(email: String, password: String): LiveData<FirebaseAuthResponse> {
        //return firebaseRepo.signinWithEmailandPassword(email, password)
        val result = MutableLiveData<FirebaseAuthResponse>()
        CoroutineScope(IO).launch {
            val signInStatus = firebaseRepo.signinWithEmailandPassword2(email, password)
            withContext(Main) {
                when (signInStatus) {
                    is FirebaseResult.Failure -> {
                        firebaseAuthResponse.errorMessages = signInStatus.t
                        result.value = firebaseAuthResponse
                    }
                    is FirebaseResult.Success<*> -> {
                        val data = signInStatus.r as AuthResult
                        firebaseAuthResponse.authResult = data
                        result.value = firebaseAuthResponse
                    }
                }
            }

        }
        return result
    }

    fun signOut() {
        firebaseRepo.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = firebaseRepo.currentUser()

    fun uploadUserInfo(userInfo: UserInfo) {
        firebaseRepo.uploadUserInfoToFirebase(userInfo)
    }

    fun getUserInfo(): LiveData<FirebaseDbResponse> {
        val dbResponse = FirebaseDbResponse()
        val liveData = MutableLiveData<FirebaseDbResponse>()
        val job = CoroutineScope(IO).launch {
            val result = firebaseRepo.readUserInfoFromFirebase()
            withContext(Main) {
                when (result) {
                    is FirebaseResult.Failure -> {
                        dbResponse.error = result.t
                        liveData.value = dbResponse
                    }
                    is FirebaseResult.Success<*> -> {
                        dbResponse.userInfo = result.r as UserInfo
                        liveData.value = dbResponse
                    }
                }
            }
        }

        return liveData
    }

//    fun uploadUserEvents(events: Events) {
//        firebaseRepo.uploadUserEventToFirebase(events)
//    }
//
//    fun uploadEvents(events: Events){
//        firebaseRepo.uploadEventToFirebase(events)
//    }


    suspend fun uploadEvents(events: Events): Boolean {

        var isSuccess = false
        val result = firebaseRepo.uploadImageToFirebase(events.imageUrl.toUri())
        when (result) {
            is FirebaseResult.Failure -> {
                result.t
            }
            is FirebaseResult.Success<*> -> {
                val task = result.r as Uri
                events.imageUrl = task.toString()
                val uploadTask = firebaseRepo.uploadEventsToFirebase(events)
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