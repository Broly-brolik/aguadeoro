package com.aguadeoro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.OrderListAdapter;
import com.aguadeoro.adapter.PaymentReportListAdapter;
import com.aguadeoro.utils.MyAsyncTask;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class ReportActivity extends ListActivity {

    ArrayList<String[]> orderList;
    ArrayList<String[]> paymentList;
    double payTotal = 0, orderTotal = 0, totalCash = 0, totalPost = 0,
            totalCredit = 0, totalBill = 0, totalOther = 0;

    String date, date0, checkedBy;
    Calendar c, c0;
    Integer range;

    private View wheelView;
    private View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_report);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        showProgress(true);
        c = Calendar.getInstance();
        c0 = Calendar.getInstance();
        c0.add(Calendar.DAY_OF_MONTH, -7);
        date = Utils.shortDateForInsert(c.getTime());
        date0 = Utils.shortDateForInsert(c0.getTime());
        Spinner rangeList = findViewById(R.id.range);
        rangeList.setAdapter(new ArrayAdapter<Integer>(this,
                android.R.layout.simple_spinner_item, new Integer[]
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                        21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31}));
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
        String where1 = " and OrderDate <= #" + date + "# and OrderDate >= #"
                + date0 + "#";
        String where2 = " and oh.EntryDate <= #" + date
                + "# and EntryDate >= #" + date0 + "#";
        //new ListReport().execute(Utils.ORD_DT, where1, where2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_validate) {
            validate();
            return true;
        }
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

    public void chooseDate(View view) {
        OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                range = (Integer) ((Spinner) findViewById(R.id.range))
                        .getSelectedItem();
                date = Utils.shortDateForInsert(c.getTime());
                c0 = (Calendar) c.clone();
                c0.add(Calendar.DAY_OF_MONTH, -range);
                date0 = Utils.shortDateForInsert(c0.getTime());
                String where1 = " and OrderDate <= #" + date
                        + "# and OrderDate >= #" + date0 + "#";
                String where2 = " and oh.EntryDate <= #" + date
                        + "# and EntryDate >= #" + date0 + "#";
                new ListReport().execute(Utils.ORD_DT, where1, where2);

            }
        };
        new DatePickerDialog(this, dateListener, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void validate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.validate_by));
        final EditText name = new EditText(this);
        name.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        builder.setView(name);
        builder.setCancelable(true);
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
                        checkedBy = name.getText().toString();
                        String idList = "(";
                        ListView list = ReportActivity.this
                                .findViewById(R.id.payment_list);
                        ArrayList<String> checkedIDs = ((PaymentReportListAdapter) list.getAdapter()).getCheckedIDs();
                        for (String id : checkedIDs) {
                            idList += id + ",";
                        }
                        idList += ")";
                        new ValidateJournal().execute(idList);
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    class ListReport extends MyAsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String whereClause1 = "", whereClause2 = "";
            if (args.length > 2) {
                whereClause1 = args[1];
                whereClause2 = args[2];
            }
            orderList = new ArrayList<String[]>();
            String sortBy = args[0];
            String query = "select "
                    + " o.*, c.CustomerName from MainOrder o, Customer c "
                    + "where o.CustomerNumber = c.CustomerNumber and OrderType <> 'Preview'"
                    + whereClause1 + " order by " + sortBy + " desc";
            Query q = new Query(query);
            boolean success = q.execute();
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            orderTotal = 0;
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
                order[8] = result.get(i).get(Utils.SELLER);
                orderList.add(order);
                if (!order[4].isEmpty())
                    orderTotal += Double.parseDouble(order[4]);
            }

            paymentList = new ArrayList<String[]>();
            query = "select "
                    + " o.OrderNumber, o.CustomerNumber, c.CustomerName, "
                    + "oh.ID, oh.Amount, oh.PaymentMode, oh.EntryDate, oh.CheckedBy, oh.CheckedOn, oh.PaymentDate, o.StoreMainOrder "
                    + "from MainOrder o, Customer c, OrderHistory oh "
                    + "where o.CustomerNumber = c.CustomerNumber and o.OrderNumber = oh.OrderNumber "
                    + whereClause2 + " and oh.PaymentMode is not null "
                    + "and oh.PaymentMode <> " + Utils.escape(Utils.DISCOUNT)
                    + " order by PaymentDate desc";
            q = new Query(query);

//            String[] pmt = new String[8];
            success = q.execute();
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result2 = q.getRes();
            payTotal = 0;
            totalPost = 0;
            totalCredit = 0;
            totalCash = 0;
            totalBill = 0;
            totalOther = 0;
            for (int i = 0; i < result2.size(); i++) {
                String[] pmt = new String[9];
                pmt[0] = result2.get(i).get(Utils.ORDER_NO)
                        + " [" + result2.get(i).get("StoreMainOrder") + "]";
                pmt[1] = result2.get(i).get(Utils.ENTRY_DATE);
                pmt[2] = "[" + result2.get(i).get(Utils.CUST_NO) + "]"
                        + result2.get(i).get(Utils.CUST_NAME);
                pmt[3] = result2.get(i).get(Utils.PAY_MODE);
                pmt[4] = result2.get(i).get(Utils.AMOUNT);
                pmt[5] = result2.get(i).get(Utils.CHECKED_BY);
                pmt[6] = result2.get(i).get(Utils.CHECKED_ON);
                pmt[7] = result2.get(i).get(Utils.ID);
                pmt[8] = result2.get(i).get("PaymentDate");

                paymentList.add(pmt);
                if (!pmt[4].isEmpty())
                    payTotal += Double.parseDouble(pmt[4]);
                if (pmt[3].equals("Post Card") || pmt[3].equals("MaestroCard")) {
                    totalPost += Double.parseDouble(pmt[4]);
                } else if (pmt[3].equals("Cash")) {
                    totalCash += Double.parseDouble(pmt[4]);
                } else if (pmt[3].equals("Credit Card")) {
                    totalCredit += Double.parseDouble(pmt[4]);
                } else if (pmt[3].equals("Bulletin de Versement")) {
                    totalBill += Double.parseDouble(pmt[4]);
                } else {
                    totalOther += Double.parseDouble(pmt[4]);
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                ReportActivity.this.setListAdapter(new OrderListAdapter(
                        ReportActivity.this, orderList, true, true, false));
                ListView pmtList = ReportActivity.this
                        .findViewById(R.id.payment_list);
                pmtList.setAdapter(new PaymentReportListAdapter(
                        ReportActivity.this, paymentList));
                ((TextView) ReportActivity.this.findViewById(R.id.pay_total))
                        .setText("" + payTotal);
                ((TextView) ReportActivity.this.findViewById(R.id.pay_bull))
                        .setText("" + totalBill);
                ((TextView) ReportActivity.this.findViewById(R.id.pay_cash))
                        .setText("" + totalCash);
                ((TextView) ReportActivity.this.findViewById(R.id.pay_credit))
                        .setText("" + totalCredit);
                ((TextView) ReportActivity.this.findViewById(R.id.pay_postcard))
                        .setText("" + totalPost);
                ((TextView) ReportActivity.this.findViewById(R.id.pay_other))
                        .setText("" + totalOther);
                ((TextView) ReportActivity.this.findViewById(R.id.order_total))
                        .setText("" + orderTotal);
                ((TextView) ReportActivity.this.findViewById(R.id.date))
                        .setText(Utils.shortDateForDisplay(c.getTime()));
            } else {
                Toast.makeText(ReportActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }

    class ValidateJournal extends MyAsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String today = Utils.shortDateForInsert(Calendar.getInstance()
                    .getTime());
            String query = "update OrderHistory set " + "CheckedBy = "
                    + Utils.escape(checkedBy) + ", CheckedOn = "
                    + Utils.escape(today) + " where ID in " + args[0];
            Query q = new Query(query);
            return q.execute();
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                String where1 = " and OrderDate <= #" + date
                        + "# and OrderDate >= #" + date0 + "#";
                String where2 = " and oh.EntryDate <= #" + date
                        + "# and EntryDate >= #" + date0 + "#";
                new ListReport().execute(Utils.DEADLINE, where1, where2);

            } else {
                Toast.makeText(ReportActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }

}
