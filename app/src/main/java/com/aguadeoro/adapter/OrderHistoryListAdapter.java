package com.aguadeoro.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aguadeoro.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class OrderHistoryListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;

    public OrderHistoryListAdapter(Context context, List<String[]> objects) {
        super(context, R.layout.line_order_history, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_order_history, parent, false);
        TextView date = rowView.findViewById(R.id.pay_date);
        TextView remark = rowView.findViewById(R.id.remark);
        Log.d("failed to convert data", "HH:mm:ss"+objects.get(position)[0]);
        try {
            Date date2 = new SimpleDateFormat("dd-MM-yyyy HH:mm z").parse(objects.get(position)[0]);
            date.setText(date2.toString());
        } catch (ParseException e) {
            Log.e("failed to convert date", "" + e);
            date.setText(objects.get(position)[0]);
        }
        remark.setText(objects.get(position)[1]);
        return rowView;
    }

}
