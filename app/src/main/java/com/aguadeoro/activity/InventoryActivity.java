package com.aguadeoro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.InventoryListAdapter;
import com.aguadeoro.utils.MyAsyncTask;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class InventoryActivity extends ListActivity {

    ArrayList<String[]> invList;
    View dialog;
    String whereClause, sortBy;
    String INI_WHERE = " where Status <> 'Inactive' ";
    boolean isMultiple;
    private View wheelView;
    private View mainView;
    AlertDialog alert;
    boolean isSwitched = false;
    String[] lines;
    String[] collections;

    enum SearchingType {
        INVENTORY, CATALOG, NAME, CATEGORY, STATUS, LINE, COLLECTION, MATERIAL, COLOR, SIZE, CARAT, PRICE
    }

    class SearchingItem {
        SearchingType type = null;
        String value = "";
        Boolean checked = false;

        public SearchingItem(SearchingType name, String value, Boolean checked) {
            this.type = name;
            this.value = value;
            this.checked = checked;
        }


        public SearchingType getType() {
            return type;
        }

        public void setType(SearchingType type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Boolean getChecked() {
            return checked;
        }

        public void setChecked(Boolean checked) {
            this.checked = checked;
        }

        @Override
        public String toString() {
            return "SearchingItem{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    ", checked=" + checked +
                    '}';
        }
    }


    List<SearchingItem> lastSearchingItems = new ArrayList<>();


    EditText editText_inventory_code;
    CheckBox checkBox_inventory_code;
    EditText editText_catalog_code;
    CheckBox checkBox_catalog_code;
    EditText editText_search_name;
    CheckBox checkBox_search_name;
    Spinner spinner_search_category;
    CheckBox checkBox_search_category;
    Spinner spinner_search_status;
    CheckBox checkBox_search_status;
    Spinner spinner_search_line;
    CheckBox checkBox_search_line;
    Spinner spinner_search_collection;
    CheckBox checkBox_search_collection;
    Spinner spinner_search_material;
    CheckBox checkBox_search_material;
    Spinner spinner_search_color;
    CheckBox checkBox_search_color;
    EditText editText_price_1;
    EditText editText_price_2;
    CheckBox checkBox_search_price;
    EditText editText_size_1;
    EditText editText_size_2;
    CheckBox checkBox_search_size;

    EditText editText_carat_1;
    EditText editText_carat_2;
    CheckBox checkBox_search_carat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_inventory);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        showProgress(true);
        whereClause = INI_WHERE;
        sortBy = " order by " + Utils.ENTRY_DATE + " desc";
        new FetchCollection().execute();
        new FetchLines().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.action_add_inventory) {
            startActivity(new Intent(this, AddInventoryActivity.class));
        }
        if (id == R.id.action_search_inv) {
            showSearchDialog();
        }
        if (id == R.id.action_select_mul) {
            selectMultiple();
        }
        if (id == R.id.action_select_single) {
            selectSingle();
        }
        if (id == R.id.action_distribute) {
            distribute(Utils.TAKE_OUT);
        }
        if (id == R.id.action_return) {
            distribute(Utils.RETURN);
        }
        if (id == R.id.action_sold) {
            distribute(Utils.SOLD);
        }
        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        if (id == R.id.action_switch_view) {
            isSwitched = !isSwitched;
            if (alert != null) {
                alert.dismiss();
            }
            InventoryActivity.this.setListAdapter(new InventoryListAdapter(
                    InventoryActivity.this, invList, isMultiple, isSwitched));
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

    protected void onListItemClick(ListView l, View v, int position, long id) {
        String[] object = (String[]) getListAdapter().getItem(position);
        Intent intent = new Intent(this, ViewInventoryActivity.class);
        intent.putExtra(Utils.ID, object[0]);
        startActivity(intent);

    }

    public void showSearchDialog() {
//        Log.e("search d", "search dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle(getString(R.string.search));
        dialog = inflater.inflate(R.layout.dialog_search_inventory, null);

        editText_inventory_code = dialog.findViewById(R.id.search_invent_code);
        checkBox_inventory_code = dialog.findViewById(R.id.inventoryCodeCheck);
        editText_catalog_code = dialog.findViewById(R.id.search_catalog_code);
        checkBox_catalog_code = dialog.findViewById(R.id.catCodeCheck);
        editText_search_name = dialog.findViewById(R.id.search_name);
        checkBox_search_name = dialog.findViewById(R.id.markCheck);

        spinner_search_category = dialog.findViewById(R.id.search_category);
        spinner_search_category.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting(Utils.ARTICLE_TYPE)));
        checkBox_search_category = dialog.findViewById(R.id.catCheck);


        spinner_search_status = dialog.findViewById(R.id.search_status);
        spinner_search_status.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting(Utils.INV_STT)));

        checkBox_search_status = dialog.findViewById(R.id.statusCheck);

        spinner_search_material = dialog.findViewById(R.id.search_material);
        spinner_search_material.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting("MaterialType")));
        checkBox_search_material = dialog.findViewById(R.id.materialCheck);


        spinner_search_color = dialog.findViewById(R.id.search_color);
        spinner_search_color.setAdapter(ArrayAdapter.createFromResource(this,
                R.array.metal_array,
                android.R.layout.simple_list_item_1));

        checkBox_search_color = dialog.findViewById(R.id.colorCheck);

        spinner_search_line = dialog.findViewById(R.id.search_line);
        spinner_search_line.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, lines));
        spinner_search_collection = dialog.findViewById(R.id.search_collection);
        spinner_search_collection.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, collections));

        checkBox_search_line = dialog.findViewById(R.id.lineCheck);
        checkBox_search_collection = dialog.findViewById(R.id.collectionCheck);

        editText_price_1 = dialog.findViewById(R.id.search_price);
        editText_price_2 = dialog.findViewById(R.id.search_price2);
        checkBox_search_price = dialog.findViewById(R.id.priceCheck);

        editText_size_1 = dialog.findViewById(R.id.search_size);
        editText_size_2 = dialog.findViewById(R.id.search_size2);
        checkBox_search_size = dialog.findViewById(R.id.sizeCheck);

        editText_carat_1 = dialog.findViewById(R.id.search_carat);
        editText_carat_2 = dialog.findViewById(R.id.search_carat2);
        checkBox_search_carat = dialog.findViewById(R.id.caratCheck);


        Log.e("last items", lastSearchingItems.toString());

        for (SearchingItem item : lastSearchingItems) {
            switch (item.type) {
                case INVENTORY: {
                    editText_inventory_code.setText(item.value);
                    checkBox_inventory_code.setChecked(true);
                    break;
                }

                case CATALOG: {
                    editText_catalog_code.setText(item.value);
                    checkBox_catalog_code.setChecked(true);
                    break;
                }
                case NAME: {
                    editText_search_name.setText(item.value);
                    checkBox_search_name.setChecked(true);
                    break;
                }
                case CATEGORY: {
                    spinner_search_category.setSelection(Integer.parseInt(item.value));
                    checkBox_search_category.setChecked(true);
                    break;
                }
                case STATUS: {
                    spinner_search_status.setSelection(Integer.parseInt(item.value));
                    checkBox_search_status.setChecked(true);
                    break;
                }
                case LINE: {
                    spinner_search_line.setSelection(Integer.parseInt(item.value));
                    checkBox_search_line.setChecked(true);
                    break;
                }
                case COLLECTION: {
                    spinner_search_collection.setSelection(Integer.parseInt(item.value));
                    checkBox_search_collection.setChecked(true);
                    break;
                }
                case MATERIAL: {
                    spinner_search_material.setSelection(Integer.parseInt(item.value));
                    checkBox_search_material.setChecked(true);
                    break;
                }
                case COLOR: {
                    spinner_search_color.setSelection(Integer.parseInt(item.value));
                    checkBox_search_color.setChecked(true);
                    break;
                }
                case PRICE: {
                    editText_price_1.setText(item.value.split("~")[0]);
                    editText_price_2.setText(item.value.split("~")[1]);
                    checkBox_search_price.setChecked(true);
                    break;
                }
                case SIZE: {
                    editText_size_1.setText(item.value.split("~")[0]);
                    editText_size_2.setText(item.value.split("~")[1]);
                    checkBox_search_size.setChecked(true);
                    break;
                }
                case CARAT: {
                    editText_carat_1.setText(item.value.split("~")[0]);
                    editText_carat_2.setText(item.value.split("~")[1]);
                    checkBox_search_carat.setChecked(true);
                    break;
                }
            }
        }

        builder.setView(dialog);
        builder.setCancelable(true);
        alert = builder.create();
        alert.show();
    }

    public void searchInventory(View v) {
        lastSearchingItems.clear();
        StringBuilder filter = new StringBuilder(" where ");
        if (checkBox_inventory_code.isChecked()) {
            lastSearchingItems.add(new SearchingItem(SearchingType.INVENTORY, editText_inventory_code.getText().toString(), true));
            filter.append(" Inventory.InventoryCode like '%").append(editText_inventory_code.getText().toString()).append("%' ");
        } else if (checkBox_catalog_code.isChecked()) {
            lastSearchingItems.add(new SearchingItem(SearchingType.CATALOG, editText_catalog_code.getText().toString(), true));
            filter.append(" CatalogCode like '%").append(editText_catalog_code.getText().toString()).append("%' ");
        }
        if (checkBox_search_name.isChecked()) {
            lastSearchingItems.add(new SearchingItem(SearchingType.NAME, editText_search_name.getText().toString(), true));

            if (filter.toString().equals(" where ")) {
                filter.append(" Name like '%").append(editText_search_name.getText().toString()).append("%'");

            } else {
                filter.append(" and Name like '%").append(editText_search_name.getText().toString()).append("%'");

            }
        }
        if (checkBox_search_category.isChecked()) {
            lastSearchingItems.add(new SearchingItem(SearchingType.CATEGORY, String.valueOf(spinner_search_category.getSelectedItemPosition()), true));

            if (filter.toString().equals(" where ")) {
                filter.append(" Category = ").append(Utils.escape(spinner_search_category.getSelectedItem().toString()));

            } else {
                filter.append(" and Category = ").append(Utils.escape(spinner_search_category.getSelectedItem().toString()));

            }
        }
        if (checkBox_search_status.isChecked()) {
            lastSearchingItems.add(new SearchingItem(SearchingType.STATUS, String.valueOf(spinner_search_status.getSelectedItemPosition()), true));

            if (filter.toString().equals(" where ")) {
                filter.append(" Status = ").append(Utils.escape(spinner_search_status.getSelectedItem().toString()));

            } else {
                filter.append(" and Status = ").append(Utils.escape(spinner_search_status.getSelectedItem().toString()));

            }
        }
        if (checkBox_search_material.isChecked()) {
            lastSearchingItems.add(new SearchingItem(SearchingType.MATERIAL, String.valueOf(spinner_search_material.getSelectedItemPosition()), true));

            if (filter.toString().equals(" where ")) {
                filter.append(" Material = ").append(Utils.escape(spinner_search_material.getSelectedItem().toString()));

            } else {
                filter.append(" and Material = ").append(Utils.escape(spinner_search_material.getSelectedItem().toString()));

            }
        }
        if (checkBox_search_size.isChecked()) {
            lastSearchingItems.add(new SearchingItem(SearchingType.SIZE, editText_size_1.getText().toString() + "~" + editText_size_2.getText().toString(), true));

            if (filter.toString().equals(" where ")) {
                filter.append(" Size between  ").append(Utils.escape(editText_size_1.getText().toString())).append(" and ")
                        .append(Utils.escape(editText_size_2.getText().toString())).append(" ");

            } else {
                filter.append(" and Size between  ").append(Utils.escape(editText_size_1.getText().toString())).append(" and ")
                        .append(Utils.escape(editText_size_2.getText().toString())).append(" ");
            }
        }
        if (checkBox_search_carat.isChecked()) {
            lastSearchingItems.add(new SearchingItem(SearchingType.CARAT, editText_carat_1.getText().toString() + "~" + editText_carat_2.getText().toString(), true));

            if (filter.toString().equals(" where ")) {
                filter.append(" Carat like '%").append(editText_carat_1.getText().toString()).append("%'");
            } else {
                filter.append(" and  Carat like '%").append(editText_carat_1.getText().toString()).append("%'");

            }
        }
        if (checkBox_search_color.isChecked()) {
            lastSearchingItems.add(new SearchingItem(SearchingType.COLOR, String.valueOf(spinner_search_color.getSelectedItemPosition()), true));

            if (filter.toString().equals(" where ")) {
                filter.append(" Color like '%").append(spinner_search_color.getSelectedItem().toString()).append("%' ");
            } else {
                filter.append(" and Color like '%").append(spinner_search_color.getSelectedItem().toString()).append("%' ");

            }
        }
        if (checkBox_search_price.isChecked()) {
            lastSearchingItems.add(new SearchingItem(SearchingType.PRICE, editText_price_1.getText().toString() + "~" + editText_price_2.getText().toString(), true));

            if (filter.toString().equals(" where ")) {
                filter.append(" Price between  ").append(editText_price_1.getText().toString()).append(" and ")
                        .append(editText_price_2.getText().toString()).append(" ");

            } else {
                filter.append(" and Price between  ").append(editText_price_1.getText().toString()).append(" and ")
                        .append(editText_price_2.getText().toString()).append(" ");
            }
        }
        if (checkBox_search_line.isChecked()) {
            lastSearchingItems.add(new SearchingItem(SearchingType.LINE, String.valueOf(spinner_search_line.getSelectedItemPosition()), true));

            if (filter.toString().equals(" where ")) {
                filter.append(" Line = ").append(Utils.escape(spinner_search_line.getSelectedItem().toString()));

            } else {
                filter.append(" and Line = ").append(Utils.escape(spinner_search_line.getSelectedItem().toString()));
            }
        }
        if (checkBox_search_collection.isChecked()) {
            lastSearchingItems.add(new SearchingItem(SearchingType.COLLECTION, String.valueOf(spinner_search_collection.getSelectedItemPosition()), true));

            if (filter.toString().equals(" where ")) {
                filter.append(" Collection = ").append(Utils.escape(spinner_search_collection.getSelectedItem().toString()));

            } else {
                filter.append(" and Collection = ").append(Utils.escape(spinner_search_collection.getSelectedItem().toString()));
            }
        }
        //TODO filtre colored or diams
        if (!filter.toString().equals(" where ")) {
            whereClause = filter.toString();
        }
        new ListInventory().execute();
    }

    public void sort(View v) {
        sortBy = " order by " + v.getTag() + " desc";
        Toast.makeText(this, getText(R.string.sorting), Toast.LENGTH_SHORT).show();
        new ListInventory().execute();
    }

    public void selectMultiple() {
        isMultiple = true;
        new ListInventory().execute();
    }

    public void selectSingle() {
        isMultiple = false;
        new ListInventory().execute();
    }

    public void distribute(String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle(action);
        final View view = inflater.inflate(R.layout.line_add_assign, null);
        Calendar c = Calendar.getInstance();
        ((EditText) view.findViewById(R.id.date)).setText(Utils
                .shortDateForDisplay(c.getTime()));
        view.findViewById(R.id.quantity).setVisibility(View.GONE);
        ((EditText) view.findViewById(R.id.status)).setText(action);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.save),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String assign = ((EditText) view
                                .findViewById(R.id.assign)).getText()
                                .toString();
                        String quantity = ((EditText) view
                                .findViewById(R.id.quantity)).getText()
                                .toString();
                        String date = ((EditText) view.findViewById(R.id.date))
                                .getText().toString();
                        String status = ((EditText) view
                                .findViewById(R.id.status)).getText()
                                .toString();
                        new AddAssign().execute(assign,
                                "1", date, status);
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

    class FetchLines extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            Query q = new Query("select distinct Line from Inventory");
            if (!q.execute()) {
                return false;
            }
            lines = new String[q.getRes().size()];
            for (int i = 0; i < q.getRes().size(); i++) {
                lines[i] = q.getRes().get(i).get("Line");
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            showSearchDialog();
        }
    }

    class FetchCollection extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            Query q = new Query("select distinct Collection from Inventory");
            if (!q.execute()) {
                return false;
            }
            collections = new String[q.getRes().size()];
            for (int i = 0; i < q.getRes().size(); i++) {
                collections[i] = q.getRes().get(i).get("Collection");
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
//            showSearchDialog();
        }
    }

    class ListInventory extends MyAsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            invList = new ArrayList<String[]>();
            boolean success = false;
            if (args.length > 0) {
                String query = "select CatalogCode from Inventory where InventoryCode = " + Utils.escape(args[1]);
                Query q = new Query(query);
                success = q.execute();
                if (!success) {
                    return false;
                }
                Log.d("cat", "" + q.getRes());
                String catCode = "";
                if (q.getRes().size() > 0) {
                    catCode = q.getRes().get(0).get("CatalogCode");
                }
                query = "select Inventory.ID, Inventory.InventoryCode, Name, Category, Description,"
                        + " Quantity, EntryDate, Status, StockHistory.IDLocation, StockHistory.Action, " +
                        "StockHistory.HistoryDate, Image.FileName, Color, Material, Stone, CatalogCode, Price, Carat" +
                        "from Inventory LEFT JOIN StockHistory ON Inventory.InventoryCode = StockHistory.InventoryCode " +
                        "where CatalogCode = " + Utils.escape(catCode) + " " + sortBy;
                q = new Query(query);
                success = q.execute();
                if (!success) {
                    return false;
                }
                ArrayList<Map<String, String>> result = q.getRes();
                result = FilterLocationData(q.getRes());
                if (result.size() == 0) {
                    result = q.getRes();
                }
                for (int i = 0; i < result.size(); i++) {
                    String[] line = new String[16];
                    line[0] = result.get(i).get(Utils.ID);
                    line[1] = result.get(i).get(Utils.INVT_CODE);
                    line[2] = result.get(i).get("Name");
                    line[3] = result.get(i).get(Utils.CATEGORY);
                    line[4] = result.get(i).get(Utils.DESCRIPTION);
                    line[5] = result.get(i).get("IDLocation");
                    line[6] = result.get(i).get(Utils.QUANTITY);
                    line[7] = result.get(i).get(Utils.ENTRY_DATE);
                    line[8] = result.get(i).get(Utils.STATUS);
                    line[9] = result.get(i).get("FileName");
                    line[10] = result.get(i).get("Color");
                    line[11] = result.get(i).get("Material");
                    line[12] = result.get(i).get("Stone");
                    line[13] = result.get(i).get("CatalogCode");
                    line[14] = result.get(i).get("Price");
                    line[15] = result.get(i).get("Carat");
                    invList.add(line);
                }

            } else {
                String query = "select distinct Inventory.ID, Inventory.InventoryCode, Name, Category, Description,"
                        + " Quantity, EntryDate, Status, StockHistory.IDLocation, StockHistory.Action, " +
                        "StockHistory.HistoryDate, Image.FileName, Color, Material, Stone, CatalogCode, Price, Carat" +
                        " from Inventory  LEFT JOIN StockHistory ON Inventory.InventoryCode = StockHistory.InventoryCode"
                        + whereClause + sortBy;
                Query q = new Query(query);
                success = q.execute();
                if (!success) {
                    return false;
                }
                ArrayList<Map<String, String>> data = new ArrayList<>();
                ArrayList<Map<String, String>> res = q.getRes();
                if (!whereClause.equals(INI_WHERE) && data.size() <= 250) {
                    data = FilterLocationData(res);
                }
                Log.d("initial size", "" + res.size());
                if (data.size() == 0) {
                    Log.d("filter", "NOT FILTERED");
                    data = res;
                }
                for (int i = 0; i < data.size(); i++) {
                    String[] line = new String[16];
                    line[0] = data.get(i).get(Utils.ID);
                    line[1] = data.get(i).get(Utils.INVT_CODE);
                    line[2] = data.get(i).get("Name");
                    line[3] = data.get(i).get(Utils.CATEGORY);
                    line[4] = data.get(i).get(Utils.DESCRIPTION);
                    if (!whereClause.equals(INI_WHERE) && data.size() <= 500) {
                        line[5] = data.get(i).get("IDLocation");
                    } else {
                        line[5] = "";
                    }
                    line[6] = data.get(i).get(Utils.QUANTITY);
                    line[7] = data.get(i).get(Utils.ENTRY_DATE);
                    line[8] = data.get(i).get(Utils.STATUS);
                    line[9] = data.get(i).get("FileName");
                    line[10] = data.get(i).get("Color");
                    line[11] = data.get(i).get("Material");
                    line[12] = data.get(i).get("Stone");
                    line[13] = data.get(i).get("CatalogCode");
                    line[14] = data.get(i).get("Price");
                    line[15] = data.get(i).get("Carat");
                    invList.add(line);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                Toast.makeText(InventoryActivity.this, "Done", Toast.LENGTH_LONG).show();
                if (alert != null) {
                    alert.dismiss();
                }
                InventoryActivity.this.setListAdapter(new InventoryListAdapter(
                        InventoryActivity.this, invList, isMultiple, false));
            } else {
                Toast.makeText(InventoryActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }

    public ArrayList<Map<String, String>> FilterLocationData(ArrayList<Map<String, String>> res) {
        ArrayList<String> codes = new ArrayList<>();
        ArrayList<Map<String, String>> data = new ArrayList<>();
        for (int i = 0; i < res.size(); i++) {
            boolean transferred = false;
            Timestamp timestamp1 = new Timestamp(1);
            Timestamp timestamp2 = new Timestamp(0);
            //if the historic date is not empty
            if (!res.get(i).get("HistoryDate").equals("")) {
                //if the action is not empty
                if (res.get(i).get("Action") != null) {
                    //we check if it is transferred
                    if (res.get(i).get("Action").equals("Transferred")) {
                        transferred = true;
                    }
                }
                //if it is not transferred we do a check
                if (!transferred) {
                    for (int j = 0; j < res.size(); j++) {
                        if (res.get(i).get("InventoryCode").equals(res.get(j).get("InventoryCode"))) {
                            if (!res.get(j).get("HistoryDate").equals("")) {
                                if (res.get(j).get("Action") != null) {
                                    if (res.get(j).get("Action").equals("Transferred")) {
                                        try {
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                            Date parsedDate = dateFormat.parse(res.get(i).get("HistoryDate"));
                                            timestamp1 = new java.sql.Timestamp(parsedDate.getTime());
                                        } catch (Exception e) {
                                            Log.d("error 1", "" + e);
                                        }
                                        try {
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                            Date parsedDate = dateFormat.parse(res.get(j).get("HistoryDate"));
                                            timestamp2 = new java.sql.Timestamp(parsedDate.getTime());
                                        } catch (Exception e) {
                                            Log.d("error 2", "" + e);
                                        }
                                        if (timestamp2.getTime() > timestamp1.getTime()) {
                                            Log.d("t1t2", "code " + res.get(j).get("InventoryCode") + " t1 " + timestamp1.getTime() + " t2 " + timestamp2.getTime());
                                            transferred = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Log.d("OK", "" + res.get(i).get("InventoryCode") + " " + transferred);
                if (!codes.contains(res.get(i).get("InventoryCode")) && !transferred) {
                    codes.add(res.get(i).get("InventoryCode"));
                    Log.e("in the loop", codes + " " + res.get(i).get("InventoryCode"));
                    data.add(res.get(i));
                }
            }
            //we add if the historic date is empty
            else {
                data.add(res.get(i));
            }
        }
        Log.e("size of data", data.size()+"");
        return data;
    }

    class AddAssign extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            ArrayList<String> iDList = ((InventoryListAdapter) getListAdapter()).getCheckedIDs();
            for (String id : iDList) {
                String query = "insert into InventoryDistribution (InventoryID,AssignedTo,Quantity,AssignDate, Status)"
                        + "values(" + id // inventory id
                        + "," + Utils.escape(args[0]) // assign to
                        + "," + args[1] // quantity
                        + "," + Utils.escape(Utils.shortDateForInsert(args[2])) // date
                        + "," + Utils.escape(args[3]) // status
                        + ")";
                Query q = new Query(query);
                if (!q.execute())
                    return false;
                if (args[3].equals(Utils.TAKE_OUT))
                    query = "update Inventory set Quantity = Quantity - " + args[1] + ", Status = 'Sorti' where ID =" + id;
                else if (args[3].equals(Utils.RETURN))
                    query = "update Inventory set Quantity = Quantity + " + args[1] + ", Status = 'Stock' where ID =" + id;
                else if (args[3].equals(Utils.SOLD))
                    query = "update Inventory set Quantity = Quantity + " + args[1] + ", Status = 'Vendu' where ID =" + id;
                q = new Query(query);
                if (!q.execute())
                    return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                InventoryActivity.this.recreate();
            } else {
                Toast.makeText(InventoryActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
