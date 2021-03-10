package com.example.android.politicalpreparedness

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.network.jsonadapter.DateAdapter
import java.util.*

@BindingAdapter("android:date")
fun TextView.setDate(date: Date?) {
    text = DateAdapter().dateToJson(date)
}

@BindingAdapter("android:setVisibility")
fun View.setVisibility(value: String?) {
    if (value.isNullOrEmpty() || value.isNullOrBlank()) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
    }
}