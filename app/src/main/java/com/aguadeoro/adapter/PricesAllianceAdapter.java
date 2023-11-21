package com.aguadeoro.adapter;

import android.content.Context;
import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aguadeoro.R;
import com.aguadeoro.activity.PricesAlliancesActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PricesAllianceAdapter extends RecyclerView.Adapter<PricesAllianceAdapter.ViewHolder> {

    Context context;
    ArrayList<Map<String, String>> data;
    LayoutInflater mInflater;
    String currentCode, wPrice, yPrice, rPrice, pPrice;
    private final ArrayList<Map<String, Object>> toOrderList = new ArrayList<>();

    public PricesAllianceAdapter(Context context, ArrayList<Map<String, String>> data) {
        this.context = context;
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
    }

    
    @Override
    public PricesAllianceAdapter.ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view;
        view = mInflater.inflate(R.layout.line_prices_alliance, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( final PricesAllianceAdapter.ViewHolder viewHolder, int i) {

        double ringPrice, meleePrice, stonePrice;

        viewHolder.catCode.setText(data.get(i).get("CatalogCode"));
        viewHolder.name.setText(data.get(i).get("Name"));

        if (data.get(i).get("RingPrize") == null || data.get(i).get("RingPrize").isEmpty()) {
            ringPrice = 0.0;
        } else {
            ringPrice = Double.parseDouble(data.get(i).get("RingPrize"));
        }
        if (data.get(i).get("MeleePrize") == null || data.get(i).get("MeleePrize").isEmpty()) {
            meleePrice = 0.0;
        } else {
            meleePrice = Double.parseDouble(data.get(i).get("MeleePrize"));
        }
        if (data.get(i).get("StonePrize") == null || data.get(i).get("StonePrize").isEmpty()) {
            stonePrice = 0.0;
        } else {
            stonePrice = Double.parseDouble(data.get(i).get("StonePrize"));
        }
        //currentMaterial = data.get(i).get("Material");

        wPrice = roundPrice(String.valueOf((int) ringPrice + meleePrice + stonePrice));
        yPrice = roundPrice(String.valueOf((int) ((0.95 * ringPrice) + meleePrice + stonePrice)));
        rPrice = roundPrice(String.valueOf((int) ((1.1 * ringPrice) + meleePrice + stonePrice)));
        pPrice = roundPrice(String.valueOf((int) ((1.35 * ringPrice) + meleePrice + stonePrice)));
        viewHolder.wPrice.setText(wPrice);
        viewHolder.pPrice.setText(pPrice);
        viewHolder.yPrice.setText(yPrice);
        viewHolder.rPrice.setText(rPrice);
        viewHolder.wPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCode = data.get(viewHolder.getAdapterPosition()).get("CatalogCode");
                addToList((TextView) view, viewHolder, viewHolder.getAdapterPosition(), "White");
            }
        });
        viewHolder.yPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCode = data.get(viewHolder.getAdapterPosition()).get("CatalogCode");
                addToList((TextView) view, viewHolder, viewHolder.getAdapterPosition(), "Yellow");
            }
        });
        viewHolder.rPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCode = data.get(viewHolder.getAdapterPosition()).get("CatalogCode");
                addToList((TextView) view, viewHolder, viewHolder.getAdapterPosition(), "Red");
            }
        });
        viewHolder.pPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCode = data.get(viewHolder.getAdapterPosition()).get("CatalogCode");
                addToList((TextView) view, viewHolder, viewHolder.getAdapterPosition(), "Platinum");
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView catCode;
        TextView wPrice;
        TextView yPrice;
        TextView rPrice;
        TextView pPrice;
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            this.catCode = itemView.findViewById(R.id.catCode);
            this.wPrice = itemView.findViewById(R.id.whitePrice);
            this.yPrice = itemView.findViewById(R.id.yellowPrice);
            this.rPrice = itemView.findViewById(R.id.redPrice);
            this.pPrice = itemView.findViewById(R.id.platPrice);
            this.name = itemView.findViewById(R.id.name);
        }
    }

    private void addToList(final TextView txtView, final PricesAllianceAdapter.ViewHolder holder, final int position, final String metal) {
        boolean isIn = false;
        if (!txtView.getText().equals("N/A")) {
            for (int i = toOrderList.size() - 1; i >= 0; i--) {
                Log.d("comparison", "" + txtView.getText() + " " + toOrderList.get(i).get("price"));
                if (toOrderList.get(i).get("price").equals(txtView.getText())) {
                    isIn = true;
                    toOrderList.remove(i);
                    txtView.setBackgroundColor(0x00000000);
                    txtView.setTextColor(Color.BLACK);
                    txtView.setSelected(false);
                }
            }
            if (!isIn) {
                txtView.setBackgroundColor(Color.GRAY);
                txtView.setTextColor(Color.WHITE);
                txtView.setSelected(true);
                toOrderList.add(new HashMap<String, Object>() {
                    {
                        put("metal", metal);
                        put("price", txtView.getText());
                        put("position", position);
                        put("catcode", currentCode);
                    }
                });
            }
        }
        Set<Map<String, Object>> set = new HashSet<>(toOrderList);
        toOrderList.clear();
        toOrderList.addAll(set);
        Log.d("to order list ", String.valueOf(toOrderList));
        ((PricesAlliancesActivity) context).getToOrder(toOrderList);
    }

    private String roundPrice(String price) {
        String usablePrice = Integer.toString((int) Double.parseDouble(price));
        Log.d("++++++", price);
        if (usablePrice.length() > 2) {
            int lastTwoDigits = Integer.parseInt(Integer.toString((int) Double.parseDouble(usablePrice)).substring(usablePrice.length() - 2, usablePrice.length()));
            String substring = Integer.toString((int) Double.parseDouble(usablePrice)).substring(0, usablePrice.length() - 2);
            Log.d("?????", "" + lastTwoDigits);
            if (lastTwoDigits > 0 && lastTwoDigits <= 30) {
                usablePrice = substring + "30";
            } else if (lastTwoDigits > 30 && lastTwoDigits <= 50) {
                usablePrice = substring + "50";

            } else if (lastTwoDigits > 50 && lastTwoDigits <= 70) {
                usablePrice = substring + "70";
            } else if (lastTwoDigits > 70 && lastTwoDigits <= 90) {
                usablePrice = substring + "90";
            }
        }
        Log.d("!!!!!!!", usablePrice);
        return usablePrice;
    }
}
