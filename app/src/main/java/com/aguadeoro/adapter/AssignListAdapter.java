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

public class AssignListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;

    public AssignListAdapter(Context context, List<String[]> objects, boolean showCustomer) {
        super(context, R.layout.line_assign, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_assign, parent, false);
        TextView assign = rowView.findViewById(R.id.assign);
        TextView quantity = rowView.findViewById(R.id.quantity);
        TextView date = rowView.findViewById(R.id.date);
        TextView status = rowView.findViewById(R.id.status);
        assign.setText(objects.get(position)[2]);
        quantity.setText(objects.get(position)[3]);
        date.setText(Utils.shortDateFromDB(objects.get(position)[5]));
        status.setText(objects.get(position)[4]);

        return rowView;
    }

}
