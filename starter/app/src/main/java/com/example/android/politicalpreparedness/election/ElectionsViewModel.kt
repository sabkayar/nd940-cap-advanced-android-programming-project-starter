package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.CivicsHttpClient
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.sql.CommonDataSource

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(val dataSource: ElectionDao) : ViewModel() {

    //TODO: Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections


    //TODO: Create live data val for saved elections
    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    fun populateElectionsFromNetwork() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                        val result = CivicsApi.retrofitService.getElections()

                    Timber.d(result.toString())


                } catch (e: Exception) {
                    Timber.e("Error: ${e.message}")
                }

            }
        }
    }

    fun populateElectionsFromLocal() {

    }


    //TODO: Create functions to navigate to saved or upcoming election voter info

}

@Suppress("UNCHECKED_CAST")
class ElectionsViewModelFactory(
        private val electionDao: ElectionDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)) {
            return ElectionsViewModel(electionDao) as T
        }
        throw IllegalArgumentException("Unknown class ViewModel")
    }

}