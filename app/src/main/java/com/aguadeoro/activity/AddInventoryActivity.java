package com.aguadeoro.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class AddInventoryActivity extends Activity {

    Calendar c;
    private View wheelView;
    private View mainView;
    private String inventCode;
    private String catalogCode;
    private String code;
    private String mark;
    private String category;
    private String description;
    private String width;
    private String height;
    private String material;
    private String color;
    private String weight;
    private String size;
    private String stone;
    private String stoneQuantity;
    private String stoneType;
    private String stoneSize;
    private String carat;
    private String stoneColor;
    private String stoneClarity;
    private String stoneCut;
    private String stonePolish;
    private String symetry;
    private String lab;
    private String certificate;
    private String cost;
    private String price;
    private String quantity;
    private String remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_add_inventory);
        wheelView = findViewById(R.id.register_status);
        mainView = findViewById(R.id.register_form);
        c = Calendar.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_inventory, menu);
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

    public void takePic(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        Intent chooser = Intent.createChooser(intent, getText(R.string.select_photo));

        PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{camera});
        }
        startActivityForResult(chooser, Utils.TAKE_INVENTORY_PIC);
    }


    public void saveInvt(View view) {
        inventCode = ((TextView) findViewById(R.id.inventory_code)).getText().toString();
        catalogCode = ((TextView) findViewById(R.id.catalog_code)).getText().toString();
        code = ((TextView) findViewById(R.id.code)).getText().toString();
        mark = ((TextView) findViewById(R.id.mark)).getText().toString();
        category = ((TextView) findViewById(R.id.category)).getText().toString();
        description = ((TextView) findViewById(R.id.width)).getText().toString();
        width = ((TextView) findViewById(R.id.width)).getText().toString();
        height = ((TextView) findViewById(R.id.height)).getText().toString();
        material = ((TextView) findViewById(R.id.material)).getText().toString();
        color = ((TextView) findViewById(R.id.color)).getText().toString();
        weight = ((TextView) findViewById(R.id.weight)).getText().toString();
        size = ((TextView) findViewById(R.id.size)).getText().toString();
        stone = ((TextView) findViewById(R.id.stone)).getText().toString();
        stoneQuantity = ((TextView) findViewById(R.id.stone_quantity)).getText().toString();
        stoneType = ((TextView) findViewById(R.id.stone_type)).getText().toString();
        stoneSize = ((TextView) findViewById(R.id.stone_size)).getText().toString();
        carat = ((TextView) findViewById(R.id.carat)).getText().toString();
        stoneColor = ((TextView) findViewById(R.id.stone_color)).getText().toString();
        stoneClarity = ((TextView) findViewById(R.id.stone_clarity)).getText().toString();
        stoneCut = ((TextView) findViewById(R.id.stone_cut)).getText().toString();
        stonePolish = ((TextView) findViewById(R.id.stone_polish)).getText().toString();
        symetry = ((TextView) findViewById(R.id.stone_symetry)).getText().toString();
        lab = ((TextView) findViewById(R.id.lab)).getText().toString();
        certificate = ((TextView) findViewById(R.id.certificate)).getText().toString();
        cost = ((TextView) findViewById(R.id.cost)).getText().toString();
        if (cost.isEmpty()) cost = "0";
        price = ((TextView) findViewById(R.id.price)).getText().toString();
        if (price.isEmpty()) price = "0";
        quantity = ((TextView) findViewById(R.id.quantity)).getText().toString();
        if (quantity.isEmpty()) quantity = "1";
        remark = ((TextView) findViewById(R.id.remark)).getText().toString();
        showProgress(true);
        new SaveInventoryTask().execute();
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

    public void goToInventoryDetailActivity(String inventoryId) {
        Intent intent = new Intent(this, ViewInventoryActivity.class);
        intent.putExtra(Utils.ID, inventoryId);
        startActivity(intent);
        finish();
    }

    public class SaveInventoryTask extends AsyncTask<String, String, Boolean> {
        String invtId;

        @Override
        protected Boolean doInBackground(String... inputs) {
            if (!Utils.isOnline()) {
                return false;
            }

            String query = "insert into Inventory(EntryDate, InventoryCode, "
                    + "CatalogCode, Code, Mark, Category, Description, Width," +
                    "Height, Material, Color, Weight, Size, Stone, StoneQuantity," +
                    "StoneType, StoneSize, Carat, StoneColor, StoneClarity," +
                    "StoneCut, StonePolish, StoneSymetry, LAB, Certificate," +
                    "Cost, Price, Quantity, Remark) "
                    + "values("
                    + Utils.escape(Utils.shortDateForInsert(c.getTime()))
                    + ", " + Utils.escape(inventCode)
                    + ", " + Utils.escape(catalogCode)
                    + ", " + Utils.escape(code)
                    + ", " + Utils.escape(mark)
                    + ", " + Utils.escape(category)
                    + ", " + Utils.escape(description)
                    + ", " + Utils.escape(width)
                    + ", " + Utils.escape(height)
                    + ", " + Utils.escape(material)
                    + ", " + Utils.escape(color)
                    + ", " + Utils.escape(weight)
                    + ", " + Utils.escape(size)
                    + ", " + Utils.escape(stone)
                    + ", " + Utils.escape(stoneQuantity)
                    + ", " + Utils.escape(stoneType)
                    + ", " + Utils.escape(stoneSize)
                    + ", " + Utils.escape(carat)
                    + ", " + Utils.escape(stoneColor)
                    + ", " + Utils.escape(stoneClarity)
                    + ", " + Utils.escape(stoneCut)
                    + ", " + Utils.escape(stonePolish)
                    + ", " + Utils.escape(symetry)
                    + ", " + Utils.escape(lab)
                    + ", " + Utils.escape(certificate)
                    + ", " + cost
                    + ", " + price
                    + ", " + quantity
                    + ", " + Utils.escape(remark)
                    + ")";
            Query q = new Query(query);
            if (!q.execute())
                return false;
            query = "select max(ID) from Inventory";
            q = new Query(query);
            if (!q.execute())
                return false;
            ArrayList<Map<String, String>> result = q.getRes();
            invtId = result.get(0).get("0");
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                goToInventoryDetailActivity(invtId);
            } else {
                Toast.makeText(AddInventoryActivity.this,
                        getString(R.string.error_retrieving_data), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}
