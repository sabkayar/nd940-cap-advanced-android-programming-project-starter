package com.example.android.politicalpreparedness

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun Context.showToast(message: String) {
    Toast.makeText(this.applicationContext, message, Toast.LENGTH_SHORT).show()
}

fun Context.showSnackBar(message: String,containerView: View) {
    Snackbar.make(containerView,message,Snackbar.LENGTH_SHORT).show()
}