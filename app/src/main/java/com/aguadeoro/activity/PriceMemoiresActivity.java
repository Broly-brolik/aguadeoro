package com.aguadeoro.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.PricesMemoiresAdapter;
import com.aguadeoro.utils.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PriceMemoiresActivity extends Activity {

    String style, catalogCode, inventoryCode, setting, ringPrice, material, color, size, height, width, stoneSize, name, prMelee, stoneQuantity;
    Integer[] ringSizes = new Integer[]{46, 49, 52, 55, 58, 61, 64};
    double marge, settingPrice;
    double[] preparation = new double[2];
    RecyclerView recyclerView;
    double[] prices;
    String type = "";
    ArrayList<Map<String, String>> allStonesPrices = new ArrayList<>();
    ArrayList<Map<String, String>> stonesData = new ArrayList<>();
    ArrayList<Map<String, String>> allStonesPricesColored = new ArrayList<>();
    ArrayList<Map<String, String>> meleeRates = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_prices_memoires);
        Intent intent = getIntent();
        style = intent.getStringExtra("style");
        catalogCode = intent.getStringExtra("catCode");
        inventoryCode = intent.getStringExtra("invCode");
        setting = intent.getStringExtra("setting");
        ringPrice = intent.getStringExtra("ringPrice");
        material = intent.getStringExtra("material");
        color = intent.getStringExtra("color");
        size = intent.getStringExtra("size");
        height = intent.getStringExtra("height");
        width = intent.getStringExtra("width");
        stoneSize = intent.getStringExtra("stoneSize");
        name = intent.getStringExtra("name");
        prMelee = intent.getStringExtra("prMelee");
        stoneQuantity = intent.getStringExtra("stoneQuantity");
        assert prMelee != null;
        if (prMelee.isEmpty()) {
            prMelee = "1.0";
        }
        recyclerView = findViewById(R.id.list);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ((TextView) findViewById(R.id.catCode)).setText(catalogCode);
        ((TextView) findViewById(R.id.name)).setText(name);
        findViewById(R.id.buttonOffset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PriceMemoiresActivity.this);
                builder.setTitle("Offset");
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout layout = new LinearLayout(PriceMemoiresActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                EditText offsetCover = new EditText(PriceMemoiresActivity.this);
                offsetCover.setHint("Cover of the correct number of stones (1.0,0.75,0.5,0.25)");
                offsetCover.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                layout.addView(offsetCover);
                builder.setView(layout);
                builder.setPositiveButton("Apply", (dialog, which) -> {
                    int ringSize = (int) Math.round(Double.parseDouble(size));
                    if (!Arrays.asList(ringSizes).contains(ringSize)) {
                        for (int i = 0; i < ringSizes.length; i++) {
                            if (!Arrays.asList(ringSizes).contains(ringSize)) {
                                ringSize = ringSize + 1;
                            } else {
                                break;
                            }
                        }
                    }
                    stoneSize = stoneSize.trim();
                    stoneQuantity = stoneQuantity.trim();
                    Pattern pattern = Pattern.compile("(\\d+)");
                    Matcher matcher = pattern.matcher(stoneQuantity);
                    int baseStoneNumber;
                    if (matcher.find()) {
                        Log.d("test", matcher.group(1));
                        baseStoneNumber = Integer.parseInt(matcher.group(1));
                        recyclerView.setAdapter(new PricesMemoiresAdapter(PriceMemoiresActivity.this, ringSizes
                                , Double.parseDouble(ringPrice), marge, meleeRates, settingPrice, preparation, prices,
                                height, stonesData, type, Double.parseDouble(prMelee), color, material, stoneSize, true, baseStoneNumber, ringSize, Double.parseDouble(offsetCover.getText().toString())));
                    } else {
                        Toast.makeText(PriceMemoiresActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
        Toast.makeText(this, "The number of stones may be ±1 due to rounding errors, so the price may be inaccurate by around 50chf", Toast.LENGTH_LONG).show();
        new getStonesPrice().execute();
    }


    private void priceMono(String stoneSize) {
        int xPos = 0;
        String[] sizes;
        String currentColor = "";
        Double currentSize = 0.0;
        int currentNumberOfStones = 0;
        stoneSize = stoneSize.replace(" ", "");
        stoneSize = stoneSize.replace("mm", "");
        stoneSize = stoneSize.replace("ME:", "");

        if (stoneSize.contains("+")) {
            sizes = stoneSize.split("[+]");
            if (sizes.length == 1) {
                if (!sizes[0].contains("a") && !sizes[0].contains("b") && !sizes[0].contains("c") && !sizes[0].contains("d")) {
                    currentColor = "di";
                }
            }
            Log.d("sizes", "" + Arrays.toString(sizes));
            for (String size : sizes) {
                for (int i = 0; i < size.length(); i++) {
                    switch (size.charAt(i)) {
                        case 'a':
                            currentColor = "a";
                            break;
                        case 'b':
                            currentColor = "b";
                            break;
                        case 'c':
                            currentColor = "c";
                            break;
                        case 'd':
                            currentColor = "d";
                            break;
                        case 'z':
                            currentColor = "di";
                            break;
                    }
                }
                size = size.replace("a", "");
                size = size.replace("b", "");
                size = size.replace("c", "");
                size = size.replace("d", "");
                size = size.replace("z", "");
                xPos = size.indexOf("x");
                if (xPos != 0) {
                    currentSize = Double.parseDouble(size.substring(xPos + 1));
                    currentNumberOfStones = Integer.parseInt(size.substring(0, xPos));
                }
                if (currentColor.equals("di")) {
                    for (Map<String, String> price : allStonesPrices) {
                        if (price.get("Diametre").equals(String.valueOf(currentSize))) {
                            Map<String, String> toAdd = new HashMap<>();
                            toAdd.put("color", currentColor);
                            toAdd.put("diametre", String.valueOf(currentSize));
                            toAdd.put("price", String.valueOf(price.get("TW/VS")));
                            toAdd.put("number", String.valueOf(currentNumberOfStones));
                            stonesData.add(toAdd);
                        }
                    }
                } else {
                    for (Map<String, String> price : allStonesPricesColored) {
                        if (price.get("Diametre").equals(String.valueOf(currentSize))) {
                            Map<String, String> toAdd = new HashMap<>();
                            toAdd.put("color", currentColor);
                            toAdd.put("diametre", String.valueOf(currentSize));
                            toAdd.put("price", String.valueOf(price.get(currentColor.toUpperCase())));
                            toAdd.put("number", String.valueOf(currentNumberOfStones));
                            stonesData.add(toAdd);
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < stoneSize.length(); i++) {
                switch (stoneSize.charAt(i)) {
                    case 'a':
                        currentColor = "a";
                        break;
                    case 'b':
                        currentColor = "b";
                        break;
                    case 'c':
                        currentColor = "c";
                        break;
                    case 'd':
                        currentColor = "d";
                        break;
                }
            }
            if (!stoneSize.contains("a") && !stoneSize.contains("b") && !stoneSize.contains("c") && !stoneSize.contains("d")) {
                currentColor = "di";
            }
            stoneSize = stoneSize.replace("a", "");
            stoneSize = stoneSize.replace("b", "");
            stoneSize = stoneSize.replace("c", "");
            stoneSize = stoneSize.replace("d", "");
            stoneSize = stoneSize.replace("z", "");
            xPos = stoneSize.indexOf("x");
            if (xPos != 0) {
                currentSize = Double.parseDouble(stoneSize.substring(xPos + 1));
                currentNumberOfStones = Integer.parseInt(stoneSize.substring(0, xPos));
            }
            if (currentColor.equals("di")) {
                for (Map<String, String> price : allStonesPrices) {
                    if (price.get("Diametre").equals(String.valueOf(currentSize))) {
                        Map<String, String> toAdd = new HashMap<>();
                        toAdd.put("color", currentColor);
                        toAdd.put("diametre", String.valueOf(currentSize));
                        toAdd.put("price", String.valueOf(price.get("TW/VS")));
                        toAdd.put("number", String.valueOf(currentNumberOfStones));
                        stonesData.add(toAdd);
                    }
                }
            } else {
                for (Map<String, String> price : allStonesPricesColored) {
                    if (price.get("Diametre").equals(String.valueOf(currentSize))) {
                        Map<String, String> toAdd = new HashMap<>();
                        toAdd.put("color", currentColor);
                        toAdd.put("diametre", String.valueOf(currentSize));
                        toAdd.put("price", String.valueOf(price.get(currentColor.toUpperCase())));
                        toAdd.put("number", String.valueOf(currentNumberOfStones));
                        stonesData.add(toAdd);
                    }
                }
            }
        }
    }

    private void priceMulti() {
        stoneSize = stoneSize.replace(" ", "");
        stoneSize = stoneSize.replace("mm", "");
        stoneSize = stoneSize.replace("ME:", "");
        String[] sizes;
        sizes = stoneSize.split("\\+", 0);
        for (String size : sizes) {
            priceMono(size);
        }
    }

    private void priceFixed() {
        priceMono(stoneSize);
    }

    private class getStonesPrice extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            Query q = new Query("select * from PU_LV");
            if (!q.execute()) {
                return false;
            }
            allStonesPrices = q.getRes();

            q = new Query("select * from PU_LV_ColoredStones");
            if (!q.execute()) {
                return false;
            }
            allStonesPricesColored = q.getRes();

            q = new Query("select * from Marge");
            if (!q.execute()) {
                return false;
            }
            marge = Double.parseDouble(q.getRes().get(0).get("Marge"));

            q = new Query("select * from MeleeRate");
            if (!q.execute()) {
                return false;
            }
            meleeRates = q.getRes();

            q = new Query("select Price from Settings where ID = " + setting);
            if (!q.execute()) {
                return false;
            }
            settingPrice = Double.parseDouble(q.getRes().get(0).get("Price"));
            q = new Query("select Cost from Preparation order by ID asc");
            if (!q.execute()) {
                return false;
            }
            preparation[0] = Double.parseDouble(q.getRes().get(0).get("Cost"));
            preparation[1] = Double.parseDouble(q.getRes().get(1).get("Cost"));
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            switch (style) {
                case "Mono":
                    type = "Mono";
                    priceMono(stoneSize);
                    break;
                case "Multi":
                    type = "Multi";
                    priceMulti();
                    break;
                case "Fixed":
                    type = "Fixed";
                    priceFixed();
                    break;
                default:
                    type = "Other";
                    priceMulti();
            }
            Log.d("stonedata", "" + stonesData);
            recyclerView.setAdapter(new PricesMemoiresAdapter(PriceMemoiresActivity.this, ringSizes
                    , Double.parseDouble(ringPrice), marge, meleeRates, settingPrice, preparation, prices, height, stonesData, type, Double.parseDouble(prMelee), color, material, stoneSize, false));
        }
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

        return super.onOptionsItemSelected(item);
    }
}
