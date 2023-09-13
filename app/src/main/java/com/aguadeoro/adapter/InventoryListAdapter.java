package com.aguadeoro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.aguadeoro.R;
import com.aguadeoro.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class InventoryListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;
    boolean showCheckbox, isAltView;
    private final boolean[] check;

    public InventoryListAdapter(Context context, List<String[]> objects, boolean showCheckbox, boolean altView) {
        super(context, R.layout.line_order, objects);
        this.context = context;
        this.objects = objects;
        this.showCheckbox = showCheckbox;
        this.isAltView = altView;
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
        View rowView;
        if (isAltView) {
            rowView = inflater.inflate(R.layout.line_inventory_alt, parent, false);
            String filename = objects.get(position)[9];
            if (filename != null && !filename.isEmpty()) {
                ImageView img = rowView.findViewById(R.id.picture);
                Picasso.with(context).load("http://195.15.223.234/aguadeoro/06_inventory toc opy/" + filename).placeholder(R.drawable.logo_small).into(img);
            }
            TextView color = rowView.findViewById(R.id.color);
            TextView material = rowView.findViewById(R.id.material);
            TextView invCode = rowView.findViewById(R.id.inventoryCode);
            TextView stone = rowView.findViewById(R.id.stone);
            TextView catCode = rowView.findViewById(R.id.catalogueCode);
            TextView price = rowView.findViewById(R.id.price);
            TextView carat = rowView.findViewById(R.id.carat);

            invCode.setText(objects.get(position)[1]);
            color.setText(objects.get(position)[10]);
            material.setText(objects.get(position)[11]);
            stone.setText(objects.get(position)[12]);
            catCode.setText(objects.get(position)[13]);
            price.setText(objects.get(position)[14] + "CHF");
            carat.setText(objects.get(position)[15]);

        } else {
            rowView = inflater.inflate(R.layout.line_inventory, parent, false);
            TextView artNo = rowView.findViewById(R.id.artno);
            TextView mark = rowView.findViewById(R.id.mark);
            TextView cat = rowView.findViewById(R.id.category);
            TextView descp = rowView.findViewById(R.id.width);
            //TextView price = (TextView) rowView.findViewById(R.id.price);
            TextView location = rowView.findViewById(R.id.location);
            TextView quantity = rowView.findViewById(R.id.quantity);
            TextView status = rowView.findViewById(R.id.status);
            TextView date = rowView.findViewById(R.id.date);
            CheckBox checkbox = rowView.findViewById(R.id.checked);
            artNo.setText(objects.get(position)[1]);
            mark.setText(objects.get(position)[2]);
            cat.setText(objects.get(position)[3]);
            descp.setText(objects.get(position)[4]);
            //price.setText(objects.get(position)[5]);
            location.setText(objects.get(position)[5]);
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
        }
        return rowView;
    }

}
