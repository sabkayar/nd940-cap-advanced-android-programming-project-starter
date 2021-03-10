package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.MainViewModel
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(val dataSource: ElectionDao) : MainViewModel() {

    //TODO: Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections


    //TODO: Create live data val for saved elections
    private val _savedElections = MutableLiveData<List<Election>?>()
    val savedElections: LiveData<List<Election>?>
        get() = _savedElections


    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    fun populateElections() {
        populateElectionsFromLocal()
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val result = CivicsApi.retrofitService.getElections()
                    withContext(Dispatchers.Main) {
                        _upcomingElections.value = result.elections
                        Timber.d(result.toString())
                    }
                }
            } catch (e: Exception) {
                Timber.e("Error: ${e.message}")
                showPromptMutable.value = "No data found!!"
            }
        }
    }

   private fun populateElectionsFromLocal() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val list = dataSource.getAllElections()
                withContext(Dispatchers.Main) {
                    _savedElections.value = list
                }
            }
        }
    }


    //TODO: DONE Create functions to navigate to saved or upcoming election voter info

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo

    fun onElectionItemClicked(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun doneNavigatingToVoterInfo() {
        _navigateToVoterInfo.value = null
    }

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