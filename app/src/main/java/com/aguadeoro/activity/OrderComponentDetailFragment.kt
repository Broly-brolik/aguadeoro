package com.aguadeoro.activity

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.aguadeoro.ui.theme.ManagementTheme
import com.aguadeoro.viewmodels.OrderComponentViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aguadeoro.viewmodels.OrderComponentViewModelFactory


//import com.example.screens.PaymentListScreen

class OrderComponentDetailFragment : Fragment() {

    lateinit var orderComponent: String


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString("orderComponentID")?.let { orderComponent = it }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Order Component $orderComponent"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)


        return ComposeView(requireContext()).apply {
            setContent {
                ManagementTheme {
                    val viewModel: OrderComponentViewModel =
                        viewModel(factory = OrderComponentViewModelFactory(orderComponent))
                    Text(viewModel.orderComponent.value.toString())

                }
            }
        }
    }
}




