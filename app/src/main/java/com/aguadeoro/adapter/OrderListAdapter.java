package com.aguadeoro.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aguadeoro.R;
import com.aguadeoro.activity.OrderDetailActivity;
import com.aguadeoro.utils.Utils;

import java.util.List;

public class OrderListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;
    boolean showCustomer, showTotal, showSeller;

    public OrderListAdapter(Context context, List<String[]> objects, boolean showCustomer, boolean showTotal, boolean showSeller) {
        super(context, R.layout.line_order, objects);
        this.context = context;
        this.objects = objects;
        this.showCustomer = showCustomer;
        this.showTotal = showTotal;
        this.showSeller = showSeller;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_order, parent, false);
        TextView orderNo = rowView.findViewById(R.id.order_number);
        TextView status = rowView.findViewById(R.id.status);
        TextView date = rowView.findViewById(R.id.date);
        TextView deadline = rowView.findViewById(R.id.deadline);
        TextView remain = rowView.findViewById(R.id.remain);
        TextView customer = rowView.findViewById(R.id.customer);
        TextView type = rowView.findViewById(R.id.order_type);
        TextView seller = rowView.findViewById(R.id.seller);
        TextView total = rowView.findViewById(R.id.total);
        orderNo.setText(objects.get(position)[0]);
        status.setText(objects.get(position)[1]);
        date.setText(Utils.shortDateFromDB(objects.get(position)[2]));
        deadline.setText(Utils.shortDateFromDB(objects.get(position)[3]));
        remain.setText(objects.get(position)[5]);
        if (objects.get(position)[5] != null && !objects.get(position)[5].startsWith("0")) {
            remain.setTextColor(Color.RED);
        }
        if (showCustomer) {
            customer.setText(objects.get(position)[6]);
        } else {
            customer.setVisibility(View.GONE);
        }
        if (showTotal) {
            total.setText(objects.get(position)[4]);
        } else {
            total.setVisibility(View.GONE);
        }
        type.setText(objects.get(position)[7]);
        seller.setText(objects.get(position)[8]);
        orderNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("OrderNumber", objects.get(position)[0]);
                context.startActivity(intent);
            }
        });

        return rowView;
    }

}
