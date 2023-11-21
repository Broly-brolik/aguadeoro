package com.aguadeoro.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aguadeoro.R;
import com.aguadeoro.adapter.CreateInvoiceINAdapter;
import com.aguadeoro.adapter.CreateInvoiceListAdapter;
import com.aguadeoro.utils.Query;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreateInvoiceActivity extends ListActivity {
    private View wheelView;
    private View mainView;
    private String tva = "";
    private String invDate = "";
    private String recDate = "";
    private String contactID;
    private String invoiceNumber = "";
    private int numberOfItems = 0;
    private String amount = "0";
    private String currency = "";
    private String registeredBy = "";
    private String remark2 = "";
    private final String lastID = "";
    private String[] orderCompID;
    private String shipCost = "0";
    private Activity acv;
    private final ArrayList<String[]> items = new ArrayList<>();
    private Double amountInt = 0.0;
    private List<String[]> data = new ArrayList<>();
    private final ArrayList<String[]> recipientStock = new ArrayList<>();
    private final boolean hasADO = false;
    private ArrayList<Map<String, String>> stockHistory = new ArrayList<>();
    private ArrayList<Map<String, String>> productsInfos = new ArrayList<>();
    private CreateInvoiceListAdapter invoiceAdapter;
    private CreateInvoiceINAdapter invoiceINAdapter;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_create_invoice);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        acv = this;
        int len;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                data = null;
            } else {
                len = extras.getInt("length");
                for (int i = 0; i < len; i++) {
                    data.add(extras.getStringArray("DATA" + i));
                }
            }
        } else {
            len = savedInstanceState.getInt("length");
            for (int i = 0; i < len; i++) {
                data.add(savedInstanceState.getStringArray("DATA" + i));
            }
        }
        if (data != null) {
            for (String[] order : data) {
                if (order[7].equals("ADO Stones")) {
                    recipientStock.add(order);
                }
            }
        }
        /*
        if (recipientStock.size() > 0) {
            hasADO = true;
            findViewById(R.id.retrieveStockButton).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.retrieveStockButton).setVisibility(View.GONE);
        }*/

        final Spinner curr = findViewById(R.id.currency_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.curr_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        curr.setAdapter(adapter);
        Button btnCreate = findViewById(R.id.btn_create_invoice);
        final EditText shipping = findViewById(R.id.shipping);
        final EditText invNumber = findViewById(R.id.invoice_number);
        final Spinner regBy = findViewById(R.id.registered_by);
        final CheckBox tvaCheck = findViewById(R.id.tvaCheck);
        ImageView btnAdd = findViewById(R.id.add);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.regby_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regBy.setAdapter(adapter2);
        final EditText remark = findViewById(R.id.remark);
        final DatePicker dpInvoice = findViewById(R.id.invoice_date);
        final DatePicker dpReceived = findViewById(R.id.received_date);
        invoiceAdapter = new CreateInvoiceListAdapter(this, data, hasADO, stockHistory, productsInfos);
        CreateInvoiceActivity.this.setListAdapter(invoiceAdapter);
        final List<String[]> finalData = data;
        wheelView.setVisibility(View.VISIBLE);
        mainView.setVisibility(View.GONE);
        new getStockHistory().execute();
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvaCheck.isChecked()) {
                    Log.d("TVACHECK", "OK");
                    tva = "7.7";
                } else {
                    tva = "0";
                }
                orderCompID = new String[finalData.size()];
                Log.d("number of items", "" + finalData.size());
                ListView list = CreateInvoiceActivity.this.findViewById(android.R.id.list);
                amountInt = 0.0;
                for (int i = 0; i < finalData.size(); i++) {
                    if (!invoiceAdapter.getValueFromFirstEditText(i).isEmpty() && Double.parseDouble(invoiceAdapter.getValueFromFirstEditText(i)) >= 0) {
                        amountInt += Double.parseDouble(invoiceAdapter.getValueFromFirstEditText(i));
                    }

                    Log.d("amount ", invoiceAdapter.getValueFromFirstEditText(i) + "\n remark " + invoiceAdapter.getValueFromSecondEditText(i) + "\n detail " + invoiceAdapter.getValueFromThirdEditText(i) + "\n orderNumber " + finalData.get(i)[15]);
                    String[] item = {invoiceAdapter.getValueFromFirstEditText(i), invoiceAdapter.getValueFromSecondEditText(i), invoiceAdapter.getValueFromThirdEditText(i), finalData.get(i)[15]};
                    if (items.size() == 0) {
                        items.add(item);
                    } else {
                        boolean same = false;
                        for (int j = 0; j < items.size(); j++) {
                            if (items.get(j)[3].equals(item[3])) {
                                same = true;
                                items.set(j, item);
                            }
                        }
                        if (!same) {
                            items.add(item);
                        }
                    }
                }
                Log.d("invoice", "data : " + dpInvoice.getDayOfMonth() + "/" + dpInvoice.getMonth() + "/" + dpInvoice.getYear() + ", " + dpReceived.getDayOfMonth() + "/" + dpReceived.getMonth() + "/" + dpReceived.getYear() + ", " + shipping.getText() + ", " + invNumber.getText() + ", " + regBy.getSelectedItem() + ", " + curr.getSelectedItem() + ", " + remark.getText() + ", " + tva + ", " + finalData.get(0)[7] + "\n " + items.get(0)[0]);
                invDate = onDateSet(dpInvoice.getYear(), dpInvoice.getMonth(), dpInvoice.getDayOfMonth());
                recDate = onDateSet(dpReceived.getYear(), dpReceived.getMonth(), dpReceived.getDayOfMonth());
                Log.d("testDate", "" + invDate + " " + recDate);
                invoiceNumber = invNumber.getText().toString();
                numberOfItems = finalData.size();
                currency = curr.getSelectedItem().toString();
                registeredBy = regBy.getSelectedItem().toString();
                remark2 = remark.getText().toString();
                shipCost = shipping.getText().toString();
                if (shipCost.equals("")) {
                    shipCost = "0";
                }
                if (invoiceNumber.equals("")) {
                    invoiceNumber = "N/A";
                }
                Log.d("shipcost", shipCost);
                Double shipCostInt = Double.parseDouble(shipCost);
                amountInt += shipCostInt;
                if (tva.equals("7.7")) {
                    amountInt += 7.7 / 100 * amountInt;
                }
                DecimalFormat df = new DecimalFormat("#.00");
                DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance();
                sym.setDecimalSeparator('.');
                df.setDecimalFormatSymbols(sym);
                amount = df.format(amountInt);
                Log.d("stock history length", "" + stockHistory.size());
                ArrayList<Map<String, String>> inItemsData = new ArrayList<>();
                for (int i = 0; i < Objects.requireNonNull(rv.getAdapter()).getItemCount(); i++) {
                    View itemView = Objects.requireNonNull(rv.findViewHolderForAdapterPosition(i)).itemView;
                    inItemsData.add(new HashMap<String, String>() {
                        {
                            put("orderNumber", ((EditText) itemView.findViewById(R.id.orderNumber)).getText().toString());
                            put("id", ((EditText) itemView.findViewById(R.id.id)).getText().toString());
                            put("used", ((EditText) itemView.findViewById(R.id.used)).getText().toString());
                            put("returned", ((EditText) itemView.findViewById(R.id.returned)).getText().toString());
                            put("lost", ((EditText) itemView.findViewById(R.id.lost)).getText().toString());
                        }
                    });
                }
                if (data != null) {
                    new DialogConfirmInvoice(acv, CreateInvoiceActivity.this, data, tva, invDate, recDate, contactID, invoiceNumber, numberOfItems, amount, currency, registeredBy, remark2, lastID, orderCompID, shipCost, acv, items, amountInt, stockHistory, productsInfos, inItemsData).show();
                } else {
                    Toast.makeText(CreateInvoiceActivity.this, "something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invoiceINAdapter.addRow();
            }
        });
    }

    public String onDateSet(int year, int monthOfYear, int dayOfMonth) {

        // do some stuff for example write on log and update TextField on activity
        int month = monthOfYear + 1;
        String fm = "" + month;
        String fd = "" + dayOfMonth;
        if (month < 10) {
            fm = "0" + month;
        }
        if (dayOfMonth < 10) {
            fd = "0" + dayOfMonth;
        }
        String date = "" + fd + "." + fm + "." + year;
        return date;
    }

    public void setStockHistory(ArrayList<Map<String, String>> stockHistory) {
        this.stockHistory = stockHistory;
        invoiceAdapter = new CreateInvoiceListAdapter(this, data, hasADO, this.stockHistory, productsInfos);
        CreateInvoiceActivity.this.setListAdapter(invoiceAdapter);
    }


    private class getStockHistory extends AsyncTask<Void, String, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            ArrayList<String> orderNumbers = new ArrayList<>();
            for (String[] object : data) {
                String fullOrderNumber = object[13];
                orderNumbers.add(fullOrderNumber);
            }
            if (orderNumbers.size() > 0) {
                StringBuilder whereBuilder = new StringBuilder();
                whereBuilder.append(" where OrderNumber = '").append(orderNumbers.get(0)).append("'");
                for (int i = 1; i < orderNumbers.size(); i++) {
                    whereBuilder.append(" or OrderNumber = '").append(orderNumbers.get(i)).append("'");
                }
                String query;
                Query q;
                query = "select * from StockHistory1 " + whereBuilder;
                q = new Query(query);
                boolean s = q.execute();
                if (!s) {
                    return false;
                }
                stockHistory = q.getRes();

                ArrayList<String> productIDs = new ArrayList<>();
                for (Map<String, String> object : stockHistory) {
                    productIDs.add(object.get("ProductID"));
                }
                if (productIDs.size() > 0) {
                    whereBuilder = new StringBuilder();
                    whereBuilder.append(" where ID = ").append(productIDs.get(0));
                    for (int i = 1; i < productIDs.size(); i++) {
                        whereBuilder.append(" or ID = ").append(productIDs.get(i));
                    }
                    query = "select * from Products " + whereBuilder;
                    q = new Query(query);
                    s = q.execute();
                    if (!s) {
                        return false;
                    }
                    productsInfos = q.getRes();
                    return true;
                }
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            wheelView.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
            rv = CreateInvoiceActivity.this.findViewById(R.id.recyclerView);
            invoiceINAdapter = new CreateInvoiceINAdapter(stockHistory, productsInfos);
            rv.setAdapter(invoiceINAdapter);
            rv.setLayoutManager(new LinearLayoutManager(CreateInvoiceActivity.this));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        if (id == android.R.id.home) {
            //startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}