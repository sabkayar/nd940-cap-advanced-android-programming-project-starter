package com.example.android.politicalpreparedness.representative

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.CHANNEL
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Official
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
                    val repsList = mutableListOf<Representative>()
                    for (office in repsResponse.offices) {
                        repsList.addAll(office.getRepresentatives(repsResponse.officials))
                    }
                    Timber.d("Representatives: $repsList")
                }
            } catch (e: Exception) {
                Timber.e(e.localizedMessage)
            }
        }
    }

    private val _loadUrlIntent = MutableLiveData<Intent>()
    val loadUrlIntent: LiveData<Intent>
        get() = _loadUrlIntent

    fun loadUrlIntent(mode: CHANNEL, official: Official) {
        var url: String? = null
        when (mode) {
            CHANNEL.WEB -> {
                if (!official.urls.isNullOrEmpty())
                    url = official.urls[0]
            }
            CHANNEL.FACEBOOK -> {
                var id: String? = null
                run loop@{
                    official.channels?.forEach { channel ->
                        if (channel.type.equals(CHANNEL.FACEBOOK.name, true)) {
                            id = channel.id
                            return@loop
                        }
                    }
                }
                id?.let {
                  url= CHANNEL.FACEBOOK.baseUrl+id
                }
            }
            CHANNEL.TWITTER -> {
                var id: String? = null
                run loop@{
                    official.channels?.forEach { channel ->
                        if (channel.type.equals(CHANNEL.TWITTER.name, true)) {
                            id = channel.id
                            return@loop
                        }
                    }
                }
                id?.let {
                    url= CHANNEL.TWITTER.baseUrl+id
                }
            }
        }

        if (!url.isNullOrEmpty()) {
                         _loadUrlIntent.value = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                     } else {
                         _loadUrlIntent.value = null
                     }
    }


}