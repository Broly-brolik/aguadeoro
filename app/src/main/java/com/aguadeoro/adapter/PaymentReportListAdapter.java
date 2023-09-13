package com.aguadeoro.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.aguadeoro.R;
import com.aguadeoro.activity.OrderDetailActivity;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PaymentReportListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;
    private final boolean[] check;

    public PaymentReportListAdapter(Context context, List<String[]> objects) {
        super(context, R.layout.line_report_payment, objects);
        this.context = context;
        this.objects = objects;
        check = new boolean[objects.size()];
    }

    public ArrayList<String> getCheckedIDs() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < check.length; i++) {
            if (check[i])
                list.add(objects.get(i)[7]);
        }
        return list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_report_payment, parent,
                false);
        TextView orderNo = rowView.findViewById(R.id.order_number);
        TextView date = rowView.findViewById(R.id.date);
        TextView customer = rowView.findViewById(R.id.customer);
        TextView paymode = rowView.findViewById(R.id.paymode);
        TextView amt = rowView.findViewById(R.id.amt);
        TextView checkedBy = rowView.findViewById(R.id.checkedby);
        TextView checkedOn = rowView.findViewById(R.id.checkedon);
        CheckBox selected = rowView.findViewById(R.id.checkbox);
        orderNo.setText(objects.get(position)[0]);
        date.setText(Utils.shortDateFromDB(objects.get(position)[8]));
        customer.setText(Utils.shortDateFromDB(objects.get(position)[2]));
        paymode.setText(objects.get(position)[3]);
        amt.setText(objects.get(position)[4]);
        checkedBy.setText(objects.get(position)[5]);
        checkedOn.setText(Utils.shortDateFromDB(objects.get(position)[6]));
        orderNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("OrderNumber", objects.get(position)[0]);
                context.startActivity(intent);
            }
        });
        if (!objects.get(position)[5].isEmpty()) {
            selected.setVisibility(View.GONE);
        } else {
            selected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    check[position] = isChecked;
                }
            });
            selected.setChecked(check[position]);
        }

        return rowView;
    }

}
