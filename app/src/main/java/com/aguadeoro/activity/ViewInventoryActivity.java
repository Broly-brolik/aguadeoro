package com.aguadeoro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.PricesListAdapter;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class ViewInventoryActivity extends Activity {

    String ID;
    ArrayList<String[]> assignList;
    private View wheelView;
    private View mainView;
    private String inventCode, catalogCode, code, mark, category, description,
            width, height, material, color, weight, size, stone, stoneQuantity,
            stoneType, stoneSize, carat, stoneColor, stoneClarity, stoneCut,
            stonePolish, symetry, lab, certificate, cost, price, quantity,
            remark, status, entryDate, line, collection, style, forme, setting, name, ringprize, meleeprize, stoneprize, prMelee;
    private Calendar c;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_inventory_detail);
        wheelView = findViewById(R.id.animation_layout);
        mainView = findViewById(R.id.main_layout);
        showProgress(true);
        assignList = new ArrayList<String[]>();
        ID = getIntent().getStringExtra(Utils.ID);
        new ListDetails().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_inventory, menu);
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
        if (id == R.id.action_prices) {
            if (status.equals("Catalogue") || status.equals("Stock")) {
                if (line.equals("solitaire")) {
                    Intent intent = new Intent(this, PricesActivity.class);
                    intent.putExtra("code", catalogCode);
                    intent.putExtra("stoneSize", stoneSize);
                    intent.putExtra("color", color);
                    intent.putExtra("material", material);
                    intent.putExtra("stoneQuantity", stoneQuantity);
                    intent.putExtra("melee", meleeprize);
                    intent.putExtra("ringPrize", ringprize);
                    intent.putExtra("InventoryCode", inventCode);
                    intent.putExtra("Status", status);
                    intent.putExtra("Clous", false);
                    startActivity(intent);
                } else if (line.equals("TearOfJoy")) {
                    Intent intent = new Intent(this, PricesTearsOfJoyActivity.class);
                    startActivity(intent);
                } else {
                    switch (category) {
                        case "Solitaire": {
                            Intent intent = new Intent(this, PricesActivity.class);
                            intent.putExtra("code", catalogCode);
                            intent.putExtra("stoneSize", stoneSize);
                            intent.putExtra("color", color);
                            intent.putExtra("material", material);
                            intent.putExtra("stoneQuantity", stoneQuantity);
                            intent.putExtra("melee", meleeprize);
                            intent.putExtra("ringPrize", ringprize);
                            intent.putExtra("InventoryCode", inventCode);
                            intent.putExtra("Status", status);
                            intent.putExtra("Clous", false);
                            startActivity(intent);
                            break;
                        }
                        case "Mémoire": {
                            if (line.equals("Line")) {
                                Intent intent = new Intent(this, PriceMemoiresActivity.class);
                                intent.putExtra("catCode", catalogCode);
                                intent.putExtra("style", style);
                                intent.putExtra("invCode", inventCode);
                                intent.putExtra("setting", setting);
                                intent.putExtra("ringPrice", ringprize);
                                intent.putExtra("material", material);
                                intent.putExtra("color", color);
                                intent.putExtra("size", size);
                                intent.putExtra("height", height);
                                intent.putExtra("width", width);
                                intent.putExtra("stoneSize", stoneSize);
                                intent.putExtra("name", name);
                                intent.putExtra("prMelee", prMelee);
                                intent.putExtra("stoneQuantity", stoneQuantity);
                                startActivity(intent);
                            }
                            break;
                        }
                        case "Boucles d'Oreilles":
                            if (line.equals("Clous")) {
                                Intent intent = new Intent(this, PricesActivity.class);
                                intent.putExtra("code", catalogCode);
                                intent.putExtra("stoneSize", stoneSize);
                                intent.putExtra("color", color);
                                intent.putExtra("material", material);
                                intent.putExtra("stoneQuantity", stoneQuantity);
                                intent.putExtra("melee", meleeprize);
                                intent.putExtra("ringPrize", ringprize);
                                intent.putExtra("InventoryCode", inventCode);
                                intent.putExtra("Status", status);
                                intent.putExtra("Clous", true);
                                startActivity(intent);
                            } else {
                                Toast.makeText(this, "Prices are not available for this category", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case "Alliance":
                            Intent intent = new Intent(this, PricesAlliancesActivity.class);
                            intent.putExtra("line", line);
                            startActivity(intent);
                            break;
                        default:
                            Toast.makeText(ViewInventoryActivity.this, "Prices are not available for this category", Toast.LENGTH_LONG).show();
                            break;
                    }
                }

            } else {
                Toast.makeText(ViewInventoryActivity.this, "Item is not in stock or catalogue", Toast.LENGTH_LONG).show();
            }

        }

        return super.onOptionsItemSelected(item);
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


    class ListDetails extends AsyncTask<String, String, Boolean> {
        File path;

        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            boolean success = false;
            String query = "select * from Inventory where ID = " + ID;
            Query q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            ArrayList<Map<String, String>> result = q.getRes();
            inventCode = result.get(0).get(Utils.INVT_CODE);
            catalogCode = (result.get(0).get(Utils.CATALOG_CODE));
            code = (result.get(0).get(Utils.CODE));
            mark = (result.get(0).get(Utils.MARK));
            category = (result.get(0).get(Utils.CATEGORY));
            description = (result.get(0).get(Utils.DESCRIPTION));
            width = (result.get(0).get(Utils.WIDTH));
            height = (result.get(0).get(Utils.HEIGHT));
            material = (result.get(0).get(Utils.MATERIAL));
            color = (result.get(0).get(Utils.COLOR));
            weight = (result.get(0).get(Utils.WEIGHT));
            size = (result.get(0).get(Utils.SIZE));
            stone = (result.get(0).get(Utils.STONE));
            stoneQuantity = (result.get(0).get(Utils.STONEQUANTITY));
            stoneType = (result.get(0).get(Utils.STONETYPE));
            stoneSize = (result.get(0).get(Utils.STONESIZE));
            carat = (result.get(0).get(Utils.CARAT));
            stoneColor = (result.get(0).get(Utils.STONECOLOR));
            stoneClarity = (result.get(0).get(Utils.STONECLARITY));
            stoneCut = (result.get(0).get(Utils.STONECUT));
            stonePolish = (result.get(0).get(Utils.STONEPOLISH));
            symetry = (result.get(0).get(Utils.STONESYMETRY));
            lab = (result.get(0).get(Utils.LAB));
            certificate = (result.get(0).get(Utils.CERTIFICATE));
            cost = (result.get(0).get(Utils.COST));
            price = (result.get(0).get(Utils.PRICE));
            quantity = (result.get(0).get(Utils.QUANTITY));
            remark = (result.get(0).get(Utils.REMARK));
            status = (result.get(0).get(Utils.STATUS));
            entryDate = (result.get(0).get("EntryDate"));
            line = (result.get(0).get("Line"));
            collection = (result.get(0).get("Collection"));
            style = (result.get(0).get("Style"));
            forme = (result.get(0).get("Forme"));
            setting = (result.get(0).get("Setting"));
            name = (result.get(0).get("Name"));
            ringprize = (result.get(0).get("RingPrize"));
            meleeprize = (result.get(0).get("MeleePrize"));
            stoneprize = (result.get(0).get("StonePrize"));
            prMelee = (result.get(0).get("PrMelee"));

            filename = (result.get(0).get(Utils.IMAGE));
            Log.e("inventory result", result.get(0).toString());
            if (!filename.isEmpty()) {
                path = new File(Environment.getExternalStorageDirectory()
                        + "/06_inventory/" + filename);

            }
            query = "select * from InventoryDistribution where InventoryID = "
                    + ID;
            q = new Query(query);
            success = q.execute();
            if (!success) {
                return false;
            }
            result = q.getRes();
            for (int i = 0; i < result.size(); i++) {
                String[] line = new String[6];
                line[0] = result.get(i).get(Utils.ID);
                line[1] = result.get(i).get("InventoryID");
                line[2] = result.get(i).get("AssignedTo");
                line[3] = result.get(i).get("Quantity");
                line[4] = result.get(i).get("Status");
                line[5] = result.get(i).get("AssignDate");
                assignList.add(line);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                TextView view = findViewById(R.id.inventory_code);
                view.setText(inventCode);
                view = findViewById(R.id.catalog_code);
                view.setText(catalogCode);
                view = findViewById(R.id.code);
                view.setText(code);
                view = findViewById(R.id.mark);
                view.setText(mark);
                view = findViewById(R.id.category);
                view.setText(category);
                view = findViewById(R.id.width);
                view.setText(description);
                view = findViewById(R.id.width);
                view.setText(width);
                view = findViewById(R.id.height);
                view.setText(height);
                view = findViewById(R.id.material);
                view.setText(material);
                view = findViewById(R.id.color);
                view.setText(color);
                view = findViewById(R.id.weight);
                view.setText(weight);
                view = findViewById(R.id.size);
                view.setText(size);
                view = findViewById(R.id.stone);
                view.setText(stone);
                view = findViewById(R.id.stone_quantity);
                view.setText(stoneQuantity);
                view = findViewById(R.id.stone_type);
                view.setText(stoneType);
                view = findViewById(R.id.stone_size);
                view.setText(stoneSize);
                view = findViewById(R.id.carat);
                view.setText(carat);
                view = findViewById(R.id.stone_color);
                view.setText(stoneColor);
                view = findViewById(R.id.stone_clarity);
                view.setText(stoneClarity);
                view = findViewById(R.id.stone_cut);
                view.setText(stoneCut);
                view = findViewById(R.id.stone_polish);
                view.setText(stonePolish);
                view = findViewById(R.id.stone_symetry);
                view.setText(symetry);
                view = findViewById(R.id.lab);
                view.setText(lab);
                view = findViewById(R.id.certificate);
                view.setText(certificate);
                view = findViewById(R.id.cost);
                view.setText(cost);
                view = findViewById(R.id.price);
                view.setText(price);
                view = findViewById(R.id.quantity);
                view.setText(quantity);
                view = findViewById(R.id.remark);
                view.setText(remark);
                view = findViewById(R.id.status);
                view.setText(status);
                view = findViewById(R.id.line);
                view.setText(line);
                view = findViewById(R.id.collection);
                view.setText(collection);
                view = findViewById(R.id.style);
                view.setText(style);
                view = findViewById(R.id.forme);
                view.setText(forme);
                view = findViewById(R.id.setting);
                view.setText(setting);
                view = findViewById(R.id.name);
                view.setText(name);
                view = findViewById(R.id.ringPrize);
                view.setText(ringprize);
                view = findViewById(R.id.meleePrize);
                view.setText(meleeprize);
                view = findViewById(R.id.stonePrize);
                view.setText(stoneprize);

                double ringPrizeDouble = 0;
                double meleePrizeDouble = 0;
                double stonePrizeDouble = 0;
                if (!ringprize.isEmpty()) {
                    ringPrizeDouble = Double.parseDouble(ringprize);
                }
                if (!meleeprize.isEmpty()) {
                    meleePrizeDouble = Double.parseDouble(meleeprize);
                }
                if (!stoneprize.isEmpty()) {
                    stonePrizeDouble = Double.parseDouble(stoneprize);
                }
                if (color.contains("White")) {
                    ((TextView) findViewById(R.id.priceWhite)).setText(price);
                    ((TextView) findViewById(R.id.priceYellow)).setText(PricesListAdapter.roundPrice((ringPrizeDouble * 0.95 + stonePrizeDouble + meleePrizeDouble) + ""));
                    ((TextView) findViewById(R.id.priceRed)).setText(PricesListAdapter.roundPrice((ringPrizeDouble * 1.1 + stonePrizeDouble + meleePrizeDouble) + ""));
                    ((TextView) findViewById(R.id.pricePlatinum)).setText(PricesListAdapter.roundPrice((ringPrizeDouble * 1.35 + stonePrizeDouble + meleePrizeDouble) + ""));

                } else if (color.contains("Yellow")) {
                    ((TextView) findViewById(R.id.priceWhite)).setText(PricesListAdapter.roundPrice((ringPrizeDouble / 0.95 + stonePrizeDouble + meleePrizeDouble) + ""));
                    ((TextView) findViewById(R.id.priceYellow)).setText(price);
                    ((TextView) findViewById(R.id.priceRed)).setText(PricesListAdapter.roundPrice((ringPrizeDouble / 0.95 * 1.1 + stonePrizeDouble + meleePrizeDouble) + ""));
                    ((TextView) findViewById(R.id.pricePlatinum)).setText(PricesListAdapter.roundPrice((ringPrizeDouble / 0.95 * 1.35 + stonePrizeDouble + meleePrizeDouble) + ""));
                } else if (color.contains("Red")) {
                    ((TextView) findViewById(R.id.priceWhite)).setText(PricesListAdapter.roundPrice((ringPrizeDouble / 1.1 + stonePrizeDouble + meleePrizeDouble) + ""));
                    ((TextView) findViewById(R.id.priceYellow)).setText(PricesListAdapter.roundPrice((ringPrizeDouble / 1.1 * 0.95 + stonePrizeDouble + meleePrizeDouble) + ""));
                    ((TextView) findViewById(R.id.priceRed)).setText(price);
                    ((TextView) findViewById(R.id.pricePlatinum)).setText(PricesListAdapter.roundPrice((ringPrizeDouble / 1.1 * 1.35 + stonePrizeDouble + meleePrizeDouble) + ""));
                } else if (color.contains("Platinum")) {
                    ((TextView) findViewById(R.id.priceWhite)).setText(PricesListAdapter.roundPrice((ringPrizeDouble / 1.35 + stonePrizeDouble + meleePrizeDouble) + ""));
                    ((TextView) findViewById(R.id.priceYellow)).setText(PricesListAdapter.roundPrice((ringPrizeDouble / 1.35 * 0.95 + stonePrizeDouble + meleePrizeDouble) + ""));
                    ((TextView) findViewById(R.id.priceRed)).setText(PricesListAdapter.roundPrice((ringPrizeDouble / 1.35 * 1.1 + stonePrizeDouble + meleePrizeDouble) + ""));
                    ((TextView) findViewById(R.id.pricePlatinum)).setText(price);
                }


                if (path != null) {
                    ImageView img = findViewById(R.id.image);
                    Picasso.with(ViewInventoryActivity.this).load("http://195.15.223.234/aguadeoro/06_inventory toc opy/" + filename).placeholder(R.drawable.logo_small).into(img);
                }
            } else {
                Toast.makeText(ViewInventoryActivity.this,
                        getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }


}
