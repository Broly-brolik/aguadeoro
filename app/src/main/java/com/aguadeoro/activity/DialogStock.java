package com.aguadeoro.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aguadeoro.R;

import java.util.ArrayList;
import java.util.Map;

public class DialogStock extends Dialog {

    Context context;
    Activity activity;
    ArrayList<Map<Map<String, String>, String>> items;

    public DialogStock(Context context, Activity activity, ArrayList<Map<Map<String, String>, String>> items) {
        super(activity);
        setContentView(R.layout.dialog_stock);
        this.context = context;
        this.activity = activity;
        this.items = items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mContainer = inflater.inflate(R.layout.dialog_stock, null);
        ListView activeList = mContainer.findViewById(R.id.list);
        ArrayAdapter<Map<Map<String, String>, String>> arrayAdapter = new ArrayAdapter<>(context, R.layout.line_dialog_stock, items);
        activeList.setAdapter(arrayAdapter);
    }


}
