package com.proiect.cargram.data.api

import com.google.gson.annotations.SerializedName

data class VinDecoderResponse(
    @SerializedName("decode")
    val decode: List<VinDecodeItem> = emptyList(),
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("error")
    val error: String? = null
)

data class VinDecodeItem(
    @SerializedName("label")
    val label: String = "",
    @SerializedName("value")
    val value: Any? = null,
    @SerializedName("id")
    val id: Int? = null
) {
    fun getValueAsString(): String {
        return when (value) {
            is String -> value
            is Number -> {
                if (value.toDouble() % 1 == 0.0) {
                    value.toLong().toString()
                } else {
                    value.toString()
                }
            }
            is Boolean -> value.toString()
            is List<*> -> value.joinToString(", ")
            else -> value?.toString() ?: ""
        }
    }
} 