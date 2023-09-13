package com.aguadeoro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aguadeoro.R;
import com.aguadeoro.utils.Utils;

import java.util.List;

public class CustomerListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;

    public CustomerListAdapter(Context context, List<String[]> objects) {
        super(context, R.layout.line_customer, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_customer, parent, false);
        TextView line1 = rowView.findViewById(R.id.line1);
        TextView line2 = rowView.findViewById(R.id.line2);
        TextView date1 = rowView.findViewById(R.id.date1);
        TextView date2 = rowView.findViewById(R.id.date2);
        TextView date3 = rowView.findViewById(R.id.date3);
        TextView type = rowView.findViewById(R.id.invoiceDate);
        line1.setText(objects.get(position)[1]);
        line2.setText(objects.get(position)[2]);
        if (objects.get(position)[2] == null) {
            line2.setVisibility(View.INVISIBLE);
        }
        date1.setText(Utils.shortDateFromDB(objects.get(position)[3]));
        date2.setText(Utils.shortDateFromDB(objects.get(position)[4]));
        date3.setText(Utils.shortDateFromDB(objects.get(position)[6]));
        type.setText(objects.get(position)[5]);
        return rowView;
    }

}
