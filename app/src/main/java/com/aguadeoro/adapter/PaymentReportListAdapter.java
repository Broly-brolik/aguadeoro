package com.aguadeoro.adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.activity.OrderDetailActivity;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

public class PaymentReportListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> objects;
    private final boolean[] check;

    private Callable reloadPayments;
    private String previousPrice = "";
    private String previousDate = "";
    private String defaultDate = "Insert date";

    public PaymentReportListAdapter(Context context, List<String[]> objects, Callable reloadPayments) {
        super(context, R.layout.line_report_payment, objects);
        this.context = context;
        this.objects = objects;
        this.reloadPayments = reloadPayments;
        check = new boolean[objects.size()];
    }

    public ArrayList<String> getCheckedIDs() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < check.length; i++) {
            if (check[i])
                list.add(objects.get(i)[7]);
        }
        return list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.line_report_payment, parent,
                false);
        TextView orderNo = rowView.findViewById(R.id.order_number);
        TextView date = rowView.findViewById(R.id.date);
        TextView customer = rowView.findViewById(R.id.customer);
        TextView paymode = rowView.findViewById(R.id.paymode);
        TextView amt = rowView.findViewById(R.id.amt);
        TextView receivedAmount = rowView.findViewById(R.id.receivedAmount);
        TextView receivedDate = rowView.findViewById(R.id.receivedDate);
        TextView textViewCommission = rowView.findViewById(R.id.commission);

        TextView checkedBy = rowView.findViewById(R.id.checkedby);
        TextView checkedOn = rowView.findViewById(R.id.checkedon);
        CheckBox selected = rowView.findViewById(R.id.checkbox);
        orderNo.setText(objects.get(position)[9]);
        date.setText(Utils.shortDateFromDB(objects.get(position)[8]));
        customer.setText(Utils.shortDateFromDB(objects.get(position)[2]));
        paymode.setText(objects.get(position)[3]);
        amt.setText(objects.get(position)[4]);
        receivedAmount.setText(objects.get(position)[10]);
        if (objects.get(position)[10].equals("0")) {
            receivedAmount.setTextColor(Color.rgb(250, 0, 0));
        }
        receivedDate.setText(Utils.shortDateFromDB(objects.get(position)[11]));
        checkedBy.setText(objects.get(position)[5]);
        checkedOn.setText(Utils.shortDateFromDB(objects.get(position)[6]));

        BigDecimal amnt = new BigDecimal(objects.get(position)[4]);
        BigDecimal receivedAmnt = new BigDecimal(receivedAmount.getText().toString());
        BigDecimal commission =  BigDecimal.ZERO;
        if (amnt.compareTo(BigDecimal.ZERO)>0) {
            ((amnt.subtract(receivedAmnt)).divide(amnt, MathContext.DECIMAL32)).multiply(BigDecimal.valueOf(100)).
                    setScale(2, RoundingMode.HALF_UP);
        }
        //BigDecimal commission = ((amnt.subtract(receivedAmnt)).divide(amnt, MathContext.DECIMAL32)).multiply(BigDecimal.valueOf(100)).
         //       setScale(2, RoundingMode.HALF_UP);
        if (receivedAmnt.intValue() > 0) {
            textViewCommission.setText(" | "+ commission.toString() + "%");
            if (commission.compareTo(BigDecimal.valueOf(2.5)) == 1){
                textViewCommission.setTextColor(Color.RED);

            }
        }


        orderNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("OrderNumber", objects.get(position)[0]);
                context.startActivity(intent);
            }
        });
        if (!objects.get(position)[5].isEmpty()) {
            selected.setVisibility(View.GONE);
        } else {
            selected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    check[position] = isChecked;
                }
            });
            selected.setChecked(check[position]);
        }


        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);

                View dialog = inflater.inflate(R.layout.dialog_update_received_payment, null);
                EditText amount = dialog.findViewById(R.id.editTextReceivedAmount);
                TextView receivedDateDialog = dialog.findViewById(R.id.textViewReceivedDate);
                final Calendar myCalendar = Calendar.getInstance();


                if (receivedDate.getText().toString().isEmpty()) {
                    receivedDateDialog.setText(defaultDate);
                } else {
                    previousDate = receivedDate.getText().toString();
                    receivedDateDialog.setText(previousDate);
                }

                if (!receivedAmount.getText().toString().isEmpty()) {
                    previousPrice = receivedAmount.getText().toString();
                    amount.setText(previousPrice);
                }

                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        receivedDateDialog.setText(Utils.shortDateForInsert(myCalendar.getTime()));
                    }
                };

                receivedDateDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(context, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                builder.setView(dialog);
                builder.setCancelable(false);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        if (amount.getText().toString().isEmpty()) {
//                            Toast.makeText(context, "Enter valid amount", Toast.LENGTH_SHORT).show();
//                        } else {
//                            dialog.dismiss();
//                        }

                    }

                });

                builder.setNegativeButton(context.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();


                alert.show();
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (amount.getText().toString().isEmpty() || receivedDateDialog.getText().toString().equals(defaultDate)) {
                            Toast.makeText(context, "Enter valid amount and valid date  ", Toast.LENGTH_SHORT).show();
                        } else {

                            if (previousDate.equals(receivedDateDialog.getText().toString()) && previousPrice.equals(amount.getText().toString())) {
                                Toast.makeText(context, "No modification done", Toast.LENGTH_SHORT).show();
                                alert.dismiss();
                                return;
                            }
                            BigDecimal amnt = new BigDecimal(objects.get(position)[4]);
                            BigDecimal receivedAmount = new BigDecimal(amount.getText().toString());
                            String commission = (amnt.subtract(receivedAmount)).toString();
                            Log.e("amount mec", objects.get(position)[4]
                            );

                            updateOrderHistory(amount.getText().toString(), receivedDateDialog.getText().toString(), objects.get(position)[7], commission);
                            alert.dismiss();
                        }
                    }
                });

                return false;
            }
        });

        return rowView;
    }


    private void updateOrderHistory(String amount, String date, String id, String commission) {

        Query query = new Query(String.format("UPDATE OrderHistory set ReceivedAmount = %s, ReceivedDate = #%s#, Commission= %s WHERE ID = %s", amount, date, commission, id));
        boolean s = query.execute();
        Log.e("query s", String.valueOf(s));
        if (!s) {
            Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            reloadPayments.call();
            Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

}
