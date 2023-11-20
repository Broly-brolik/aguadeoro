package com.aguadeoro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PdfPrint;
import android.print.PrintAttributes;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.aguadeoro.R;
import com.aguadeoro.adapter.SupplierOrderListAdapter;
import com.aguadeoro.models.SupplierOrder;
import com.aguadeoro.utils.Constants;
import com.aguadeoro.utils.MyAsyncTask;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class SupplierOrderActivity extends ListActivity implements AdapterView.OnItemSelectedListener {
    ArrayList<Map<String, String>> orderList;
    private View wheelView;
    private View mainView;
    private View dialog;
    private String spinnerValue, approvalValue;
    private ListView lv;
    private Activity acv;

    private String defaultOrder = "";

    private List<String> recipients = new ArrayList<>();

    private AlertDialog searchDialog = null;

    List<String> selectedOrders = new ArrayList<>();

    List<String> shippingCompanies = new ArrayList<>();

    Context context;

    Boolean showSelectedOrders = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();


//        Bundle b = getIntent().getExtras();
//        if(b != null)
//            defaultOrder = b.getString("sorder", "");
//            Intent intent = new Intent(this, OrderDetailActivity.class);
//            intent.putExtra(Utils.ORDER_NO, defaultOrder);
////            intent.putExtra(Utils.COMP_ID, object[4]);
//            intent.putExtra(Utils.CALLER, this.getClass().toString());
//            startActivity(intent);


//        Log.e("max r", Utils.getStringSetting("max_results"));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_supplier_order);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        showProgress(true);
        acv = this;
        new ListOrders().execute("");
        lv = findViewById(android.R.id.list);
        lv.setLongClickable(true);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> object = (Map<String, String>) getListAdapter().getItem(i);

                showDialog(object);
                return true;
            }
        });
//


    }

    public void showDialog(Map<String, String> object) {
        DialogChoiceSupplier dcs = new DialogChoiceSupplier(acv, object, this);
        dcs.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.supplier_order, menu);
        return true;
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
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        if (id == R.id.action_search_order) {
            showSearchDialog();
        }
        if (id == R.id.filter_status) {
            Log.d("filter", "OK");
            showSearchDialog();
        }
        if (id == R.id.selected_orders) {
            Toast.makeText(this, selectedOrders.toString(), Toast.LENGTH_LONG).show();
            showSelectedOrders = !showSelectedOrders;
            new ListOrders().execute("");
        }
        if (id == R.id.send_selected_orders) {
            if (selectedOrders.isEmpty()) {
                Toast.makeText(this, "Select at least one order", Toast.LENGTH_LONG).show();
            } else {

                showSendSupplierOrderDialog();
            }
        }
        if (id == R.id.unselect_all){
            showProgress(true);
            selectedOrders.clear();
            showSelectedOrders = false;
            new ListOrders().execute("");
        }

        return true;
    }


    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            wheelView.setVisibility(View.VISIBLE);
            wheelView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            wheelView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mainView.setVisibility(View.VISIBLE);
            mainView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mainView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            wheelView.setVisibility(show ? View.VISIBLE : View.GONE);
            mainView.setVisibility(show ? View.GONE : View.VISIBLE);
        }


    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        Map<String, String> object = (Map<String, String>) getListAdapter().getItem(position);

        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(Utils.ORDER_NO, object.get("OrderNumber"));
        intent.putExtra(Utils.COMP_ID, object.get("OrderComponentID"));
        intent.putExtra(Utils.CALLER, this.getClass().toString());
        startActivity(intent);

    }


    public void sortOrder(View v) {
        String sortBy = v.getTag().toString();
        new ListOrders().execute(sortBy);
    }

    public SupplierOrder getSupplierOrderFromNumber(String number) {
        String queryShippingCompanies = String.format("select * FROM SupplierOrderMain WHERE SupplierOrderNumber = '%s'", number);

//                    String SupplierOrderTemplate = getAssets()
        Query q = new Query(queryShippingCompanies);
        boolean success = q.execute();
        if (success) {
//                    shippingCompanies = q.getRes().stream().map(contact -> contact.get("Contacts")).collect(Collectors.toList());
            SupplierOrder supplierOrder = new SupplierOrder();
            supplierOrder.setInstruction(q.getRes().get(0).get("Instruction"));
            supplierOrder.setDeadline(LocalDate.parse(q.getRes().get(0).get("Deadline"), Constants.fromAccessFormatter).format(Constants.DateFormatter));
            supplierOrder.setRecipient(q.getRes().get(0).get("Recipient"));
            supplierOrder.setStatus(q.getRes().get(0).get("Status"));
            supplierOrder.setCreatedDate(LocalDate.parse(q.getRes().get(0).get("CreatedDate"), Constants.fromAccessFormatter).format(Constants.DateFormatter));
            supplierOrder.setStep(q.getRes().get(0).get("Step"));
            supplierOrder.setInstructionCode(q.getRes().get(0).get("InstructionCode"));
            supplierOrder.setRemark(q.getRes().get(0).get("Remark"));
            supplierOrder.setSupplierOrderNumber(q.getRes().get(0).get("SupplierOrderNumber"));
            supplierOrder.setApproval(q.getRes().get(0).get("Approval"));
            supplierOrder.setShippedDate(q.getRes().get(0).get("ShippedDate"));
            supplierOrder.setShippedBy(q.getRes().get(0).get("ShippedBy"));
            try {
                supplierOrder.setOrderComponentID(Integer.parseInt(q.getRes().get(0).get("OrderComponentID")));
            } catch (Exception e) {

            }
            Log.e("order", supplierOrder.toString());
            return supplierOrder;
//                        Toast.makeText(SupplierOrderActivity.this, q.getRes().toString(), Toast.LENGTH_LONG).show();
        }
        return new SupplierOrder();
    }

    public List<String> getSupplierOrderPics(String orderComponentID) {
        ArrayList<String> empty = new ArrayList<>();
        String query = String.format("select Link FROM SupplierOrderPics WHERE OrderComponentID = %s", orderComponentID);
        Query q = new Query(query);
        boolean success = q.execute();
        if (!success) {
            return empty;
        } else {
            return q.getRes().stream().map(hash -> hash.get("Link")).collect(Collectors.toList());
        }
    }

    public List<String> getInventoryPics(int orderComponentID) {
        ArrayList<String> empty = new ArrayList<>();
        String queryInventoryCode = String.format("select InventoryID FROM OrderComponent WHERE ID = %s", orderComponentID);
        Query q = new Query(queryInventoryCode);
        boolean success = q.execute();
        if (!success) {
            return empty;
        }
        String inventoryID = q.getRes().get(0).get("InventoryID");
        Log.e("inventoryID", inventoryID);
        String queryInventoryPics = String.format("select Image FROM Inventory WHERE ID = %s", inventoryID);
        q = new Query(queryInventoryPics);
        success = q.execute();
        if (!success) {
            return empty;
        }
        Log.e("pics", q.getRes().stream().map(stringStringMap -> stringStringMap.get("Image")).collect(Collectors.toList()).toString());

        return q.getRes().stream().map(stringStringMap -> stringStringMap.get("Image")).collect(Collectors.toList());
    }

    public String sendSupplierOrder() {
        try {
            InputStream orderEntryFile = getAssets().open("orderEntry.html");
            InputStream tableEntryFile = getAssets().open("tableEntry.html");
            InputStream supplierOrderTemplateFile = getAssets().open("SupplierOrdersTemplate.html");

            String tableEntries = "";

            StringBuilder orderEntryStringBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(orderEntryFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    orderEntryStringBuilder.append(line).append("\n");
                }
            }
            String orderEntryTemplate = orderEntryStringBuilder.toString();


            StringBuilder tableEntryStringBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(tableEntryFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    tableEntryStringBuilder.append(line).append("\n");
                }
            }
            String tableEntryTemplate = tableEntryStringBuilder.toString();


            StringBuilder supplierOrderStringBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(supplierOrderTemplateFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    supplierOrderStringBuilder.append(line).append("\n");
                }
            }
            String supplierOrderTemplate = supplierOrderStringBuilder.toString();

            ArrayList<SupplierOrder> selectedSupplierOrder = new ArrayList<>();

            for (String order : selectedOrders) {
                selectedSupplierOrder.add(getSupplierOrderFromNumber(order));
            }
            List<String> recipientList = selectedSupplierOrder.stream().map(SupplierOrder::getRecipient).distinct().collect(Collectors.toList());
            Log.e("recipient list", recipientList.toString());
            if (recipientList.size() > 1) {
                return "";
            }
            supplierOrderTemplate = supplierOrderTemplate.replace("{supplier}", recipientList.get(0));

            String order_pics_template = "<p>{pic}</p>";

            for (int i = 0; i < 4; i++) {
                String tableEntry = tableEntryTemplate;
                String orderEntries = "";

                int orderCount = 0;
                for (SupplierOrder supplierOrder : selectedSupplierOrder) {
                    String orderEntry = orderEntryTemplate;
                    String imgTemplate1 = "<table class=\"image-table\"><tr><td><img style=\"width: 33%;\"src=\"{url}\"     /></td></tr></table>";
                    String imgTemplate2 = "<table class=\"image-table\"><tr><td><img style=\"width: 66%;\"src=\"{url1}\"     /></td><td><img style=\"width: 66%;\"src=\"{url2}\"     /></td></tr></table>";
                    String imgTemplate3 = "<table class=\"image-table\"><tr><td><img style=\"width: 85%;\"src=\"{url1}\"     /></td><td><img style=\"width: 85%;\"src=\"{url2}\"     /></td><td><img style=\"width: 85%;\"src=\"{url3}\"     /></td></tr></table>";
                    if (!supplierOrder.getStatus().equals("Status " + i)) {
                        continue;
                    }

                    orderEntry = orderEntry.replace("{date}", supplierOrder.getCreatedDate());
                    orderEntry = orderEntry.replace("{orderNumber}", supplierOrder.getSupplierOrderNumber());
                    orderEntry = orderEntry.replace("{deadline}", supplierOrder.getDeadline());
                    orderEntry = orderEntry.replace("{instruction}", supplierOrder.getInstruction());
                    orderEntry = orderEntry.replace("{remark}", supplierOrder.getRemark());

                    List<String> pics = getInventoryPics(supplierOrder.getOrderComponentID());
//                    if (pics.size() == 1) {
//                        pics.add(pics.get(0));
//                    }
                    if (pics.size() == 0) {
//                        Log.e("no pics", "no pics");
                        orderEntry = orderEntry.replace("{pics}", "");

                    }

                    String base_url = "http://195.15.223.234/aguadeoro/06_inventory%20toc%20opy/";
                    if (pics.size() == 1) {
                        imgTemplate1 = imgTemplate1.replace("{url}", base_url + pics.get(0));
                        Log.e("template", imgTemplate1);
                        orderEntry = orderEntry.replace("{pics}", imgTemplate1);

                    }
                    if (pics.size() == 2) {
                        imgTemplate2 = imgTemplate2.replace("{url1}", base_url + pics.get(0));
                        imgTemplate2 = imgTemplate2.replace("{url2}", base_url + pics.get(1));
                        orderEntry = orderEntry.replace("{pics}", imgTemplate2);

                    }
                    if (pics.size() >= 3) {
                        imgTemplate3 = imgTemplate3.replace("{url1}", base_url + pics.get(0));
                        imgTemplate3 = imgTemplate3.replace("{url2}", base_url + pics.get(1));
                        imgTemplate3 = imgTemplate3.replace("{url3}", base_url + pics.get(2));
                        orderEntry = orderEntry.replace("{pics}", imgTemplate3);
                    }

                    List<String> supplierOrderPics = getSupplierOrderPics(String.valueOf(supplierOrder.getOrderComponentID()));

                    if (!supplierOrderPics.isEmpty()) {
                        orderEntry += "<h3>Links for more pictures</h3>\n";
                        for (String pic : supplierOrderPics) {
                            orderEntry += order_pics_template.replace("{pic}", pic);
                        }
                    }

                    orderEntries += orderEntry;
//                    Log.e("orderEntry", orderEntry);
                    orderCount++;
                }
                if (orderCount > 0) {
//                    Log.e("index", String.valueOf(i));
                    tableEntry = tableEntry.replace("{orderEntries}", orderEntries);
//                    Log.e("entries", orderEntries);
                    tableEntry = tableEntry.replace("{statusNumber}", String.valueOf(i));

                    tableEntries += tableEntry;
                }
            }

            supplierOrderTemplate = supplierOrderTemplate.replace("{content}", tableEntries);
//            Log.e("supplier template", supplierOrderTemplate);
            return supplierOrderTemplate;

        } catch (IOException e) {
            throw new RuntimeException(e);

        }

    }

    public void showSendSupplierOrderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Send invoices");
        dialog = inflater.inflate(R.layout.dialog_send_supplier_order, null);

        Spinner spinnerShipping = dialog.findViewById(R.id.spinner_shipped_by);
        spinnerShipping.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shippingCompanies));

        WebView order_preview = dialog.findViewById(R.id.preview_order);
//        String htmlCode = "<html><body><h1>Hello, World!</h1></body></html>";
//
        String filled_template = sendSupplierOrder();
        if (filled_template.isEmpty()) {
            Toast.makeText(this, "Can't send to multiple supplier", Toast.LENGTH_LONG).show();
            return;
        }
        order_preview.loadData(sendSupplierOrder(), "text/html", "UTF-8");
//


        TextView shippedDate = dialog.findViewById(R.id.textViewShippedDate);
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                shippedDate.setText(Utils.shortDateForDisplay(myCalendar.getTime()));
            }
        };
        ;
        shippedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SupplierOrderActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        builder.setView(dialog);
        builder.setCancelable(true);

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                String jobName = getString(R.string.app_name) + " Document";
                PrintAttributes attributes = new PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
                File path = new File(Environment.getExternalStorageDirectory() + "/03_supplierorders/");
                PdfPrint pdfPrint = new PdfPrint(attributes);
                String filename = String.valueOf(System.currentTimeMillis()) + ".pdf";
                pdfPrint.print(order_preview.createPrintDocumentAdapter(jobName), path, filename);
                File test = new File(Environment.getExternalStorageDirectory() + "/03_supplierorders/" + filename);
                Log.e("exists", String.valueOf(test.exists()));

                Intent printIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                Uri uri = FileProvider.getUriForFile(context, "com.aguadeoro.android.fileprovider", test);
                printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                printIntent.setDataAndType(uri, context.getContentResolver().getType(uri));
                printIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                ArrayList<Uri> uriList = new ArrayList<>();
                uriList.add(uri);
                printIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);

                printIntent.setType("application/pdf");
                printIntent.putExtra(Intent.EXTRA_SUBJECT, "test");
                //printIntent.putExtra(Intent.EXTRA_TEXT, body);
                printIntent.putExtra(Intent.EXTRA_TEXT,
                        Html.fromHtml("hello"
                                , Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
                printIntent.putExtra(Intent.EXTRA_HTML_TEXT, "hello");
                String[] bcc = {"prod@aguadeoro.ch"};
                printIntent.putExtra(Intent.EXTRA_BCC, bcc);

                context.startActivity(printIntent);
                updateSupplierOrders(selectedOrders, LocalDate.parse(shippedDate.getText().toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).format(Constants.fromAccessFormatterDate), spinnerShipping.getSelectedItem().toString());


            }
        });

        AlertDialog alert = builder.create();
        searchDialog = alert;
        alert.show();
    }

    public void updateSupplierOrders(List<String> orders, String shippedDate, String shippedBy){
        for (String order: orders){
            String query = String.format("UPDATE SupplierOrderMain set ShippedDate = #%s#, ShippedBy = '%s' WHERE SupplierOrderNumber = '%s'", shippedDate, shippedBy, order);
            Query q = new Query(query);
            boolean success = q.execute();
            if (success) {
                Toast.makeText(this, "Orders updated", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void showSearchDialog() {
        String[] items = new String[Utils.getSetSetting(Utils.SUPP_ORD_STT).length + 1];
        for (int i = 0; i < Utils.getSetSetting(Utils.SUPP_ORD_STT).length; i++) {
            items[i] = Utils.getSetSetting(Utils.SUPP_ORD_STT)[i];
        }
        items[items.length - 1] = "Status 1 + 2";


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle(getString(R.string.search));
        dialog = inflater.inflate(R.layout.dialog_search_order, null);
        Spinner status = dialog.findViewById(R.id.search_order_status);
        Spinner recipient = dialog.findViewById(R.id.search_order_recipient);
        status.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items));
        status.setOnItemSelectedListener(this);
        recipient.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, recipients));
        Spinner approval = dialog.findViewById(R.id.search_order_approval);
        approval.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Utils.getSetSetting("ApprovalStatus")));
        approval.setOnItemSelectedListener(this);
        builder.setView(dialog);
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        searchDialog = alert;
        alert.show();
    }

    public void filterName(String name, int i) {
        String filter;
        switch (i) {
            case 1:
                filter = " Recipient = " + "'" + name + "' and (Status = 'Status 1' or Status = 'Status 2') ";
                break;
            case 2:
                filter = " Recipient = " + "'" + name + "' and (Status = 'Status 3') ";
                break;
            case 3:
                filter = " Recipient = " + "'" + name + "' and (Status = 'Status 1' or Status = 'Status 2' or Status = 'Status 3') ";
                break;
            default:
                filter = "Recipient = " + "'" + name + "' ";


        }
        Log.d("filterName", "name : " + name);
        new ListOrders().execute(Utils.DEADLINE, filter);

    }

    public void searchOrder(View v) {

        showProgress(true);
        if (searchDialog != null) {
            searchDialog.dismiss();
        }
        String searchString = "";
        if (v.getId() == R.id.search_num_btn) {
            searchString = "MainOrder." + Utils.ORDER_NO + "= "
                    + ((EditText) dialog.findViewById(R.id.search_num)).getText().toString();
            if (((EditText) dialog.findViewById(R.id.search_num)).getText().length() == 0) {
                searchString = "";
            }
        } else if (v.getId() == R.id.search_date_btn) {
            searchString = " InvoiceItems.InvoiceID = " + ((EditText) dialog.findViewById(R.id.search_date)).getText().toString();
        } else if (v.getId() == R.id.search_deadline_btn) {
            searchString = " Customer.CustomerName like "
                    + Utils.escape("%" + ((EditText) dialog.findViewById(R.id.search_deadline)).getText().toString() + "%");
        } else if (v.getId() == R.id.search_name_btn) {
            searchString = "SupplierOrderMain.Recipient like "
                    + Utils.escape("%" + ((Spinner) dialog.findViewById(R.id.search_order_recipient)).getSelectedItem().toString() + "%");
        } else if (v.getId() == R.id.search_status_btn) {
            if (spinnerValue.equals("Status 1 + 2")) {
                searchString = "(" + "Status" + " = 'Status 1' or Status = 'Status 2')";

            } else {
                searchString = " Status" + " = " + "'" + spinnerValue + "'";

            }
            Log.d("spinnerSearch", spinnerValue);
        } else if (v.getId() == R.id.search_approval_btn) {
            searchString = " SupplierOrderMain.ApprovalStatus" + " = " + "'" + approvalValue + "'";
        }
        new ListOrders().execute(Utils.DEADLINE, searchString);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.search_order_status) {
            spinnerValue = adapterView.getItemAtPosition(i).toString();
        } else if (adapterView.getId() == R.id.search_order_approval) {
            approvalValue = adapterView.getItemAtPosition(i).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    class ListOrders extends MyAsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }

            String queryShippingCompanies = "select Contacts FROM Contacts WHERE Type = 'Shipping' ORDER BY Contacts";


            Query q = new Query(queryShippingCompanies);
            boolean success = q.execute();
            if (success) {
                shippingCompanies = q.getRes().stream().map(contact -> contact.get("Contacts")).collect(Collectors.toList());
            }

            String queryContact = "select Contacts FROM Contacts WHERE Type = 'Supplier' ORDER BY Contacts";
            q = new Query(queryContact);
            success = q.execute();
            if (success) {
                recipients = q.getRes().stream().map(contact -> contact.get("Contacts")).collect(Collectors.toList());
            }

            String whereClause = "";
            if (args.length > 1) {
                whereClause = " where " + args[1];
                Log.e("WHERE CLAUSE", whereClause);
            } else {
                whereClause = " where (Status = 'Status 1' or Status = 'Status 2')";

            }
//            orderList = new ArrayList<String[]>();
            //String sortBy = " order by MainOrder.OrderNumber desc, OrderComponent.ArticleNumber desc, SupplierOrderMain.Deadline desc";
            String sortBy = " order by Deadline DESC";
//            String query = "select OrderComponent.ArticleNumber, SupplierOrderMain.*, OrderComponent.OrderNumber, Customer.CustomerName, OrderComponent.ID FROM ((MainOrder INNER JOIN OrderComponent ON MainOrder.OrderNumber = OrderComponent.OrderNumber) INNER JOIN SupplierOrderMain ON OrderComponent.ID = SupplierOrderMain.OrderComponentID)";

            String query = String.format("select TOP %s * FROM SupplierOrderMain", Utils.getStringSetting("max_results"));
            query += whereClause;
            query += sortBy;


            if (showSelectedOrders) {
                String ordernumbers = selectedOrders.stream().reduce("", (subtotal, element) -> subtotal + "'" + element + "'" + ", ");
                ordernumbers = ordernumbers.substring(0, ordernumbers.length() - 2);
                ordernumbers = "(" + ordernumbers + ")";
//                query = "select * FROM SupplierOrderMain WHERE SupplierOrderNumber in " + ordernumbers +";";
                query = "select * FROM SupplierOrderMain WHERE SupplierOrderNumber in " + ordernumbers + ";";
                Log.e("query select", query);
            }


            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            Log.d("+++", "" + result.size());

//            Log.e("RESULT", result.get(0).get("Instruction"));
            orderList = result;


            List<String> order_component_ids = orderList.stream().map(order -> order.get("OrderComponentID")).collect(Collectors.toList());
            if (order_component_ids.size() > 0) {
                String ids_as_string = order_component_ids.stream().reduce("", (subtotal, element) -> subtotal + element + ", ");
                ids_as_string = "(" + ids_as_string.substring(0, ids_as_string.length() - 2) + ")";
                Log.e("ids as string", ids_as_string);

                query = "select ID, OrderNumber, ArticleNumber FROM OrderComponent WHERE ID in " + ids_as_string;
                q = new Query(query);
                success = q.execute();
                if (!success) {
                    return false;
                }
                Log.e("component", q.getRes().toString());

                List<String> order_numbers = new ArrayList<>();

                for (Map<String, String> order : orderList) {
//                order.put("")
                    if (order_component_ids.contains(order.get("OrderComponentID"))) {
                        Map<String, String> res = q.getRes().stream().filter(component -> component.get("ID").equals(order.get("OrderComponentID"))).collect(Collectors.toList()).get(0);
                        order.put("OrderNumber", res.get("OrderNumber"));
                        order_numbers.add(res.get("OrderNumber"));
                        order.put("ArticleNumber", res.get("ArticleNumber"));
                    }
//                result = q.getRes();
//                String orderNumber = result.get(0).get("OrderNumber");
//                String articleNumber = result.get(0).get("ArticleNumber");
                }



                String numbers_as_string = order_numbers.stream().reduce("", (subtotal, element) -> subtotal + element + ", ");
                numbers_as_string = "(" + numbers_as_string.substring(0, numbers_as_string.length() - 2) + ")";
                Log.e("num as string", numbers_as_string);

                query = "select OrderNumber, Deadline FROM MainOrder WHERE OrderNumber in " + numbers_as_string;
                q = new Query(query);
                success = q.execute();
                if (!success) {
                    return false;
                }
                Log.e("dead", q.getRes().toString());
                for (Map<String, String> order : orderList) {
//                order.put("")
                    if (numbers_as_string.contains(order.get("OrderNumber"))) {
                        Map<String, String> res = q.getRes().stream().filter(component -> component.get("OrderNumber").equals(order.get("OrderNumber"))).collect(Collectors.toList()).get(0);
                        order.put("OrderDeadline", res.get("Deadline"));
//                        order_numbers.add(res.get("OrderNumber"));
//                        order.put("ArticleNumber", res.get("ArticleNumber"));
                    }
//                result = q.getRes();
//                String orderNumber = result.get(0).get("OrderNumber");
//                String articleNumber = result.get(0).get("ArticleNumber");
                }
            }


            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                SupplierOrderActivity.this.setListAdapter(new SupplierOrderListAdapter(
                        SupplierOrderActivity.this, orderList, true, selectedOrders));
            } else {
                Toast.makeText(SupplierOrderActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }

}
