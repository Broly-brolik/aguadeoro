package com.aguadeoro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.aguadeoro.R;
import com.aguadeoro.utils.Utils;

import java.util.List;

public class CustomerHistoryListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;
    private final boolean action;

    public CustomerHistoryListAdapter(Context context, List<String[]> objects, boolean isForAction) {
        super(context, R.layout.line_customer_history, objects);
        this.context = context;
        this.objects = objects;
        this.setNotifyOnChange(true);
        this.action = isForAction;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_customer_history, parent, false);
        TextView date = rowView.findViewById(R.id.date);
        TextView remark = rowView.findViewById(R.id.remark);
        CheckBox checkbox = rowView.findViewById(R.id.checkbox);
        date.setText(Utils.shortDateFromDB(objects.get(position)[0]));
        remark.setText(objects.get(position)[1]);
        if (action) {
            checkbox.setChecked(!objects.get(position)[2].isEmpty());
            checkbox.setVisibility(View.VISIBLE);
        }
        return rowView;
    }

}
