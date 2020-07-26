package com.example.evento.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.evento.data.model.Events
import com.example.evento.data.model.FirebaseResult
import com.example.evento.data.repo.EventRepo
import com.example.evento.data.repo.FirebaseRepo
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class EventViewModel(
    application: Application,
    private val repo: EventRepo,
    private val firebaseRepo: FirebaseRepo
) :
    AndroidViewModel(application) {


  //  val eventsId :LiveData<MutableList<String>> = firebaseRepo.fetchEventsIdFromUser2()
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

     fun fetchEvents() = firebaseRepo.fetchEventsFromFirebase2()


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

     fun fetchEventsFromUser2():LiveData<List<Events>> {
        val eventsId :LiveData<List<String>> = firebaseRepo.fetchEventsIdFromUser2()

        return Transformations.switchMap(eventsId){
            firebaseRepo.fetchEventsFromFirebase2(it)
        }
    }
}