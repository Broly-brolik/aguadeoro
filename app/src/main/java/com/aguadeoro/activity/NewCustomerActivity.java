package com.aguadeoro.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.Address;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.Event;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PhoneNumber;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NewCustomerActivity extends Activity {

    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date, date2;
    EditText wDate, wDate2, createdDate, createdTime;
    String today, timeNow;
    private View wheelView;
    private View mainView;
    String name1, name2, address, address2, email, email2, tel, tel2, type, channel, wDateString,
            wDate2String, language, store;
    private static final String APPLICATION_NAME = "Aguadeoro";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "googleTokens";
    private static final List<String> SCOPES = Arrays.asList(PeopleServiceScopes.CONTACTS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private Credential credentials;
    private final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private String whatsapp1, whatsapp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_new_customer);
        //new getCredentials().execute(HTTP_TRANSPORT);
        Spinner custType = findViewById(R.id.cust_type);
        Spinner custSource = findViewById(R.id.customerSource);
        custType.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting(Utils.CUSTOMER_TYPE)));
        custSource.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting("CustomerSource")));
        wheelView = findViewById(R.id.register_status);
        mainView = findViewById(R.id.register_form);
        myCalendar = Calendar.getInstance();
        today = Utils.shortDateForDisplay(myCalendar.getTime());
        timeNow = myCalendar.get(Calendar.HOUR_OF_DAY) + ":" + myCalendar.get(Calendar.MINUTE);
        wDate = findViewById(R.id.wedding_date);
        wDate2 = findViewById(R.id.wedding_date2);
        createdTime = findViewById(R.id.created_time);
        createdTime.setText(timeNow);
        createdDate = findViewById(R.id.created_date);
        createdDate.setText(today);
        Spinner languageSpinner = findViewById(R.id.languageSpinner);
        languageSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting("CustomerLanguages")));
        Spinner storeSpinner = findViewById(R.id.storesSpinner);
        storeSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Utils
                .getSetSetting("Stores")));
        for (int i = 0; i < Utils.getSetSetting("Stores").length; i++) {
            Log.d("STORES", Utils.getSetSetting("Stores")[i] + " " + Utils.getStringSetting("store_selected"));
            if (Utils.getSetSetting("Stores")[i].equals(Utils.getStringSetting("store_selected"))) {
                storeSpinner.setSelection(i);
                break;
            }
        }
        wDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewCustomerActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(wDate);
            }
        };
        wDate2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewCustomerActivity.this, date2, myCalendar
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
                updateLabel(wDate2);
            }
        };
    }

    public void updateLabel(EditText whichDate) {
        whichDate.setText(Utils.shortDateForDisplay(myCalendar.getTime()));
        //wDate.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return true;
    }

    public void saveCustomer(View view) {
        name1 = ((EditText) findViewById(R.id.name1)).getText().toString();
        if (name1.trim().equals("")) {
            Toast msg = Toast.makeText(this, "Customer Name", Toast.LENGTH_SHORT);
            msg.setGravity(Gravity.CENTER, 0, 0);
            msg.show();
            return;
        }
        name2 = ((EditText) findViewById(R.id.name2)).getText().toString();
        address = ((EditText) findViewById(R.id.address)).getText().toString();
        address2 = ((EditText) findViewById(R.id.address2)).getText().toString();
        email = ((EditText) findViewById(R.id.email)).getText().toString();
        email2 = ((EditText) findViewById(R.id.email2)).getText().toString();
        tel = ((EditText) findViewById(R.id.tel)).getText().toString();
        tel2 = ((EditText) findViewById(R.id.tel2)).getText().toString();
        wDateString = this.wDate.getText().toString();
        wDate2String = this.wDate2.getText().toString();
        type = ((Spinner) findViewById(R.id.cust_type)).getSelectedItem().toString();
        String sourceRemarks = ((EditText) findViewById(R.id.sourceRemark)).getText().toString();
        channel = ((Spinner) findViewById(R.id.customerSource)).getSelectedItem().toString();
        language = ((Spinner) findViewById(R.id.languageSpinner)).getSelectedItem().toString();
        store = ((Spinner) findViewById(R.id.storesSpinner)).getSelectedItem().toString();

/*		String source = ((RadioButton)findViewById(R.id.source_salon)).getText().toString();
        if (((RadioButton)findViewById(R.id.source_magasin)).isChecked()){
			source = ((RadioButton)findViewById(R.id.source_magasin)).getText().toString();
		}
		else if (((RadioButton)findViewById(R.id.source_website)).isChecked()){
			source = ((RadioButton)findViewById(R.id.source_website)).getText().toString();
		}
		else if (((RadioButton)findViewById(R.id.source_other)).isChecked()){
			source = ((TextView)findViewById(R.id.source_other_text)).getText().toString();
		}
*/
        String interest = "";
        if (((CheckBox) findViewById(R.id.interest_bf)).isChecked()) {
            interest += findViewById(R.id.interest_bf).getTag();
        }
        if (((CheckBox) findViewById(R.id.interest_al)).isChecked()) {
            interest += findViewById(R.id.interest_al).getTag();
        }
        if (((CheckBox) findViewById(R.id.interest_bj)).isChecked()) {
            interest += findViewById(R.id.interest_bj).getTag();
        }
        if (((CheckBox) findViewById(R.id.interest_other)).isChecked()) {
            interest += findViewById(R.id.interest_other).getTag();
        }
        whatsapp1 = String.valueOf(((CheckBox) findViewById(R.id.whatsapp1)).isChecked());
        whatsapp2 = String.valueOf(((CheckBox) findViewById(R.id.whatsapp2)).isChecked());

        final String fullCreatedDate = createdDate.getText().toString() + " "
                + createdTime.getText().toString();
        showProgress(true);
        final String finalInterest = interest;

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new SaveCustomerTask().execute(name1, name2, address, email, tel, wDateString, address2, email2,
                        tel2, wDate2String, type, channel, /*source,*/ finalInterest, fullCreatedDate, whatsapp1, whatsapp2, sourceRemarks, language, store);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(NewCustomerActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
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

    //bugged, check if custRelId is correct before using
    public void goToCustomerDetailActivity(String custRelID) {
        Intent intent = new Intent(this, CustomerDetailActivity.class);
        intent.putExtra(Utils.CUST_REL_ID, custRelID);
        startActivity(intent);
        finish();
    }
    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public class SaveCustomerTask extends AsyncTask<String, String, Boolean> {
        String custRelID, weddingDate, weddingDate2, title, description;

        @Override
        protected Boolean doInBackground(String... inputs) {
            if (!Utils.isOnline()) {
                return false;
            }

            boolean success = false;
            String query = "select max(CustomerNumber) from Customer";
            Query q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            int custNo;
            weddingDate = inputs[5];
            weddingDate2 = inputs[9];
            String wDate = inputs[5];
            String wDate2 = inputs[9];
            String createdDate = inputs[13];
            if (Utils.isInteger(result.get(0).get("0"))) {
                custNo = Integer.parseInt(result.get(0).get("0")) + 1;
            } else {
                Log.e("NEW CUSTOMER", "return is integer");
                return false;
            }
            if (wDate == null || wDate.length() == 0) {
                wDate = "null";
            } else {
                wDate = Utils.escape(Utils.shortDateForInsert(wDate));
                weddingDate = Utils.shortDateForInsert(weddingDate);
            }
            if (wDate2 == null || wDate2.length() == 0) {
                wDate2 = "null";
            } else {
                wDate2 = Utils.escape(Utils.shortDateForInsert(wDate2));
                weddingDate2 = Utils.shortDateForInsert(weddingDate2);
            }
            if (createdDate == null || createdDate.length() == 0) {
                createdDate = "null";
            } else {
                createdDate = Utils.escape(Utils.longDateForInsert(createdDate));
            }
            title = "Wed. " + inputs[0];
            description = "Name 1: " + inputs[0];
            if (inputs[1] != null) {
                description += "\nName 2: " + inputs[1];
            }
            description += "\nDate 1: " + weddingDate;
            description += "\nDate 2: " + weddingDate2;

            query = "insert into Customer(CustomerNumber, CustomerName, "
                    //+ "Address, Email, Tel, WeddingDate, CreatedDate, WeddingDate2, Type, Channel, Source, Interest) "
                    + "Address, Email, Tel, WeddingDate, CreatedDate, WeddingDate2, Type, Channel, Interest, WhatsApp, Source, Language, Store) "
                    + "values("
                    + custNo
                    + ", "
                    + Utils.escape(inputs[0])        //name
                    + ", "
                    + Utils.escape(inputs[2])        //address
                    + ", "
                    + Utils.escape(inputs[3])        //email
                    + ", "
                    + Utils.escape(inputs[4])        //tel
                    + ", "
                    + wDate        //wedding
                    + ", "
                    + createdDate           //created date
                    + ", "
                    + wDate2        //wedding
                    + ", "
                    + Utils.escape(inputs[10])    //customer type
                    + ", "
                    + Utils.escape(inputs[11])    //customer channel
                    + ", "
//					+ Utils.escape(inputs[12])	//customer source
//					+ ", "
                    + Utils.escape(inputs[12])    //customer interest
                    + " , "
                    + whatsapp1 + ", "
                    + Utils.escape(inputs[14])    //customer source
                    + ", " + Utils.escape(inputs[17])    //customer language
                    + ", " + Utils.escape(inputs[18])    //customer store
                    + " )";
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            query = "insert into Customer(CustomerNumber, CustomerName, "
//					+ "Address, Email, Tel, WeddingDate, CreatedDate, WeddingDate2, Type, Channel, Source, Interest) "
                    + "Address, Email, Tel, WeddingDate, CreatedDate, WeddingDate2, Type, Channel, Interest, WhatsApp, Source, Language, Store) "
                    + "values("
                    + (custNo + 1)
                    + ", "
                    + Utils.escape(inputs[1])    //name
                    + ", "
                    + Utils.escape(inputs[6])    //address
                    + ", "
                    + Utils.escape(inputs[7])    //email
                    + ", "
                    + Utils.escape(inputs[8])    //tel
                    + ", "
                    + wDate    //wdate
                    + ", "
                    + createdDate        //created
                    + ", "
                    + wDate2        //wedding
                    + ", "
                    + Utils.escape(inputs[10])    //customer type
                    + ", "
                    + Utils.escape(inputs[11])    //customer channel
                    + ", "
//					+ Utils.escape(inputs[12])	//customer source
//					+ ", "
                    + Utils.escape(inputs[12])    //customer interest
                    + ", " + whatsapp2 + ", "
                    + Utils.escape(inputs[14])    //customer source
                    + ", " + Utils.escape(inputs[17])    //customer language
                    + ", " + Utils.escape(inputs[18])    //customer store
                    + ")";
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            query = "insert into CustomerRelationship(Customer1, Customer2) values("
                    + (custNo) + ", " + (custNo + 1) + ")";
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }

            query = "select max(ID) from CustomerRelationship";
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            custRelID = q.getRes().get(0).get("0");
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                if (weddingDate != null && weddingDate.length() > 0) {
                    Utils.insertEvent(weddingDate, title, description, true);
                }
                if (weddingDate2 != null && weddingDate2.length() > 0) {
                    Utils.insertEvent(weddingDate2, title, description, true);
                }
                //new addContact().execute(custRelID);
                showProgress(false);
                finish();
                //goToCustomerDetailActivity(custRelID);
                goToHome();
            } else {
                Toast.makeText(NewCustomerActivity.this,
                        getString(R.string.error_retrieving_data), Toast.LENGTH_LONG).show();
            }
        }

    }


    private class addContact extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {

            PeopleService service = null;
            try {
                service = new PeopleService.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
                        .setApplicationName(APPLICATION_NAME)
                        .build();
                Person contactToCreate = new Person();
                Person contactToCreate2 = new Person();
                List<Name> names = new ArrayList<>();
                List<Name> names2 = new ArrayList<>();
                List<EmailAddress> emailAddresses = new ArrayList<>();
                List<EmailAddress> emailAddresses2 = new ArrayList<>();
                List<Address> addresses = new ArrayList<>();
                List<Address> addresses2 = new ArrayList<>();
                List<PhoneNumber> phoneNumbers = new ArrayList<>();
                List<PhoneNumber> phoneNumbers2 = new ArrayList<>();
                List<Event> events = new ArrayList<>();

                names.add(new Name().setFamilyName(name1));
                names2.add(new Name().setFamilyName(name2));

                emailAddresses.add(new EmailAddress().setValue(email));
                emailAddresses2.add(new EmailAddress().setValue(email2));

                addresses.add(new Address().setStreetAddress(address));
                addresses2.add(new Address().setStreetAddress(address2));

                phoneNumbers.add(new PhoneNumber().setValue(tel));
                phoneNumbers2.add(new PhoneNumber().setValue(tel2));

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date dateWedding = null;
                Date dateWedding2 = null;
                try {
                    if (!wDateString.equals("")) {
                        dateWedding = dateFormat.parse(wDateString);
                    }
                    if (!wDate2String.equals("")) {
                        dateWedding2 = dateFormat.parse(wDate2String);
                    }
                } catch (ParseException e) {
                    Log.e("date parsing", e.toString());
                }
                if (dateWedding != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateWedding);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    events.add(new Event().setType("Civil wedding").setDate(new com.google.api.services.people.v1.model.Date().setDay(day).setMonth(month).setYear(year)));
                }
                if (dateWedding2 != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateWedding2);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    events.add(new Event().setType("Ceremony wedding").setDate(new com.google.api.services.people.v1.model.Date().setDay(day).setMonth(month).setYear(year)));
                }
                contactToCreate.setNames(names);
                contactToCreate.setAddresses(addresses);
                contactToCreate.setPhoneNumbers(phoneNumbers);
                contactToCreate.setEmailAddresses(emailAddresses);
                contactToCreate.setEvents(events);

                contactToCreate2.setNames(names2);
                contactToCreate2.setAddresses(addresses2);
                contactToCreate2.setPhoneNumbers(phoneNumbers2);
                contactToCreate2.setEmailAddresses(emailAddresses2);
                contactToCreate2.setEvents(events);

                service.people().createContact(contactToCreate).execute();
                service.people().createContact(contactToCreate2).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return args[0];
        }

        @Override
        protected void onPostExecute(String s) {
            showProgress(false);
            finish();
            goToCustomerDetailActivity(s);
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    private class getCredentials extends AsyncTask<NetHttpTransport, Void, Credential> {

        @Override
        protected Credential doInBackground(NetHttpTransport... netHttpTransports) {
            try {
                return getCredentials(HTTP_TRANSPORT);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Credential credential) {
            if (credential != null) {
                credentials = credential;
            }
        }

    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = CustomerDetailActivity.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        File tokenFolder = new File(Environment.getExternalStorageDirectory() +
                File.separator + TOKENS_DIRECTORY_PATH);
        if (!tokenFolder.exists()) {
            tokenFolder.mkdirs();
        }
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(tokenFolder))
                .setAccessType("offline")
                .build();
        //LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        AuthorizationCodeInstalledApp ab = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()) {
            protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
                String url = (authorizationUrl.build());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        };
        return ab.authorize("471861201953-cb0lrcqm07t5ksrqvri3k337m4qoh4nl.apps.googleusercontent.com").setAccessToken("471861201953-cb0lrcqm07t5ksrqvri3k337m4qoh4nl.apps.googleusercontent.com");
    }
}
