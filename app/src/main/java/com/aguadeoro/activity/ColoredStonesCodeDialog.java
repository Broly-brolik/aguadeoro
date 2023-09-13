package com.aguadeoro.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.aguadeoro.R;
import com.aguadeoro.utils.Query;

import java.util.ArrayList;
import java.util.Map;

public class ColoredStonesCodeDialog {

    private final Context context;
    private Dialog loadingDialog;

    public ColoredStonesCodeDialog(Context context) {
        this.context = context;
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Get colored stones infos");
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText input = new EditText(context);
        input.setHint("Enter ID");
        layout.addView(input);
        TextView or = new TextView(context);
        or.setText("OR");
        layout.addView(or);
        EditText input2 = new EditText(context);
        input2.setHint("Enter color (B,G,K,O,R,V,Y,P)");
        layout.addView(input2);
        builder.setView(layout);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String id = input.getText().toString();
            String color = input2.getText().toString();
            showLoadingDialog();
            new getNewCode().execute(id, color);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    class getNewCode extends AsyncTask<String, Void, ArrayList<Map<String, String>>> {
        @Override
        protected ArrayList<Map<String, String>> doInBackground(String... params) {
            String id = params[0];
            String color = params[1];
            String q = "";
            if (id.length() > 0) {
                if (id.matches("^[0-9]+$")) {
                    q = "select *" +
                            "FROM Color_Matrix " +
                            "WHERE (Tones<>'') and (PriceCODE <>'')  and Products.ID = " + id + " ORDER BY ID;";
                }
            } else if (color.length() > 0) {
                q = "select * " +
                        "FROM Color_Matrix " +
                        "WHERE (Tones<>'') and (PriceCODE <>'') and Color_Matrix.Tones like '" + color + "%' " +
                        "ORDER BY ID;";

            }

            Query query = new Query(q);
            query.execute();
            return query.getRes();
        }

        @Override
        protected void onPostExecute(ArrayList<Map<String, String>> result) {
            super.onPostExecute(result);
            dismissLoadingDialog();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Colored stones infos");
            ScrollView scrollView = new ScrollView(context);
            TableLayout tableLayout = new TableLayout(context);
            tableLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            tableLayout.setPadding(10, 10, 10, 10);
            tableLayout.setDividerDrawable(context.getResources().getDrawable(R.drawable.divider));
            TableRow tableRow = new TableRow(context);
            tableRow.setPadding(10, 10, 10, 25);
            TextView textView = new TextView(context);
            textView.setText("Product");
            textView.setPadding(0, 0, 20, 0);
            tableRow.addView(textView);
            textView = new TextView(context);
            textView.setText("Color");
            textView.setPadding(0, 0, 20, 0);
            tableRow.addView(textView);
            textView = new TextView(context);
            textView.setText("Description");
            textView.setPadding(0, 0, 20, 0);
            tableRow.addView(textView);
            textView = new TextView(context);
            textView.setText("Supplier code");
            textView.setPadding(0, 0, 20, 0);
            tableRow.addView(textView);
            textView = new TextView(context);
            textView.setText("Price code");
            textView.setPadding(0, 0, 20, 0);
            tableRow.addView(textView);
            TableLayout.LayoutParams tableRowParams =
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            tableRowParams.setMargins(0, 0, 0, 10);
            tableRow.setLayoutParams(tableRowParams);
            tableLayout.addView(tableRow);
            for (Map<String, String> map : result) {
                tableRow = new TableRow(context);
                tableRow.setPadding(10, 10, 10, 25);
                textView = new TextView(context);
                textView.setText("" + Math.round(Double.parseDouble(map.get("ID"))));
                textView.setPadding(0, 0, 20, 0);
                tableRow.addView(textView);
                textView = new TextView(context);
                textView.setText("" + map.get("Tones"));
                textView.setPadding(0, 0, 20, 0);
                tableRow.addView(textView);
                textView = new TextView(context);
                textView.setText("" + map.get("Description"));
                textView.setWidth(400);
                textView.setPadding(0, 0, 20, 0);
                tableRow.addView(textView);
                textView = new TextView(context);
                textView.setText("" + map.get("Supplier_code"));
                textView.setPadding(0, 0, 20, 0);
                tableRow.addView(textView);
                textView = new TextView(context);
                textView.setText("" + map.get("PriceCODE"));
                textView.setPadding(0, 0, 20, 0);
                tableRow.addView(textView);
                tableRow.setBackground(context.getResources().getDrawable(R.drawable.divider));
                tableLayout.addView(tableRow);
            }
            scrollView.addView(tableLayout);
            builder.setView(scrollView);
            if (result.size() == 0) {
                builder.setMessage("ID not found");
            }
            builder.show();
        }
    }

    private void showLoadingDialog() {
        loadingDialog = new Dialog(context, android.R.style.Theme_Black);
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_loading, null);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.getWindow().setBackgroundDrawableResource(
                R.color.transparent);
        loadingDialog.setContentView(view);
        loadingDialog.show();
    }

    private void dismissLoadingDialog() {
        loadingDialog.dismiss();
    }
}
