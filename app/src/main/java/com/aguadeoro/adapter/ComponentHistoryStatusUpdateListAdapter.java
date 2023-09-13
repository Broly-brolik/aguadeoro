package com.aguadeoro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.aguadeoro.R;
import com.aguadeoro.utils.Utils;

import java.util.List;

public class ComponentHistoryStatusUpdateListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;

    public ComponentHistoryStatusUpdateListAdapter(Context context, List<String[]> objects) {
        super(context, R.layout.line_component_history, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_component_history_status_update, parent, false);
        ((TextView) rowView.findViewById(R.id.comp_instruction)).setText(objects.get(position)[4]);
        rowView.findViewById(R.id.comp_status).setTag(objects.get(position)[0]);//the tag is the compHistID
        ((Spinner) rowView.findViewById(R.id.comp_status)).setAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1,
                Utils.getSetSetting(Utils.SUPP_ORD_STT)));
        ((Spinner) rowView.findViewById(R.id.comp_status)).setSelection(
                Utils.getSelectedIndex(objects.get(position)[3], Utils.getSetSetting(Utils.SUPP_ORD_STT)));

        return rowView;
    }
}
