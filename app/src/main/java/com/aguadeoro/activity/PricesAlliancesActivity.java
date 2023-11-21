package com.aguadeoro.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.adapter.PricesAllianceAdapter;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class PricesAlliancesActivity extends Activity {

    String line;
    ArrayList<Map<String, String>> data = new ArrayList<>();
    PricesAllianceAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Map<String, Object>> selectedPrices;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.line = intent.getStringExtra("line");
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_prices_alliance);
        ((TextView) findViewById(R.id.lineName)).setText(line);
        Button copy = findViewById(R.id.copyBtn);
        Button clear = findViewById(R.id.clearBtn);
        recyclerView = findViewById(R.id.list);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new GetAlliancesData().execute(line);

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", ((TextView) findViewById(R.id.toOrderEdit)).getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(PricesAlliancesActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPrices.clear();
                ((EditText) findViewById(R.id.toOrderEdit)).setText("");
                new GetAlliancesData().execute(line);
            }
        });

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
        ArrayList<String> codePresent = new ArrayList<>();


        for (Map<String, Object> selectedPrice : selectedPrices) {
            if (!codePresent.contains(Objects.requireNonNull(selectedPrice.get("catcode")).toString())) {
                codePresent.add(Objects.requireNonNull(selectedPrice.get("catcode")).toString());
            }
        }
        for (int i = 0; i < codePresent.size(); i++) {
            lineTxt.append(codePresent.get(i));
            lineTxt.append(" : ");
            for (Map<String, Object> selectedPrice : selectedPrices) {
                if (selectedPrice.get("catcode").toString().equals(codePresent.get(i))) {
                    lineTxt.append(selectedPrice.get("metal"));
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

    private class GetAlliancesData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String line = strings[0];
            Query q = new Query("select * from Inventory where Category = 'Alliance' and Line = " + Utils.escape(line) + " order by CatalogCode ASC");
            q.execute();
            data = q.getRes();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            adapter = new PricesAllianceAdapter(PricesAlliancesActivity.this, data);
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

        return super.onOptionsItemSelected(item);
    }
}
