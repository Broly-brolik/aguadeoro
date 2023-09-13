package com.aguadeoro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.ComponentActivityListAdapter;
import com.aguadeoro.utils.EditTextDropdown;
import com.aguadeoro.utils.MyAsyncTask;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class ComponentActivityDetailActivity extends ListActivity {

    ArrayList<String[]> lineList;
    View dialog;
    LayoutInflater inflater;
    String compID;
    //Loc Request 1 2018.04.17 BEGIN
    String[] compInfo;
    ArrayList<String[]> suppOrderList;
    ArrayList<String> suppOrderNumberList;
    //Loc Request 1 2018.04.17 END
    private View wheelView;
    private View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_component_activity);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        showProgress(true);
        inflater = getLayoutInflater();
        compID = this.getIntent().getStringExtra(Utils.COMP_ID);
        //Loc Request 1 2018.04.17 BEGIN
        //display component info on top of page
        compInfo = this.getIntent().getStringArrayExtra(Utils.COMP_INFO);
        String compDetailText = compInfo[2] + ", " + compInfo[3] + ", "
                + compInfo[4] + ", " + compInfo[27];
        ((TextView) mainView.findViewById(R.id.compDetailText)).setText(compDetailText);
        //Loc Request 1 2018.04.17 END
        new ListActivities().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.component_activities, menu);
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
            finish();
        }
        if (id == R.id.action_add) {
            addActivity();
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

    //Loc Request 1 2018.04.17 BEGIN
    //view and edit activity details
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final String[] object = (String[]) getListAdapter().getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Activity details");
        final LinearLayout dialog = (LinearLayout) inflater.inflate(
                R.layout.dialog_add_activity, null);
        ((TextView) dialog.findViewById(R.id.date)).setText(object[2]);
        ((TextView) dialog.findViewById(R.id.invoice)).setText(object[3]);
        ((TextView) dialog.findViewById(R.id.invoiceDate)).setText(object[4]);
        ((TextView) dialog.findViewById(R.id.detail)).setText(object[5]);
        ((TextView) dialog.findViewById(R.id.quantity)).setText(object[6]);
        ((TextView) dialog.findViewById(R.id.recipient)).setText(object[7]);
        ((TextView) dialog.findViewById(R.id.transport)).setText(object[8]);
        ((TextView) dialog.findViewById(R.id.vat)).setText(object[9]);
        ((TextView) dialog.findViewById(R.id.amount)).setText(object[10]);
        ((TextView) dialog.findViewById(R.id.paymentDeadline)).setText(object[11]);
        ((TextView) dialog.findViewById(R.id.paymentDate)).setText(object[12]);
        ((TextView) dialog.findViewById(R.id.paymentBy)).setText(object[13]);
        ((TextView) dialog.findViewById(R.id.remark)).setText(object[14]);
        ((TextView) dialog.findViewById(R.id.suppOrdNo)).setText(object[15]);
        //Loc Request 1 2018.04.25 START
        ((TextView) dialog.findViewById(R.id.inventoryCode)).setText(object[16]);
        //Loc Request 1 2018.04.25 END
        //set dropdown to show list of supp order numbers
        ((EditTextDropdown) dialog.findViewById(R.id.suppOrdNo)).
                setList(suppOrderNumberList.toArray(new String[suppOrderNumberList.size()]));
        //set date popup buttons
        final Calendar myCalendar = Calendar.getInstance();
        final EditText invDate = dialog.findViewById(R.id.invoiceDate);
        final EditText pmtDeadline = dialog.findViewById(R.id.paymentDeadline);
        final EditText pmtDate = dialog.findViewById(R.id.paymentDate);
        final DatePickerDialog.OnDateSetListener date, date2, date3;

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                invDate.setText(Utils.shortDateForDisplay(myCalendar.getTime()));
            }
        };
        invDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ComponentActivityDetailActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                pmtDeadline.setText(Utils.shortDateForDisplay(myCalendar.getTime()));
            }
        };
        pmtDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ComponentActivityDetailActivity.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        date3 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                pmtDate.setText(Utils.shortDateForDisplay(myCalendar.getTime()));
            }
        };
        pmtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ComponentActivityDetailActivity.this, date3, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        builder.setView(dialog);
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
                    public void onClick(DialogInterface dialog2, int which) {
                        String[] args = new String[16];
                        args[0] = object[0];
                        args[1] = ((TextView) dialog.findViewById(R.id.date)).getText().toString();
                        args[2] = ((TextView) dialog.findViewById(R.id.invoice)).getText().toString();
                        args[3] = ((TextView) dialog.findViewById(R.id.invoiceDate)).getText().toString();
                        args[4] = ((TextView) dialog.findViewById(R.id.detail)).getText().toString();
                        args[5] = ((TextView) dialog.findViewById(R.id.quantity)).getText().toString();
                        args[6] = ((TextView) dialog.findViewById(R.id.recipient)).getText().toString();
                        args[7] = ((TextView) dialog.findViewById(R.id.transport)).getText().toString();
                        args[8] = ((TextView) dialog.findViewById(R.id.vat)).getText().toString();
                        args[9] = ((TextView) dialog.findViewById(R.id.amount)).getText().toString();
                        args[10] = ((TextView) dialog.findViewById(R.id.paymentDeadline)).getText().toString();
                        args[11] = ((TextView) dialog.findViewById(R.id.paymentDate)).getText().toString();
                        args[12] = ((TextView) dialog.findViewById(R.id.paymentBy)).getText().toString();
                        args[13] = ((TextView) dialog.findViewById(R.id.remark)).getText().toString();
                        args[14] = ((TextView) dialog.findViewById(R.id.suppOrdNo)).getText().toString();
                        //Loc Request 1 2018.04.25 START
                        args[15] = ((TextView) dialog.findViewById(R.id.inventoryCode)).getText().toString();
                        //Loc Request 1 2018.04.25 END
                        new SaveActivity().execute(args);
                    }
                });
        AlertDialog viewInstDialog = builder.create();
        viewInstDialog.show();
    }
//Loc Request 1 2018.04.17 END

    //Loc Request 1 2018.04.17 BEGIN
    public void addActivity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add activity");
        final LinearLayout dialog = (LinearLayout) inflater.inflate(
                R.layout.dialog_add_activity, null);

        ((TextView) dialog.findViewById(R.id.quantity)).setText("1");
        ((TextView) dialog.findViewById(R.id.amount)).setText("0");
        ((TextView) dialog.findViewById(R.id.date)).setText(
                Utils.shortDateForDisplay(Calendar.getInstance().getTime()));
        //set dropdown to show list of supp order numbers
        ((EditTextDropdown) dialog.findViewById(R.id.suppOrdNo)).
                setList(suppOrderNumberList.toArray(new String[suppOrderNumberList.size()]));

        //set date popup buttons
        final Calendar myCalendar = Calendar.getInstance();
        final EditText invDate = dialog.findViewById(R.id.invoiceDate);
        final EditText pmtDeadline = dialog.findViewById(R.id.paymentDeadline);
        final EditText pmtDate = dialog.findViewById(R.id.paymentDate);
        final DatePickerDialog.OnDateSetListener date, date2, date3;

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                invDate.setText(Utils.shortDateForDisplay(myCalendar.getTime()));
            }
        };
        invDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ComponentActivityDetailActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                pmtDeadline.setText(Utils.shortDateForDisplay(myCalendar.getTime()));
            }
        };
        pmtDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ComponentActivityDetailActivity.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        date3 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                pmtDate.setText(Utils.shortDateForDisplay(myCalendar.getTime()));
            }
        };
        pmtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ComponentActivityDetailActivity.this, date3, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        builder.setView(dialog);
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
                    public void onClick(DialogInterface dialog2, int which) {
                        String[] args = new String[15];
                        args[0] = ((TextView) dialog.findViewById(R.id.date)).getText().toString();
                        args[1] = ((TextView) dialog.findViewById(R.id.invoice)).getText().toString();
                        args[2] = ((TextView) dialog.findViewById(R.id.invoiceDate)).getText().toString();
                        args[3] = ((TextView) dialog.findViewById(R.id.detail)).getText().toString();
                        args[4] = ((TextView) dialog.findViewById(R.id.quantity)).getText().toString();
                        args[5] = ((TextView) dialog.findViewById(R.id.recipient)).getText().toString();
                        args[6] = ((TextView) dialog.findViewById(R.id.transport)).getText().toString();
                        args[7] = ((TextView) dialog.findViewById(R.id.vat)).getText().toString();
                        args[8] = ((TextView) dialog.findViewById(R.id.amount)).getText().toString();
                        args[9] = ((TextView) dialog.findViewById(R.id.paymentDeadline)).getText().toString();
                        args[10] = ((TextView) dialog.findViewById(R.id.paymentDate)).getText().toString();
                        args[11] = ((TextView) dialog.findViewById(R.id.paymentBy)).getText().toString();
                        args[12] = ((TextView) dialog.findViewById(R.id.remark)).getText().toString();
                        args[13] = ((TextView) dialog.findViewById(R.id.suppOrdNo)).getText().toString();
                        //Loc Request 1 2018.04.25 START
                        args[14] = ((TextView) dialog.findViewById(R.id.inventoryCode)).getText().toString();
                        //Loc Request 1 2018.04.25 END
                        new SaveActivity().execute(args);
                    }
                });
        AlertDialog viewInstDialog = builder.create();
        viewInstDialog.show();
    }
//Loc Request 1 2018.04.17 END

    class ListActivities extends MyAsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String query = "select top " + limit + " * from OrderComponentActivity " +
                    "where OrderComponentID =" + compID +
                    " order by CreatedDate desc";
            Query q = new Query(query);
            boolean success = q.execute();
            if (!success) {
                return false;
            }
            lineList = new ArrayList<String[]>();
            ArrayList<Map<String, String>> result = q.getRes();
            for (int i = 0; i < result.size(); i++) {
                //Loc Request 1 2018.04.17 START
                String[] line = new String[17];
                line[0] = result.get(i).get("ID");
                line[1] = result.get(i).get("OrderComponentID");
                line[2] = Utils.shortDateFromDB(result.get(i).get("CreatedDate"));
                line[3] = result.get(i).get("InvoiceNumber");
                line[4] = Utils.shortDateFromDB(result.get(i).get("InvoiceDate"));
                line[5] = result.get(i).get("Detail");
                line[6] = result.get(i).get("ItemQuantity");
                line[7] = result.get(i).get("Recipient");
                line[8] = result.get(i).get("Transport");
                line[9] = result.get(i).get("VAT");
                line[10] = result.get(i).get("Amount");
                line[11] = Utils.shortDateFromDB(result.get(i).get("PaymentDeadline"));
                line[12] = Utils.shortDateFromDB(result.get(i).get("PaymentDate"));
                line[13] = result.get(i).get("PaymentBy");
                line[14] = result.get(i).get("Remark");
                line[15] = result.get(i).get("SupplierOrderNumber");
                //Loc Request 1 2018.04.25 START
                line[16] = result.get(i).get("InventoryCode");
                //Loc Request 1 2018.04.25 END
                //Loc Request 1 2018.04.17 END
                lineList.add(line);
            }
            //Loc Request 1 2018.04.17 BEGIN
            query = "select * from SupplierOrderMain " +
                    "where OrderComponentID =" + compID +
                    " order by CreatedDate desc";
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            suppOrderList = new ArrayList<String[]>();
            suppOrderNumberList = new ArrayList<String>();
            result = q.getRes();
            for (int i = 0; i < result.size(); i++) {
                String[] line = new String[5];
                line[0] = result.get(i).get("ID");
                line[1] = result.get(i).get("OrderComponentID");
                line[2] = result.get(i).get("Instruction");
                line[3] = result.get(i).get("Recipient");
                line[4] = result.get(i).get("SupplierOrderNumber");
                suppOrderList.add(line);
                suppOrderNumberList.add(line[4]);
            }
            //Loc Request 1 2018.04.17 END

            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                ComponentActivityDetailActivity.this.setListAdapter(new ComponentActivityListAdapter(
                        ComponentActivityDetailActivity.this, lineList));
            } else {
                Toast.makeText(ComponentActivityDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }

    //Loc Request 1 2018.04.18 BEGIN
    //Loc Request 1 2018.04.25 BEGIN
    class SaveActivity extends MyAsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String query;
            if (args.length == 15)  //add new activity
                query = "insert into OrderComponentActivity" +
                        "(OrderComponentID, CreatedDate, InvoiceNumber, InvoiceDate, Detail, " +
                        "ItemQuantity, Recipient, Transport, VAT, Amount, PaymentDeadline," +
                        "PaymentDate, PaymentBy, Remark, SupplierOrderNumber, InventoryCode) values("
                        + compID + ","
                        + (args[0].isEmpty() ? "Null" : Utils.escape(Utils.shortDateForInsert(args[0]))) + ","
                        + Utils.escape(args[1]) + ","
                        + (args[2].isEmpty() ? "Null" : Utils.escape(Utils.shortDateForInsert(args[2]))) + ","
                        + Utils.escape(args[3]) + ","
                        + args[4] + ","
                        + Utils.escape(args[5]) + ","
                        + Utils.escape(args[6]) + ","
                        + Utils.escape(args[7]) + ","
                        + args[8] + ","
                        + (args[9].isEmpty() ? "Null" : Utils.escape(Utils.shortDateForInsert(args[9]))) + ","
                        + (args[10].isEmpty() ? "Null" : Utils.escape(Utils.shortDateForInsert(args[10]))) + ","
                        + Utils.escape(args[11]) + ","
                        + Utils.escape(args[12]) + ","
                        + Utils.escape(args[13]) + ","
                        + Utils.escape(args[14]) + ")";

            else    //edit activity
                query = "update OrderComponentActivity set" +
                        " CreatedDate = " + (args[1].isEmpty() ? "Null" : Utils.escape(Utils.shortDateForInsert(args[1]))) +
                        " ,InvoiceNumber = " + Utils.escape(args[2]) +
                        " ,InvoiceDate = " + (args[3].isEmpty() ? "Null" : Utils.escape(Utils.shortDateForInsert(args[3]))) +
                        " ,Detail = " + Utils.escape(args[4]) +
                        " ,ItemQuantity = " + args[5] +
                        " ,Recipient = " + Utils.escape(args[6]) +
                        " ,Transport = " + Utils.escape(args[7]) +
                        " ,VAT = " + Utils.escape(args[8]) +
                        " ,Amount = " + args[9] +
                        " ,PaymentDeadline = " + (args[10].isEmpty() ? "Null" : Utils.escape(Utils.shortDateForInsert(args[10]))) +
                        " ,PaymentDate = " + (args[11].isEmpty() ? "Null" : Utils.escape(Utils.shortDateForInsert(args[11]))) +
                        " ,PaymentBy = " + Utils.escape(args[12]) +
                        " ,Remark = " + Utils.escape(args[13]) +
                        " ,SupplierOrderNumber = " + Utils.escape(args[14]) +
                        " ,InventoryCode = " + Utils.escape(args[15]) +
                        " where ID = " + args[0];
            Query q = new Query(query);
            return q.execute();
        }
        //Loc Request 1 2018.04.25 END
        //Loc Request 1 2018.04.18 END

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                ComponentActivityDetailActivity.this.recreate();
            } else {
                Toast.makeText(ComponentActivityDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }
}
