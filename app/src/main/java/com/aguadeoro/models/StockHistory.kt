package com.aguadeoro.models

import android.os.Parcelable

data class StockHistory(
    val id: Int,
    val orderNumber: String,
    val historicDate: String,
    val productId: Int,
    val supplier: String,
    val detail: String,
    val type: Int,
    val quantity: Double,
    val cost: Double,
    val remark: String,
    val weight: Double,
    val loss: String,
    val process: String,
    val flow: String,
    var shortCode: String,
    var uuid: String = "",
    var category: String = ""
)
