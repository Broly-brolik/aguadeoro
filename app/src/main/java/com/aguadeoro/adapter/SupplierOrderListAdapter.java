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
import com.aguadeoro.utils.Constants;
import com.aguadeoro.utils.Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SupplierOrderListAdapter extends ArrayAdapter<Map<String, String>> {

    private final Context context;
    private final ArrayList<Map<String, String>> objects;
    private int pos2;
    private final List<String> idList = new ArrayList<>();
    boolean showCheckbox;

    private List<String> selectedOrders;

    public SupplierOrderListAdapter(Context context, ArrayList<Map<String, String>> objects, boolean showCheckbox, List<String> selectedOrders) {
        super(context, R.layout.line_supplier_order, objects);
        this.context = context;
        this.objects = objects;
        this.showCheckbox = showCheckbox;
        this.selectedOrders = selectedOrders;
//        check = new boolean[objects.size()];
//        for (int i = 0; i < objects.size(); i++) {
//            if (objects.get(i)[0] != null) {
//                idList.add(objects.get(i)[0]);
//            }
//        }
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        int pos = Integer.parseInt(objects.get(position)[8].substring(objects.get(position)[8].length() - 1));
        String[] items = new String[]{"Status 1", "Status 2", "Status 3"};
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_supplier_order, parent, false);
        //TextView orderNo = (TextView) rowView.findViewById(R.id.order_number);
        TextView orderDeadline = rowView.findViewById(R.id.order_deadline);
        TextView artNo = rowView.findViewById(R.id.art_no);
        TextView createDate = rowView.findViewById(R.id.created_date);
        TextView deadline = rowView.findViewById(R.id.deadline);
        TextView recipient = rowView.findViewById(R.id.recipient);
        TextView instruction = rowView.findViewById(R.id.instruction);

        //TextView step = (TextView) rowView.findViewById(R.id.step);
        TextView status = rowView.findViewById(R.id.status);
        TextView supOrdNo = rowView.findViewById(R.id.sup_ord_no);
//        deadline.setText(Utils.shortDateFromDB(objects.get(position)[6]));
        supOrdNo.setText(objects.get(position).get("SupplierOrderNumber"));
        deadline.setText(Utils.shortDateFromDB(objects.get(position).get("Deadline")));
        status.setText(objects.get(position).get("Status"));
        createDate.setText(Utils.shortDateFromDB(objects.get(position).get("CreatedDate")));
        recipient.setText(objects.get(position).get("Recipient"));
        instruction.setText(objects.get(position).get("Instruction"));
        artNo.setText(objects.get(position).get("ArticleNumber"));
        String orderDeadlineStr = objects.get(position).get("OrderDeadline");
        orderDeadlineStr = LocalDateTime.parse(orderDeadlineStr, Constants.fromAccessFormatter).format(Constants.DateFormatter);

        orderDeadline.setText(orderDeadlineStr);


        CheckBox selectInvoice = rowView.findViewById(R.id.checkBoxInvoice);
        if (selectedOrders.contains(objects.get(position).get("SupplierOrderNumber"))) {
            selectInvoice.setChecked(true);
        }
        selectInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    System.out.println("Checked");
                    selectedOrders.add(objects.get(position).get("SupplierOrderNumber"));
                } else {
                    System.out.println("Un-Checked");
                    selectedOrders.remove(objects.get(position).get("SupplierOrderNumber"));
                }
            }
        });

        /*recipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("test","ok");
            }
        });*/
        //step.setText(objects.get(position)[10]);
        //status.setAdapter(adapter);
        pos2 = position;
        //status.post(new Runnable() {
            /*public void run() {
                status.setOnItemSelectedListener(SupplierOrderListAdapter.this);
            }
        });
        status.setSelection(pos-1);*/
        return rowView;
    }

}

