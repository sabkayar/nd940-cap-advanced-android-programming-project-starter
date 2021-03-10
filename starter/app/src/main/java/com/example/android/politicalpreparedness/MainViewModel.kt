package com.example.android.politicalpreparedness

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class MainViewModel() : ViewModel() {
    protected val showPromptMutable = MutableLiveData<String>()
    open val showPrompt: LiveData<String>
        get() = showPromptMutable


}