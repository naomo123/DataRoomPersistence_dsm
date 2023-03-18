package com.example.dataroompersistence_dsm.context

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dataroompersistence_dsm.Converters
import com.example.dataroompersistence_dsm.Dao.ActivitiesDao
import com.example.dataroompersistence_dsm.model.Activities

@Database(entities= [Activities::class], version=1 , exportSchema = true)
@TypeConverters(Converters::class)
abstract class DBContext: RoomDatabase() {
    abstract fun activitiesDao(): ActivitiesDao


    }
