package com.aguadeoro.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.activity.CreateInvoiceActivity;
import com.aguadeoro.utils.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateInvoiceListAdapter extends ArrayAdapter<String[]> {
    private final Context context;
    private final List<String[]> objects;
    private EditText amount;
    private EditText remark;
    private EditText detail;
    private final boolean[] check;
    private final boolean checkfirst = true;
    private final HashMap<String, String> textValues = new HashMap<>();
    private final boolean hasADO;
    private boolean generatedLines = false;
    private final ArrayList<Map<String, String>> stockHistory;
    private final ArrayList<Map<String, String>> productsInfos;
    private final HashMap<Integer, String> spinners = new HashMap<>();
    private Map<String, String> newProduct = new HashMap<>();
    private String trueOrderNumber = "";
    private View adapterView;


    public CreateInvoiceListAdapter(Context context, List<String[]> objects, boolean hasADO, ArrayList<Map<String, String>> stockHistory, ArrayList<Map<String, String>> productsInfos) {
        super(context, R.layout.line_create_invoice, objects);
        this.context = context;
        this.objects = objects;
        this.hasADO = hasADO;
        this.stockHistory = stockHistory;
        this.productsInfos = productsInfos;
        check = new boolean[objects.size()];
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;
        final ViewHolder vh;
        if (view == null) {
            view = inflater.inflate(R.layout.line_create_invoice, parent, false);
            vh = new ViewHolder();
            vh.one = view.findViewById(R.id.amount);
            vh.two = view.findViewById(R.id.remark);
            vh.three = view.findViewById(R.id.detail);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        TextView orderNumber = view.findViewById(R.id.orderNumber);
        TextView instruction = view.findViewById(R.id.instruction);
        TextView artNo = view.findViewById(R.id.article_number);
        TextView recipient = view.findViewById(R.id.recipient);
        vh.posit = position;

        recipient.setText(objects.get(position)[7] + " |");
        if (objects.get(position)[2].length() == 0) {
            artNo.setText("N/A |");
        } else {
            artNo.setText(objects.get(position)[2] + " |");
        }
        if (objects.get(position)[5].length() == 0) {
            instruction.setText("N/A");
        } else {
            instruction.setText(objects.get(position)[5]);
        }
        orderNumber.setText(objects.get(position)[13] + " |");
        vh.one.setTag("firstEditAtPos" + position);
        vh.two.setTag("secondEditAtPos" + position);
        vh.three.setTag("thirdEditAtPos" + position);
        vh.one.setText(textValues.get("firstEditAtPos" + position));
        vh.two.setText(textValues.get("secondEditAtPos" + position));
        vh.three.setText(textValues.get("thirdEditAtPos" + position));
        if (vh.three.getText().toString().equals("")) {
            vh.three.setText(objects.get(position)[13]);
            textValues.put("thirdEditAtPos" + position, objects.get(position)[13]);
        }
        vh.one.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    EditText am = view.findViewById(R.id.amount);
                    textValues.put("firstEditAtPos" + position, am.getText().toString().trim());
                }
            }
        });
        vh.two.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    EditText am = view.findViewById(R.id.remark);
                    textValues.put("secondEditAtPos" + position, am.getText().toString().trim());
                }
            }
        });
        vh.three.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    EditText am = view.findViewById(R.id.detail);
                    textValues.put("thirdEditAtPos" + position, am.getText().toString().trim());
                }
            }
        });
        if (hasADO && !generatedLines && stockHistory.size() > 0 && productsInfos.size() > 0) {
            generateLines(view, position);
        }
        return view;

    }


    private void generateLines(View view, int position) {
        if (objects.get(position)[7].equals("ADO Stones")) {
            final ArrayList<String> spinnerArray = new ArrayList<>();
            spinnerArray.add("New");
            spinnerArray.add("Used");
            spinnerArray.add("Returned");
            spinnerArray.add("Lost");
            spinnerArray.add("Damaged");
            spinnerArray.add("Other");
            LinearLayout rootLayout = view.findViewById(R.id.rootLayoutInvoice);
            final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout header = new LinearLayout(context);
            header.setLayoutParams(lparams);
            header.setVisibility(View.VISIBLE);
            header.setOrientation(LinearLayout.HORIZONTAL);

            TextView productHeader = new TextView(context);
            productHeader.setText("     ID");
            productHeader.setLayoutParams(lparams);
            productHeader.setTextSize(15f);
            productHeader.setWidth(100);
            TextView shortCodeHeader = new TextView(context);
            shortCodeHeader.setText("Shortcode");
            shortCodeHeader.setLayoutParams(lparams);
            shortCodeHeader.setTextSize(15f);
            shortCodeHeader.setWidth(250);
            TextView quantityHeader = new TextView(context);
            quantityHeader.setText("Quantity");
            quantityHeader.setTextSize(15f);
            quantityHeader.setLayoutParams(lparams);
            quantityHeader.setWidth(100);
            TextView weightHeader = new TextView(context);
            weightHeader.setText("Weight");
            weightHeader.setLayoutParams(lparams);
            weightHeader.setTextSize(15f);
            weightHeader.setWidth(100);
            TextView costHeader = new TextView(context);
            costHeader.setText("Cost");
            costHeader.setLayoutParams(lparams);
            costHeader.setTextSize(15f);
            costHeader.setWidth(100);
            TextView movementHeader = new TextView(context);
            movementHeader.setText("Movement type");
            movementHeader.setLayoutParams(lparams);
            movementHeader.setTextSize(15f);
            movementHeader.setWidth(200);

            View line = new View(context);
            line.setLayoutParams(new LinearLayout.LayoutParams(
                    1000,
                    1
            ));
            line.setBackgroundColor(Color.parseColor("#B3B3B3"));

            header.addView(productHeader);
            header.addView(shortCodeHeader);
            header.addView(quantityHeader);
            header.addView(weightHeader);
            header.addView(costHeader);
            header.addView(movementHeader);
            if (header.getParent() != null) {
                ((ViewGroup) header.getParent()).removeView(header);
            }
            rootLayout.addView(header, lparams);
            rootLayout.addView(line);
            rootLayout.invalidate();

            for (int i = 0; i < stockHistory.size(); i++) {
                String fullOrderNumber = objects.get(position)[13];
                Matcher matcher = Pattern.compile("\\d").matcher(fullOrderNumber);
                matcher.find();
                trueOrderNumber = fullOrderNumber.substring(matcher.start());
                Log.d("order numbers", "" + stockHistory.get(i).get("OrderNumber") + " " + objects.get(position)[13] + " " + trueOrderNumber);
                if (Objects.equals(stockHistory.get(i).get("OrderNumber"), trueOrderNumber)) {
                    LinearLayout newLayout = new LinearLayout(context);
                    newLayout.setLayoutParams(lparams);
                    newLayout.setVisibility(View.VISIBLE);
                    newLayout.setOrientation(LinearLayout.HORIZONTAL);

                    final TextView productID = new TextView(context);
                    productID.setText("     " + stockHistory.get(i).get("ProductID"));
                    productID.setLayoutParams(lparams);
                    productID.setTextSize(15f);
                    productID.setWidth(100);
                    final TextView shortCode = new TextView(context);
                    String code = "";
                    if (stockHistory.get(i).containsKey("ShortCode")) {
                        code = stockHistory.get(i).get("ShortCode");
                    } else {
                        for (Map<String, String> product : productsInfos) {
                            if ((int) Double.parseDouble(product.get("ID")) == Integer.parseInt(Objects.requireNonNull(stockHistory.get(i).get("ProductID")))) {
                                code = product.get("ProductCode");
                            }
                        }
                    }
                    shortCode.setText("     " + code);
                    shortCode.setLayoutParams(lparams);
                    shortCode.setTextSize(15f);
                    shortCode.setWidth(250);
                    final TextView quantity = new TextView(context);
                    quantity.setText("     " + stockHistory.get(i).get("Quantity"));
                    quantity.setTextSize(15f);
                    quantity.setLayoutParams(lparams);
                    quantity.setWidth(100);
                    quantity.setTag("quantity" + i);
                    final TextView weight = new TextView(context);
                    weight.setText("     " + stockHistory.get(i).get("Weight"));
                    weight.setLayoutParams(lparams);
                    weight.setTextSize(15f);
                    weight.setWidth(100);
                    weight.setTag("weight" + i);
                    final TextView cost = new TextView(context);
                    cost.setText("     " + stockHistory.get(i).get("Cost"));
                    cost.setLayoutParams(lparams);
                    cost.setTextSize(15f);
                    cost.setWidth(100);
                    final TextView type = new TextView(context);
                    type.setLayoutParams(lparams);
                    if (!stockHistory.get(i).containsKey("Type")) {
                        stockHistory.get(i).put("Type", "New");
                    }
                    type.setText("     " + stockHistory.get(i).get("Type"));
                    type.setTextSize(15f);
                    type.setWidth(100);
                    type.setTag("type" + i);
                    Button edit = new Button(context);
                    edit.setLayoutParams(lparams);
                    edit.setText("Edit");
                    final int finalI = i;
                    final String finalCode = code;
                    final View finalConvertView = view;
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editDialog(finalI, finalCode, spinnerArray, finalConvertView);
                        }
                    });

                    newLayout.addView(productID, lparams);
                    newLayout.addView(shortCode, lparams);
                    newLayout.addView(quantity, lparams);
                    newLayout.addView(weight, lparams);
                    newLayout.addView(cost, lparams);
                    newLayout.addView(type, lparams);
                    newLayout.addView(edit);
                    if (newLayout.getParent() != null) {
                        ((ViewGroup) newLayout.getParent()).removeView(newLayout);
                    }
                    rootLayout.addView(newLayout, lparams);
                    rootLayout.invalidate();
                }
            }
            LinearLayout newLayout = new LinearLayout(context);
            newLayout.setLayoutParams(lparams);
            newLayout.setVisibility(View.VISIBLE);
            newLayout.setOrientation(LinearLayout.HORIZONTAL);
            newLayout.setMinimumHeight(40);
            Button addLine = new Button(context);
            addLine.setLayoutParams(lparams);
            addLine.setText("Add");
            addLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addDialog(spinnerArray);
                }
            });
            newLayout.addView(addLine);
            rootLayout.addView(newLayout, lparams);
            rootLayout.invalidate();
            generatedLines = true;
        }
    }

    private void editDialog(final int finalI, final String finalCode,
                            final ArrayList<String> spinnerArray, final View finalConvertView) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit item");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_edit_invoice, null);
        ((TextView) dialogView.findViewById(R.id.productId)).setText(stockHistory.get(finalI).get("ProductID"));
        ((TextView) dialogView.findViewById(R.id.shortcode)).setText(finalCode);
        final Spinner quantitySpinner = dialogView.findViewById(R.id.quantitySpinner);
        int maxQuantity = Integer.parseInt(stockHistory.get(finalI).get("Quantity"));
        ArrayList<String> allQuantities = new ArrayList<>();
        for (int j = 0; j <= maxQuantity; j++) {
            allQuantities.add("" + j);
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (context, android.R.layout.simple_spinner_dropdown_item, allQuantities);
        quantitySpinner.setAdapter(spinnerArrayAdapter);
        ((EditText) dialogView.findViewById(R.id.weight)).setText(stockHistory.get(finalI).get("Weight"));
        final Spinner movementSpinner = dialogView.findViewById(R.id.movementSpinner);
        spinnerArrayAdapter = new ArrayAdapter<String>
                (context, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        movementSpinner.setAdapter(spinnerArrayAdapter);
        builder.setView(dialogView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((TextView) finalConvertView.findViewWithTag("quantity" + finalI))
                                .setText(quantitySpinner.getSelectedItem().toString());
                        ((TextView) finalConvertView.findViewWithTag("weight" + finalI))
                                .setText(((EditText) dialogView.findViewById(R.id.weight)).getText().toString());
                        ((TextView) finalConvertView.findViewWithTag("type" + finalI))
                                .setText(movementSpinner.getSelectedItem().toString());
                        stockHistory.get(finalI).put("Quantity", quantitySpinner.getSelectedItem().toString());
                        stockHistory.get(finalI).put("Weight", ((EditText) dialogView.findViewById(R.id.weight)).getText().toString());
                        stockHistory.get(finalI).put("Type", movementSpinner.getSelectedItem().toString());
                        ((CreateInvoiceActivity) context).setStockHistory(stockHistory);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        builder.show();
    }

    private void addDialog(final ArrayList<String> spinnerArray) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add item");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_add_invoice, null);
        final EditText productId = dialogView.findViewById(R.id.productId);
        final EditText weight = dialogView.findViewById(R.id.weight);
        final EditText quantity = dialogView.findViewById(R.id.quantity);
        final EditText cost = dialogView.findViewById(R.id.cost);
        final Spinner spinner = dialogView.findViewById(R.id.movementSpinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinner.setAdapter(spinnerArrayAdapter);
        dialogView.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!productId.getText().toString().isEmpty()) {
                    Toast.makeText(context, "searching...", Toast.LENGTH_SHORT).show();
                    new getProduct(dialogView).execute(productId.getText().toString());
                }
            }
        });
        builder.setView(dialogView).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String, String> newData = new HashMap<String, String>() {{
                    put("ProductID", newProduct.get("ID"));
                    put("OrderNumber", "" + trueOrderNumber);
                    put("ShortCode", newProduct.get("ProductCode"));
                    put("Description", newProduct.get("Description"));
                    put("Weight", "" + weight.getText().toString());
                    put("Quantity", "" + quantity.getText().toString());
                    put("Cost", "" + cost.getText().toString());
                    put("Type", "" + spinner.getSelectedItem().toString());
                }};
                stockHistory.add(newData);
                ((CreateInvoiceActivity) context).setStockHistory(stockHistory);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public ArrayList<String> getCheckedIDs() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < check.length; i++) {
            if (check[i])
                list.add(getValueFromFirstEditText(i));
        }
        return list;
    }

    public EditText getAmount() {
        return amount;
    }

    public EditText getRemark() {
        return remark;
    }

    public EditText getDetail() {
        return detail;
    }


    private class GenericTextWatcher implements TextWatcher {

        private final View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            //save the value for the given tag :
            CreateInvoiceListAdapter.this.textValues.put((String) view.getTag(), text);
            for (String key : textValues.keySet()) {
                Log.d("changetext", "" + key + " " + CreateInvoiceListAdapter.this.textValues.get(key));
            }
        }
    }

    public ArrayList<Map<String, String>> getStockHistory() {
        Log.e("stockhistory size adapt2", "" + stockHistory.size());
        return stockHistory;
    }

    public String getValueFromFirstEditText(int position) {
        //here you need to recreate the id for the first editText
        String result = textValues.get("firstEditAtPos" + position);
        if (result == null)
            result = "0";

        return result;
    }


    public String getValueFromSecondEditText(int position) {
        //here you need to recreate the id for the second editText
        String result = textValues.get("secondEditAtPos" + position);
        if (result == null) {
            result = "";
        }

        return result;
    }

    public String getValueFromThirdEditText(int position) {
        //here you need to recreate the id for the second editText
        String result = textValues.get("thirdEditAtPos" + position);
        Log.d("testDetails", result + "");
        if (result == null)
            result = "";

        return result;
    }

    static class ViewHolder {
        int posit;
        EditText one;
        EditText two;
        EditText three;
    }

    private class getProduct extends AsyncTask<String, String, String> {
        View view;

        public getProduct(View view) {
            super();
            this.view = view;
        }

        @Override
        protected String doInBackground(String... args) {
            String query = "select * from Products where ID = " + args[0];
            Query q = new Query(query);
            boolean s = q.execute();
            if (!s) {
                return "";
            }
            newProduct = q.getRes().get(0);
            return q.getRes().get(0).get("ProductCode");
        }

        @Override
        protected void onPostExecute(String shortCode) {
            Toast.makeText(context, "done", Toast.LENGTH_SHORT).show();
            if (!shortCode.isEmpty()) {
                ((TextView) view.findViewById(R.id.shortcode)).setText(shortCode);
            }
        }
    }
}
