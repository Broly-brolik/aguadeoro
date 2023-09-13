package com.aguadeoro.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aguadeoro.R;
import com.aguadeoro.activity.PricesActivity;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PricesListAdapter extends BaseAdapter {
    private final Context context;
    private final double melee;
    private double ringPrize, metalMultiplier;
    private double msPrice = 0.0;
    Map<String, ArrayList<String>> data;
    private final String[] mKeys;
    private final double[] intKeys;
    private int twins = 1;
    private final boolean warning = false;
    private final boolean colored;
    private boolean firstIsDiamond;
    private final boolean isMine;
    private final String metal;
    private String quality;
    private String color;
    private String cColor;
    private final ArrayList<Map<String, Object>> toOrderList = new ArrayList<>();
    private ArrayList<Double> MSColored, MSDiamonds;
    private final Map<String, String> limits;
    private final Map<String, String> ringPriceLimits;
    private ArrayList<String> limitsArray;

    public PricesListAdapter(Context context, Map<String, ArrayList<String>> res, double melee, String metal, double ringPrize, boolean colored, boolean isMine, Map<String, String> limits, Map<String, String> ringPriceLimits) {
        this.context = context;
        this.data = res;
        this.melee = melee;
        this.metal = metal;
        this.ringPrize = ringPrize;
        this.colored = colored;
        this.isMine = isMine;
        this.limits = limits;
        this.ringPriceLimits = ringPriceLimits;
        for (Map.Entry entry : data.entrySet()) {
            Log.d("data", "key: " + entry.getKey() + "; value: " + entry.getValue());
        }
        mKeys = data.keySet().toArray(new String[data.size()]);
        intKeys = new double[mKeys.length];
        for (int i = 0; i < mKeys.length; i++) {
            intKeys[i] = Double.parseDouble(mKeys[i]);
        }
        Arrays.sort(intKeys);
        for (double intKey : intKeys) {
            Log.d("keys", "" + intKey);
        }
    }

    public PricesListAdapter(Context context, Map<String, ArrayList<String>> res, double melee, String metal, double ringPrize, boolean colored, ArrayList<Double> msDiamonds, ArrayList<Double> msColored, boolean firstIsDiamond, String quality, String color1, String cColor, boolean isMine, Map<String, String> limits, Map<String, String> ringPriceLimits) {
        this.context = context;
        this.data = res;
        this.melee = melee;
        this.metal = metal;
        this.ringPrize = ringPrize;
        this.colored = colored;
        this.MSColored = msColored;
        this.MSDiamonds = msDiamonds;
        this.firstIsDiamond = firstIsDiamond;
        this.quality = quality;
        this.color = color1;
        this.cColor = cColor;
        this.isMine = isMine;
        this.limits = limits;
        this.ringPriceLimits = ringPriceLimits;
        switch (color1) {
            case "E,F":
                color = "E";
                break;
            case "G,H":
                color = "G";
                break;
            case "I,J":
                color = "I";
                break;
            case "K,L,M":
                color = "K";
                break;
        }
        for (Map.Entry entry : data.entrySet()) {
            Log.d("data", "key: " + entry.getKey() + "; value: " + entry.getValue());
        }
        mKeys = data.keySet().toArray(new String[data.size()]);
        intKeys = new double[mKeys.length];
        for (int i = 0; i < mKeys.length; i++) {
            intKeys[i] = Double.parseDouble(mKeys[i]);
        }
        Arrays.sort(intKeys);
        for (double intKey : intKeys) {
            Log.d("keys", "" + intKey);
        }
        getPrices();
    }


    Boolean checkSame(ArrayList<Double> a) {
        for (int i = 1; i < a.size(); i++) {
            if (!a.get(0).equals(a.get(i))) {
                return false;
            }
        }
        return true;
    }

    Boolean getPrices() {
        String query;
        Query q;
        boolean s;
        if (firstIsDiamond) {
            if (MSColored.size() == 0) {
                Log.d("isSame", "" + checkSame(MSDiamonds));
                if (checkSame(MSDiamonds)) {
                    twins = MSDiamonds.size();
                    return true;
                }
                for (int i = 1; i < MSDiamonds.size(); i++) {
                    if (isMine) {
                        query = "select top 4 * from LV where Color =" + Utils.escape(color) + " order by ABS(Carat-" + MSDiamonds.get(i) + ")";
                    } else {
                        query = "select top 3 * from LV where Color =" + Utils.escape(color) + " order by ABS(Carat-" + MSDiamonds.get(i) + ")";
                    }
                    q = new Query(query);
                    s = q.execute();
                    if (!s) {
                        return false;
                    }
                    ArrayList<Map<String, String>> res = q.getRes();
                    if (isMine) {
                        switch (quality) {
                            case "VVS":
                                msPrice += Double.parseDouble(res.get(3).get("" + 3));
                                break;
                            case "VS":
                                msPrice += Double.parseDouble(res.get(1).get("" + 3));
                                break;
                            case "SI":
                                msPrice += Double.parseDouble(res.get(2).get("" + 3));
                                break;
                            case "IF":
                                msPrice += Double.parseDouble(res.get(0).get("" + 3));
                        }
                    } else {
                        switch (quality) {
                            case "VVS":
                                msPrice += Double.parseDouble(res.get(2).get("" + 3));
                                break;
                            case "VS":
                                msPrice += Double.parseDouble(res.get(0).get("" + 3));
                                break;
                            case "SI":
                                msPrice += Double.parseDouble(res.get(1).get("" + 3));
                                break;
                        }
                    }

                }

            } else if (MSDiamonds.size() == 1) {
                for (int i = 0; i < MSColored.size(); i++) {
                    query = "select * from LVColored where Cat=" + Utils.escape(cColor) + " order by ABS(Carat-" + MSColored.get(i) + ")";
                    q = new Query(query);
                    s = q.execute();
                    if (!s) {
                        return false;
                    }
                    ArrayList<Map<String, String>> res = q.getRes();
                    msPrice += Double.parseDouble(res.get(0).get("" + 3));
                }
            }
        } else {
            if (MSDiamonds.size() == 0) {
                if (checkSame(MSColored)) {
                    twins = MSColored.size();
                    Log.d("twins", "" + twins);
                    return true;
                }
                for (int i = 1; i < MSColored.size(); i++) {
                    query = "select * from LVColored where Cat=" + Utils.escape(cColor) + " order by ABS(Carat-" + MSColored.get(i) + ")";
                    Query q2 = new Query(query);
                    s = q2.execute();
                    if (!s) {
                        return false;
                    }
                    ArrayList<Map<String, String>> res = q2.getRes();
                    msPrice += Double.parseDouble(res.get(0).get("" + 3));
                }
            } else if (MSColored.size() == 1) {
                for (int i = 0; i < MSDiamonds.size(); i++) {
                    query = "select top 3 * from LV where Color =" + Utils.escape(color) + " order by ABS(Carat-" + MSDiamonds.get(i) + ")";
                    q = new Query(query);
                    s = q.execute();
                    if (!s) {
                        return false;
                    }
                    ArrayList<Map<String, String>> res = q.getRes();
                    switch (quality) {
                        case "VVS":
                            msPrice += Double.parseDouble(res.get(2).get("" + 3));
                            break;
                        case "VS":
                            msPrice += Double.parseDouble(res.get(0).get("" + 3));
                            break;
                        case "SI":
                            msPrice += Double.parseDouble(res.get(1).get("" + 3));

                    }
                }
            }
        }
        return true;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(mKeys[i]);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Holder holder;
        final HolderColored holder2;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        metalMultiplier = 1;
        Log.d("metal", "" + metal);
        limitsArray = new ArrayList<>();
        for (String s : limits.values()) {
            limitsArray.add(s);
        }
        double answer = Double.parseDouble(limitsArray.get(0));
        double current = Double.MAX_VALUE;
        for (String valueString : limitsArray) {
            double value = Double.parseDouble(valueString);
            if (Math.abs(value - intKeys[position]) < current) {
                answer = value;
                current = Math.abs(value - intKeys[position]);
            }
        }
        String closestTo = String.valueOf(answer);
        String closestToCode = "";
        for (Map.Entry<String, String> entry : limits.entrySet()) {
            if (entry.getValue().equals(closestTo) || entry.getValue().equals(closestTo + "0")) {
                closestToCode = entry.getKey();
            }
        }
        if (!closestToCode.equals("") && !ringPriceLimits.get(closestToCode).equals("")) {
            ringPrize = Double.parseDouble(Objects.requireNonNull(ringPriceLimits.get(closestToCode)));
            Log.d("new ringprice", "" + ringPrize);
        }
        switch (metal) {
            case "Yellow":
                metalMultiplier = 0.95;
                break;
            case "Red":
                metalMultiplier = 1.1;
                break;
            case "Platinum":
                metalMultiplier = 1.35;
                break;
            case "White":
                metalMultiplier = 1;
                break;
        }
        if (colored) {
            convertView = inflater.inflate(R.layout.line_prices_colored, parent, false);

            if (convertView == null || convertView.getTag() == null) {
                holder2 = new HolderColored();
                holder2.range = convertView.findViewById(R.id.rangeColored);
                holder2.a = convertView.findViewById(R.id.a);
                holder2.b = convertView.findViewById(R.id.b);
                holder2.c = convertView.findViewById(R.id.c);
                holder2.d = convertView.findViewById(R.id.d);
                holder2.e = convertView.findViewById(R.id.e);
                holder2.f = convertView.findViewById(R.id.f);
                convertView.setTag(holder2);
            } else {
                holder2 = (HolderColored) convertView.getTag();
            }
            holder2.a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToListColored(holder2.a, holder2, position);
                }
            });
            holder2.b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToListColored(holder2.b, holder2, position);
                }
            });
            holder2.c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToListColored(holder2.c, holder2, position);
                }
            });
            holder2.d.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToListColored(holder2.d, holder2, position);
                }
            });
            holder2.e.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToListColored(holder2.e, holder2, position);
                }
            });
            holder2.f.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToListColored(holder2.f, holder2, position);
                }
            });
            Log.d("holder", "" + holder2.range);
            DecimalFormat df = new DecimalFormat();
            DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance();
            sym.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(sym);
            df.setMaximumFractionDigits(2);
            double minRange = (intKeys[position] - 0.05);
            minRange = Double.parseDouble(df.format(minRange));
            holder2.range.setText("" + minRange + "-" + intKeys[position]);

            if (!data.get(String.valueOf(intKeys[position])).get(1).equals("")) {
                holder2.a.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(1)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(3).equals("")) {
                holder2.b.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(3)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(5).equals("")) {
                holder2.c.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(5)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(7).equals("")) {
                holder2.d.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(7)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(9).equals("")) {
                holder2.e.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(9)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(11).equals("")) {
                holder2.f.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(11)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }

        } else {
            convertView = inflater.inflate(R.layout.line_prices, parent, false);
            if (convertView == null || convertView.getTag() == null) {
                holder = new Holder();
                holder.range = convertView.findViewById(R.id.range);
                holder.d0 = convertView.findViewById(R.id.d0);
                holder.d1 = convertView.findViewById(R.id.d1);
                holder.d2 = convertView.findViewById(R.id.d2);
                holder.d3 = convertView.findViewById(R.id.d3);
                holder.e0 = convertView.findViewById(R.id.e0);
                holder.e1 = convertView.findViewById(R.id.e1);
                holder.e2 = convertView.findViewById(R.id.e2);
                holder.e3 = convertView.findViewById(R.id.e3);
                holder.g0 = convertView.findViewById(R.id.g0);
                holder.g1 = convertView.findViewById(R.id.g1);
                holder.g2 = convertView.findViewById(R.id.g2);
                holder.g3 = convertView.findViewById(R.id.g3);
                holder.i0 = convertView.findViewById(R.id.i0);
                holder.i1 = convertView.findViewById(R.id.i1);
                holder.i2 = convertView.findViewById(R.id.i2);
                holder.i3 = convertView.findViewById(R.id.i3);
                holder.k0 = convertView.findViewById(R.id.k0);
                holder.k1 = convertView.findViewById(R.id.k1);
                holder.k2 = convertView.findViewById(R.id.k2);
                holder.k3 = convertView.findViewById(R.id.k3);
                holder.warning = convertView.findViewById(R.id.warning);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.d0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.d1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.d2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.d3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.e0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.e1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.e2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.e3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.g0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.g1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.g2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.g3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.i0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.i1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.i2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.i3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.k0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.k1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.k2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);

                }
            });
            holder.k3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToList((TextView) view, holder, position);
                }
            });

            DecimalFormat df = new DecimalFormat();
            DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance();
            sym.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(sym);
            df.setMaximumFractionDigits(2);
            double minRange = (intKeys[position] - 0.04);
            minRange = Double.parseDouble(df.format(minRange));
            holder.range.setText("" + minRange + "-" + intKeys[position]);
            if (warning) {
                holder.warning.setText(R.string.warningPrice);
            }
        /*for (int i=0;i<data.get(String.valueOf(intKeys[position])).size();i++){
            Log.d("here",""+position+" "+i+" "+data.get(String.valueOf(intKeys[position])).get(i));
        }*/
            int iterator = 0;
            if (isMine) {
                iterator = 2;
            }
            if (isMine) {
                if (!data.get(String.valueOf(intKeys[position])).get(1).equals("")) {
                    holder.d0.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(1)) * twins + melee + ringPrize * metalMultiplier + msPrice)));
                }
                if (!data.get(String.valueOf(intKeys[position])).get(9).equals("")) {
                    holder.e0.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(9)) * twins + melee + ringPrize * metalMultiplier + msPrice)));
                }
                if (!data.get(String.valueOf(intKeys[position])).get(17).equals("")) {
                    holder.g0.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(17)) * twins + melee + ringPrize * metalMultiplier + msPrice)));
                }
                if (!data.get(String.valueOf(intKeys[position])).get(25).equals("")) {
                    holder.i0.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(25)) * twins + melee + ringPrize * metalMultiplier + msPrice)));
                }
                if (!data.get(String.valueOf(intKeys[position])).get(33).equals("")) {
                    holder.k0.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(33)) * twins + melee + ringPrize * metalMultiplier + msPrice)));
                }
            }
            if (!data.get(String.valueOf(intKeys[position])).get(1 + iterator).equals("")) {
                Log.d("1111111111", "position : " + position + " price : " + data.get(String.valueOf(intKeys[position])).get(1 + iterator));
                holder.d1.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(1 + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(3 + iterator).equals("")) {
                holder.d2.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(3 + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(5 + iterator).equals("")) {
                holder.d3.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(5 + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(7 + iterator + iterator).equals("")) {
                holder.e1.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(7 + iterator + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(9 + iterator + iterator).equals("")) {
                holder.e2.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(9 + iterator + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(11 + iterator + iterator).equals("")) {
                Log.d("e3-price", "position " + position + " " + data.get(String.valueOf(intKeys[position])).get(11) + " carat " + intKeys[position] + " twins " + twins + " melee " + melee + " ringprice " + ringPrize + " metal " + metalMultiplier + " msprice " + msPrice);
                holder.e3.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(11 + iterator + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(13 + iterator + iterator + iterator).equals("")) {
                holder.g1.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(13 + iterator + iterator + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(15 + iterator + iterator + iterator).equals("")) {
                holder.g2.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(15 + iterator + iterator + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(17 + iterator + iterator + iterator).equals("")) {
                holder.g3.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(17 + iterator + iterator + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(19 + iterator + iterator + iterator + iterator).equals("")) {
                holder.i1.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(19 + iterator + iterator + iterator + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(21 + iterator + iterator + iterator + iterator).equals("")) {
                holder.i2.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(21 + iterator + iterator + iterator + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(23 + iterator + iterator + iterator + iterator).equals("")) {
                holder.i3.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(23 + iterator + iterator + iterator + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(25 + iterator + iterator + iterator + iterator + iterator).equals("")) {
                holder.k1.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(25 + iterator + iterator + iterator + iterator + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(27 + iterator + iterator + iterator + iterator + iterator).equals("")) {
                holder.k2.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(27 + iterator + iterator + iterator + iterator + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            if (!data.get(String.valueOf(intKeys[position])).get(29 + iterator + iterator + iterator + iterator + iterator).equals("")) {
                holder.k3.setText(roundPrice(String.valueOf(Double.parseDouble(data.get(String.valueOf(intKeys[position])).get(29 + iterator + iterator + iterator + iterator + iterator)) * twins + melee + ringPrize * metalMultiplier + msPrice)));

            }
            for (int i = 0; i < toOrderList.size(); i++) {
                TextView selectedText;
                if (position == Integer.parseInt(toOrderList.get(i).get("position").toString())) {
                    String name = toOrderList.get(i).get("color").toString();
                    switch (name) {
                        case "d0":
                            selectedText = holder.d0;
                            break;
                        case "d1":
                            selectedText = holder.d1;
                            break;
                        case "d2":
                            selectedText = holder.d2;
                            break;
                        case "d3":
                            selectedText = holder.d3;
                            break;
                        case "e0":
                            selectedText = holder.e0;
                            break;
                        case "e1":
                            selectedText = holder.e1;
                            break;
                        case "e2":
                            selectedText = holder.e2;
                            break;
                        case "e3":
                            selectedText = holder.e3;
                            break;
                        case "g0":
                            selectedText = holder.g0;
                            break;
                        case "g1":
                            selectedText = holder.g1;
                            break;
                        case "g2":
                            selectedText = holder.g2;
                            break;
                        case "g3":
                            selectedText = holder.g3;
                            break;
                        case "i0":
                            selectedText = holder.i0;
                            break;
                        case "i1":
                            selectedText = holder.i1;
                            break;
                        case "i2":
                            selectedText = holder.i2;
                            break;
                        case "i3":
                            selectedText = holder.i3;
                            break;
                        case "k0":
                            selectedText = holder.k0;
                            break;
                        case "k1":
                            selectedText = holder.k1;
                            break;
                        case "k2":
                            selectedText = holder.k2;
                            break;
                        case "k3":
                            selectedText = holder.k3;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + name);
                    }
                    selectedText.setBackgroundColor(Color.GRAY);
                    selectedText.setTextColor(Color.WHITE);
                }
            }
        }
        convertView.setEnabled(false);
        convertView.setOnClickListener(null);

        return convertView;
    }


    static class Holder {
        TextView d0;
        TextView d1;
        TextView d2;
        TextView d3;
        TextView e0;
        TextView e1;
        TextView e2;
        TextView e3;
        TextView g0;
        TextView g1;
        TextView g2;
        TextView g3;
        TextView i0;
        TextView i1;
        TextView i2;
        TextView i3;
        TextView k0;
        TextView k1;
        TextView k2;
        TextView k3;
        TextView range;
        TextView warning;
    }

    static class HolderColored {
        TextView a;
        TextView b;
        TextView c;
        TextView d;
        TextView e;
        TextView f;
        TextView range;
    }

    private void addToList(final TextView txtView, final Holder holder, final int position) {
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
                        put("range", holder.range.getText());
                        put("color", context.getResources().getResourceEntryName(txtView.getId()));
                        put("price", txtView.getText());
                        put("position", position);
                        put("metal", metal);
                        put("metalPrice", metalMultiplier * ringPrize);
                    }
                });
            }
        }
        Set<Map<String, Object>> set = new HashSet<>(toOrderList);
        toOrderList.clear();
        toOrderList.addAll(set);
        Log.d("to order list ", String.valueOf(toOrderList));
        ((PricesActivity) context).getToOrder(toOrderList);
    }

    private void addToListColored(final TextView txtView, final HolderColored holder, final int position) {
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
                        put("range", holder.range.getText());
                        put("color", context.getResources().getResourceEntryName(txtView.getId()));
                        put("price", txtView.getText());
                        put("position", position);
                        put("metal", metal);
                        put("metalPrice", metalMultiplier * ringPrize);
                    }
                });
            }
        }
        Set<Map<String, Object>> set = new HashSet<>(toOrderList);
        toOrderList.clear();
        toOrderList.addAll(set);
        Log.d("to order list ", String.valueOf(toOrderList));
        ((PricesActivity) context).getToOrder(toOrderList);
    }

    public static String roundPrice(String price) {
        String usablePrice = Integer.toString((int) Double.parseDouble(price));
        if (usablePrice.length() > 2) {
            int lastTwoDigits = Integer.parseInt(Integer.toString((int) Double.parseDouble(usablePrice)).substring(usablePrice.length() - 2, usablePrice.length()));
            String substring = Integer.toString((int) Double.parseDouble(usablePrice)).substring(0, usablePrice.length() - 2);
            if (lastTwoDigits > 0 && lastTwoDigits <= 30) {
                usablePrice = substring + "30";
            } else if (lastTwoDigits > 30 && lastTwoDigits <= 50) {
                usablePrice = substring + "50";

            } else if (lastTwoDigits > 50 && lastTwoDigits <= 70) {
                usablePrice = substring + "70";
            } else if (lastTwoDigits > 70 && lastTwoDigits <= 90) {
                usablePrice = substring + "90";
            } else {
                usablePrice = substring + "90";
            }
        }
        return usablePrice;
    }
}
