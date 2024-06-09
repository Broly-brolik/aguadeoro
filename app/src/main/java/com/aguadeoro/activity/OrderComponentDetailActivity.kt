package com.aguadeoro.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.aguadeoro.R

class OrderComponentDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)
        val frag = OrderComponentDetailFragment()
        val orderCompID = intent.extras?.getString("orderComponentID")
        val bundle = Bundle()
        bundle.putString("orderComponentID", orderCompID)
        frag.arguments = bundle
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.frame_layout, frag)
        }


    }
}