package com.aguadeoro.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PricesActivity extends ListActivity {
    private String catCode, stoneSize, color, material, caratRange, stoneQuantity, melee, ringPrize, inventoryCode, status;
    private View wheelView;
    private View mainView;
    private double meleeLV, meleeMine, newMin, newMax;
    private boolean MS = true;
    private int numberOfStones;
    private boolean hasMelee, firstIsDiamond, isMine, isClous;
    private ArrayList<Double> MSColored, MSDiamonds;
    ArrayList<Map<String, String>> res;
    ArrayList<String[]> list = new ArrayList<String[]>();
    Map<String, String> caratsLimits;
    Map<String, String> ringPriceLimits;
    Map<String, ArrayList<String>> tempCarat;
    ArrayList<Map<String, Object>> selectedPrices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.catCode = intent.getStringExtra("code");
        this.stoneSize = intent.getStringExtra("stoneSize");
        this.color = intent.getStringExtra("color");
        this.material = intent.getStringExtra("material");
        this.stoneQuantity = intent.getStringExtra("stoneQuantity");
        this.melee = intent.getStringExtra("melee");
        this.ringPrize = intent.getStringExtra("ringPrize");
        this.inventoryCode = intent.getStringExtra("InventoryCode");
        this.status = intent.getStringExtra("Status");
        this.isClous = intent.getBooleanExtra("Clous", false);

        if (!status.equals("Catalogue")) {
            replaceStockByCatalog();
        }
        if (!isClous) {
            if (stoneQuantity.contains("CS:1") || stoneQuantity.contains("CS: 1")) {
                MS = false;
                firstIsDiamond = true;
            } else if (stoneQuantity.contains("RC:1") || stoneQuantity.contains("RC :1")) {
                MS = false;
                firstIsDiamond = false;
            } else if (stoneQuantity.contains("MS")) {
                MS = true;
                char nmbr;
                String size2 = stoneQuantity.replaceAll("\\s+", "");
                size2 = size2.replaceAll(";", "");
                nmbr = size2.charAt(size2.length() - 1);
                numberOfStones = Integer.parseInt(String.valueOf(nmbr));
                Log.d("number of stones", "" + size2 + " " + nmbr);
            }
        } else {
            numberOfStones = 2;
        }

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (stoneSize.contains("RC")) {
            setContentView(R.layout.activity_prices_colored);
        } else {
            setContentView(R.layout.activity_prices);

        }
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        TextView code = findViewById(R.id.code);
        code.setText(catCode);
        TextView carat = findViewById(R.id.carat);
        carat.setText(stoneSize);
        TextView metal = findViewById(R.id.metal);
        final CheckBox mine = findViewById(R.id.mine);
        Spinner spinner = findViewById(R.id.spinner);
        Spinner spinner2 = findViewById(R.id.spinner2);
        Spinner spinner3 = findViewById(R.id.spinner3);
        Spinner spinner4 = findViewById(R.id.spinner4);
        Button button = findViewById(R.id.refresh);
        Button copy = findViewById(R.id.copyBtn);
        Button clear = findViewById(R.id.clearBtn);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.metal_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.diamond_quality, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.diamond_color, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this, R.array.colored_color, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner2.setAdapter(adapter2);
        spinner3.setAdapter(adapter3);
        spinner4.setAdapter(adapter4);
        if (material == null || material.equals("") || color == null || color.equals("")) {
            metal.setText("N/A");
        } else {
            metal.setText(material + " " + color);
        }
        if (melee.equals("0") || melee.equals("") || melee.equals("0.0")) {
            hasMelee = false;
        } else {
            hasMelee = true;
            String[] meleeList = melee.split("\\.");
            meleeLV = Double.parseDouble(meleeList[0]);
            meleeMine = Double.parseDouble(meleeList[1]);
            Log.d("melee", "" + melee + " LV :" + meleeLV + " mine :" + meleeMine);
        }
        if (MS) {
            MSColored = new ArrayList<>();
            MSDiamonds = new ArrayList<>();
            Pattern p = Pattern.compile("([0-9]+(?:[,.][0-9]+)?)");
            Matcher m;
            String s = stoneSize;
            if (hasMelee && stoneSize.contains("ME")) {
                s = stoneSize.substring(0, stoneSize.indexOf("ME"));
            }
            if (s.indexOf("RC") == 0) {
                firstIsDiamond = false;
                if (s.contains("DI")) {
                    String s2 = s.substring(s.indexOf("DI"));
                    m = p.matcher(s2);
                    while (m.find()) {
                        MSDiamonds.add(Double.parseDouble(m.group()));
                    }
                    String s3 = s.substring(s.indexOf("RC"), s.indexOf("DI"));
                    m = p.matcher(s3);
                } else {
                    m = p.matcher(s);
                }
                while (m.find()) {
                    MSColored.add(Double.parseDouble(m.group()));
                }
                caratRange = MSColored.get(0).toString();
            } else if (s.indexOf("MS") == 0) {
                firstIsDiamond = true;
                m = p.matcher(s);
                while (m.find()) {
                    MSDiamonds.add(Double.parseDouble(m.group()));
                }
                caratRange = MSDiamonds.get(0).toString();
            } else if (s.indexOf("DI") == 0) {
                firstIsDiamond = true;
                if (s.contains("RC")) {
                    String s2 = s.substring(s.indexOf("RC"));
                    m = p.matcher(s2);
                    while (m.find()) {
                        MSColored.add(Double.parseDouble(m.group()));
                    }
                    String s3 = s.substring(s.indexOf("DI"), s.indexOf("RC"));
                    m = p.matcher(s3);
                } else {
                    m = p.matcher(s);
                }
                while (m.find()) {
                    MSDiamonds.add(Double.parseDouble(m.group()));
                }
                caratRange = MSDiamonds.get(0).toString();
            }
            Log.d("testtest", MSDiamonds + "\n" + MSColored);
        } else {
            if (stoneSize.contains("CS:")) {
                StringBuilder trueRange = new StringBuilder();
                String size2 = stoneSize.replaceAll("\\s+", "");
                trueRange.append(size2.substring(size2.lastIndexOf("CS:") + 3, size2.lastIndexOf("CS:") + 7));
                Log.d("true-range", " " + trueRange);

                caratRange = trueRange.toString();
                caratRange = caratRange.replace(";", "");
                caratRange = caratRange.replace("ME", "");
            } else if (stoneSize.contains("RC:")) {
                StringBuilder trueRange = new StringBuilder();
                String size2 = stoneSize.replaceAll("\\s+", "");
                trueRange.append(size2.substring(size2.lastIndexOf("RC:") + 3, size2.lastIndexOf("RC:") + 7));
                Log.d("true-range", " " + trueRange);
                caratRange = trueRange.toString();
                caratRange = caratRange.replace(";", "");
                caratRange = caratRange.replace("ME", "");
            } else {
                Log.d("error", "no CS: or RC:");
                caratRange = "";
            }

        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMine = mine.isChecked();
                new getPrices().execute();
                if (!firstIsDiamond) {
                    if (isMine) {
                        ((TextView) findViewById(R.id.textView16)).setText("I");
                        ((TextView) findViewById(R.id.textView)).setText("II");
                        ((TextView) findViewById(R.id.textView4)).setText("III");
                    } else {
                        ((TextView) findViewById(R.id.textView16)).setText("D");
                        ((TextView) findViewById(R.id.textView)).setText("E");
                        ((TextView) findViewById(R.id.textView4)).setText("F");
                    }
                }
                new getPrices().execute();
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", ((TextView) findViewById(R.id.toOrderEdit)).getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(PricesActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPrices.clear();
                ((EditText) findViewById(R.id.toOrderEdit)).setText("");
                isMine = mine.isChecked();
                new getPrices().execute();
                if (!firstIsDiamond) {
                    if (isMine) {
                        ((TextView) findViewById(R.id.textView16)).setText("I");
                        ((TextView) findViewById(R.id.textView)).setText("II");
                        ((TextView) findViewById(R.id.textView4)).setText("III");
                    } else {
                        ((TextView) findViewById(R.id.textView16)).setText("D");
                        ((TextView) findViewById(R.id.textView)).setText("E");
                        ((TextView) findViewById(R.id.textView4)).setText("F");
                    }
                }
                new getPrices().execute();
            }
        });
        ImageView helpbtn = findViewById(R.id.help);
        helpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColoredStonesCodeDialog dialog = new ColoredStonesCodeDialog(PricesActivity.this);
                dialog.showDialog();
            }
        });
        new getPrices().execute();
    }

    public void replaceStockByCatalog() {
        String query = "select * from Inventory where CatalogCode = " + Utils.escape(catCode) + " and Status = 'Catalogue'";
        Query q = new Query(query);
        boolean s = q.execute();
        if (!s) {
            Toast.makeText(PricesActivity.this, "Error could not find this item with status : catalogue", Toast.LENGTH_LONG).show();

        } else {
            this.catCode = q.getRes().get(0).get("CatalogCode");
            this.stoneSize = q.getRes().get(0).get("StoneSize");
            this.color = q.getRes().get(0).get("Color");
            this.material = q.getRes().get(0).get("Material");
            this.stoneQuantity = q.getRes().get(0).get("StoneQuantity");
            this.melee = q.getRes().get(0).get("MeleePrize");
            this.ringPrize = q.getRes().get(0).get("RingPrize");
            this.inventoryCode = q.getRes().get(0).get("InventoryCode");
            this.status = q.getRes().get(0).get("Status");
        }
    }


    public void displayHelp() {
        String query = "select top 1 * from HelpTexts";
        Query q = new Query(query);
        boolean s = q.execute();
        if (!s) {
            return;
        }
        ArrayList<Map<String, String>> res = q.getRes();
        String text = res.get(0).get("HelpText");
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PricesActivity.this);
        builder.setMessage(text).setTitle("Help");
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    class getPrices extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            String query = "";
            Query q;
            boolean s;
            if (MS) {
                if (stoneSize.indexOf("MS") == 0 || stoneSize.indexOf("DI") == 0) {
                    if (isMine) {
                        query = "select * from MinedDiamonds";
                    } else {
                        query = "select * from LV";
                    }
                } else if (stoneSize.indexOf("RC") == 0) {
                    if (isMine) {
                        query = "select * from MinedColoredPrices";
                    } else {
                        query = "select * from LVColoredPrices";

                    }
                }
            } else {
                if (stoneSize.contains("CS")) {
                    if (isMine) {
                        query = "select * from MinedDiamonds";
                    } else {
                        query = "select * from LV";
                    }
                } else if (stoneSize.contains("RC")) {
                    if (isMine) {
                        query = "select * from MinedColoredPrices";
                    } else {
                        query = "select * from LVColoredPrices";

                    }
                } else {
                    return false;
                }
            }
            q = new Query(query);
            s = q.execute();
            if (!s) {
                return false;
            }
            res = q.getRes();
            ArrayList<String> carats = new ArrayList<>();
            boolean eq = false;
            for (int i = 0; i < res.size(); i++) {
                if (!(carats.contains(res.get(i).get("Carat")))) {
                    carats.add(res.get(i).get("Carat"));
                }
            }
            String catCodeWithoutCarat = catCode.substring(0, catCode.lastIndexOf('.'));
            query = "select CatalogCode, RingPrize from Inventory where CatalogCode like '" + catCodeWithoutCarat + "%' and Status = 'Catalogue' order by CatalogCode";
            q = new Query(query);
            s = q.execute();
            if (!s) {
                return false;
            }
            res = q.getRes();
            caratsLimits = new HashMap<>();
            ringPriceLimits = new HashMap<>();
            newMin = 100000.0;
            newMax = 0.0;
            for (int i = 0; i < res.size(); i++) {
                String temp = res.get(i).get("CatalogCode");
                int lastDot = temp.lastIndexOf('.');
                String carat = temp.substring(lastDot + 1, lastDot + 4);
                carat = carat.substring(0, 1) + '.' + carat.substring(1);
                if (catCode.matches(".*[A-Z].*")) {
                    if (temp.matches(".*[A-Z].*")) {
                        caratsLimits.put(temp, carat);
                        ringPriceLimits.put(temp, res.get(i).get("RingPrize"));
                        if (Double.parseDouble(carat) < newMin) {
                            newMin = Double.parseDouble(carat) + 0.04;
                        }
                        if (Double.parseDouble(carat) > newMax) {
                            newMax = Double.parseDouble(carat) + 0.04;
                        }
                    }
                } else {
                    if (!temp.matches(".*[A-Z].*")) {
                        caratsLimits.put(temp, carat);
                        ringPriceLimits.put(temp, res.get(i).get("RingPrize"));
                        if (Double.parseDouble(carat) < newMin) {
                            newMin = Double.parseDouble(carat) + 0.04;
                        }
                        if (Double.parseDouble(carat) > newMax) {
                            newMax = Double.parseDouble(carat) + 0.04;
                        }
                    }
                }
            }
            Log.d("newmax newmin", "" + newMax + " " + newMin);
            for (int i = 0; i < 4; i++) {
                newMax += 0.04;
                if (newMin > 0.1) {
                    newMin -= 0.04;
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            TextView range = findViewById(R.id.range);
            range.setText(String.format("%.2f", newMin) + " - " + String.format("%.2f", newMax));
            new getPrices2().execute(String.valueOf(newMin), String.valueOf(newMax));
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    class getPrices2 extends AsyncTask<String, String, Boolean> {

        @Override
        protected Boolean doInBackground(String... args) {
            Query q = new Query("");
            if (stoneSize.indexOf("CS") == 0 || stoneSize.indexOf("MS") == 0 || stoneSize.indexOf("DI") == 0) {
                if (isMine) {
                    if (isClous) {
                        q = new Query("select * from MinedDiamonds where Carat <= 1.0 and Color <> 'F' and Color <> 'H' and Color <> 'J' and Color <> 'L' and Color <> 'M' order by ID asc");
                    } else {
                        if (args[0].equals("min")) {
                            q = new Query("select * from MinedDiamonds where Carat <= " + args[1] + " and Color <> 'F' and Color <> 'H' and Color <> 'J' and Color <> 'L' and Color <> 'M' order by ID asc");
                        } else if (args[1].equals("max")) {
                            q = new Query("select * from MinedDiamonds where Carat >= " + args[0] + " and Color <> 'F' and Color <> 'H' and Color <> 'J' and Color <> 'L' and Color <> 'M' order by ID asc");
                        } else {
                            q = new Query("select * from MinedDiamonds where Carat between " + args[0] + " and " + args[1] + " and Color <> 'F' and Color <> 'H' and Color <> 'J' and Color <> 'L' and Color <> 'M' order by ID asc");
                        }
                    }

                } else {
                    if (isClous) {
                        q = new Query("select * from LV where Carat <= 1.0");
                    } else {
                        if (args[0].equals("min")) {
                            q = new Query("select * from LV where Carat <= " + args[1]);
                        } else if (args[1].equals("max")) {
                            q = new Query("select * from LV where Carat >= " + args[0]);
                        } else {
                            q = new Query("select * from LV where Carat between " + args[0] + " and " + args[1] + "");
                        }
                    }

                }
                boolean s = q.execute();
                if (!s) {
                    return false;
                }
                final ArrayList<Map<String, String>> result = q.getRes();
                tempCarat = new HashMap<>();
                ArrayList<String> caratData = new ArrayList<>();
                Log.d("res size", result.size() + "");
                for (int i = 0; i < result.size(); i++) {
                    if (isMine) {
                        if (i % 20 == 0 && i != 0) {
                            tempCarat.put(result.get(i - 1).get("Carat"), caratData);
                            Log.d("finalCarat", "size :" + caratData.size());
                            caratData = new ArrayList<>();
                        }
                    } else {
                        if (i % 15 == 0 && i != 0) {
                            tempCarat.put(result.get(i - 1).get("" + 2), caratData);
                            Log.d("finalCarat", "size :" + result.get(i - 1).get("" + 2) + " at " + i);
                            caratData = new ArrayList<>();
                        }
                    }
                    if (i == result.size() - 1) {
                        caratData.add(result.get(i).get("" + 1));
                        caratData.add(result.get(i).get("" + 3));
                        tempCarat.put(result.get(i - 1).get("" + 2), caratData);
                        Log.d("finalCarat last", "size :" + result.get(i - 1).get("" + 2) + " at " + i);
                        caratData = new ArrayList<>();
                    }
                    caratData.add(result.get(i).get("" + 1));
                    caratData.add(result.get(i).get("" + 3));
                }
            } else if (stoneSize.indexOf("RC") == 0) {
                if (isMine) {
                    if (args[0].equals("min")) {
                        q = new Query("select * from MinedColoredPrices where Carat <= " + args[1]);
                    } else if (args[1].equals("max")) {
                        q = new Query("select * from MinedColoredPrices where Carat >= " + args[0]);
                    } else {
                        q = new Query("select * from MinedColoredPrices where Carat between " + args[0] + " and " + args[1] + "");
                    }
                } else {
                    if (args[0].equals("min")) {
                        q = new Query("select * from LVColoredPrices where Carat <= " + args[1]);
                    } else if (args[1].equals("max")) {
                        q = new Query("select * from LVColoredPrices where Carat >= " + args[0]);
                    } else {
                        q = new Query("select * from LVColoredPrices where Carat between " + args[0] + " and " + args[1] + "");
                    }
                }

                boolean s = q.execute();
                if (!s) {
                    return false;
                }
                final ArrayList<Map<String, String>> result = q.getRes();
                tempCarat = new HashMap<>();
                ArrayList<String> caratData = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {
                    if (i % 6 == 0 && i != 0) {
                        tempCarat.put(result.get(i - 1).get("" + 1), caratData);
                        Log.d("finalCarat", "size :" + caratData.size());
                        caratData = new ArrayList<>();
                    }
                    caratData.add(result.get(i).get("" + 2));
                    caratData.add(result.get(i).get("" + 3));
                }
            }


            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            String metal;
            Spinner m = findViewById(R.id.spinner);
            Spinner d1 = findViewById(R.id.spinner2);
            Spinner d2 = findViewById(R.id.spinner3);
            Spinner d3 = findViewById(R.id.spinner4);
            String quality = d1.getSelectedItem().toString();
            String color = d2.getSelectedItem().toString();
            String cColor = d3.getSelectedItem().toString();
            metal = m.getSelectedItem().toString();
            Log.d("final data", "metal : " + metal + " melee: " + hasMelee + " MS:" + MS + " first diamond: " + firstIsDiamond);
            Log.d("final data2", "tempcarat size : " + tempCarat.size() + " melee: " + meleeMine + " " + meleeLV + " ringprice : " + ringPrize);
            if (hasMelee) {
                if (isMine) {
                    PricesListAdapter priceAdapter;
                    if (isClous) {
                        priceAdapter = new PricesListAdapter(PricesActivity.this, tempCarat, 0, metal, Double.parseDouble(ringPrize), false, isMine, caratsLimits, ringPriceLimits);

                    } else {
                        if (MS) {
                            if (!firstIsDiamond) {
                                priceAdapter = new PricesListAdapter(PricesActivity.this, tempCarat, meleeMine, metal, Double.parseDouble(ringPrize), true, MSDiamonds, MSColored, firstIsDiamond, quality, color, cColor, isMine, caratsLimits, ringPriceLimits);
                            } else {
                                priceAdapter = new PricesListAdapter(PricesActivity.this, tempCarat, meleeMine, metal, Double.parseDouble(ringPrize), false, MSDiamonds, MSColored, firstIsDiamond, quality, color, cColor, isMine, caratsLimits, ringPriceLimits);
                            }
                        } else {
                            if (!firstIsDiamond) {
                                priceAdapter = new PricesListAdapter(PricesActivity.this, tempCarat, meleeMine, metal, Double.parseDouble(ringPrize), true, isMine, caratsLimits, ringPriceLimits);
                            } else {
                                priceAdapter = new PricesListAdapter(PricesActivity.this, tempCarat, meleeMine, metal, Double.parseDouble(ringPrize), false, isMine, caratsLimits, ringPriceLimits);
                            }
                        }
                    }
                    PricesActivity.this.setListAdapter(priceAdapter);

                } else {
                    if (MS) {
                        PricesListAdapter priceAdapter;
                        if (!firstIsDiamond) {
                            priceAdapter = new PricesListAdapter(PricesActivity.this, tempCarat, meleeLV, metal, Double.parseDouble(ringPrize), true, MSDiamonds, MSColored, firstIsDiamond, quality, color, cColor, isMine, caratsLimits, ringPriceLimits);
                        } else {
                            priceAdapter = new PricesListAdapter(PricesActivity.this, tempCarat, meleeLV, metal, Double.parseDouble(ringPrize), false, MSDiamonds, MSColored, firstIsDiamond, quality, color, cColor, isMine, caratsLimits, ringPriceLimits);
                        }
                        PricesActivity.this.setListAdapter(priceAdapter);
                    } else {
                        PricesListAdapter priceAdapter;
                        if (!firstIsDiamond) {
                            priceAdapter = new PricesListAdapter(PricesActivity.this, tempCarat, meleeLV, metal, Double.parseDouble(ringPrize), true, isMine, caratsLimits, ringPriceLimits);
                        } else {
                            priceAdapter = new PricesListAdapter(PricesActivity.this, tempCarat, meleeLV, metal, Double.parseDouble(ringPrize), false, isMine, caratsLimits, ringPriceLimits);
                        }
                        PricesActivity.this.setListAdapter(priceAdapter);
                    }
                }

            } else {
                if (MS) {
                    PricesListAdapter priceAdapter;
                    if (!firstIsDiamond) {
                        priceAdapter = new PricesListAdapter(PricesActivity.this, tempCarat, 0.0, metal, Double.parseDouble(ringPrize), true, MSDiamonds, MSColored, firstIsDiamond, quality, color, cColor, isMine, caratsLimits, ringPriceLimits);
                    } else {
                        priceAdapter = new PricesListAdapter(PricesActivity.this, tempCarat, 0.0, metal, Double.parseDouble(ringPrize), false, MSDiamonds, MSColored, firstIsDiamond, quality, color, cColor, isMine, caratsLimits, ringPriceLimits);
                    }
                    PricesActivity.this.setListAdapter(priceAdapter);
                } else {
                    PricesListAdapter priceAdapter;
                    if (!firstIsDiamond) {
                        priceAdapter = new PricesListAdapter(PricesActivity.this, tempCarat, 0.0, metal, Double.parseDouble(ringPrize), true, isMine, caratsLimits, ringPriceLimits);
                    } else {
                        priceAdapter = new PricesListAdapter(PricesActivity.this, tempCarat, 0.0, metal, Double.parseDouble(ringPrize), false, isMine, caratsLimits, ringPriceLimits);
                    }
                    PricesActivity.this.setListAdapter(priceAdapter);
                }

            }

        }
    }

    public void getToOrder(ArrayList<Map<String, Object>> toOrderList) {
        if (selectedPrices == null) {
            selectedPrices = new ArrayList<>();
        }
        selectedPrices.addAll(toOrderList);
        Set<Map<String, Object>> set = new HashSet<>(selectedPrices);
        selectedPrices.clear();
        selectedPrices.addAll(set);
        Log.d("selectedPrices", "" + selectedPrices);
        TextView line = findViewById(R.id.toOrderEdit);
        StringBuilder lineTxt = new StringBuilder();
        ArrayList<String> metalsPresent = new ArrayList<>();


        for (Map<String, Object> selectedPrice : selectedPrices) {
            if (!metalsPresent.contains(Objects.requireNonNull(selectedPrice.get("metal")).toString())) {
                metalsPresent.add(Objects.requireNonNull(selectedPrice.get("metal")).toString());
            }
        }
        for (int i = 0; i < metalsPresent.size(); i++) {
            lineTxt.append(metalsPresent.get(i));
            lineTxt.append(" : ");
            for (Map<String, Object> selectedPrice : selectedPrices) {
                if (selectedPrice.get("metal").toString().equals(metalsPresent.get(i))) {
                    if (isMine) {
                        lineTxt.append(" MI ");

                    } else {
                        lineTxt.append(" LV ");
                    }
                    lineTxt.append(selectedPrice.get("range"));
                    lineTxt.append(" ");
                    lineTxt.append(idToColor(selectedPrice.get("color").toString()));
                    lineTxt.append(" ");
                    lineTxt.append((int) Double.parseDouble(selectedPrice.get("price").toString()));
                    lineTxt.append("CHF ");
                    lineTxt.append(" / ");
                }
            }
            lineTxt.append(" // ");
        }
        line.setText(lineTxt.toString());
    }

    private String idToColor(String id) {
        if (id.length() < 2) {
            return id.toUpperCase();
        } else {
            StringBuilder color = new StringBuilder();
            switch (id.charAt(0)) {
                case 'd':
                    color.append("D");
                    break;
                case 'e':
                    color.append("E,F");
                    break;
                case 'g':
                    color.append("G,H");
                    break;
                case 'i':
                    color.append("I,J");
                    break;
                case 'k':
                    color.append("K,L,M");
                    break;
            }
            switch (id.charAt(id.length() - 1)) {
                case '0':
                    color.append(" IF");
                    break;
                case '1':
                    color.append(" VVS");
                    break;
                case '2':
                    color.append(" VS");
                    break;
                case '3':
                    color.append(" SI");
                    break;
            }
            return color.toString();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.prices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_prices) {
            Intent intent = new Intent(this, StonesPricesActivity.class);
            startActivity(intent);
            return true;
        }
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
