package com.aguadeoro.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.activity.OrderDetailActivity;
import com.aguadeoro.utils.EditTextDropdown;
import com.aguadeoro.utils.EditTextDropdownCustomFont;
import com.aguadeoro.utils.Query;
import com.aguadeoro.utils.Utils;

import java.util.ArrayList;

public class OrderComponentListAdapter extends ArrayAdapter<String[]> {

    private final Activity context;
    private final ArrayList<String[]> objects;
    String orderType;
    boolean editable;
    private static final int RESULT_LOAD_IMG = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TIME_STAMP = "null";
    String imgPath, fileName;
    Bitmap bitmap, lesimg;

    public OrderComponentListAdapter(Activity context, ArrayList<String[]> objects, String orderType, boolean editable) {
        super(context, R.layout.line_order_component, objects);
        this.context = context;
        this.objects = objects;
        this.orderType = orderType;
        this.editable = editable;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout rowView = (LinearLayout) inflater.inflate(R.layout.line_order_component, parent, false);
        ((EditText) rowView.findViewById(R.id.article_type)).setText(objects.get(position)[2]);
        ((EditText) rowView.findViewById(R.id.article_prefix)).setText(objects.get(position)[3]);
        if (orderType.equals("Vente")) {
            ((EditText) rowView.findViewById(R.id.article_number)).setText(getInventoryCode(objects.get(position)[28]));
        } else {
            ((EditText) rowView.findViewById(R.id.article_number)).setText("");
        }
        ((EditText) rowView.findViewById(R.id.material)).setText(objects.get(position)[5]);
        ((EditText) rowView.findViewById(R.id.color)).setText(objects.get(position)[6]);
        ((EditText) rowView.findViewById(R.id.length)).setText(objects.get(position)[7]);
        ((EditText) rowView.findViewById(R.id.height)).setText(objects.get(position)[8]);
        ((EditText) rowView.findViewById(R.id.size)).setText(objects.get(position)[9]);
        ((EditText) rowView.findViewById(R.id.surface)).setText(objects.get(position)[10]);
        ((EditText) rowView.findViewById(R.id.stone)).setText(objects.get(position)[11]);
        ((EditText) rowView.findViewById(R.id.engraving_text)).setText(objects.get(position)[12]);
        ((EditText) rowView.findViewById(R.id.engraving_type)).setText(objects.get(position)[13]);
        ((EditText) rowView.findViewById(R.id.engraving_font)).setText(objects.get(position)[14]);
        ((EditText) rowView.findViewById(R.id.engraving_cost)).setText(objects.get(position)[15]);
        ((EditText) rowView.findViewById(R.id.price)).setText(objects.get(position)[16]);
        ((EditText) rowView.findViewById(R.id.remark)).setText(objects.get(position)[17]);
        //Log.d("2222222",""+objects.get(position)[29]);
        /*if(!objects.get(position)[29].equals("")){
            ((TextView) rowView.findViewById(R.id.description)).setText("Name");
            ((EditText) rowView.findViewById(R.id.remark)).setText(objects.get(position)[29]);
        }
        else{
            ((EditText) rowView.findViewById(R.id.remark)).setText(objects.get(position)[17]);
        }*/
        ((CheckBox) rowView.findViewById(R.id.fingerprint)).setChecked(objects.get(position)[25].equals("1"));
        ((CheckBox) rowView.findViewById(R.id.microtext)).setChecked(objects.get(position)[26].equals("1"));
        ((TextView) rowView.findViewById(R.id.catalog_code)).setText(objects.get(position)[27]);
        rowView.findViewById(R.id.inventory_detail_btn).setTag(objects.get(position)[28]);
        rowView.findViewById(R.id.inventory_detail_stock_btn).setTag(objects.get(position)[27]);
        //((TextView) rowView.findViewById(R.id.ringName)).setText(objects.get(position)[29]);

        //hide some icons
        rowView.findViewById(R.id.confirm_btn).setVisibility(View.GONE);
        rowView.findViewById(R.id.search_btn).setVisibility(View.GONE);
        //if not linked to inventory item
        if (objects.get(position)[28].isEmpty()) {
            rowView.findViewById(R.id.inventory_detail_btn).setVisibility(View.GONE);
        }
        if(objects.get(position)[27].isEmpty()){
            rowView.findViewById(R.id.inventory_detail_stock_btn).setVisibility(View.GONE);
        }

        if (Utils.ORD_PREVIEW.equals(orderType)) {
            rowView.findViewById(R.id.order_comp_line3).setVisibility(View.GONE);
            rowView.findViewById(R.id.order_comp_line4).setVisibility(View.GONE);
            rowView.findViewById(R.id.order_comp_line5).setVisibility(View.GONE);
        }
        if (!editable) {
            rowView.findViewById(R.id.catalog_code).setFocusable(editable);
            rowView.findViewById(R.id.article_type).setFocusable(editable);
            rowView.findViewById(R.id.article_prefix).setFocusable(editable);
            rowView.findViewById(R.id.article_number).setFocusable(editable);
            rowView.findViewById(R.id.material).setFocusable(editable);
            rowView.findViewById(R.id.color).setFocusable(editable);
            rowView.findViewById(R.id.length).setFocusable(editable);
            rowView.findViewById(R.id.height).setFocusable(editable);
            rowView.findViewById(R.id.size).setFocusable(editable);
            rowView.findViewById(R.id.surface).setFocusable(editable);
            rowView.findViewById(R.id.stone).setFocusable(editable);
            rowView.findViewById(R.id.engraving_text).setFocusable(editable);
            rowView.findViewById(R.id.engraving_type).setFocusable(editable);
            rowView.findViewById(R.id.engraving_font).setFocusable(editable);
            rowView.findViewById(R.id.engraving_cost).setFocusable(editable);
            rowView.findViewById(R.id.price).setFocusable(editable);
            rowView.findViewById(R.id.remark).setFocusable(editable);
            //hide remark if empty
            if (objects.get(position)[17].equals("")) {
                rowView.findViewById(R.id.remark).setVisibility(View.GONE);
            }
            rowView.findViewById(R.id.fingerprint).setFocusable(editable);
            rowView.findViewById(R.id.microtext).setFocusable(editable);
            rowView.findViewById(R.id.fingerprint).setClickable(editable);
            rowView.findViewById(R.id.microtext).setClickable(editable);
        }


        ImageView edit = rowView.findViewById(R.id.edit_btn);
        ImageView takePic = rowView.findViewById(R.id.takepic_btn);
        ImageView viewPic = rowView.findViewById(R.id.viewpic_btn);

        edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = context.getLayoutInflater();
                builder.setTitle(context.getString(R.string.ord_cmp));
                final View view = inflater.inflate(R.layout.line_order_component, null);
                view.findViewById(R.id.edit_btn).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.takepic_btn).setVisibility(View.GONE);
                view.findViewById(R.id.viewpic_btn).setVisibility(View.GONE);
                ((EditText) view.findViewById(R.id.article_type)).setText(objects.get(position)[2]);
                ((EditText) view.findViewById(R.id.article_prefix)).setText(objects.get(position)[3]);
                ((EditText) view.findViewById(R.id.article_number)).setText(objects.get(position)[4]);
                ((EditText) view.findViewById(R.id.catalog_code)).setText(objects.get(position)[27]);
                ((EditText) view.findViewById(R.id.material)).setText(objects.get(position)[5]);
                ((EditText) view.findViewById(R.id.color)).setText(objects.get(position)[6]);
                ((EditText) view.findViewById(R.id.length)).setText(objects.get(position)[7]);
                ((EditText) view.findViewById(R.id.height)).setText(objects.get(position)[8]);
                ((EditText) view.findViewById(R.id.size)).setText(objects.get(position)[9]);
                ((EditText) view.findViewById(R.id.surface)).setText(objects.get(position)[10]);
                ((EditText) view.findViewById(R.id.stone)).setText(objects.get(position)[11]);
                ((EditText) view.findViewById(R.id.engraving_text)).setText(objects.get(position)[12]);
                ((EditText) view.findViewById(R.id.engraving_type)).setText(objects.get(position)[13]);
                ((EditText) view.findViewById(R.id.engraving_font)).setText(objects.get(position)[14]);
                ((EditText) view.findViewById(R.id.engraving_cost)).setText(objects.get(position)[15]);
                ((EditText) view.findViewById(R.id.price)).setText(objects.get(position)[16]);
                ((EditText) view.findViewById(R.id.remark)).setText(objects.get(position)[17]);
                ((CheckBox) view.findViewById(R.id.fingerprint)).setChecked(objects.get(position)[25].equals("1"));
                ((CheckBox) view.findViewById(R.id.microtext)).setChecked(objects.get(position)[26].equals("1"));

                view.findViewById(R.id.inventory_detail_btn).setVisibility(View.GONE);
                view.findViewById(R.id.inventory_detail_stock_btn).setVisibility(View.GONE);
                view.findViewById(R.id.search_btn).setVisibility(View.GONE);
                view.findViewById(R.id.confirm_btn).setVisibility(View.GONE);

                ((EditTextDropdown) view.findViewById(R.id.article_type)).setList(Utils.getSetSetting(
                        Utils.ARTICLE_TYPE));
                ((EditTextDropdown) view.findViewById(R.id.engraving_type)).setList(Utils.getSetSetting(
                        Utils.ENGRAVE_TYPE));
                ((EditTextDropdown) view.findViewById(R.id.material)).setList(Utils.getSetSetting(
                        Utils.MATERIAL_TYPE));
                ((EditTextDropdown) view.findViewById(R.id.surface)).setList(Utils.getSetSetting(
                        Utils.SURFACE));
                ((EditTextDropdown) view.findViewById(R.id.color)).setList(Utils.getSetSetting(
                        Utils.COLOR_TYPE));

                ((EditTextDropdownCustomFont) view.findViewById(R.id.engraving_font)).setAdapter(
                        new ArrayAdapterCustomFonts(getContext(),
                                ((EditText) view.findViewById(R.id.engraving_text)).getText().toString()));
                final TextView enText = view
                        .findViewById(R.id.engraving_text);
                enText.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus && !enText.getText().toString().equals("")) {
                            ((EditTextDropdownCustomFont)
                                    view.findViewById(R.id.engraving_font)).getAdapter()
                                    .setText(((TextView) v).getText().toString());
                        }
                    }
                });
                builder.setView(view);
                builder.setCancelable(true);
                builder.setPositiveButton(context.getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] args = new String[22];
                        args[0] = ((EditText) view.findViewById(R.id.article_type)).getText().toString();
                        args[1] = ((EditText) view.findViewById(R.id.article_prefix)).getText().toString();
                        args[2] = ((EditText) view.findViewById(R.id.article_number)).getText().toString();
                        args[3] = ((EditText) view.findViewById(R.id.material)).getText().toString();
                        args[4] = ((EditText) view.findViewById(R.id.color)).getText().toString();
                        args[5] = ((EditText) view.findViewById(R.id.length)).getText().toString();
                        args[6] = ((EditText) view.findViewById(R.id.height)).getText().toString();
                        args[7] = ((EditText) view.findViewById(R.id.size)).getText().toString();
                        args[8] = ((EditText) view.findViewById(R.id.surface)).getText().toString();
                        args[9] = ((EditText) view.findViewById(R.id.stone)).getText().toString();
                        args[10] = ((EditText) view.findViewById(R.id.engraving_text)).getText().toString();
                        args[11] = ((EditText) view.findViewById(R.id.engraving_type)).getText().toString();
                        args[12] = ((EditText) view.findViewById(R.id.engraving_font)).getText().toString();
                        args[13] = ((EditText) view.findViewById(R.id.engraving_cost)).getText().toString();
                        args[14] = ((EditText) view.findViewById(R.id.price)).getText().toString();
                        args[15] = ((EditText) view.findViewById(R.id.remark)).getText().toString();
                        args[16] = objects.get(position)[0]; //component ID
                        //arg 17: update the price difference
                        args[17] = "" + (Double.parseDouble(args[13]) + Double.parseDouble(args[14])
                                - Double.parseDouble(objects.get(position)[15]) - Double.parseDouble(objects.get(position)[16]));
                        args[18] = objects.get(position)[1]; //order number
                        args[19] = ((CheckBox) view.findViewById(R.id.fingerprint)).isChecked() ? "1" : "0";
                        args[20] = ((CheckBox) view.findViewById(R.id.microtext)).isChecked() ? "1" : "0";
                        args[21] = ((EditText) view.findViewById(R.id.catalog_code)).getText().toString();
                        new UpdateOrderComp().execute(args);

                    }
                });

                builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        takePic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                Intent chooser = Intent.createChooser(intent, context.getText(R.string.select_photo));

                PackageManager pm = context.getPackageManager();
                if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{camera});
                }
                ((OrderDetailActivity) context).dispatchTakePictureIntent(Integer.parseInt(objects.get(position)[0]));

            }
        });

        viewPic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetImageTask().execute(objects.get(position)[0]);
            }
        });

        return rowView;
    }

    public class GetImageTask extends AsyncTask<String, Void, Boolean> {
        byte[] image;

        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            Query q = new Query("select * from OrderComponentPic "
                    + "where OrderComponentID = "
                    + args[0]);
            if (!q.execute()) {
                return false;
            }
            if (q.getRes().size() > 0) {
                image = Base64.decode(q.getRes().get(0).get("" + 1), Base64.DEFAULT);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(context,
                        context.getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = context.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_pic, null);
            ImageView img = view.findViewById(R.id.img);
            if (image != null) {
                Bitmap b = BitmapFactory.decodeByteArray(image, 0, image.length);
                img.setImageBitmap(b);
                img.setRotation(90f);
            }
            builder.setView(view);
            builder.create().show();

        }
    }


    class UpdateOrderComp extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            if (!Utils.isOnline()) {
                return false;
            }
            String query = "update OrderComponent set"
                    + " ArticleType = " + Utils.escape(args[0])
                    + " ,ArticlePrefix = " + Utils.escape(args[1])
                    + " ,ArticleNumber = " + Utils.escape(args[2])
                    + " ,CatalogCode = " + Utils.escape(args[21])
                    + " ,Material = " + Utils.escape(args[3])
                    + " ,Color = " + Utils.escape(args[4])
                    + " ,Length = " + Utils.escape(args[5])
                    + " ,Height = " + Utils.escape(args[6])
                    + " ,Size = " + Utils.escape(args[7])
                    + " ,Surface = " + Utils.escape(args[8])
                    + " ,Stone = " + Utils.escape(args[9])
                    + " ,EngravingText = " + Utils.escape(args[10])
                    + " ,EngravingType = " + Utils.escape(args[11])
                    + " ,EngravingFont = " + Utils.escape(args[12])
                    + " ,EngravingCost = " + args[13]
                    + " ,Price = " + args[14]
                    + " ,Remark = " + Utils.escape(args[15])
                    + " ,Fingerprint = " + args[19]
                    + " ,Microtext = " + args[20]
                    + " where ID =" + args[16];
            Query q = new Query(query);
            if (!q.execute()) {
                return false;
            }
            query = "update MainOrder set" +
                    " Total = Total +(" + args[17] + ")," +
                    " Remain = Remain +(" + args[17] + ")" +
                    " where OrderNumber =" + args[18];
            q = new Query(query);
            return q.execute();
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if (sucess) {
                context.recreate();
            } else {
                Toast.makeText(context,
                        context.getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getInventoryCode(String id) {
        if (!id.equals("")) {
            Query q = new Query("select InventoryCode from Inventory where ID=" + id);
            q.execute();
            if (q.getRes().size() > 0) {
                return q.getRes().get(0).get("InventoryCode");
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

}
