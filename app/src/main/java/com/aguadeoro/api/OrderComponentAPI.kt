package com.aguadeoro.api

import android.util.Log
import com.aguadeoro.models.OrderComponent
import com.aguadeoro.models.StockHistory
import com.aguadeoro.utils.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getOrderComponent(id: String): OrderComponent {
    return withContext(Dispatchers.IO) {
        val query = Query("select * FROM OrderComponent WHERE ID = $id")
        query.execute()
        val map = query.res[0]
        return@withContext OrderComponent(
            ID = id,
            orderNumber = map.getOrDefault("OrderNumber", ""),
            articleType = map.getOrDefault("ArticleType", ""),
            articlePrefix = map.getOrDefault("ArticlePrefix", ""),
            articleNumber = map.getOrDefault("ArticleNumber", ""),
            material = map.getOrDefault("Material", ""),
            color = map.getOrDefault("Color", ""),
            length = map.getOrDefault("Length", ""),
            height = map.getOrDefault("Height", ""),
            size = map.getOrDefault("Size", ""),
            surface = map.getOrDefault("Surface", ""),
            stone = map.getOrDefault("Stone", ""),
            engravingText = map.getOrDefault("EngravingText", ""),
            engravingType = map.getOrDefault("EngravingType", ""),
            engravingFont = map.getOrDefault("EngravingFont", ""),
            engravingCost = map.getOrDefault("EngravingCost", ""),
            price = map.getOrDefault("Price", ""),
            remark = map.getOrDefault("Remark", ""),
            stoneDia = map.getOrDefault("StoneDia", ""),
            stoneColor = map.getOrDefault("StoneColor", ""),
            stoneAmount = map.getOrDefault("StoneAmount", ""),
            stoneSetting = map.getOrDefault("StoneSetting", ""),
            stoneCarat = map.getOrDefault("StoneCarat", ""),
            stoneRemark = map.getOrDefault("StoneRemark", ""),
            fingerPrint = map.getOrDefault("Fingerprint", "").toBooleanStrictOrNull()?:false,
            microtext = map.getOrDefault("Microtext", "").toBooleanStrictOrNull()?:false,
            catalogCode = map.getOrDefault("CatalogCode", ""),
            inventoryID = map.getOrDefault("InventoryID", ""),

            )
    }
}


suspend fun getStockForOrderComponent(ID: String){
    return withContext(Dispatchers.IO){
        val query = Query("select * FROM SupplierOrderMain WHERE OrderComponentID = $ID")
        query.execute()
        Log.e("supplier order main for orderComponent $ID", query.res.toString())
        if (query.res.size == 0){
            return@withContext
        }
        val suppOrderID = query.res[0].getOrDefault("ID", "")
        if (suppOrderID.isEmpty()){
            return@withContext
        }

        val queryStock = Query("select * from StockHistory1 where SupplierOrderMainID = ${suppOrderID}")
        queryStock.execute()
        val res = mutableMapOf<String, MutableList<StockHistory>>()
        queryStock.res.forEach { map ->
            res.putIfAbsent(map["OrderNumber"]!!, mutableListOf())
            res[map["OrderNumber"]]?.add(
                StockHistory(
                    map["ID"]!!.toInt(),
                    map["OrderNumber"]!!,
                    map["HistoricDate"]!!,
                    map["ProductID"]?.toIntOrNull() ?: 0,
                    map["Supplier"]!!,
                    map["Detail"]!!,
                    map["Type"]!!.toInt(),
                    map["Quantity"]?.toDoubleOrNull() ?: 0.0,
                    map["Cost"]?.toDoubleOrNull() ?: 0.0,
                    map["Remark"]!!,
                    map["Weight"]?.toDoubleOrNull() ?: 0.0,
                    map["Loss"]!!,
                    map["Process"]!!,
                    map["Flow"]!!,
                    shortCode = ""
//                    map["ProductCode"] ?: "no product",
                )
            )
        }
        Log.e("stock history for component $ID", res.toString())

    }
}
