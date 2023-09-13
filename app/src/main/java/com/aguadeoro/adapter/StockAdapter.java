package com.aguadeoro.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aguadeoro.R;
import com.aguadeoro.activity.StockActivity;
import com.aguadeoro.activity.ViewInventoryActivity;
import com.aguadeoro.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StockAdapter extends ArrayAdapter<Map<String, String>> {

    Context context;
    ArrayList<Map<String, String>> data, dataNotes, dataNotes2;
    String filename;
    File path;
    private boolean isCheckAll;
    private final boolean[] check;
    private final HashMap<String, String> textValues = new HashMap<String, String>();
    String[] keys;


    public StockAdapter(Context context, ArrayList<Map<String, String>> data, ArrayList<Map<String, String>> notes) {
        super(context, R.layout.line_stock, data);
        this.context = context;
        this.data = data;
        check = new boolean[data.size()];
        this.dataNotes = notes;
        keys = new String[dataNotes.size()];

        for (int i = 0; i < dataNotes.size(); i++) {
            for (String key : dataNotes.get(i).keySet()) {
                keys[i] = key;
            }
        }
        Arrays.sort(keys);
        Log.d("keys", Arrays.toString(keys));
        dataNotes2 = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            for (int j = 0; j < dataNotes.size(); j++) {
                for (String key : dataNotes.get(j).keySet()) {
                    if (key.equals(keys[i])) {
                        dataNotes2.add(dataNotes.get(j));
                    }
                }
            }
        }
    }

    public StockAdapter(StockActivity context, ArrayList<Map<String, String>> data, ArrayList<Map<String, String>> notes, boolean isCheckAll) {
        super(context, R.layout.line_stock, data);

        this.context = context;
        this.data = data;
        check = new boolean[data.size()];
        this.dataNotes = notes;
        keys = new String[dataNotes.size()];
        this.isCheckAll = isCheckAll;
        for (int i = 0; i < dataNotes.size(); i++) {
            for (String key : dataNotes.get(i).keySet()) {
                keys[i] = key;
            }
        }
        Arrays.sort(keys);
        Log.d("keys", Arrays.toString(keys));
        dataNotes2 = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            for (int j = 0; j < dataNotes.size(); j++) {
                for (String key : dataNotes.get(j).keySet()) {
                    if (key.equals(keys[i])) {
                        dataNotes2.add(dataNotes.get(j));
                    }
                }
            }
        }
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder vh;
        View rowView = convertView;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.line_stock, parent, false);
            vh = new ViewHolder();
            vh.notes = rowView.findViewById(R.id.Notes);
            rowView.setTag(vh);
        } else {
            vh = (ViewHolder) rowView.getTag();
        }
        ((TextView) rowView.findViewById(R.id.inventoryCode)).setText(data.get(position).get("InventoryCode"));
        ((TextView) rowView.findViewById(R.id.catalogueCode)).setText(data.get(position).get("CatalogCode"));
        ((TextView) rowView.findViewById(R.id.material)).setText(data.get(position).get("Material"));
        ((TextView) rowView.findViewById(R.id.color)).setText(data.get(position).get("Color"));
        ((TextView) rowView.findViewById(R.id.stone)).setText(data.get(position).get("Stone"));
        ((TextView) rowView.findViewById(R.id.price)).setText(data.get(position).get("Price") + "chf");
        ((TextView) rowView.findViewById(R.id.previousNote)).setText("Previous note : " + dataNotes2.get(position).get(keys[position]));
        ((TextView) rowView.findViewById(R.id.previous_status)).setText("Previous status : " + data.get(position).get("Action"));
        ((TextView) rowView.findViewById(R.id.itemNumber)).setText("" + (position + 1));
        filename = (data.get(position).get(Utils.IMAGE));
        CheckBox checkbox = rowView.findViewById(R.id.stockCheck);
        if (isCheckAll) {
            checkbox.setChecked(true);
            check[position] = checkbox.isChecked();
        }
        vh.notes.setTag("note" + position);
        vh.notes.setText(textValues.get("note" + position));
        if (!filename.isEmpty()) {
            final View finalRowView = rowView;

            path = new File(Environment.getExternalStorageDirectory()
                    + "/06_inventory/" + filename);
            ImageButton img = finalRowView.findViewById(R.id.picture);
            //Picasso.with(context).load(path).placeholder(R.drawable.logo_small).into(img);
            Picasso.with(context).load("http://195.15.223.234/aguadeoro/06_inventory toc opy/" + filename).placeholder(R.drawable.logo_small).into(img);
            //check all needed permissions together
            //img.setImageURI(Uri.fromFile(path));
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewInventoryActivity.class);
                    intent.putExtra(Utils.ID, data.get(position).get("ID"));
                    context.startActivity(intent);

                }
            });
        }
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                check[position] = isChecked;
            }
        });
        checkbox.setChecked(check[position]);
        vh.notes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                EditText am = view.findViewById(R.id.Notes);
                textValues.put("note" + position, am.getText().toString().trim());
            }
        });

        return rowView;
    }

    static class ViewHolder {
        EditText notes;
    }

    public String getNote(int position) {
        String result = textValues.get("note" + position);
        if (result == null)
            result = "";
        return result;
    }

    public ArrayList<Map<String, String>> getCheckedIDs() {
        ArrayList<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < check.length; i++) {
            data.get(i).put("note", getNote(i));
            if (!check[i]) {
                list.add(data.get(i));
            }
        }
        return list;
    }

}
