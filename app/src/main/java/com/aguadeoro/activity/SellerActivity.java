package com.aguadeoro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.SellerInventoryListAdapter;
import com.aguadeoro.utils.MyAsyncTask;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class SellerActivity extends ListActivity {

    ArrayList<String[]> invList;
    View dialog;
    String whereClause, sortBy;
    boolean isMultiple;
    ArrayList<String> sellers;
    String seller;
    private View wheelView;
    private View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_seller);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        showProgress(true);
        whereClause = "";
        sortBy = " order by AssignDate desc";
        new ListSeller().execute();
        Spinner names = findViewById(R.id.seller_name);
        names.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                seller = ((TextView) arg1).getText().toString();
                updateSellerName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.seller_inventory
                , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
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
        intent.putExtra(Utils.ID, object[9]);
        startActivity(intent);

    }

    public void updateSellerName() {
        whereClause = "and id.AssignedTo = " + Utils.escape(seller);
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
        String date = Utils.shortDateForInsert(Calendar.getInstance().getTime());
        new UpdateAssign().execute(date, action);
    }

    class ListSeller extends MyAsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            sellers = new ArrayList<String>();
            boolean success = false;
            String query = "select distinct AssignedTo from InventoryDistribution order by AssignedTo asc";
            Query q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            for (int i = 0; i < result.size(); i++) {
                sellers.add(result.get(i).get("0"));
            }
            if (result.isEmpty()) {
                sellers.add("------------");
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                Spinner name = findViewById(R.id.seller_name);
                name.setAdapter(new ArrayAdapter<String>(SellerActivity.this, android.R.layout.simple_list_item_1, sellers));
                whereClause = "and id.AssignedTo = " + Utils.escape(sellers.get(0));
                new ListInventory().execute();
            } else {
                Toast.makeText(SellerActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
            showProgress(false);
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
            String query = "select id.ID, i.InventoryCode, i.Mark, i.Category, i.Description, id.InventoryID,"
                    + " i.Price, id.Quantity, id.AssignDate, id.Status from Inventory i, InventoryDistribution id "
                    + "where id.InventoryID = i.ID "
                    + whereClause
                    + " order by id.AssignDate desc";
            Query q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            for (int i = 0; i < result.size(); i++) {
                String[] line = new String[10];
                line[0] = result.get(i).get(Utils.ID);
                line[1] = result.get(i).get(Utils.INVT_CODE);
                line[2] = result.get(i).get(Utils.MARK);
                line[3] = result.get(i).get(Utils.CATEGORY);
                line[4] = result.get(i).get(Utils.DESCRIPTION);
                line[5] = result.get(i).get(Utils.PRICE);
                line[6] = result.get(i).get(Utils.QUANTITY);
                line[7] = result.get(i).get("AssignDate");
                line[8] = result.get(i).get(Utils.STATUS);
                line[9] = result.get(i).get("InventoryID");
                invList.add(line);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                SellerActivity.this
                        .setListAdapter(new SellerInventoryListAdapter(
                                SellerActivity.this, invList, isMultiple));
            } else {
                Toast.makeText(SellerActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }

    class UpdateAssign extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            ArrayList<String> iDList = ((SellerInventoryListAdapter) getListAdapter())
                    .getCheckedAssignIDs();
            ArrayList<String> inventoryIDList = ((SellerInventoryListAdapter) getListAdapter())
                    .getCheckedInventoryIDs();
            for (int i = 0; i < iDList.size(); ++i) {
                String query = "update InventoryDistribution set AssignDate =" + Utils.escape(args[0])
                        + ",Status =" + Utils.escape(args[1])
                        + " where ID =" + iDList.get(i);
                Query q = new Query(query);
                if (!q.execute())
                    return false;
                if (args[1].equals(Utils.TAKE_OUT))
                    query = "update Inventory set Quantity=Quantity-1, Status=" + Utils.escape(Utils.TAKE_OUT)
                            + " where ID =" + inventoryIDList.get(i);
                else if (args[1].equals(Utils.RETURN))
                    query = "update Inventory set Quantity=Quantity+1, Status=" + Utils.escape(Utils.STOCK)
                            + " where ID =" + inventoryIDList.get(i);
                else if (args[1].equals(Utils.SOLD))
                    query = "update Inventory set Status=" + Utils.escape(Utils.SOLD)
                            + " where ID =" + inventoryIDList.get(i);
                q = new Query(query);
                if (!q.execute())
                    return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                isMultiple = false;
                updateSellerName();
            } else {
                Toast.makeText(SellerActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
