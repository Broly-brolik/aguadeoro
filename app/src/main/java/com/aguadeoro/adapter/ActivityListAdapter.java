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

public class ActivityListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;

    public ActivityListAdapter(Context context, List<String[]> objects) {
        super(context, R.layout.line_activity, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_activity, parent, false);
        TextView date = rowView.findViewById(R.id.date);
        TextView remark = rowView.findViewById(R.id.remark);
        remark.setText(objects.get(position)[1]);
        date.setText(Utils.shortDateFromDB(objects.get(position)[0]));
        return rowView;
    }

}
