package com.aguadeoro.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aguadeoro.api.getOrderComponent
import com.aguadeoro.api.getStockForOrderComponent
import com.aguadeoro.models.OrderComponent
import kotlinx.coroutines.launch

class OrderComponentViewModelFactory(private val orderCompID: String) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        OrderComponentViewModel(orderCompID) as T
}

class OrderComponentViewModel(orderCompID: String) : ViewModel() {
    val orderComponent = mutableStateOf<OrderComponent?>(null)



    init {
        viewModelScope.launch {
            orderComponent.value = getOrderComponent(orderCompID)
            getStockForOrderComponent(orderCompID)
        }
    }
}