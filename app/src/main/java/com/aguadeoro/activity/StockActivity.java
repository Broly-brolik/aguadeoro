package com.aguadeoro.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.aguadeoro.R;
import com.aguadeoro.adapter.StockAdapter;
import com.aguadeoro.models.LocationInventory;
import com.aguadeoro.threads.ConnectThread;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class StockActivity extends ListActivity {

    private View wheelView;
    private View mainView;
    private String[] locationID, products, dates, locationImages;
    ArrayList<String> locationDescription;
    ArrayList<Map<String, String>> notes;
    Map<String, String> previousLocs = new HashMap();
    Set<String> detectedInventoryCodes = new HashSet<>();
    Set<String> detectedRFID = new HashSet<>();
    Set<String> previousRFID = new HashSet<>();
    private Activity acv;
    private String location;
    private int locationId;
    private ArrayList<Map<String, String>> checkedIDs, data;
    private String doneBy;
    private boolean isCheckAll = false;
    boolean previous = false;
    boolean scanStarted = false;

    ConnectThread connectThread = null;

    Timer timer = new Timer();

    boolean readerConnected = false;

    Button buttonStartScan = null;

    Set<String> inventoryCodes = new HashSet<>();

    boolean inventoryScanStarted = false;

    HashMap<Integer, LocationInventory> locationInventoryList = new HashMap();

    Spinner locations;
    Spinner datesSpinner;
    Button check;
    Button checkAll;

    boolean scanItem = false;
    String currentItem = "";

    public void showDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_summary_scan_location, null);
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogView);
        dialog.show();

//        if (detectedInventoryCodes.containsAll(inventoryCodes)) {
//            Toast.makeText(getApplicationContext(), "All items were detected", Toast.LENGTH_LONG).show();
//        } else {
//            Set<String> missingCodes = new HashSet<>(inventoryCodes);
//            missingCodes.removeAll(detectedInventoryCodes);
//            Toast.makeText(getApplicationContext(), "missing: " + missingCodes.toString(), Toast.LENGTH_LONG).show();
//        }
    }


    public void showDialogScannedItem(String filename, String inventoryCode, String catalog, String name, String stone, String location) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_scanned_item, null);
        ImageView image = dialogView.findViewById(R.id.imageViewScannedItem);
        //Picasso.with(context).load(path).placeholder(R.drawable.logo_small).into(img);
        Picasso.with(getApplicationContext()).load("http://195.15.223.234/aguadeoro/06_inventory toc opy/" + filename).placeholder(R.drawable.logo_small).into(image);

        TextView textViewInventory = dialogView.findViewById(R.id.inventoryCodeScannedItem);
        textViewInventory.setText("Inventory code: " + inventoryCode);

        TextView textViewCatalog = dialogView.findViewById(R.id.catalogCodeScannedItem);
        textViewCatalog.setText("Catalog code: " + catalog);

        TextView textViewName = dialogView.findViewById(R.id.nameScannedItem);
        textViewName.setText("Name: " + name);

        TextView textViewStone = dialogView.findViewById(R.id.stoneScannedItem);
        textViewStone.setText("Stone: " + stone);

        TextView textViewLocation = dialogView.findViewById(R.id.locationScannedItem);
        textViewLocation.setText("Location: " + location);


        final Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogView);
        dialog.show();
    }

    public void launchDetailPage(String id) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, ViewInventoryActivity.class);
        intent.putExtra(Utils.ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
        setContentView(R.layout.activity_stock);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        acv = this;
        showProgress(true);
        new PopulateSpinner().execute();
        locations = findViewById(R.id.location_spinner);
        datesSpinner = findViewById(R.id.spinner_date);
        check = findViewById(R.id.confirm_location);
        final TextView description = findViewById(R.id.description);
        checkAll = findViewById(R.id.checkAll);
        buttonStartScan = findViewById(R.id.buttonScan);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("from")) {
                previous = true;
            }
        }
        locations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!locations.getSelectedItem().toString().equals("")) {
                    showProgress(true);
                    int selectedItemPos = Integer.parseInt(locations.getSelectedItem().toString().replaceAll("[^0-9]", ""));
                    Log.e("pos", selectedItemPos + "");
                    description.setText(locationDescription.get(i));
                    new FetchLocationData().execute(selectedItemPos);
                    location = locations.getSelectedItem().toString();
                    locationId = selectedItemPos;
                    detectedRFID = new HashSet<>();
                    previousRFID = new HashSet<>();
                    detectedInventoryCodes = new HashSet<>();
                    new PopulateDateSpinner().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        datesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                new FetchQuantities().execute(String.valueOf(locationId), datesSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCheckAll = !isCheckAll;
                StockAdapter stockAdapter = new StockAdapter(StockActivity.this, data, notes, isCheckAll, previousLocs, scanStarted, detectedInventoryCodes);
                StockActivity.this.setListAdapter(stockAdapter);
            }
        });

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(StockActivity.this);
                dialog.setTitle("Location picture");
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_stock_location, null);
                dialog.setContentView(dialogView);
                /*String filename = locationImages[location - 1];
                Log.d("test", "test " + filename);
                StringBuilder tempFileName = new StringBuilder();
                for (int i = 0; i < filename.length(); i++) {
                    if (filename.charAt(i) == ';') {
                        break;
                    } else {
                        tempFileName.append(filename.charAt(i));
                    }
                }
                filename = tempFileName.toString();

                if (filename != null && !filename.isEmpty()) {
                    File path = new File(Environment.getExternalStorageDirectory()
                            + "/07_locations/" + filename);
                    Log.d("test", "" + path.getPath());
                    ImageView img = dialogView.findViewById(R.id.locationImage);
                    Picasso.with(dialog.getContext()).load(path).placeholder(R.drawable.logo_small).into(img);
                    //check all needed permissions together

                    //img.setImageURI(Uri.fromFile(path));
                }*/
                Button close = dialogView.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView list = StockActivity.this.findViewById(android.R.id.list);
                checkedIDs = ((StockAdapter) list.getAdapter()).getCheckedIDs();
                final Dialog dialog = new Dialog(StockActivity.this);
                dialog.setTitle("Stock confirmation");
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_stock, null);
                ListView lv2 = dialogView.findViewById(R.id.list);
                StockAdapter2 sa = new StockAdapter2(StockActivity.this, checkedIDs);
                lv2.setAdapter(sa);
                dialog.setContentView(dialogView);
                Button confirm = dialogView.findViewById(R.id.confirm);
                final Spinner regBy = dialogView.findViewById(R.id.checkedBy);
                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(StockActivity.this, R.array.regby_array, android.R.layout.simple_spinner_item);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                regBy.setAdapter(adapter2);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] checkedCodes = new String[checkedIDs.size()];
                        Log.d("click", "click");
                        for (int i = 0; i < checkedIDs.size(); i++) {
                            checkedCodes[i] = checkedIDs.get(i).get("InventoryCode");
                        }
                        doneBy = regBy.getSelectedItem().toString();
                        Toast.makeText(StockActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
                        showProgress(true);
                        new sendData().execute(checkedCodes);
                    }
                });
                dialog.show();
            }
        });


        buttonStartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScanLocationHandler();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stock, menu);
        return true;
    }

    class sendData extends AsyncTask<String[], String, Boolean> {

        @Override
        protected Boolean doInBackground(String[]... params) {
            String query = "";
            Query q;
            boolean s;
            java.util.Date utilDate = new java.util.Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(utilDate);
            cal.set(Calendar.MILLISECOND, 0);
            Log.d("time", String.valueOf(new java.sql.Timestamp(cal.getTimeInMillis())));
            String time = String.valueOf(new java.sql.Timestamp(cal.getTimeInMillis()));
            time = time.substring(0, time.length() - 2);
            for (int i = 0; i < params[0].length; i++) {
                query = "update Inventory set Status ='Check' where InventoryCode=" + Utils.escape(params[0][i]);
                q = new Query(query);
                s = q.execute();
                if (!s) {
                    return false;
                }
            }
            ArrayList<String> products2 = new ArrayList<>();
            String[] actions = Utils.getSetSetting("InventoryAction");
            for (int i = 0; i < data.size(); i++) {
                String action = "";
                String note = "";
                for (int i0 = 0; i0 < checkedIDs.size(); i0++) {
                    if (checkedIDs.get(i0).get("InventoryCode").equals(data.get(i).get("InventoryCode"))) {
                        if (Objects.requireNonNull(checkedIDs.get(i0).get("note")).length() > 0) {
                            note = Objects.requireNonNull(checkedIDs.get(i0).get("note"));
                        }
                        if (!products2.contains(checkedIDs.get(i0).get("InventoryCode"))) {
                            products2.add(checkedIDs.get(i0).get("InventoryCode"));
                            query = "insert into StockHistory (HistoryDate,InventoryCode,IDLocation,Notes,DoneBy,Action) values (#" + time + "#," + Utils.escape(checkedIDs.get(i0).get("InventoryCode")) + "," + locationId + "," + Utils.escape(note) + "," + Utils.escape(doneBy) + ", " + Utils.escape(actions[3]) + ")";
                            q = new Query(query);
                            s = q.execute();
                            if (!s) {
                                return false;
                            }
                        }
                    }
                }
            }
            for (int i0 = 0; i0 < data.size(); i0++) {
                if (products2.size() < 1) {
                    if (data.get(i0).get("note") == null) {
                        query = "insert into StockHistory (HistoryDate,InventoryCode,IDLocation,Notes,DoneBy, Action) values (#" + time + "#," + Utils.escape(data.get(i0).get("InventoryCode")) + "," + locationId + ",''," + Utils.escape(doneBy) + ", " + Utils.escape(actions[2]) + ")";
                    } else {
                        query = "insert into StockHistory (HistoryDate,InventoryCode,IDLocation,Notes,DoneBy, Action) values (#" + time + "#," + Utils.escape(data.get(i0).get("InventoryCode")) + "," + locationId + "," + Utils.escape(data.get(i0).get("note")) + "," + Utils.escape(doneBy) + ", " + Utils.escape(actions[2]) + ")";
                    }
                    q = new Query(query);
                    s = q.execute();
                    if (!s) {
                        return false;
                    }
                }
            }
            if (products2.size() > 0) {
                for (int i0 = 0; i0 < data.size(); i0++) {
                    if (!products2.contains(data.get(i0).get("InventoryCode"))) {
                        if (data.get(i0).get("note") == null) {
                            query = "insert into StockHistory (HistoryDate,InventoryCode,IDLocation,Notes,DoneBy,Action) values (#" + time + "#," + Utils.escape(data.get(i0).get("InventoryCode")) + "," + locationId + ",''," + Utils.escape(doneBy) + ", " + Utils.escape(actions[2]) + ")";
                        } else {
                            query = "insert into StockHistory (HistoryDate,InventoryCode,IDLocation,Notes,DoneBy,Action) values (#" + time + "#," + Utils.escape(data.get(i0).get("InventoryCode")) + "," + locationId + "," + Utils.escape(data.get(i0).get("note")) + "," + Utils.escape(doneBy) + ", " + Utils.escape(actions[2]) + ")";
                        }
                        q = new Query(query);
                        s = q.execute();
                        if (!s) {
                            return false;
                        }
                    }
                }
            }


            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            //gress(false);
            Toast.makeText(StockActivity.this, "Done", Toast.LENGTH_LONG).show();

        }
    }

    class StockAdapter2 extends ArrayAdapter<Map<String, String>> {

        ArrayList<Map<String, String>> data;

        public StockAdapter2(Context context, ArrayList<Map<String, String>> data) {
            super(context, R.layout.line_dialog_stock, data);
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.line_dialog_stock, container, false);
            }
            ((TextView) convertView.findViewById(R.id.inventoryCode)).setText(data.get(position).get("InventoryCode"));
            ((TextView) convertView.findViewById(R.id.note)).setText(data.get(position).get("note"));

            return convertView;
        }
    }

    public static <T> T[] add2BeginningOfArray(T[] elements, T element) {
        T[] newArray = Arrays.copyOf(elements, elements.length + 1);
        newArray[0] = element;
        System.arraycopy(elements, 0, newArray, 1, elements.length);

        return newArray;
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
            Log.e("res . size", res.size() + "");
            locationID = new String[res.size()];
            locationDescription = new ArrayList<String>();
            locationImages = new String[res.size()];
            for (int i = 0; i < res.size(); i++) {
                locationID[i] = res.get(i).get("PlaceNumber");
                locationDescription.add(res.get(i).get("Description"));
                locationImages[i] = res.get(i).get("Picture");
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            showProgress(false);
            if (locationID != null) {
                Spinner locations = findViewById(R.id.location_spinner);
                ArrayAdapter<CharSequence> adapter = new ArrayAdapter(StockActivity.this, android.R.layout.simple_spinner_item, locationID);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                locations.setAdapter(adapter);
            } else {
                Toast.makeText(StockActivity.this, "error could not fetch data", Toast.LENGTH_LONG).show();
            }
        }
    }

    class PopulateDateSpinner extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String query = "select distinct HistoryDate from StockHistory where IDLocation =" + locationId + " group by HistoryDate HAVING count(HistoryDate)>1 order by HistoryDate desc";
            Query q = new Query(query);
            boolean s = q.execute();
            if (!s) {
                return false;
            }
            ArrayList<Map<String, String>> res = q.getRes();
            Log.d("date", String.valueOf(res));
            dates = new String[res.size()];
            for (int i = 0; i < res.size(); i++) {
                dates[i] = res.get(i).get("HistoryDate");
            }
            dates = add2BeginningOfArray(dates, "Now");
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            showProgress(false);
            Spinner datesSpin = findViewById(R.id.spinner_date);
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(StockActivity.this, android.R.layout.simple_spinner_item, dates);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            datesSpin.setAdapter(adapter);
        }
    }

    class FetchQuantities extends AsyncTask<String, String, Boolean> {
        int stockQuantity, checkQuantity, total;
        String selectedDate;

        @Override
        protected Boolean doInBackground(String... params) {
            selectedDate = params[1];
            if (params[1].equals("Now")) {
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).get("Status").equals("Stock")) {
                        stockQuantity += 1;
                    } else if (data.get(i).get("Status").equals("Check")) {
                        checkQuantity += 1;
                    } else {
                        total += 1;
                    }
                }
                total += stockQuantity;
                total += checkQuantity;
            } else {
                String query = "select distinct StockHistory.InventoryCode FROM StockHistory INNER JOIN Inventory ON StockHistory.InventoryCode = Inventory.InventoryCode" + " where Status = 'Stock' and IDLocation = " + params[0] + " and StockHistory.HistoryDate = #" + params[1] + "# ;";
                Query q = new Query(query);
                boolean s = q.execute();
                if (!s) {
                    return false;
                }
                ArrayList<Map<String, String>> res = q.getRes();
                stockQuantity = res.size();
                Log.d("stock", String.valueOf(res));

                query = "select distinct StockHistory.InventoryCode FROM StockHistory INNER JOIN Inventory ON StockHistory.InventoryCode = Inventory.InventoryCode" + " where Status = 'Check' and IDLocation = " + params[0] + " and StockHistory.HistoryDate =#" + params[1] + "#;";
                q = new Query(query);
                s = q.execute();
                if (!s) {
                    return false;
                }
                res = q.getRes();
                checkQuantity = res.size();
                Log.d("check", String.valueOf(res));
                query = "select distinct StockHistory.InventoryCode FROM StockHistory INNER JOIN Inventory ON StockHistory.InventoryCode = Inventory.InventoryCode" + " where IDLocation = " + params[0] + " and StockHistory.HistoryDate =#" + params[1] + "#;";
                q = new Query(query);
                s = q.execute();
                if (!s) {
                    return false;
                }
                res = q.getRes();
                total = res.size();
                Log.d("check", String.valueOf(res));

            }
            return true;

        }

        @Override
        protected void onPostExecute(Boolean success) {
            TextView quant = findViewById(R.id.quantities);
            showProgress(false);
            if (selectedDate.equals("Now")) {
                quant.setText("Stock (Now) : " + stockQuantity + "\nCheck (Now) : " + checkQuantity + "\nTotal (Now) : " + (total));

            } else {
                quant.setText("Stock (Now) : " + stockQuantity + "\nCheck (Now) : " + checkQuantity + "\nTotal (" + selectedDate.substring(0, 10) + ") : " + (total));
            }
        }
    }

    class FetchLocationData extends AsyncTask<Integer, String, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
            String query;
            Query q;
            boolean s;
            query = "select * FROM StockHistory left JOIN Inventory ON StockHistory.InventoryCode = Inventory.InventoryCode" + " where IDLocation = " + params[0] + " and (Status='Stock' or Status='Check') order by Inventory.CatalogCode asc;";
            q = new Query(query);
            s = q.execute();
            if (!s) {
                return false;
            }
            ArrayList<Map<String, String>> res = q.getRes();
            Log.d("initial size", "" + res.size());
            data = FilterLocationData(res);
            inventoryCodes = data.stream().map(stringStringMap -> stringStringMap.getOrDefault("InventoryCode", "")).collect(Collectors.toSet());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (data == null) {
                return;
            }
            if (data.size() > 0) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        StockAdapter stockAdapter = new StockAdapter(StockActivity.this, data, notes, previousLocs, scanStarted, detectedInventoryCodes);
                        StockActivity.this.setListAdapter(stockAdapter);

                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(StockActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }

                };
                TedPermission.create()
                        .setPermissionListener(permissionlistener)
                        .setRationaleMessage("This app requires access to your storage to manage stock data.")
                        .setDeniedMessage("If you reject permission, you cannot use this service.\n\nPlease enable permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();


            } else {
                Toast.makeText(StockActivity.this, "Empty location", Toast.LENGTH_LONG).show();
                StockAdapter stockAdapter = new StockAdapter(StockActivity.this, new ArrayList(), notes, previousLocs, scanStarted, detectedInventoryCodes);
                StockActivity.this.setListAdapter(stockAdapter);
            }
        }
    }

    public ArrayList<Map<String, String>> FilterLocationData(ArrayList<Map<String, String>> res) {
        notes = new ArrayList<>();
        ArrayList<String> codes = new ArrayList<>();
        ArrayList<Map<String, String>> data = new ArrayList<>();
        for (int i = 0; i < res.size(); i++) {
            boolean transferred = false;
            Timestamp timestamp1 = new Timestamp(1);
            Timestamp timestamp2 = new Timestamp(0);
            if (!res.get(i).get("HistoryDate").equals("")) {
                if (res.get(i).get("Action") != null) {
                    if (res.get(i).get("Action").equals("Transferred")) {
                        transferred = true;
                    }
                }
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
                    HashMap<String, String> map = new HashMap<>();
                    map.put(res.get(i).get("InventoryCode"), res.get(i).get("Notes"));
                    codes.add(res.get(i).get("InventoryCode"));
                    data.add(res.get(i));
                    notes.add(map);
                }
            }
        }
//        Log.e("codes mec", Arrays.toString(codes.toArray()));
//        for (String code : codes) {
//            Query q = new Query(String.format("select * FROM StockHistory left JOIN Inventory ON StockHistory.InventoryCode = Inventory.InventoryCode" + " where StockHistory.InventoryCode = '%s' AND StockHistory.Action = 'Transferred' Order By StockHistory.HistoryDate Desc", code));
//            Boolean s = q.execute();
//            if (q.getRes().size() == 0) {
//                continue;
//            }
//
//            String previousLoc = q.getRes().get(0).getOrDefault("IDLocation", "");
//            if (!previousLoc.isEmpty()) {
//                previousLocs.put(code, previousLoc);
//            }
////            Log.e("previous loc", q.getRes().get(0).getOrDefault("IDLocation", ""));
//
//        }
        Log.e("previous locss", previousLocs.toString());
        return data;
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
            if (previous) {
                finish();
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
        if (id == R.id.editStock) {
            Intent intent = new Intent(this, EditStockActivity.class);
            startActivity(intent);
        }

        if (id == R.id.connectReader) {
            Log.e("connect", "connard");
            Callable onConnection = new Callable() {
                @Override
                public Object call() throws Exception {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            item.setTitle("Disconnect Reader");

                        }
                    });
                    readerConnected = true;
                    return null;
                }
            };
            Callable onDisconnection = new Callable() {
                @Override
                public Object call() throws Exception {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            item.setTitle("Connect Reader");

                            scanStarted = false;
                            try {
                                timer.cancel();
                            } catch (Exception e) {
                            }

                        }
                    });
                    readerConnected = false;
                    return null;
                }
            };
            if (!readerConnected) {
                connectReader(onConnection, onDisconnection);
            } else {
                if (connectThread != null) {
                    try {
                        onDisconnection.call();
                    } catch (Exception e) {

                    }
                    connectThread.closeConnection();
                }
            }
        }

        if (id == R.id.scanInventory) {
            scanInventoryHandler(item);
        }

        if (id == R.id.scanItem) {
            scanItem = !scanItem;
            if (scanItem) {
                item.setTitle("Stop scan item");
                startTimer();
            } else {
                item.setTitle("Scan item");
                timer.cancel();
            }
        }


        return super.onOptionsItemSelected(item);
    }

    boolean busy = false;

    public void connectReader(Callable onConnection, Callable onDisconnection) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Device doesn't support Bluetooth, handle accordingly.


        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, request to turn it on.
            // You should implement the code to request Bluetooth enable here.
            Log.e("enabled", "not");

        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            String deviceName = device.getName();
            Log.e("paired device !", deviceName);
            if (deviceName.equals("RFID0")) {
                MyRunnable r = new MyRunnable("");
                connectThread = new ConnectThread(getApplicationContext(), device, r, onConnection, onDisconnection);
                connectThread.run();
            }
        }
    }

    public class MyRunnable implements Runnable {

        public String tag;

        public MyRunnable(String RFID) {
            this.tag = RFID;
        }

        public void run() {
            if (scanStarted) {
                detectedRFID.add(this.tag);
            }
            if (scanItem) {
                if (currentItem.isEmpty()) {
                    currentItem = this.tag;
                }
            }
//            Log.e("detected", detectedRFID.toString());
//            StockAdapter stockAdapter = new StockAdapter(StockActivity.this, data, notes, isCheckAll, previousLocs, scanStarted, detectedRFID);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    StockActivity.this.setListAdapter(stockAdapter);
//                }
//            });
        }
    }

    public void scanInventoryHandler(MenuItem item) {
        if (readerConnected) {
            if (scanStarted) {
                Toast.makeText(getApplicationContext(), "Finish to scan the location first ", Toast.LENGTH_LONG).show();
                return;
            }
            //on a fini
            if (inventoryScanStarted) {
                item.setTitle("Scan inventory");
                inventoryScanStarted = false;
                Log.e("all codes", locationInventoryList.toString());
                buttonStartScan.setVisibility(View.GONE);
                Intent intent = new Intent(this, InventoryReportActivity.class);
                intent.putExtra("location", locationInventoryList);
                startActivity(intent);
//                finish();

//                    showDialog();

            }
            //on commence le scan
            else {
                buttonStartScan.setVisibility(View.VISIBLE);

                locationInventoryList = new HashMap<>();
                inventoryScanStarted = true;
                item.setTitle("Finish scan");
            }
        } else {
            Toast.makeText(getApplicationContext(), "please connect the reader first", Toast.LENGTH_SHORT).show();
        }
    }

    public void deactivateButtons(boolean yes) {
        locations.setEnabled(yes);
        datesSpinner.setEnabled(yes);
        check.setEnabled(yes);
        checkAll.setEnabled(yes);


    }

    public void startScanLocationHandler() {
        scanStarted = !scanStarted;
        //le scan a commencé
        if (scanStarted) {
            detectedRFID = new HashSet<>();
            previousRFID = new HashSet<>();
            detectedInventoryCodes = new HashSet<>();
            deactivateButtons(false);
            buttonStartScan.setText("Finish scan location");
            LocationInventory inv = new LocationInventory();
            inv.setInventoryCodes(inventoryCodes);


            locationInventoryList.put(locationId, inv);
            startTimer();


        }
        //le scan a fini
        else {
            showFinishLocationDialog();

//                    showDialog();
//
        }

//                Toast.makeText(getApplicationContext(), "start scanning", Toast.LENGTH_LONG).show();
        StockAdapter stockAdapter = new StockAdapter(StockActivity.this, data, notes, isCheckAll, previousLocs, scanStarted, detectedInventoryCodes);
        StockActivity.this.setListAdapter(stockAdapter);
    }

    public void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Set<String> newRFID = new HashSet<>(detectedRFID);
//                        newRFID.removeAll(previousRFID);
                Log.e("timer", currentItem);


                if (!currentItem.isEmpty() & !busy) {
                    busy = true;
                    String q = String.format("select * FROM StockHistory left JOIN Inventory ON StockHistory.InventoryCode = Inventory.InventoryCode" + " where Inventory.rfidTag = '%s' AND StockHistory.Action = 'New' Order By StockHistory.HistoryDate Desc", currentItem);
                    Query query = new Query(q);
                    boolean s = query.execute();
                    if (query.getRes().size() == 0) {
                        Log.e("no tag with rfid: ", currentItem);
                        currentItem = "";
                        busy = false;
                        return;
                    }
                    Map<String, String> res = query.getRes().get(0);
                    Log.e("scanned item", res.toString());


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDialogScannedItem(
                                    res.getOrDefault("Image", ""),
                                    res.getOrDefault("InventoryCode", ""),
                                    res.getOrDefault("CatalogCode", ""),
                                    res.getOrDefault("Name", ""),
                                    res.getOrDefault("Stone", ""),
                                    res.getOrDefault("IDLocation", "")


                            );
                        }
                    });

                    currentItem = "";
                    busy = false;
                }


                if (!newRFID.isEmpty() & !busy) {
                    busy = true;
                    String allTags = "";
                    for (String newTag : newRFID) {
                        allTags += ("'" + newTag + "'" + ", ");
                    }
                    allTags = allTags.substring(0, allTags.length() - 2);

                    Log.e("new rfid", newRFID.toString());
                    String q = String.format("select * FROM StockHistory left JOIN Inventory ON StockHistory.InventoryCode = Inventory.InventoryCode" + " where Inventory.rfidTag in (%s) AND StockHistory.Action = 'New' Order By StockHistory.HistoryDate Desc", allTags);
                    Query query = new Query(q);
                    boolean s = query.execute();
                    ArrayList<Map<String, String>> res = query.getRes();
                    Set<String> newCodes = res.stream().map(stringStringMap -> stringStringMap.getOrDefault("InventoryCode", "")).collect(Collectors.toSet());
                    detectedInventoryCodes.addAll(newCodes);
//                            Log.e("new codes", newCodes.toString());

                    StockAdapter stockAdapter = new StockAdapter(StockActivity.this, data, notes, isCheckAll, previousLocs, scanStarted, detectedInventoryCodes);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            StockActivity.this.setListAdapter(stockAdapter);
                        }
                    });
                }

                previousRFID.addAll(detectedRFID);
                busy = false;

//                        Log.e("all rfid", previousRFID.toString());
            }
        }, 0, 1000);
    }

    public void showFinishLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you done with this location ?");
        builder.setMessage("You scanned " + detectedInventoryCodes.size() + " items.");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setPositiveButton("Finish location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                buttonStartScan.setText("Scan location");
                LocationInventory inv = locationInventoryList.get(locationId);
                inv.setDetectedCodes(detectedInventoryCodes);

                Set<String> missingCodes = inventoryCodes;
                missingCodes.removeAll(detectedInventoryCodes);
                inv.setMissingCodes(missingCodes);

                Set<String> newCodes = detectedInventoryCodes;
                newCodes.removeAll(inventoryCodes);
                inv.setNewCodes(newCodes);


                timer.cancel();
                deactivateButtons(true);
                dialogInterface.cancel();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        detectedRFID.clear();
        detectedInventoryCodes.clear();
        try{
            connectThread.closeConnection();
        }catch(Exception e){

        }
        super.onDestroy();
    }
}

