package com.aguadeoro.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aguadeoro.R
import com.aguadeoro.activity.ViewInventoryActivity
import com.aguadeoro.utils.Utils
import com.squareup.picasso.Picasso

class PricesTearsOfJoyAdapter(
    private val data: ArrayList<Map<String, String>>,
    private val metal: String,
    private val pricesData: ArrayList<Map<String, String>>,
    private val isMine: Boolean,
    private val context: Context
) :
    RecyclerView.Adapter<PricesTearsOfJoyAdapter.ViewHolder>() {
    private var prStone = 1.0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        p1: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.line_prices_tear_of_joy, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var checked = holder.switch.isChecked
        var earRingsMultiplier = 1
        val metalMultiplier = when (metal) {
            "White" -> 1.0
            "Yellow" -> 0.95
            "Red" -> 1.1
            "Platinum" -> 1.35
            else -> {
                1.0
            }
        }
        var metalMultiplier2 = 1.0
        if (data[position]["Color"]?.contains("White") == true) {
            metalMultiplier2 = 1.0
        } else if (data[position]["Color"]?.contains("Yellow") == true) {
            metalMultiplier2 = 0.95
        } else if (data[position]["Color"]?.contains("Red") == true) {
            metalMultiplier2 = 1.1
        } else if (data[position]["Color"]?.contains("Platinum") == true) {
            metalMultiplier2 = 1.35
        }
        var a = 0.0
        var b = 0.0
        var c = 0.0
        var d = 0.0
        var e = 0.0
        var f = 0.0


        var carat = data[position]["CatalogCode"]!!
        var index = carat.indexOf(".")
        index = carat.indexOf(".", index + 1)
        carat = StringBuilder(carat.substring(index + 1, index + 4)).insert(1, ".").toString()
        if (data[position]["StoneSize"]?.contains(carat) == false && data[position]["StoneSize"]?.isNotEmpty() == true) {
            var stoneSize = data[position]["StoneSize"]!!
            stoneSize = stoneSize.filter { !it.isWhitespace() }
            carat = stoneSize.substringAfter(":")
            if (carat.contains(";") || carat.contains("-")) {
                val pattern = Regex(""".+?(?=[;-])""")
                carat = pattern.find(carat)!!.value
            }
        }
        if (data[position]["Category"] == "Boucles d'Oreilles") {
            earRingsMultiplier = 2
        }
        if (!isMine) {
            carat = String.format("%.2f", carat.toDouble() + 0.04)
        }
        pricesData.forEach { line ->
            if (line["Carat"]!!.toDouble() == carat.toDouble()) {
                when (line["category"]) {
                    "a" -> a = line["price"]!!.toDouble()
                    "b" -> b = line["price"]!!.toDouble()
                    "c" -> c = line["price"]!!.toDouble()
                    "d" -> d = line["price"]!!.toDouble()
                    "e" -> e = line["price"]!!.toDouble()
                    "f" -> f = line["price"]!!.toDouble()
                    "I" -> d = line["price"]!!.toDouble()
                    "II" -> e = line["price"]!!.toDouble()
                    "III" -> f = line["price"]!!.toDouble()
                }
            }
        }
        holder.catCode.text = data[position]["CatalogCode"]
        val filename = data[position][Utils.IMAGE]
        if (filename!!.isNotEmpty()) {
            Picasso.with(holder.image.context)
                .load("http://195.15.223.234/aguadeoro/06_inventory toc opy/$filename")
                .placeholder(R.drawable.logo_small).into(holder.image)
        }
        holder.image.setOnClickListener {
            val tempData = data
            val intent = Intent(context, ViewInventoryActivity::class.java).apply {
                putExtra("ID", tempData[position]["ID"])
            }
            context.startActivity(intent)
        }
        data[position]["PrStone"]?.let {
            if (it.isNotEmpty()) {
                prStone = it.toDouble()
            }
        }
        var ringPrice = 0.0
        data[position]["RingPrize"]?.let {
            if (it.isNotEmpty()) {
                ringPrice = it.toDouble()
            }
        }
        var meleePrice = 0.0
        data[position]["MeleePrize"]?.let {
            if (it.isNotEmpty()) {
                meleePrice = it.toDouble()
            }
        }
        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            checked = isChecked
            notifyDataSetChanged()
        }
        holder.aPrice.text =
            roundPrice(
                (((a * getTruePrStone(
                    "A",
                    position,
                    checked
                ) * earRingsMultiplier) + (ringPrice / metalMultiplier2 * metalMultiplier) + (meleePrice))).toInt()
                    .toString()
            ) + "CHF"
        holder.bPrice.text =
            roundPrice(
                (((b * getTruePrStone(
                    "B",
                    position,
                    checked
                ) * earRingsMultiplier) + (ringPrice / metalMultiplier2 * metalMultiplier) + (meleePrice))).toInt()
                    .toString()
            ) + "CHF"
        holder.cPrice.text =
            roundPrice(
                (((c * getTruePrStone(
                    "C",
                    position,
                    checked
                ) * earRingsMultiplier) + (ringPrice / metalMultiplier2 * metalMultiplier) + (meleePrice))).toInt()
                    .toString()
            ) + "CHF"
        holder.dPrice.text =
            roundPrice(
                (((d * getTruePrStone(
                    "D",
                    position,
                    checked
                ) * earRingsMultiplier) + (ringPrice / metalMultiplier2 * metalMultiplier) + (meleePrice))).toInt()
                    .toString()
            ) + "CHF"
        holder.ePrice.text =
            roundPrice(
                (((e * getTruePrStone(
                    "E",
                    position,
                    checked
                ) * earRingsMultiplier) + (ringPrice / metalMultiplier2 * metalMultiplier) + (meleePrice))).toInt()
                    .toString()
            ) + "CHF"
        holder.fPrice.text =
            roundPrice(
                (((f * getTruePrStone(
                    "F",
                    position,
                    checked
                ) * earRingsMultiplier) + (ringPrice / metalMultiplier2 * metalMultiplier) + (meleePrice))).toInt()
                    .toString()
            ) + "CHF"

    }

    private fun getTruePrStone(letter: String, position: Int, checked: Boolean): Double {
        return if (checked && data[position]["StoneColor"]?.contains(letter) == true) {
            prStone
        } else {
            1.0
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val catCode: TextView = itemView.findViewById(R.id.catalog_code)
        val image: ImageButton = itemView.findViewById(R.id.imageView)
        val switch: Switch = itemView.findViewById(R.id.switch1)
        val aPrice: TextView = itemView.findViewById(R.id.a)
        val bPrice: TextView = itemView.findViewById(R.id.b)
        val cPrice: TextView = itemView.findViewById(R.id.c)
        val dPrice: TextView = itemView.findViewById(R.id.d)
        val ePrice: TextView = itemView.findViewById(R.id.e)
        val fPrice: TextView = itemView.findViewById(R.id.f)

        init {
            switch.isChecked = false
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun roundPrice(price: String): String {
        var usablePrice = price.toDouble().toInt().toString()
        if (usablePrice.length > 2) {
            val lastTwoDigits = usablePrice.toDouble().toInt().toString()
                .substring(usablePrice.length - 2, usablePrice.length).toInt()
            val substring = usablePrice.toDouble().toInt().toString()
                .substring(0, usablePrice.length - 2)
            when (lastTwoDigits) {
                in 1..30 -> {
                    usablePrice = substring + "30"
                }
                in 31..50 -> {
                    usablePrice = substring + "50"
                }
                in 51..70 -> {
                    usablePrice = substring + "70"
                }
                in 71..90 -> {
                    usablePrice = substring + "90"
                }
            }
        }
        return usablePrice
    }
}