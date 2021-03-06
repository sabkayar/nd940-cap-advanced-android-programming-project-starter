package com.example.android.politicalpreparedness

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.android.politicalpreparedness.network.jsonadapter.DateAdapter
import com.example.android.politicalpreparedness.utils.GlideApp
import java.util.*

@BindingAdapter("android:date")
fun TextView.setDate(date: Date?) {
    text = DateAdapter().dateToJson(date)
}

@BindingAdapter("android:setVisibility")
fun View.setVisibility(value: String?) {
    visibility = if (value.isNullOrEmpty() || value.isNullOrBlank()) {
        View.GONE
    } else {
        View.VISIBLE
    }

}

@BindingAdapter("setUrl")
fun ImageView.loadImage(url: String?) {
    try {
        GlideApp.with(this).load(url)
                .centerCrop()
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_profile)
                .into(this)
    } catch (e: Exception) {
        e.printStackTrace()
        setImageResource(R.drawable.ic_profile)
    }

}

