package com.aguadeoro.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aguadeoro.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;


public class PricesMemoiresAdapter extends RecyclerView.Adapter<PricesMemoiresAdapter.ViewHolder> {

    private final Context context;
    double[] preparation, prices;
    Integer[] ringSizes;
    LayoutInflater mInflater;
    ArrayList<Map<String, String>> meleeRates;
    double ringPrice;
    double marge, prMelee, ringPriceW, ringPriceY, ringPriceR, ringPriceP;
    double settingPrice, offsetCover;
    int offset, offsetSize;
    boolean isOffset;
    String height, type, color, material, stoneSize;
    ArrayList<Map<String, String>> stonesData;

    public PricesMemoiresAdapter(Context context, Integer[] sizes,
                                 double ringPrice, double marge, ArrayList<Map<String, String>> meleeRates, double settingPrice,
                                 double[] preparation, double[] prices, String height, ArrayList<Map<String, String>> stonesData,
                                 String type, double prMelee, String color, String material, String stoneSize, boolean isOffset, int offset, int offsetSize, double offsetCover) {
        this.context = context;
        this.ringSizes = sizes;
        this.mInflater = LayoutInflater.from(context);
        this.ringPrice = ringPrice;
        this.marge = marge;
        this.meleeRates = meleeRates;
        this.settingPrice = settingPrice;
        this.preparation = preparation;
        this.prices = prices;
        this.height = height;
        this.stonesData = stonesData;
        Log.d("stonesData", Arrays.toString(stonesData.toArray()));
        this.type = type;
        this.prMelee = prMelee;
        this.color = color;
        this.material = material;
        this.stoneSize = stoneSize;
        this.isOffset = isOffset;
        this.offset = offset;
        this.offsetSize = offsetSize;
        this.offsetCover = offsetCover;
    }

    public PricesMemoiresAdapter(Context context, Integer[] sizes,
                                 double ringPrice, double marge, ArrayList<Map<String, String>> meleeRates, double settingPrice,
                                 double[] preparation, double[] prices, String height, ArrayList<Map<String, String>> stonesData,
                                 String type, double prMelee, String color, String material, String stoneSize, boolean isOffset) {
        this.context = context;
        this.ringSizes = sizes;
        this.mInflater = LayoutInflater.from(context);
        this.ringPrice = ringPrice;
        this.marge = marge;
        this.meleeRates = meleeRates;
        this.settingPrice = settingPrice;
        this.preparation = preparation;
        this.prices = prices;
        this.height = height;
        this.stonesData = stonesData;
        this.type = type;
        this.prMelee = prMelee;
        this.color = color;
        this.material = material;
        this.stoneSize = stoneSize;
        this.isOffset = isOffset;
    }

    @NonNull
    @Override
    public PricesMemoiresAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = mInflater.inflate(R.layout.line_prices_memoires, viewGroup, false);
        return new PricesMemoiresAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PricesMemoiresAdapter.ViewHolder viewHolder, int i) {
        int stoneNumber = 0;
        ArrayList<Double> tempDiametre = new ArrayList<>();
        if (type.equals("Fixed")) {
            for (int j = 0; j < stonesData.size(); j++) {
                stoneNumber += Integer.parseInt(stonesData.get(j).get("number"));
            }
            viewHolder.number.setText(stoneNumber + " stones (100%) " + stoneNumber +
                    " stones(75%) " + stoneNumber + " stones(50%) " + stoneNumber + " stones(25%)");
        } else {
            for (int j = 0; j < stonesData.size(); j++) {
                if (!tempDiametre.contains(Double.parseDouble(stonesData.get(j).get("diametre")))) {
                    stoneNumber = stoneNumber + calculateNumberOfStones(Double.parseDouble(stonesData.get(j).get("diametre")), i);
                }
                tempDiametre.add(Double.parseDouble(stonesData.get(j).get("diametre")));
            }
            if (isOffset) {
                stoneNumber = calculateNumberOfStonesWithOffset(offset, offsetCover, offsetSize, i);
            }
            viewHolder.number.setText("    " + stoneNumber + " (100%) \t\t\t\t" + (int) (stoneNumber * 0.90) + " (90%) \t\t\t\t" + (int) (stoneNumber * 0.75) +
                    " (75%) \t\t\t\t" + (int) (stoneNumber * 0.5) + " (50%) \t\t\t\t" + (int) (stoneNumber * 0.25) + " (25%)");
        }
        viewHolder.range.setText(ringSizes[i] - 2 + "-" + ringSizes[i]);

        if (material.contains("PT")) {
            ringPriceW = ringPrice / 1.35;
            ringPriceY = ringPrice / 1.35 * 0.95;
            ringPriceR = ringPrice / 1.35 * 1.1;
            ringPriceP = ringPrice;
        } else {
            if (color.contains("Yellow")) {
                ringPriceW = ringPrice / 0.95;
                ringPriceY = ringPrice;
                ringPriceR = ringPrice / 0.95 * 1.1;
                ringPriceP = ringPrice / 0.95 * 1.35;
            } else if (color.contains("Red")) {
                ringPriceW = ringPrice / 1.1;
                ringPriceY = ringPrice / 1.1 * 0.95;
                ringPriceR = ringPrice;
                ringPriceP = ringPrice / 1.1 * 1.35;
            } else {
                ringPriceW = ringPrice;
                ringPriceY = ringPrice * 0.95;
                ringPriceR = ringPrice * 1.1;
                ringPriceP = ringPrice * 1.35;
            }
        }


        viewHolder.w100.setText(roundPrice(Integer.toString((int) (ringPriceW + totalPrice(1, i, isOffset)))) + "CHF");
        viewHolder.w90.setText(roundPrice(Integer.toString((int) (ringPriceW + totalPrice(0.9, i, isOffset)))) + "CHF");
        viewHolder.w75.setText(roundPrice(Integer.toString((int) (ringPriceW + totalPrice(0.75, i, isOffset)))) + "CHF");
        viewHolder.w50.setText(roundPrice(Integer.toString((int) (ringPriceW + totalPrice(0.5, i, isOffset)))) + "CHF");
        viewHolder.w25.setText(roundPrice(Integer.toString((int) (ringPriceW + totalPrice(0.25, i, isOffset)))) + "CHF");
        viewHolder.y100.setText(roundPrice(Integer.toString((int) (ringPriceY + totalPrice(1, i, isOffset)))) + "CHF");
        viewHolder.y90.setText(roundPrice(Integer.toString((int) (ringPriceY + totalPrice(0.9, i, isOffset)))) + "CHF");
        viewHolder.y75.setText(roundPrice(Integer.toString((int) (ringPriceY + totalPrice(0.75, i, isOffset)))) + "CHF");
        viewHolder.y50.setText(roundPrice(Integer.toString((int) (ringPriceY + totalPrice(0.5, i, isOffset)))) + "CHF");
        viewHolder.y25.setText(roundPrice(Integer.toString((int) (ringPriceY + totalPrice(0.25, i, isOffset)))) + "CHF");
        viewHolder.r100.setText(roundPrice(Integer.toString((int) (ringPriceR + totalPrice(1, i, isOffset)))) + "CHF");
        viewHolder.r90.setText(roundPrice(Integer.toString((int) (ringPriceR + totalPrice(0.9, i, isOffset)))) + "CHF");
        viewHolder.r75.setText(roundPrice(Integer.toString((int) (ringPriceR + totalPrice(0.75, i, isOffset)))) + "CHF");
        viewHolder.r50.setText(roundPrice(Integer.toString((int) (ringPriceR + totalPrice(0.5, i, isOffset)))) + "CHF");
        viewHolder.r25.setText(roundPrice(Integer.toString((int) (ringPriceR + totalPrice(0.25, i, isOffset)))) + "CHF");
        viewHolder.p100.setText(roundPrice(Integer.toString((int) (ringPriceP + totalPrice(1, i, isOffset)))) + "CHF");
        viewHolder.p90.setText(roundPrice(Integer.toString((int) (ringPriceP + totalPrice(0.9, i, isOffset)))) + "CHF");
        viewHolder.p75.setText(roundPrice(Integer.toString((int) (ringPriceP + totalPrice(0.75, i, isOffset)))) + "CHF");
        viewHolder.p50.setText(roundPrice(Integer.toString((int) (ringPriceP + totalPrice(0.5, i, isOffset)))) + "CHF");
        viewHolder.p25.setText(roundPrice(Integer.toString((int) (ringPriceP + totalPrice(0.25, i, isOffset)))) + "CHF");
    }

    @Override
    public int getItemCount() {
        return ringSizes.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView w100;
        TextView w90;
        TextView w75;
        TextView w50;
        TextView w25;
        TextView y100;
        TextView y90;
        TextView y75;
        TextView y50;
        TextView y25;
        TextView r100;
        TextView r90;
        TextView r75;
        TextView r50;
        TextView r25;
        TextView p100;
        TextView p90;
        TextView p75;
        TextView p50;
        TextView p25;
        TextView range;
        TextView number;

        ViewHolder(View itemView) {
            super(itemView);
            this.w100 = itemView.findViewById(R.id.w100);
            this.w90 = itemView.findViewById(R.id.w90);
            this.w75 = itemView.findViewById(R.id.w75);
            this.w50 = itemView.findViewById(R.id.w50);
            this.w25 = itemView.findViewById(R.id.w25);
            this.y100 = itemView.findViewById(R.id.y100);
            this.y90 = itemView.findViewById(R.id.y90);
            this.y75 = itemView.findViewById(R.id.y75);
            this.y50 = itemView.findViewById(R.id.y50);
            this.y25 = itemView.findViewById(R.id.y25);
            this.r100 = itemView.findViewById(R.id.r100);
            this.r90 = itemView.findViewById(R.id.r90);
            this.r75 = itemView.findViewById(R.id.r75);
            this.r50 = itemView.findViewById(R.id.r50);
            this.r25 = itemView.findViewById(R.id.r25);
            this.p100 = itemView.findViewById(R.id.p100);
            this.p90 = itemView.findViewById(R.id.p90);
            this.p75 = itemView.findViewById(R.id.p75);
            this.p50 = itemView.findViewById(R.id.p50);
            this.p25 = itemView.findViewById(R.id.p25);
            this.range = itemView.findViewById(R.id.range);
            this.number = itemView.findViewById(R.id.stonesNumber);
        }
    }


    private double totalPrice(double cover, int i, boolean calculateWithOffset) {
        double stonePrice = 0.0;
        double tempPrice = 0.0;
        double finalRate = 1.0;
        stonePrice = Double.parseDouble(Objects.requireNonNull(stonesData.get(0).get("price")));
        int stoneNumber = 0;
        double result = (settingPrice + stonePrice);

        if (stonesData.size() > 1) {
            for (int j = 0; j < stonesData.size(); j++) {
                int tempStoneNumber;
                if (type.equals("Multi")) {
                    tempStoneNumber = calculateNumberOfStones(Double.parseDouble(stonesData.get(j).get("diametre")), i);
                } else if (type.equals("Fixed")) {
                    tempStoneNumber = Integer.parseInt(stonesData.get(j).get("number"));
                } else {
                    if (calculateWithOffset) {
                        int stoneNumberForOffset = 0;
                        //stoneNumberForOffset += (calculateNumberOfStones(Double.parseDouble(stonesData.get(j).get("diametre")), i) / (offset / Integer.parseInt(stonesData.get(j).get("number"))));
                        stoneNumberForOffset += offset / stonesData.get(j).get("number").length();
                        tempStoneNumber = calculateNumberOfStonesWithOffset(stoneNumberForOffset, offsetCover, offsetSize, i);
                    } else {
                        tempStoneNumber = calculateNumberOfStones(Double.parseDouble(stonesData.get(j).get("diametre")), i) / stonesData.size();
                    }
                }

                stoneNumber += tempStoneNumber;
                tempPrice = tempPrice + ((tempStoneNumber * cover) * (settingPrice + Double.parseDouble(stonesData.get(j).get("price"))));
            }

        } else {
            if (calculateWithOffset) {
                stoneNumber = calculateNumberOfStonesWithOffset(offset, offsetCover, offsetSize, i);
            } else {
                stoneNumber = calculateNumberOfStones(Double.parseDouble(Objects.requireNonNull(stonesData.get(0).get("diametre"))), i);
            }
            tempPrice = ((stoneNumber * cover) * (settingPrice + stonePrice));
        }
        for (int j = 0; j < meleeRates.size(); j++) {
            if (tempPrice <= Double.parseDouble(meleeRates.get(j).get("Range"))) {
                finalRate = Double.parseDouble(meleeRates.get(j).get("Rate"));
                break;
            }
        }
        double finalStoneNumber = stoneNumber * cover;
        if (finalStoneNumber % 1 != 0) {
            finalStoneNumber = Math.ceil(finalStoneNumber);
        }
        result = result * finalStoneNumber;
        result = result * finalRate;
        result = result * prMelee;
        return result;
    }

    private int calculateNumberOfStones(Double stoneDiametre, int i) {
        return (int) ((((ringSizes[i] / Math.PI) + (Double.parseDouble(height) * 2)) * Math.PI) / stoneDiametre);
    }

    private int calculateNumberOfStonesWithOffset(int offset, double cover, int offsetSize, int i) {
        if (cover < 1.0) {
            offset = (int) (offset / cover);
        }
        if (offsetSize == ringSizes[i]) {
            return offset;
        } else {
            return (ringSizes[i] * offset / offsetSize);
        }
    }

    private String roundPrice(String price) {
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
            } else if (lastTwoDigits > 70 && lastTwoDigits <= 99) {
                usablePrice = substring + "90";
            }
        }
        return usablePrice;
    }
}
