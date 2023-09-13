package com.aguadeoro.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.*
import com.aguadeoro.R
import com.aguadeoro.adapter.PricesTearsOfJoyAdapter
import com.aguadeoro.utils.Query
import com.aguadeoro.utils.Utils
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PricesTearsOfJoyActivity : Activity() {

    private var data = ArrayList<Map<String, String>>()
    private var pricesData = ArrayList<Map<String, String>>()
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        )
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_prices_tears_of_joy)
        val categorySpinner = findViewById<Spinner>(R.id.spinner)
        val modelSpinner = findViewById<Spinner>(R.id.spinner2)
        val metalsSpinner = findViewById<Spinner>(R.id.spinner5)
        val refreshButton = findViewById<Button>(R.id.refresh)
        val isMine = findViewById<CheckBox>(R.id.mine)
        recyclerView = findViewById(R.id.recyclerView)
        val categories = ArrayList<String>()
        val models = ArrayList<String>()
        val helpButton = findViewById<ImageView>(R.id.help)

        helpButton.setOnClickListener {
            val coloredStonesCodeDialog = ColoredStonesCodeDialog(this)
            coloredStonesCodeDialog.showDialog()
        }

        if (getInventoryData("").isCompleted) {
            data.forEach { product ->
                if (!categories.contains(product["Category"])) {
                    product["Category"]?.let { categories.add(it) }
                }
                product["CatalogCode"]?.let {
                    val regex = "\\.([^\\.]+)\\.".toRegex()
                    val regexValue = regex.find(it)!!.destructured.component1()
                    if (!models.contains(regexValue)) {
                        models.add(regexValue)
                    }
                }

            }
            models.sort()
            models.add(0, "ALL")
            categories.sort()
            var adapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
            categorySpinner.adapter = adapter
            adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, models)
            modelSpinner.adapter = adapter
            ArrayAdapter.createFromResource(
                this,
                R.array.metal_array,
                android.R.layout.simple_spinner_item
            ).also { ad ->
                ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                metalsSpinner.adapter = ad
            }
            refreshButton.setOnClickListener {
                refresh(
                    categorySpinner.selectedItem.toString(),
                    modelSpinner.selectedItem.toString(),
                    metalsSpinner,
                    isMine
                )
            }
        } else {
            Toast.makeText(this, "Error : could not fetch data", Toast.LENGTH_SHORT).show()
        }
        refresh(
            categorySpinner.selectedItem.toString(),
            modelSpinner.selectedItem.toString(),
            metalsSpinner,
            isMine
        )
    }

    private fun refresh(
        categorySpinner: String,
        modelSpinner: String,
        metalsSpinner: Spinner,
        isMine: CheckBox
    ) {
        val sb = StringBuilder()
        sb.append(" and Category = " + Utils.escape(categorySpinner) + " ")
        if (modelSpinner != "ALL") {
            sb.append(" and CatalogCode like '%.$modelSpinner.%' ")
        }
        val where = sb.toString()
        if (getInventoryData(where).isCompleted) {
            val from: String = if (isMine.isChecked) {
                "MinedColoredPrices"
            } else {
                "LVColoredPrices"
            }
            if (getColoredStonesPrices(from).isCompleted) {
                recyclerView?.layoutManager = LinearLayoutManager(this)
                recyclerView?.adapter = PricesTearsOfJoyAdapter(
                    data,
                    metalsSpinner.selectedItem.toString(),
                    pricesData,
                    isMine.isChecked,
                    this
                )
            }
        } else {
            Toast.makeText(this, "Error : could not fetch data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getInventoryData(where: String) = runBlocking {
        launch {
            val query =
                "select * from Inventory where Line = 'TearOfJoy' and Status = 'Catalogue'" +
                        " and CatalogCode not like 'BO-%' $where"
            val q = Query(query)
            q.execute()
            data = q.res
        }
    }

    private fun getColoredStonesPrices(from: String) = runBlocking {
        launch {
            val query = "select * from $from"
            val q = Query(query)
            q.execute()
            pricesData = q.res
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.view_inventory, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_home) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}