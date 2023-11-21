package com.aguadeoro.adapter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aguadeoro.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateInvoiceINAdapter extends RecyclerView.Adapter<CreateInvoiceINAdapter.ViewHolder> {

    private final ArrayList<Map<String, String>> stockData;
    private final ArrayList<Map<String, String>> productsData;

    public CreateInvoiceINAdapter(ArrayList<Map<String, String>> stockData, ArrayList<Map<String, String>> productsData) {
        this.stockData = stockData;
        this.productsData = productsData;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.line_create_invoice_in_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String, String> currentProduct = null;
        for (Map<String, String> product : productsData) {
            Log.d("test", "product: " + product.get("ID") + " - " + stockData.get(position).get("ProductID"));
            if ((stockData.get(position).get("ProductID") + ".0").equals(product.get("ID"))) {
                currentProduct = product;
            }
        }
        holder.orderNumber.setText(stockData.get(position).get("OrderNumber"));
        holder.id.setText(stockData.get(position).get("ProductID"));
        if (currentProduct != null) {
            holder.shortCode.setText(currentProduct.get("ProductCode"));
        }
        if (stockData.get(position).get("Type").equals("2")) {
            holder.outQuantity.setText(stockData.get(position).get("Quantity"));
        }
    }

    @Override
    public int getItemCount() {
        return stockData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber;
        TextView id;
        TextView shortCode;
        TextView outQuantity;
        EditText usedQuantity;
        EditText returnedQuantity;
        EditText lostQuantity;


        public ViewHolder(View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.orderNumber);
            id = itemView.findViewById(R.id.id);
            shortCode = itemView.findViewById(R.id.shortcode);
            outQuantity = itemView.findViewById(R.id.out);
            usedQuantity = itemView.findViewById(R.id.used);
            returnedQuantity = itemView.findViewById(R.id.returned);
            lostQuantity = itemView.findViewById(R.id.lost);
        }
    }

    public void addRow() {
        stockData.add(new HashMap<String, String>() {
            {
                if (stockData.size() > 0) {
                    put("OrderNumber", stockData.get(stockData.size() - 1).get("OrderNumber"));
                } else {
                    put("OrderNumber", "");
                }
                put("ProductID", "");
                put("Quantity", "");
                put("Type", "1");
            }
        });
        notifyDataSetChanged();
    }
}
