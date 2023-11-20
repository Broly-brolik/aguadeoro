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
import com.aguadeoro.activity.StonesPricesActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class StonesPricesAdapter extends RecyclerView.Adapter<StonesPricesAdapter.ViewHolder> {

    private Map<String, ArrayList<Map<String, String>>> data;
    private ArrayList<ArrayList<Map<String, String>>> stockData;
    private final Context context;
    private String[] mKeys;
    private double[] carats;
    private final boolean isColor;
    private final boolean isStock;
    private final LayoutInflater mInflater;
    private boolean isMine;

    public StonesPricesAdapter(Context context, Map<String, ArrayList<Map<String, String>>> data, boolean isColor, boolean isMine) {
        this.data = data;
        this.context = context;
        this.isColor = isColor;
        this.isMine = isMine;
        this.mInflater = LayoutInflater.from(context);
        mKeys = data.keySet().toArray(new String[data.size()]);
        double[] intKeys = new double[mKeys.length];
        for (int i = 0; i < mKeys.length; i++) {
            intKeys[i] = Double.parseDouble(mKeys[i]);
        }
        Arrays.sort(intKeys);
        for (int i = 0; i < intKeys.length; i++) {
            mKeys[i] = String.valueOf(intKeys[i]);
        }
        this.isStock = false;
    }

    public StonesPricesAdapter(Context context, ArrayList<ArrayList<Map<String, String>>> data, double[] carats, boolean isColor) {
        this.context = context;
        this.stockData = data;
        this.carats = carats;
        this.isStock = true;
        this.isColor = isColor;
        this.mInflater = LayoutInflater.from(context);
    }

    
    @Override
    public StonesPricesAdapter.ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view;
        if (isStock && isColor) {
            view = mInflater.inflate(R.layout.line_colored_stock, viewGroup, false);
        } else if (isColor) {
            view = mInflater.inflate(R.layout.line_prices_colored, viewGroup, false);

        } else if (isStock) {
            view = mInflater.inflate(R.layout.line_stones_stock, viewGroup, false);
        } else {
            view = mInflater.inflate(R.layout.line_prices, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( StonesPricesAdapter.ViewHolder holder, int position) {
        int greenColor = Color.parseColor("#862FAE0C");
        int redColor = Color.parseColor("#86E10D0D");
        double minRange = 0.04;
        if (isColor && isStock) {
            holder.range.setText(String.format("%.2f", carats[position] - minRange) + " - " + carats[position]);
            int totalA = 0;
            int totalB = 0;
            int totalC = 0;
            int totalD = 0;
            int totalE = 0;
            int totalF = 0;
            for (ArrayList<Map<String, String>> stoneArray : stockData) {
                for (Map<String, String> stone : stoneArray) {
                    if (Double.parseDouble(stone.get("Carat")) == carats[position]) {
                        if (stone.containsKey("a")) {
                            totalA += Integer.parseInt(stone.get("a"));
                        }
                        if (stone.containsKey("b")) {
                            totalB += Integer.parseInt(stone.get("b"));
                        }
                        if (stone.containsKey("c")) {
                            totalC += Integer.parseInt(stone.get("c"));
                        }
                        if (stone.containsKey("d")) {
                            totalD += Integer.parseInt(stone.get("d"));
                        }
                        if (stone.containsKey("e")) {
                            totalE += Integer.parseInt(stone.get("e"));
                        }
                        if (stone.containsKey("f")) {
                            totalF += Integer.parseInt(stone.get("f"));
                        }
                    }
                }
                if (totalA > 0) {
                    holder.a.setText(String.valueOf(totalA));
                    holder.a.setBackgroundColor(greenColor);
                }
                if (totalB > 0) {
                    holder.b.setText(String.valueOf(totalB));
                    holder.b.setBackgroundColor(greenColor);
                }
                if (totalC > 0) {
                    holder.c.setText(String.valueOf(totalC));
                    holder.c.setBackgroundColor(greenColor);
                }
                if (totalD > 0) {
                    holder.d.setText(String.valueOf(totalD));
                    holder.d.setBackgroundColor(greenColor);
                }
                if (totalE > 0) {
                    holder.e.setText(String.valueOf(totalE));
                    holder.e.setBackgroundColor(greenColor);
                }
                if (totalF > 0) {
                    holder.f.setText(String.valueOf(totalF));
                    holder.f.setBackgroundColor(greenColor);
                }
            }
        } else if (isStock) {
            holder.range.setText(String.format("%.2f", carats[position] - minRange) + " - " + carats[position]);
            int totald0 = 0;
            int totald1 = 0;
            int totald2 = 0;
            int totald3 = 0;
            int totale0 = 0;
            int totale1 = 0;
            int totale2 = 0;
            int totale3 = 0;
            int totalg0 = 0;
            int totalg1 = 0;
            int totalg2 = 0;
            int totalg3 = 0;
            int totali0 = 0;
            int totali1 = 0;
            int totali2 = 0;
            int totali3 = 0;
            int totalk0 = 0;
            int totalk1 = 0;
            int totalk2 = 0;
            int totalk3 = 0;
            int pos = position;
            for (ArrayList<Map<String, String>> stoneArray : stockData) {
                for (Map<String, String> stone : stoneArray) {
                    if (Double.parseDouble(stone.get("Carat")) == carats[position]) {
                        if (stone.containsKey("d0")) {
                            totald0 += Integer.parseInt(stone.get("d0"));
                        }
                        if (stone.containsKey("d1")) {
                            totald1 += Integer.parseInt(stone.get("d1"));
                        }
                        if (stone.containsKey("d2")) {
                            totald2 += Integer.parseInt(stone.get("d2"));
                        }
                        if (stone.containsKey("d3")) {
                            totald3 += Integer.parseInt(stone.get("d3"));
                        }
                        if (stone.containsKey("e0")) {
                            totale0 += Integer.parseInt(stone.get("e0"));
                        }
                        if (stone.containsKey("e1")) {
                            totale1 += Integer.parseInt(stone.get("e1"));
                        }
                        if (stone.containsKey("e2")) {
                            totale2 += Integer.parseInt(stone.get("e2"));
                        }
                        if (stone.containsKey("e3")) {
                            totale3 += Integer.parseInt(stone.get("e3"));
                        }
                        if (stone.containsKey("g0")) {
                            totalg0 += Integer.parseInt(stone.get("g0"));
                        }
                        if (stone.containsKey("g1")) {
                            totalg1 += Integer.parseInt(stone.get("g1"));
                        }
                        if (stone.containsKey("g2")) {
                            totalg2 += Integer.parseInt(stone.get("g2"));
                        }
                        if (stone.containsKey("g3")) {
                            totalg3 += Integer.parseInt(stone.get("g3"));
                        }
                        if (stone.containsKey("i0")) {
                            totali0 += Integer.parseInt(stone.get("i0"));
                        }
                        if (stone.containsKey("i1")) {
                            totali1 += Integer.parseInt(stone.get("i1"));
                        }
                        if (stone.containsKey("i2")) {
                            totali2 += Integer.parseInt(stone.get("i2"));
                        }
                        if (stone.containsKey("i3")) {
                            totali3 += Integer.parseInt(stone.get("i3"));
                        }
                        if (stone.containsKey("k0")) {
                            totalk0 += Integer.parseInt(stone.get("k0"));
                        }
                        if (stone.containsKey("k1")) {
                            totalk1 += Integer.parseInt(stone.get("k1"));
                        }
                        if (stone.containsKey("k2")) {
                            totalk2 += Integer.parseInt(stone.get("k2"));
                        }
                        if (stone.containsKey("k3")) {
                            totalk3 += Integer.parseInt(stone.get("k3"));
                        }
                    }
                }
            }
            if (totald0 > 0) {
                holder.d0.setText(String.valueOf(totald0));
                holder.d0.setBackgroundColor(greenColor);
                holder.d0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("d0", carats[pos]);
                    }
                });

            } else {
                holder.d0.setText("0");
                holder.d0.setBackgroundColor(redColor);
            }
            if (totald1 > 0) {
                holder.d1.setText(String.valueOf(totald1));
                holder.d1.setBackgroundColor(greenColor);
                holder.d1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("d1", carats[pos]);
                    }
                });
            } else {
                holder.d1.setText("0");
                holder.d1.setBackgroundColor(redColor);
            }
            if (totald2 > 0) {
                holder.d2.setText(String.valueOf(totald2));
                holder.d2.setBackgroundColor(greenColor);
                holder.d2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("d2", carats[pos]);
                    }
                });
            } else {
                holder.d2.setText("0");
                holder.d2.setBackgroundColor(redColor);
            }
            if (totald3 > 0) {
                holder.d3.setText(String.valueOf(totald3));
                holder.d3.setBackgroundColor(greenColor);
                holder.d3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("d3", carats[pos]);
                    }
                });
            } else {
                holder.d3.setText("0");
                holder.d3.setBackgroundColor(redColor);
            }
            if (totale0 > 0) {
                holder.e0.setText(String.valueOf(totale0));
                holder.e0.setBackgroundColor(greenColor);
                holder.e0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("e0", carats[pos]);
                    }
                });
            } else {
                holder.e0.setText("0");
                holder.e0.setBackgroundColor(redColor);
            }
            if (totale1 > 0) {
                holder.e1.setText(String.valueOf(totale1));
                holder.e1.setBackgroundColor(greenColor);
                holder.e1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("e1", carats[pos]);
                    }
                });
            } else {
                holder.e1.setText("0");
                holder.e1.setBackgroundColor(redColor);
            }
            if (totale2 > 0) {
                holder.e2.setText(String.valueOf(totale2));
                holder.e2.setBackgroundColor(greenColor);
                holder.e2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("e2", carats[pos]);
                    }
                });
            } else {
                holder.e2.setText("0");
                holder.e2.setBackgroundColor(redColor);
            }
            if (totale3 > 0) {
                holder.e3.setText(String.valueOf(totale3));
                holder.e3.setBackgroundColor(greenColor);
                holder.e3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("e3", carats[pos]);
                    }
                });
            } else {
                holder.e3.setText("0");
                holder.e3.setBackgroundColor(redColor);
            }
            if (totalg0 > 0) {
                holder.g0.setText(String.valueOf(totalg0));
                holder.g0.setBackgroundColor(greenColor);
                holder.g0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("g0", carats[pos]);
                    }
                });
            } else {
                holder.g0.setText("0");
                holder.g0.setBackgroundColor(redColor);
            }
            if (totalg1 > 0) {
                holder.g1.setText(String.valueOf(totalg1));
                holder.g1.setBackgroundColor(greenColor);
                holder.g1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("g1", carats[pos]);
                    }
                });
            } else {
                holder.g1.setText("0");
                holder.g1.setBackgroundColor(redColor);
            }
            if (totalg2 > 0) {
                holder.g2.setText(String.valueOf(totalg2));
                holder.g2.setBackgroundColor(greenColor);
                holder.g2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("g2", carats[pos]);
                    }
                });
            } else {
                holder.g2.setText("0");
                holder.g2.setBackgroundColor(redColor);
            }
            if (totalg3 > 0) {
                holder.g3.setText(String.valueOf(totalg3));
                holder.g3.setBackgroundColor(greenColor);
                holder.g3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("g3", carats[pos]);
                    }
                });
            } else {
                holder.g3.setText("0");
                holder.g3.setBackgroundColor(redColor);
            }
            if (totali0 > 0) {
                holder.i0.setText(String.valueOf(totali0));
                holder.i0.setBackgroundColor(greenColor);
                holder.i0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("i0", carats[pos]);
                    }
                });
            } else {
                holder.i0.setText("0");
                holder.i0.setBackgroundColor(redColor);
            }
            if (totali1 > 0) {
                holder.i1.setText(String.valueOf(totali1));
                holder.i1.setBackgroundColor(greenColor);
                holder.i1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("i1", carats[pos]);
                    }
                });
            } else {
                holder.i1.setText("0");
                holder.i1.setBackgroundColor(redColor);
            }
            if (totali2 > 0) {
                holder.i2.setText(String.valueOf(totali2));
                holder.i2.setBackgroundColor(greenColor);
                holder.i2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("i2", carats[pos]);
                    }
                });
            } else {
                holder.i2.setText("0");
                holder.i2.setBackgroundColor(redColor);
            }
            if (totali3 > 0) {
                holder.i3.setText(String.valueOf(totali3));
                holder.i3.setBackgroundColor(greenColor);
                holder.i3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("i3", carats[pos]);
                    }
                });
            } else {
                holder.i3.setText("0");
                holder.i3.setBackgroundColor(redColor);
            }
            if (totalk0 > 0) {
                holder.k0.setText(String.valueOf(totalk0));
                holder.k0.setBackgroundColor(greenColor);
                holder.k0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("k0", carats[pos]);
                    }
                });
            } else {
                holder.k0.setText("0");
                holder.k0.setBackgroundColor(redColor);
            }
            if (totalk1 > 0) {
                holder.k1.setText(String.valueOf(totalk1));
                holder.k1.setBackgroundColor(greenColor);
                holder.k1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("k1", carats[pos]);
                    }
                });
            } else {
                holder.k1.setText("0");
                holder.k1.setBackgroundColor(redColor);
            }
            if (totalk2 > 0) {
                holder.k2.setText(String.valueOf(totalk2));
                holder.k2.setBackgroundColor(greenColor);
                holder.k2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("k2", carats[pos]);
                    }
                });
            } else {
                holder.k2.setText("0");
                holder.k2.setBackgroundColor(redColor);
            }
            if (totalk3 > 0) {
                holder.k3.setText(String.valueOf(totalk3));
                holder.k3.setBackgroundColor(greenColor);
                holder.k3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendDialogData("k3", carats[pos]);
                    }
                });
            } else {
                holder.k3.setText("0");
                holder.k3.setBackgroundColor(redColor);
            }
        } else {
            holder.range.setText(String.format("%.2f", Double.parseDouble(mKeys[position]) - 0.04) + " - " + mKeys[position]);
            if (!isColor) {
                int iterator = 0;
                if (isMine) {
                    iterator = 1;
                    if (!data.get((mKeys[position])).get(0).get("Price").equals("")) {
                        holder.d0.setText(((data.get((mKeys[position])).get(0).get("Price"))));
                    } else {
                        holder.d0.setText("N/A");
                    }
                    if (!data.get((mKeys[position])).get(4).get("Price").equals("")) {
                        holder.e0.setText(((data.get((mKeys[position])).get(4).get("Price"))));
                    } else {
                        holder.e0.setText("N/A");
                    }
                    if (!data.get((mKeys[position])).get(8).get("Price").equals("")) {
                        holder.g0.setText(((data.get((mKeys[position])).get(8).get("Price"))));
                    } else {
                        holder.g0.setText("N/A");
                    }
                    if (!data.get((mKeys[position])).get(12).get("Price").equals("")) {
                        holder.i0.setText(((data.get((mKeys[position])).get(12).get("Price"))));
                    } else {
                        holder.i0.setText("N/A");
                    }
                    if (!data.get((mKeys[position])).get(16).get("Price").equals("")) {
                        holder.k0.setText(((data.get((mKeys[position])).get(16).get("Price"))));
                    } else {
                        holder.k0.setText("N/A");
                    }
                }
                if (!data.get((mKeys[position])).get(0 + iterator).get("Price").equals("")) {
                    holder.d1.setText(((data.get((mKeys[position])).get(0 + iterator).get("Price"))));
                } else {
                    holder.d1.setText("N/A");
                }
                if (!data.get((mKeys[position])).get(1 + iterator).get("Price").equals("")) {
                    holder.d2.setText(((data.get((mKeys[position])).get(1 + iterator).get("Price"))));
                } else {
                    holder.d2.setText("N/A");
                }
                if (!data.get((mKeys[position])).get(2 + iterator).get("Price").equals("")) {
                    holder.d3.setText(((data.get((mKeys[position])).get(2 + iterator).get("Price"))));
                } else {
                    holder.d3.setText("N/A");
                }
                if (isMine) {
                    iterator += 1;
                }
                if (!data.get((mKeys[position])).get(3 + iterator).get("Price").equals("")) {
                    holder.e1.setText(((data.get((mKeys[position])).get(3 + iterator).get("Price"))));
                } else {
                    holder.e1.setText("N/A");
                }
                if (!data.get((mKeys[position])).get(4 + iterator).get("Price").equals("")) {
                    holder.e2.setText(((data.get((mKeys[position])).get(4 + iterator).get("Price"))));
                } else {
                    holder.e2.setText("N/A");
                }
                if (!data.get((mKeys[position])).get(5 + iterator).get("Price").equals("")) {
                    holder.e3.setText(((data.get((mKeys[position])).get(5 + iterator).get("Price"))));
                } else {
                    holder.e3.setText("N/A");
                }
                if (isMine) {
                    iterator += 1;
                }
                if (!data.get((mKeys[position])).get(6 + iterator).get("Price").equals("")) {
                    holder.g1.setText(((data.get((mKeys[position])).get(6 + iterator).get("Price"))));
                } else {
                    holder.g1.setText("N/A");
                }
                if (!data.get((mKeys[position])).get(7 + iterator).get("Price").equals("")) {
                    holder.g2.setText(data.get(mKeys[position]).get(7 + iterator).get("Price"));
                } else {
                    holder.g2.setText("N/A");
                }
                if (!data.get((mKeys[position])).get(8 + iterator).get("Price").equals("")) {
                    holder.g3.setText(((data.get((mKeys[position])).get(8 + iterator).get("Price"))));
                } else {
                    holder.g3.setText("N/A");
                }
                if (isMine) {
                    iterator += 1;
                }
                if (!data.get((mKeys[position])).get(9 + iterator).get("Price").equals("")) {
                    holder.i1.setText(((data.get((mKeys[position])).get(9 + iterator).get("Price"))));
                } else {
                    holder.i1.setText("N/A");
                }
                if (!data.get((mKeys[position])).get(10 + iterator).get("Price").equals("")) {
                    holder.i2.setText(((data.get((mKeys[position])).get(10 + iterator).get("Price"))));
                } else {
                    holder.i2.setText("N/A");
                }
                if (!data.get((mKeys[position])).get(11 + iterator).get("Price").equals("")) {
                    holder.i3.setText(((data.get((mKeys[position])).get(11 + iterator).get("Price"))));
                } else {
                    holder.i3.setText("N/A");
                }
                if (isMine) {
                    iterator += 1;
                }
                if (!data.get((mKeys[position])).get(12 + iterator).get("Price").equals("")) {
                    holder.k1.setText(((data.get((mKeys[position])).get(12 + iterator).get("Price"))));
                } else {
                    holder.k1.setText("N/A");
                }
                if (!data.get((mKeys[position])).get(13 + iterator).get("Price").equals("")) {
                    holder.k2.setText(((data.get((mKeys[position])).get(13 + iterator).get("Price"))));
                } else {
                    holder.k2.setText("N/A");
                }
                if (!data.get((mKeys[position])).get(14 + iterator).get("Price").equals("")) {
                    holder.k3.setText(((data.get((mKeys[position])).get(14 + iterator).get("Price"))));
                } else {
                    holder.k3.setText("N/A");
                }
            } else {
                if (!data.get(mKeys[position]).get(0).get("price").equals("")) {
                    holder.a.setText(data.get((mKeys[position])).get(0).get("price"));

                }
                if (!data.get(mKeys[position]).get(1).get("price").equals("")) {
                    holder.b.setText(data.get((mKeys[position])).get(1).get("price"));

                }
                if (!data.get(mKeys[position]).get(2).get("price").equals("")) {
                    holder.c.setText(data.get((mKeys[position])).get(2).get("price"));

                }
                if (!data.get(mKeys[position]).get(3).get("price").equals("")) {
                    holder.d.setText(data.get((mKeys[position])).get(3).get("price"));

                }
                if (!data.get(mKeys[position]).get(4).get("price").equals("")) {
                    holder.e.setText(data.get((mKeys[position])).get(4).get("price"));

                }
                if (!data.get(mKeys[position]).get(5).get("price").equals("")) {
                    holder.f.setText(data.get((mKeys[position])).get(5).get("price"));

                }
            }
        }
        if (isMine) {

        }

    }

    private void sendDialogData(String gridPosition, double carat) {
        ArrayList<Map<String, String>> stoneArray = new ArrayList<>();
        for (ArrayList<Map<String, String>> stoneArray1 : stockData) {
            Log.e("stock data", stockData.toString());
            for (Map<String, String> stone : stoneArray1) {
                if (Double.parseDouble(stone.get("Carat")) == carat) {
                    if (stone.containsKey(gridPosition)) {
                        stoneArray.add(stone);
                    }
                }
            }
        }
        ((StonesPricesActivity) context).onClickStock(stoneArray);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (isStock) {
            return carats.length;
        } else {
            return data.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
        TextView a;
        TextView b;
        TextView c;
        TextView d;
        TextView e;
        TextView f;
        TextView range;

        ViewHolder(View itemView) {
            super(itemView);
            if (!isColor) {
                range = itemView.findViewById(R.id.range);
                d0 = itemView.findViewById(R.id.d0);
                d1 = itemView.findViewById(R.id.d1);
                d2 = itemView.findViewById(R.id.d2);
                d3 = itemView.findViewById(R.id.d3);
                e0 = itemView.findViewById(R.id.e0);
                e1 = itemView.findViewById(R.id.e1);
                e2 = itemView.findViewById(R.id.e2);
                e3 = itemView.findViewById(R.id.e3);
                g0 = itemView.findViewById(R.id.g0);
                g1 = itemView.findViewById(R.id.g1);
                g2 = itemView.findViewById(R.id.g2);
                g3 = itemView.findViewById(R.id.g3);
                i0 = itemView.findViewById(R.id.i0);
                i1 = itemView.findViewById(R.id.i1);
                i2 = itemView.findViewById(R.id.i2);
                i3 = itemView.findViewById(R.id.i3);
                k0 = itemView.findViewById(R.id.k0);
                k1 = itemView.findViewById(R.id.k1);
                k2 = itemView.findViewById(R.id.k2);
                k3 = itemView.findViewById(R.id.k3);
            } else {
                range = itemView.findViewById(R.id.rangeColored);
                a = itemView.findViewById(R.id.a);
                b = itemView.findViewById(R.id.b);
                c = itemView.findViewById(R.id.c);
                d = itemView.findViewById(R.id.d);
                e = itemView.findViewById(R.id.e);
                f = itemView.findViewById(R.id.f);
            }
        }
    }


}