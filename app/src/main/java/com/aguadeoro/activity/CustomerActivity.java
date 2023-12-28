package com.aguadeoro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.CustomerListAdapter;
import com.aguadeoro.utils.MyAsyncTask;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;
import java.util.Map;

public class CustomerActivity extends ListActivity {

    ArrayList<String[]> customerList;
    View dialog;
    private View wheelView;
    private View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_customer);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);

        showProgress(true);
        new ListCustomers().execute(Utils.CREATED_DATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        if (id == R.id.action_new_customer) {
            startActivity(new Intent(this, NewCustomerActivity.class));
        }
        if (id == R.id.action_search_customer) {
            showSearchDialog();
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
        Intent intent = new Intent(this, CustomerDetailActivity.class);
        intent.putExtra(Utils.CUST_REL_ID, object[0]);
        startActivity(intent);
    }

    public void sortCustomer(View v) {
        String sortBy = v.getTag().toString();
        Toast toast = Toast.makeText(this, getString(R.string.sorting), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 100);
        toast.show();
        new ListCustomers().execute(sortBy);
    }

    public void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle(getString(R.string.search));
        dialog = inflater.inflate(R.layout.dialog_search_customer, null);
        builder.setView(dialog);
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void searchCustomer(View v) {
        String searchString = "";
        if (v.getId() == R.id.search_name_btn) {
            searchString = "(c1." + Utils.CUST_NAME + " like "
                    + Utils.escape("%" + ((EditText) dialog.findViewById(R.id.search_name)).getText().toString() + "%")
                    + " or c2." + Utils.CUST_NAME + " like "
                    + Utils.escape("%" + ((EditText) dialog.findViewById(R.id.search_name)).getText().toString() + "%")
                    + ")";
        } else if (v.getId() == R.id.search_email_btn) {
            searchString = "c1." + Utils.EMAIL + " like "
                    + Utils.escape("%" + ((EditText) dialog.findViewById(R.id.search_email)).getText().toString() + "%");
        } else if (v.getId() == R.id.search_addr_btn) {
            searchString = "c1." + Utils.ADDR + " like "
                    + Utils.escape("%" + ((EditText) dialog.findViewById(R.id.search_address)).getText().toString() + "%");
        } else if (v.getId() == R.id.search_tel_btn) {
            searchString = "c1." + Utils.TEL + " like "
                    + Utils.escape("%" + ((EditText) dialog.findViewById(R.id.search_tel)).getText().toString() + "%");
        } else if (v.getId() == R.id.search_wedding_btn) {
            searchString = "Format([c1." + Utils.WEDDING_DATE + "], 'dd/mm/yyyy') like "
                    + Utils.escape("%" + ((EditText) dialog.findViewById(R.id.search_wedding)).getText().toString() + "%");
        }
        new ListCustomers().execute(Utils.WEDDING_DATE, "and " + searchString);
    }

    class ListCustomers extends MyAsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String sortBy = args[0];
            String whereClause = "";
            if (args.length > 1) {
                whereClause = args[1];
            }
            customerList = new ArrayList<String[]>();
            boolean success = false;
            String query = "select top " + limit + " cr.ID, c1.CustomerName as CustomerName1, "
                    + "c2.CustomerName as CustomerName2, c1.WeddingDate, c1.CreatedDate, "
                    + "c1.Address, c1.Email, c1.Tel, c1.Type, c1.WeddingDate2 "
                    + "from CustomerRelationship cr, Customer c1, Customer c2 "
                    + "where cr.Customer1 = c1.CustomerNumber "
                    + "and cr.Customer2 = c2.CustomerNumber "
                    + whereClause
                    + " order by " + "c1." + sortBy + " desc";
            Query q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            for (int i = 0; i < result.size(); i++) {
                String[] customer = new String[7];
                customer[0] = result.get(i).get(Utils.ID);
                customer[1] = result.get(i).get(Utils.CUST_NAME_1);
                customer[2] = result.get(i).get(Utils.CUST_NAME_2);
                if (result.get(i).get(Utils.CUST_NAME_2) == null
                        || "null".equals(result.get(i).get(Utils.CUST_NAME_2))) {
                    customer[2] = null;
                }
                customer[3] = result.get(i).get(Utils.CREATE_DATE);
                customer[4] = result.get(i).get(Utils.WEDDING_DATE);
                customer[5] = result.get(i).get(Utils.TYPE);
                customer[6] = result.get(i).get(Utils.WEDDING_DATE2);
                customerList.add(customer);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                CustomerActivity.this.setListAdapter(new CustomerListAdapter(
                        CustomerActivity.this, customerList));
            } else {
                Toast.makeText(CustomerActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }
}
