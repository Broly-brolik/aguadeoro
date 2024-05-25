package com.aguadeoro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class EditStockActivity extends ListActivity {
    private View wheelView;
    private View mainView;
    Boolean isMissing;
    int stock, check, missing, selectedLocationId;
    private String lastDate, selectedLocation, selectedLine;
    String[] locationID, categories, lines;
    ArrayList<Map<String, String>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stock);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        showProgress(true);
        new PopulateSpinner().execute();

        Button refresh = findViewById(R.id.refresh);
        final Spinner locations = findViewById(R.id.location_spinner);
        final Spinner category = findViewById(R.id.category_spinner);
        final Spinner lines = findViewById(R.id.line_spinner);
        final CheckBox missing = findViewById(R.id.missingCheckBox);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedCategory = category.getSelectedItem().toString();
                isMissing = missing.isChecked();
                if (isMissing) {
                    locations.setSelection(0);
                }
                selectedLocation = locations.getSelectedItem().toString();
                try {
                    selectedLocationId = Integer.parseInt(selectedLocation.replaceAll("[^0-9]", ""));
                } catch (NumberFormatException e) {
                    selectedLocationId = 0;
                }

                selectedLine = lines.getSelectedItem().toString();
                new FetchData().execute("" + selectedLocationId, selectedCategory, selectedLine);
            }
        });


    }

    class FetchData extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String query = "";
            Query q;
            boolean s;
            String lineFilter = "";
            if (!params[2].equals("ALL")) {
                lineFilter = " and Collection = '" + params[2] + "' ";
            }
            if (!isMissing) {
                Log.d("-----", "" + params[0] + " " + params[1]);
                if (params[0].equals("0") && !params[1].equals("ALL")) {
                    query = "select * FROM StockHistory left JOIN Inventory ON StockHistory.InventoryCode = Inventory.InventoryCode" + " where Inventory.Category = " + Utils.escape(params[1]) + " and (Status='Stock' or Status='Check'" + ") " + lineFilter + " order by StockHistory.InventoryCode asc;";

                } else if (params[1].equals("ALL") && !params[0].equals("0")) {
                    query = "select * FROM StockHistory left JOIN Inventory ON StockHistory.InventoryCode = Inventory.InventoryCode" + " where IDLocation = " + params[0] + " and (Status='Stock' or Status='Check'" + ") " + lineFilter + " order by StockHistory.InventoryCode asc;";

                } else if (params[0].equals("0") && params[1].equals("ALL")) {
                    query = "select * FROM StockHistory left JOIN Inventory ON StockHistory.InventoryCode = Inventory.InventoryCode" + " where (Status='Stock' or Status='Check') " + lineFilter + " order by StockHistory.InventoryCode asc;";

                } else {
                    query = "select * FROM StockHistory left JOIN Inventory ON StockHistory.InventoryCode = Inventory.InventoryCode" + " where Inventory.Category = " + Utils.escape(params[1]) + " and IDLocation = " + params[0] + " and (Status='Stock' or Status='Check'" + ") " + lineFilter + " order by StockHistory.InventoryCode asc;";
                }
            } else {
                if (params[1].equals("ALL")) {
                    query = "select * from Inventory where InventoryCode not in (select InventoryCode from StockHistory where InventoryCode is not null) and " + "(Status='Stock' or Status='Check')";

                } else {
                    query = "select * from Inventory where InventoryCode not in (select InventoryCode from StockHistory where InventoryCode is not null) and" + " (Status='Stock' or Status='Check') and Category = " + Utils.escape(params[1]);

                }
            }
            q = new Query(query);
            s = q.execute();
            if (!s) {
                return false;
            }
            ArrayList<Map<String, String>> res = q.getRes();
            Log.d("initial size", "" + res.size());
            if (!isMissing) {
                data = FilterLocationData(res);
            } else {
                data = res;
            }
            Log.d("final size", "" + data.size());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            ListView lv = EditStockActivity.this.findViewById(android.R.id.list);
            EditStockActivity.StockAdapter sa = new EditStockActivity.StockAdapter(EditStockActivity.this, data);
            lv.setAdapter(sa);
            check = 0;
            stock = 0;
            missing = 0;
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).get("Status").equals("Check")) {
                    check += 1;
                } else if (data.get(i).get("Status").equals("Stock")) {
                    stock += 1;
                }
                if (isMissing) {
                    missing += 1;
                }
            }
            ((TextView) findViewById(R.id.missings)).setText("" + missing);
            ((TextView) findViewById(R.id.stocks)).setText("" + stock);
            ((TextView) findViewById(R.id.checks)).setText("" + check);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final Map<String, String> selectedItem = (Map<String, String>) adapterView.getItemAtPosition(i);
                    final Dialog dialog = new Dialog(EditStockActivity.this);
                    if (selectedItem.get("IDLocation") == null) {
                        dialog.setTitle("Assign Location");
                    } else {
                        dialog.setTitle("Change Location");
                    }
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_editstock, null);
                    dialog.setContentView(dialogView);
                    Button confirm = dialogView.findViewById(R.id.changeLocation);
                    final Spinner locations = dialogView.findViewById(R.id.locations);
                    final Spinner doneBY = dialogView.findViewById(R.id.doneBy);
                    ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter(EditStockActivity.this, android.R.layout.simple_spinner_item, locationID);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    locations.setAdapter(adapter2);
                    adapter2 = ArrayAdapter.createFromResource(EditStockActivity.this, R.array.regby_array, android.R.layout.simple_spinner_item);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    doneBY.setAdapter(adapter2);
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            java.util.Date utilDate = new java.util.Date();
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(utilDate);
                            cal.set(Calendar.MILLISECOND, 0);
                            Log.d("time", String.valueOf(new java.sql.Timestamp(cal.getTimeInMillis())));
                            String time = String.valueOf(new java.sql.Timestamp(cal.getTimeInMillis()));
                            time = time.substring(0, time.length() - 2);
                            String time2 = String.valueOf(new java.sql.Timestamp(cal.getTimeInMillis() - 1000));
                            time2 = time2.substring(0, time2.length() - 2);
                            String[] actions = Utils.getSetSetting("InventoryAction");
                            int selectedId = Integer.parseInt(locations.getSelectedItem().toString().replaceAll("[^0-9]", ""));
                            if (selectedItem.get("IDLocation") == null) {
                                String query = "insert into StockHistory (HistoryDate, InventoryCode, IDLocation, Notes, DoneBy, Action) values (#" + time + "#, " + Utils.escape(selectedItem.get("InventoryCode")) + "" + ", " + selectedId + ", '', " + Utils.escape(doneBY.getSelectedItem().toString()) + ", " + Utils.escape(actions[4]) + ")";
                                Query q = new Query(query);
                                boolean s = q.execute();
                                if (!s) {
                                    Toast.makeText(EditStockActivity.this, "Error", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            } else {
                                String query = "insert into StockHistory (HistoryDate, InventoryCode, IDLocation, Notes, DoneBy, Action) values (#" + time + "#, " + Utils.escape(selectedItem.get("InventoryCode")) + "" + ", " + selectedId + ", '', " + Utils.escape(doneBY.getSelectedItem().toString()) + ", " + Utils.escape(actions[4]) + ")";
                                Query q = new Query(query);
                                boolean s = q.execute();
                                if (!s) {
                                    Toast.makeText(EditStockActivity.this, "Error", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                                query = "insert into StockHistory (HistoryDate, InventoryCode, IDLocation, Notes, DoneBy, Action) values (#" + time2 + "#, " + Utils.escape(selectedItem.get("InventoryCode")) + "" + ", " + selectedItem.get("IDLocation").replaceAll("[^0-9]", "") + ", '', " + Utils.escape(doneBY.getSelectedItem().toString()) + ", " + Utils.escape(actions[0]) + ")";
                                q = new Query(query);
                                s = q.execute();
                                if (!s) {
                                    Toast.makeText(EditStockActivity.this, "Error", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }

                            Log.d("test", Arrays.toString(Utils.getSetSetting("InventoryAction")));
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    public ArrayList<Map<String, String>> FilterLocationData(ArrayList<Map<String, String>> res) {
        ArrayList<String> codes = new ArrayList<>();
        ArrayList<Map<String, String>> data = new ArrayList<>();
        for (int i = 0; i < res.size(); i++) {
            boolean transferred = false;
            Timestamp timestamp1 = new Timestamp(1);
            Timestamp timestamp2 = new Timestamp(0);
            if (res.get(i).get("HistoryDate") != null && !res.get(i).get("HistoryDate").equals("")) {
                if (res.get(i).get("Action") != null) {
                    if (res.get(i).get("Action").equals("Transferred")) {
                        transferred = true;
                    }
                }
                if (!transferred) {
                    for (int j = 0; j < res.size(); j++) {
                        if (i != j && res.get(i).get("InventoryCode").equals(res.get(j).get("InventoryCode"))) {
                            if (!res.get(j).get("HistoryDate").equals("")) {
                                if (res.get(j).get("Action") != null) {
                                    if (res.get(j).get("Action").equals("Transferred")) {
                                        try {
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            Date parsedDate = dateFormat.parse(res.get(i).get("HistoryDate"));
                                            timestamp1 = new java.sql.Timestamp(parsedDate.getTime());
                                        } catch (Exception e) {
                                            Log.d("error 1", "" + e);
                                        }
                                        try {
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            Date parsedDate = dateFormat.parse(res.get(j).get("HistoryDate"));
                                            timestamp2 = new java.sql.Timestamp(parsedDate.getTime());
                                        } catch (Exception e) {
                                            Log.d("error 2", "" + e);
                                        }
                                        if (timestamp2.getTime() > timestamp1.getTime()) {
                                            transferred = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (!codes.contains(res.get(i).get("InventoryCode")) && !transferred) {
                    res.get(i).get("InventoryCode");
                    codes.add(res.get(i).get("InventoryCode"));
                    data.add(res.get(i));
                }
            }
        }
        return data;
    }


    class StockAdapter extends ArrayAdapter<Map<String, String>> {

        ArrayList<Map<String, String>> data;
        Context context;

        public StockAdapter(Context context, ArrayList<Map<String, String>> data) {
            super(context, R.layout.line_edit_stock, data);
            this.data = data;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.line_edit_stock, container, false);
            }
            String filename;
            filename = (data.get(position).get(Utils.IMAGE));
            if (!filename.isEmpty()) {
                ImageView img = convertView.findViewById(R.id.picture);
                Picasso.with(context).load("http://195.15.223.234/aguadeoro/06_inventory toc opy/" + filename).placeholder(R.drawable.logo_small).into(img);
            }
            ((TextView) convertView.findViewById(R.id.inventoryCode)).setText(data.get(position).get("InventoryCode"));
            ((TextView) convertView.findViewById(R.id.catalogueCode)).setText(data.get(position).get("CatalogCode"));
            ((TextView) convertView.findViewById(R.id.description)).setText(data.get(position).get("Description"));
            if (!selectedLocation.equals("ALL")) {
                ((TextView) convertView.findViewById(R.id.location)).setText("Location : " + selectedLocation);
            } else if (data.get(position).get("IDLocation") != null) {
                ((TextView) convertView.findViewById(R.id.location)).setText("Location : " + data.get(position).get("IDLocation"));

            }

            return convertView;
        }
    }

    class PopulateSpinner extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String query = "select * from StockLocation order by ID";
            Query q = new Query(query);
            boolean s = q.execute();
            if (!s) {
                return false;
            }
            ArrayList<Map<String, String>> res = q.getRes();
            locationID = new String[res.size()];
            for (int i = 0; i < res.size(); i++) {
                locationID[i] = res.get(i).get("PlaceNumber");
            }
            query = "select OptionValue from OptionValues where Type = 'ArticleType'";
            q = new Query(query);
            s = q.execute();
            if (!s) {
                return false;
            }
            res = q.getRes();
            categories = new String[res.size()];
            for (int i = 0; i < res.size(); i++) {
                categories[i] = res.get(i).get("OptionValue");
            }
            locationID = add2BeginningOfArray(locationID, "ALL");
            categories = add2BeginningOfArray(categories, "ALL");
            query = "select top 1 HistoryDate from StockHistory order by HistoryDate desc";
            q = new Query(query);
            s = q.execute();
            if (!s) {
                return false;
            }
            lastDate = q.getRes().get(0).get("HistoryDate");

            query = "select distinct Collection from Inventory";
            q = new Query(query);
            s = q.execute();
            if (!s) {
                return false;
            }
            res = q.getRes();
            lines = new String[res.size()];
            for (int i = 0; i < res.size(); i++) {
                lines[i] = res.get(i).get("Collection");
            }
            lines = add2BeginningOfArray(lines, "ALL");
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Spinner locations = findViewById(R.id.location_spinner);
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(EditStockActivity.this, android.R.layout.simple_spinner_item, locationID);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            locations.setAdapter(adapter);
            Spinner category = findViewById(R.id.category_spinner);
            adapter = new ArrayAdapter(EditStockActivity.this, android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            category.setAdapter(adapter);
            Spinner linesSpinner = findViewById(R.id.line_spinner);
            adapter = new ArrayAdapter(EditStockActivity.this, android.R.layout.simple_spinner_item, lines);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            linesSpinner.setAdapter(adapter);
            showProgress(false);
        }
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            wheelView.setVisibility(View.VISIBLE);
            wheelView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    wheelView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            mainView.setVisibility(View.VISIBLE);
            mainView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mainView.setVisibility(show ? View.GONE : View.VISIBLE);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        if (id == android.R.id.home) {
            startActivity(new Intent(this, StockActivity.class));
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    public static <T> T[] add2BeginningOfArray(T[] elements, T element) {
        T[] newArray = Arrays.copyOf(elements, elements.length + 1);
        newArray[0] = element;
        System.arraycopy(elements, 0, newArray, 1, elements.length);

        return newArray;
    }
}
