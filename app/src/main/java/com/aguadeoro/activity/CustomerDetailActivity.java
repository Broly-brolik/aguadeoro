package com.aguadeoro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.CustomerHistoryListAdapter;
import com.aguadeoro.adapter.ImagesAdapter;
import com.aguadeoro.adapter.OrderListAdapter;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

public class CustomerDetailActivity extends ListActivity {

    String customerRelationshipID;
    String mainCustomerNumber, otherCustomerNumber, customerNumbers, whatsapp1String, whatsapp2String, customerSource;
    ArrayList<String[]> historyList;
    ArrayList<String[]> orderList;
    ArrayList<String[]> customerActionList;
    String[] languages;
    String[] stores;
    private View wheelView;
    private View mainView;
    private String name1, name2, address, address2,
            email, email2, tel, tel2, weddingDate, weddingDate2,
            newWeddingDate, newWeddingDate2, type, customerSourceRemark, storeHandling, customerLanguage;
    private boolean isCheckedBF, isCheckedAL, isCheckedBJ, isCheckedOther, clickedStatus, whatsapp1, whatsapp2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_customer_detail);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        showProgress(true);
        historyList = new ArrayList<String[]>();
        orderList = new ArrayList<String[]>();
        customerRelationshipID = getIntent().getStringExtra(Utils.CUST_REL_ID);
        Log.d("+++++++",""+customerRelationshipID);

        if (getIntent().getStringExtra(Utils.CUST_NO) != null) {
            mainCustomerNumber = getIntent().getStringExtra(Utils.CUST_NO);
        }
        TextView status = findViewById(R.id.status_column);
        clickedStatus = true;
        status.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedStatus = !clickedStatus;
                new ListDetails().execute(clickedStatus);
            }
        });
        new ListDetails().execute(clickedStatus);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String title = item.getTitle().toString();
        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        if (id == R.id.action_add_history) {
            addHistory("");
        }
        if (id == R.id.action_view_actions) {
            viewActions();
        }
        if (id == R.id.action_edit_customer) {
            editCustomer();
        }
        if (id == R.id.action_add_sale_order) {
            Intent intent = new Intent(this, NewOrderActivity.class);
            intent.putExtra(Utils.CUST_NO, mainCustomerNumber);
            intent.putExtra(Utils.ORDER_TYPE, Utils.ORD_PURCHASE);
            intent.putExtra(Utils.CUST_REL_ID, customerRelationshipID);
            intent.putExtra(Utils.CUST_NAME_1, name1);
            intent.putExtra(Utils.CUST_NAME_2, name2);
            intent.putExtra("isSale", true);
            intent.putExtra(Utils.TITLE, title);
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_add_order) {
            Intent intent = new Intent(this, NewOrderActivity.class);
            intent.putExtra(Utils.CUST_NO, mainCustomerNumber);
            intent.putExtra(Utils.ORDER_TYPE, Utils.ORD_ORDER);
            intent.putExtra(Utils.CUST_REL_ID, customerRelationshipID);
            intent.putExtra(Utils.CUST_NAME_1, name1);
            intent.putExtra(Utils.CUST_NAME_2, name2);
            intent.putExtra(Utils.TITLE, title);
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_add_preview_order) {
            Intent intent = new Intent(this, NewOrderActivity.class);
            intent.putExtra(Utils.CUST_NO, mainCustomerNumber);
            intent.putExtra(Utils.CUST_REL_ID, customerRelationshipID);
            intent.putExtra(Utils.ORDER_TYPE, Utils.ORD_PREVIEW);
            intent.putExtra(Utils.CUST_NAME_1, name1);
            intent.putExtra(Utils.CUST_NAME_2, name2);
            intent.putExtra(Utils.TITLE, title);
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_add_repair_order) {
            Intent intent = new Intent(this, NewOrderActivity.class);
            intent.putExtra(Utils.CUST_NO, mainCustomerNumber);
            intent.putExtra(Utils.CUST_REL_ID, customerRelationshipID);
            intent.putExtra(Utils.ORDER_TYPE, Utils.ORD_REPAIR);
            intent.putExtra(Utils.CUST_NAME_1, name1);
            intent.putExtra(Utils.CUST_NAME_2, name2);
            intent.putExtra(Utils.TITLE, title);
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_add_rdv) {
            addRDV();
        }
        if (id == R.id.action_print_note_en) {
            Utils.printNote(this, "en");
        }
        if (id == R.id.action_print_note_fr) {
            Utils.printNote(this, "fr");
        }
        if (id == R.id.action_email) {
            sendEmail();
        }
        return true;
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        String[] object = (String[]) getListAdapter().getItem(position);
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(Utils.ORDER_NO, object[0]);
        intent.putExtra(Utils.CUST_REL_ID, customerRelationshipID);
        startActivity(intent);
        finish();
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            wheelView.setVisibility(View.VISIBLE);
            wheelView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            wheelView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mainView.setVisibility(View.VISIBLE);
            mainView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
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

    public void editCustomer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle(getString(R.string.edit_cust_info));
        final View view = inflater.inflate(R.layout.activity_new_customer, null);
        View button = view.findViewById(R.id.register_button);
        ((LinearLayout) button.getParent()).removeView(button);
        ((TextView) view.findViewById(R.id.name1)).setText(name1);
        ((TextView) view.findViewById(R.id.name2)).setText(name2);
        ((TextView) view.findViewById(R.id.address)).setText(address);
        ((TextView) view.findViewById(R.id.address2)).setText(address2);
        ((TextView) view.findViewById(R.id.email)).setText(email);
        ((TextView) view.findViewById(R.id.email2)).setText(email2);
        ((TextView) view.findViewById(R.id.tel)).setText(tel);
        ((TextView) view.findViewById(R.id.tel2)).setText(tel2);
        ((TextView) view.findViewById(R.id.wedding_date)).setText(weddingDate);
        ((TextView) view.findViewById(R.id.wedding_date2)).setText(weddingDate2);
        ((EditText) view.findViewById(R.id.sourceRemark)).setText(customerSourceRemark);
        ((CheckBox) view.findViewById(R.id.whatsapp1)).setChecked(whatsapp1);
        ((CheckBox) view.findViewById(R.id.whatsapp2)).setChecked(whatsapp2);
        Spinner languageSpinner = (Spinner) view.findViewById(R.id.languageSpinner);
        languageSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, languages));
        int spinnerIndex = 0;
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equals(customerLanguage)) {
                spinnerIndex = i;
                break;
            }
        }
        languageSpinner.setSelection(spinnerIndex);
        Spinner storesSpinner = (Spinner) view.findViewById(R.id.storesSpinner);
        storesSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stores));
        spinnerIndex = 0;
        for (int i = 0; i < stores.length; i++) {
            if (stores[i].equals(storeHandling)) {
                spinnerIndex = i;
                break;
            }
        }
        storesSpinner.setSelection(spinnerIndex);

        Spinner custType = view.findViewById(R.id.cust_type);
        Spinner custSource = view.findViewById(R.id.customerSource);

        custType.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting(Utils.CUSTOMER_TYPE)));
        custType.setSelection(1);
        custSource.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting("CustomerSource")));
        int spinnerSelection = new ArrayList<>(Arrays.asList(Utils.getSetSetting("CustomerSource"))).indexOf(customerSource);
        custSource.setSelection(spinnerSelection);
        ((CheckBox) view.findViewById(R.id.interest_bf)).setChecked(isCheckedBF);
        ((CheckBox) view.findViewById(R.id.interest_al)).setChecked(isCheckedAL);
        ((CheckBox) view.findViewById(R.id.interest_bj)).setChecked(isCheckedBJ);
        ((CheckBox) view.findViewById(R.id.interest_other)).setChecked(isCheckedOther);

        final Calendar myCalendar = Calendar.getInstance();
        final EditText wd1 = view.findViewById(R.id.wedding_date);
        final EditText wd2 = view.findViewById(R.id.wedding_date2);
        final DatePickerDialog.OnDateSetListener date, date2;
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                wd1.setText(Utils.shortDateForDisplay(myCalendar.getTime()));
            }
        };
        wd1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CustomerDetailActivity.this, date, myCalendar
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
                wd2.setText(Utils.shortDateForDisplay(myCalendar.getTime()));
            }
        };
        wd2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CustomerDetailActivity.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        builder.setView(view);
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name1 = ((EditText) view.findViewById(R.id.name1)).getText().toString();
                name2 = ((EditText) view.findViewById(R.id.name2)).getText().toString();
                address = ((EditText) view.findViewById(R.id.address)).getText().toString();
                address2 = ((EditText) view.findViewById(R.id.address2)).getText().toString();
                email = ((EditText) view.findViewById(R.id.email)).getText().toString();
                email2 = ((EditText) view.findViewById(R.id.email2)).getText().toString();
                tel = ((EditText) view.findViewById(R.id.tel)).getText().toString();
                tel2 = ((EditText) view.findViewById(R.id.tel2)).getText().toString();
                newWeddingDate = ((EditText) view.findViewById(R.id.wedding_date)).getText().toString();
                newWeddingDate2 = ((EditText) view.findViewById(R.id.wedding_date2)).getText().toString();
                type = ((Spinner) view.findViewById(R.id.cust_type)).getSelectedItem().toString();
                whatsapp1 = ((CheckBox) view.findViewById(R.id.whatsapp1)).isChecked();
                whatsapp2 = ((CheckBox) view.findViewById(R.id.whatsapp2)).isChecked();
                whatsapp1String = String.valueOf(whatsapp1);
                whatsapp2String = String.valueOf(whatsapp2);
                customerSource = ((Spinner) view.findViewById(R.id.customerSource)).getSelectedItem().toString();
                customerLanguage = ((Spinner) view.findViewById(R.id.languageSpinner)).getSelectedItem().toString();
                storeHandling = ((Spinner) view.findViewById(R.id.storesSpinner)).getSelectedItem().toString();
                String sourceRemarks = ((EditText) view.findViewById(R.id.sourceRemark)).getText().toString();
                String interest = "";
                if (((CheckBox) view.findViewById(R.id.interest_bf)).isChecked()) {
                    interest += view.findViewById(R.id.interest_bf).getTag();
                }
                if (((CheckBox) view.findViewById(R.id.interest_al)).isChecked()) {
                    interest += view.findViewById(R.id.interest_al).getTag();
                }
                if (((CheckBox) view.findViewById(R.id.interest_bj)).isChecked()) {
                    interest += view.findViewById(R.id.interest_bj).getTag();
                }
                if (((CheckBox) view.findViewById(R.id.interest_other)).isChecked()) {
                    interest += view.findViewById(R.id.interest_other).getTag();
                }

                new UpdateCustomer().execute(name1, name2, address, email, tel, newWeddingDate,
                        address2, email2, tel2, newWeddingDate2, type, interest, whatsapp1String, whatsapp2String, customerSource, sourceRemarks, customerLanguage, storeHandling);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void addHistory(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle(getString(R.string.add_history));
        final View view = inflater.inflate(R.layout.dialog_add_history, null);
        if (!"".equals(msg))
            ((TextView) view.findViewById(R.id.remark)).setText(msg);
        ((Spinner) view.findViewById(R.id.hist_type)).setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                new String[]{Utils.HISTTYPE_ACTION, Utils.HISTTYPE_REMARK, Utils.HISTTYPE_Visit}));
        builder.setView(view);
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText remarkView = view.findViewById(R.id.remark);
                String remark = remarkView.getText().toString();
                DatePicker dateP = view.findViewById(R.id.date);
                Calendar c = Calendar.getInstance();
                c.set(dateP.getYear(), dateP.getMonth(), dateP.getDayOfMonth());

                new AddHistory().execute(mainCustomerNumber,
                        Utils.shortDateForInsert(c.getTime()),
                        remark);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void addRDV() {
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(Events.TITLE, "RDV " + name1 + "/" + name2)
                .putExtra(Events.DESCRIPTION, "RDV: " + name1 + ", " + name2)
                .putExtra(Events.EVENT_LOCATION, "Agua De Oro")
                .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
                .putExtra(Intent.EXTRA_EMAIL, email);
        startActivity(intent);
    }

    public void editHistory(int position) {
        String remark = historyList.get(position)[1];
        final String id = historyList.get(position)[2];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle(getString(R.string.edit_hist));
        final View view = inflater.inflate(R.layout.dialog_edit_history, null);
        ((TextView) view.findViewById(R.id.remark)).setText(remark);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newRemark = ((EditText) view.findViewById(R.id.remark)).getText().toString();
                new UpdateHistory().execute(id, newRemark);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void displayPicDialog(int position) {
        ArrayList<String> picnames = new ArrayList<String>();
        final String histID = historyList.get(position)[2];
        new File(Environment.getExternalStorageDirectory()
                + "/04_pics/").mkdir();
        File path = new File(Environment.getExternalStorageDirectory()
                + "/04_pics/");
        for (String name : path.list()) {
            new File(path, name).delete();
        }

        Utils.loadHistoryPic(histID, path.getAbsolutePath() + "/" + histID);
        for (String name : path.list()) {
            if (name.contains(histID))
                picnames.add(path.getAbsolutePath() + "/" + name);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        GridView view = (GridView) inflater.inflate(R.layout.dialog_pic_list, null);
        view.setAdapter(new ImagesAdapter(this, path.getAbsolutePath(), picnames));


        builder.setView(view);
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                Intent chooser = Intent.createChooser(intent, CustomerDetailActivity.this.getText(R.string.select_photo));
                PackageManager pm = CustomerDetailActivity.this.getPackageManager();
                if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{camera});
                }
                CustomerDetailActivity.this.startActivityForResult(chooser, Integer.parseInt(histID));

            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog showPicDialog = builder.create();
        showPicDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.SEND_THANKS_CODE) {
            addHistory(getString(R.string.sent_thank));
        }
        if (data == null) {
            return;
        }
        String uri = Utils.getPicPath(data.getData());
        new SaveImageTask().execute("" + requestCode, uri);

    }

    public void viewActions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle(getString(R.string.viewaction));
        final ListView view = (ListView) inflater.inflate(R.layout.dialog_view_actions, null);
        view.setAdapter(new CustomerHistoryListAdapter(this, customerActionList, true));
        builder.setView(view);
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> newActions = new ArrayList<String>();
                for (int i = 0; i < customerActionList.size(); ++i) {
                    if (customerActionList.get(i)[2].isEmpty()) {
                        if (((CheckBox) view.getChildAt(i).findViewById(R.id.checkbox)).isChecked()) {
                            newActions.add(customerActionList.get(i)[1]);
                        }
                    }
                }
                System.out.println(newActions);
                new AddActions().execute(newActions.toArray(new String[0]));
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/html");
        emailIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{email, email2});
        startActivity(emailIntent);

    }

    class ListDetails extends AsyncTask<Boolean, String, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            boolean success = false;
            orderList.clear();
            customerActionList = new ArrayList<>();
            historyList.clear();

            if (mainCustomerNumber != null) {
                String query = "select ID from CustomerRelationship where Customer1 = " + mainCustomerNumber
                        + " or Customer2 = " + mainCustomerNumber;
                Query q = new Query(query);
                if (!q.execute()) return false;
                ArrayList<Map<String, String>> result = q.getRes();
                customerRelationshipID = result.get(0).get("0");
            }
            String query = "select * from Customer where CustomerNumber in "
                    + "(select distinct c.CustomerNumber from Customer c, "
                    + "CustomerRelationship cr where "
                    + "(c.CustomerNumber = cr.Customer1 "
                    + "or c.CustomerNumber = cr.Customer2)and cr.ID = " + customerRelationshipID + ") order by CustomerNumber asc";
            Query q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            if (result.size() == 0) {
                return false;
            }
            customerNumbers = "(" + result.get(0).get(Utils.CUST_NO);
            mainCustomerNumber = result.get(0).get(Utils.CUST_NO);
            name1 = result.get(0).get(Utils.CUST_NAME);
            address = (result.get(0).get(Utils.ADDR));
            email = (result.get(0).get(Utils.EMAIL));
            tel = (result.get(0).get(Utils.TEL));
            weddingDate = Utils.shortDateFromDB(result.get(0).get(Utils.WEDDING_DATE));
            weddingDate2 = Utils.shortDateFromDB(result.get(0).get(Utils.WEDDING_DATE2));
            type = result.get(0).get(Utils.TYPE);
            isCheckedBF = result.get(0).get(Utils.INTEREST).contains("1");
            isCheckedAL = result.get(0).get(Utils.INTEREST).contains("2");
            isCheckedBJ = result.get(0).get(Utils.INTEREST).contains("3");
            isCheckedOther = result.get(0).get(Utils.INTEREST).contains("4");
            whatsapp1 = result.get(0).get("WhatsApp").equals("1");
            customerSource = result.get(0).get("Channel");
            customerSourceRemark = result.get(0).get("Source");
            storeHandling = result.get(0).get("Store");
            customerLanguage = result.get(0).get("Language");
            if (result.size() > 1) {
                name2 = result.get(1).get(Utils.CUST_NAME);
                customerNumbers += "," + result.get(1).get(Utils.CUST_NO) + ")";
                otherCustomerNumber = result.get(1).get(Utils.CUST_NO);
                address2 = (result.get(1).get(Utils.ADDR));
                email2 = (result.get(1).get(Utils.EMAIL));
                tel2 = (result.get(1).get(Utils.TEL));
                whatsapp2 = result.get(1).get("WhatsApp").equals("1");
            } else {
                //legacy data, must add 2nd customer to Customer and CustomerRelationship
                query = "select max(CustomerNumber) from Customer";
                q = new Query(query);
                success = q.execute();
                if (!success) {
                    return false;
                }
                result = q.getRes();
                Integer custNo = 1;
                if (Utils.isInteger(result.get(0).get("0"))) {
                    custNo = Integer.parseInt(result.get(0).get("0")) + 1;
                }
                otherCustomerNumber = custNo.toString();
                query = "insert into Customer(CustomerNumber) "
                        + "values(" + custNo + ")";
                q = new Query(query);
                success = q.execute();
                if (!success) {
                    return false;
                }
                query = "update CustomerRelationship set Customer2 = " + custNo
                        + " where ID = " + customerRelationshipID;
                q = new Query(query);
                success = q.execute();
                if (!success) {
                    return false;
                }

                customerNumbers += "," + custNo + ")";
            }

            query = "select * from CustomerHistory where CustomerNumber in " + customerNumbers
                    + " order by HistoryDate desc, ID desc";
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            result = q.getRes();
            for (int i = 0; i < result.size(); i++) {
                String[] hist = new String[3];
                hist[0] = result.get(i).get(Utils.HISTORY_DATE);
                hist[1] = result.get(i).get(Utils.REMARK);
                hist[2] = result.get(i).get(Utils.ID);
                historyList.add(hist);
            }
            String filter = "";
            if (!args[0]) {
                filter = " and OrderStatus <> 'Livré-Fermé' ";
            }
            query = "select * from MainOrder where CustomerNumber in " + customerNumbers + "" + filter + " order by OrderDate desc";
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            result = q.getRes();
            for (int i = 0; i < result.size(); i++) {
                String[] order = new String[9];
                order[0] = result.get(i).get(Utils.ORDER_NO);
                order[1] = result.get(i).get(Utils.ORD_STT);
                order[2] = result.get(i).get(Utils.ORD_DT);
                order[3] = result.get(i).get(Utils.DEADLINE);
                order[4] = result.get(i).get(Utils.TOTAL);
                order[5] = result.get(i).get(Utils.REMAIN);
                order[7] = result.get(i).get(Utils.ORDER_TYPE);
                order[8] = result.get(i).get(Utils.SELLER);
                orderList.add(order);
            }

            //retrieving customer actions
            String[] actionTypes = Utils.getSetSetting(Utils.HISTTYPE_ACTION);
            query = "select * from CustomerHistory where CustomerNumber in " + customerNumbers
                    + " and HistoryType = 'Action' order by HistoryDate desc";
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            result = q.getRes();
            customerActionList = new ArrayList<String[]>();
            for (int i = 0; i < actionTypes.length; i++) {
                String[] action = new String[3];
                action[0] = "";
                action[1] = actionTypes[i];
                action[2] = "";
                for (int ii = 0; ii < result.size(); ii++) {
                    if (result.get(ii).get(Utils.REMARK).equals(actionTypes[i])) {
                        action[0] = result.get(ii).get(Utils.HISTORY_DATE);
                        action[1] = actionTypes[i];
                        action[2] = "1";
                        break;
                    }
                }
                customerActionList.add(action);
            }
            languages = Utils.getSetSetting("CustomerLanguages");
            stores = Utils.getSetSetting("Stores");
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                setListAdapter(new OrderListAdapter(
                        CustomerDetailActivity.this, orderList, false, true, false));
                ListView hList = findViewById(R.id.history_list);
                hList.setAdapter(new CustomerHistoryListAdapter(
                        CustomerDetailActivity.this, historyList, false));
                hList.setOnItemLongClickListener(new OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                                   int position, long id) {
                        editHistory(position);
                        return true;
                    }
                });

                hList.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        displayPicDialog(position);
                    }
                });

                ((TextView) findViewById(R.id.name1)).setText(name1);
                ((TextView) findViewById(R.id.address)).setText(address);
                ((TextView) findViewById(R.id.address2)).setText(address2);
                ((TextView) findViewById(R.id.email)).setText(email);
                ((TextView) findViewById(R.id.email2)).setText(email2);
                ((TextView) findViewById(R.id.tel)).setText(tel);
                ((TextView) findViewById(R.id.tel2)).setText(tel2);
                ((TextView) findViewById(R.id.wedding_date)).setText(weddingDate);
                ((TextView) findViewById(R.id.wedding_date2)).setText(weddingDate2);
                ((TextView) findViewById(R.id.name2)).setText(name2);
                //((TextView) findViewById(R.id.invoiceDate)).setText(type);
                ((CheckBox) findViewById(R.id.interest_bf)).setChecked(isCheckedBF);
                ((CheckBox) findViewById(R.id.interest_al)).setChecked(isCheckedAL);
                ((CheckBox) findViewById(R.id.interest_bj)).setChecked(isCheckedBJ);
                ((CheckBox) findViewById(R.id.interest_other)).setChecked(isCheckedOther);
                ((CheckBox) findViewById(R.id.whatsapp1)).setChecked(whatsapp1);
                ((CheckBox) findViewById(R.id.whatsapp2)).setChecked(whatsapp2);
                ((TextView) findViewById(R.id.sourceTextView)).setText(customerSource);
                ((TextView) findViewById(R.id.storeHandlingCustomer)).setText(storeHandling);
                ((TextView) findViewById(R.id.customerLanguage)).setText(customerLanguage);
            } else {
                Toast.makeText(CustomerDetailActivity.this,
                        getString(R.string.error_retrieving_data), Toast.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }

    class AddHistory extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String query = "insert into CustomerHistory(CustomerNumber, HistoryDate, Remark)"
                    + "values("
                    + args[0]
                    + ", "
                    + Utils.escape(args[1])
                    + ", "
                    + Utils.escape(args[2])
                    + ")";
            Query q = new Query(query);
            return q.execute();
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                CustomerDetailActivity.this.recreate();
            } else {
                Toast.makeText(CustomerDetailActivity.this,
                        getString(R.string.error_retrieving_data), Toast.LENGTH_LONG).show();
            }
        }
    }

    class UpdateCustomer extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String weddingDate = "null";
            String weddingDate2 = "null";
            Log.d("dates", "" + args[5] + " " + args[9]);
            if (args[5].length() > 0) {
                if (args[5].contains(".")) {
                    newWeddingDate = newWeddingDate.replace(".", "/");
                    weddingDate = Utils.escape(Utils.shortDateForInsert(args[5].replace(".", "/")));
                } else if (args[5].contains("-")) {
                    newWeddingDate = newWeddingDate.replace("-", "/");
                    weddingDate = Utils.escape(Utils.shortDateForInsert(args[5].replace("-", "/")));
                } else {
                    weddingDate = Utils.escape(Utils.shortDateForInsert(args[5]));

                }
            }
            if (args[9].length() > 0) {
                if (args[9].contains(".")) {
                    newWeddingDate2 = newWeddingDate2.replace(".", "/");
                    weddingDate2 = Utils.escape(Utils.shortDateForInsert(args[9].replace(".", "/")));
                } else if (args[9].contains("-")) {
                    newWeddingDate2 = newWeddingDate2.replace("-", "/");
                    weddingDate2 = Utils.escape(Utils.shortDateForInsert(args[9].replace("-", "/")));
                } else {
                    weddingDate2 = Utils.escape(Utils.shortDateForInsert(args[9]));

                }
            }
            String query = "update Customer set CustomerName = " + Utils.escape(args[0]) + ","
                    + "Address = " + Utils.escape(args[2]) + ","
                    + "Email = " + Utils.escape(args[3]) + ","
                    + "Tel = " + Utils.escape(args[4]) + ","
                    + "Type = " + Utils.escape(args[10]) + ","
                    + "Interest = " + Utils.escape(args[11]) + ","
                    + "WeddingDate = " + weddingDate + ","
                    + "WeddingDate2 = " + weddingDate2 + ", "
                    + "WhatsApp = " + args[12] + ", "
                    + "Channel = " + Utils.escape(args[14]) + ", "
                    + "Source = " + Utils.escape(args[15]) + ", "
                    + "Language = " + Utils.escape(args[16]) + ", "
                    + "Store = " + Utils.escape(args[17]) + " "
                    + "Where CustomerNumber = " + mainCustomerNumber;
            Query q = new Query(query);
            if (otherCustomerNumber == null) {
                return q.execute();
            }
            boolean success = q.execute();
            if (!success) {
                return false;
            }
            query = "update Customer set CustomerName = " + Utils.escape(args[1]) + ","
                    + "Address = " + Utils.escape(args[6]) + ","
                    + "Email = " + Utils.escape(args[7]) + ","
                    + "Tel = " + Utils.escape(args[8]) + ","
                    + "Type = " + Utils.escape(args[10]) + ","
                    + "Interest = " + Utils.escape(args[11]) + ","
                    + "WeddingDate = " + weddingDate + ","
                    + "WeddingDate2 = " + weddingDate2 + ", "
                    + "WhatsApp = " + args[13] + ", "
                    + "Channel = " + Utils.escape(args[14]) + ", "
                    + "Source = " + Utils.escape(args[15]) + " "
                    + "Where CustomerNumber = " + otherCustomerNumber;

            q = new Query(query);
            return q.execute();
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                //update wedding calendar
                if (!newWeddingDate.equals(weddingDate) && newWeddingDate.length() > 0) {
                    String desc = "Name 1:" + name1;
                    if (name2 != null)
                        desc += "\nName 2:" + name2;
                    desc += "\nDate:" + newWeddingDate;
                    Utils.insertEvent(Utils.shortDateForInsert(newWeddingDate), "Wed. " + name1, desc, true);
                }
                if (!newWeddingDate2.equals(weddingDate2) && newWeddingDate2.length() > 0) {
                    String desc = "Name 1:" + name1;
                    if (name2 != null)
                        desc += "\nName 2:" + name2;
                    desc += "\nDate:" + newWeddingDate2;
                    Utils.insertEvent(Utils.shortDateForInsert(newWeddingDate2), "Wed. " + name1, desc, true);
                }
                CustomerDetailActivity.this.recreate();
            } else {
                Toast.makeText(CustomerDetailActivity.this,
                        getString(R.string.error_retrieving_data), Toast.LENGTH_LONG).show();
            }
        }
    }

    class UpdateHistory extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String query = "update CustomerHistory set Remark = " + Utils.escape(args[1])
                    + " Where ID = " + args[0];
            Query q = new Query(query);
            return q.execute();
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                CustomerDetailActivity.this.recreate();
            } else {
                Toast.makeText(CustomerDetailActivity.this,
                        getString(R.string.error_retrieving_data), Toast.LENGTH_LONG).show();
            }
        }
    }

    public class SaveImageTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            Bitmap bp = BitmapFactory.decodeFile(args[1]);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            String img = Base64.encodeToString(stream.toByteArray(),
                    Base64.DEFAULT);
            Query q = new Query("insert into CustomerHistoryPic(HistoryID, Image) values("
                    + args[0] + "," + Utils.escape(img) + ")");
            return q.execute();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(CustomerDetailActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class AddActions extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String date = Utils.shortDateForInsert(Calendar.getInstance().getTime());
            for (int i = 0; i < args.length; ++i) {
                String query = "insert into CustomerHistory(CustomerNumber, HistoryType, HistoryDate, Remark)"
                        + "values("
                        + mainCustomerNumber
                        + ", "
                        + "'Action'"
                        + ", "
                        + Utils.escape(date)
                        + ", "
                        + Utils.escape(args[i])
                        + ")";
                Query q = new Query(query);
                System.out.println(query);
                if (!q.execute())
                    return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                CustomerDetailActivity.this.recreate();
            } else {
                Toast.makeText(CustomerDetailActivity.this,
                        getString(R.string.error_retrieving_data), Toast.LENGTH_LONG).show();
            }
        }
    }
}
