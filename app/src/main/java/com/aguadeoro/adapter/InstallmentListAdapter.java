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

public class InstallmentListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;

    public InstallmentListAdapter(Context context, List<String[]> objects) {
        super(context, R.layout.line_view_installment, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_view_installment, parent, false);
        TextView deadline = rowView.findViewById(R.id.deadline);
        TextView amount = rowView.findViewById(R.id.amount);
        CheckBox paid = rowView.findViewById(R.id.paid);
        deadline.setText(Utils.shortDateFromDB(objects.get(position)[1]));
        amount.setText(objects.get(position)[2]);
        paid.setChecked(objects.get(position)[3].equals("1"));
        if (paid.isChecked()) paid.setEnabled(false);
        paid.setTag(objects.get(position)[0]);
        return rowView;
    }

}
