package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
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
                    val result = CivicsApi.retrofitService.getRepresentatives(address.getAddressForApi())
                    Timber.d(result.toString())
                }
            } catch (e: Exception) {
                Timber.e(e.localizedMessage)
            }
        }
    }

}