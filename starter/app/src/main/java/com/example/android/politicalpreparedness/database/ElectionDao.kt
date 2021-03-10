package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //TODO: DONE Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg election: Election)

    //TODO: DONE Add select all election query
    @Query("select * from election_table")
    suspend fun getAllElections():List<Election>?

    //TODO: DONE Add select single election query
    @Query("select * from election_table where id=:id")
    suspend fun getElection(id:Int):Election?

    //TODO: DONE Add delete query
    @Query("delete from election_table where id=:id")
    suspend fun deleteElection(id:Int)

    //TODO: DONE Add clear query
    @Query("delete from election_table")
    suspend fun clearElection()
}