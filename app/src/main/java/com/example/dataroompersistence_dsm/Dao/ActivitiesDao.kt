package com.example.dataroompersistence_dsm.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.dataroompersistence_dsm.model.Activities
import java.util.Date


@Dao
interface ActivitiesDao {

    @Query("SELECT * from Activities ORDER BY DATE ASC")
    suspend fun getAllActivities():MutableList<Activities>
    @Insert
    suspend fun insert(activities: Activities)

    @Query("UPDATE Activities SET title=:Title, description=:Description, location=:Location, date=:Date, hours=:Hours   WHERE title=:Title")
    suspend fun updateActivity(Title: String, Description: String, Location: String, Date: Date, Hours: Float)

    @Query("DELETE FROM Activities WHERE title=:Title")
    suspend fun deleteActivity(Title: String)

    @Query("DELETE FROM Activities")
    suspend fun deleteAllActivities()

}