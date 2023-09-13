package com.aguadeoro.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.aguadeoro.R;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.Map;

public class DialogChoiceSupplier extends Dialog implements android.view.View.OnClickListener {

    public Activity c, owner;
    public Dialog d;
    public Button btnStatus, btnFilter, btnApproval;
    private String b1, b2;
    private final Map<String, String> objects;
    private CheckBox st1, st3, name;

    public DialogChoiceSupplier(Activity a, Map<String, String> object, Context context) {
        super(a);
        // TODO Auto-generated constructor stub
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
        this.c = a;
        this.objects = object;
        this.owner = getOwnerActivity();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_choice_supplier);
        btnStatus = findViewById(R.id.btn_change_status);
        btnStatus.setOnClickListener(this);
        btnFilter = findViewById(R.id.btn_filter_name);
        btnFilter.setOnClickListener(this);
        btnApproval = findViewById(R.id.approval_btn);
        btnApproval.setOnClickListener(this);
        st1 = findViewById(R.id.st12Check);
        st3 = findViewById(R.id.st3Check);
        String[] items = Utils.getSetSetting(Utils.SUPP_ORD_STT);
        final Spinner status = findViewById(R.id.status);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);
        final Spinner approval = findViewById(R.id.approval);
        items = Utils.getSetSetting("ApprovalStatus");
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, items);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        approval.setAdapter(adapter2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change_status:
                Spinner spin = findViewById(R.id.status);
                String value = spin.getSelectedItem().toString();
                Log.d("---", "Status : " + objects.get("Status") + " OrdSupNo : " + objects.get("SupplierOrderNumber") + " Selected Status : " + value);
                String query = "UPDATE SupplierOrderMain SET Status =" + "'" + value + "'" + " WHERE (SupplierOrderNumber=" + "'" + objects.get("SupplierOrderNumber") + "'" + ");";
                Query update = new Query(query);
                update.execute();
                dismiss();
                break;
            case R.id.btn_filter_name:
                Log.d("---", "name : " + objects.get("Recipient"));
                if (owner != null) {
                    int i = 0;
                    if (st1.isChecked()) {
                        i = 1;
                    } else if (st3.isChecked()) {
                        i = 2;
                    } else if (st1.isChecked() && st3.isChecked()) {
                        i = 3;
                    }
                    ((SupplierOrderActivity) (DialogChoiceSupplier.this.getOwnerActivity())).filterName(objects.get("Recipient"), i);
                } else {
                    Log.d("owner", "no owner");
                }
                dismiss();
                break;
            case R.id.approval_btn:
                Spinner spin2 = findViewById(R.id.approval);
                String value2 = spin2.getSelectedItem().toString();
                String query2 = "UPDATE SupplierOrderMain SET Approval =" + "'" + value2 + "'" + " WHERE (SupplierOrderNumber=" + "'" + objects.get("SupplierOrderNumber") + "'" + ");";
                Query update2 = new Query(query2);
                update2.execute();
            default:
                break;
        }
        dismiss();
    }
}
