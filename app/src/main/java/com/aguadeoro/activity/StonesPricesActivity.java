package com.aguadeoro.activity;

import static android.app.AlertDialog.Builder;
import static android.app.AlertDialog.OnClickListener;

import static java.sql.DriverManager.println;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aguadeoro.R;
import com.aguadeoro.adapter.StonesPricesAdapter;
import com.aguadeoro.utils.Query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StonesPricesActivity extends Activity {

    StonesPricesAdapter adapter;
    RecyclerView recyclerView;
    boolean isDefault = true;
    boolean previous = false;
    private ArrayList<Map<String, String>> dialogData = new ArrayList<>();
    private AlertDialog loading;
    private double[] caratsToSend;
    private int calculatedTotal = 0;
    private final int DBTotal = 0;
    private int skipped = 0;
    String selectedType = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stones_prices);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        final CheckBox mine = findViewById(R.id.mine);
        final CheckBox color = findViewById(R.id.checkColor);
        ImageView help = findViewById(R.id.help);
        Button refresh = findViewById(R.id.refresh);
        Button stock = findViewById(R.id.displayStock);
        recyclerView = findViewById(R.id.stonesPricesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("from")) {
                previous = true;
            }
        }
        ((TextView) StonesPricesActivity.this.findViewById(R.id.textView13)).setText("Color");
        StonesPricesActivity.this.findViewById(R.id.textView6).setVisibility(View.VISIBLE);
        ((TextView) StonesPricesActivity.this.findViewById(R.id.textView6)).setText("IF");
        ((TextView) StonesPricesActivity.this.findViewById(R.id.textView14)).setText("VVS");
        StonesPricesActivity.this.findViewById(R.id.textView15).setVisibility(View.VISIBLE);
        ((TextView) StonesPricesActivity.this.findViewById(R.id.textView15)).setText("VS");
        ((TextView) StonesPricesActivity.this.findViewById(R.id.textView16)).setText("SI");
        new getPrices().execute("LV", "Color");

        stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogData.size() > 0) {
                    displayDialog(dialogData);
                } else {
                    ProgressDialog dialog = new ProgressDialog(StonesPricesActivity.this);
                    dialog.setMessage("Loading...");
                    dialog.setCancelable(false);
                    loading = dialog;
                    loading.show();
                    new StockDialog().execute();
                }
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColoredStonesCodeDialog dialog = new ColoredStonesCodeDialog(StonesPricesActivity.this);
                dialog.showDialog();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (color.isChecked()) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(70, 20, 0, 0);
                    ((TextView) StonesPricesActivity.this.findViewById(R.id.textView13)).setText("A");
                    StonesPricesActivity.this.findViewById(R.id.textView13).setLayoutParams(params);
                    ((TextView) StonesPricesActivity.this.findViewById(R.id.textView6)).setText("B");
                    StonesPricesActivity.this.findViewById(R.id.textView6).setLayoutParams(params);
                    ((TextView) StonesPricesActivity.this.findViewById(R.id.textView14)).setText("C");
                    StonesPricesActivity.this.findViewById(R.id.textView14).setLayoutParams(params);
                    ((TextView) StonesPricesActivity.this.findViewById(R.id.textView15)).setText("D");
                    StonesPricesActivity.this.findViewById(R.id.textView15).setLayoutParams(params);
                    ((TextView) StonesPricesActivity.this.findViewById(R.id.textView16)).setText("E            F");
                    StonesPricesActivity.this.findViewById(R.id.textView16).setLayoutParams(params);

                    if (mine.isChecked()) {
                        new getPrices().execute("MinedColoredPrices", "category");
                    } else {
                        new getPrices().execute("LVColoredPrices", "category");
                    }
                } else {
                    ((TextView) StonesPricesActivity.this.findViewById(R.id.textView13)).setText("Color");
                    StonesPricesActivity.this.findViewById(R.id.textView6).setVisibility(View.VISIBLE);
                    ((TextView) StonesPricesActivity.this.findViewById(R.id.textView6)).setText("IF");
                    ((TextView) StonesPricesActivity.this.findViewById(R.id.textView14)).setText("VVS");
                    StonesPricesActivity.this.findViewById(R.id.textView15).setVisibility(View.VISIBLE);
                    ((TextView) StonesPricesActivity.this.findViewById(R.id.textView15)).setText("VS");
                    ((TextView) StonesPricesActivity.this.findViewById(R.id.textView16)).setText("SI");
                    if (mine.isChecked()) {
                        new getPrices().execute("MinedDiamonds", "Color");
                    } else {
                        new getPrices().execute("LV", "Color");
                    }
                }
            }
        });
    }

    class StockDialog extends AsyncTask<Void, Void, ArrayList<Map<String, String>>> {
        @Override
        protected ArrayList<Map<String, String>> doInBackground(Void... voids) {
            String query = "select Products.*, Subcategory.Subcategory " +
                    "FROM Products INNER JOIN Subcategory ON Products.Subcategory = Subcategory.ID";
            Query q = new Query(query);
            q.execute();
            ArrayList<Map<String, String>> results = q.getRes();
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<Map<String, String>> results) {
            super.onPostExecute(results);
            dialogData = results;
            loading.dismiss();
            displayDialog(results);
        }
    }

    private String[] getSubcategories(ArrayList<Map<String, String>> results, String type) {
        ArrayList<String> subcategories = new ArrayList<>();
        for (Map<String, String> result : results) {
            if (result.get("Type").equals(type)) {
                if (!subcategories.contains(result.get("Subcategory"))) {
                    subcategories.add(result.get("Subcategory"));
                }
            }
        }
        return subcategories.toArray(new String[subcategories.size()]);
    }

    private void displayDialog(ArrayList<Map<String, String>> results) {
        ArrayList<String> subcategories = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        String[] lines = new String[]{"Linea Verde", "Mined"};

        for (Map<String, String> result : results) {
            if (!subcategories.contains(result.get("Subcategory"))) {
                subcategories.add(result.get("Subcategory"));
            }
        }
        for (Map<String, String> result : results) {
            if (!types.contains(result.get("Type"))) {
                types.add(result.get("Type"));
            }
        }
        Builder builder = new Builder(StonesPricesActivity.this);
        builder.setTitle("Select stock data");
        LinearLayout layout = new LinearLayout(StonesPricesActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);
        final Spinner subcategory = new Spinner(StonesPricesActivity.this);
        final Spinner type = new Spinner(StonesPricesActivity.this);
        final Spinner line = new Spinner(StonesPricesActivity.this);
        String[] subcategoriesArray = new String[subcategories.size()];
        subcategories.toArray(subcategoriesArray);
        String[] typesArray = new String[types.size()];
        types.toArray(typesArray);
        ArrayAdapter<String> subcategoryAdapter = new ArrayAdapter<>(StonesPricesActivity.this, android.R.layout.simple_spinner_item, subcategoriesArray);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(StonesPricesActivity.this, android.R.layout.simple_spinner_item, typesArray);
        ArrayAdapter<String> lineAdapter = new ArrayAdapter<>(StonesPricesActivity.this, android.R.layout.simple_spinner_item, lines);
        subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subcategory.setAdapter(subcategoryAdapter);
        type.setAdapter(typeAdapter);
        line.setAdapter(lineAdapter);
        type.setMinimumHeight(100);
        setMargins(type, 0, 0, 0, 30);
        layout.addView(type);
        subcategory.setMinimumHeight(100);
        setMargins(subcategory, 0, 0, 0, 30);
        layout.addView(subcategory);
        line.setMinimumHeight(100);
        setMargins(line, 0, 0, 0, 30);
        layout.addView(line);
        builder.setView(layout);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String[] subcategories = getSubcategories(results, type.getSelectedItem().toString());
                        ArrayAdapter<String> subcategoryAdapter = new ArrayAdapter<>(StonesPricesActivity.this, android.R.layout.simple_spinner_item, subcategories);
                        subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        subcategory.setAdapter(subcategoryAdapter);
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        builder.setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                calculatedTotal = 0;
                skipped = 0;
                int trueSubcategory = 0;
                for (Map<String, String> result : results) {
                    if (subcategory.getSelectedItem().toString().equals(result.get("Subcategory"))) {
                        trueSubcategory = Integer.parseInt(result.get("4"));
                    }
                }
                selectedType = type.getSelectedItem().toString();
                ProgressDialog dialog = new ProgressDialog(StonesPricesActivity.this);
                dialog.setMessage("Loading...");
                dialog.setCancelable(false);
                loading = dialog;
                loading.show();
                new GetCarats().execute("" + trueSubcategory, type.getSelectedItem().toString(), line.getSelectedItem().toString());
            }
        });
        builder.show();
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    class GetStock extends AsyncTask<String, Void, ArrayList<Map<String, String>>> {

        @Override
        protected ArrayList<Map<String, String>> doInBackground(String... strings) {
            String query = "select DISTINCT m1.ProductID, Products.ProductCode, Products.ID, " +
                    " (select sum(iif(m2.Type='1',m2.Quantity, -m2.quantity)) from StockHistory1 m2" +
                    " where m2.ProductID = m1.ProductID) AS Total " +
                    " FROM StockHistory1 AS m1 INNER JOIN Products ON m1.ProductID = Products.ID " +
                    " where Products.Line = '" + strings[2] + "' and Products.Type = '" + strings[1] +
                    "' and Products.Subcategory = " + strings[0] + " order by Products.ProductCode asc";
            Query q = new Query(query);
            q.execute();
            return q.getRes();
        }

        @Override
        protected void onPostExecute(ArrayList<Map<String, String>> results) {
            super.onPostExecute(results);
            ArrayList<ArrayList<Map<String, String>>> finalResults = new ArrayList<>();
            boolean isColor = false;
            //try {
            if (selectedType.equals("Color")) {
                isColor = true;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(70, 20, 0, 0);
                ((TextView) StonesPricesActivity.this.findViewById(R.id.textView13)).setText("A");
                StonesPricesActivity.this.findViewById(R.id.textView13).setLayoutParams(params);
                ((TextView) StonesPricesActivity.this.findViewById(R.id.textView6)).setText("B");
                StonesPricesActivity.this.findViewById(R.id.textView6).setLayoutParams(params);
                ((TextView) StonesPricesActivity.this.findViewById(R.id.textView14)).setText("C");
                StonesPricesActivity.this.findViewById(R.id.textView14).setLayoutParams(params);
                ((TextView) StonesPricesActivity.this.findViewById(R.id.textView15)).setText("D");
                StonesPricesActivity.this.findViewById(R.id.textView15).setLayoutParams(params);
                ((TextView) StonesPricesActivity.this.findViewById(R.id.textView16)).setText("E            F");
                StonesPricesActivity.this.findViewById(R.id.textView16).setLayoutParams(params);
            }

            for (Map<String, String> result : results) {
                String productID = String.valueOf((int) Double.parseDouble(result.get("ID").replaceAll("\"", "")));
                String trimmedCode = result.get("ProductCode").replaceAll(" ", "");
                String originalCode = result.get("ProductCode");
                double total = 0.0;
                if (result.get("Total") != null && !result.get("Total").isEmpty()) {
                    total = Double.parseDouble(Objects.requireNonNull(result.get("Total")));
                }
                if (total != 0.0) {
                    Pattern pattern = Pattern.compile(".+?(?=[0-9])");
                    Matcher matcher = pattern.matcher(trimmedCode);
                    String toReplace = "CTRD";
                    if (matcher.find()) {
                        toReplace = matcher.group();
                    }
                    trimmedCode = trimmedCode.replace(toReplace, "");
                    if (trimmedCode.contains("_")) {
                        trimmedCode = trimmedCode.substring(0, trimmedCode.indexOf("_"));
                    }
                    if (trimmedCode.contains("-")) {
                        trimmedCode = trimmedCode.replace(trimmedCode.substring(trimmedCode.indexOf("-"), trimmedCode.indexOf("-") + 4), "");
                    }
                    String stoneInfos = trimmedCode.replaceAll("[^A-Za-z]+", "");
                    String color = "A";
                    String clarity = "VS";
                    if (stoneInfos.length() == 0) {
                        skipped++;
                        continue;
                    }
                    if (isColor) {
                        switch (stoneInfos.charAt(0)) {
                            case 'A':
                                color = "A";
                                break;
                            case 'B':
                                color = "B";
                                break;
                            case 'C':
                                color = "C";
                                break;
                            case 'D':
                                color = "D";
                                break;
                            case 'E':
                                color = "E";
                                break;
                            case 'F':
                                color = "F";
                                break;
                        }
                    } else {
                        switch (stoneInfos.charAt(0)) {
                            case 'D':
                                color = "D";
                                break;
                            case 'E':
                            case 'F':
                                color = "E";
                                break;
                            case 'G':
                            case 'H':
                                color = "G";
                                break;
                            case 'I':
                            case 'J':
                                color = "I";
                                break;
                            case 'K':
                            case 'L':
                            case 'M':
                                color = "K";
                                break;
                        }
                    }

                    stoneInfos = stoneInfos.substring(1);
                    int gridPosition = 0;
                    if (stoneInfos.contains("VVS")) {
                        clarity = "VVS";
                        gridPosition = 1;
                    } else if (stoneInfos.contains("VS")) {
                        clarity = "VS";
                        gridPosition = 2;
                    } else if (stoneInfos.contains("VS1")) {
                        clarity = "VS";
                        gridPosition = 2;
                    } else if (stoneInfos.contains("VS2")) {
                        clarity = "VS";
                        gridPosition = 2;
                    } else if (stoneInfos.contains("SI")) {
                        clarity = "SI";
                        gridPosition = 3;
                    } else if (stoneInfos.contains("IF")) {
                        clarity = "IF";
                    } else if (stoneInfos.contains("S1")) {
                        clarity = "S1";
                        gridPosition = 3;
                    }
                    int finalTotal = (int) total;
                    String finalColor = color;
                    String finalClarity = clarity;
                    trimmedCode = trimmedCode.replaceAll("[^\\d.]", "");
                    if (trimmedCode.length() > 3 && !trimmedCode.contains(".")) {
                        trimmedCode = trimmedCode.substring(0, trimmedCode.length() - 1);
                    }
                    double carat = 0.0;
                    if (!trimmedCode.isEmpty()) {
                        if (trimmedCode.contains(".")) {
                            carat = Double.parseDouble(trimmedCode);
                        } else {
                            carat = Double.parseDouble(trimmedCode.charAt(0) + "." + trimmedCode.substring(1));
                        }
                    }
                    BigDecimal compareCarat = BigDecimal.valueOf(carat);
                    for (double defaultCarat : caratsToSend) {
                        BigDecimal defaultCaratBD = BigDecimal.valueOf(defaultCarat);
                        int minCaratRange = compareCarat.compareTo(defaultCaratBD.subtract(BigDecimal.valueOf(0.04)));
                        if ((compareCarat.compareTo(defaultCaratBD) < 0 || compareCarat.compareTo(defaultCaratBD) == 0) && (minCaratRange == 0 || minCaratRange > 0)) {
                            carat = defaultCarat;
                            break;
                        }
                    }
                    int finalGridPosition = gridPosition;
                    double finalCarat = carat;
                    if (finalResults.size() == 0) {
                        boolean finalIsColor = isColor;
                        finalResults.add(new ArrayList<>());
                        finalResults.get(0).add(new HashMap<String, String>() {{
                            put("ID", productID);
                            put("ProductCode", originalCode);
                            put("Total", String.valueOf(finalTotal));
                            put("Carat", String.valueOf(finalCarat));
                            put("Color", finalColor);
                            put("Clarity", finalClarity);
                            if (finalIsColor) {
                                put(finalColor.toLowerCase(), String.valueOf(finalTotal));
                            } else {
                                put(finalColor.toLowerCase() + "" + finalGridPosition, String.valueOf(finalTotal));
                            }
                        }});
                    } else {
                        boolean found = true;
                        ArrayList<ArrayList<Map<String, String>>> tempFinalResults = finalResults;
                        mainLoop:
                        for (int i = 0; i < tempFinalResults.size(); i++) {
                            ArrayList<Map<String, String>> tempArray = tempFinalResults.get(i);
                            for (Map<String, String> map : tempArray) {
                                if (isColor) {
                                    if (map.containsKey(finalColor.toLowerCase())) {
                                        if (map.get("Carat").equals(String.valueOf(finalCarat))) {
                                            finalResults.get(i).add(new HashMap<String, String>() {{
                                                put("ID", productID);
                                                put("ProductCode", originalCode);
                                                put("Total", String.valueOf(finalTotal));
                                                put("Carat", String.valueOf(finalCarat));
                                                put("Color", finalColor);
                                                put("Clarity", finalClarity);
                                                put(finalColor.toLowerCase(), String.valueOf(finalTotal));
                                            }});
                                            found = true;
                                            break mainLoop;
                                        } else {
                                            found = false;
                                        }
                                    } else {
                                        found = false;
                                    }
                                } else {
                                    if (map.containsKey(finalColor.toLowerCase() + "" + finalGridPosition)) {
                                        if (map.get("Carat").equals(String.valueOf(finalCarat))) {
                                            finalResults.get(i).add(new HashMap<String, String>() {{
                                                put("ID", productID);

                                                put("ProductCode", originalCode);
                                                put("Total", String.valueOf(finalTotal));
                                                put("Carat", String.valueOf(finalCarat));
                                                put("Color", finalColor);
                                                put("Clarity", finalClarity);
                                                put(finalColor.toLowerCase() + "" + finalGridPosition, String.valueOf(finalTotal));
                                            }});
                                            found = true;
                                            break mainLoop;
                                        } else {
                                            found = false;
                                        }
                                    } else {
                                        found = false;
                                    }
                                }
                            }
                        }
                        if (!found) {
                            boolean finalIsColor1 = isColor;
                            ArrayList<Map<String, String>> newList = new ArrayList<>();
                            newList.add(new HashMap<String, String>() {{
                                put("ID", productID);

                                put("ProductCode", originalCode);
                                put("Total", String.valueOf(finalTotal));
                                put("Carat", String.valueOf(finalCarat));
                                put("Color", finalColor);
                                put("Clarity", finalClarity);
                                if (finalIsColor1) {
                                    put(finalColor.toLowerCase(), String.valueOf(finalTotal));
                                } else {
                                    put(finalColor.toLowerCase() + "" + finalGridPosition, String.valueOf(finalTotal));
                                }
                            }});
                            finalResults.add(newList);
                        }
                    }
                }
            }
            /*} catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(StonesPricesActivity.this, "Error: could not display all items for selection", Toast.LENGTH_LONG).show();
            }*/
            for (ArrayList<Map<String, String>> mapArray : finalResults) {
                for (Map<String, String> map : mapArray) {
                    calculatedTotal += Integer.parseInt(map.get("Total"));
                }
            }
            ((TextView) StonesPricesActivity.this.findViewById(R.id.stockTotal)).setText("Stock total : " + calculatedTotal + "\n Skipped : " + skipped);
            adapter = new StonesPricesAdapter(StonesPricesActivity.this, finalResults, caratsToSend, isColor);
            loading.dismiss();
            recyclerView.setAdapter(adapter);
        }
    }

    class GetCarats extends AsyncTask<String, Void, ArrayList<Map<String, String>>> {
        String[] args;

        @Override
        protected ArrayList<Map<String, String>> doInBackground(String... strings) {
            String query = "select DISTINCT Carat from LV order by Carat asc";
            Query q = new Query(query);
            q.execute();
            this.args = strings;
            return q.getRes();
        }

        @Override
        protected void onPostExecute(ArrayList<Map<String, String>> results) {
            super.onPostExecute(results);
            double[] carats = new double[results.size()];
            for (int i = 0; i < results.size(); i++) {
                carats[i] = Double.parseDouble(Objects.requireNonNull(results.get(i).get("Carat")));
            }
            caratsToSend = carats;
            new GetStock().execute(args);
        }
    }

    class getPrices extends AsyncTask<String, String, Map<String, ArrayList<Map<String, String>>>> {

        @Override
        protected Map<String, ArrayList<Map<String, String>>> doInBackground(String... args) {
            String where = "";
            if (args[1].equals("Color")) {
                where = "where Color <> 'F' and Color <> 'H' and Color <> 'J' and Color <> 'L' and Color <> 'M'";
            }
            String query = "select * from " + args[0] + " " + where + " order by ID ASC";
            Query q = new Query(query);
            boolean s = q.execute();
            if (!s) {
                return null;
            }
            final ArrayList<Map<String, String>> tempRes = q.getRes();
            ArrayList<String> carats = new ArrayList<>();
            Map<String, ArrayList<Map<String, String>>> data = new HashMap<>();
            for (int i = 0; i < tempRes.size(); i++) {
                if (!carats.contains(tempRes.get(i).get("Carat"))) {
                    carats.add(tempRes.get(i).get("Carat"));
                }
            }
            for (int i = 0; i < carats.size(); i++) {
                ArrayList<Map<String, String>> newArray = new ArrayList<>();
                ArrayList<String> keys = new ArrayList<>(data.keySet());
                if (!keys.contains(carats.get(i))) {
                    data.put(carats.get(i), newArray);
                    keys.add(carats.get(i));
                }
                for (int j = 0; j < tempRes.size(); j++) {
                    if (Objects.requireNonNull(tempRes.get(j).get("Carat")).equals(keys.get(i))) {
                        ArrayList<Map<String, String>> toUpdate = data.get(keys.get(i));
                        toUpdate.add(tempRes.get(j));
                        data.put(keys.get(i), toUpdate);
                    }
                }

            }

            return data;
        }

        @Override
        protected void onPostExecute(Map<String, ArrayList<Map<String, String>>> data) {
            boolean color = ((CheckBox) StonesPricesActivity.this.findViewById(R.id.checkColor)).isChecked();
            boolean mine = ((CheckBox) StonesPricesActivity.this.findViewById(R.id.mine)).isChecked();
            adapter = new StonesPricesAdapter(StonesPricesActivity.this, data, color, mine);
            recyclerView.setAdapter(adapter);
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
        return true;
    }

    public void onClickStock(ArrayList<Map<String, String>> data) {
        Builder builder = new Builder(this);
        builder.setTitle("Stock");
        StringBuilder message = new StringBuilder();
        Log.e("MAP", data.toString());
        for (Map<String, String> map : data) {
            message.append(map.get("ID")).append(" : ").append(map.get("ProductCode")).append(" : ").append(map.get("Total")).append("\n\n");
        }
        builder.setMessage(message.toString());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}


