package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.MainViewModel
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.IllegalArgumentException

class VoterInfoViewModel(private val dataSource: ElectionDao) : MainViewModel() {

    //TODO: Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    //TODO: Add var and methods to populate voter info
    fun populateVoterInfo(argElectionId: Int, argDivision: Division) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val result = CivicsApi.retrofitService.getVoterInfo("${argDivision.state}, ${argDivision.country}", argElectionId.toLong())
                    withContext(Dispatchers.Main) {
                        Timber.d(result.toString())
                        _voterInfo.value = result
                    }
                }
            } catch (e: Exception) {
                Timber.e("Error: ${e.message}")
                showPromptMutable.value = "Data not found!!"
            }
            setupButtonText(argElectionId)
        }
    }

    //TODO: DONE Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status


    private val _isElectionAvailableLocally = MutableLiveData<Boolean?>()
    val isElectionAvailableLocally: LiveData<Boolean?>
        get() = _isElectionAvailableLocally


    private val _onSaveButtonClicked = MutableLiveData<Boolean?>()
    val onSaveButtonClicked: LiveData<Boolean?>
        get() = _onSaveButtonClicked


    fun onSaveButtonClicked(election: Election) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val item = dataSource.getElection(election.id)
                if (item == null) {
                    dataSource.insertAll(election)
                } else {
                    dataSource.deleteElection(election.id)
                }
            }
            _onSaveButtonClicked.value = true
        }
    }


    private fun setupButtonText(electionID: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val item = dataSource.getElection(electionID)
                withContext(Dispatchers.Main) {
                    _isElectionAvailableLocally.value = (item != null)
                }
            }
        }
    }

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    private val _loadUrlIntent = MutableLiveData<Intent>()
    val loadUrlIntent: LiveData<Intent>
        get() = _loadUrlIntent

    fun loadUrlIntent(url: String?) {
        if (!url.isNullOrEmpty()) {
            _loadUrlIntent.value = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        } else {
            _loadUrlIntent.value = null
        }
    }

}

@Suppress("UNCHECKED_CAST")
class VoterInfoViewModelFactory(val electionDao: ElectionDao) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            return VoterInfoViewModel(electionDao) as T
        }
        throw IllegalArgumentException("Unknown class ViewModel")
    }
}