package com.example.evento.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.evento.data.model.Events
import com.example.evento.data.model.FirebaseResult
import com.example.evento.data.repo.EventRepo
import com.example.evento.data.repo.FirebaseRepo
import kotlinx.coroutines.InternalCoroutinesApi

class EventViewModel(
    application: Application,
    private val repo: EventRepo,
    private val firebaseRepo: FirebaseRepo
) :
    AndroidViewModel(application) {

    //    private var repo: EventRepo =
//        EventRepo(application)

    suspend fun insertEvent(events: Events) {
        repo.insertEvent(events)
    }

    suspend fun updateEvent(events: Events) {
        repo.updateEvent(events)
    }

    suspend fun deleteEvent(events: Events) {
        repo.deleteEvent(events)
    }

    fun getAllEvents(): LiveData<List<Events>> {
        return repo.getAllEvents()
    }

    fun getFavouritesEvent(): LiveData<List<Events>> {
        return repo.getFavouritesEvents()
    }

    fun fetchEvents() = liveData {
        //  val liveData = MutableLiveData<List<Events>>()
        val result = firebaseRepo.fetchEventsFromFirebase()
        when (result) {
            is FirebaseResult.Failure -> {
            }
            is FirebaseResult.Success<*> -> {
                emit(result.r as List<Events>)
            }
        }
    }

    fun fetchEventsFromUser() = liveData {
        when (val data = firebaseRepo.fetchEventsIdFromUser()) {
            is FirebaseResult.Failure -> {
            }
            is FirebaseResult.Success<*> -> {
                val listEventsId = data.r as MutableList<String>
                val result = firebaseRepo.fetchEventsFromFirebase(listEventsId)
                when (result) {
                    is FirebaseResult.Failure -> {
                    }
                    is FirebaseResult.Success<*> -> {
                        val result2 = result.r as List<Events>
                        println("RESULT 2 = $result2")
                        emit(result2)
                    }
                }
            }
        }

    }
}