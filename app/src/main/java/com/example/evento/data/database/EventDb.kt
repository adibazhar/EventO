package com.example.evento.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.evento.data.model.Events
import com.example.evento.data.model.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Events::class,UserInfo::class],version = 1,exportSchema = false)
abstract class EventDb:RoomDatabase() {

    abstract fun eventsDao():EventsDao
    abstract fun userDao():UserDao

    companion object {
    private var instance:EventDb? = null

        @Synchronized
        fun getInstance(context: Context):EventDb{
            if (instance==null){
                instance = Room.databaseBuilder(context.applicationContext,
                    EventDb::class.java,
                    "eventDb")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()
            }

            return instance as EventDb
        }

        private val roomCallback = object : RoomDatabase.Callback(){
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
//                val eventsDao = instance!!.eventsDao()
//                CoroutineScope(Dispatchers.IO).launch {
//                    eventsDao.insertEvents(Events("123","Title 1","Feb",""))
//                    eventsDao.insertEvents(Events("124","Title 2","Feb",""))
//                    eventsDao.insertEvents(Events("125","Title 3","Feb","","This is 3",true))
//
//                }
            }
        }
    }
}