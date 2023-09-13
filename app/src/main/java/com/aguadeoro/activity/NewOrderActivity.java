package com.aguadeoro.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.ArrayAdapterCustomFonts;
import com.aguadeoro.utils.EditTextDropdown;
import com.aguadeoro.utils.EditTextDropdownCustomFont;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

public class NewOrderActivity extends Activity {
    String customerNumber, orderType, name1, name2, store;
    String[] compListLabels = {"-"};
    String[][] compList; //list of all articles from previous orders, for dropdown
    String[] inventoryInfo;
    ArrayList<String> saleList;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener odate, ddate;
    Double tvaRate;
    LinearLayout currentCompLine;
    boolean isSale = false;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CALENDAR = 1;
    private static final String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final String[] PERMISSIONS_CALENDAR = {Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_new_order);
        setTitle(getIntent().getStringExtra(Utils.TITLE));
        verifyCalendarPermissions(this);
        saleList = new ArrayList<String>();
        customerNumber = getIntent().getStringExtra(Utils.CUST_NO);
        orderType = getIntent().getStringExtra(Utils.ORDER_TYPE);
        name1 = getIntent().getStringExtra(Utils.CUST_NAME_1);
        name2 = getIntent().getStringExtra(Utils.CUST_NAME_2);
        isSale = getIntent().getBooleanExtra("isSale", false);
        hideLinesForSpecialOrder();
        myCalendar = Calendar.getInstance();
        final EditText oDate = findViewById(R.id.order_date);
        final EditText oTime = findViewById(R.id.order_time);
        final EditText dDate = findViewById(R.id.order_deadline);
        Spinner stores = findViewById(R.id.storeSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Utils.getSetSetting("Stores"));
        stores.setAdapter(adapter);
        for(int i = 0; i < Utils.getSetSetting("Stores").length; i++) {
            if(Utils.getSetSetting("Stores")[i].equals(Utils.getStringSetting("store_selected"))) {
                stores.setSelection(i);
                break;
            }
        }
        oTime.setText(myCalendar.get(Calendar.HOUR_OF_DAY) + ":" + myCalendar.get(Calendar.MINUTE));
        oDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewOrderActivity.this, odate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        odate = new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate(oDate);
            }
        };
        dDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewOrderActivity.this, ddate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        ddate = new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate(dDate);
            }
        };

        ((EditText) findViewById(R.id.order_date)).setText(Utils.shortDateForDisplay(myCalendar.getTime()));
        ((EditText) findViewById(R.id.order_deadline)).setText(Utils.shortDateForDisplay(myCalendar.getTime()));
        findViewById(R.id.add_button).setLongClickable(true);// long click to add discount
        findViewById(R.id.add_button).setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (findViewById(R.id.order_discount).getVisibility() == View.GONE) {
                    findViewById(R.id.order_discount).setVisibility(View.VISIBLE);
                    findViewById(R.id.order_discount_txt).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.order_discount).setVisibility(View.GONE);
                    findViewById(R.id.order_discount_txt).setVisibility(View.GONE);
                }
                return true;
            }
        });

        ((EditTextDropdown) findViewById(R.id.engraving_type)).setList(Utils.getSetSetting(Utils.ENGRAVE_TYPE));
        ((EditTextDropdown) findViewById(R.id.material)).setList(Utils.getSetSetting(Utils.MATERIAL_TYPE));
        ((EditTextDropdown) findViewById(R.id.surface)).setList(Utils.getSetSetting(Utils.SURFACE));
        ((EditTextDropdown) findViewById(R.id.color)).setList(Utils.getSetSetting(Utils.COLOR_TYPE));


        final EditTextDropdownCustomFont enFont = findViewById(R.id.engraving_font);
        enFont.setAdapter(new ArrayAdapterCustomFonts(this, "Bonnie and Clyde forever"));
        final TextView enText = findViewById(R.id.engraving_text);
        enText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !enText.getText().toString().equals("")) {
                    enFont.getAdapter().setText(((TextView) v).getText().toString());
                }
            }
        });
        makeChangesForRepairOrder();
        ((Spinner) findViewById(R.id.seller)).setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Utils.getSetSetting(Utils.SELLER)));
        //getting tva rate from settings
        tvaRate = Utils.getNumberSetting("tva_rate") / 100;
    }

    public void updateDate(EditText date) {
        date.setText(Utils.shortDateForDisplay(myCalendar.getTime()));
    }

    public void goToStonesPrices(View view) {
        Intent intent = new Intent(this, StonesPricesActivity.class);
        intent.putExtra("from", "order");
        startActivity(intent);
    }

    public static void verifyCalendarPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_CALENDAR, REQUEST_CALENDAR);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_order, menu);
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
            Intent i = new Intent(this, CustomerDetailActivity.class);
            i.putExtra(Utils.CUST_REL_ID, getIntent().getStringExtra(Utils.CUST_REL_ID));
            startActivity(i);
            finish();
        }
        if (id == R.id.stonesPrices) {
            Intent i = new Intent(this, StonesPricesActivity.class);
            i.putExtra("from", "neworder");
            startActivity(i);
        }
        if (id == R.id.inventory) {
            Intent i = new Intent(this, InventoryActivity.class);
            startActivity(i);
        }
        if (id == R.id.location) {
            Intent i = new Intent(this, StockActivity.class);
            i.putExtra("from", "neworder");
            startActivity(i);
        }
        return true;
    }

    public void updateTotal(View v) {
        double t = 0, d = 0;
        EditText total = findViewById(R.id.order_total);
        EditText discount = findViewById(R.id.order_discount);
        if (!"".equals(discount.getText().toString()))
            d = Double.parseDouble(discount.getText().toString());
        EditText remain = findViewById(R.id.order_remaining);
        LinearLayout cps = findViewById(R.id.order_component_holder);
        for (int i = 0; i < cps.getChildCount(); i++) {
            if (cps.getChildAt(i).getId() == R.id.order_component) {
                String price = ((EditText) cps.getChildAt(i).findViewById(R.id.price)).getText().toString();
                if (!"".equals(price)) t += Double.parseDouble(price);
                String engravingCost = ((EditText) cps.getChildAt(i).findViewById(R.id.engraving_cost)).getText().toString();
                if (!"".equals(engravingCost)) t += Double.parseDouble(engravingCost);
            }
        }
        //change order total according to tva
        CheckBox isTVA = findViewById(R.id.tva);
        total.setText("" + t);
        remain.setText("" + (t - d));
        if (!isTVA.isChecked()) {
            remain.setText("" + (t - d - Utils.round((t - d) * tvaRate / (1 + tvaRate), 1)));
        }
    }

    public void addComponent(View v) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout cmp = (LinearLayout) inflater.inflate(R.layout.line_order_component, null);
        LinearLayout holder = findViewById(R.id.order_component_holder);
        holder.addView(cmp);
        View removeButton = findViewById(R.id.remove_button);
        removeButton.setVisibility(View.VISIBLE);
        hideLinesForSpecialOrder();
        ((EditTextDropdown) cmp.findViewById(R.id.engraving_type)).setList(Utils.getSetSetting(Utils.ENGRAVE_TYPE));
        ((EditTextDropdown) cmp.findViewById(R.id.material)).setList(Utils.getSetSetting(Utils.MATERIAL_TYPE));
        ((EditTextDropdown) cmp.findViewById(R.id.surface)).setList(Utils.getSetSetting(Utils.SURFACE));
        ((EditTextDropdown) cmp.findViewById(R.id.color)).setList(Utils.getSetSetting(Utils.COLOR_TYPE));

        final EditTextDropdownCustomFont enFont = cmp.findViewById(R.id.engraving_font);
        enFont.setAdapter(new ArrayAdapterCustomFonts(this, "Bonnie and Clyde forever"));
        final TextView enText = cmp.findViewById(R.id.engraving_text);
        enText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !enText.getText().toString().equals("")) {
                    enFont.getAdapter().setText(((TextView) v).getText().toString());
                }
            }
        });

        // if clicked on copy button, copy from previous component
        if (holder.getChildCount() > 1 && v.getId() == R.id.copy_button) {
            LinearLayout org = (LinearLayout) holder.getChildAt(holder.getChildCount() - 2);
            ((EditText) cmp.findViewById(R.id.article_type)).setText(((EditText) org.findViewById(R.id.article_type)).getText().toString());
            ((EditText) cmp.findViewById(R.id.article_prefix)).setText(((EditText) org.findViewById(R.id.article_prefix)).getText().toString());
            ((EditText) cmp.findViewById(R.id.article_number)).setText(((EditText) org.findViewById(R.id.article_number)).getText().toString());
            ((EditText) cmp.findViewById(R.id.material)).setText(((EditText) org.findViewById(R.id.material)).getText().toString());
            ((EditText) cmp.findViewById(R.id.catalog_code)).setText(((EditText) org.findViewById(R.id.catalog_code)).getText().toString());
            ((EditText) cmp.findViewById(R.id.color)).setText(((EditText) org.findViewById(R.id.color)).getText().toString());
            ((EditText) cmp.findViewById(R.id.length)).setText(((EditText) org.findViewById(R.id.length)).getText().toString());
            ((EditText) cmp.findViewById(R.id.height)).setText(((EditText) org.findViewById(R.id.height)).getText().toString());
            ((EditText) cmp.findViewById(R.id.size)).setText(((EditText) org.findViewById(R.id.size)).getText().toString());
            ((EditText) cmp.findViewById(R.id.surface)).setText(((EditText) org.findViewById(R.id.surface)).getText().toString());
            ((EditText) cmp.findViewById(R.id.stone)).setText(((EditText) org.findViewById(R.id.stone)).getText().toString());
            ((EditText) cmp.findViewById(R.id.engraving_text)).setText(((EditText) org.findViewById(R.id.engraving_text)).getText().toString());
            ((EditText) cmp.findViewById(R.id.engraving_type)).setText(((EditText) org.findViewById(R.id.engraving_type)).getText().toString());
            ((EditText) cmp.findViewById(R.id.engraving_font)).setText(((EditText) org.findViewById(R.id.engraving_font)).getText().toString());
        }

        // if selected an article from a previous order, use the info of this article to populate new article
        if (orderType.equals(Utils.ORD_REPAIR) || orderType.equals(Utils.ORD_PURCHASE) || orderType.equals(Utils.ORD_ORDER) || orderType.equals(Utils.ORD_PREVIEW)) {
            Spinner compListDropdown = findViewById(R.id.comp_list);
            if (compListDropdown.getSelectedItemPosition() == 0) {
                return;
            }
            String[] compInfo = compList[compListDropdown.getSelectedItemPosition() - 1];
            /*for(int i=0;i<compInfo.length;i++){
                Log.d("infodrop",""+i+" "+compInfo[i]);
            }*/
            ((EditText) cmp.findViewById(R.id.article_type)).setText(compInfo[2]);
            ((EditText) cmp.findViewById(R.id.article_prefix)).setText(compInfo[3]);
            ((EditText) cmp.findViewById(R.id.article_number)).setText(compInfo[4]);
            ((EditText) cmp.findViewById(R.id.material)).setText(compInfo[5]);
            ((EditText) cmp.findViewById(R.id.color)).setText(compInfo[6]);
            ((EditText) cmp.findViewById(R.id.length)).setText(compInfo[7]);
            ((EditText) cmp.findViewById(R.id.height)).setText(compInfo[8]);
            ((EditText) cmp.findViewById(R.id.size)).setText(compInfo[9]);
            ((EditText) cmp.findViewById(R.id.surface)).setText(compInfo[10]);
            ((EditText) cmp.findViewById(R.id.stone)).setText(compInfo[11]);
            ((EditText) cmp.findViewById(R.id.remark)).setText(compInfo[17]);
            ((EditText) cmp.findViewById(R.id.catalog_code)).setText(compInfo[27]);
            ((EditText) cmp.findViewById(R.id.engraving_text)).setText(compInfo[12]);
            ((EditText) cmp.findViewById(R.id.engraving_type)).setText(compInfo[13]);
            ((EditText) cmp.findViewById(R.id.engraving_font)).setText(compInfo[14]);
            ((CheckBox) cmp.findViewById(R.id.fingerprint)).setChecked(compInfo[25].equals("1"));
            ((CheckBox) cmp.findViewById(R.id.microtext)).setChecked(compInfo[26].equals("1"));
        }
    }

    private void makeChangesForRepairOrder() {
        LinearLayout cps = findViewById(R.id.order_component_holder);
        if (orderType.equals(Utils.ORD_REPAIR)) {
            cps.removeViewAt(0);
            findViewById(R.id.comp_list).setVisibility(View.VISIBLE);
            findViewById(R.id.sellertext).setVisibility(View.INVISIBLE);
            findViewById(R.id.seller).setVisibility(View.INVISIBLE);
            new GetCompList().execute();
        } else if (orderType.equals(Utils.ORD_PURCHASE) || orderType.equals(Utils.ORD_ORDER)) {
            cps.removeViewAt(0);
            findViewById(R.id.comp_list).setVisibility(View.VISIBLE);
            new GetCompList().execute();
        } else if (orderType.equals(Utils.ORD_PREVIEW)) {
            findViewById(R.id.sellertext).setVisibility(View.INVISIBLE);
            findViewById(R.id.seller).setVisibility(View.INVISIBLE);
            cps.removeViewAt(0);
            findViewById(R.id.comp_list).setVisibility(View.VISIBLE);
            new GetCompList().execute();
        }

    }

    public void removeComponent(View v) {
        LinearLayout holder = findViewById(R.id.order_component_holder);
        holder.removeViewAt(holder.getChildCount() - 1);
        if (holder.getChildCount() < 1) {
            v.setVisibility(View.INVISIBLE);
            return;
        }
    }

    public void saveOrder(View v) {
        v.setClickable(false);
        updateTotal(v);
        LinearLayout holder = findViewById(R.id.order_component_holder);
        int c = holder.getChildCount();
        String[][] inputs = new String[c + 1][];
        inputs[0] = new String[12];
        inputs[0][0] = getString(R.string.newStt);
        inputs[0][1] = customerNumber;
        String fullOrderDate = ((EditText) findViewById(R.id.order_date)).getText().toString() + " " + ((EditText) findViewById(R.id.order_time)).getText().toString();
        inputs[0][2] = Utils.longDateForInsert(fullOrderDate);
        inputs[0][3] = Utils.shortDateForInsert(((EditText) findViewById(R.id.order_deadline)).getText().toString());
        inputs[0][4] = ((EditText) findViewById(R.id.order_total)).getText().toString();
        inputs[0][5] = ((EditText) findViewById(R.id.order_discount)).getText().toString();
        inputs[0][6] = "0"; // paid amount
        inputs[0][7] = ((EditText) findViewById(R.id.order_remaining)).getText().toString();
        inputs[0][8] = ((EditText) findViewById(R.id.order_remark)).getText().toString();
        inputs[0][9] = orderType;
        inputs[0][10] = ((Spinner) findViewById(R.id.seller)).getSelectedItem().toString();
        if (inputs[0][10].length() == 0) inputs[0][10] = Utils.ADO;
        //tva included or not?
        inputs[0][11] = ((CheckBox) findViewById(R.id.tva)).isChecked() ? "1" : "0";
        for (int i = 1; i < c + 1; i++) {
            LinearLayout child = (LinearLayout) holder.getChildAt(i - 1);
            inputs[i] = new String[20];
            inputs[i][0] = ((EditText) child.findViewById(R.id.article_type)).getText().toString();
            inputs[i][1] = ((EditText) child.findViewById(R.id.article_prefix)).getText().toString();
            inputs[i][2] = ((EditText) child.findViewById(R.id.article_number)).getText().toString();
            inputs[i][3] = ((EditText) child.findViewById(R.id.material)).getText().toString();
            inputs[i][4] = ((EditText) child.findViewById(R.id.color)).getText().toString();
            inputs[i][5] = ((EditText) child.findViewById(R.id.length)).getText().toString();
            inputs[i][6] = ((EditText) child.findViewById(R.id.height)).getText().toString();
            inputs[i][7] = ((EditText) child.findViewById(R.id.size)).getText().toString();
            inputs[i][8] = ((EditText) child.findViewById(R.id.surface)).getText().toString();
            inputs[i][9] = ((EditText) child.findViewById(R.id.stone)).getText().toString();
            inputs[i][10] = ((EditText) child.findViewById(R.id.engraving_text)).getText().toString();
            inputs[i][11] = ((EditText) child.findViewById(R.id.engraving_type)).getText().toString();
            inputs[i][12] = ((EditText) child.findViewById(R.id.engraving_font)).getText().toString();
            inputs[i][13] = ((EditText) child.findViewById(R.id.engraving_cost)).getText().toString();
            inputs[i][14] = ((EditText) child.findViewById(R.id.price)).getText().toString();
            inputs[i][15] = ((EditText) child.findViewById(R.id.remark)).getText().toString();
            inputs[i][16] = ((CheckBox) child.findViewById(R.id.fingerprint)).isChecked() ? "1" : "0";
            inputs[i][17] = ((CheckBox) child.findViewById(R.id.microtext)).isChecked() ? "1" : "0";
            inputs[i][18] = ((TextView) child.findViewById(R.id.catalog_code)).getText().toString();
            inputs[i][19] = "" + child.findViewById(R.id.catalog_code).getTag();
        }
        store = ((Spinner) findViewById(R.id.storeSpinner)).getSelectedItem().toString();
        new SaveOrderTask().execute(inputs);
    }

    public void goToOrderDetailActivity(String orderNumber) {
        Intent i = new Intent(this, OrderDetailActivity.class);
        i.putExtra(Utils.ORDER_NO, orderNumber);
        i.putExtra(Utils.CUST_REL_ID, getIntent().getStringExtra(Utils.CUST_REL_ID));
        startActivity(i);
        finish();
    }

    public void hideLinesForSpecialOrder() {
        LinearLayout cps = findViewById(R.id.order_component_holder);
        if (orderType.equals(Utils.ORD_PREVIEW)) {
            int i = cps.getChildCount() - 1;// for (int i = 0; i <
            // cps.getChildCount(); i++) {
            cps.getChildAt(i).findViewById(R.id.order_comp_line3).setVisibility(View.GONE);
            cps.getChildAt(i).findViewById(R.id.order_comp_line4).setVisibility(View.GONE);
            cps.getChildAt(i).findViewById(R.id.order_comp_line5).setVisibility(View.GONE);
            // }
            findViewById(R.id.total_button).setVisibility(View.GONE);
        }
        int i = cps.getChildCount() - 1;// for (int i = 0; i <
        // cps.getChildCount(); i++) {
        EditTextDropdown artNo = cps.getChildAt(i).findViewById(R.id.article_type);
        artNo.setList(Utils.getSetSetting(Utils.ARTICLE_TYPE));
        cps.getChildAt(i).findViewById(R.id.edit_btn).setVisibility(View.GONE);
        cps.getChildAt(i).findViewById(R.id.takepic_btn).setVisibility(View.GONE);
        cps.getChildAt(i).findViewById(R.id.viewpic_btn).setVisibility(View.GONE);
        // }
    }

    public void searchInfo(View v) {
        Toast toast = Toast.makeText(NewOrderActivity.this, getString(R.string.sorting), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 100);
        toast.show();
        inventoryInfo = null;
        currentCompLine = (LinearLayout) v.getParent().getParent();
        String catalogCode = ((TextView) currentCompLine.findViewById(R.id.catalog_code)).getText().toString();
        new SearchInfo().execute(catalogCode);
    }

    public void confirmInfo(View v) {
        if (inventoryInfo != null) {
            saleList.add(inventoryInfo[0]);
            inventoryInfo = null;
            v.setClickable(false);
        }
        Toast toast = Toast.makeText(NewOrderActivity.this, saleList.toString(), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 100);
        toast.show();
    }

    public void viewInventoryDetail(View v) {
        if (inventoryInfo == null) return;
        Intent intent = new Intent(this, ViewInventoryActivity.class);
        intent.putExtra(Utils.ID, inventoryInfo[0]);
        startActivity(intent);

    }

    public void viewInventoryDetailStock(View v) {
        if (inventoryInfo == null) return;
        Intent intent = new Intent(this, ViewInventoryActivity.class);
        String query = "select ID from Inventory where CatalogCode = " + Utils.escape(inventoryInfo[10]) + " and Status = 'Stock'";
        Query q = new Query(query);
        q.execute();
        if (q.getRes().size() > 0) {
            intent.putExtra(Utils.ID, q.getRes().get(0).get("ID"));
            startActivity(intent);
        } else {
            Toast.makeText(NewOrderActivity.this, "error, no item in stock with that InventoryCode", Toast.LENGTH_LONG).show();
        }

    }

    public class SaveOrderTask extends AsyncTask<String[], String, Boolean> {
        Integer orderNumber;
        String deadline, title, description;

        @Override
        protected Boolean doInBackground(String[]... inputs) {
            if (!Utils.isOnline()) {
                return false;
            }
            boolean success;
            String query = "select max(OrderNumber) from MainOrder";
            Query q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            orderNumber = 1;
            if (Utils.isInteger(result.get(0).get("0"))) {
                orderNumber = Integer.parseInt(result.get(0).get("0")) + 1;
            }
            query = "insert into MainOrder(OrderNumber, OrderStatus, " + "CustomerNumber, OrderDate, Deadline, Total, " + "Discount, Paid, Remain, Remark, OrderType, Seller, TVA, StoreMainOrder) " + "values(" + orderNumber + ", " + Utils.escape(inputs[0][0]) // order status
                    + ", " + inputs[0][1] // customer no
                    + ", " + Utils.escape(inputs[0][2]) // order date
                    + ", " + Utils.escape(inputs[0][3]) // deadline
                    + ", " + inputs[0][4] // total
                    + ", " + inputs[0][5] // discount
                    + ", " + inputs[0][6] // paid
                    + ", " + inputs[0][7] // remain
                    + ", " + Utils.escape(inputs[0][8]) // remark
                    + ", " + Utils.escape(inputs[0][9]) // type
                    + ", " + Utils.escape(inputs[0][10]) // seller
                    + ", " + inputs[0][11] // TVA yes or no
                    + ", " + Utils.escape(store) // store main order
                    + ")";
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            query = "insert into CustomerHistory(CustomerNumber, HistoryDate, Remark, HistoryType)" + " values(" + inputs[0][1] // cust no
                    + ", " + Utils.escape(inputs[0][2]) // order date
                    + ", '" + getString(R.string.placed_ord_no) + " " + orderNumber + "'," + Utils.escape(Utils.HISTTYPE_ORDER) + ")";
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }

            //if customer made a REAL order, change type to Normal
            if (!orderType.equals(Utils.ORD_PREVIEW)) {
                query = "update Customer set Type = 'Normal' " + "where CustomerNumber in " + "(select Customer2 from CustomerRelationship " + "where Customer1 = " + inputs[0][1] + ")";
                q = new Query(query);
                success = q.execute();
                if (!success) {
                    return false;
                }
                query = "update Customer set Type = 'Normal' " + "where CustomerNumber = " + inputs[0][1];
                q = new Query(query);
                success = q.execute();
                if (!success) {
                    return false;
                }
            }
            for (int i = 1; i < inputs.length; i++) {
                query = "insert into OrderComponent(OrderNumber, ArticleType, ArticlePrefix, ArticleNumber, " + "Material, Color, Length, Height, Size, Surface, Stone, EngravingText, " + "EngravingType, EngravingFont, EngravingCost, Price, Remark, Fingerprint," + " Microtext, CatalogCode, InventoryID) " + "values";
                query += "(" + orderNumber + ", " + Utils.escape(inputs[i][0]) // article
                        // type
                        + ", " + Utils.escape(inputs[i][1]) // article prefix
                        + ", " + Utils.escape(inputs[i][2]) // article no
                        + ", " + Utils.escape(inputs[i][3]) // material
                        + ", " + Utils.escape(inputs[i][4]) // color
                        + ", " + Utils.escape(inputs[i][5]) // length
                        + ", " + Utils.escape(inputs[i][6]) // height
                        + ", " + Utils.escape(inputs[i][7]) // size
                        + ", " + Utils.escape(inputs[i][8]) // surface
                        + ", " + Utils.escape(inputs[i][9]) // stone
                        + ", " + Utils.escape(inputs[i][10]) // engraving text
                        + ", " + Utils.escape(inputs[i][11]) // type
                        + ", " + Utils.escape(inputs[i][12]) // font
                        + ", " + inputs[i][13] // cost
                        + ", " + inputs[i][14] // price
                        + ", " + Utils.escape(inputs[i][15]) // remark
                        + ", " + inputs[i][16] // fingerprint
                        + ", " + inputs[i][17] // microtext
                        + ", " + Utils.escape(inputs[i][18]) // catalogCode
                        + ", " + inputs[i][19]        // inventory id
                        + ")";
                q = new Query(query);
                success = q.execute();
                if (!success) {
                    return false;
                }
            }

            // if sale of stock, update inventory, insert distribution line if sorti
            if (orderType.equals(Utils.ORD_PURCHASE)) {
                for (String saleID : saleList) {
                    query = "update Inventory set Quantity = Quantity -1 " + " Where ID = " + saleID + " and Status = " + Utils.escape(Utils.STOCK);
                    q = new Query(query);
                    success = q.execute();
                    if (!success) {
                        return false;
                    }
                    query = "insert into InventoryDistribution(InventoryID, AssignedTo, Quantity, " + "AssignDate, Status) select top 1 IID, AT, QT, DT, ST from " + "(select " + saleID + " as IID,id.AssignedTo as AT,1 as QT,Now() as DT," + Utils.escape(Utils.SOLD) + "as ST" + " from InventoryDistribution id, Inventory i" + " where i.Status =" + Utils.escape(Utils.TAKE_OUT) + " and i.ID = " + saleID + " and i.ID = id.InventoryID " + " order by AssignDate Desc)";
                    q = new Query(query);
                    success = q.execute();
                    if (!success) {
                        return false;
                    }

                    query = "update Inventory set Status = " + Utils.escape(Utils.SOLD) + " Where ID = " + saleID;
                    q = new Query(query);
                    success = q.execute();
                    if (!success) {
                        return false;
                    }
                }
            }

            deadline = inputs[0][3];
            title = "Deadline OrdNo[" + orderNumber + "]";
            description = "Order no: " + orderNumber;
            description += "\n" + name1 + ", " + name2;
            description += "\nCreated: " + inputs[0][2];
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Utils.insertEvent(deadline, title, description, false);
                finish();
                goToOrderDetailActivity(orderNumber.toString());
            } else {
                Toast.makeText(NewOrderActivity.this, getString(R.string.error_retrieving_data), Toast.LENGTH_LONG).show();
                findViewById(R.id.save_button).setClickable(true);
            }
        }

    }

    public class GetCompList extends AsyncTask<String[], String, Boolean> {

        @Override
        protected Boolean doInBackground(String[]... inputs) {
            if (!Utils.isOnline()) {
                return false;
            }
            String query = "select c.* from OrderComponent c, MainOrder o where " + "c.OrderNumber = o.OrderNumber and " + "o.CustomerNumber = " + customerNumber;
            Query q = new Query(query);
            if (!q.execute()) return false;
            ArrayList<Map<String, String>> result = q.getRes();
            compListLabels = new String[result.size() + 1];
            compListLabels[0] = "-";
            compList = new String[result.size()][28];
            for (int i = 0; i < result.size(); i++) {
                compListLabels[i + 1] = result.get(i).get("2") + " " + result.get(i).get("3") + " " + result.get(i).get("4") + "/" + result.get(i).get("5") + "/" + result.get(i).get("6") + "/" + result.get(i).get("7") + "/" + result.get(i).get("8");
                for (int j = 0; j < 28; j++) {
                    compList[i][j] = result.get(i).get("" + j);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Spinner compListDropdown = findViewById(R.id.comp_list);
                compListDropdown.setAdapter(new ArrayAdapter<String>(NewOrderActivity.this, android.R.layout.simple_list_item_1, compListLabels));
            } else {
                Toast.makeText(NewOrderActivity.this, getString(R.string.error_retrieving_data), Toast.LENGTH_LONG).show();
            }
        }

    }

    public class SearchInfo extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... inputs) {
            if (!Utils.isOnline()) {
                return false;
            }
            String query;
            Query q;
            ArrayList<Map<String, String>> result = null;
            if (isSale) {
                query = "select top 1 * from Inventory where " + "Status = 'Stock' and InventoryCode =" + Utils.escape(inputs[0]);
            } else {
                query = "select top 1 * from Inventory where " + "Status in ('Stock','Catalogue','Vendu', 'Prod') and(" + "CatalogCode =" + Utils.escape(inputs[0]) + "or InventoryCode =" + Utils.escape(inputs[0]) + ")";
            }

            q = new Query(query);
            if (!q.execute() || q.getRes().size() == 0) {
                return false;
            }
            result = q.getRes();
            if (result.size() == 0) {
                return false;
            }
            if (!isSale) {
                query = "select top 1 * from Inventory where Status ='Catalogue' and CatalogCode=" + Utils.escape(result.get(0).get("CatalogCode"));
            }
            q = new Query(query);
            if (!q.execute()) {
                return false;
            }
            if (q.getRes().size() > 0) {
                result = q.getRes();
            }

            for (int i = 0; i < result.size(); i++) {
                inventoryInfo = new String[12];
                inventoryInfo[0] = result.get(i).get("ID");
                inventoryInfo[1] = result.get(i).get("Code");
                inventoryInfo[2] = result.get(i).get("Category");
                if (!(Objects.equals(result.get(i).get("Name"), "") || result.get(i).get("Name") == null)) {
                    inventoryInfo[3] = result.get(i).get("Name");
                } else {
                    inventoryInfo[3] = result.get(i).get("Description");
                }
                inventoryInfo[4] = result.get(i).get("Material");
                inventoryInfo[5] = result.get(i).get("Color");
                inventoryInfo[6] = result.get(i).get("Width");
                inventoryInfo[7] = result.get(i).get("Height");
                inventoryInfo[8] = result.get(i).get("Size");
                inventoryInfo[9] = result.get(i).get("Stone");
                inventoryInfo[10] = result.get(i).get("CatalogCode");
                inventoryInfo[11] = result.get(i).get("InventoryCode");
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success && inventoryInfo != null) {
                ((EditText) currentCompLine.findViewById(R.id.article_type)).setText(inventoryInfo[2]);
                ((TextView) currentCompLine.findViewById(R.id.article_prefix)).setText(inventoryInfo[3]);
                if (isSale) {
                    ((TextView) currentCompLine.findViewById(R.id.article_number)).setText(inventoryInfo[11]);
                } else {
                    ((TextView) currentCompLine.findViewById(R.id.article_number)).setText(inventoryInfo[1]);
                }
                ((TextView) currentCompLine.findViewById(R.id.material)).setText(inventoryInfo[4]);
                ((TextView) currentCompLine.findViewById(R.id.color)).setText(inventoryInfo[5]);
                ((TextView) currentCompLine.findViewById(R.id.length)).setText(inventoryInfo[6]);
                ((TextView) currentCompLine.findViewById(R.id.height)).setText(inventoryInfo[7]);
                ((TextView) currentCompLine.findViewById(R.id.size)).setText(inventoryInfo[8]);
                ((TextView) currentCompLine.findViewById(R.id.stone)).setText(inventoryInfo[9]);
                currentCompLine.findViewById(R.id.catalog_code).setTag(inventoryInfo[0]);
                ((TextView) currentCompLine.findViewById(R.id.catalog_code)).setText(inventoryInfo[10]);

            } else {
                Toast.makeText(NewOrderActivity.this, getString(R.string.error_retrieving_data), Toast.LENGTH_LONG).show();
            }
        }

    }

}
