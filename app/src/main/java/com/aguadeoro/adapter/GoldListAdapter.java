package com.aguadeoro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.aguadeoro.R;

import java.util.ArrayList;
import java.util.List;

public class GoldListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;
    private final boolean[] check;

    public GoldListAdapter(Context context, List<String[]> objects) {
        super(context, R.layout.line_gold, objects);
        this.context = context;
        this.objects = objects;
        check = new boolean[objects.size()];
    }

    public ArrayList<String> getCheckedIDs() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < check.length; i++) {
            if (check[i])
                list.add(objects.get(i)[0]);
        }
        return list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_gold, parent, false);
        ((TextView) rowView.findViewById(R.id.date)).setText(objects.get(position)[2]);
        ((TextView) rowView.findViewById(R.id.unites)).setText(objects.get(position)[3]);
        ((TextView) rowView.findViewById(R.id.detail)).setText(objects.get(position)[4]);
        ((TextView) rowView.findViewById(R.id.grs_livres)).setText(objects.get(position)[5]);
        ((TextView) rowView.findViewById(R.id.grs_rendus)).setText(objects.get(position)[6]);
        ((TextView) rowView.findViewById(R.id.prix)).setText(objects.get(position)[7]);
        ((TextView) rowView.findViewById(R.id.signature_liv)).setText(objects.get(position)[8]);
        ((TextView) rowView.findViewById(R.id.date_liv)).setText(objects.get(position)[9]);
        ((TextView) rowView.findViewById(R.id.paye)).setText(objects.get(position)[10]);
        ((TextView) rowView.findViewById(R.id.signature)).setText(objects.get(position)[11]);
        ((TextView) rowView.findViewById(R.id.suppOrdNo)).setText(objects.get(position)[12]);
        ((TextView) rowView.findViewById(R.id.comp_instruction)).setText(objects.get(position)[13]);
        CheckBox checkbox = rowView.findViewById(R.id.selected);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                check[position] = isChecked;
            }
        });
        checkbox.setChecked(check[position]);

        return rowView;
    }

}
