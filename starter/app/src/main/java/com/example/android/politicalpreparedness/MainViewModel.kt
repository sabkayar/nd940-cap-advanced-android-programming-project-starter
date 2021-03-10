package com.example.android.politicalpreparedness

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class MainViewModel() : ViewModel() {
    protected val showToastMutable = MutableLiveData<String>()
    open val showToast: LiveData<String>
        get() = showToastMutable


}