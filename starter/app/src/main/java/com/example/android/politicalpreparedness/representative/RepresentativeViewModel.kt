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
import com.example.android.politicalpreparedness.network.GeocodeApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Official
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class RepresentativeViewModel(private val appContext: Application) : AndroidViewModel(appContext) {
    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    fun callRepresentativesApi(address: Address) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val repsList = mutableListOf<Representative>()
                    val repsResponse = CivicsApi.retrofitService.getRepresentatives(address.getAddressForApi())
                    Timber.d(repsResponse.toString())
                    for (office in repsResponse.offices) {
                        repsList.addAll(office.getRepresentatives(repsResponse.officials))
                    }
                    withContext(Dispatchers.Main) {
                        _representatives.value = repsList
                    }
                }
            } catch (e: Exception) {
                Timber.e(e.localizedMessage)
            }
        }
    }


    private val _useMyLocationClicked = MutableLiveData<Boolean?>()
    val useMyLocationClicked: LiveData<Boolean?>
        get() = _useMyLocationClicked

    fun useMyLocationClicked() {
        _useMyLocationClicked.value = true
    }


    private val _geoAddress = MutableLiveData<Address?>()
    val geoAddress: LiveData<Address?>
        get() = _geoAddress

    fun callGeoCodingApi(latLng: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val result = GeocodeApi.retrofitService.getGeoCodes(latLng)
                    Timber.d(result.toString())

                    val address: Address = Address()
                    for (component in result.results[0].address_components) {
                        if (component.types?.get(0) == "administrative_area_level_1") {
                            address.state = component.long_name!!
                        }
                        if (component.types?.get(0) == "locality") {
                            address.city = component.long_name!!
                        }
                        if (component.types?.get(0) == "postal_code") {
                            address.zip = component.long_name!!
                        }
                    }

                    val formattedAddress = result.results[0].formatted_address
                    val arrayOfAddress = formattedAddress?.split(",")
                    when (arrayOfAddress?.size) {
                        2 -> {
                            address.line1 = arrayOfAddress[0]
                        }
                        3 -> {
                            address.line1 = arrayOfAddress[0]
                            address.line2 = arrayOfAddress[1]
                        }
                        4 -> {
                            address.line1 = arrayOfAddress[0]
                            address.line2 = arrayOfAddress[1]
                        }
                    }
                    Timber.d("Formatted Address: ${address}")

                    withContext(Dispatchers.Main) {
                        _geoAddress.value = address
                    }
                    //populateAddressFields()
                    //call Representatives API
                }
            } catch (e: Exception) {
                Timber.e(e.localizedMessage)
            }
        }
    }

    fun useMyLocationClickDone() {
        _useMyLocationClicked.value = false
    }


    private fun getState(position: Int): String {
        return appContext.resources.getStringArray(R.array.states)[position]
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
                    url = CHANNEL.FACEBOOK.baseUrl + id
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
                    url = CHANNEL.TWITTER.baseUrl + id
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