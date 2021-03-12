package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class RepresentativeViewModel(private val appContext: Application) : AndroidViewModel(appContext) {


    private fun getState(position: Int): String {
        return appContext.resources.getStringArray(R.array.states)[position]
    }

    fun onFindMyRepsClicked(address: Address) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val repsResponse = CivicsApi.retrofitService.getRepresentatives(address.getAddressForApi())
                    Timber.d("Response: $repsResponse")
                    val repsList= mutableListOf<Representative>()
                    for(office in repsResponse.offices){
                      repsList.addAll(office.getRepresentatives(repsResponse.officials))
                    }
                    Timber.d("Representatives: $repsList")
                }
            } catch (e: Exception) {
                Timber.e(e.localizedMessage)
            }
        }
    }

}