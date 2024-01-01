package com.example.joymp3.data.local.rSong

//import androidx.lifecycle.LiveData
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.example.joymp3.data.model.SongModel
//
//@Dao
//interface SongDao {
//
//    @Query("SELECT * FROM song")
//    fun getAllSong(): LiveData<List<SongModel>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertSong(model: List<SongModel>)
//
//}