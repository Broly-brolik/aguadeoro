package com.aguadeoro.api

import com.aguadeoro.models.OrderComponent
import com.aguadeoro.utils.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getOrderComponent(id: String): OrderComponent{
    return withContext(Dispatchers.IO){
        val query = Query("select * FROM OrderComponent WHERE ID = $id")
        query.execute()
        val map = query.res[0]
        return@withContext OrderComponent(
            ID = id,
            orderNumber = map.getOrDefault("OrderNumber", ""),
            articleType = map.getOrDefault("ArticleType", "")
        )
    }
}