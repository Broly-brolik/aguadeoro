package com.aguadeoro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.aguadeoro.R;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SellerInventoryListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;
    boolean showCheckbox;
    private final boolean[] check;

    public SellerInventoryListAdapter(Context context, List<String[]> objects, boolean showCheckbox) {
        super(context, R.layout.line_order, objects);
        this.context = context;
        this.objects = objects;
        this.showCheckbox = showCheckbox;
        check = new boolean[objects.size()];
    }

    public ArrayList<String> getCheckedAssignIDs() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < check.length; i++) {
            if (check[i])
                list.add(objects.get(i)[0]);
        }
        return list;
    }

    public ArrayList<String> getCheckedInventoryIDs() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < check.length; i++) {
            if (check[i])
                list.add(objects.get(i)[9]);
        }
        return list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_seller_inventory, parent, false);
        TextView artNo = rowView.findViewById(R.id.artno);
        TextView mark = rowView.findViewById(R.id.mark);
        TextView cat = rowView.findViewById(R.id.category);
        TextView descp = rowView.findViewById(R.id.width);
        TextView price = rowView.findViewById(R.id.price);
        TextView quantity = rowView.findViewById(R.id.quantity);
        TextView date = rowView.findViewById(R.id.date);
        TextView status = rowView.findViewById(R.id.status);
        CheckBox checkbox = rowView.findViewById(R.id.checked);
        artNo.setText(objects.get(position)[1]);
        mark.setText(objects.get(position)[2]);
        cat.setText(objects.get(position)[3]);
        descp.setText(objects.get(position)[4]);
        price.setText(objects.get(position)[5]);
        quantity.setText(objects.get(position)[6]);
        date.setText(Utils.shortDateFromDB(objects.get(position)[7]));
        status.setText(objects.get(position)[8]);
        artNo.setTag(objects.get(position)[0]);
        if (showCheckbox) {
            checkbox.setVisibility(View.VISIBLE);
            checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    check[position] = isChecked;
                }
            });
            checkbox.setChecked(check[position]);

        }
        return rowView;
    }

}
