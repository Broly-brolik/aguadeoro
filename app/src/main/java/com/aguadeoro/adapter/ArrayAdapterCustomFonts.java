package com.aguadeoro.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aguadeoro.utils.Utils;

public class ArrayAdapterCustomFonts extends ArrayAdapter<String> {

    static Typeface[] fonts;
    String text;
    private int defaultPosition;

    public ArrayAdapterCustomFonts(Context context, String text) {
        super(context, 0, new String[Utils.FONTS.length]);
        fonts = new Typeface[Utils.FONTS.length];
        for (int i = 0; i < Utils.FONTS.length; i++) {

            fonts[i] = Typeface.createFromAsset(getContext().getAssets(), Utils.FONTS[i]);
            Log.d("fontPath", Utils.FONTS[i] + " " + fonts[i]);

        }
        this.text = text;
    }

    public int getDefaultPosition() {
        return defaultPosition;
    }

    public void setText(String text) {
        this.text = text;
    }
//        public CustomFontsArrayAdapter(Context context, String[] objects) {
//            super(context, 0, objects);
//        }

    public void setDefaultPostion(int position) {
        this.defaultPosition = position;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomDropdownView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomDropdownView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = LayoutInflater.from(getContext()).inflate(
                android.R.layout.simple_spinner_item, parent, false);
        TextView label = row.findViewById(android.R.id.text1);
        label.setTypeface(fonts[position]);
        label.setText(Utils.FONTS[position]);

        return row;
    }

    public View getCustomDropdownView(int position, View convertView,
                                      ViewGroup parent) {

        View row = LayoutInflater.from(getContext()).inflate(
                android.R.layout.simple_spinner_item, parent, false);
        TextView label = row.findViewById(android.R.id.text1);
        label.setTypeface(fonts[position]);
        label.setTextSize(25);


        if (position == 3 || position == 6) {
            label.setTextSize(35);
        }
        label.setText(text);

        return row;
    }
}