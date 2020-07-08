package com.example.evento.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.evento.data.model.Events

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: Events)

    @Update
    suspend fun updateEvents(events: Events)

    @Delete
    suspend fun deleteEvents(events: Events)

    @Query("SELECT * FROM events")
    fun getAllEvents():LiveData<List<Events>>

    @Query("SELECT * FROM events WHERE favourites = 1")
    fun getFavouritesEvent():LiveData<List<Events>>
}