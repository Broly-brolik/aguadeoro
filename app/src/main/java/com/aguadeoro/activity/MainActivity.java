package com.aguadeoro.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.BuildConfig;
import com.aguadeoro.R;
import com.aguadeoro.utils.Functions;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MainActivity extends Activity {
    MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.initializeUtils(this);
        PreferenceManager.setDefaultValues(this, R.xml.pref_thankyou, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_supplier_email, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_order_ready_email, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_order_closed_email, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_invoice_email, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_customer_email, true);
        if (Utils.getStringSetting("store_selected").equals("null")) {
            new FetchStores().execute();
        }
        activity = this;

        TextView appVersion = findViewById(R.id.textViewAppVersion);
        appVersion.setText("Version " + BuildConfig.VERSION_NAME);

        appVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                 Toast.makeText(getApplicationContext(), "whats new !", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("What's new ?");
                builder.setMessage(Functions.openAssetFile("whatsnew.txt", getApplicationContext()))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // START THE GAME!
                                dialog.cancel();
                            }
                        });

                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
//        Toast.makeText(this, BuildConfig.VERSION_NAME, Toast.LENGTH_SHORT).show();
        // Utils.test(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_lang_en) {
            Utils.setLocale(this, "en");
        }
        if (id == R.id.action_lang_fr) {
            Utils.setLocale(this, "fr");
        }

        return true;
    }

    public void customerActivity(View view) {
        Intent intent = new Intent(this, CustomerActivity.class);
        startActivity(intent);
        finish();
    }

    public void orderActivity(View view) {
        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
        finish();
    }

    public void reportActivity(View view) {
        Intent intent = new Intent(this, ReportActivity.class);
        startActivity(intent);
        finish();
    }

    public void sorderActivity(View view) {
        if (Utils.getStringSetting("store_selected").equals("Geneva")) {
            Intent intent = new Intent(this, SupplierOrderActivity.class);
//            Bundle b = new Bundle();
//            b.putString("sorder", "6493"); //Your id
//            intent.putExtras(b);
            startActivity(intent);
            //finish();
        } else {
            Toast.makeText(this, "This feature is not available for this store", Toast.LENGTH_SHORT).show();
        }

    }

    public void inventoryActivity(View view) {
        Intent intent = new Intent(this, InventoryActivity.class);
        startActivity(intent);
        //finish();
    }

    public void sellerActivity(View view) {
        Intent intent = new Intent(this, SellerActivity.class);
        startActivity(intent);
        finish();
    }

    public void activitiesActivity(View view) {
        Intent intent = new Intent(this, ActivitiesActivity.class);
        startActivity(intent);
        finish();
    }

    public void weddingActivity(View view) {
        Intent intent = new Intent(this, WeddingActivity.class);
        startActivity(intent);
        finish();
    }

    public void calendarActivity(View view) {
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, Calendar.getInstance().getTimeInMillis());
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        startActivity(intent);
    }

    public void calculatorActivity(View view) {
        Intent intent = new Intent(this, CalculatorActivity.class);
        startActivity(intent);
        //finish();
    }

    public void stockActivity(View view) {
        Intent intent = new Intent(this, StockActivity.class);
        startActivity(intent);
        finish();
    }

    public void StonesActivity(View view) {
        Intent intent = new Intent(this, StonesPricesActivity.class);
        startActivity(intent);
        //finish();
    }

    public void weddingFollowUp(View view) {
        Intent intent = new Intent(this, WeddingFollowUpActivity.class);
        startActivity(intent);
        finish();
    }

    private class FetchStores extends AsyncTask<Void, Void, ArrayList<Map<String, String>>> {
        @Override
        protected ArrayList<Map<String, String>> doInBackground(Void... params) {
            String query = "select * from OptionValues where Type = 'Stores'";
            Query q = new Query(query);
            q.execute();
            return q.getRes();
        }

        @Override
        protected void onPostExecute(ArrayList<Map<String, String>> res) {
            super.onPostExecute(res);
            if (res != null) {
                String[] stores = new String[res.size()];
                for (int i = 0; i < res.size(); i++) {
                    stores[i] = res.get(i).get("OptionValue");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select a store to continue");
                builder.setItems(stores, (dialog, which) -> {
                    boolean needLogin = stores[which].equals("Geneva");
                    if (needLogin) {
                        AlertDialog.Builder loginDialog = new AlertDialog.Builder(MainActivity.this);
                        loginDialog.setTitle("Please enter password");
                        LinearLayout layout = new LinearLayout(MainActivity.this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        final EditText password = new EditText(MainActivity.this);
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        layout.addView(password);
                        loginDialog.setView(layout);
                        loginDialog.setPositiveButton("OK", (dialog1, which1) -> {
                            if (password.getText().toString().equals("aguadmin21")) {
                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("store_selected", stores[which]);
                                editor.apply();
                                dialog.dismiss();
                                dialog1.dismiss();
                            } else {
                                Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                            }
                        });
                        loginDialog.show();
                    } else {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("store_selected", stores[which]);
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
