package com.example.android.politicalpreparedness.representative.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.android.politicalpreparedness.BR


class Item : BaseObservable() {
    @get:Bindable
    var selectedItemPosition = 0
        set(selectedItemPosition) {
            field = selectedItemPosition
            notifyPropertyChanged(BR.selectedItemPosition)
        }
}