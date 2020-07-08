package com.example.evento.data.repo

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.evento.data.database.EventDb
import com.example.evento.data.database.EventsDao
import com.example.evento.data.model.Events
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventRepo(application: Application) {

    private lateinit var eventsDao: EventsDao
    private lateinit var allEvents:LiveData<List<Events>>
    init {
        eventsDao = EventDb.getInstance(application).eventsDao()
        allEvents = eventsDao.getAllEvents()
    }

    suspend fun insertEvent(events: Events){
            eventsDao.insertEvents(events)
    }

    suspend fun updateEvent(events: Events){
            eventsDao.updateEvents(events)
        }


    suspend fun deleteEvent(events: Events){
            eventsDao.deleteEvents(events)
    }

     fun getAllEvents():LiveData<List<Events>>{
        return allEvents
    }

     fun getFavouritesEvents():LiveData<List<Events>>{
        return eventsDao.getFavouritesEvent()
    }
}