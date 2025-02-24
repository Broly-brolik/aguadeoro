package com.aguadeoro.models

data class OrderComponent(
    val ID: String = "",
    val orderNumber: String = "",
    val articleType: String = "",
    val articlePrefix: String = "",
    val articleNumber: String = "",
    val material: String = "",
    val color: String = "",
    val length: String = "",
    val height: String = "",
    val size: String = "",
    val surface: String = "",
    val stone: String = "",
    var engravingText: String = "",
    var engravingType: String = "",
    var engravingFont: String = "",
    var engravingCost: String = "",
    var price: String = "",
    var remark: String = "",
    var stoneDia: String = "",
    var stoneColor: String = "",
    var stoneAmount: String = "",
    var stoneSetting: String = "",
    var stoneCarat: String = "",
    var stoneRemark: String = "",
    var fingerPrint: Boolean = false,
    var microtext: Boolean = false,
    var catalogCode: String = "",
    var inventoryID: String = "",

    )
