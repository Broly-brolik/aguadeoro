package com.aguadeoro.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aguadeoro.R
import com.aguadeoro.adapter.WeddingFollowUpListAdapter
import com.aguadeoro.utils.Query
import com.aguadeoro.utils.Utils
import java.io.File
import java.util.*
import kotlin.streams.toList


class WeddingFollowUpActivity : Activity() {

    private val timeRanges =
        arrayOf(" < 6 months", " < 1 year", " < 2 years", " < 5 years", "since the beginning")
    private var data: String = ""
    private val parent = this
    private lateinit var lw: ListView
    private lateinit var marriedStatus: String
    private lateinit var toMarry: ArrayList<String>
    private lateinit var list: RecyclerView
    private lateinit var weddingFollowUpListAdapter: WeddingFollowUpListAdapter
    private var wedding_data = arrayListOf<Map<String, String>>()
    private var current_data = arrayListOf<Map<String, String>>()
    private lateinit var textViewEntryNumber: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wedding_follow_up)


        val spinnerStore = findViewById<Spinner>(R.id.spinnerStore)
        val stores = mutableListOf("All Stores")
        stores.addAll(Utils.getSetSetting("Stores").toList())
        textViewEntryNumber = findViewById(R.id.textViewWeddingFollowUpQuantity)
        var aa = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            stores
        )
        spinnerStore.adapter = aa
        spinnerStore.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val id = id.toInt()
                if (id == 0) {
                    current_data = wedding_data
                } else {
                    current_data =
                        wedding_data.stream().filter { it["StoreMainOrder"] == stores[id] }
                            .toList() as ArrayList<Map<String, String>>
                }
                weddingFollowUpListAdapter = WeddingFollowUpListAdapter(current_data)


                list.adapter = weddingFollowUpListAdapter
                textViewEntryNumber.text = current_data.size.toString() + " entries"
            }
        }

        list = findViewById<RecyclerView>(R.id.list)



        actionBar?.setDisplayHomeAsUpEnabled(true)
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Choose a time range")
        mBuilder.setSingleChoiceItems(timeRanges, -1, null)
        mBuilder.setPositiveButton("Select") { dialog, _ ->
            lw = (dialog as AlertDialog).listView
            if (lw.checkedItemCount > 0) {
                findViewById<ProgressBar>(R.id.progressBar1).visibility = View.VISIBLE
                FetchStatus().execute()
            }
        }
        mBuilder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        val mDialog = mBuilder.create()
        mDialog.show()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.wedding_follow_up, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        if (id == R.id.action_home) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        if (id == R.id.saveMarry) {
            val currentAdapter =
                this.findViewById<RecyclerView>(R.id.list).adapter as WeddingFollowUpListAdapter
            toMarry = currentAdapter.toMarry
            UpdateStatus().execute(toMarry)
        }

        if (id == R.id.exportMarry) {
            var csv_file = "Name,Email,Order Date,Order name,Amount,Store\n"
            for (data in current_data) {
                csv_file += "${data["CustomerName"]},${data["Email"]},${data["OrderDate"]},${data["ArticlePrefix"]},${data["Total"]},${data["StoreMainOrder"]}\n"
            }
            Log.e("csv", csv_file)


            //            Log.e("autorisation new file", String.valueOf(newF));

//            File pdfFile = new File(Environment.getExternalStorageDirectory()
//                    + "/03_supplierorders/", "Order " + orderInfo.getOrDefault("orderNumber", "") + ".pdf");
            val pdfFile = File(
                Environment.getExternalStorageDirectory().toString() + "/wedding.csv"
            )
            pdfFile.writeText(csv_file)
            val mIntent = Intent(Intent.ACTION_SEND)
            /*To send an email you need to specify mailto: as URI using setData() method
            and data type will be to text/plain using setType() method*/
            mIntent.data = Uri.parse("mailto:")
            mIntent.type = "text/plain"
            // put recipient email in intent
            /* recipient is put as array because you may wanna send email to multiple emails
               so enter comma(,) separated emails, it will be stored in array*/
//            mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("recipient"))
            //put the Subject in the intent
            mIntent.putExtra(Intent.EXTRA_SUBJECT, "Wedding list")
            //put the message in the intent
//            mIntent.putExtra(Intent.EXTRA_TEXT, "wedding follow up")
            mIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdfFile))


            try {
                //start email intent
                startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
            } catch (e: Exception) {
                //if any thing goes wrong for example no email client application or any exception
                //get and show exception message
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }


        }

        return true
    }

    inner class UpdateStatus : AsyncTask<ArrayList<String>, String, Boolean>() {

        override fun doInBackground(vararg p0: ArrayList<String>?): Boolean {
            val where = StringBuilder()
            p0[0]?.forEachIndexed { index, number ->
                where.append(" CustomerNumber = ")
                where.append(number)
                where.append(" or ")
                where.append(" CustomerNumber = ")
                where.append((number.toInt() + 1).toString())
                if (index != p0[0]?.lastIndex) {
                    where.append(" or ")
                }
            }
            val query = "update Customer set PersonalStatus = '$marriedStatus' where $where"
            val q = Query(query)
            return q.execute()
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            findViewById<ProgressBar>(R.id.progressBar1).visibility = View.VISIBLE
            FetchCustomers().execute(lw.checkedItemPosition)
        }
    }

    inner class FetchCustomers : AsyncTask<Int, String, Boolean>() {

        private var data = ArrayList<Map<String, String>>()

        override fun doInBackground(vararg params: Int?): Boolean {
            var filter = ""
            val cal: Calendar = Calendar.getInstance()
            when (params[0]) {
                0 -> {
                    cal.add(Calendar.MONTH, -6) //6 months
                }
                1 -> {
                    cal.add(Calendar.MONTH, -12)
                }//1 year
                2 -> {
                    cal.add(Calendar.MONTH, -24)
                }//2years
                3 -> {
                    cal.add(Calendar.MONTH, -60)
                }//5years
                4 -> {
                    cal.clear()
                }//since beginning
            }
            if (cal.isSet(Calendar.DAY_OF_MONTH)) {
                filter =
                    " and OrderDate Between NOW() And #" + java.sql.Date(cal.timeInMillis) + "# "
            }
            val query =
                "select Customer.CustomerNumber, Customer.CustomerName, Customer.Email, MainOrder.OrderDate, MainOrder.Total, MainOrder.StoreMainOrder, OrderComponent.ArticlePrefix, Customer.PersonalStatus " +
                        " FROM (MainOrder INNER JOIN OrderComponent ON MainOrder.OrderNumber = OrderComponent.OrderNumber) INNER JOIN Customer ON MainOrder.CustomerNumber = Customer.CustomerNumber " +

                        " WHERE (((Customer.CustomerNumber) Not In " +
                        "" +
                        "(SELECT DISTINCT Customer.CustomerNumber " +
                        " FROM (MainOrder INNER JOIN Customer ON MainOrder.CustomerNumber = Customer.CustomerNumber) INNER JOIN OrderComponent ON MainOrder.OrderNumber = OrderComponent.OrderNumber " +
                        " WHERE (((OrderComponent.ArticleType)='Bague'))))" +
                        " AND ((MainOrder.Total)>0) AND ((Customer.PersonalStatus) Is Null Or (Customer.PersonalStatus)<>'" + marriedStatus + "') AND ((OrderComponent.ArticleType)='Solitaire' OR (OrderComponent.ArticleType)='Bague de Fiançailles') AND ((MainOrder.OrderStatus)='Livré-Fermé' Or (MainOrder.OrderStatus)='Livré')) " +
                        filter + " order by OrderDate desc"
            Log.d("test", "" + params[0])
            Log.d("test", "" + query)

            val q = Query(query)
            val s = q.execute()
            if (!s) {
                return false
            }
            data = q.res
            wedding_data = q.res
            current_data = q.res
            Log.e("status", data.stream().map { it["PersonalStatus"] }.toList().toString())
            Log.e("size", data.size.toString())
            runOnUiThread {
                textViewEntryNumber.text =
                    data.size.toString() + " entries"
            }
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            findViewById<ProgressBar>(R.id.progressBar1).visibility = View.GONE

            list.addItemDecoration(
                DividerItemDecoration(
                    list.context,
                    DividerItemDecoration.VERTICAL
                )
            )

            weddingFollowUpListAdapter = WeddingFollowUpListAdapter(data)

            list.adapter = weddingFollowUpListAdapter
            list.layoutManager = LinearLayoutManager(parent)
        }
    }

    inner class FetchStatus : AsyncTask<Int, String, Boolean>() {
        override fun doInBackground(vararg p0: Int?): Boolean {
            val q =
                Query("select OptionValue from OptionValues where Type = 'CustomerStatus' and OptionKey = '1'")
            val s = q.execute()
            if (!s) {
                return false
            }
            marriedStatus = q.res[0]["OptionValue"]!!
            Log.e("married status", marriedStatus)
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            FetchCustomers().execute(lw.checkedItemPosition)
        }

    }
}