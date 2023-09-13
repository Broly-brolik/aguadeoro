package com.aguadeoro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.PricesListAdapter;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorActivity extends Activity {

    private View wheelView;
    private View mainView;
    private ArrayList<String> rates;
    private Double rate;
    private String[] settingsValues, settingsPrice, preparationPrices;
    private String autoHeight, baseRingPrice, autoWidth, autoSize, autoStoneSize, autoSetting, range, settingPrice, stonePrice, autoStoneNumber, premium, calc, codeFromInventory, carat, prMelee, metal;
    private double preliminaryRes, finalRes, ringPriceW, ringPriceY, ringPriceR, ringPriceP;
    private String tempAutoStoneNumber;
    private double[] prems, carats;
    private boolean isMine, hasColor;
    private EditText stoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        Intent intent = getIntent();
        if (intent != null) {
            this.codeFromInventory = intent.getStringExtra("InventoryCode");

        }
        final EditText ringSize = findViewById(R.id.ringSize);
        EditText ringWidth = findViewById(R.id.ringWidth);
        final EditText ringHeight = findViewById(R.id.ringHeight);
        final EditText catCode = findViewById(R.id.productCode);
        ImageView autofill = findViewById(R.id.autofill);
        final EditText stoneNumber = findViewById(R.id.stonesNumber);
        this.stoneNumber = stoneNumber;
        ImageView stoneNumberAutofill = findViewById(R.id.stoneAutoFill);
        final EditText ringPrice = findViewById(R.id.ringPrice);
        final EditText ecart = findViewById(R.id.ecart);
        Button calculate = findViewById(R.id.calculate);
        final EditText stoneSize = findViewById(R.id.stoneSize);
        final Spinner settings = findViewById(R.id.settings_spinner);
        final CheckBox mine = findViewById(R.id.mine);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.metal_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        showProgress(true);
        new PopulateSpinner().execute();
        autofill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AutoFill().execute(catCode.getText().toString());
            }
        });
        stoneNumberAutofill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(ringSize.getText().toString().equals("")) && !(ringHeight.getText().toString().equals("")) && !(stoneSize.getText().toString().equals(""))) {
                    if (ecart.getText().toString().equals("")) {
                        ecart.setText("0");
                    }
                    Matcher matcher = Pattern.compile("(0|[1-9]\\d*)(\\.\\d+)+?(?=m)").matcher(stoneSize.getText().toString().replaceAll(" ", ""));
                    double stoneDiameter = 0.0;
                    autoStoneSize = "0.0";
                    if (matcher.find()) {
                        stoneDiameter = Double.parseDouble(matcher.group());
                        autoStoneSize = String.valueOf(stoneDiameter);
                    }
                    double res = (((Double.parseDouble(ringSize.getText().toString()) / Math.PI) + (Double.parseDouble(ringHeight.getText().toString()) * 2)) * Math.PI) /
                            (stoneDiameter + stoneDiameter * Double.parseDouble(ecart.getText().toString()) / 100);
                    stoneNumber.setText(String.valueOf((int) res));
                } else {
                    Toast.makeText(CalculatorActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ecart.getText().toString().equals("")) {
                    ecart.setText("0");
                }
                autoStoneNumber = stoneNumber.getText().toString();
                Matcher matcher = Pattern.compile("(0|[1-9]\\d*)(\\.\\d+)+?(?=m)").matcher(stoneSize.getText().toString().replaceAll(" ", ""));
                double stoneDiameter = 0.0;
                autoStoneSize = "0.0";
                if (matcher.find()) {
                    stoneDiameter = Double.parseDouble(matcher.group());
                    autoStoneSize = String.valueOf(stoneDiameter);
                }
                if (!(stoneNumber.getText().toString().equals("") || stoneSize.getText().toString().equals("")
                        || ringPrice.getText().toString().equals(""))) {
                    Toast.makeText(CalculatorActivity.this, "Calculating... please wait for the result to appear", Toast.LENGTH_LONG).show();
                    isMine = mine.isChecked();

                    if (metal.contains("Yellow")) {
                        ringPriceW = Double.parseDouble(ringPrice.getText().toString()) / 0.95;
                        ringPriceY = Double.parseDouble(ringPrice.getText().toString());
                        ringPriceR = Double.parseDouble(ringPrice.getText().toString()) / 0.95 * 1.1;
                        ringPriceP = Double.parseDouble(ringPrice.getText().toString()) / 0.95 * 1.35;

                    } else if (metal.contains("Red")) {
                        ringPriceW = Double.parseDouble(ringPrice.getText().toString()) / 1.1;
                        ringPriceY = Double.parseDouble(ringPrice.getText().toString()) / 1.1 * 0.95;
                        ringPriceR = Double.parseDouble(ringPrice.getText().toString());
                        ringPriceP = Double.parseDouble(ringPrice.getText().toString()) / 1.1 * 1.35;
                    } else if (metal.contains("Platinum")) {
                        ringPriceW = Double.parseDouble(ringPrice.getText().toString()) / 1.35;
                        ringPriceY = Double.parseDouble(ringPrice.getText().toString()) / 1.35 * 0.95;
                        ringPriceR = Double.parseDouble(ringPrice.getText().toString()) / 1.35 * 1.1;
                        ringPriceP = Double.parseDouble(ringPrice.getText().toString());
                    } else {
                        ringPriceW = Double.parseDouble(ringPrice.getText().toString());
                        ringPriceY = Double.parseDouble(ringPrice.getText().toString()) * 0.95;
                        ringPriceR = Double.parseDouble(ringPrice.getText().toString()) * 1.1;
                        ringPriceP = Double.parseDouble(ringPrice.getText().toString()) * 1.35;
                    }
                    ((TextView) findViewById(R.id.textView8)).setText("25% | " + (int) ((Double.parseDouble(autoStoneNumber)) * 0.25));
                    ((TextView) findViewById(R.id.textView9)).setText("50% | " + (int) ((Double.parseDouble(autoStoneNumber)) * 0.50));
                    ((TextView) findViewById(R.id.textView10)).setText("75% | " + (int) ((Double.parseDouble(autoStoneNumber)) * 0.75));
                    ((TextView) findViewById(R.id.textView11)).setText("90% | " + (int) ((Double.parseDouble(autoStoneNumber)) * 0.9));
                    ((TextView) findViewById(R.id.textView12)).setText("100% | " + (int) ((Double.parseDouble(autoStoneNumber)) * 1));

                    new getSettingPrice().execute(settings.getSelectedItem().toString());
                } else {
                    Toast.makeText(CalculatorActivity.this, "Error : all required fields must be filled", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    class getSettingPrice extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String query = "select * from Settings where Type = " + Utils.escape(params[0]);
            Query q = new Query(query);
            boolean s = q.execute();
            if (!s || q.getRes().size() < 1) {
                return false;
            }
            settingPrice = q.getRes().get(0).get("Price");
            Log.d("settingprice", "" + settingPrice);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            new getStonePrice().execute();
        }
    }

    class getStonePrice extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String query;
            if (isMine && !hasColor) {
                query = "select * from PU_Mined order by ABS(Diametre-" + autoStoneSize + ")";

            } else if (!isMine && !hasColor) {
                query = "select * from PU_LV order by ABS(Diametre-" + autoStoneSize + ")";
            } else if (isMine && hasColor) {
                query = "select * from PU_Mined_ColoredStones order by ABS(Diametre-" + autoStoneSize + ")";
            } else {
                query = "select * from PU_LV_ColoredStones order by ABS(Diametre-" + autoStoneSize + ")";
            }
            Query q = new Query(query);
            boolean s = q.execute();
            if (!s) {
                return false;
            }
            ArrayList<Map<String, String>> res = q.getRes();
            stonePrice = res.get(0).get("TW/VS");
            carat = res.get(0).get("Carat");
            Log.d("price", "" + stonePrice);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            String res11 = "0";
            String res12 = "0";
            String res13 = "0";
            String res14 = "0";
            try {
                res11 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.25,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceW,
                        Double.parseDouble(prMelee)
                ));
                res12 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.25,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceY,
                        Double.parseDouble(prMelee)
                ));
                res13 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.25,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceR,
                        Double.parseDouble(prMelee)
                ));
                res14 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.25,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceP,
                        Double.parseDouble(prMelee)
                ));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Log.e("error", "" + e);
            }
            String res21 = "0";
            String res22 = "0";
            String res23 = "0";
            String res24 = "0";
            try {
                res21 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.50,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceW,
                        Double.parseDouble(prMelee)
                ));
                res22 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.50,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceY,
                        Double.parseDouble(prMelee)
                ));
                res23 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.50,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceR,
                        Double.parseDouble(prMelee)
                ));
                res24 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.50,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceP,
                        Double.parseDouble(prMelee)
                ));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Log.e("error", "" + e);
            }
            String res31 = "0";
            String res32 = "0";
            String res33 = "0";
            String res34 = "0";
            try {
                res31 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.75,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceW,
                        Double.parseDouble(prMelee)
                ));
                res32 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.75,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceY,
                        Double.parseDouble(prMelee)
                ));
                res33 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.75,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceR,
                        Double.parseDouble(prMelee)
                ));
                res34 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.75,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceP,
                        Double.parseDouble(prMelee)
                ));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Log.e("error", "" + e);
            }
            String res41 = "0";
            String res42 = "0";
            String res43 = "0";
            String res44 = "0";
            try {
                res41 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.90,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceW,
                        Double.parseDouble(prMelee)
                ));
                res42 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.90,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceY,
                        Double.parseDouble(prMelee)
                ));
                res43 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.90,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceR,
                        Double.parseDouble(prMelee)
                ));
                res44 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        0.90,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceP,
                        Double.parseDouble(prMelee)
                ));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Log.e("error", "" + e);
            }
            String res51 = "0";
            String res52 = "0";
            String res53 = "0";
            String res54 = "0";
            try {
                res51 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        1.0,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceW,
                        Double.parseDouble(prMelee)
                ));
                res52 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        1.0,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceY,
                        Double.parseDouble(prMelee)
                ));
                res53 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        1.0,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceR,
                        Double.parseDouble(prMelee)
                ));
                res54 = PricesListAdapter.roundPrice(calculateMeleePrice(
                        Double.parseDouble(stoneNumber.getText().toString()),
                        1.0,
                        Double.parseDouble(settingPrice),
                        Double.parseDouble(stonePrice),
                        ringPriceP,
                        Double.parseDouble(prMelee)
                ));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Log.e("error", "" + e);
            }

            ((TextView) findViewById(R.id.res11)).setText("" + res11 + ".-");
            ((TextView) findViewById(R.id.res12)).setText("" + res12 + ".-");
            ((TextView) findViewById(R.id.res13)).setText("" + res13 + ".-");
            ((TextView) findViewById(R.id.res14)).setText("" + res14 + ".-");
            ((TextView) findViewById(R.id.res21)).setText("" + res21 + ".-");
            ((TextView) findViewById(R.id.res22)).setText("" + res22 + ".-");
            ((TextView) findViewById(R.id.res23)).setText("" + res23 + ".-");
            ((TextView) findViewById(R.id.res24)).setText("" + res24 + ".-");
            ((TextView) findViewById(R.id.res31)).setText("" + res31 + ".-");
            ((TextView) findViewById(R.id.res32)).setText("" + res32 + ".-");
            ((TextView) findViewById(R.id.res33)).setText("" + res33 + ".-");
            ((TextView) findViewById(R.id.res34)).setText("" + res34 + ".-");
            ((TextView) findViewById(R.id.res41)).setText("" + res41 + ".-");
            ((TextView) findViewById(R.id.res42)).setText("" + res42 + ".-");
            ((TextView) findViewById(R.id.res43)).setText("" + res43 + ".-");
            ((TextView) findViewById(R.id.res44)).setText("" + res44 + ".-");
            ((TextView) findViewById(R.id.res51)).setText("" + res51 + ".-");
            ((TextView) findViewById(R.id.res52)).setText("" + res52 + ".-");
            ((TextView) findViewById(R.id.res53)).setText("" + res53 + ".-");
            ((TextView) findViewById(R.id.res54)).setText("" + res54 + ".-");
            ((TextView) findViewById(R.id.textView8)).setText("25% | " + (int) (Double.parseDouble(stoneNumber.getText().toString()) * 0.25) + " stones | " + String.format("%.2f", (Double.parseDouble(carat) * Double.parseDouble(autoStoneNumber) * 0.25)) + " carats");
            ((TextView) findViewById(R.id.textView9)).setText("50% | " + (int) (Double.parseDouble(stoneNumber.getText().toString()) * 0.50) + " stones | " + String.format("%.2f", (Double.parseDouble(carat) * Double.parseDouble(autoStoneNumber) * 0.50)) + " carats");
            ((TextView) findViewById(R.id.textView10)).setText("75% | " + (int) (Double.parseDouble(stoneNumber.getText().toString()) * 0.75) + " stones | " + String.format("%.2f", (Double.parseDouble(carat) * Double.parseDouble(autoStoneNumber) * 0.75)) + " carats");
            ((TextView) findViewById(R.id.textView11)).setText("90% | " + (int) (Double.parseDouble(stoneNumber.getText().toString()) * 0.9) + " stones | " + String.format("%.2f", (Double.parseDouble(carat) * Double.parseDouble(autoStoneNumber) * 0.9)) + " carats");
            ((TextView) findViewById(R.id.textView12)).setText("100% | " + (int) (Double.parseDouble(stoneNumber.getText().toString()) * 1) + " stones | " + String.format("%.2f", (Double.parseDouble(carat) * Double.parseDouble(autoStoneNumber) * 1)) + " carats");
            autoStoneNumber = tempAutoStoneNumber;
        }
    }

    class getRange extends AsyncTask<Double, Double, Boolean> {
        @Override
        protected Boolean doInBackground(Double... params) {
            String query;
            Double prem = params[0];
            query = "select * from MeleeRate where " + (int) Math.abs(prem) + " <= MeleeRate.Range order by MeleeRate.Rate desc";
            Query q = new Query(query);
            boolean s = q.execute();
            if (!s) {
                return false;
            }
            rate = Double.parseDouble(q.getRes().get(0).get("Rate"));
            return true;
        }
    }

    private String calculateMeleePrice(Double stoneNumber, Double cover, Double setting, Double stonePrice, Double ringPrice, Double prMelee) throws ExecutionException, InterruptedException {
        double result = (setting + stonePrice);
        double finalStoneNumber = stoneNumber * cover;
        result = result * finalStoneNumber;
        new getRange().execute(result).get();
        result = result * rate;
        result = result * prMelee;
        Log.d("test", "(" + rate + "* ((" + stoneNumber + " * " + cover + ") * (" + setting + " + " + stonePrice + "))) * " + prMelee);
        result += ringPrice;
        return String.valueOf(result);
    }

    class PopulateSpinner extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String query = "select * from Settings";
            Query q = new Query(query);
            boolean s = q.execute();
            if (!s) {
                return false;
            }
            ArrayList<Map<String, String>> res = q.getRes();
            settingsValues = new String[res.size()];
            settingsPrice = new String[res.size()];
            for (int i = 0; i < res.size(); i++) {
                settingsValues[i] = res.get(i).get("Type");
                settingsPrice[i] = res.get(i).get("Price");
            }
            query = "select * from Preparation";
            q = new Query(query);
            s = q.execute();
            if (!s) {
                return false;
            }
            res = q.getRes();
            preparationPrices = new String[res.size()];
            for (int i = 0; i < res.size(); i++) {
                preparationPrices[i] = res.get(i).get("Cost");
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean success) {
            Spinner settings = findViewById(R.id.settings_spinner);
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(CalculatorActivity.this, android.R.layout.simple_spinner_item, settingsValues);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            settings.setAdapter(adapter);

            ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter(CalculatorActivity.this, android.R.layout.simple_spinner_item, preparationPrices);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            new FetchMarge().execute();
        }
    }

    class FetchMarge extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String query = "select top 1 * from Marge";
            Query q = new Query(query);
            boolean s = q.execute();
            return s && q.getRes().size() >= 1;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            showProgress(false);
            if (codeFromInventory != null) {
                if (!codeFromInventory.equals("")) {
                    EditText productCode = findViewById(R.id.productCode);
                    productCode.setText(codeFromInventory);
                    new AutoFill().execute(codeFromInventory);
                }
            }
        }
    }

    class AutoFill extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String query = "select top 1 * from Inventory where InventoryCode = " + Utils.escape(params[0]) + " or CatalogCode = " + Utils.escape(params[0]);
            Query q = new Query(query);
            boolean s = q.execute();
            if (!s) {
                return false;
            }
            ArrayList<Map<String, String>> res = q.getRes();
            if (res.size() == 0) {
                return false;
            }
            autoHeight = res.get(0).get("Height");
            autoSize = res.get(0).get("Size");
            autoWidth = res.get(0).get("Width");
            baseRingPrice = res.get(0).get("RingPrize");
            autoStoneSize = res.get(0).get("StoneSize");
            autoSetting = res.get(0).get("Setting");
            prMelee = res.get(0).get("PrMelee");
            metal = res.get(0).get("Color");
            return true;
        }


        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(CalculatorActivity.this, "Error could not retrieve matching data", Toast.LENGTH_SHORT).show();
            } else {
                ((EditText) findViewById(R.id.ringHeight)).setText(autoHeight);
                ((EditText) findViewById(R.id.ringPrice)).setText(baseRingPrice);
                ((EditText) findViewById(R.id.ringWidth)).setText(autoWidth);
                ((EditText) findViewById(R.id.ringSize)).setText(autoSize);
                ((EditText) findViewById(R.id.stoneSize)).setText(autoStoneSize);
                if (autoSetting.equals("")) {
                    Toast.makeText(CalculatorActivity.this, "error could not automatically select settings, please verify the selected setting", Toast.LENGTH_LONG).show();
                    ((Spinner) findViewById(R.id.settings_spinner)).setSelection(0);

                } else {
                    ((Spinner) findViewById(R.id.settings_spinner)).setSelection(Integer.parseInt(autoSetting) - 1);
                }

            }
        }
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
}
