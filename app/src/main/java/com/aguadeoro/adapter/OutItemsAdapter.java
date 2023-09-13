package com.aguadeoro.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.aguadeoro.R;

import java.util.ArrayList;
import java.util.Map;

public class OutItemsAdapter extends RecyclerView.Adapter<OutItemsAdapter.ViewHolder> {
    ArrayList<Map<String, String>> dataset;
    ArrayList<String> count;

    public OutItemsAdapter(ArrayList<Map<String, String>> data, ArrayList<String> count) {
        dataset = data;
        this.count = count;
        Log.d("------", "" + count);
    }

    @NonNull
    @Override
    public OutItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.line_out_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutItemsAdapter.ViewHolder viewHolder, int i) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!viewHolder.getId().getText().toString().isEmpty()) {
                    for (int j = 0; j < dataset.size(); j++) {
                        String parsedId = String.valueOf((int) (Double.parseDouble(dataset.get(j).get("ID"))));
                        if (viewHolder.getId().getText().toString().equals(parsedId)) {
                            viewHolder.getProductCode().setText(dataset.get(j).get("ProductCode"));
                        }
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return count.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final EditText quantity;
        private final EditText productId;
        private final ImageView search;
        private final EditText productCode;
        private final EditText remark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            quantity = itemView.findViewById(R.id.itemQuantity);
            productId = itemView.findViewById(R.id.id);
            search = itemView.findViewById(R.id.search);
            productCode = itemView.findViewById(R.id.productCode);
            remark = itemView.findViewById(R.id.remark);
        }

        public EditText getQuantity() {
            return quantity;
        }

        public EditText getId() {
            return productId;
        }

        public ImageView getSearch() {
            return search;
        }

        public EditText getProductCode() {
            return productCode;
        }

        public EditText getRemark() {
            return remark;
        }
    }
}
