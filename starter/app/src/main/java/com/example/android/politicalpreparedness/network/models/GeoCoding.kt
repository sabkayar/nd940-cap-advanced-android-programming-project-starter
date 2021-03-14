package com.example.android.politicalpreparedness.network.models

data class GeoCoding(
        val plus_code: PlusCode,
        val results: List<Result>,
        val status: String?=null
)

data class PlusCode(
    val compound_code: String?=null,
    val global_code: String?=null
)

data class Result(
        val address_components: List<AddressComponent>,
        val formatted_address: String?=null
)

data class AddressComponent(
    val long_name: String?=null,
    val short_name: String?=null,
    val types: List<String>?=null
)

