package com.aguadeoro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.ActivityListAdapter;
import com.aguadeoro.utils.MyAsyncTask;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class ActivitiesActivity extends ListActivity {

    ArrayList<String[]> activityList;

    String date, date0;
    Calendar c, c0;
    Integer range;

    private View wheelView;
    private View mainView;
    private boolean displaySuppliers = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_activities);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        showProgress(true);
        c = Calendar.getInstance();
        c0 = Calendar.getInstance();
        c0.add(Calendar.DAY_OF_MONTH, -7);
        date = Utils.shortDateForInsert(c.getTime());
        date0 = Utils.shortDateForInsert(c0.getTime());
        Spinner rangeList = findViewById(R.id.range);
        rangeList.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31}));
        rangeList.setSelection(6);
        rangeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chooseDate(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //new ListActivities().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        if (id == R.id.action_suppliers) {
            displaySuppliers = !displaySuppliers;
            if (displaySuppliers) {
                item.setTitle("Hide suppliers");
            } else {
                item.setTitle("Display suppliers");
            }
            new ListActivities().execute();
        }
        return true;
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

    public void chooseDate(View view) {
        OnDateSetListener dateListener = new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                range = (Integer) ((Spinner) findViewById(R.id.range)).getSelectedItem();
                c.add(Calendar.DATE, 1);
                date = Utils.shortDateForInsert(c.getTime());
                c0 = (Calendar) c.clone();
                c0.add(Calendar.DAY_OF_MONTH, -range);
                date0 = Utils.shortDateForInsert(c0.getTime());
                new ListActivities().execute();

            }
        };
        new DatePickerDialog(this, dateListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    class ListActivities extends MyAsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            activityList = new ArrayList<String[]>();
            Log.d("dates", "" + date0 + " " + date);
            //retrieve new customers
            String whereClause = " and CreatedDate <=#" + date + "# and CreatedDate >=#" + date0 + "#";
            String query = "select CreatedDate, CustomerName, CustomerNumber from Customer " + "where CustomerName <> ''" + whereClause + " order by CreatedDate desc";
            Query q = new Query(query);
            boolean success = q.execute();
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            for (int i = 0; i < result.size(); i++) {
                String[] newCustomers = new String[3];
                newCustomers[0] = result.get(i).get("CreatedDate");
                newCustomers[1] = "Created customer : " + result.get(i).get("CustomerName");
                newCustomers[2] = result.get(i).get("CustomerNumber");
                activityList.add(newCustomers);
            }

            //retrieve customer activities
            whereClause = " and HistoryDate <=#" + date + "# and HistoryDate >=#" + date0 + "#";
            query = "select ch.HistoryDate, c.CustomerName,c.CustomerNumber, ch.Remark from Customer c, CustomerHistory ch " + "where c.CustomerNumber = ch.CustomerNumber " + whereClause + " order by HistoryDate desc";
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            result = q.getRes();
            for (int i = 0; i < result.size(); i++) {
                String[] custActs = new String[3];
                custActs[0] = result.get(i).get("HistoryDate");
                custActs[1] = result.get(i).get("CustomerName") + " - " + result.get(i).get("Remark");
                custActs[2] = result.get(i).get("CustomerNumber");
                activityList.add(custActs);
            }

            //retrieve order activities
            whereClause = " and EntryDate <=#" + date + "# and EntryDate >=#" + date0 + "#";
            query = "select oh.EntryDate, c.CustomerName,c.CustomerNumber, oh.Remark, mo.OrderNumber from Customer c, MainOrder mo, OrderHistory oh " + "where c.CustomerNumber = mo.CustomerNumber and mo.OrderNumber = oh.OrderNumber" + whereClause + " order by EntryDate desc";
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            result = q.getRes();
            for (int i = 0; i < result.size(); i++) {
                String[] ordtActs = new String[3];
                ordtActs[0] = result.get(i).get("EntryDate");
                ordtActs[1] = result.get(i).get("CustomerName") + " - " + result.get(i).get("Remark") + " (order " + result.get(i).get("OrderNumber") + ")";
                ordtActs[2] = result.get(i).get("CustomerNumber");
                activityList.add(ordtActs);
            }

            if (displaySuppliers) {
                //retrieve suporder activities
                query = "select SupplierOrderMain.CreatedDate, SupplierOrderMain.SupplierOrderNumber," +
                        " SupplierOrderMain.Recipient, Customer.CustomerNumber, SupplierOrderMain.Status " + "" +
                        " FROM (MainOrder INNER JOIN (OrderComponent INNER JOIN SupplierOrderMain ON" +
                        " OrderComponent.ID = SupplierOrderMain.OrderComponentID) ON MainOrder.OrderNumber" +
                        " = OrderComponent.OrderNumber) INNER JOIN Customer ON MainOrder.CustomerNumber =" +
                        " Customer.CustomerNumber " +
                        " WHERE SupplierOrderMain.CreatedDate between #" + date + "# And #" + date0 + "# " +
                        " ORDER BY SupplierOrderMain.CreatedDate DESC;";
                q = new Query(query);
                success = q.execute();
                if (!success) {
                    return false;
                }
                result = q.getRes();
                for (int i = 0; i < result.size(); i++) {
                    String[] ordtActs = new String[3];
                    ordtActs[0] = result.get(i).get("CreatedDate");
                    ordtActs[1] = "Created instruction for :" + result.get(i).get("Recipient") + " - " + result.get(i).get("SupplierOrderNumber") + " (" + result.get(i).get("Status") + ")";
                    ordtActs[2] = result.get(i).get("CustomerNumber");
                    activityList.add(ordtActs);
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                int size = activityList.size();
                for (int i = size - 1; i > -1; i--) {
                    for (int j = i; j > -1; j--) {
                        if (activityList.get(i)[0].compareTo(activityList.get(j)[0]) > 0) {
                            String[] temp = activityList.get(i);
                            activityList.set(i, activityList.get(j));
                            activityList.set(j, temp);
                        }
                    }
                }
                ActivitiesActivity.this.setListAdapter(new ActivityListAdapter(ActivitiesActivity.this, activityList));
                ((TextView) ActivitiesActivity.this.findViewById(R.id.date)).setText(Utils.shortDateForDisplay(c.getTime()));
            } else {
                Toast.makeText(ActivitiesActivity.this, getString(R.string.error_retrieving_data), Toast.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String[] activity = activityList.get(position);
        Intent intent = new Intent(this, CustomerDetailActivity.class);
        intent.putExtra("CustomerNumber", activity[2]);
        startActivity(intent);
    }
}
