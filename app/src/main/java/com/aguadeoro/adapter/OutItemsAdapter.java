package com.aguadeoro.adapter;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    public OutItemsAdapter.ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.line_out_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( OutItemsAdapter.ViewHolder viewHolder, int i) {
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
        private final Spinner process;
        private final EditText flow;

        public ViewHolder( View itemView) {
            super(itemView);
            quantity = itemView.findViewById(R.id.itemQuantity);
            productId = itemView.findViewById(R.id.id);
            search = itemView.findViewById(R.id.search);
            productCode = itemView.findViewById(R.id.productCode);
            remark = itemView.findViewById(R.id.remark);
            process = itemView.findViewById(R.id.process);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    context, R.array.process_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            process.setAdapter(adapter);
            flow = itemView.findViewById(R.id.flow);
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

       public Spinner getProcess() {
            return process;
       }

     public EditText getFlow() {return flow;}

    }
}
