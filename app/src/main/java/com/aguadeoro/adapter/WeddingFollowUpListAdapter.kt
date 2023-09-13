package com.aguadeoro.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.aguadeoro.R

class WeddingFollowUpListAdapter(val data: ArrayList<Map<String, String>>) :
    RecyclerView.Adapter<WeddingFollowUpListAdapter.ViewHolder>() {

    val toMarry = ArrayList<String>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.customerName)
        val email: TextView = itemView.findViewById(R.id.customerEmail)
        val orderDate: TextView = itemView.findViewById(R.id.customerOrderDate)
        val orderName: TextView = itemView.findViewById(R.id.customerOrderName)
        val amount: TextView = itemView.findViewById(R.id.customerTotal)
        val isMarried: CheckBox = itemView.findViewById(R.id.isMarried)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(p0.context)
            .inflate(R.layout.line_wedding_follow_up, p0, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(p0: ViewHolder, i: Int) {
        p0.name.text = data[i]["CustomerName"]
        p0.email.text = data[i]["Email"]
        p0.orderDate.text = data[i]["OrderDate"]
        p0.amount.text = data[i]["Total"]
        p0.orderName.text = data[i]["ArticlePrefix"]
        p0.isMarried.setOnClickListener {
            data[i]["CustomerNumber"]?.let { number ->
                if (toMarry.contains(number)) {
                    toMarry.remove(number)
                } else {
                    toMarry.add(number)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}