package com.aguadeoro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.OrderListAdapter;
import com.aguadeoro.utils.MyAsyncTask;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;
import java.util.Map;

public class OrderActivity extends ListActivity {

    ArrayList<String[]> orderList;
    private View wheelView;
    private View mainView;
    private View dialog;
    private String sortOrder;
    private boolean isDisplayedADO = true;
    private String previousWhere = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_order);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        showProgress(true);
        sortOrder = " desc ";
        new ListOrders().execute(" order by " + Utils.ORD_DT + sortOrder,
                " and OrderStatus not in ('Interessés','Livré-Fermé') ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        if (id == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        if (id == R.id.action_search_order) {
            showSearchDialog();
        }
        if (id == R.id.action_payment_report) {
            sortByRemaining();
        }
        if (id == R.id.displayADO) {
            displayADO();
            if (isDisplayedADO) {
                item.setTitle("Hide ADO");
            } else {
                item.setTitle("Show ADO");
            }

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
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(Utils.ORDER_NO, object[0]);
        intent.putExtra(Utils.CALLER, this.getClass().toString());
        startActivity(intent);

    }

    public void sortOrder(View v) {
        String sortBy = "order by " + v.getTag().toString() + sortOrder;
        Toast toast = Toast.makeText(this, getString(R.string.sorting),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 100);
        toast.show();
        new ListOrders().execute(sortBy);
        if (sortOrder.equals(" desc "))
            sortOrder = " asc ";
        else
            sortOrder = " desc ";
    }

    public void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle(getString(R.string.search));
        dialog = inflater.inflate(R.layout.dialog_search_order, null);
        ((Spinner) dialog.findViewById(R.id.search_order_status))
                .setAdapter(new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, Utils
                        .getSetSetting(Utils.ORD_STT)));

        builder.setView(dialog);
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void searchOrder(View v) {
        String searchString = "";
        if (v.getId() == R.id.search_num_btn) {
            searchString = "and "
                    + Utils.ORDER_NO
                    + "= "
                    + ((EditText) dialog.findViewById(R.id.search_num))
                    .getText().toString();
            if (((EditText) dialog.findViewById(R.id.search_num)).getText()
                    .length() == 0) {
                searchString = "";
            }
        } else if (v.getId() == R.id.search_date_btn) {
            searchString = "and Format(["
                    + Utils.ORD_DT
                    + "], 'dd/mm/yyyy') like "
                    + Utils.escape("%"
                    + ((EditText) dialog.findViewById(R.id.search_date))
                    .getText().toString() + "%");
        } else if (v.getId() == R.id.search_deadline_btn) {
            searchString = "and Format(["
                    + Utils.DEADLINE
                    + "], 'dd/mm/yyyy') like "
                    + Utils.escape("%"
                    + ((EditText) dialog
                    .findViewById(R.id.search_deadline))
                    .getText().toString() + "%");
        } else if (v.getId() == R.id.search_name_btn) {
            searchString = "and "
                    + Utils.CUST_NAME
                    + " like "
                    + Utils.escape("%"
                    + ((EditText) dialog.findViewById(R.id.search_name))
                    .getText().toString() + "%");
        } else if (v.getId() == R.id.search_status_btn) {
            searchString = "and "
                    + Utils.ORD_STT
                    + " = "
                    + Utils.escape(((Spinner) dialog.findViewById(R.id.search_order_status))
                    .getSelectedItem().toString());
        }
        new ListOrders().execute(" order by OrderDate DESC ", searchString);
    }

    public void sortByRemaining() {
        String sortBy = "order by Seller desc, OrderNumber desc";
        String where = "and Remain <> 0 ";
        Toast toast = Toast.makeText(this, getString(R.string.sorting),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 100);
        toast.show();
        new ListOrders().execute(sortBy, where, "noLimit");
    }

    public void displayADO() {
        Toast.makeText(this, getString(R.string.sorting),
                Toast.LENGTH_SHORT).show();
        isDisplayedADO = !isDisplayedADO;
//        String sortBy = "order by OrderDate desc";
//        String where = "and not c.IsADO and OrderStatus not in ('Interessés','Livré-Fermé') ";
//        Toast toast = Toast.makeText(this, getString(R.string.sorting),
//                Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.TOP, 0, 100);
//        toast.show();
//        String sortBy = "order by Seller desc, OrderNumber desc";

        new ListOrders().execute(" order by " + Utils.ORD_DT + sortOrder, previousWhere);
    }

    class ListOrders extends MyAsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {

            if (!Utils.isOnline()) {
                return false;
            }
            String sortBy = "";

            if (args.length > 0) {
                sortBy = args[0];
            }

            String whereClause = "";
            if (args.length > 1) {
                whereClause = args[1];
                previousWhere = whereClause;
            }
            if (!isDisplayedADO) {
                whereClause += " and not c.IsADO ";
            }

            String limitString = "top " + limit;
            if (args.length > 2) {
                limitString = "";
            }
            orderList = new ArrayList<String[]>();
            String query = "select " + limitString
                    + " o.*, c.CustomerName from MainOrder o, Customer c "
                    + "where o.CustomerNumber = c.CustomerNumber "
                    + whereClause + sortBy;
            Log.d("query is", query);
            Query q = new Query(query);
            boolean success = q.execute();//for unkown reason, POST doenst work with list order request, hence makeshift with GET
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            if (result == null)
                return true;
            for (int i = 0; i < result.size(); i++) {
                String[] order = new String[9];
                order[0] = result.get(i).get(Utils.ORDER_NO);
                order[1] = result.get(i).get(Utils.ORD_STT);
                order[2] = result.get(i).get(Utils.ORD_DT);
                order[3] = result.get(i).get(Utils.DEADLINE);
                order[4] = result.get(i).get(Utils.TOTAL);
                order[5] = result.get(i).get(Utils.REMAIN);
                order[6] = result.get(i).get(Utils.CUST_NAME);
                order[7] = result.get(i).get(Utils.ORDER_TYPE);
                order[8] = result.get(i).get("StoreMainOrder");
                orderList.add(order);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {

            if (s) {
                OrderActivity.this.setListAdapter(new OrderListAdapter(
                        OrderActivity.this, orderList, true, false, true));
            } else {
                Toast.makeText(OrderActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }
}
