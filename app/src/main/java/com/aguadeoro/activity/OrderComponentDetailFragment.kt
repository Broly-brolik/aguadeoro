package com.aguadeoro.activity

import android.os.Bundle
import android.view.*
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.aguadeoro.R
import com.aguadeoro.ui.theme.ManagementTheme


//import com.example.screens.PaymentListScreen

class OrderComponentDetailFragment : Fragment() {


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }
        return super.onOptionsItemSelected(item)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)


        return ComposeView(requireContext()).apply {

            setContent {
                ManagementTheme() {
                    Text("heehehe")
                }
            }
        }
    }
}




