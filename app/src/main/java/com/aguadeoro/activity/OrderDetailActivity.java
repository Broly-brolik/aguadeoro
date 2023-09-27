package com.aguadeoro.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.ComponentHistoryListAdapter;
import com.aguadeoro.adapter.ComponentHistoryStatusUpdateListAdapter;
import com.aguadeoro.adapter.InstallmentListAdapter;
import com.aguadeoro.adapter.OrderComponentListAdapter;
import com.aguadeoro.adapter.OrderHistoryListAdapter;
import com.aguadeoro.adapter.OutItemsAdapter;
import com.aguadeoro.utils.EditTextDropdown;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderDetailActivity extends ListActivity {

    public ArrayList<String[]> componentList;
    ArrayList<String[]> paymentList;
    String orderNumber, orderStatus, oldOrderStatus, orderDate, orderDeadline, orderTotal,
            orderDiscount, orderPaid, orderRemain, remark, orderType,
            customerName, customer2Name, mainCustomerNo, address, address2, email, email2, tel,
            tel2, weddingDate, weddingDate2, TVA, compID, trueSuppOrdNo, store, oldStore;
    String seller;
    String callerActivity;
    int selectedCompPosition;
    ArrayList<String[]> suppOrderListSelectedToSendToSupplier;
    String suppReminderSuppName, suppReminderDeadline;
    LayoutInflater inflater;
    AlertDialog showComp;
    Map<String, String> res;
    AlertDialog addComp;
    AlertDialog reminder;
    int pos;
    AlertDialog reminderSupplier;
    LinearLayout installmentView;
    ArrayList<String[]> installs;
    LinearLayout installmentList;
    AlertDialog viewInstDialog;
    ArrayList<String[]> updateInstallList;
    ArrayList<String> driveLinks = new ArrayList<>();
    private View wheelView;
    private View mainView;
    private String name;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CALENDAR = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final String[] PERMISSIONS_CALENDAR = {
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
    };
    private ArrayList<Map<String, String>> stockQuantities = new ArrayList<>();
    public int lineCOunt = 0;
    private ArrayList<Map<String, String>> outItemsData = new ArrayList<>();
    private ArrayList<ArrayList<String>> outItemsToSend = new ArrayList<>();
    private Bitmap imageBitmap;
    private ScrollView window;
    private ArrayList<String[]> supplierOrderStatus = new ArrayList<>();

    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_order_detail);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        showProgress(true);
        componentList = new ArrayList<>();
        paymentList = new ArrayList<>();
        orderNumber = getIntent().getStringExtra(Utils.ORDER_NO);
        Log.e("ORDER NUMBER", orderNumber);
        callerActivity = getIntent().getStringExtra(Utils.CALLER);
        inflater = getLayoutInflater();
        verifyStoragePermissions(this);
        verifyCalendarPermissions(this);
        new ListDetails().execute(orderNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.order_detail, menu);
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
            try {
                if (OrderActivity.class.toString().equals(callerActivity)) {
                    finish();
                    return true;
                } else if (SupplierOrderActivity.class.toString().equals(
                        callerActivity)) {
                    finish();
                    return true;
                }
            } catch (Exception e) {
            }


            Intent i = new Intent(this, CustomerDetailActivity.class);
            i.putExtra(Utils.CUST_REL_ID,
                    getIntent().getStringExtra(Utils.CUST_REL_ID));
            startActivity(i);
            finish();
        }
        if (id == R.id.action_add_payment) {
            addPayment(0);
        }
        if (id == R.id.action_add_installment) {
            viewInstallment();
        }
//		if (id == R.id.action_change_total) {
//			changeTotal();
//		}
        if (id == R.id.action_update_status) {
            updateOrderDetails();
        }
        if (id == R.id.action_update_deadline) {
            updateDeadline();
        }
        if (id == R.id.action_send_order_ready_en) {
            printCustomerInvoice(Utils.SEND_ORDER_READY, "en");
        }
        if (id == R.id.action_send_order_ready_fr) {
            printCustomerInvoice(Utils.SEND_ORDER_READY, "fr");
        }
        if (id == R.id.action_print_customer_order) {
            printCustomerOrder(true, "fr", 0);
        }
        if (id == R.id.action_send_customer_order_en) {
            printCustomerOrder(false, "en", 1);
        }
        if (id == R.id.action_send_customer_order_fr) {
            printCustomerOrder(false, "fr", 1);
        }
        if (id == R.id.action_send_order_closed_en) {
            printCustomerInvoice(Utils.SEND_ORDER_CLOSED, "en");
        }
        if (id == R.id.action_send_order_closed_fr) {
            printCustomerInvoice(Utils.SEND_ORDER_CLOSED, "fr");
        }
        if (id == R.id.action_print_customer_invoice_jewel_en) {
            printCustomerInvoice(Utils.SEND_INVOICE_JEWEL, "en");
        }
        if (id == R.id.action_print_customer_invoice_alliances_en) {
            printCustomerInvoice(Utils.SEND_INVOICE_ALLIANCES, "en");
        }
        if (id == R.id.action_print_customer_invoice_bague_en) {
            printCustomerInvoice(Utils.SEND_INVOICE_BAGUE, "en");
        }
        if (id == R.id.action_print_customer_invoice_jewel_fr) {
            printCustomerInvoice(Utils.SEND_INVOICE_JEWEL, "fr");
        }
        if (id == R.id.action_print_customer_invoice_alliances_fr) {
            printCustomerInvoice(Utils.SEND_INVOICE_ALLIANCES, "fr");
        }
        if (id == R.id.action_print_customer_invoice_bague_fr) {
            printCustomerInvoice(Utils.SEND_INVOICE_BAGUE, "fr");
        }
        if (id == R.id.action_discount) {
            addDiscount();
        }
        if (id == R.id.action_add_history) {
            updateAfterSending("");
        }
        if (id == R.id.action_send_customer_offer_en) {
            printCustomerOrder(false, "en", 2);
        }
        if (id == R.id.action_send_customer_offer_fr) {
            printCustomerOrder(false, "fr", 2);
        }
        if (id == R.id.action_send_customer_repair_en) {
            printCustomerOrder(false, "en", 3);
        }
        if (id == R.id.action_send_customer_repair_fr) {
            printCustomerOrder(false, "fr", 3);
        }
        if (id == R.id.action_send_review_en) {
            sendReview("en");
        }
        if (id == R.id.action_send_review_fr) {
            sendReview("fr");
        }
        return true;
    }

    public void sendReview(String lang) {
        Utils.sendReview(this, lang, email + "," + email2);
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

    public void hideLinesForPreviewOrder() {
        findViewById(R.id.payhist_header).setVisibility(View.GONE);
        findViewById(R.id.payhist_title).setVisibility(View.GONE);
        findViewById(R.id.payhist_line).setVisibility(View.GONE);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        showCompHist(position); // pass in position
        selectedCompPosition = position;
    }

    public void addPayment(double amt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.add_payment));
        final View view = inflater.inflate(R.layout.dialog_add_payment, null);
        ((EditTextDropdown) view.findViewById(R.id.mode)).setList(Utils
                .getSetSetting(Utils.PAYMENT_METHOD));
        ((TextView) view.findViewById(R.id.amount)).setText("" + amt);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.add),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mode = ((EditText) view.findViewById(R.id.mode))
                                .getText().toString();
                        String amount = ((EditText) view
                                .findViewById(R.id.amount)).getText()
                                .toString();
                        String remark = ((EditText) view
                                .findViewById(R.id.remark)).getText()
                                .toString();
                        // entryDate with current time:
                        Date entryDate = Calendar.getInstance().getTime();
                        DateFormat dateFormatWithDayTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                        String strEntryDate = dateFormatWithDayTime.format(entryDate);
                        // DatePicker for PaymentDate:
                        DatePicker dateP = view
                                .findViewById(R.id.date);
                        Calendar c = Calendar.getInstance();
                        c.set(dateP.getYear(), dateP.getMonth(),
                                dateP.getDayOfMonth());
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String strPaymentDate = dateFormat.format(c.getTime());
                        new AddPayment().execute(orderNumber,
                                strEntryDate, mode,
                                amount, remark, strPaymentDate);
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void updateOrderDetails() {
        String status = (String) ((Spinner) findViewById(R.id.order_status))
                .getSelectedItem();
        String remark = ((EditText) findViewById(R.id.order_remark)).getText()
                .toString();
        String seller = ((EditText) findViewById(R.id.seller)).getText()
                .toString();
        store = ((Spinner) findViewById(R.id.storeHandlingOrder)).getSelectedItem().toString();
        new UpdateOrderDetails().execute(orderNumber, status,
                remark, seller);
    }

    public void updateDeadline() {
        final Calendar c = Calendar.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.update_ord_dl));
        final DatePicker view = (DatePicker) inflater.inflate(
                R.layout.dialog_update_deadline, null);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.save),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        c.set(view.getYear(), view.getMonth(),
                                view.getDayOfMonth());
                        new UpdateOrderDeadline().execute(orderNumber,
                                Utils.shortDateForInsert(c.getTime()));
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showCompHist(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.comp_hist));
        final View view = inflater.inflate(R.layout.dialog_component_history,
                null);
        builder.setView(view);
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showComp.dismiss();
                    }
                });
        builder.setPositiveButton(getString(R.string.add_inst),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new getComponentQuantities().execute(orderNumber, "" + position, null);
                        //addOrEditCompHist(position, null);
                    }
                });
        new ListSupplierOrder(view).execute(position);
        pos = position;
        showComp = builder.create();
        showComp.show();
    }


    public Map<String, String> getInfos() {
        String query;
        Query q;
        boolean s;
        String id = componentList.get(pos)[27];

        if (!(id.equals(""))) {
            query = "select * from Inventory where CatalogCode = '" + id + "' and Status = 'Catalogue';";
            q = new Query(query);
            s = q.execute();
            if (!(q.getRes().size() > 0)) {
                Log.d("testtest", "no data");
            } else {
                res = q.getRes().get(0);
                Log.d("testtest", "" + res.get("ID"));
            }
        }
        return res;
/*        else{
            id=componentList.get(pos)[28];
            query ="select * from Inventory where ID = "+id+";";
        }*/
    }

    public void addOrEditCompHist(final int position, final String compHistID) {
        showComp.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //new getInventoryInfos().execute();

        if (compHistID != null) {
            builder.setTitle(getString(R.string.edit_inst));
        } else {
            builder.setTitle(getString(R.string.add_inst));
        }
        window = (ScrollView) inflater.inflate(
                R.layout.dialog_add_supplier_order, null);
        final LinearLayout view = window
                .findViewById(R.id.main_dialog);
        String[] thisCompHist = null;

        for (int i = 0; i < this.componentList.get(pos).length; i++) {
            Log.d("componentlist", "" + i + " " + componentList.get(pos)[i]);
        }
        Map<String, String> data = getInfos();
        TextView code = view.findViewById(R.id.suppCode);
        if (!(data == null)) {
            code.setText(data.get("Code"));
            TextView catCode = view.findViewById(R.id.catalogCode);
            catCode.setText(data.get("CatalogCode"));
            TextView width = view.findViewById(R.id.width);
            width.setText(data.get("Width"));
            TextView height = view.findViewById(R.id.height);
            height.setText(data.get("Height"));
            TextView stone = view.findViewById(R.id.stone);
            stone.setText(data.get("Stone"));
            TextView stonQty = view.findViewById(R.id.stoneQuantity);
            stonQty.setText(data.get("StoneQuantity"));
            TextView stoneType = view.findViewById(R.id.stoneType);
            stoneType.setText(data.get("StoneType"));
            TextView stoneSize = view.findViewById(R.id.stoneSize);
            stoneSize.setText(data.get("StoneSize"));
            TextView setting = view.findViewById(R.id.setting);
            setting.setText(data.get("Setting"));
            TextView remark = view.findViewById(R.id.remark);
            remark.setText(data.get("Remark"));
            //TextView name = (TextView) view.findViewById(R.id.ringName);
        } else {
            code.setText("N/A");
            TextView catCode = view.findViewById(R.id.catalogCode);
            catCode.setText("N/A");
            TextView width = view.findViewById(R.id.width);
            width.setText("N/A");
            TextView height = view.findViewById(R.id.height);
            height.setText("N/A");
            TextView stone = view.findViewById(R.id.stone);
            stone.setText("N/A");
            TextView stonQty = view.findViewById(R.id.stoneQuantity);
            stonQty.setText("N/A");
            TextView stoneType = view.findViewById(R.id.stoneType);
            stoneType.setText("N/A");
            TextView stoneSize = view.findViewById(R.id.stoneSize);
            stoneSize.setText("N/A");
            TextView setting = view.findViewById(R.id.setting);
            setting.setText("N/A");
            TextView remark = view.findViewById(R.id.remark);
            remark.setText("N/A");
        }

        // displaying other steps
        ListAdapter compHistListAdapter = ((ListView) showComp
                .findViewById(R.id.comp_hist_list)).getAdapter();
        for (int i = 0; i < compHistListAdapter.getCount(); i++) {
            String[] compHist = (String[]) compHistListAdapter.getItem(i);
            if (compHistID != null && compHistID.equals(compHist[0])) {
                thisCompHist = compHist;
            }

        }
        // step dropdown
        final Spinner step = view.findViewById(R.id.comp_step);
        step.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting(Utils.ORDER_STEP)));
        if (thisCompHist != null) {
            step.setSelection(((ArrayAdapter) step.getAdapter())
                    .getPosition(thisCompHist[6]));
        }
        // status dropdown
        final Spinner status = view.findViewById(R.id.comp_status);
        status.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting(Utils.SUPP_ORD_STT)));
        if (thisCompHist != null) {
            status.setSelection(((ArrayAdapter) status.getAdapter())
                    .getPosition(thisCompHist[3]));
        }

        // recipient dropdown
        Spinner reci = view.findViewById(R.id.comp_recipient);
        reci.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting(Utils.SUPPLIER)));
        if (thisCompHist != null) {
            reci.setSelection(((ArrayAdapter) reci.getAdapter())
                    .getPosition(thisCompHist[2]));
        }

        // deadline chooser
        final EditText deadline = view
                .findViewById(R.id.comp_deadline);
        final Calendar myCalendar = Calendar.getInstance();
        // if adding instruction, put default deadline to 7 days before order deadline
        // if order deadline is less than 2 week from today, put default
        // deadline to 1 day before order deadline
        Date orderDeadlineDate = Utils.dateFromStringddMMyyyy(orderDeadline);
        Date todayDate = Calendar.getInstance().getTime();
        long diffDays = (orderDeadlineDate.getTime() - todayDate.getTime())
                / (24 * 60 * 60 * 1000);
        if (diffDays >= 14)
            myCalendar.setTimeInMillis(orderDeadlineDate.getTime() - 7
                    * (24 * 60 * 60 * 1000));
        else
            myCalendar.setTimeInMillis(orderDeadlineDate.getTime() - (24 * 60 * 60 * 1000));
        if (thisCompHist != null) {
            deadline.setText(Utils.shortDateFromDB(thisCompHist[1]));
        } else {
            deadline.setText(Utils.shortDateForDisplay(myCalendar.getTime()));
        }
        final OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                deadline.setText(Utils.shortDateForDisplay(myCalendar.getTime()));
            }
        };

        deadline.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(OrderDetailActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // display comp info
        //ici thiscomplist[14] = suppordno


        final LinearLayout leftCheckboxesView = view
                .findViewById(R.id.left_checkboxes);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        for (int i = 0; i < stockQuantities.size(); i++) {
            CheckBox newCheckBox = new CheckBox(OrderDetailActivity.this);
            newCheckBox.setLayoutParams(layoutParams);
            newCheckBox.setText(stockQuantities.get(i).get("ShortCode") + " / Weight : " + stockQuantities.get(i).get("Weight") +
                    " / Quantity : " + stockQuantities.get(i).get("Quantity"));
            leftCheckboxesView.addView(newCheckBox);
            leftCheckboxesView.invalidate();
        }
        for (int i = 1; i < leftCheckboxesView.getChildCount(); i++) {
            if (leftCheckboxesView.getChildAt(i) instanceof CheckBox) {
                ((CheckBox) leftCheckboxesView.getChildAt(i)).setText(componentList
                        .get(position)[i + 2]);
                if (thisCompHist != null) {
                    ((CheckBox) leftCheckboxesView.getChildAt(i))
                            .setChecked(thisCompHist[7].charAt(i - 1) == '1');
                }
            }
        }
        ((CheckBox) leftCheckboxesView.findViewById(R.id.comp_remark))
                .setText(componentList.get(position)[17]);


        if (thisCompHist != null) {
            ((TextView) view.findViewById(R.id.comp_add_remark))
                    .setText(thisCompHist[8]);

            ((CheckBox) leftCheckboxesView.findViewById(R.id.comp_remark))
                    .setChecked(thisCompHist[7].charAt(11) == '1');
        }


        builder.setView(window);
        builder.setCancelable(false);
        final String saveOrAdd;
        if (compHistID != null) {
            saveOrAdd = getString(R.string.save);
        } else {
            saveOrAdd = getString(R.string.add);
        }

        view.findViewById(R.id.outButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchProducts().execute();
            }
        });

        view.findViewById(R.id.addPictures).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                builder.setTitle("Add links");
                View dialogView = inflater.inflate(R.layout.dialog_add_drive_links, null);
                final EditText link = dialogView.findViewById(R.id.link);
                Button add = dialogView.findViewById(R.id.add);
                driveLinks = new ArrayList<>();
                add.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        driveLinks.add(link.getText().toString());
                        Toast.makeText(OrderDetailActivity.this, "Added link", Toast.LENGTH_SHORT).show();
                        link.setText("");
                    }
                });
                builder.setView(dialogView);
                builder.create().show();
            }
        });

        builder.setPositiveButton(saveOrAdd,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar c = Calendar.getInstance();
                        String date = Utils.shortDateForInsert(c.getTime());
                        String deadLine = Utils.shortDateForInsert(deadline
                                .getText().toString());
                        String addRemark = ((TextView) view
                                .findViewById(R.id.comp_add_remark)).getText()
                                .toString();
                        StringBuilder checkboxes = new StringBuilder();
                        StringBuilder ins = new StringBuilder();
                        ins.append(componentList.get(position)[2]);
                        ins.append(", ");
                        for (int i = 0; i < leftCheckboxesView.getChildCount(); i++) {
                            if (leftCheckboxesView.getChildAt(i) instanceof CheckBox) {
                                if (((CheckBox) leftCheckboxesView.getChildAt(i)).isChecked()) {
                                    checkboxes.append("1");
                                    ins.append(((CheckBox) leftCheckboxesView
                                            .getChildAt(i)).getText().toString()).append(", ");
                                } else {
                                    checkboxes.append("0");
                                }
                            }
                        }

                        String rep = ((Spinner) view
                                .findViewById(R.id.comp_recipient))
                                .getSelectedItem().toString();
                        new AddOrEditSupplierOrder().execute(date, // created date (0)
                                ins.toString(), // instruction (1)
                                deadLine, // deadline (2)
                                rep, // recipient (3)
                                status.getSelectedItem().toString(),// status (4)
                                step.getSelectedItem().toString(), // step (5)
                                "" + position, // position of component (6)
                                checkboxes.toString(), addRemark, compHistID  // checkboxes (7,8,9)
                        );
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addComp.dismiss();
                        showComp.show();
                    }
                });
        addComp = builder.create();
        addComp.show();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public void updateAfterSending(String remark) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.update_ord_stt));
        LinearLayout view = (LinearLayout) inflater.inflate(
                R.layout.dialog_update_status, null);
        final Spinner status = view.findViewById(R.id.status);
        status.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting(Utils.ORD_STT)));
        status.setSelection(Utils.getSelectedIndex(orderStatus,
                Utils.getSetSetting(Utils.ORD_STT)));
        final EditText remarkField = view.findViewById(R.id.remark);
        if (!"".equals(remark))
            remarkField.setText(remark);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.save),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new UpdateOrderAfterSending().execute(orderNumber, remarkField.getText().toString(),
                                status.getSelectedItem().toString());
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        reminder = builder.create();
        reminder.show();
    }

    // here we name the request code component id cos we used comp id as request
    // code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.SEND_ORDER_CONF_CODE) {
            updateAfterSending(getString(R.string.sent_conf));
            return;
        }
        if (requestCode == Utils.SEND_ORDER_READY_CODE) {
            updateAfterSending(getString(R.string.sent_ready));
            return;
        }
        if (requestCode == Utils.SEND_SUPPLIER_CODE) {
            updateAfterSendingSupplier(getString(R.string.sent_supplier));
            return;
        }
        if (requestCode == Utils.PRINT_INVOICE_CODE) {
            updateAfterSending(getString(R.string.invoice_printed));
            return;
        }
        if (requestCode == Utils.SEND_ORDER_CLOSE_CODE) {
            updateAfterSending(getString(R.string.sent_ready));
            return;
        }
        if (requestCode == Utils.SEND_OFFER_CONF_CODE) {
            updateAfterSending("Offer confirmation email sent.");
        }
        // if request code is non of the above, it means to save pic to a
        // componentID.
        // here requestCode is the componentID
        try {
            Uri uri = Uri.fromFile(new File(currentPhotoPath));
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            if (imageBitmap != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                Toast.makeText(OrderDetailActivity.this,
                        "uploading picture please wait...",
                        Toast.LENGTH_LONG).show();
                new SaveImageTask().execute("" + requestCode, encodeImage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + orderNumber + "_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void dispatchTakePictureIntent(int id) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        "com.aguadeoro.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, id);
            }
        }
    }

    public void printCustomerOrder(boolean toPrint, String lang, int pressed) {
        String[] orderInfo = new String[13];
        orderInfo[0] = customerName.substring(customerName.indexOf(']') + 1);
        orderInfo[1] = (customer2Name == null) ? "" : customer2Name;
        orderInfo[2] = address;
        orderInfo[3] = email + "," + email2;
        orderInfo[4] = tel;
        orderInfo[5] = Utils.shortDateFromDB(weddingDate);
        if (weddingDate2.length() > 0)
            orderInfo[5] += ", " + Utils.shortDateFromDB(weddingDate2);
        orderInfo[6] = orderDate;
        orderInfo[7] = orderDeadline;
        orderInfo[8] = orderTotal;
        orderInfo[9] = orderDiscount;
        orderInfo[10] = orderPaid;
        orderInfo[11] = orderRemain;
        orderInfo[12] = orderNumber;
        Utils.printCustomerOrder(this, orderType, orderInfo, componentList,
                toPrint, lang, pressed);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void verifyCalendarPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_CALENDAR,
                    REQUEST_CALENDAR
            );
        }
    }

    public void printCustomerInvoice(String action, String lang) {
        String[] orderInfo = new String[14];
        orderInfo[0] = customerName.substring(customerName.indexOf(']') + 1);
        orderInfo[1] = (customer2Name == null) ? "" : customer2Name;
        orderInfo[2] = address;
        orderInfo[3] = email + "," + email2;
        orderInfo[4] = tel;
        orderInfo[5] = Utils.shortDateFromDB(weddingDate);
        if (weddingDate2.length() > 0)
            orderInfo[5] += ", " + Utils.shortDateFromDB(weddingDate2);
        orderInfo[6] = Utils.shortDateForDisplay(Calendar.getInstance()
                .getTime());
        orderInfo[7] = orderDate;
        orderInfo[8] = orderTotal;
        orderInfo[9] = orderDiscount;
        orderInfo[10] = orderPaid;
        orderInfo[11] = orderRemain;
        orderInfo[12] = orderNumber;
        orderInfo[13] = TVA;

        Utils.printCustomerInvoice(orderType, orderInfo, componentList,
                paymentList, this, action, lang);
    }

    public void printSupplierOrder(ArrayList<String[]> instDetails,
                                   ArrayList<Integer> componentPositions, boolean toView, String name, ArrayList<String> links) {
        if (instDetails.size() == 0) {
            return;
        }
        String[] orderInfo = new String[8];
        orderInfo[0] = instDetails.get(0)[9]; // order number
        orderInfo[1] = instDetails.get(0)[2]; // supplier
        orderInfo[2] = Utils.getSetSetting(instDetails.get(0)[2])[0]; // email
        orderInfo[3] = Utils.getSetSetting(instDetails.get(0)[2])[2]; // address
        orderInfo[4] = Utils.getSetSetting(instDetails.get(0)[2])[3]; // tel
        orderInfo[5] = Utils.shortDateFromDB(instDetails.get(0)[5]); // createddate
        orderInfo[6] = Utils.shortDateFromDB(instDetails.get(0)[1]); // deadline
        orderInfo[7] = Utils.getSetSetting(instDetails.get(0)[2])[1]; // responsable

        Map<String, String> orderInfoMap = new HashMap<>();
        orderInfoMap.put("orderNumber", instDetails.get(0)[9]);
        orderInfoMap.put("supplier", instDetails.get(0)[2]);
        orderInfoMap.put("email", Utils.getSetSetting(instDetails.get(0)[2])[0]);
        orderInfoMap.put("address", Utils.getSetSetting(instDetails.get(0)[2])[2]);
        orderInfoMap.put("tel", Utils.getSetSetting(instDetails.get(0)[2])[3]);
        orderInfoMap.put("createdDate", Utils.shortDateFromDB(instDetails.get(0)[5]));
        orderInfoMap.put("deadline", Utils.shortDateFromDB(instDetails.get(0)[1]));
        orderInfoMap.put("responsable", Utils.getSetSetting(instDetails.get(0)[2])[1]);


        suppReminderSuppName = orderInfo[1];
        suppReminderDeadline = orderInfo[6];

        ArrayList<String[]> compInstList = new ArrayList<>();
        for (int i = 0; i < instDetails.size(); i++) {
            String inst = instDetails.get(i)[7];
            String[] compInst = new String[15];
            String[] component = componentList.get(componentPositions.get(i));




            compInst[0] = "";
            if (inst.charAt(0) == '1') {
                compInst[0] = component[2];
            }
            if (inst.charAt(1) == '1') {
                compInst[0] += " " + component[3];
            }
            if (inst.charAt(2) == '1') {
                compInst[0] += " " + component[4];
            }

            for (int j = 0; j < 8; j++) {
                if (inst.charAt(j + 1) == '1') {
                    compInst[j] = component[j + 4];
                } else {
                    compInst[j] = Utils.NON;
                }
            }

            // processing engraving detail inst.charAt(8)
            if (inst.charAt(10) == '1') {
                compInst[8] = component[12];
                compInst[9] = component[13];
                compInst[10] = component[14];
            } else {
                compInst[8] = Utils.NON;
                compInst[9] = Utils.NON;
                compInst[10] = Utils.NON;
            }
            // processing remark inst.charAt(9)
            if (inst.charAt(11) == '1') {
                compInst[11] = component[17];
            } else {
                compInst[11] = Utils.NON;
            }
            // additional remark
            compInst[12] = instDetails.get(i)[8];
            compInst[13] = component[0];
            // processing stone detail inst.charAt(12)
            String stoneDetail = inst.substring(12);
            if (!stoneDetail.contains("1")) {
                compInst[14] = Utils.NON;
            } else {
                compInst[14] = getString(R.string.stone_detail) + " - ";
                if (stoneDetail.charAt(0) == '1')
                    compInst[14] += getString(R.string.stone_dia) + ":"
                            + component[18] + "; ";
                if (stoneDetail.charAt(1) == '1')
                    compInst[14] += getString(R.string.stone_color) + ":"
                            + component[19] + "; ";
                if (stoneDetail.charAt(2) == '1')
                    compInst[14] += getString(R.string.stone_clarity) + ":"
                            + component[20] + "; ";
                if (stoneDetail.charAt(3) == '1')
                    compInst[14] += getString(R.string.stone_amount) + ":"
                            + component[21] + "; ";
                if (stoneDetail.charAt(4) == '1')
                    compInst[14] += getString(R.string.stone_setting) + ":"
                            + component[22] + " ;";
                if (stoneDetail.charAt(5) == '1')
                    compInst[14] += getString(R.string.stone_carat) + ":"
                            + component[23] + "; ";
                if (stoneDetail.charAt(6) == '1')
                    compInst[14] += getString(R.string.stone_remark) + ":"
                            + component[24] + ";";
            }

            compInstList.add(compInst);
        }
        Utils.printSupplierOrder(this, orderType, orderInfoMap, compInstList, toView, this.name, links);
    }

    public void showAllOrderHist(View v) {
        Log.e("SHOW ALL ORDER HIST", "");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.comp_hist));
        final View view = inflater.inflate(
                R.layout.dialog_all_component_history, null);

        Spinner reci = view.findViewById(R.id.comp_recipient);
        reci.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting(Utils.SUPPLIER)));
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.send),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView list = view
                                .findViewById(R.id.comp_hist_list);
                        suppOrderListSelectedToSendToSupplier = new ArrayList<String[]>();
                        ArrayList<Integer> compPosts = new ArrayList<Integer>();
                        ListAdapter adapter = list.getAdapter();
                        for (int i = 0; i < list.getChildCount(); i++) {
                                String[] array = (String[]) adapter.getItem(i);
                            Log.e("INST", Arrays.toString(array));
                            suppOrderListSelectedToSendToSupplier.add(array);
                            if (((CheckBox) list.getChildAt(i).findViewById(R.id.selected)).isChecked()) {
                                int pos = 0;
                                Log.e("COMPONENT LIST", Arrays.toString(componentList.get(0)));
                                for (int j = 1; j < componentList.size(); j++) {
                                    if (((String[]) adapter.getItem(i))[10]
                                            .equals(componentList.get(j)[0])) {
                                        pos = j;
                                        break;
                                    }
                                }
                                compPosts.add(pos);
                            }
                        }
                        Query q = new Query("select * from SupplierOrderPics where OrderComponentID = " + compID);
                        q.execute();
                        driveLinks = new ArrayList<>();
                        for (Map<String, String> res : q.getRes()) {
                            driveLinks.add(res.get("Link"));
                        }

                        printSupplierOrder(
                                suppOrderListSelectedToSendToSupplier,
                                compPosts, false, name, driveLinks);
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        new ListSupplierOrder(view).execute(-1);
        showComp = builder.create();
        showComp.show();
    }

    public void sendOrderReadyNotice(String lang) {
        String subject = Utils.getStringSetting("email_order_ready_subject_"
                + lang);
        String body = Utils
                .getStringSetting("email_order_ready_body_" + lang)
                .replace("[1]",
                        customerName.substring(1 + customerName.indexOf("]")))
                .replace("[2]", customer2Name == null ? "" : customer2Name);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        emailIntent
                .putExtra(Intent.EXTRA_EMAIL, new String[]{email, email2});

        startActivityForResult(emailIntent, Utils.SEND_ORDER_READY_CODE);

    }

    public void addDiscount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.textview_discnt));
        final View view = inflater.inflate(R.layout.dialog_add_discount, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.add),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String amount = ((EditText) view
                                .findViewById(R.id.amount)).getText()
                                .toString();
                        String remark = ((EditText) view
                                .findViewById(R.id.remark)).getText()
                                .toString();
                        Calendar c = Calendar.getInstance();
                        new AddDiscount().execute(orderNumber,
                                Utils.shortDateForInsert(c.getTime()), amount,
                                remark);
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void updateAfterSendingSupplier(String remark) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.update_ord_stt));
        LinearLayout view = (LinearLayout) inflater.inflate(
                R.layout.dialog_update_comp_hist_status, null);

        final Spinner status = view.findViewById(R.id.status);
        status.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting(Utils.ORD_STT)));
        status.setSelection(Utils.getSelectedIndex(orderStatus,
                Utils.getSetSetting(Utils.ORD_STT)));

        final EditText remarkField = view.findViewById(R.id.remark);
        if (!"".equals(remark))
            remarkField.setText(remark + " " + suppReminderSuppName + " "
                    + suppReminderDeadline);

        final ListView compHistStatusList = view
                .findViewById(R.id.comp_hist_list);
        compHistStatusList
                .setAdapter(new ComponentHistoryStatusUpdateListAdapter(this,
                        suppOrderListSelectedToSendToSupplier));
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.save),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // collect each compHist status
                        String[] inputs = new String[3 + 2 * compHistStatusList
                                .getChildCount()];
                        inputs[0] = orderNumber;
                        inputs[1] = remarkField.getText().toString();
                        inputs[2] = status.getSelectedItem().toString();
                        for (int i = 0; i < compHistStatusList.getChildCount(); i++) {
                            Spinner child = compHistStatusList
                                    .getChildAt(i).findViewById(
                                            R.id.comp_status);
                            inputs[3 + 2 * i] = child.getTag().toString(); // compHistID is stored in the tag
                            inputs[3 + 2 * i + 1] = child.getSelectedItem().toString(); // status
                        }
                        new UpdateOrderAfterSendingSupplier().execute(inputs);
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        reminder = builder.create();
        reminder.show();
    }

    public void addInstallment() {
        Calendar cal = Calendar.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_payment));
        installmentView = (LinearLayout) inflater.inflate(
                R.layout.dialog_add_installment, null);
        ((TextView) installmentView.findViewById(R.id.total)).setText("CHF "
                + orderRemain);
        ((TextView) installmentView.findViewById(R.id.start_date))
                .setText(Utils.shortDateForDisplay(cal.getTime()));
        builder.setView(installmentView);
        builder.setPositiveButton(getString(R.string.save),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveInstallment();
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void applyInstallment(View view) {
        String start = ((TextView) installmentView
                .findViewById(R.id.start_date)).getText().toString();
        Date startDate = Utils.dateFromStringddMMyyyy(start);
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        int paymentNum = 1;
        if (!((EditText) installmentView.findViewById(R.id.install_num))
                .getText().toString().isEmpty()) {
            paymentNum = Integer.parseInt(((EditText) installmentView
                    .findViewById(R.id.install_num)).getText().toString());
        }
        double amount = Double.parseDouble(orderRemain) / paymentNum;
        LinearLayout install_lines = installmentView
                .findViewById(R.id.install_lines);
        install_lines.removeAllViews();
        for (int i = 0; i < paymentNum; i++) {
            if (i > 0)
                cal.add(Calendar.MONTH, 1);
            LinearLayout line = (LinearLayout) inflater.inflate(
                    R.layout.line_installment, null);
            ((EditText) line.findViewById(R.id.deadline)).setText(Utils
                    .shortDateForDisplay(cal.getTime()));
            ((EditText) line.findViewById(R.id.amount)).setText("" + amount);
            ((EditText) line.findViewById(R.id.amount))
                    .addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start,
                                                  int before, int count) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s,
                                                      int start, int count, int after) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            double total = 0;
                            LinearLayout lines = installmentView
                                    .findViewById(R.id.install_lines);
                            for (int i = 0; i < lines.getChildCount(); i++) {
                                String amount = ((TextView) lines.getChildAt(i)
                                        .findViewById(R.id.amount)).getText()
                                        .toString();
                                try {
                                    total += Double.parseDouble(amount);
                                } catch (Exception e) {
                                }
                            }
                            ((TextView) installmentView
                                    .findViewById(R.id.actual_total))
                                    .setText("" + (-total + Double.parseDouble(orderRemain)));
                        }
                    });
            install_lines.addView(line);
        }
    }

    public void saveInstallment() {
        installs = new ArrayList<String[]>();
        LinearLayout lines = installmentView
                .findViewById(R.id.install_lines);
        for (int i = 0; i < lines.getChildCount(); i++) {
            String date = ((TextView) lines.getChildAt(i).findViewById(
                    R.id.deadline)).getText().toString();
            String amount = ((TextView) lines.getChildAt(i).findViewById(
                    R.id.amount)).getText().toString();
            installs.add(new String[]{date, amount});
        }
        new SaveInstall().execute();
    }

    public void viewInstallment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_payment));
        installmentList = (LinearLayout) inflater.inflate(
                R.layout.dialog_view_installment, null);
        builder.setView(installmentList);
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.setPositiveButton(getString(R.string.save),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateInstallment();
                        dialog.dismiss();
                    }
                });
        new GetInstall().execute();
        viewInstDialog = builder.create();
        viewInstDialog.show();
    }

    public void updateInstallment() {
        updateInstallList = new ArrayList<String[]>();
        ListView list = installmentList
                .findViewById(R.id.install_list);
        for (int i = 0; i < list.getChildCount(); i++) {
            CheckBox paid = list.getChildAt(i).findViewById(
                    R.id.paid);
            if (!paid.isEnabled() || !paid.isChecked())
                continue;
            String id = paid.getTag().toString();
            String amount = ((TextView) list.findViewById(R.id.amount))
                    .getText().toString();
            updateInstallList.add(new String[]{id, amount});
        }
        if (!updateInstallList.isEmpty())
            new UpdateInstall().execute();

    }

    public void viewInventoryDetail(View view) {
        Intent intent = new Intent(this, ViewInventoryActivity.class);
        intent.putExtra(Utils.ID, view.getTag().toString());
        startActivity(intent);

    }

    public void viewInventoryDetailStock(View v) {
        Intent intent = new Intent(this, ViewInventoryActivity.class);
        String query = "select ID from Inventory where CatalogCode = " + Utils.escape(v.getTag().toString()) + " and Status = 'Stock'";
        Query q = new Query(query);
        q.execute();
        if (q.getRes().size() > 0) {
            intent.putExtra(Utils.ID, q.getRes().get(0).get("ID"));
            startActivity(intent);
        } else {
            Toast.makeText(OrderDetailActivity.this, "error, no item in stock with that InventoryCode", Toast.LENGTH_LONG).show();
        }
    }

    public void dislayCustDetail(View v) {
        Intent intent = new Intent(this, CustomerDetailActivity.class);
        intent.putExtra(Utils.CUST_NO, mainCustomerNo);
        startActivity(intent);

    }

    public void searchInfo(View v) {
    }

    public void changeTotal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Ajuster Total");
        final View view = inflater.inflate(R.layout.dialog_ajuster_total, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.add),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String amount = ((EditText) view
                                .findViewById(R.id.amount)).getText()
                                .toString();
                        String remark = ((EditText) view
                                .findViewById(R.id.remark)).getText()
                                .toString();
                        DatePicker dateP = view
                                .findViewById(R.id.date);
                        Calendar c = Calendar.getInstance();
                        c.set(dateP.getYear(), dateP.getMonth(),
                                dateP.getDayOfMonth());
                        new AdjustTotal().execute(orderNumber,
                                Utils.shortDateForInsert(c.getTime()),
                                amount, remark);
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showCompActivityDetail(int compPosition) {
        String compID = componentList.get(compPosition)[0];
        Intent intent = new Intent(this, ComponentActivityDetailActivity.class);
        intent.putExtra(Utils.COMP_ID, compID);
        //Loc Request 1 2018.04.17 BEGIN
        intent.putExtra(Utils.COMP_INFO, componentList.get(compPosition));
        //Loc Request 1 2018.04.17 END
        startActivity(intent);
    }

    class ListDetails extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            // get main order info
            String query = "select o.*, c.* from MainOrder o, Customer c where "
                    + "o.CustomerNumber = c.CustomerNumber "
                    + "and o.OrderNumber = " + args[0];
            Query q = new Query(query);
            boolean success = q.execute();
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            seller = result.get(0).get(Utils.SELLER);
            orderStatus = result.get(0).get(Utils.ORD_STT);
            oldOrderStatus = orderStatus;
            orderDate = Utils.shortDateFromDB(result.get(0).get(Utils.ORD_DT));
            orderDeadline = Utils.shortDateFromDB(result.get(0).get(
                    Utils.DEADLINE));
            orderTotal = result.get(0).get(Utils.TOTAL);
            orderDiscount = result.get(0).get(Utils.DISCOUNT);
            TVA = result.get(0).get("TVA");
            orderPaid = result.get(0).get(Utils.PAID);
            orderRemain = result.get(0).get(Utils.REMAIN);
            remark = result.get(0).get(Utils.REMARK);
            orderType = result.get(0).get(Utils.ORDER_TYPE);
            mainCustomerNo = result.get(0).get(Utils.CUST_NO);
            customerName = "[" + result.get(0).get(Utils.CUST_NO) + "]"
                    + result.get(0).get(Utils.CUST_NAME);
            address = result.get(0).get(Utils.ADDR);
            email = result.get(0).get(Utils.EMAIL);
            tel = result.get(0).get(Utils.TEL);
            weddingDate = result.get(0).get(Utils.WEDDING_DATE);
            weddingDate2 = result.get(0).get(Utils.WEDDING_DATE2);
            store = result.get(0).get("StoreMainOrder");
            oldStore = store;
            // get customer 2
            query = "select c.* from Customer c, CustomerRelationship r where "
                    + "c.CustomerNumber = r.Customer2 " + "and r.Customer1 = "
                    + result.get(0).get(Utils.CUST_NO);
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            if (q.getRes().size() > 0) {
                customer2Name = q.getRes().get(0).get(Utils.CUST_NAME);
                address2 = q.getRes().get(0).get(Utils.ADDR);
                email2 = q.getRes().get(0).get(Utils.EMAIL);
                tel2 = q.getRes().get(0).get(Utils.TEL);
            }

            // get component info
            query = "select * from OrderComponent where OrderNumber = "
                    + args[0];
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            result = q.getRes();
            if (result.size() > 0) {
                name = result.get(0).get("ID");
                compID = name;
            }

            String[] names = new String[result.size()];
            for (int ii = 0; ii < result.size(); ii++) {
                if (!Objects.requireNonNull(result.get(ii).get("" + 28)).equals("")) {
                    Log.d("querytest", "" + result.get(ii).get("" + 28));
                    query = "select Name from Inventory where ID =" + result.get(ii).get("" + 28) + ";";
                    q = new Query(query);
                    success = q.execute();
                    if (!success) {
                        return false;
                    }
                    if (q.getRes().size() > 0) {
                        names[ii] = q.getRes().get(0).get("Name");
                        Log.d("name", "" + q.getRes().get(0).get("Name"));
                    } else {
                        names[ii] = "";
                    }
                }
            }

            Log.e("result size", "" + result.size());
            Log.e("result mec", result.toString());
            //order components
            for (int i = 0; i < result.size(); i++) {
                String[] orderComp = new String[30];
                for (int j = 0; j < 29; j++) {
                    orderComp[j] = result.get(i).get("" + j);
                }
                if (names[i] == null || names[i].equals("")) {
                    orderComp[29] = remark;
                } else {
                    orderComp[29] = names[i];
                }
                componentList.add(orderComp);
            }

            // get payment info
            query = "select * from OrderHistory where OrderNumber = " + args[0]
                    + " order by EntryDate desc";
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            result = q.getRes();
            for (int i = 0; i < result.size(); i++) {
                String[] pay = new String[4];
                pay[0] = result.get(i).get(Utils.ENRY_DT);
                pay[1] = result.get(i).get(Utils.REMARK);
                pay[2] = result.get(i).get(Utils.AMOUNT);
                pay[3] = result.get(i).get(Utils.PAY_MODE);
                paymentList.add(pay);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                setListAdapter(new OrderComponentListAdapter(
                        OrderDetailActivity.this, componentList, orderType, false));
                ListView paylist = findViewById(R.id.pay_list);
                paylist.setAdapter(new OrderHistoryListAdapter(
                        OrderDetailActivity.this, paymentList));
                ((TextView) findViewById(R.id.order_number))
                        .setText(orderNumber);
                ((Spinner) findViewById(R.id.order_status))
                        .setAdapter(new ArrayAdapter<String>(
                                OrderDetailActivity.this,
                                android.R.layout.simple_list_item_1, Utils
                                .getSetSetting(Utils.ORD_STT)));
                String[] statuses = Utils.getSetSetting(Utils.ORD_STT);
                for (int i = 0; i < statuses.length; i++) {
                    if (orderStatus.equals(statuses[i])) {
                        ((Spinner) findViewById(R.id.order_status))
                                .setSelection(i);
                    }
                }
                ((EditText) findViewById(R.id.order_date)).setText(orderDate);
                ((EditText) findViewById(R.id.order_deadline))
                        .setText(orderDeadline);
                ((EditText) findViewById(R.id.order_total)).setText(orderTotal);
                ((EditText) findViewById(R.id.order_discount))
                        .setText(orderDiscount);
                if (orderDiscount.startsWith("0")) {
                    findViewById(R.id.order_discount)
                            .setVisibility(View.GONE);
                    findViewById(R.id.order_discount_txt)
                            .setVisibility(View.GONE);
                }
                ((TextView) findViewById(R.id.order_paid)).setText(orderPaid);
                ((TextView) findViewById(R.id.order_remaining))
                        .setText(orderRemain);
                ((TextView) findViewById(R.id.order_remark)).setText(remark);
                ((TextView) findViewById(R.id.customer)).setText(customerName);
                ((TextView) findViewById(R.id.order_type)).setText(orderType);
                ((TextView) findViewById(R.id.seller)).setText(seller);
                Spinner storeSpinner = findViewById(R.id.storeHandlingOrder);
                storeSpinner.setAdapter(new ArrayAdapter<String>(
                        OrderDetailActivity.this,
                        android.R.layout.simple_list_item_1, Utils
                        .getSetSetting("Stores")));
                String[] stores = Utils.getSetSetting("Stores");
                for (int i = 0; i < stores.length; i++) {
                    if (stores[i].equals(store)) {
                        storeSpinner.setSelection(i);
                        break;
                    }
                }
                if (Utils.ORD_PREVIEW.equals(orderType)) {
                    // hideLinesForPreviewOrder();
                }
                if (getIntent().getStringExtra(Utils.COMP_ID) != null) {
                    String compID = getIntent().getStringExtra(Utils.COMP_ID);
                    for (int i = 0; i < componentList.size(); i++) {
                        if (compID.equals(componentList.get(i)[0])) {
                            showCompHist(i);
                        }
                    }
                }

                getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        showCompActivityDetail(position); // pass in position
                        selectedCompPosition = position;
                        Log.d("test", Arrays.toString(componentList.get(position)));
                        return true;
                    }
                });

            } else {
                Toast.makeText(OrderDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }

    class AddPayment extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            double paid = Double.parseDouble(orderPaid), remain = Double
                    .parseDouble(orderRemain), paying = Double
                    .parseDouble(args[3]);

            String remark = "Payment: " + paying + " by " + args[2] + " on " + args[5] + ". ";
            String query = "insert into OrderHistory(OrderNumber, EntryDate, PaymentMode,"
                    + " Amount, Remark, PaymentDate)" + "values(" + args[0] // order number
                    + ", " + Utils.escape(args[1])  // entry date
                    + ", " + Utils.escape(args[2]) // pay mode
                    + ", " + args[3] // amount
                    + ", " + Utils.escape(remark + args[4])
                    + ", " + Utils.escape(args[5]) + ")"; // payment date
            Query q = new Query(query);
            boolean success = q.execute();
            if (!success) {
                return false;
            }
            query = "update MainOrder set Paid = " + (paid + paying)
                    + ", Remain = " + (remain - paying)
                    + " where OrderNumber = " + args[0];
            q = new Query(query);
            return q.execute();
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                OrderDetailActivity.this.recreate();
            } else {
                Toast.makeText(OrderDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class UpdateOrderDetails extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String query = "update MainOrder set OrderStatus = "
                    + Utils.escape(args[1]) + ", Remark = "
                    + Utils.escape(args[2]) + ", Seller = "
                    + Utils.escape(args[3]) + ", StoreMainOrder = " + Utils.escape(store) + " where OrderNumber = " + args[0];
            Query q = new Query(query);
            boolean s = q.execute();
            if (!oldOrderStatus.equals(args[1])) {
                query = "insert into OrderHistory(OrderNumber, EntryDate, Remark)"
                        + " values(" + args[0] + ", Now(), " + "'Changed status from " + orderStatus + " to " + args[1] + "')";
                q = new Query(query);
                if (!q.execute()) {
                    return false;
                }
            }

            if (!oldStore.equals(store)) {
                query = "insert into OrderHistory(OrderNumber, EntryDate, Remark)"
                        + " values(" + args[0] + ", Now(), " + "'Changed store from " + oldStore + " to " + store + "')";
                q = new Query(query);
                return q.execute();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                OrderDetailActivity.this.recreate();
            } else {
                Toast.makeText(OrderDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class UpdateOrderDeadline extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String query = "update MainOrder set Deadline = "
                    + Utils.escape(args[1]) + " where OrderNumber = " + args[0];
            Query q = new Query(query);
            boolean success = q.execute();
            if (!success) {
                return false;
            }
            query = "insert into OrderHistory(OrderNumber, EntryDate, Remark) values("
                    + args[0]
                    + ", Now()"
                    + ", "
                    + Utils.escape("Deadline changed: " + args[1]) + ")";
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            Utils.insertEvent(args[1], "New Deadline Ord[" + args[0] + "]",
                    "Old deadline: " + orderDeadline + "\n" + customerName,
                    false);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                OrderDetailActivity.this.recreate();
            } else {
                Toast.makeText(OrderDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class ListSupplierOrder extends AsyncTask<Integer, String, Boolean> {
        View view;
        boolean showAll;
        ArrayList<String[]> histList;

        public ListSupplierOrder(View view) {
            this.view = view;
        }

        @Override
        protected Boolean doInBackground(Integer... args) {
            Log.e("LIST HIST", "");

            if (!Utils.isOnline()) {
                return false;
            }
            histList = new ArrayList<String[]>();
            String query = "";
            // -1 means show all, otherwise it is the position of the compHist
            if (args[0] == -1) {
                query = "select * from SupplierOrderMain "
                        + "where OrderComponentID in "
                        + "(select ID from OrderComponent where OrderNumber ="
                        + orderNumber
                        + ") order by OrderComponentID asc, Step asc";
                showAll = true;
            } else {
                query = "select * from SupplierOrderMain "
                        + "where OrderComponentID = "
                        + componentList.get(args[0])[0]
                        + " order by OrderComponentID asc, Step asc";
                showAll = false;
            }
            Query q = new Query(query);
            boolean success = q.execute();
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            for (int i = 0; i < result.size(); i++) {
                Log.d("LOCCC", "" + result.get(i).toString());
                //compID1[i] = result.get(i).get(Utils.ID);
                String[] hist = new String[21];
                hist[0] = result.get(i).get(Utils.ID);
                hist[1] = result.get(i).get(Utils.DEADLINE);
                hist[2] = result.get(i).get(Utils.RECIPIENT);
                hist[3] = result.get(i).get(Utils.STATUS);
                hist[4] = result.get(i).get(Utils.INST);
                hist[5] = result.get(i).get(Utils.CREATED_DATE);
                hist[6] = result.get(i).get(Utils.STEP);
                hist[7] = result.get(i).get(Utils.INST_CODE);
                // the following line if is to fix dirty data, in case the inst
                // code has less than 19 bits
                if (hist[7].length() < 30) {
                    hist[7] += "000000000000000000000000000000".substring(0,
                            30 - hist[7].length());
                }
                hist[8] = result.get(i).get(Utils.REMARK);
                hist[9] = result.get(i).get(Utils.SUP_ORD_NO);
                hist[10] = result.get(i).get(Utils.ORD_COMP_ID);
                supplierOrderStatus.add(new String[]{result.get(i).get(Utils.ID), result.get(i).get(Utils.SUP_ORD_NO), result.get(i).get(Utils.STATUS)});
                //retrieving stone and gold lines
                query = "select * from SupplierOrderStone" +
                        " where SupplierOrderMainID = " + hist[0];
                q = new Query(query);
                if (!q.execute()) return false;
                if (!q.getRes().isEmpty()) {
                    hist[11] = q.getRes().get(0).get("Detail");
                    hist[12] = q.getRes().get(0).get("Unites");
                    hist[13] = q.getRes().get(0).get("Diam");
                    hist[14] = q.getRes().get(0).get("Type");
                    hist[15] = q.getRes().get(0).get("SertPierre");
                    hist[16] = q.getRes().get(0).get("Prix");
                }
                query = "select * from SupplierOrderGold" +
                        " where SupplierOrderMainID = " + hist[0];
                q = new Query(query);
                if (!q.execute()) return false;
                if (!q.getRes().isEmpty()) {
                    hist[17] = q.getRes().get(0).get("Detail");
                    hist[18] = q.getRes().get(0).get("Unites");
                    hist[19] = q.getRes().get(0).get("GrsLivres");
                    hist[20] = q.getRes().get(0).get("Prix");
                }
                histList.add(hist);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (!s) {
                Toast.makeText(OrderDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
                return;
            }
            final ListView compHistListView = view
                    .findViewById(R.id.comp_hist_list);
            compHistListView.setAdapter(new ComponentHistoryListAdapter(
                    OrderDetailActivity.this, histList, showAll));
            compHistListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    ListAdapter adapter = compHistListView.getAdapter();
                    // changeComponentHistoryStatus((String[])adapter.getItem(position));
                    String compHistID = ((String[]) adapter.getItem(position))[0];
                    String compID = ((String[]) adapter.getItem(position))[10];
                    String suppOrdNo = ((String[]) adapter.getItem(position))[9];
                    trueSuppOrdNo = suppOrdNo;
                    for (int i = 0; i < componentList.size(); i++) {
                        if (componentList.get(i)[0].equals(compID))
                            new getComponentQuantities().execute(suppOrdNo, "" + i, compHistID);
                    }
                }
            });
            compHistListView
                    .setOnItemLongClickListener(new OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent,
                                                       View view, int position, long id) {
                            ListAdapter adapter = compHistListView.getAdapter();
                            ArrayList<String[]> compHistList = new ArrayList<String[]>();
                            if (!name.equals("")) {
                                Query q = new Query("select Name from Inventory where ID=" + name + ";");
                                boolean s = q.execute();
                                if (!s) {
                                    return false;
                                }
                                ArrayList<Map<String, String>> r = q.getRes();
                                if (r.size() <= 0) {
                                    name = "";
                                } else {
                                    name = q.getRes().get(0).get("Name");
                                }
                            } else {
                                name = "";
                            }

                            compHistList.add((String[]) adapter
                                    .getItem(position));
                            ArrayList<Integer> compPosList = new ArrayList<Integer>();
                            compPosList.add(selectedCompPosition);
                            Query q = new Query("select * from SupplierOrderPics where OrderComponentID = " + compID);
                            if (!q.execute()) {
                                return false;
                            }
                            driveLinks = new ArrayList<>();
                            for (Map<String, String> res : q.getRes()) {
                                driveLinks.add(res.get("Link"));
                            }
                            printSupplierOrder(compHistList, compPosList, true, name, driveLinks);
                            return true;
                        }
                    });
        }
    }

    class FetchProducts extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            Query q;
            q = new Query("select * from Products");
            if (!q.execute()) {
                return false;
            } else {
                outItemsData = q.getRes();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
            builder.setTitle("select 'out' items");
            View dialogView = inflater.inflate(R.layout.dialog_out_items, null);
            RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
            ImageView add = dialogView.findViewById(R.id.addLine);
            Button save = dialogView.findViewById(R.id.saveButton);
            outItemsToSend = new ArrayList<>();
            ArrayList<String> newCount = new ArrayList<>();
            newCount.add("");
            OutItemsAdapter adapter = new OutItemsAdapter(outItemsData, newCount);
            recyclerView.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this));
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(false);


            builder.setView(dialogView);
            Dialog dialog = builder.create();
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    dialog.getWindow().clearFlags(
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                }
            });
            add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    newCount.add("");
                    adapter.notifyItemInserted(newCount.size() - 1);
                }
            });
            save.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    OutItemsAdapter.ViewHolder viewHolder;
                    for (int i = 0; i < recyclerView.getChildCount(); i++) {
                        viewHolder = (OutItemsAdapter.ViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                        String cost = "0";
                        for (int j = 0; j < outItemsData.size(); j++) {
                            String parsedId = String.valueOf((int) (Double.parseDouble(outItemsData.get(j).get("ID"))));
                            if (viewHolder.getId().getText().toString().equals(parsedId)) {
                                cost = outItemsData.get(j).get("Standard_Cost");
                            }
                        }
                        ArrayList<String> outData = new ArrayList<>();
                        if (!viewHolder.getId().getText().toString().isEmpty() || !viewHolder.getQuantity().getText().toString().isEmpty()) {
                            outData.add(viewHolder.getId().getText().toString());
                            outData.add(viewHolder.getQuantity().getText().toString());
                            if (cost.isEmpty()) {
                                cost = "0";
                            }
                            outData.add(cost);
                            outData.add(viewHolder.getRemark().getText().toString());
                            outItemsToSend.add(outData);
                        }
                    }
                    Log.d("items to send", "" + outItemsToSend);
                    StringBuilder outItemsString = new StringBuilder();
                    outItemsString.append("OUT:");
                    for (int i = 0; i < outItemsToSend.size(); i++) {
                        outItemsString.append(outItemsToSend.get(i).get(0)).append(",").append(outItemsToSend.get(i).get(1)).append(",").append(outItemsToSend.get(i).get(2)).append(",").append(outItemsToSend.get(i).get(3)).append(";");
                    }
                    ((TextView) window.findViewById(R.id.comp_add_remark)).append(outItemsString.toString());
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    class AddOrEditSupplierOrder extends AsyncTask<String, String, Boolean> {
        String title, desc, deadline, createdDate, instruction, supplier,
                suppOrderNo;
        int position;

        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                Log.d("utilsError", "!isOnline");
                return false;
            }
            for (int i = 0; i < componentList.get(position).length; i++) {
                Log.d("------", "" + i + " " + componentList.get(position)[i]);
            }
            instruction = args[1];
            deadline = args[2];
            createdDate = args[0];
            position = Integer.parseInt(args[6]);
            String componentID = componentList.get(position)[0];
            String articleNumber = componentList.get(position)[2] + " "
                    + componentList.get(position)[3] + " "
                    + componentList.get(position)[4];
            suppOrderNo = customerName.substring(customerName.indexOf(' ') + 1)
                    + " "
                    //+ createdDate.replaceAll("/", "")
                    + orderNumber
                    + "_"
                    + (position + 1)
                    + "_" + Utils.generateSupplierOrderNumber(componentID);
            title = "Deadline " + suppOrderNo;
            desc = "Order no: " + orderNumber;
            desc += "\nArticle: " + articleNumber;
            desc += "\nRecipient: " + args[3];
            desc += "\nInstruction: " + instruction;
            // suppOrderNo = Utils.generateSupplierOrderNumber(args[3]);
            supplier = args[3];

            // print supplier order here
            String query = "";
            Query q;
            if (args[9] != null) {
                query = "update SupplierOrderMain set " + "Instruction = "
                        + Utils.escape(instruction) + ",Deadline = "
                        + Utils.escape(deadline) + ", Recipient = "
                        + Utils.escape(supplier) + ", Status = "
                        + Utils.escape(args[4]) + ", Step = "
                        + Utils.escape(args[5]) + ", InstructionCode = "
                        + Utils.escape(args[7]) + ", Remark = "
                        + Utils.escape(args[8]) + " where ID = " + args[9];
                q = new Query(query);
                if (!q.execute()) {
                    Log.d("failed", "NOK");
                    return false;
                }
                AtomicReference<String> query2 = new AtomicReference<>("");
                supplierOrderStatus.forEach(status -> {
                    if (status[0].equals(args[9])) {
                        if (!status[1].equals(args[4])) {
                            query2.set("insert into OrderHistory(OrderNumber,EntryDate,Remark) " +
                                    "values(" + orderNumber + ", Now(), 'Updated status from " +
                                    status[2] + " to " + args[4] + " (" + status[1] + ")')");
                        }
                    }
                });
                Query q2 = new Query(query2.get());
                q2.execute();
            } else {
                query = "insert into SupplierOrderMain"
                        + "(OrderComponentID, Instruction, Deadline,"
                        + " Recipient, Status, CreatedDate, Step, InstructionCode, Remark, SupplierOrderNumber)"
                        + "values(" + componentID // comp id
                        + ", " + Utils.escape(instruction) // instruction
                        + ", " + Utils.escape(deadline) // deadline
                        + ", " + Utils.escape(supplier) // recipient
                        + ", " + Utils.escape(args[4]) // status
                        + ", " + Utils.escape(createdDate) // created date
                        + ", " + Utils.escape(args[5]) // step
                        + ", " + Utils.escape(args[7]) // code
                        + ", " + Utils.escape(args[8]) // remark
                        + ", " + Utils.escape(suppOrderNo) // supplier order
                        + ")";
                q = new Query(query);
                if (!q.execute()) {
                    Log.d("failed", "NOK");
                    return false;
                }
                query = "insert into OrderHistory(OrderNumber,EntryDate,Remark) " +
                        "values(" + orderNumber + ", Now(), 'Created supplier order " +
                        suppOrderNo + " (" + args[4] + ")')";
                q = new Query(query);
                q.execute();
            }


            for (String link : driveLinks) {
                Query q2 = new Query("insert into SupplierOrderPics (OrderComponentID, Link) values " +
                        "( " + componentID + ", '" + link + "' )");
                if (!q2.execute()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            new PostOuts().execute(createdDate, supplier, "" + position);
        }
    }

    class PostOuts extends AsyncTask<String, String, Boolean> {
        int position;

        @Override
        protected Boolean doInBackground(String... args) {
            String suppOrderNo = customerName.substring(customerName.indexOf(' ') + 1);
            String createdDate = args[0];
            String supplier = args[1];
            position = Integer.parseInt(args[2]);
            for (ArrayList<String> item : outItemsToSend) {
                String type;
                String quantity;
                if (item.get(1).contains("-")) {
                    quantity = item.get(1).replace("-", "");
                    type = "1";
                } else {
                    quantity = item.get(1);
                    type = "2";
                }
                /*suppOrderNo = customerName.substring(customerName.indexOf(' ') + 1)
                        + " "
                        + orderNumber
                        + "_"
                        + (position + 1);*/
                if (suppOrderNo == null) {
                    suppOrderNo = "error";
                }


                Query q = new Query("insert into StockHistory1 (OrderNumber, HistoricDate, ProductID," +
                        " Supplier, Type, Quantity, Cost, Remark) values " +
                        "('" + trueSuppOrdNo + "','" + createdDate + "'," + item.get(0) + ",'" + supplier + "'," + type + "," + quantity + "," + item.get(2) + ",'" + item.get(3) + "')");
                return q.execute();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                // Utils.updateSupplierOrderNumber(supplier, suppOrderNo + 1);
                //Utils.insertEvent(deadline, title, desc, false);
                showCompHist(position);
            } else {
                Toast.makeText(OrderDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class UpdateOrderAfterSending extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String query = "update MainOrder set OrderStatus = "
                    + Utils.escape(args[2]) + " where OrderNumber = " + args[0];
            Query q = new Query(query);
            if (!q.execute())
                return false;

            query = "insert into OrderHistory(OrderNumber, EntryDate, Remark) values("
                    + args[0]
                    + ", Now()"
                    + ", "
                    + Utils.escape(args[1]) + ")";
            q = new Query(query);
            return q.execute();

        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                OrderDetailActivity.this.recreate();
                reminder.dismiss();
            } else {
                Toast.makeText(OrderDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public class SaveImageTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }

            String img = args[1];
            Query q = new Query("insert into OrderComponentPic values("
                    + args[0] + "," + Utils.escape(img) + ")");
            if (!q.execute()) {
                q = new Query("update OrderComponentPic set " + "Image = "
                        + Utils.escape(img) + " where OrderComponentID = "
                        + args[0]);
                return q.execute();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(OrderDetailActivity.this,
                        "Error uploading picture",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(OrderDetailActivity.this,
                        "Uploaded picture",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class AddDiscount extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            double remain = Double.parseDouble(orderRemain), discount = Double
                    .parseDouble(args[2]), oldDiscount = Double
                    .parseDouble(orderDiscount);

            String remark = getString(R.string.textview_discnt) + ": "
                    + discount + ". ";
            String query = "insert into OrderHistory(OrderNumber, EntryDate, PaymentMode,"
                    + " Amount, Remark)" + "values(" + args[0] // order number
                    + ", Now()"  // date
                    + ", " + Utils.escape(Utils.DISCOUNT) // mode
                    + ", " + args[2] // amount
                    + ", " + Utils.escape(remark + args[3]) + ")";
            Query q = new Query(query);
            boolean success = q.execute();
            if (!success) {
                return false;
            }
            query = "update MainOrder set Remain = " + (remain - discount)
                    + ", Discount = " + (oldDiscount + discount)
                    + " where OrderNumber = " + args[0];
            q = new Query(query);
            return q.execute();
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                OrderDetailActivity.this.recreate();
            } else {
                Toast.makeText(OrderDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class UpdateOrderAfterSendingSupplier extends
            AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String query = "update MainOrder set OrderStatus = "
                    + Utils.escape(args[2]) + " where OrderNumber = " + args[0];
            Query q = new Query(query);
            if (!q.execute())
                return false;

            query = "insert into OrderHistory(OrderNumber, EntryDate, Remark) values("
                    + args[0]
                    + ", Now()"
                    + ", "
                    + Utils.escape(args[1]) + ")";
            q = new Query(query);
            if (!q.execute())
                return false;
            for (int i = 0; i < (args.length - 3) / 2; i++) {
                query = "update SupplierOrderMain set Status = "
                        + Utils.escape(args[3 + 2 * i + 1]) + "where ID = "
                        + args[3 + 2 * i];
                q = new Query(query);
                if (!q.execute())
                    return false;
            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                OrderDetailActivity.this.recreate();
                reminder.dismiss();
            } else {
                Toast.makeText(OrderDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public class SaveInstall extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            if (!Utils.isOnline()) {
                return false;
            }
            boolean success;
            String query;
            Query q;
            for (String[] line : installs) {
                query = "insert into Installment(OrderNumber, Deadline, Amount) "
                        + "values("
                        + orderNumber
                        + ", "
                        + Utils.escape(line[0]) // deadline
                        + ", " + line[1] // amount
                        + ")";
                q = new Query(query);
                success = q.execute();
                if (!success) {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (!success) {
                Toast.makeText(OrderDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    public class GetInstall extends AsyncTask<Void, Void, Boolean> {
        ArrayList<String[]> installments;

        @Override
        protected Boolean doInBackground(Void... params) {
            if (!Utils.isOnline()) {
                return false;
            }
            String query = "select * from Installment where OrderNumber = "
                    + orderNumber;
            Query q = new Query(query);
            if (!q.execute())
                return false;
            installments = new ArrayList<String[]>();
            for (int i = 0; i < q.getRes().size(); i++) {
                String[] line = new String[4];
                line[0] = q.getRes().get(i).get(Utils.ID);
                line[1] = q.getRes().get(i).get(Utils.DEADLINE);
                line[2] = q.getRes().get(i).get(Utils.AMOUNT);
                line[3] = q.getRes().get(i).get(Utils.PAID);
                installments.add(line);
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                if (installments.isEmpty()) {
                    viewInstDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setClickable(false);
                    installmentList.removeAllViews();
                    Button b = new Button(OrderDetailActivity.this);
                    b.setText("Add Installments");
                    b.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewInstDialog.dismiss();
                            addInstallment();
                        }
                    });
                    installmentList.addView(b);
                    return;
                }

                ((ListView) installmentList.findViewById(R.id.install_list))
                        .setAdapter(new InstallmentListAdapter(
                                OrderDetailActivity.this, installments));
            } else {
                Toast.makeText(OrderDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    public class UpdateInstall extends AsyncTask<Void, Void, Boolean> {
        double total = 0;

        @Override
        protected Boolean doInBackground(Void... params) {
            if (!Utils.isOnline()) {
                return false;
            }
            String ids = "(";
            for (String[] id : updateInstallList) {
                ids += id[0] + ",";
                total += Double.parseDouble(id[1]);
            }
            ids = ids + ")";
            String query = "update Installment set Paid = 1 " + "where ID in "
                    + ids;
            Query q = new Query(query);
            return q.execute();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                addPayment(total);
            } else {
                Toast.makeText(OrderDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    class AdjustTotal extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            double total = Double.parseDouble(orderTotal), remain = Double
                    .parseDouble(orderRemain), amount = Double
                    .parseDouble(args[2]);

            String remark = "Ajustement: " + amount + " - " + args[3] + ". ";
            String query = "insert into OrderHistory(OrderNumber, EntryDate, PaymentMode,"
                    + " Amount, Remark)" + "values(" + args[0] // order number
                    + ", Now()"  // date
                    + ", " + "'Ajustement'" // mode
                    + ", " + amount // amount
                    + ", " + Utils.escape(remark) + ")";
            Query q = new Query(query);
            boolean success = q.execute();
            if (!success) {
                return false;
            }
            query = "update MainOrder set Total = " + (total + amount)
                    + ", Remain = " + (remain + amount)
                    + " where OrderNumber = " + args[0];
            q = new Query(query);
            return q.execute();
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                OrderDetailActivity.this.recreate();
            } else {
                Toast.makeText(OrderDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class getComponentQuantities extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... args) {
            String fullOrderNumber = args[0];
            Matcher matcher = Pattern.compile("\\d").matcher(fullOrderNumber);
            matcher.find();
            String trueOrderNumber = fullOrderNumber.substring(matcher.start());
            for (int i = trueOrderNumber.length() - 1; i > 0; i--) {
                if (trueOrderNumber.charAt(i) == '_') {
                    trueOrderNumber = trueOrderNumber.substring(0, i);
                    break;
                }
            }
            String query = "select * from OrderComponentQuantity where OrderNumber like '" + trueOrderNumber + "%'";
            Query q = new Query(query);
            boolean s = q.execute();
            stockQuantities = q.getRes();
            String[] data = new String[2];
            data[0] = args[1];
            data[1] = args[2];
            return data;
        }

        @Override
        protected void onPostExecute(String[] data) {
            addOrEditCompHist(Integer.parseInt(data[0]), data[1]);
        }
    }

}
//TODO display compID, comp details in activities, increase article prefix