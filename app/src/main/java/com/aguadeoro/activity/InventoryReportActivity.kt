package com.aguadeoro.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.aguadeoro.R
import com.aguadeoro.R.*
import com.aguadeoro.models.LocationInventory


class InventoryReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(layout.activity_inventory_report)
        val intent = intent
        val hashMap = intent.getSerializableExtra("location") as HashMap<Integer, LocationInventory>
//        val tv = findViewById<TextView>(R.id.textViewReport)
//        tv.text = hashMap.toString()
        setContent {
            MaterialTheme {
                LazyColumn(){
                    items(hashMap.toList()){
                        Text(it.first.toString() + " - " + it.second.toString())


                    }
                }
            }
        }
    }

//    override fun onCreateView(
//        parent: View?,
//        name: String,
//        context: Context,
//        attrs: AttributeSet
//    ): View? {
//        return ComposeView(applicationContext).apply {
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            setContent {
//                Text("hey")
//            }
//        }
//    }
}
