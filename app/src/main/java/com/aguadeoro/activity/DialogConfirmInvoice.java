package com.aguadeoro.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DialogConfirmInvoice extends Dialog {

    private final Activity act;
    private final Activity owner;
    private final List<String[]> data;
    private final String tva;
    private final String invDate;
    private final String recDate;
    private String contactID;
    private final String invoiceNumber;
    private final int numberOfItems;
    private final String amount;
    private final String currency;
    private final String registeredBy;
    private final String remark2;
    private String lastID, lastShippingID;
    private String selectedCompany, shippingRemark;
    private final String[] orderCompID;
    private final String shipCost;
    private final Activity acv;
    private final ArrayList<String[]> items;
    private String[] shippingCompanies;
    private final Double amountInt;
    private String statusString = "";
    private ArrayList<Map<String, String>> shippingCompaniesData;
    private final ArrayList<Map<String, String>> stockHistory, productsInfos, inItemsData;

    public DialogConfirmInvoice(Activity a, Context context,
                                List<String[]> data, String tva, String invDate, String recDate, String contactID, String invoiceNumber,
                                int numberOfItems, String amount, String currency, String registeredBy, String remark2, String lastID,
                                String[] orderCompID, String shipCost, Activity acv, ArrayList<String[]> items, Double amountInt,
                                ArrayList<Map<String, String>> stockHistory, ArrayList<Map<String, String>> productsInfos, ArrayList<Map<String, String>> inItemsData) {
        super(a);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
        this.act = a;
        this.owner = getOwnerActivity();
        this.data = data;
        this.tva = tva;
        this.invDate = invDate;
        this.recDate = recDate;
        this.contactID = contactID;
        this.invoiceNumber = invoiceNumber;
        this.numberOfItems = numberOfItems;
        this.amount = amount;
        this.currency = currency;
        this.registeredBy = registeredBy;
        this.remark2 = remark2 + " " + data.get(0)[7] + " " + amount;
        this.lastID = lastID;
        this.orderCompID = orderCompID;
        this.shipCost = shipCost;
        this.acv = acv;
        this.items = items;
        this.amountInt = amountInt;
        this.stockHistory = stockHistory;
        this.productsInfos = productsInfos;
        this.inItemsData = inItemsData;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_invoice);
        new PopulateSpinner().execute();
        TextView total = findViewById(R.id.total);
        TextView recipient = findViewById(R.id.Recipient);
        TextView tvaText = findViewById(R.id.tva);
        TextView invNumberText = findViewById(R.id.invoiceNumber);
        TextView invDateText = findViewById(R.id.invoiceDate);
        TextView shipCostText = findViewById(R.id.ShipCost);
        Button btnSave = findViewById(R.id.saveBtn);
        Button cancel = findViewById(R.id.cancelBtn);
        final Spinner status = findViewById(R.id.status);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.dropdown_status2, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);
        total.setText(amount + currency);
        recipient.setText(data.get(0)[7]);
        tvaText.setText(tva + "%");
        invNumberText.setText(invoiceNumber);
        invDateText.setText(invDate);
        shipCostText.setText(shipCost + currency);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                statusString = status.getSelectedItem().toString();
                Spinner shippingCompanies = findViewById(R.id.shippingCompanies);
                EditText shippingRemarkEdit = findViewById(R.id.shippingRemark);
                selectedCompany = shippingCompanies.getSelectedItem().toString();
                shippingRemark = shippingRemarkEdit.getText().toString();
                Log.d("test", selectedCompany);
                if (!(status.getSelectedItem().toString().equals("No Change"))) {
                    Log.d("NEW STATUS", statusString);
                    new updateStatus().execute();
                } else {
                    new getContactID().execute();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }


    class PopulateSpinner extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            String query;
            Query q;
            boolean s;
            query = "select * from Contacts where Type = 'Shipping'";
            q = new Query(query);
            s = q.execute();
            if (!s) {
                return false;
            }
            shippingCompanies = new String[q.getRes().size() + 1];
            shippingCompanies[0] = "No Shipping";
            shippingCompaniesData = q.getRes();
            for (int i = 0; i < q.getRes().size(); i++) {
                shippingCompanies[i + 1] = q.getRes().get(i).get("Contacts");
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            Spinner shippingCompaniesSpinner = findViewById(R.id.shippingCompanies);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(DialogConfirmInvoice.this.getContext(), android.R.layout.simple_spinner_dropdown_item, shippingCompanies);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            shippingCompaniesSpinner.setAdapter(adapter);
        }
    }

    class updateStatus extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String[] queries = new String[items.size()];
            Log.d("-----", "" + data.size() + " " + items.size());
            for (int i = 0; i < items.size(); i++) {
                String query = "update SupplierOrderMain SET SupplierOrderMain.Status = " + Utils.escape(statusString) + " where SupplierOrderMain.ID=" + data.get(i)[3] + ";";
                queries[i] = query;
            }
            for (String query : queries) {
                Query q = new Query(query);
                boolean success = q.execute();
                if (!success) {
                    Log.d("failed", "NOK00");
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                /*Toast toast = Toast.makeText(CreateInvoiceActivity.this,,Toast.LENGTH_LONG);
                toast.show();*/
                new getContactID().execute();

            }

        }
    }


    class getContactID extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String name = data.get(0)[7];
            String query = "select top 1 Contacts.ID FROM SupplierOrderMain, Contacts INNER JOIN Supplier ON Contacts.SupplierID = Supplier.ID WHERE (([Supplier].[ID]=[Contacts].[SupplierID]) AND ([Supplier].[SupplierName]=" + "'" + name + "'" + "));";
            Query q = new Query(query);
            boolean success = q.execute();
            if (!success) {
                Log.d("NOK", "NOK0");
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            if (result.size() > 0) {
                Log.d("id", "" + result.get(0).get("ID"));
                contactID = result.get(0).get("ID");
                return true;
            } else {
                Toast.makeText(act, "Error supplier does not exist", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                /*Toast toast = Toast.makeText(CreateInvoiceActivity.this,,Toast.LENGTH_LONG);
                toast.show();*/
                new addInvoice().execute();

            }

        }
    }


    class addInvoice extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String query = "insert INTO Invoice" +
                    "( Supplier, InvoiceDate, InvNumber, NumberItems, Amount, Curr, [VAT%], ShippingCost, ReceivedDate, CreatedDate, RegisteredBy, Remark, Autogen ) " +
                    "VALUES (" + Utils.escape(contactID) + ", " + Utils.escape(invDate) + ", " + Utils.escape(invoiceNumber) + ", " + Utils.escape("" + numberOfItems) + ", " + Utils.escape(amount)
                    + ", " + Utils.escape(currency) + ", " + Utils.escape(tva) + ", " + Utils.escape(shipCost) + ", " + Utils.escape(recDate) + ", " + Utils.escape(Utils.shortDateForInsert(Calendar.getInstance().getTime()))
                    + ", " + Utils.escape(registeredBy) + ", " + Utils.escape(remark2) + ", True);";
            Query q = new Query(query);
            boolean success = q.execute();
            if (!success) {
                Log.d("failed", "NOK1");
                return false;
            }
            query = "select top 1 Invoice.ID FROM Invoice ORDER BY Invoice.ID DESC;";
            q = new Query(query);
            boolean success2 = q.execute();
            if (!success2) {
                Log.d("failed", "NOK2");
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            Log.d("idid", "" + result.get(0).get("ID"));
            lastID = result.get(0).get("ID");
            if (!selectedCompany.equals("No Shipping")) {
                String shippingId = null;
                for (int i = 0; i < shippingCompaniesData.size(); i++) {
                    if (Objects.equals(shippingCompaniesData.get(i).get("Contacts"), selectedCompany)) {
                        shippingId = shippingCompaniesData.get(i).get("ID");
                    }
                }
                Log.d("shippingID", "" + shippingId);
                if (shippingId == null) {
                    Log.d("error", "error shippingID = null");
                    return false;
                }
                query = "insert INTO Invoice" +
                        "( Supplier, InvNumber, NumberItems, Curr, [VAT%], CreatedDate, RegisteredBy, Remark, Autogen ) " +
                        "VALUES (" + shippingId + ",'', " + numberOfItems + ", " + Utils.escape(currency) + ", " + Utils.escape(tva) + " , " + Utils.escape(Utils.shortDateForInsert(Calendar.getInstance().getTime()))
                        + ", " + Utils.escape(registeredBy) + ", " + Utils.escape(shippingRemark) + ", True);";
                q = new Query(query);
                success = q.execute();
                if (!success) {
                    Log.d("failed", "NOK1");
                    return false;
                }
                query = "select top 1 Invoice.ID FROM Invoice ORDER BY Invoice.ID DESC;";
                q = new Query(query);
                success2 = q.execute();
                if (!success2) {
                    Log.d("failed", "NOK2");
                    return false;
                }
                result = q.getRes();
                Log.d("idid", "" + result.get(0).get("ID"));
                lastShippingID = result.get(0).get("ID");
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                /*Toast toast = Toast.makeText(CreateInvoiceActivity.this,,Toast.LENGTH_LONG);
                toast.show();*/
                //new getLastID().execute();
                Log.d("OK", "OK");
                new addInvoiceItems().execute();

            }

        }
    }


    class addInvoiceItems extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String[] lines = new String[items.size()];
            for (int i = 0; i < items.size(); i++) {
                Log.d("values", "lastid " + lastID + " items " + Arrays.toString(items.get(i)));
                String newLine = " insert into InvoiceItems (InvoiceID, Amount, Remark, OrderNumber, Detail) " +
                        " VALUES (" + Utils.escape(lastID) + ", " + Utils.escape(items.get(i)[0]) + ", " + Utils.escape(items.get(i)[1]) + ", " + Utils.escape(items.get(i)[3]) + ", " + Utils.escape(items.get(i)[2]) + "); ";
                lines[i] = newLine;
            }
            for (String line : lines) {
                Query q = new Query(line);
                boolean success = q.execute();
                if (!success) {
                    Log.d("failed", "NOK3");
                    return false;
                }
            }
            if (!selectedCompany.equals("No Shipping")) {
                for (int i = 0; i < items.size(); i++) {
                    Log.d("values", "lastShippingID " + lastShippingID + " items " + Arrays.toString(items.get(i)));
                    String newLine = " insert into InvoiceItems (InvoiceID, Remark, OrderNumber, Detail) " +
                            " VALUES (" + Utils.escape(lastShippingID) + " , " + Utils.escape(items.get(i)[1]) + ", " + Utils.escape(items.get(i)[3]) + ", " + Utils.escape(items.get(i)[2]) + "); ";
                    lines[i] = newLine;
                }
                for (String line : lines) {
                    Query q = new Query(line);
                    boolean success = q.execute();
                    if (!success) {
                        Log.d("failed", "NOK3");
                        return false;
                    }
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            /*Intent intent = new Intent(CreateInvoiceActivity.this,SupplierOrderActivity.class);
            startActivity(intent);*/
            new updateHistory().execute();
        }
    }

    class updateHistory extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            String query;
            Query q;
            boolean s;
            String remark = "Order invoice received. " + data.get(0)[7] + " " + recDate;
            query = "insert into OrderHistory (OrderNumber, EntryDate, Remark) values (" + Utils.escape(items.get(0)[3]) + ",Now()," + Utils.escape(remark) + ")";
            q = new Query(query);
            s = q.execute();
            return s;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                new addToOrderComponentQuantity().execute();
            }
        }
    }

    class addToOrderComponentQuantity extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String query = "";
            Query q;
            boolean s;
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            for (Map<String, String> object : stockHistory) {
                String productID = "0";
                String shortCode = "could not find product";
                String description = "error";
                if (object.containsKey("ProductID") && object.containsKey("ShortCode") && object.containsKey("Description")) {
                    productID = object.get("ProductID");
                    shortCode = object.get("ShortCode");
                    description = object.get("Description");
                }
                for (Map<String, String> product : productsInfos) {
                    if ((int) Double.parseDouble(product.get("ID")) == (int) Double.parseDouble(object.get("ProductID"))) {
                        productID = product.get("ID");
                        shortCode = product.get("ProductCode");
                        description = product.get("Description");
                    }
                }
                query = "insert into OrderComponentQuantity (OrderNumber, ProductID, ShortCode, Description, Weight, " +
                        "Quantity, Cost, MovementDate, Type) values (" + Utils.escape(object.get("OrderNumber")) + ", " + productID + ", " + Utils.escape(shortCode) + ", " +
                        "" + Utils.escape(description) + ", " + object.get("Weight") + ", " + object.get("Quantity") + ", " + object.get("Cost") + ", '" + formatter.format(date) + "', '" + object.get("Type") + "')";
                q = new Query(query);
                s = q.execute();
                if (!s) {
                    return false;
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                new AddToStockHistory().execute();
            }
        }
    }

    class AddToStockHistory extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String query;
            Query q;
            for (Map<String, String> row : inItemsData) {
                if (!row.get("ProductID").isEmpty() || !row.get("ProductID").equals("")) {
                    Double price = 0.0;
                    String supplier = "n/a";
                    for (Map<String, String> product : productsInfos) {
                        if (product.get("ID").equals(row.get("id") + ".0")) {
                            if (!product.get("Standard_Cost").isEmpty()) {
                                price = Double.valueOf(product.get("Standard_Cost"));
                            }
                        }
                    }
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i)[2].equals(row.get("orderNumber"))) {
                            supplier = data.get(i)[2];
                        }
                    }
                    if (!row.get("used").isEmpty()) {
                        query = "insert into StockHistory1 (OrderNumber, HistoricDate, ProductID, Supplier, Type, Quantity, Cost) values" +
                                " ('" + row.get("orderNumber") + "',NOW() , " + row.get("id") + ", '" + supplier + "', 3 , " + row.get("used") + ", " + price + ")";
                        q = new Query(query);
                        boolean s = q.execute();
                        if (!s) {
                            return false;
                        }
                    }
                    if (!row.get("returned").isEmpty()) {
                        query = "insert into StockHistory1 (OrderNumber, HistoricDate, ProductID, Supplier, Type, Quantity, Cost) values" +
                                " ('" + row.get("orderNumber") + "',NOW() , " + row.get("id") + ", '" + supplier + "', 1 , " + row.get("returned") + ", " + price + ")";
                        q = new Query(query);
                        boolean s = q.execute();
                        if (!s) {
                            return false;
                        }
                    }
                    if (!row.get("lost").isEmpty()) {
                        query = "insert into StockHistory1 (OrderNumber, HistoricDate, ProductID, Supplier, Type, Quantity, Cost) values" +
                                " ('" + row.get("orderNumber") + "',NOW() , " + row.get("id") + ", '" + supplier + "', 4 , " + row.get("lost") + ", " + price + ")";
                        q = new Query(query);
                        boolean s = q.execute();
                        if (!s) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.d("OK", "OK2");
            Toast toast = Toast.makeText(DialogConfirmInvoice.this.getContext(), "Successfully created invoice", Toast.LENGTH_LONG);
            toast.show();
            Intent intent = new Intent(DialogConfirmInvoice.this.getContext(), SupplierOrderActivity.class);
            DialogConfirmInvoice.this.getContext().startActivity(intent);
        }
    }

    class AddInvoice2 extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            if (!Utils.isOnline()) {
                return false;
            }
            //TODO amount doit etre pareil que celui de l'ancienne invoice ????

            String query = "insert INTO Invoice" +
                    "( Supplier, InvoiceDate, InvNumber, NumberItems, Amount, Curr, [VAT%], ShippingCost, ReceivedDate, CreatedDate, RegisteredBy, Remark, Autogen ) " +
                    "VALUES (126, " + Utils.escape(invDate) + ", " + Utils.escape(invoiceNumber) + ", " + Utils.escape("" + numberOfItems) + ", " + Utils.escape(amount)
                    + ", " + Utils.escape(currency) + ", " + Utils.escape(tva) + ", " + Utils.escape(shipCost) + ", " + Utils.escape(recDate) + ", " + Utils.escape(Utils.shortDateForInsert(Calendar.getInstance().getTime()))
                    + ", " + Utils.escape(registeredBy) + ", " + Utils.escape(remark2) + ", True);";
            Query q = new Query(query);
            boolean success = q.execute();
            if (!success) {
                Log.d("failed", "NOK1");
                return false;
            }
            query = "select top 1 Invoice.ID FROM Invoice ORDER BY Invoice.ID DESC;";
            q = new Query(query);
            boolean success2 = q.execute();
            if (!success2) {
                Log.d("failed", "NOK2");
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            Log.d("idid", "" + result.get(0).get("ID"));
            lastID = result.get(0).get("ID");
            if (!selectedCompany.equals("No Shipping")) {
                String shippingId = null;
                for (int i = 0; i < shippingCompaniesData.size(); i++) {
                    if (Objects.equals(shippingCompaniesData.get(i).get("Contacts"), selectedCompany)) {
                        shippingId = shippingCompaniesData.get(i).get("ID");
                    }
                }
                Log.d("shippingID", "" + shippingId);
                if (shippingId == null) {
                    Log.d("error", "error shippingID = null");
                    return false;
                }
                query = "insert INTO Invoice" +
                        "( Supplier, InvNumber, NumberItems, Curr, [VAT%], CreatedDate, RegisteredBy, Remark, Autogen ) " +
                        "VALUES (" + shippingId + ",'', " + numberOfItems + ", " + Utils.escape(currency) + ", " + Utils.escape(tva) + " , " + Utils.escape(Utils.shortDateForInsert(Calendar.getInstance().getTime()))
                        + ", " + Utils.escape(registeredBy) + ", " + Utils.escape(shippingRemark) + ", True);";
                q = new Query(query);
                success = q.execute();
                if (!success) {
                    Log.d("failed", "NOK1");
                    return false;
                }
                query = "select top 1 Invoice.ID FROM Invoice ORDER BY Invoice.ID DESC;";
                q = new Query(query);
                success2 = q.execute();
                if (!success2) {
                    Log.d("failed", "NOK2");
                    return false;
                }
                result = q.getRes();
                Log.d("idid", "" + result.get(0).get("ID"));
                lastShippingID = result.get(0).get("ID");
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}
