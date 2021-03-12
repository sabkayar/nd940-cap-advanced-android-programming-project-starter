package com.example.android.politicalpreparedness

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun Context.showToast(message: String) {
    Toast.makeText(this.applicationContext, message, Toast.LENGTH_SHORT).show()
}

fun Context.showSnackBar(message: String, containerView: View) {
    Snackbar.make(containerView, message, Snackbar.LENGTH_SHORT).show()
}

fun Context.loadUrl(intent: Intent?) {
    intent?.let {
        startActivity(intent)
        return
    }
    Toast.makeText(this, "Information not available", Toast.LENGTH_SHORT).show()
}

enum class CHANNEL(baseUrl: String) {
    WEB(""), FACEBOOK("https://www.facebook.com/"), TWITTER("https://twitter.com/");

    var baseUrl: String? = null

    init {
        this.baseUrl = baseUrl
    }
}