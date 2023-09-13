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

public class ComponentHistoryListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;
    private final boolean showCheckbox;

    public ComponentHistoryListAdapter(Context context, List<String[]> objects, boolean showCheckbox) {
        super(context, R.layout.line_component_history, objects);
        this.context = context;
        this.objects = objects;
        this.showCheckbox = showCheckbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_component_history, parent, false);
        ((TextView) rowView.findViewById(R.id.comp_deadline)).setText(Utils.shortDateFromDB(objects.get(position)[1]));
        ((TextView) rowView.findViewById(R.id.comp_recipient)).setText(objects.get(position)[2]);
        ((TextView) rowView.findViewById(R.id.comp_status)).setText(objects.get(position)[3]);
        ((TextView) rowView.findViewById(R.id.comp_instruction)).setText(objects.get(position)[4]);
        ((TextView) rowView.findViewById(R.id.comp_date)).setText(Utils.shortDateFromDB(objects.get(position)[5]));
        ((TextView) rowView.findViewById(R.id.comp_step)).setText(objects.get(position)[6]);
        if (objects.get(position)[8] != null && objects.get(position)[8].length() > 0) {
            ((TextView) rowView.findViewById(R.id.comp_remark)).setText(objects.get(position)[8]);
            rowView.findViewById(R.id.comp_remark).setVisibility(View.VISIBLE);
        }
        ((TextView) rowView.findViewById(R.id.sup_ord_no)).setText(objects.get(position)[9]);
        if (showCheckbox) {
            rowView.findViewById(R.id.selected).setVisibility(View.VISIBLE);
        }
        return rowView;
    }
}
