package com.aguadeoro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aguadeoro.R;

import java.util.List;

public class ComponentActivityListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;
    private boolean[] check;


    public ComponentActivityListAdapter(Context context, List<String[]> objects) {
        super(context, R.layout.line_component_activity, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_component_activity, parent, false);
        //Loc Request 1 2018.04.17 BEGIN
        ((TextView) rowView.findViewById(R.id.date)).setText(objects.get(position)[2]);
        ((TextView) rowView.findViewById(R.id.invoice)).setText(objects.get(position)[3]);
        ((TextView) rowView.findViewById(R.id.invoiceDate)).setText(objects.get(position)[4]);
        if (!"".equals(objects.get(position)[5]))
            ((TextView) rowView.findViewById(R.id.detail)).setText(objects.get(position)[5]);
        else
            rowView.findViewById(R.id.detail).setVisibility(View.GONE);
        ((TextView) rowView.findViewById(R.id.quantity)).setText(objects.get(position)[6]);
        ((TextView) rowView.findViewById(R.id.recipient)).setText(objects.get(position)[7]);
        ((TextView) rowView.findViewById(R.id.transport)).setText(objects.get(position)[8]);
        ((TextView) rowView.findViewById(R.id.vat)).setText(objects.get(position)[9]);
        ((TextView) rowView.findViewById(R.id.amount)).setText(objects.get(position)[10]);
        ((TextView) rowView.findViewById(R.id.paymentDeadline)).setText(objects.get(position)[11]);
        ((TextView) rowView.findViewById(R.id.paymentDate)).setText(objects.get(position)[12]);
        ((TextView) rowView.findViewById(R.id.paymentBy)).setText(objects.get(position)[13]);
        if (!"".equals(objects.get(position)[14]))
            ((TextView) rowView.findViewById(R.id.remark)).setText(objects.get(position)[14]);
        else
            rowView.findViewById(R.id.remark).setVisibility(View.GONE);
        ((TextView) rowView.findViewById(R.id.suppOrdNo)).setText(objects.get(position)[15]);
        //Loc Request 1 2018.04.17 END
        return rowView;
    }

}
