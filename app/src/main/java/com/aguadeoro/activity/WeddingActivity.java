package com.aguadeoro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.ActivityListAdapter;
import com.aguadeoro.utils.MyAsyncTask;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class WeddingActivity extends ListActivity {

    ArrayList<String[]> activityList;

    Calendar c;

    private View wheelView;
    private View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_wedding);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        showProgress(true);
        c = Calendar.getInstance();
        Spinner rangeList = findViewById(R.id.range);
        rangeList.setAdapter(new ArrayAdapter<Integer>(this,
                android.R.layout.simple_spinner_item, new Integer[]
                {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}));
        rangeList.setSelection(c.get(Calendar.MONTH));
        rangeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chooseMonth(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    public void chooseMonth(int month) {
        new ListActivities().execute("" + month);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        String[] object = (String[]) getListAdapter().getItem(position);
        Intent intent = new Intent(this, CustomerDetailActivity.class);
        intent.putExtra(Utils.CUST_REL_ID, object[2]);
        startActivity(intent);

    }

    class ListActivities extends MyAsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            activityList = new ArrayList<String[]>();

            //retrieve civil wedding dates
            String query = "select * from (select WeddingDate as Wedding, 'Civil' as Type, CustomerName, Day(WeddingDate) as Day from Customer "
                    + " where CustomerName <> '' and Month(WeddingDate) = " + args[0] + ")"
                    + " union (select WeddingDate2 as Wedding, 'Ceremony' as Type, CustomerName, Day(WeddingDate2) as Day from Customer "
                    + " where CustomerName <> '' and Month(WeddingDate2) = " + args[0] + ") order by Day";
            query = "select * from (select cr.ID, c1.WeddingDate as Wedding, 'Civil' as Type, " +
                    "c1.CustomerName as Name1, c2.CustomerName as Name2, Day(c1.WeddingDate) as Day " +
                    "from CustomerRelationship cr, Customer c1, Customer c2 " +
                    "where cr.Customer1 = c1.CustomerNumber and cr.Customer2 = c2.CustomerNumber " +
                    "and Month(c1.WeddingDate) = " + args[0] +
                    " union " +
                    "select cr.ID, c.WeddingDate as Wedding, 'Civil' as Type, " +
                    "c.CustomerName as Name1, null as Name2, Day(c.WeddingDate) as Day " +
                    "from CustomerRelationship cr, Customer c " +
                    "where ((cr.Customer1 = c.CustomerNumber and cr.Customer2 is null) " +
                    "or (cr.Customer2 = c.CustomerNumber and cr.Customer1 is null)) " +
                    "and Month(c.WeddingDate) = " + args[0] +
                    " union " +
                    "select cr.ID, c1.WeddingDate2 as Wedding, 'Ceremony' as Type, " +
                    "c1.CustomerName as Name1, c2.CustomerName as Name2, Day(c1.WeddingDate2) as Day " +
                    "from CustomerRelationship cr, Customer c1, Customer c2 " +
                    "where cr.Customer1 = c1.CustomerNumber and cr.Customer2 = c2.CustomerNumber " +
                    "and Month(c1.WeddingDate2) = " + args[0] +
                    " union " +
                    "select cr.ID, c.WeddingDate2 as Wedding, 'Ceremony' as Type, " +
                    "c.CustomerName as Name1, null as Name2, Day(c.WeddingDate2) as Day " +
                    "from CustomerRelationship cr, Customer c " +
                    "where ((cr.Customer1 = c.CustomerNumber and cr.Customer2 is null) " +
                    "or (cr.Customer2 = c.CustomerNumber and cr.Customer1 is null)) " +
                    "and Month(c.WeddingDate2) = " + args[0] + ") " +
                    "order by Day";
            Query q = new Query(query);
            boolean success = q.execute();
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            for (int i = 0; i < result.size(); i++) {
                String[] newCustomers = new String[3];
                newCustomers[0] = result.get(i).get("Wedding");
                newCustomers[1] = result.get(i).get("Type") + " - " + result.get(i).get("Name1") + ", " + result.get(i).get("Name2");
                newCustomers[2] = result.get(i).get("ID");
                activityList.add(newCustomers);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                WeddingActivity.this.setListAdapter(new ActivityListAdapter(
                        WeddingActivity.this, activityList));
            } else {
                Toast.makeText(WeddingActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }

}
