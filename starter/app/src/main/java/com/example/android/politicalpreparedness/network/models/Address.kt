package com.example.android.politicalpreparedness.network.models

data class Address(
        var line1: String="",
        var line2: String? = null,
        var city: String="",
        var state: String="Alabama",
        var zip: String=""
) {
    fun toFormattedString(): String {
        var output = line1.plus("\n")
        if (!line2.isNullOrEmpty()) output = output.plus(line2).plus("\n")
        output = output.plus("$city, $state $zip")
        return output
    }

    fun getAddressForApi(): String {
        var output = line1.plus(",")
        if (!line2.isNullOrEmpty()) output = output.plus(line2).plus(",")
        output = output.plus("$city,$state,$zip")
        return output
    }
}