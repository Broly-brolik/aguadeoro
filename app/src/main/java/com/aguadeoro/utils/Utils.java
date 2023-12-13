package com.aguadeoro.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.MediaStore;


import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.aguadeoro.R;
import com.aguadeoro.activity.OrderDetailActivity;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;


public class Utils {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String WEDDING_DATE = "WeddingDate", WEDDING_DATE2 = "WeddingDate2",
            CUST_REL_ID = "CustomerRelationshipID", ID = "ID",
            CUST_NAME_1 = "CustomerName1", CUST_NAME_2 = "CustomerName2",
            CREATE_DATE = "CreatedDate", CUST_NO = "CustomerNumber",
            ORDER_TYPE = "OrderType", ORD_PURCHASE = "Vente", ORD_ORDER = "Commande",
            ORD_PREVIEW = "Preview", ORD_REPAIR = "Repair",
            ORDER_NO = "OrderNumber", CUST_NAME = "CustomerName",
            ADDR = "Address", EMAIL = "Email", TEL = "Tel",
            HISTORY_DATE = "HistoryDate", REMARK = "Remark", SUPP_ORD_STT = "SupplierOrderStatus",
            ORD_STT = "OrderStatus", ORD_DT = "OrderDate", INV_STT = "InventoryStatus",
            DEADLINE = "Deadline", TOTAL = "Total", REMAIN = "Remain",
            CALLER = "Caller", DISCOUNT = "Discount", PAID = "Paid",
            ENRY_DT = "EntryDate", PAY_MODE = "PaymentMode", AMOUNT = "Amount",
            STATUS = "Status", INST = "Instruction", RECIPIENT = "Recipient",
            ENGRAVE_TYPE = "EngravingType", PAYMENT_METHOD = "PaymentMethod",
            CREATED_DATE = "CreatedDate", STEP = "Step",
            ORDER_STEP = "OrderStep", INST_CODE = "InstructionCode",
            SUPPLIER = "Supplier", ARTICLE_TYPE = "ArticleType", ARTICLE_NO = "ArticleNumber",
            MATERIAL_TYPE = "MaterialType", SURFACE = "SurfaceType",
            CUSTOMER_TYPE = "CustomerType", TYPE = "Type",
            COLOR_TYPE = "ColorType", SUP_ORD_NO = "SupplierOrderNumber",
            COMP_ID = "CompID", ORD_COMP_ID = "OrderComponentID",
    //Loc Request 1 2018.04.17 BEGIN
    COMP_INFO = "CompInfo",
    //Loc Request 1 2018.04.17 END
    NON = "-", SELLER = "Seller", ADO = "ADO",
            SEND_INVOICE_JEWEL = "SendInvoiceJewel", SEND_INVOICE_ALLIANCES = "SendInvoiceAlliances",
            SEND_INVOICE_BAGUE = "SendInvoiceBague", SEND_ORDER_CLOSED = "SendFinal",
            SEND_ORDER_READY = "SendReady",
            DESCRIPTION = "Description", PRICE = "Price", QUANTITY = "Quantity",
            IMAGE = "Image", CHECKED_BY = "CheckedBy", CHECKED_ON = "CheckedOn",
            INTEREST = "Interest";
    public static final String ENTRY_DATE = "EntryDate", INVT_CODE = "InventoryCode",
            CATALOG_CODE = "CatalogCode", CODE = "Code", MARK = "Mark",
            CATEGORY = "Category", WIDTH = "Width", HEIGHT = "Height",
            MATERIAL = "Material", COLOR = "Color", WEIGHT = "Weight",
            SIZE = "Size", STONE = "Stone", STONEQUANTITY = "StoneQuantity",
            STONETYPE = "StoneType", STONESIZE = "StoneSize", CARAT = "Carat",
            STONECOLOR = "StoneColor", STONECLARITY = "StoneClarity",
            STONECUT = "StoneCut", STONEPOLISH = "StonePolish", STONESYMETRY = "StoneSymetry",
            LAB = "LAB", CERTIFICATE = "Certificate", COST = "Cost", TITLE = "Title",
            HISTTYPE_ORDER = "Order", HISTTYPE_REMARK = "Remark",
            HISTTYPE_ACTION = "Customer Action", HISTTYPE_Visit = "Visit";
    public static final String TAKE_OUT = "Sorti", RETURN = "Rendu", SOLD = "Vendu", STOCK = "Stock";
    public static final int SEND_THANKS_CODE = 1, SEND_ORDER_CONF_CODE = 2,
            SEND_ORDER_READY_CODE = 3, SEND_SUPPLIER_CODE = 4, PRINT_INVOICE_CODE = 5,
            TAKE_INVENTORY_PIC = 6, SEND_ORDER_CLOSE_CODE = 7, SEND_OFFER_CONF_CODE = 8;

    public static final String[] FONTS = {"Imprimerie", "English", "Bradley Hand",
            "Freestyle Script", "Lucida Handwriting", "Script MT Bold",
            "Vladimir Script", "Champagne & Limousines"};

    static SharedPreferences settings;
    static SimpleDateFormat shortDisplayFormat, longFormat, shortInsertFormat, longInsertFormat;
    static Activity context;
    static String THANKYOU = "Nous vous remercions et félicitons d’avoir acheté vos alliances chez Agua de Oro.",
            WARRANTY = "Agua de Oro accorde pendant trois ans, à partir de la date d’achat, une garantie sur les matières, le travail et les pierres. La garantie ne couvre pas les dommages consécutifs à un accident (choc), au manque de soin ou à l’usure normale des bijoux.",
            WARRANTY_ALLIANCE = "Agua de Oro vous offre un service spécial pour vos alliances ; pendant les deux années suivant la date d’achat, nous assurerons gratuitement le rafraîchissement de vos alliances pour raviver leur éclat et nous contrôleront également la monture des pierres. Nous vous recommandons de faire soumettre vos alliances à un contrôle régulier qui, suivant l’usage que vous en faites, devra être effectué tous les un à trois ans",
            FOOTER = "AGUA DE ORO Sarl, Grand-Rue 21, 1204 Genève, SALES@AGUADEORO.CH";


    public static void initializeUtils(Activity c) {
        settings = PreferenceManager.getDefaultSharedPreferences(c
                .getApplicationContext());
        context = c;
        shortDisplayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        shortInsertFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.FRENCH);
        longInsertFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.FRENCH);
        longFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.FRENCH);

        //bypass the uri exposure for SDK>24
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    public static void deleteUserFromSession() {
        Editor editor = settings.edit();
        editor.remove("username");
        editor.commit();
    }

    public static void setLocale(Activity act, String lang) {

        Locale myLocale = new Locale(lang);
        Resources res = act.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(act, act.getClass());
        act.startActivity(refresh);
    }

    @Deprecated
    public static void saveSetting(String setting, Object value) {
        Editor editor = settings.edit();
        if (value instanceof String) {
            editor.putString(setting, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(setting, (Boolean) value);
        } else if (value instanceof Set<?>) {
            editor.putStringSet(setting, (Set<String>) value);
        }
        editor.commit();
    }

    public static void syncSettingFromDatabase() {
        Editor editor = settings.edit();
        Query query = new Query("select distinct Type from OptionValues");
        if (!query.execute()) {
            Toast.makeText(context,
                    context.getString(R.string.error_retrieving_data),
                    Toast.LENGTH_LONG).show();
            return;
        }
        ArrayList<Map<String, String>> result = query.getRes();
        for (int i = 0; i < result.size(); i++) {
            query = new Query(
                    "select OptionValue from OptionValues where Type = '"
                            + result.get(i).get("0")
                            + "' order by OptionKey asc");
            if (!query.execute()) {
                Toast.makeText(context,
                        context.getString(R.string.error_retrieving_data),
                        Toast.LENGTH_LONG).show();
                return;
            }
            ArrayList<Map<String, String>> r = query.getRes();
            LinkedHashSet<String> option = new LinkedHashSet<String>();
            for (int j = 0; j < r.size(); j++) {
                String value = "" + j + "|" + r.get(j).get("0");
                option.add(value);

            }
            editor.remove(result.get(i).get("0"));
            editor.putStringSet(result.get(i).get("0"), option);
        }

        query = new Query("select * from Supplier");
        if (!query.execute()) {
            Toast.makeText(context,
                    context.getString(R.string.error_retrieving_data),
                    Toast.LENGTH_LONG).show();
            return;
        }
        result = query.getRes();
        for (int i = 0; i < result.size(); i++) {
            LinkedHashSet<String> option = new LinkedHashSet<String>();
            option.add("1|" + result.get(i).get("2"));//email
            option.add("2|" + result.get(i).get("3"));//person in charge
            option.add("3|" + result.get(i).get("4"));//supplier address
            option.add("4|" + result.get(i).get("5"));//supplier email
            editor.remove(result.get(i).get("1"));
            editor.putStringSet(result.get(i).get("1"), option);
        }

        query = new Query("select * from Email");
        if (!query.execute()) {
            Toast.makeText(context,
                    context.getString(R.string.error_retrieving_data),
                    Toast.LENGTH_LONG).show();
            return;
        }
        result = query.getRes();
        for (int i = 0; i < result.size(); i++) {
            editor.remove(result.get(i).get("1"));
            editor.putString(result.get(i).get("1"), result.get(i).get("2"));
        }

        editor.commit();
        Toast.makeText(context, context.getString(R.string.finis_sync),
                Toast.LENGTH_SHORT).show();
    }

    @Deprecated
    public static int generateSupplierOrderNumber(String compID) {
        Query query = new Query(
                "select count(*) from SupplierOrderMain where OrderComponentID ="
                        + compID);
        query.execute();
        ArrayList<Map<String, String>> result = query.getRes();
        if (result.size() < 1) {
            return 1;
        }
        return Integer.parseInt(result.get(0).get("0")) + 1;

    }

    @Deprecated
    public static void updateSupplierOrderNumber(String supplier, int num) {
        Query query = new Query("update Supplier set LastOrder = " + num
                + " where SupplierName =" + escape(supplier));
        query.execute();
    }

    public static String getStringSetting(String setting) {
        return settings.getString(setting, null);
    }

    public static Double getNumberSetting(String setting) {
        return Double.valueOf(settings.getString(setting, null));
    }


    public static String[] getSetSetting(String setting) {
        List<String> values = Arrays.asList(settings.getStringSet(setting,
                new HashSet<String>()).toArray(new String[0]));
        Collections.sort(values);
        String[] cleanValues = new String[values.size()];
        for (int i = 0; i < cleanValues.length; i++) {
            cleanValues[i] = values.get(i).substring(
                    values.get(i).indexOf("|") + 1);
        }
        return cleanValues;
    }

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String shortDateFromDB(String date) {
        Date d;
        try {
            d = longFormat.parse(date);
        } catch (Exception e) {
            return date;
        }
        return shortDisplayFormat.format(d);
    }

    public static String shortDateForInsert(Date date) {
        return shortInsertFormat.format(date);
    }

    public static String shortDateForDisplay(Date date) {
        return shortDisplayFormat.format(date);
    }

    public static String shortDateForInsert(String date_in_dd_MM_YYYY) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy",
                    Locale.FRENCH);
            return shortInsertFormat.format((df.parse(date_in_dd_MM_YYYY)));
        } catch (ParseException e) {
            return null;
        }
    }

    public static String longDateForInsert(String date_in_dd_MM_YYYY_HH_MM) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm",
                    Locale.FRENCH);
            return longInsertFormat.format((df.parse(date_in_dd_MM_YYYY_HH_MM)));
        } catch (ParseException e) {
            return null;
        }
    }


    public static Date dateFromStringyyyyMMdd(String date) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd",
                    Locale.FRENCH);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date dateFromStringddMMyyyy(String date) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy",
                    Locale.FRENCH);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private static void askForPermission(int type) {
        if (type == 1) {
            context.requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR}, 2);

        } else if (type == 2) {
            context.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            context.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);

        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void insertEvent(String date, String title, String desc, boolean yearly) {
        if (context.checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            askForPermission(1);
        }
        Log.d("Insert Calendar", date + title + desc);
        Cursor cursor;
        cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"),
                new String[]{"_id", "calendar_displayName"}, null, null, null);
        int id = 8;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String[] calendarNames = new String[cursor.getCount()];
            // Get calendars id
            int[] calendarIds = new int[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++) {
                calendarIds[i] = cursor.getInt(0);
                calendarNames[i] = cursor.getString(1);
                Log.d("Calendar Name : ", calendarNames[i] + " " + calendarIds[i]);
                if (calendarNames[i].equals("AGUADEORO-CRM")) {
                    id = calendarIds[i];
                }
                cursor.moveToNext();
            }
        } else {
            Log.d("calendarError", "No calendar found in the device");
        }

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.ALL_DAY, 1);
        values.put(CalendarContract.Events.DURATION, "P1D");// 1 day
        values.put(CalendarContract.Events.DTSTART, dateFromStringyyyyMMdd(date).getTime());
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, desc);
        values.put(CalendarContract.Events.CALENDAR_ID, id);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put(CalendarContract.Events.AVAILABILITY, 1);
        if (yearly) {
            values.put(CalendarContract.Events.RRULE, "FREQ=YEARLY;INTERVAL=1");
        }
        if (context.checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            askForPermission(1);

        }
        Uri event = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        Log.d("After insert Calendar", values.toString());

    }


    public static String escape(String s) {
        return DatabaseUtils.sqlEscapeString(s);
    }


    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String getPicPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cur = context.managedQuery(uri, projection, null, null, null);
        if (cur != null) {
            int column_index = cur
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cur.moveToFirst();
            return cur.getString(column_index);
        }
        return uri.getPath();
    }

    public static void printNote(final Activity context, String lang) {
        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            askForPermission(2);
        }
        LayoutInflater inflater = context.getLayoutInflater();
        final View screen = inflater.inflate(R.layout.dialog_thankyou, null);
        String msgTemplate = getStringSetting("thankyou_template_" + lang);
        String name1 = ((EditText) context.findViewById(R.id.name1)).getText()
                .toString();
        String name2 = ((EditText) context.findViewById(R.id.name2)).getText()
                .toString();
        final String email = ((EditText) context.findViewById(R.id.email))
                .getText().toString();
        final String message = msgTemplate.replace("[1]", name1).replace("[2]",
                name2);
        ((TextView) screen.findViewById(R.id.text)).setText(message);
        screen.findViewById(R.id.print_area).setBackgroundColor(Color.WHITE);
        screen.findViewById(R.id.print_area).setDrawingCacheEnabled(true);
        Dialog d = new AlertDialog.Builder(context).setView(screen)
                .setPositiveButton("Print", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bitmap b = screen.findViewById(R.id.print_area)
                                .getDrawingCache(true);
                        try {
                            Rectangle pagesize = new Rectangle(Utilities.inchesToPoints(4), Utilities.inchesToPoints(6));
                            Document document = new Document(pagesize, 2f,
                                    2f, 2f, 2f);
                            File pdfFile = new File(Environment
                                    .getExternalStorageDirectory(),
                                    "thankyou.pdf");
                            PdfWriter.getInstance(document,
                                    new FileOutputStream(pdfFile));
                            document.open();
                            PdfPTable table = new PdfPTable(1);
                            table.setWidthPercentage(100);
                            table.setSpacingBefore(0);
                            table.setSpacingAfter(0);
                            table.getDefaultCell().setBorder(0);
                            table.getDefaultCell().setNoWrap(true);
                            table.getDefaultCell().setVerticalAlignment(
                                    Element.ALIGN_CENTER);
                            table.getDefaultCell().setHorizontalAlignment(
                                    Element.ALIGN_JUSTIFIED);
                            table.addCell(message);
                            //InputStream in = new FileInputStream(f);
                            InputStream in = context.getAssets().open("logo.jpg");
                            byte[] logo = IOUtils.toByteArray(in);
                            Image l = Image.getInstance(logo);
                            l.scalePercent(17);
                            PdfPCell cell = new PdfPCell(l);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setMinimumHeight(100f);
                            table.addCell(cell);
                            document.add(table);
                            document.close();

                            Intent printIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                            printIntent.putExtra(Intent.EXTRA_TITLE,
                                    "Thank you");
                            printIntent.putExtra(Intent.EXTRA_SUBJECT,
                                    "Thank you");
                            printIntent.setType("application/pdf");
                            printIntent.putExtra(Intent.EXTRA_EMAIL,
                                    new String[]{email});
                            if (!pdfFile.exists() || !pdfFile.canRead()) {
                                Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show();
                            }

                            Uri uri = FileProvider.getUriForFile(context, "com.aguadeoro.android.fileprovider", pdfFile);
                            printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            printIntent.setDataAndType(uri, context.getContentResolver().getType(uri));
                            Log.d("test uri", "" + uri);
                            printIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            printIntent.putExtra(Intent.EXTRA_TEXT, message);
                            context.startActivityForResult(printIntent, SEND_THANKS_CODE);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error printing",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error printing",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }).create();
        d.show();
    }


    public static void printCustomerInvoice(String orderType,
                                            String[] orderInfo, ArrayList<String[]> orderComps,
                                            ArrayList<String[]> payHist, Activity context, String action, String lang) {

        try {
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                askForPermission(2);
            }
            verifyStoragePermissions(Utils.context);
            ArrayList<Map<String, String>> res;
            String query = "";
            if (lang.equals("en")) {
                switch (action) {
                    case SEND_INVOICE_ALLIANCES:
                        query = "select TextField from E_Textes where Key ='invoice_all_en_0' or Key='invoice_all_en_1' or Key='invoice_all_en_1.1' or Key='invoice_all_en_2' or Key='invoice_all_en_2.1' or Key='invoice_all_en_3' or Key='invoice_all_en_3.1'";
                        break;
                    case SEND_INVOICE_BAGUE:
                        query = "select TextField from E_Textes where Key ='invoice_bf_en_0' or Key='invoice_bf_en_1' or Key='invoice_bf_en_1.1' or Key='invoice_bf_en_2' or Key='invoice_bf_en_2.1' or Key='invoice_bf_en_3' or Key='invoice_bf_en_3.1'";
                        break;
                    case SEND_INVOICE_JEWEL:
                        query = "select TextField from E_Textes where Key ='invoice_bij_en_0' or Key='invoice_bij_en_1' or Key='invoice_bij_en_1.1' or Key='invoice_bij_en_2' or Key='invoice_bij_en_2.1' or Key='invoice_bij_en_3' or Key='invoice_bij_en_3.1'";
                        break;
                }
                Query q = new Query(query);
                q.execute();
                res = q.getRes();

                for (int j = 0; j < res.size(); j++) {
                    Log.d("testText", j + " " + res.get(j).get("TextField"));
                }
            } else {
                switch (action) {
                    case SEND_INVOICE_ALLIANCES:
                        query = "select TextField from E_Textes where Key ='invoice_all_fr_0' or Key='invoice_all_fr_1' or Key='invoice_all_fr_1.1' or Key='invoice_all_fr_2' or Key='invoice_all_fr_2.1' or Key='invoice_all_fr_3' or Key='invoice_all_fr_3.1'";
                        break;
                    case SEND_INVOICE_BAGUE:
                        query = "select TextField from E_Textes where Key ='invoice_bf_fr_0' or Key='invoice_bf_fr_1' or Key='invoice_bf_fr_1.1' or Key='invoice_bf_fr_2' or Key='invoice_bf_fr_2.1' or Key='invoice_bf_fr_3' or Key='invoice_bf_fr_3.1'";
                        break;
                    case SEND_INVOICE_JEWEL:
                        query = "select TextField from E_Textes where Key ='invoice_bij_fr_0' or Key='invoice_bij_fr_1' or Key='invoice_bij_fr_1.1' or Key='invoice_bij_fr_2' or Key='invoice_bij_fr_2.1' or Key='invoice_bij_fr_3' or Key='invoice_bij_fr_3.1'";
                        break;
                }
                Query q = new Query(query);
                q.execute();
                res = q.getRes();
                for (int j = 0; j < res.size(); j++) {
                    Log.d("testText", j + " " + res.get(j).get("TextField"));
                }
            }
            BaseColor darkBlue = new CMYKColor(1, 0.476f, 0, 0.431f);
            Font font14U = new Font(FontFamily.TIMES_ROMAN, 14, Font.UNDERLINE);
            Font font10B = new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD);
            Font font10BBlue = new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD);
            font10BBlue.setColor(darkBlue);
            Font font10 = new Font(FontFamily.TIMES_ROMAN, 10);
            Font font11 = new Font(FontFamily.TIMES_ROMAN, 11);
            Font font9U = new Font(FontFamily.TIMES_ROMAN, 9, Font.UNDERLINE);
            Font font9 = new Font(FontFamily.TIMES_ROMAN, 9);
            Font font9ub = new Font(FontFamily.TIMES_ROMAN, 9, Font.UNDERLINE);
            BaseColor lightBlue = new CMYKColor(1, 0.476f, 0, 0.3f);
            font9ub.setColor(lightBlue);
            Font font9b = new Font(FontFamily.TIMES_ROMAN, 9);
            font9b.setColor(darkBlue);
            Font font10I = new Font(FontFamily.TIMES_ROMAN, 10, Font.ITALIC);
            Font font10BU = new Font(FontFamily.TIMES_ROMAN, 10,
                    Font.BOLDITALIC);
            Font font11BU = new Font(FontFamily.TIMES_ROMAN, 11,
                    Font.BOLDITALIC);


            Document document = new Document(PageSize.A4, 40f, 40f, 0f, 10f);
            new File(Environment.getExternalStorageDirectory()
                    + "/01_invoices/").mkdir();
            File pdfFile = new File(Environment.getExternalStorageDirectory()
                    + "/01_invoices/", "Invoice Order " + orderInfo[12] + " "
                    + orderInfo[0] + ".pdf");
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setSpacingBefore(0);
            table.setSpacingAfter(0);
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setNoWrap(false);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.getDefaultCell().setMinimumHeight(15f);
            table.setWidths(new float[]{25f, 150f, 90f, 68f, 38f, 164f, 123f});

            // logo
            InputStream in = context.getAssets().open("logo2.png");
            byte[] logo = IOUtils.toByteArray(in);
            Image l = Image.getInstance(logo);
            l.scalePercent(30);
            document.add(new Paragraph("\n"));
            PdfPCell cell = new PdfPCell(l);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(7);
            cell.setBorder(0);
            table.addCell(cell);

            // line 1: facture
            cell = new PdfPCell(new Phrase("Facture - Invoice", font14U));
            cell.getPhrase().getFont().setColor(BaseColor.BLUE);
            cell.setColspan(7);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(orderComps.get(0)[1], font9));
            cell.setBorder(0);
            cell.setColspan(7);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);

            // line 2 date
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            cell = new PdfPCell();
            cell.addElement(new Phrase("Date commande:", font10BBlue));
            cell.addElement(new Phrase("Order date:", font9b));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.addCell(new Phrase(orderInfo[7], font11));

            // line 3 date also
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            cell = new PdfPCell();
            cell.addElement(new Phrase("Date facture:", font10BBlue));
            cell.addElement(new Phrase("Invoice date:", font9b));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.addCell(new Phrase(orderInfo[6], font11));

            // line 4: Destinataire
            cell = new PdfPCell();
            cell.addElement(new Phrase("Destinataire:", font9ub));
            cell.addElement(new Phrase("Recipient:", font9ub));
            cell.setColspan(7);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.addCell("");
            cell = new PdfPCell(new Phrase(orderInfo[0], font10B));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.completeRow();
            table.addCell("");
            cell = new PdfPCell(new Phrase(orderInfo[1], font10B));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.completeRow();

            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");

            // line 5 date marriage
            cell = new PdfPCell();
            cell.addElement(new Phrase("Date Mariage:", font9ub));
            cell.addElement(new Phrase("Wedding date:", font9ub));
            cell.setColspan(7);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.addCell("");
            cell = new PdfPCell(new Phrase(orderInfo[5], font11));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.completeRow();

            // TVA line
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            cell = new PdfPCell();
            cell.addElement(new Phrase("Numéro TVA", font10));
            cell.addElement(new Phrase("VAT number", font9));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.addCell(new Phrase(getStringSetting("tva_number"), font10));

            // black line
            table.addCell("");
            cell = new PdfPCell(new Phrase(""));
            cell.setColspan(6);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1f);
            table.addCell(cell);

            // headers
            table.addCell("");
            cell = new PdfPCell();
            cell.addElement(new Phrase("Article", font10BBlue));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("Matière", font10BBlue));
            cell.addElement(new Phrase("Material", font9b));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("Couleur", font10BBlue));
            cell.addElement(new Phrase("Colour", font9b));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("Taille", font10BBlue));
            cell.addElement(new Phrase("Size", font9b));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("Pierres", font10BBlue));
            cell.addElement(new Phrase("Stones", font9b));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("Prix CHF", font10BBlue));
            cell.addElement(new Phrase("Price CHF", font9b));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);

            // data
            double engraveCost = 0;
            for (int i = 0; i < orderComps.size(); i++) {
                String[] comp = orderComps.get(i);

                table.addCell("");
                String article = comp[2] + " " + comp[3] + " " + comp[4];
                cell = new PdfPCell(new Phrase(article, font10));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(comp[5], font10));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(comp[6], font10));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(comp[9], font10));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(comp[11], font10));
                cell.setBorder(0);
                cell.setBorderWidthBottom(1f);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(comp[16], font10));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);
                engraveCost += Double.parseDouble(comp[15]);
            }

            // engraving cost
            table.addCell("");
            cell = new PdfPCell(new Phrase(""));
            cell.setColspan(4);
            cell.setBorder(0);
            cell.setBorderWidthBottom(0.5f);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Supplement Gravures / Engraving Supplement", font10));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorderWidthBottom(0.5f);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + engraveCost, font10));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorderWidthBottom(0.5f);
            table.addCell(cell);

            // totals
            table.addCell("");
            cell = new PdfPCell(new Phrase(""));
            cell.setColspan(4);
            cell.setBorder(0);
            cell.setBorderWidthBottom(0.5f);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Total", font10BBlue));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorderWidthBottom(0.5f);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(orderInfo[8], font10B));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorderWidthBottom(0.5f);
            table.addCell(cell);

            double total = Double.parseDouble(orderInfo[8]);
            double totalAfterDis = total
                    - Double.parseDouble(orderInfo[9]);
            //total after discount, only if discount > 0
            if (total != totalAfterDis) {
                table.addCell("");
                cell = new PdfPCell(new Phrase(""));
                cell.setColspan(4);
                cell.setBorder(0);
                cell.setBorderWidthBottom(0.5f);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Total après rabais", font10BBlue));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setBorderWidthBottom(0.5f);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("" + totalAfterDis, font10B));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setBorderWidthBottom(0.5f);
                table.addCell(cell);
            }

            //TVA line according to order type
            Double tvaRate = getNumberSetting("tva_rate");
            table.addCell("");
            cell = new PdfPCell(new Phrase(""));
            cell.setColspan(4);
            cell.setBorder(0);
            cell.setBorderWidthBottom(0.5f);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Y compris TVA/ " + tvaRate + "%\nVAT included", font10));
            if (orderInfo[13].equals("0")) {
                cell = new PdfPCell(new Phrase("TVA déduite " + tvaRate + "%", font10));
            }
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorderWidthBottom(0.5f);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + round(totalAfterDis * (tvaRate / 100) / (1 + tvaRate / 100), 1), font10B));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorderWidthBottom(0.5f);
            table.addCell(cell);

            // acompte
            for (int i = 0; i < payHist.size(); i++) {
                if (payHist.get(i)[2].equals("") || payHist.get(i)[3].equals(Utils.DISCOUNT))
                    continue;
                table.addCell("");
                cell = new PdfPCell(new Phrase(""));
                cell.setColspan(4);
                cell.setBorder(0);
                cell.setBorderWidthBottom(1f);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Acompte /\nInstallment"
                        + Utils.shortDateFromDB(payHist.get(i)[0]), font10));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setBorderWidthBottom(1f);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(payHist.get(i)[2], font10));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setBorderWidthBottom(1f);
                table.addCell(cell);
            }

            // solde
            table.addCell("");
            cell = new PdfPCell(new Phrase(""));
            cell.setColspan(4);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1f);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Solde a payer à la livraison/\nTo pay upon delivery",
                    font10I));
            cell.getPhrase().getFont().setColor(BaseColor.BLUE);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorderWidthBottom(1f);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(orderInfo[11], font10B));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorderWidthBottom(1f);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(""));
            cell.setColspan(7);
            cell.setMinimumHeight(70f);
            cell.setBorder(0);
            table.addCell(cell);

            if (res.size() > 0) {
                if (action.equals(SEND_INVOICE_ALLIANCES)) {
                    if (lang.equals("en")) {
                        if (!(Objects.equals(res.get(3).get("TextField"), ""))) {
                            cell = new PdfPCell(
                                    new Phrase(res.get(3).get("TextField"), font11));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(5).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(5).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(1).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(1).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }
                        if (!(res.get(6).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(6).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(4).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(4).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }
                        if (!(res.get(0).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(0).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(2).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(2).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }

                    } else {
                        if (!(Objects.equals(res.get(4).get("TextField"), ""))) {
                            cell = new PdfPCell(
                                    new Phrase(res.get(4).get("TextField"), font11));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(5).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(5).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(6).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(6).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }
                        if (!(res.get(0).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(0).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(2).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(2).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }
                        if (!(res.get(1).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(1).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(3).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(3).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }

                    }
                } else if (action.equals(SEND_INVOICE_BAGUE)) {
                    if (lang.equals("en")) {
                        if (!(Objects.equals(res.get(3).get("TextField"), ""))) {
                            cell = new PdfPCell(
                                    new Phrase(res.get(3).get("TextField"), font11));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(4).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(4).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(5).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(5).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }
                        if (!(res.get(6).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(6).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(0).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(0).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }
                        if (!(res.get(1).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(1).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(2).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(2).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }

                    } else {
                        if (!(Objects.equals(res.get(4).get("TextField"), ""))) {
                            cell = new PdfPCell(
                                    new Phrase(res.get(4).get("TextField"), font11));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(5).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(5).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(6).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(6).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }
                        if (!(res.get(0).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(0).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(2).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(2).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }
                        if (!(res.get(1).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(1).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(3).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(3).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }

                    }
                } else if (action.equals(SEND_INVOICE_JEWEL)) {
                    if (lang.equals("en")) {
                        if (!(Objects.equals(res.get(3).get("TextField"), ""))) {
                            cell = new PdfPCell(
                                    new Phrase(res.get(3).get("TextField"), font11));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(4).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(4).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(5).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(5).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }
                        if (!(res.get(6).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(6).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(0).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(0).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }
                        if (!(res.get(1).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(1).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(2).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(2).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }

                    } else {
                        if (!(Objects.equals(res.get(4).get("TextField"), ""))) {
                            cell = new PdfPCell(
                                    new Phrase(res.get(4).get("TextField"), font11));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(5).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(5).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(6).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(6).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }
                        if (!(res.get(0).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(0).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(2).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(2).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }
                        if (!(res.get(1).get("TextField").equals(""))) {

                            cell = new PdfPCell(new Phrase(res.get(1).get("TextField"), font11BU));
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);

                            table.addCell("");
                            table.completeRow();
                        }
                        if (!(res.get(3).get("TextField").equals(""))) {

                            cell = new PdfPCell(
                                    new Phrase(res.get(3).get("TextField"), font11));
                            cell.setNoWrap(false);
                            cell.setColspan(7);
                            cell.setBorder(0);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            table.addCell(cell);
                        }

                    }
                }

            }
            // footer
            table.addCell("");
            table.completeRow();
            table.addCell("");
            table.completeRow();
            table.addCell("");
            table.completeRow();
            table.addCell("");
            table.completeRow();
            table.addCell("");
            table.completeRow();
            table.addCell("");
            table.completeRow();
            table.addCell("");
            table.completeRow();
            table.addCell("");
            table.completeRow();
            cell = new PdfPCell(
                    new Phrase(FOOTER, font10));
            cell.setColspan(7);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            document.add(table);
            document.close();

            String subject = "", body = "";
            int actionCode = 0;
            Intent printIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            switch (action) {
                case SEND_INVOICE_JEWEL: {
                    subject = Utils.getStringSetting("email_invoice_subject_" + lang)
                            + " " + orderInfo[12];
                    body = Utils.getStringSetting("email_invoice_body_jewel_" + lang)
                            .replace("[1]", orderInfo[0]).replace("[2]", orderInfo[1]);
                    actionCode = PRINT_INVOICE_CODE;
                    String[] bcc = {"finance@aguadeoro.ch"};
                    printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                    break;
                }
                case SEND_INVOICE_ALLIANCES: {
                    subject = Utils.getStringSetting("email_invoice_subject_" + lang)
                            + " " + orderInfo[12];
                    body = Utils.getStringSetting("email_invoice_body_alliances_" + lang)
                            .replace("[1]", orderInfo[0]).replace("[2]", orderInfo[1]);
                    actionCode = PRINT_INVOICE_CODE;
                    String[] bcc = {"finance@aguadeoro.ch"};
                    printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                    break;
                }
                case SEND_INVOICE_BAGUE: {
                    subject = Utils.getStringSetting("email_invoice_subject_" + lang)
                            + " " + orderInfo[12];
                    body = Utils.getStringSetting("email_invoice_body_bague_" + lang)
                            .replace("[1]", orderInfo[0]).replace("[2]", orderInfo[1]);
                    actionCode = PRINT_INVOICE_CODE;
                    String[] bcc = {"finance@aguadeoro.ch"};
                    printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                    break;
                }
                case SEND_ORDER_CLOSED: {
                    subject = Utils.getStringSetting("email_order_closed_subject_" + lang) + " " + orderInfo[12];
                    body = Utils.getStringSetting("email_order_closed_body_" + lang)
                            .replace("[1]", orderInfo[0]).replace("[2]", orderInfo[1]);
                    actionCode = SEND_ORDER_CLOSE_CODE;
                    String[] bcc = {"prod@aguadeoro.ch"};
                    printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                    break;
                }
                case SEND_ORDER_READY: {
                    subject = Utils.getStringSetting("email_order_ready_subject_" + lang) + " " + orderInfo[12];
                    body = Utils.getStringSetting("email_order_ready_body_" + lang)
                            .replace("[1]", orderInfo[0]).replace("[2]", orderInfo[1]);
                    actionCode = SEND_ORDER_READY_CODE;
                    String[] bcc = {"prod@aguadeoro.ch"};
                    printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                    break;
                }
            }

            printIntent.setType("application/pdf");
            printIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            //printIntent.putExtra(Intent.EXTRA_TEXT, body);
            /*String test = "<html>\n" +
                    "<body>\n" +
                    "<div>\n" +
                    "Dear [1] and [2],\n" +
                    "</br>\n" +
                    "</br>\n" +
                    "\n" +
                    "We would like to thank you for your order. It was a great pleasure to help you choose your rings. \n" +
                    "Please find attached your order confirmation.\n" +
                    "</br><a href=\"https://api.whatsapp.com/send/?phone=41799168751&text=test&app_absent=0\">test</a>\n" +
                    "We remain at your entire disposal for any further information and we look forward to meeting you soon.\n" +
                    "Best regards, </br>\n" +
                    "AGUAdeORO\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>";*/
            printIntent.putExtra(Intent.EXTRA_TEXT,
                    Html.fromHtml(body
                            , Html.FROM_HTML_MODE_LEGACY));
            printIntent.putExtra(Intent.EXTRA_HTML_TEXT, body);
            String[] emails = orderInfo[3].split(",");
            printIntent.putExtra(Intent.EXTRA_EMAIL, emails);
            if (!pdfFile.exists() || !pdfFile.canRead()) {
                Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show();
            }
            ArrayList<Uri> uriList = new ArrayList<>();
            Uri uri = FileProvider.getUriForFile(context, "com.aguadeoro.android.fileprovider", pdfFile);
            printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            printIntent.setDataAndType(uri, context.getContentResolver().getType(uri));
            Log.d("test uri", "" + uri);
            uriList.add(uri);


            for (String[] comp : orderComps) {
                Query q = new Query("select * from OrderComponentPic "
                        + "where OrderComponentID = "
                        + comp[0]);
                q.execute();
                if (q.getRes().size() > 0) {
                    byte[] image = Base64.decode(q.getRes().get(0).get("" + 1), Base64.DEFAULT);
                    Bitmap b = BitmapFactory.decodeByteArray(image, 0, image.length);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + comp[0] + "_" + timeStamp;
                    File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File imageFile = File.createTempFile(
                            imageFileName,
                            ".jpg",
                            storageDir
                    );
                    FileOutputStream fout = new FileOutputStream(imageFile);
                    b.compress(Bitmap.CompressFormat.JPEG, 80, fout);
                    fout.flush();
                    fout.close();
                    MediaStore.Images.Media.insertImage(context.getContentResolver(), imageFile.getAbsolutePath(), imageFile.getName(), imageFile.getName());
                    Uri uri2 = getImageContentUri(context, imageFile);
                    uriList.add(uri2);
                }
            }
            printIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            printIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);

            context.startActivityForResult(printIntent, actionCode);

        } catch (Exception e) {
            Log.d("here", "---------------");
            e.printStackTrace();
            Log.d("here2", "---------------");
            Toast.makeText(context,
                    context.getString(R.string.error_retrieving_data),
                    Toast.LENGTH_LONG).show();
        }

    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static void printCustomerOrder(Activity context, String orderType, String[] orderInfo,
                                          ArrayList<String[]> orderComps, boolean toPrint, String lang, int pressed) {
        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            askForPermission(2);
        }
        verifyStoragePermissions(Utils.context);
        ArrayList<Map<String, String>> res = null;
        if (orderType.equals(ORD_ORDER)) {
            if (lang.equals("en")) {
                String query = "select TextField from E_Textes where Key ='order_en_0' or Key='order_en_1' or Key='order_en_1.1' or Key='order_en_2' or Key='order_en_2.1' or Key='order_en_3' or Key='order_en_3.1'";
                Query q = new Query(query);
                q.execute();
                res = q.getRes();
                for (int j = 0; j < res.size(); j++) {
                    Log.d("testText", j + " " + res.get(j).get("TextField"));
                }
            } else {
                String query = "select TextField from E_Textes where Key ='order_fr_0' or Key='order_fr_1' or Key='order_fr_1.1' or Key='order_fr_2' or Key='order_fr_2.1' or Key='order_fr_3' or Key='order_fr_3.1'";
                Query q = new Query(query);
                q.execute();
                res = q.getRes();
                for (int j = 0; j < res.size(); j++) {
                    Log.d("testText", j + " " + res.get(j).get("TextField"));
                }
            }
        } else if (orderType.equals(ORD_PREVIEW)) {
            if (lang.equals("en")) {
                String query = "select TextField from E_Textes where Key ='offer_en_0' or Key='offer_en_1' or Key='offer_en_1.1' or Key='offer_en_2' or Key='offer_en_2.1' or Key='offer_en_3' or Key='offer_en_3.1'";
                Query q = new Query(query);
                q.execute();
                res = q.getRes();
                for (int j = 0; j < res.size(); j++) {
                    Log.d("testText", j + " " + res.get(j).get("TextField"));
                }
            } else {
                String query = "select TextField from E_Textes where Key ='offer_fr_0' or Key='offer_fr_1' or Key='offer_fr_1.1' or Key='offer_fr_2' or Key='offer_fr_2.1' or Key='offer_fr_3' or Key='offer_fr_3.1'";
                Query q = new Query(query);
                q.execute();
                res = q.getRes();
                for (int j = 0; j < res.size(); j++) {
                    Log.d("testText", j + " " + res.get(j).get("TextField"));
                }
            }
        } else {
            if (lang.equals("en")) {
                String query = "select TextField from E_Textes where Key ='repair_en_0' or Key='repair_en_1' or Key='repair_en_1.1' or Key='repair_en_2' or Key='repair_en_2.1' or Key='repair_en_3' or Key='repair_en_3.1'";
                Query q = new Query(query);
                q.execute();
                res = q.getRes();
                for (int j = 0; j < res.size(); j++) {
                    Log.d("testText", j + " " + res.get(j).get("TextField"));
                }
            } else {
                String query = "select TextField from E_Textes where Key ='repair_fr_0' or Key='repair_fr_1' or Key='repair_fr_1.1' or Key='repair_fr_2' or Key='repair_fr_2.1' or Key='repair_fr_3' or Key='repair_fr_3.1'";
                Query q = new Query(query);
                q.execute();
                res = q.getRes();
                for (int j = 0; j < res.size(); j++) {
                    Log.d("testText", j + " " + res.get(j).get("TextField"));
                }
            }
        }
        try {
            /*Font font10 = new Font(FontFamily.HELVETICA, 10);
            Font font10I = new Font(FontFamily.HELVETICA, 10, Font.ITALIC);
            Font font11 = new Font(FontFamily.HELVETICA, 11);
            Font font11BI = new Font(FontFamily.HELVETICA, 11, Font.BOLDITALIC);
            Font font12B = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
            Font font10BI = new Font(FontFamily.HELVETICA, 10, Font.BOLDITALIC);
            BaseColor grey = new CMYKColor(0,0,0,0.60f);
            Font font9G = new Font(FontFamily.HELVETICA,8);
            font9G.setColor(grey);*/
            Font font10 = new Font(FontFamily.HELVETICA, 8);
            Font font10I = new Font(FontFamily.HELVETICA, 8, Font.ITALIC);
            Font font11 = new Font(FontFamily.HELVETICA, 9);
            Font font11BI = new Font(FontFamily.HELVETICA, 9, Font.BOLDITALIC);
            Font font12B = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
            Font font10BI = new Font(FontFamily.HELVETICA, 8, Font.BOLDITALIC);
            BaseColor grey = new CMYKColor(0, 0, 0, 0.60f);
            Font font9G = new Font(FontFamily.HELVETICA, 7);
            font9G.setColor(grey);
            Document document = new Document(PageSize.A4, 40f, 40f, 0f, 10f);
            new File(Environment.getExternalStorageDirectory() + "/02_orders/")
                    .mkdir();
            File pdfFile = new File(Environment.getExternalStorageDirectory()
                    + "/02_orders/", "Order " + orderInfo[12] + " "
                    + orderInfo[0] + ".pdf");
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setSpacingBefore(0);
            table.setSpacingAfter(0);
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setNoWrap(false);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.getDefaultCell().setMinimumHeight(28f);
            table.setWidths(new float[]{35f, 80f, 69f, 69f, 38f, 38f, 38f,
                    69f, 83f, 55f});

            // logo
            InputStream in = context.getAssets().open("logo2.png");
            byte[] logo = IOUtils.toByteArray(in);
            Image l = Image.getInstance(logo);
            l.scalePercent(30);
            document.add(new Paragraph("\n"));
            PdfPCell cell = new PdfPCell(l);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setColspan(10);
            cell.setBorder(0);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(orderComps.get(0)[1], font10));
            cell.setBorder(0);
            cell.setColspan(10);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);

            // line 1: name1, date
            cell = new PdfPCell();
            cell.addElement(new Phrase("First Name Last Name", font9G));
            cell.addElement(new Phrase("Prénom Nom 1", font10));
            cell.setColspan(2);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(orderInfo[0], font11));
            cell.setColspan(4);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.addCell("");
            cell = new PdfPCell();
            cell.addElement(new Phrase("Date:", font10));
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorder(0);
            cell.setColspan(1);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(orderInfo[6], font11));
            cell.setColspan(2);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);


            // line 2: name2, deadline
            cell = new PdfPCell();
            cell.addElement(new Phrase("First Name Last Name", font9G));
            cell.addElement(new Phrase("Prénom Nom 2", font10));
            cell.setColspan(2);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(orderInfo[1], font11));
            cell.setColspan(4);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.addCell("");
            cell = new PdfPCell();
            cell.addElement(new Phrase("Deadline :", font9G));
            cell.addElement(new Phrase("Délai :", font10));
            cell.setBorder(0);
            cell.setColspan(1);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(orderInfo[7], font11));
            cell.setColspan(2);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);

            // line 3: address
            cell = new PdfPCell();
            cell.addElement(new Phrase("Address", font9G));
            cell.addElement(new Phrase("Adresse", font10));
            cell.setColspan(2);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(orderInfo[2], font11));
            cell.setColspan(4);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setMinimumHeight(40f);
            table.addCell(cell);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            //table.addCell("");


            // line 4: email
            cell = new PdfPCell();
            cell.addElement(new Phrase("Email", font10));
            cell.setColspan(2);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(orderInfo[3].replace(',', '\n'),
                    font11));
            cell.setColspan(4);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");

            // line 5: Tel
            cell = new PdfPCell();
            cell.addElement(new Phrase("Phone", font9G));
            cell.addElement(new Phrase("Tél", font10));
            cell.setColspan(2);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(orderInfo[4], font11));
            cell.setColspan(4);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");

            // line 6: weddingdate
            cell = new PdfPCell();
            cell.addElement(new Phrase("Wedding Date", font9G));
            cell.addElement(new Phrase("Date Mar.", font10));
            cell.setColspan(2);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(Utils.shortDateFromDB(orderInfo[5]),
                    font11));
            cell.setColspan(4);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");

            // line 7: order header
            if (ORD_REPAIR.equals(orderType)) {
                cell = new PdfPCell(new Phrase("REPARATION", font12B));
            } else if (ORD_PREVIEW.equals(orderType)) {
                cell = new PdfPCell(new Phrase("Offre / Offer", font12B));
            } else {
                cell = new PdfPCell(new Phrase("COMMANDE / ORDER", font12B));
            }
            cell.setColspan(10);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setMinimumHeight(41f);
            table.addCell(cell);

            // line 8 ring header
            table.addCell("");
            cell = new PdfPCell();
            cell.addElement(new Phrase("    Article", font10BI));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("    Matière", font10BI));
            cell.addElement(new Phrase("    Material", font9G));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("Couleur", font10BI));
            cell.addElement(new Phrase("Colour", font9G));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("L", font10BI));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("H", font10BI));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("Taille", font10BI));
            cell.addElement(new Phrase("Size", font9G));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("    Surface", font10BI));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("Pierres", font10BI));
            cell.addElement(new Phrase("Stones", font9G));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            if (ORD_PREVIEW.equals(orderType)) {
                table.addCell("");
            } else {
                cell = new PdfPCell();
                cell.addElement(new Phrase("Prix", font10BI));
                cell.addElement(new Phrase("Price", font9G));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
            }

            // line 9 ring detail
            for (int i = 0; i < orderComps.size(); i++) {
                String[] comp = orderComps.get(i);
                for (int j = 0; j < comp.length; j++) {
                    Log.d("comp", "" + j + " " + comp[j]);
                }
                cell = new PdfPCell(new Phrase("Art." + (i + 1), font10BI));
                cell.setMinimumHeight(32f);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setBorder(0);
                table.addCell(cell);
                String article;
                /*if(comp[29]!= null){
                    article = comp[2] + ", \n " + comp[29] + ", \n " + comp[27];
                }
                else{
                    article = comp[2] + ", \n " + comp[3] + ", \n " + comp[27];
                }*/
                Log.d("3333333", "" + comp[29] + " " + comp[3]);
                article = comp[2] + ", \n " + comp[3] + ", \n " + comp[27];
                table.addCell(new Phrase(article, font10));
                table.addCell(new Phrase(comp[5], font10));
                table.addCell(new Phrase(comp[6], font10));
                table.addCell(new Phrase(comp[7], font10));
                table.addCell(new Phrase(comp[8], font10));
                table.addCell(new Phrase(comp[9], font10));
                table.addCell(new Phrase(comp[10], font10));
                table.addCell(new Phrase(comp[11], font10));
                if (!ORD_PREVIEW.equals(orderType)) {
                    table.addCell(new Phrase(comp[16], font10));
                } else {
                    table.addCell("");
                }

                if (!"".equals(comp[17])) {
                    table.addCell("");
                    cell = new PdfPCell();
                    cell.addElement(new Phrase("Observations:", font10));
                    cell.addElement(new Phrase("Remarks:", font9G));
                    cell.setBorder(0);
                    table.addCell(cell);
                    cell = new PdfPCell();
                    cell.addElement(new Phrase(comp[17], font10));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    cell.setBorder(0);
                    cell.setColspan(8);
                    table.addCell(cell);

                }
            }
            // line 10: engraving

            cell = new PdfPCell(new Phrase("Gravure / Engraving", font11BI));
            cell.setColspan(10);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setMinimumHeight(41f);
            table.addCell(cell);

            // line 11: engraving details
            for (int i = 0; i < orderComps.size(); i++) {
                String[] comp = orderComps.get(i);
                if (i % 2 == 0) {
                    cell = new PdfPCell(new Phrase("Art." + (i + 1), font10BI));
                } else {
                    cell = new PdfPCell(new Phrase("Art." + (i + 1), font10BI));
                }
                Log.d("comp", i + " " + comp[i]);
                cell.setMinimumHeight(32f);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setBorder(0);
                table.addCell(cell);
                table.addCell(new Phrase(comp[13] + ", " + comp[14], font10));
                try {
                    in = context.getAssets().open(comp[14]);
                    byte[] b = IOUtils.toByteArray(in);
                    BaseFont font = BaseFont.createFont(comp[14] + ".ttf",
                            BaseFont.CP1252, true, true, b, null);
                    if (comp[14].equals(FONTS[1]) || comp[14].equals(FONTS[2]))
                        cell = new PdfPCell(new Phrase(comp[12], new Font(font,
                                26)));
                    else if (comp[14].equals(FONTS[3])
                            || comp[14].equals(FONTS[5])
                            || comp[14].equals(FONTS[6]))
                        cell = new PdfPCell(new Phrase(comp[12], new Font(font,
                                23)));
                    else
                        cell = new PdfPCell(new Phrase(comp[12], new Font(font,
                                18)));

                } catch (Exception e) {
                    cell = new PdfPCell(new Phrase(comp[12]));
                }
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setBorder(0);
                cell.setColspan(7);
                table.addCell(cell);
                if (!ORD_PREVIEW.equals(orderType)) {
                    table.addCell(new Phrase(comp[15], font10));
                } else {
                    table.addCell("");
                }
            }

            // line 12: price
            if (!ORD_PREVIEW.equals(orderType)) {
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell(new Phrase("TOTAL", font10BI));
                table.addCell("");
                cell = new PdfPCell(new Phrase(orderInfo[8], font11));
                cell.setMinimumHeight(47f);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setBorder(0);
                cell.setBottom(1);
                table.addCell(cell);
                if (Double.parseDouble(orderInfo[9]) != 0.0) {
                    table.addCell("");
                    table.addCell("");
                    table.addCell("");
                    table.addCell("");
                    table.addCell("");
                    table.addCell("");
                    table.addCell("");
                    cell = new PdfPCell(new Phrase("Rabais", font10BI));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorder(0);
                    cell.setNoWrap(true);
                    table.addCell(cell);
                    table.addCell("");
                    cell = new PdfPCell(new Phrase(orderInfo[9], font11));
                    cell.setMinimumHeight(20f);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorder(0);
                    cell.setBottom(1);
                    table.addCell(cell);

                }

                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                cell = new PdfPCell(new Phrase("Acompte / Installment", font10BI));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setBorder(0);
                cell.setNoWrap(true);
                table.addCell(cell);

                table.addCell("");
                cell = new PdfPCell(new Phrase(orderInfo[10], font11));
                cell.setMinimumHeight(20f);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setBorder(0);
                cell.setBottom(1);
                table.addCell(cell);

                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                cell = new PdfPCell(new Phrase("Solde à payer à la livraison /\nTo pay upon delivery",
                        font10BI));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setBorder(0);
                cell.setNoWrap(true);
                table.addCell(cell);
                table.addCell("");
                cell = new PdfPCell(new Phrase(orderInfo[11], font11));
                cell.setMinimumHeight(30f);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setBorder(0);
                cell.setBottom(1);
                table.addCell(cell);
            }
            if (lang.equals("fr")) {
                if (!(res.get(0).get("TextField").equals(""))) {
                    cell = new PdfPCell(
                            new Phrase("\n" + res.get(0).get("TextField"), font11));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);

                    table.addCell("");
                    table.completeRow();
                }

                if (!(res.get(1).get("TextField").equals(""))) {
                    cell = new PdfPCell(
                            new Phrase(res.get(1).get("TextField"), font10BI));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);

                    table.addCell("");
                    table.completeRow();
                }

                if (!(res.get(2).get("TextField").equals(""))) {
                    cell = new PdfPCell(
                            new Phrase(res.get(2).get("TextField"), font10));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);

                    table.addCell("");
                    table.completeRow();
                }

                if (!(res.get(3).get("TextField").equals(""))) {
                    cell = new PdfPCell(
                            new Phrase(res.get(3).get("TextField"), font10BI));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);

                    table.addCell("");
                    table.completeRow();
                }

                if (!(res.get(4).get("TextField").equals(""))) {
                    cell = new PdfPCell(
                            new Phrase(res.get(4).get("TextField"), font10));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);

                    table.addCell("");
                    table.completeRow();
                }

                if (!(res.get(5).get("TextField").equals(""))) {
                    cell = new PdfPCell(
                            new Phrase(res.get(5).get("TextField"), font10BI));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);

                    table.addCell("");
                    table.completeRow();
                }

                if (!(res.get(6).get("TextField").equals(""))) {
                    cell = new PdfPCell(
                            new Phrase(res.get(6).get("TextField"), font10));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);

                    table.addCell("");
                    table.completeRow();
                }
            } else {
                if (!(res.get(0).get("TextField").equals(""))) {
                    cell = new PdfPCell(
                            new Phrase("\n" + res.get(0).get("TextField"), font11));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);

                    table.addCell("");
                    table.completeRow();
                }

                if (!(res.get(1).get("TextField").equals(""))) {
                    cell = new PdfPCell(
                            new Phrase(res.get(1).get("TextField"), font10BI));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);

                    table.addCell("");
                    table.completeRow();
                }

                if (!(res.get(2).get("TextField").equals(""))) {
                    cell = new PdfPCell(
                            new Phrase(res.get(2).get("TextField"), font10));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);

                    table.addCell("");
                    table.completeRow();
                }

                if (!(res.get(3).get("TextField").equals(""))) {
                    cell = new PdfPCell(
                            new Phrase(res.get(3).get("TextField"), font10BI));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);

                    table.addCell("");
                    table.completeRow();
                }

                if (!(res.get(4).get("TextField").equals(""))) {
                    cell = new PdfPCell(
                            new Phrase(res.get(4).get("TextField"), font10));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);

                    table.addCell("");
                    table.completeRow();
                }

                if (!(res.get(5).get("TextField").equals(""))) {
                    cell = new PdfPCell(
                            new Phrase(res.get(5).get("TextField"), font10BI));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);

                    table.addCell("");
                    table.completeRow();
                }

                if (!(res.get(6).get("TextField").equals(""))) {
                    cell = new PdfPCell(
                            new Phrase(res.get(2).get("TextField"), font10));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);

                    table.addCell("");
                    table.completeRow();
                }
            }


            // footer
            table.addCell("");
            table.completeRow();
            cell = new PdfPCell();
            cell.addElement(new Phrase("Nos coordonnées bancaires: / Our Banking Details:", font10I));
            cell.setColspan(5);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("", font11));
            cell.setColspan(5);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setBorder(0);
            table.addCell(cell);
            cell = new PdfPCell(
                    new Phrase(
                            "AGUA DE ORO SARL\nBANQUE UBS SA\nGENEVE 1201",
                            font10I));
            cell.setColspan(10);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            cell = new PdfPCell(
                    new Phrase(
                            "IBAN: CH29 0024 0240 4559 7301 E",
                            font10I));
            cell.setColspan(10);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            cell = new PdfPCell(
                    new Phrase(FOOTER, font12B));
            cell.setColspan(10);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setFixedHeight(100f);
            table.addCell(cell);
            document.add(table);

            document.close();
            String subject = Utils.getStringSetting("email_cust_subject_"
                    + lang) + " ";
            String body = Utils.getStringSetting("email_cust_body_" + lang)
                    .replace("[1]", orderInfo[0]).replace("[2]", orderInfo[1]).replace("XXXX", orderInfo[12]);
            Intent printIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            ArrayList<Uri> uriList = new ArrayList<>();

            if (toPrint) {
                Log.d("here", "here");
                printIntent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(pdfFile);
                printIntent.setDataAndType(uri, "application/pdf");

            } else {
                if (orderType.equals(ORD_PREVIEW)) {
                    if (pressed == 1) {
                        ArrayList<Uri> uris = new ArrayList<Uri>();
                        uris.add(Uri.fromFile(pdfFile));
                        // include pic if any
                        for (int i = 0; i < orderComps.size(); i++) {
                            File filename = new File(
                                    Environment.getExternalStorageDirectory()
                                            + "/02_orders/" + "Order " + orderInfo[12]
                                            + " Article " + (i + 1) + ".jpeg");
                            if (loadComponentPic(orderComps.get(i)[0], filename)) {
                                uris.add(Uri.fromFile(filename));
                            }
                        }
                        if (!pdfFile.exists() || !pdfFile.canRead()) {
                            Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show();
                        }

                        Uri uri = FileProvider.getUriForFile(context, "com.aguadeoro.android.fileprovider", pdfFile);
                        printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        printIntent.setDataAndType(uri, context.getContentResolver().getType(uri));
                        Log.d("test uri", "" + uri);
                        uriList.add(uri);
                        //printIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        printIntent.setType("application/pdf");
                        printIntent.putExtra(Intent.EXTRA_SUBJECT, subject + " " + orderInfo[12]);
                        printIntent.putExtra(Intent.EXTRA_TEXT,
                                Html.fromHtml(body
                                        , Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
                        printIntent.putExtra(Intent.EXTRA_HTML_TEXT, body);
                        String[] emails = orderInfo[3].split(",");
                        printIntent.putExtra(Intent.EXTRA_EMAIL, emails);
                        String[] bcc = {"prod@aguadeoro.ch"};
                        printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                    } else if (pressed == 2) {
                        String bod = "";
                        String sub = "";
                        if (lang.equals("en")) {
                            String query = "select OptionValue from Email where OptionKey = 'email_offer_subject_en' or OptionKey = 'email_offer_body_en'";
                            Query q = new Query(query);
                            boolean success = q.execute();
                            if (!success) {
                                Log.d("Print customer order", "NOK");
                            }
                            ArrayList<Map<String, String>> results = q.getRes();
                            sub = results.get(0).get("OptionValue");
                            bod = results.get(1).get("OptionValue");
                        } else {
                            String query = "select OptionValue from Email where OptionKey = 'email_offer_subject_fr' or OptionKey = 'email_offer_body_fr'";
                            Query q = new Query(query);
                            boolean success = q.execute();
                            if (!success) {
                                Log.d("Print customer order", "NOK");
                            }
                            ArrayList<Map<String, String>> results = q.getRes();
                            sub = results.get(0).get("OptionValue");
                            bod = results.get(1).get("OptionValue");
                        }
                        ArrayList<Uri> uris = new ArrayList<Uri>();
                        uris.add(Uri.fromFile(pdfFile));
                        // include pic if any
                        for (int i = 0; i < orderComps.size(); i++) {
                            File filename = new File(
                                    Environment.getExternalStorageDirectory()
                                            + "/02_orders/" + "Order " + orderInfo[12]
                                            + " Article " + (i + 1) + ".jpeg");
                            if (loadComponentPic(orderComps.get(i)[0], filename)) {
                                uris.add(Uri.fromFile(filename));
                            }
                        }
                        if (!pdfFile.exists() || !pdfFile.canRead()) {
                            Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show();
                        }

                        Uri uri = FileProvider.getUriForFile(context, "com.aguadeoro.android.fileprovider", pdfFile);
                        uriList.add(uri);
                        printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        printIntent.setDataAndType(uri, context.getContentResolver().getType(uri));
                        Log.d("test uri", "" + uri);
                        //printIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        printIntent.setType("application/pdf");
                        printIntent.putExtra(Intent.EXTRA_SUBJECT, sub + " " + orderInfo[12]);
                        printIntent.putExtra(Intent.EXTRA_TEXT,
                                Html.fromHtml(bod
                                        , Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
                        printIntent.putExtra(Intent.EXTRA_HTML_TEXT, bod);
                        String[] emails = orderInfo[3].split(",");
                        printIntent.putExtra(Intent.EXTRA_EMAIL, emails);
                        String[] bcc = {"prod@aguadeoro.ch"};
                        printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                    } else if (pressed == 3) {
                        String bod = "";
                        String sub = "";
                        if (lang.equals("en")) {
                            String query = "select OptionValue from Email where OptionKey = 'email_repair_subject_en' or OptionKey = 'email_repair_body_en'";
                            Query q = new Query(query);
                            boolean success = q.execute();
                            if (!success) {
                                Log.d("Print customer order", "NOK");
                            }
                            ArrayList<Map<String, String>> results = q.getRes();
                            sub = results.get(0).get("OptionValue");
                            bod = results.get(1).get("OptionValue").replace("XXXX", orderInfo[12]);
                        } else {
                            String query = "select OptionValue from Email where OptionKey = 'email_repair_subject_fr' or OptionKey = 'email_repair_body_fr'";
                            Query q = new Query(query);
                            boolean success = q.execute();
                            if (!success) {
                                Log.d("Print customer order", "NOK");
                            }
                            ArrayList<Map<String, String>> results = q.getRes();
                            sub = results.get(0).get("OptionValue");
                            bod = results.get(1).get("OptionValue").replace("XXXX", orderInfo[12]);
                        }
                        ArrayList<Uri> uris = new ArrayList<Uri>();
                        uris.add(Uri.fromFile(pdfFile));
                        // include pic if any
                        for (int i = 0; i < orderComps.size(); i++) {
                            File filename = new File(
                                    Environment.getExternalStorageDirectory()
                                            + "/02_orders/" + "Order " + orderInfo[12]
                                            + " Article " + (i + 1) + ".jpeg");
                            if (loadComponentPic(orderComps.get(i)[0], filename)) {
                                uris.add(Uri.fromFile(filename));
                            }
                        }
                        if (!pdfFile.exists() || !pdfFile.canRead()) {
                            Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show();
                        }

                        Uri uri = FileProvider.getUriForFile(context, "com.aguadeoro.android.fileprovider", pdfFile);
                        uriList.add(uri);
                        printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        printIntent.setDataAndType(uri, context.getContentResolver().getType(uri));
                        Log.d("test uri", "" + uri);
                        //printIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        printIntent.setType("application/pdf");
                        printIntent.putExtra(Intent.EXTRA_SUBJECT, sub + " " + orderInfo[12]);
                        printIntent.putExtra(Intent.EXTRA_TEXT,
                                Html.fromHtml(bod
                                        , Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
                        printIntent.putExtra(Intent.EXTRA_HTML_TEXT, bod);
                        String[] emails = orderInfo[3].split(",");
                        printIntent.putExtra(Intent.EXTRA_EMAIL, emails);
                        String[] bcc = {"prod@aguadeoro.ch"};
                        printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                    }

                } else if (orderType.equals(ORD_REPAIR)) {
                    if (pressed == 1) {
                        ArrayList<Uri> uris = new ArrayList<Uri>();
                        uris.add(Uri.fromFile(pdfFile));
                        // include pic if any
                        for (int i = 0; i < orderComps.size(); i++) {
                            File filename = new File(
                                    Environment.getExternalStorageDirectory()
                                            + "/02_orders/" + "Order " + orderInfo[12]
                                            + " Article " + (i + 1) + ".jpeg");
                            if (loadComponentPic(orderComps.get(i)[0], filename)) {
                                uris.add(Uri.fromFile(filename));
                            }
                        }
                        printIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                                uris);
                        printIntent.setType("application/pdf");
                        printIntent.putExtra(Intent.EXTRA_SUBJECT, subject + " " + orderInfo[12]);
                        printIntent.putExtra(Intent.EXTRA_TEXT,
                                Html.fromHtml(body
                                        , Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
                        printIntent.putExtra(Intent.EXTRA_HTML_TEXT, body);
                        String[] emails = orderInfo[3].split(",");
                        printIntent.putExtra(Intent.EXTRA_EMAIL, emails);
                        String[] bcc = {"prod@aguadeoro.ch"};
                        printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                    } else if (pressed == 2) {
                        String bod = "";
                        String sub = "";
                        if (lang.equals("en")) {
                            String query = "select OptionValue from Email where OptionKey = 'email_offer_subject_en' or OptionKey = 'email_offer_body_en'";
                            Query q = new Query(query);
                            boolean success = q.execute();
                            if (!success) {
                                Log.d("Print customer order", "NOK");
                            }
                            ArrayList<Map<String, String>> results = q.getRes();
                            sub = results.get(0).get("OptionValue");
                            bod = results.get(1).get("OptionValue");
                        } else {
                            String query = "select OptionValue from Email where OptionKey = 'email_offer_subject_fr' or OptionKey = 'email_offer_body_fr'";
                            Query q = new Query(query);
                            boolean success = q.execute();
                            if (!success) {
                                Log.d("Print customer order", "NOK");
                            }
                            ArrayList<Map<String, String>> results = q.getRes();
                            sub = results.get(0).get("OptionValue");
                            bod = results.get(1).get("OptionValue");
                        }
                        ArrayList<Uri> uris = new ArrayList<Uri>();
                        uris.add(Uri.fromFile(pdfFile));
                        // include pic if any
                        for (int i = 0; i < orderComps.size(); i++) {
                            File filename = new File(
                                    Environment.getExternalStorageDirectory()
                                            + "/02_orders/" + "Order " + orderInfo[12]
                                            + " Article " + (i + 1) + ".jpeg");
                            if (loadComponentPic(orderComps.get(i)[0], filename)) {
                                uris.add(Uri.fromFile(filename));
                            }
                        }
                        printIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                                uris);
                        printIntent.setType("application/pdf");
                        printIntent.putExtra(Intent.EXTRA_SUBJECT, sub + " " + orderInfo[12]);
                        printIntent.putExtra(Intent.EXTRA_TEXT,
                                Html.fromHtml(bod
                                        , Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
                        printIntent.putExtra(Intent.EXTRA_HTML_TEXT, bod);
                        String[] emails = orderInfo[3].split(",");
                        printIntent.putExtra(Intent.EXTRA_EMAIL, emails);
                        String[] bcc = {"prod@aguadeoro.ch"};
                        printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                    } else if (pressed == 3) {
                        String bod = "";
                        String sub = "";
                        if (lang.equals("en")) {
                            String query = "select OptionValue from Email where OptionKey = 'email_repair_subject_en' or OptionKey = 'email_repair_body_en'";
                            Query q = new Query(query);
                            boolean success = q.execute();
                            if (!success) {
                                Log.d("Print customer order", "NOK");
                            }
                            ArrayList<Map<String, String>> results = q.getRes();
                            sub = results.get(0).get("OptionValue");
                            bod = results.get(1).get("OptionValue").replace("XXXX", orderInfo[12]);
                        } else {
                            String query = "select OptionValue from Email where OptionKey = 'email_repair_subject_fr' or OptionKey = 'email_repair_body_fr'";
                            Query q = new Query(query);
                            boolean success = q.execute();
                            if (!success) {
                                Log.d("Print customer order", "NOK");
                            }
                            ArrayList<Map<String, String>> results = q.getRes();
                            sub = results.get(0).get("OptionValue");
                            bod = results.get(1).get("OptionValue").replace("XXXX", orderInfo[12]);
                        }
                        ArrayList<Uri> uris = new ArrayList<Uri>();
                        uris.add(Uri.fromFile(pdfFile));
                        // include pic if any
                        for (int i = 0; i < orderComps.size(); i++) {
                            File filename = new File(
                                    Environment.getExternalStorageDirectory()
                                            + "/02_orders/" + "Order " + orderInfo[12]
                                            + " Article " + (i + 1) + ".jpeg");
                            if (loadComponentPic(orderComps.get(i)[0], filename)) {
                                uris.add(Uri.fromFile(filename));
                            }
                        }
                        if (!pdfFile.exists() || !pdfFile.canRead()) {
                            Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show();
                        }
                        Uri uri = FileProvider.getUriForFile(context, "com.aguadeoro.android.fileprovider", pdfFile);
                        uriList.add(uri);
                        printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        printIntent.setDataAndType(uri, context.getContentResolver().getType(uri));
                        Log.d("test uri", "" + uri);
                        //printIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        printIntent.setType("application/pdf");
                        printIntent.putExtra(Intent.EXTRA_SUBJECT, sub + " " + orderInfo[12]);
                        printIntent.putExtra(Intent.EXTRA_TEXT,
                                Html.fromHtml(bod
                                        , Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
                        printIntent.putExtra(Intent.EXTRA_HTML_TEXT, bod);
                        String[] emails = orderInfo[3].split(",");
                        printIntent.putExtra(Intent.EXTRA_EMAIL, emails);
                        String[] bcc = {"prod@aguadeoro.ch"};
                        printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                    }
                } else {
                    if (pressed == 1) {
                        ArrayList<Uri> uris = new ArrayList<Uri>();
                        uris.add(Uri.fromFile(pdfFile));
                        // include pic if any
                        for (int i = 0; i < orderComps.size(); i++) {
                            File filename = new File(
                                    Environment.getExternalStorageDirectory()
                                            + "/02_orders/" + "Order " + orderInfo[12]
                                            + " Article " + (i + 1) + ".jpeg");
                            if (loadComponentPic(orderComps.get(i)[0], filename)) {
                                uris.add(Uri.fromFile(filename));
                            }
                        }
                        if (!pdfFile.exists() || !pdfFile.canRead()) {
                            Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show();
                        }

                        Uri uri = FileProvider.getUriForFile(context, "com.aguadeoro.android.fileprovider", pdfFile);
                        uriList.add(uri);
                        printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        printIntent.setDataAndType(uri, context.getContentResolver().getType(uri));
                        Log.d("test uri", "" + uri);
                        //printIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        printIntent.setType("application/pdf");
                        printIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        printIntent.putExtra(Intent.EXTRA_TEXT,
                                Html.fromHtml(body
                                        , Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
                        printIntent.putExtra(Intent.EXTRA_HTML_TEXT, body);
                        String[] emails = orderInfo[3].split(",");
                        printIntent.putExtra(Intent.EXTRA_EMAIL, emails);
                        String[] bcc = {"prod@aguadeoro.ch"};
                        printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                    } else if (pressed == 2) {
                        String bod = "";
                        String sub = "";
                        if (lang.equals("en")) {
                            String query = "select OptionValue from Email where OptionKey = 'email_offer_subject_en' or OptionKey = 'email_offer_body_en'";
                            Query q = new Query(query);
                            boolean success = q.execute();
                            if (!success) {
                                Log.d("Print customer order", "NOK");
                            }
                            ArrayList<Map<String, String>> results = q.getRes();
                            sub = results.get(0).get("OptionValue");
                            bod = results.get(1).get("OptionValue");
                        } else {
                            String query = "select OptionValue from Email where OptionKey = 'email_offer_subject_fr' or OptionKey = 'email_offer_body_fr'";
                            Query q = new Query(query);
                            boolean success = q.execute();
                            if (!success) {
                                Log.d("Print customer order", "NOK");
                            }
                            ArrayList<Map<String, String>> results = q.getRes();
                            sub = results.get(0).get("OptionValue");
                            bod = results.get(1).get("OptionValue");
                        }
                        ArrayList<Uri> uris = new ArrayList<Uri>();
                        uris.add(Uri.fromFile(pdfFile));
                        // include pic if any
                        for (int i = 0; i < orderComps.size(); i++) {
                            File filename = new File(
                                    Environment.getExternalStorageDirectory()
                                            + "/02_orders/" + "Order " + orderInfo[12]
                                            + " Article " + (i + 1) + ".jpeg");
                            if (loadComponentPic(orderComps.get(i)[0], filename)) {
                                uris.add(Uri.fromFile(filename));
                            }
                        }

                        if (!pdfFile.exists() || !pdfFile.canRead()) {
                            Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show();
                        }

                        Uri uri = FileProvider.getUriForFile(context, "com.aguadeoro.android.fileprovider", pdfFile);
                        uriList.add(uri);
                        printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        printIntent.setDataAndType(uri, context.getContentResolver().getType(uri));
                        Log.d("test uri", "" + uri);
                        //printIntent.putExtra(Intent.EXTRA_STREAM, uri);

                        //printIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                        printIntent.setType("application/pdf");
                        printIntent.putExtra(Intent.EXTRA_SUBJECT, sub + " " + orderInfo[12]);
                        printIntent.putExtra(Intent.EXTRA_TEXT,
                                Html.fromHtml(bod
                                        , Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
                        printIntent.putExtra(Intent.EXTRA_HTML_TEXT, bod);
                        String[] emails = orderInfo[3].split(",");
                        printIntent.putExtra(Intent.EXTRA_EMAIL, emails);
                        String[] bcc = {"prod@aguadeoro.ch"};
                        printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                    } else if (pressed == 3) {
                        String bod = "";
                        String sub = "";
                        if (lang.equals("en")) {
                            String query = "select OptionValue from Email where OptionKey = 'email_repair_subject_en' or OptionKey = 'email_repair_body_en'";
                            Query q = new Query(query);
                            boolean success = q.execute();
                            if (!success) {
                                Log.d("Print customer order", "NOK");
                            }
                            ArrayList<Map<String, String>> results = q.getRes();
                            sub = results.get(0).get("OptionValue");
                            bod = results.get(1).get("OptionValue").replace("XXXX", orderInfo[12]);
                        } else {
                            String query = "select OptionValue from Email where OptionKey = 'email_repair_subject_fr' or OptionKey = 'email_repair_body_fr'";
                            Query q = new Query(query);
                            boolean success = q.execute();
                            if (!success) {
                                Log.d("Print customer order", "NOK");
                            }
                            ArrayList<Map<String, String>> results = q.getRes();
                            sub = results.get(0).get("OptionValue");
                            bod = results.get(1).get("OptionValue").replace("XXXX", orderInfo[12]);

                        }
                        ArrayList<Uri> uris = new ArrayList<Uri>();
                        uris.add(Uri.fromFile(pdfFile));
                        // include pic if any
                        for (int i = 0; i < orderComps.size(); i++) {
                            File filename = new File(
                                    Environment.getExternalStorageDirectory()
                                            + "/02_orders/" + "Order " + orderInfo[12]
                                            + " Article " + (i + 1) + ".jpeg");
                            if (loadComponentPic(orderComps.get(i)[0], filename)) {
                                uris.add(Uri.fromFile(filename));
                            }
                        }
                        if (!pdfFile.exists() || !pdfFile.canRead()) {
                            Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show();
                        }

                        Uri uri = FileProvider.getUriForFile(context, "com.aguadeoro.android.fileprovider", pdfFile);
                        uriList.add(uri);
                        printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        printIntent.setDataAndType(uri, context.getContentResolver().getType(uri));
                        Log.d("test uri", "" + uri);
                        //printIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        printIntent.setType("application/pdf");
                        printIntent.putExtra(Intent.EXTRA_SUBJECT, sub + " " + orderInfo[12]);
                        printIntent.putExtra(Intent.EXTRA_TEXT,
                                Html.fromHtml(bod
                                        , Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
                        printIntent.putExtra(Intent.EXTRA_HTML_TEXT, bod);
                        String[] emails = orderInfo[3].split(",");
                        printIntent.putExtra(Intent.EXTRA_EMAIL, emails);
                        String[] bcc = {"prod@aguadeoro.ch"};
                        printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                    }
                }
            }

            for (String[] comp : orderComps) {
                Query q = new Query("select * from OrderComponentPic "
                        + "where OrderComponentID = "
                        + comp[0]);
                q.execute();
                if (q.getRes().size() > 0) {
                    byte[] image = Base64.decode(q.getRes().get(0).get("" + 1), Base64.DEFAULT);
                    Bitmap b = BitmapFactory.decodeByteArray(image, 0, image.length);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + comp[0] + "_" + timeStamp;
                    File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File imageFile = File.createTempFile(
                            imageFileName,
                            ".jpg",
                            storageDir
                    );
                    FileOutputStream fout = new FileOutputStream(imageFile);
                    b.compress(Bitmap.CompressFormat.JPEG, 80, fout);
                    fout.flush();
                    fout.close();
                    MediaStore.Images.Media.insertImage(context.getContentResolver(), imageFile.getAbsolutePath(), imageFile.getName(), imageFile.getName());
                    Uri uri2 = getImageContentUri(context, imageFile);
                    uriList.add(uri2);
                }
            }
            Log.d("test", "" + uriList);
            printIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);

            if (orderType.equals(ORD_PREVIEW)) {
                context.startActivityForResult(printIntent, SEND_OFFER_CONF_CODE);
            } else {
                context.startActivityForResult(printIntent, SEND_ORDER_CONF_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,
                    context.getString(R.string.error_retrieving_data),
                    Toast.LENGTH_LONG).show();
        }

    }

    public static void printSupplierOrder(Activity context, String orderType, Map<String, String> orderInfo,
                                          ArrayList<String[]> orderCompList, boolean toView, String name, ArrayList<String> links) {
        Log.e("orderCompList", String.valueOf(orderCompList.size()));
        try {
            boolean newF = new File(Environment.getExternalStorageDirectory()
                    + "/03_supplierorders/").mkdir();
//            Log.e("autorisation new file", String.valueOf(newF));

//            File pdfFile = new File(Environment.getExternalStorageDirectory()
//                    + "/03_supplierorders/", "Order " + orderInfo.getOrDefault("orderNumber", "") + ".pdf");
            File pdfFile = new File(Environment.getExternalStorageDirectory() +
                            "/03_supplierorders/", "Order " + orderInfo.getOrDefault("orderNumber", "") + ".pdf");
            Document document = new Document(PageSize.A4, 40f, 40f, 0f, 10f);
//            new File(Environment.getExternalStorageDirectory()
//                    + "/03_supplierorders/").mkdir();
pdfFile.createNewFile();
            Log.e("exist ?", String.valueOf(pdfFile.exists()));

            PdfWriter.getInstance(document, Files.newOutputStream(pdfFile.toPath()));
            document.open();
            generateSupplierOrder(document, context, orderType, orderInfo, orderCompList, toView, name, links);
//            document.newPage();
//            generateSupplierOrder(document, context, orderType, orderInfo, orderCompList, toView, name, links);
//            document.add(new Paragraph("\n"));
//            PdfPTable table = new PdfPTable(10);
//            table.setWidthPercentage(100);
//            table.setSpacingBefore(0);
//            table.setSpacingAfter(0);
//            table.getDefaultCell().setBorder(0);
//            table.getDefaultCell().setNoWrap(false);
//            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.getDefaultCell().setMinimumHeight(30f);
//            table.setWidths(new float[]{78f, 76f, 69f, 69f, 38f, 38f, 38f,
//                    76f, 57f, 69f});
//
//            // logo
//            InputStream in = context.getAssets().open("logo2.png");
//            byte[] logo = IOUtils.toByteArray(in);
//            Image l = Image.getInstance(logo);
//            l.scalePercent(30);
//            document.add(new Paragraph("\n"));
//            PdfPCell cell = new PdfPCell(l);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setColspan(10);
//            cell.setBorder(0);
//            table.addCell(cell);
//            document.add(table);
            document.close();

//            if (document == null) {
//                Log.e("document", "null");
//                return;
//            }
            Log.e("pdf file", "" + pdfFile.length());
            String subject = Utils.getStringSetting("email_supplier_subject")
                    + " " + orderInfo.getOrDefault("orderNumber", "");
            String body = Utils.getStringSetting("email_supplier_body")
                    .replace("[1]", Utils.getSetSetting(orderInfo.getOrDefault("supplier", ""))[3]);
            Intent printIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            if (toView) {
                printIntent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(pdfFile);
                printIntent.setDataAndType(uri, "application/pdf");
            } else {
                printIntent.setType("application/pdf");
                printIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                //printIntent.putExtra(Intent.EXTRA_TEXT, body);
                printIntent.putExtra(Intent.EXTRA_TEXT,
                        Html.fromHtml(body
                                , Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
                printIntent.putExtra(Intent.EXTRA_HTML_TEXT, body);
                printIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{orderInfo.getOrDefault("email", "")});
                String[] bcc = {"prod@aguadeoro.ch"};
                printIntent.putExtra(Intent.EXTRA_BCC, bcc);
                ArrayList<Uri> uris = new ArrayList<Uri>();
                uris.add(Uri.fromFile(pdfFile));

                // include pic if any
                for (int i = 0; i < orderCompList.size(); i++) {
                    String[] orderComp = orderCompList.get(i);
                    File filename = new File(
                            Environment.getExternalStorageDirectory()
                                    + "/03_supplierorders/", "Order "
                            + orderInfo.getOrDefault("orderNumber", "") + " Article " + (i + 1)
                            + ".jpeg");
                    if (loadComponentPic(orderComp[13], filename)) {
                        uris.add(Uri.fromFile(filename));
                    }
                }// end for loop
                if (!pdfFile.exists() || !pdfFile.canRead()) {
                    Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show();
                }

                ArrayList<Uri> uriList = new ArrayList<>();
                Uri uri = FileProvider.getUriForFile(context, "com.aguadeoro.android.fileprovider", pdfFile);
                printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                printIntent.setDataAndType(uri, context.getContentResolver().getType(uri));
                Log.d("test uri", "" + uri);
                //printIntent.putExtra(Intent.EXTRA_STREAM, uri);
                uriList.add(uri);

                for (String[] comp : orderCompList) {
                    Query q = new Query("select * from OrderComponentPic "
                            + "where OrderComponentID = "
                            + comp[0]);
                    q.execute();
                    if (q.getRes().size() > 0) {
                        byte[] image = Base64.decode(q.getRes().get(0).get("" + 1), Base64.DEFAULT);
                        Bitmap b = BitmapFactory.decodeByteArray(image, 0, image.length);
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + comp[0] + "_" + timeStamp;
                        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        File imageFile = File.createTempFile(
                                imageFileName,
                                ".jpg",
                                storageDir
                        );
                        FileOutputStream fout = new FileOutputStream(imageFile);
                        b.compress(Bitmap.CompressFormat.JPEG, 80, fout);
                        fout.flush();
                        fout.close();
                        MediaStore.Images.Media.insertImage(context.getContentResolver(), imageFile.getAbsolutePath(), imageFile.getName(), imageFile.getName());
                        Uri uri2 = getImageContentUri(context, imageFile);
                        uriList.add(uri2);
                    }
                }
                printIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                printIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Log.d("test", "" + uriList);
                printIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
            }
//            context.startActivity(printIntent);
            if (toView) {
                context.startActivity(printIntent);
            } else {
                context.startActivityForResult(printIntent, SEND_SUPPLIER_CODE);
            }
            document.close();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static Document generateSupplierOrder(Document document, Activity context, String orderType, Map<String, String> orderInfo,
                                                 ArrayList<String[]> orderCompList, boolean toView, String name, ArrayList<String> links) {

        Log.e("order type", orderType);
        Log.e("order info", orderInfo.toString());
        for (String[] orderComp : orderCompList) {
            Log.e("order comp list", Arrays.toString(orderComp));
        }
//        Log.e("order component list", Arrays.toString(orderCompList));
        try {
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                askForPermission(2);
            }
            verifyStoragePermissions(Utils.context);
            ArrayList<Map<String, String>> res = null;
            String query;
            if (orderType.equals(ORD_REPAIR)) {
                query = "select TextField from E_Textes where Key ='supplier_repair_0' or Key='supplier_repair_1' or Key='supplier_repair_1.1' or Key='supplier_repair_2' or Key='supplier_repair_2.1' or Key='supplier_repair_3' or Key='supplier_repair_3.1'";
                Query q = new Query(query);
                q.execute();
                res = q.getRes();
            } else {
                query = "select TextField from E_Textes where Key ='supplier_order_0' or Key='supplier_order_1' or Key='supplier_order_1.1' or Key='supplier_order_2' or Key='supplier_order_2.1' or Key='supplier_order_3' or Key='supplier_order_3.1'";
                Query q = new Query(query);
                q.execute();
                res = q.getRes();
            }
            Log.d("testText", "" + res.get(0).get("TextField") + " " + res.get(1).get("TextField"));
            Font font10 = new Font(FontFamily.HELVETICA, 10);
            Font font10B = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
            Font font11 = new Font(FontFamily.HELVETICA, 11);
            Font font12B = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
            Font font10BI = new Font(FontFamily.HELVETICA, 10, Font.BOLDITALIC);
            BaseColor grey = new CMYKColor(0, 0, 0, 0.60f);
            Font font9G = new Font(FontFamily.HELVETICA, 8);
            font9G.setColor(grey);

            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setSpacingBefore(0);
            table.setSpacingAfter(0);
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setNoWrap(false);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.getDefaultCell().setMinimumHeight(30f);
            table.setWidths(new float[]{78f, 76f, 69f, 69f, 38f, 38f, 38f,
                    76f, 57f, 69f});

            // logo
            InputStream in = context.getAssets().open("logo2.png");
            byte[] logo = IOUtils.toByteArray(in);
            Image l = Image.getInstance(logo);
            l.scalePercent(30);
            document.add(new Paragraph("\n"));
            PdfPCell cell = new PdfPCell(l);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(10);
            cell.setBorder(0);
            table.addCell(cell);


            // line 1: name1, date
            cell = new PdfPCell();
            cell.addElement(new Phrase("Numero Commande", font10));
            cell.addElement(new Phrase("Order Number", font9G));
            cell.setBorder(0);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setColspan(2);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(orderInfo.getOrDefault("orderNumber", ""), font11));
            cell.setColspan(3);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.addCell("");
            //table.addCell("");
            cell = new PdfPCell();
            cell.addElement(new Phrase("Date Commande:", font11));
            cell.addElement(new Phrase("Order Date:", font9G));
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setColspan(2);
            cell.setBorder(0);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(orderInfo.getOrDefault("createdDate", ""), font11));
            cell.setColspan(2);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);

            // line 2: name2, deadline
            table.addCell("");
            cell = new PdfPCell(new Phrase(""));
            cell.setColspan(4);
            cell.setBorder(0);
            cell.setBorderWidthBottom(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.addCell("");
            //table.addCell("");
            cell = new PdfPCell();
            cell.addElement(new Phrase("Délai:", font11));
            cell.addElement(new Phrase("Deadline", font9G));
            cell.setColspan(2);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBorder(0);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(orderInfo.getOrDefault("deadline", ""), font11));
            cell.setColspan(2);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);

            // line 3: address
            table.addCell(new Phrase("Destinataire", font10));
            cell = new PdfPCell(new Phrase(orderInfo.getOrDefault("responsable", ""), font11));
            cell.setColspan(4);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setMinimumHeight(35f);
            table.addCell(cell);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");

            // line 4: email
            table.addCell(new Phrase("Email", font10));
            cell = new PdfPCell(new Phrase(orderInfo.getOrDefault("email", ""), font11));
            cell.setColspan(4);
            cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");

            // line 7: order header
            if (orderType.equals(ORD_REPAIR)) {
                cell = new PdfPCell(new Phrase("REPARATION", font12B));
            } else if (orderType.equals(ORD_PREVIEW)) {
                cell = new PdfPCell(new Phrase("COMMANDE A CHOIX", font12B));
            } else {
                cell = new PdfPCell(new Phrase("Commande Fournisseur\nSupplier Order", font12B));
            }
            cell.setColspan(10);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setMinimumHeight(41f);
            table.addCell(cell);

            // line 8 ring header
            table.addCell("");
            cell = new PdfPCell();
            cell.addElement(new Phrase("    Article", font10BI));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("    Matière", font10BI));
            cell.addElement(new Phrase("    Material", font9G));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("Couleur", font10BI));
            cell.addElement(new Phrase("Colour", font9G));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("L", font10BI));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("H", font10BI));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("Taille", font10BI));
            cell.addElement(new Phrase("Size", font9G));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("    Surface", font10BI));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.addElement(new Phrase("Pierres", font10BI));
            cell.addElement(new Phrase("Stones", font9G));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            table.addCell("");
            // line 9 ring detail
            for (int i = 0; i < orderCompList.size(); i++) {
                String[] orderComp = orderCompList.get(i);
                cell = new PdfPCell(new Phrase("Article " + (i + 1), font10B));
                cell.setMinimumHeight(32f);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setBorder(0);
                table.addCell(cell);
                /*if(!name.equals("")){
                    table.addCell(new Phrase(name+"\n",font10));
                }*/
                OrderDetailActivity activity = (OrderDetailActivity) context;
                table.addCell(new Phrase((activity.componentList.get(0)[2] + ", " + orderComp[0] + "\n"), font10));
                table.addCell(new Phrase(orderComp[1], font10));
                table.addCell(new Phrase(orderComp[2], font10));
                table.addCell(new Phrase(orderComp[3], font10));
                table.addCell(new Phrase(orderComp[4], font10));
                table.addCell(new Phrase(orderComp[5], font10));
                table.addCell(new Phrase(orderComp[6], font10));
                cell = new PdfPCell(new Phrase(orderComp[7], font10));
                cell.setColspan(2);
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);

                // line stone detail
                if (!orderComp[14].equals(NON)) {
                    cell = new PdfPCell(new Phrase(orderComp[14], font10));
                    cell.setColspan(10);
                    cell.setBorder(0);
                    cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    table.addCell(cell);
                }

                if (!orderComp[8].equals(NON)) {

                    // line 11: engraving details
                    cell = new PdfPCell(new Phrase("Gravure:", font10));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorder(0);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(""));
                    if ((orderComp[9] + orderComp[10]).length() > 0) {
                        cell = new PdfPCell(new Phrase(orderComp[9] + ", "
                                + orderComp[10], font10));
                    }
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorder(0);
                    cell.setColspan(2);
                    table.addCell(cell);

                    //engraving text
                    Log.e("GRAVURE", "C'EST LE MOMENT DE GRAVER");
                    cell = new PdfPCell(new Phrase(orderComp[8]));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorder(0);
                    cell.setColspan(7);
                    table.addCell(cell);
                    //engraving text preview line
                    cell = new PdfPCell(new Phrase("Aperçu:", font10));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorder(0);
                    table.addCell(cell);
                    try {
                        in = context.getAssets().open(orderComp[10]);
                        byte[] b = IOUtils.toByteArray(in);
                        BaseFont font = BaseFont.createFont(orderComp[10]
                                + ".ttf", BaseFont.CP1252, true, true, b, null);
                        if (orderComp[10].equals(FONTS[1])
                                || orderComp[10].equals(FONTS[2]))
                            cell = new PdfPCell(new Phrase(orderComp[8],
                                    new Font(font, 30)));
                        else if (orderComp[10].equals(FONTS[3])
                                || orderComp[10].equals(FONTS[5])
                                || orderComp[10].equals(FONTS[6]))
                            cell = new PdfPCell(new Phrase(orderComp[8],
                                    new Font(font, 24)));
                        else
                            cell = new PdfPCell(new Phrase(orderComp[8],
                                    new Font(font, 19)));
                    } catch (Exception e) {
                        cell = new PdfPCell(new Phrase(orderComp[8]));
                    }
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorder(0);
                    cell.setColspan(9);
                    table.addCell(cell);
                }

                // line 12 remark
                if (!orderComp[11].equals(NON)) {
                    cell = new PdfPCell(new Phrase("Observations:", font10));
                    cell.setMinimumHeight(32f);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorder(0);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(orderComp[11], font11));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorder(0);
                    cell.setColspan(9);
                    table.addCell(cell);
                    if (!"".equals(orderComp[12])) {
                        table.addCell("");
                        cell = new PdfPCell(new Phrase(orderComp[12], font11));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                        cell.setBorder(0);
                        cell.setColspan(9);
                        table.addCell(cell);
                    }

                } else if (!"".equals(orderComp[12])) {
                    cell = new PdfPCell(new Phrase("Observations:", font10));
                    cell.setMinimumHeight(32f);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorder(0);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(orderComp[12], font11));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorder(0);
                    cell.setColspan(9);
                    table.addCell(cell);
                }
                table.addCell("");
                table.completeRow();
            }// end for loop

            if (!(res.get(0).get("TextField").equals(""))) {
                cell = new PdfPCell(
                        new Phrase(res.get(0).get("TextField"), font11));
                cell.setColspan(7);
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);

                table.addCell("");
                table.completeRow();
            }

            if (!(res.get(1).get("TextField").equals(""))) {
                cell = new PdfPCell(
                        new Phrase(res.get(1).get("TextField"), font10BI));
                cell.setColspan(7);
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);

                table.addCell("");
                table.completeRow();
            }

            if (!(res.get(2).get("TextField").equals(""))) {
                cell = new PdfPCell(
                        new Phrase(res.get(2).get("TextField"), font10));
                cell.setColspan(7);
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);

                table.addCell("");
                table.completeRow();
            }

            if (!(res.get(3).get("TextField").equals(""))) {
                cell = new PdfPCell(
                        new Phrase(res.get(3).get("TextField"), font10BI));
                cell.setColspan(7);
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);

                table.addCell("");
                table.completeRow();
            }

            if (!(res.get(4).get("TextField").equals(""))) {
                cell = new PdfPCell(
                        new Phrase(res.get(4).get("TextField"), font10));
                cell.setColspan(7);
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);

                table.addCell("");
                table.completeRow();
            }

            if (!(res.get(5).get("TextField").equals(""))) {
                cell = new PdfPCell(
                        new Phrase(res.get(5).get("TextField"), font10BI));
                cell.setColspan(7);
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);

                table.addCell("");
                table.completeRow();
            }

            if (!(res.get(6).get("TextField").equals(""))) {
                cell = new PdfPCell(
                        new Phrase(res.get(6).get("TextField"), font10));
                cell.setColspan(7);
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);

                table.addCell("");
                table.completeRow();
            }

            cell = new PdfPCell(
                    new Phrase("Images : ", font10));
            cell.setColspan(7);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            table.addCell("");
            table.completeRow();

            for (String link : links) {
                Anchor anchor = new Anchor(link, font10);
                anchor.setReference(link);
                cell = new PdfPCell(anchor);
                cell.setColspan(7);
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);
                table.completeRow();
            }

            // footer
            cell = new PdfPCell(
                    new Phrase(
                            "AGUA DE ORO Sarl, Grand-Rue 21, 1204 Genève, SALES@AGUADEORO.CH"));
            cell.setColspan(10);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setFixedHeight(130f);
            table.addCell(cell);
            document.add(table);

//            document.close();
            return document;
        } catch (Exception e) {
            Log.e("error", "error when creating document");
            e.printStackTrace();
            return null;

        }
    }

    public static void sendReview(Activity context, String lang, String address) {
        Query q;
        String query;
        boolean success;
        if (lang.equals("fr")) {
            query = "select OptionValue from Email where OptionKey= 'email_review_subject_fr' or OptionKey= 'email_review_body_fr';";
        } else {
            query = "select OptionValue from Email where OptionKey= 'email_review_subject_en' or OptionKey= 'email_review_body_en';";
        }
        q = new Query(query);
        success = q.execute();
        ArrayList<Map<String, String>> result = q.getRes();
        String subject = result.get(0).get("" + 0);
        String body = result.get(1).get("" + 0);
        Intent it = new Intent(Intent.ACTION_SEND_MULTIPLE);
        it.setType("application/pdf");
        it.putExtra(Intent.EXTRA_SUBJECT, subject);
        it.putExtra(Intent.EXTRA_TEXT, body);
        it.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        context.startActivityForResult(it, 0);
    }

    //try to save the pic to local, return false if no pic
    public static boolean loadComponentPic(String compID, File filename) {
        Query query = new Query(
                "select * from OrderComponentPic where OrderComponentID = "
                        + compID);
        if (!query.execute()) {
            Toast.makeText(context,
                    context.getString(R.string.error_retrieving_data),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        ArrayList<Map<String, String>> result = query.getRes();
        if (result.size() < 1) {
            return false;
        }
        byte[] image = Base64.decode(query.getRes().get(0).get("1"),
                Base64.DEFAULT);
        try {
            OutputStream fOut = new FileOutputStream(filename);
            fOut.write(image);
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //try to save pics to local storage, and return number of pic saved (0 or more)
    public static int loadHistoryPic(String histID, String filename) {
        Query query = new Query(
                "select * from CustomerHistoryPic where HistoryID = "
                        + histID);
        if (!query.execute()) {
            Toast.makeText(context,
                    context.getString(R.string.error_retrieving_data),
                    Toast.LENGTH_LONG).show();
            return 0;
        }
        ArrayList<Map<String, String>> result = query.getRes();
        if (result.size() < 1) {
            return 0;
        }
        for (int i = 0; i < result.size(); i++) {
            Map<String, String> line = result.get(i);
            byte[] image = Base64.decode(line.get("2"),
                    Base64.DEFAULT);
            int picID = Integer.parseInt(line.get("0"));
            try {
                OutputStream fOut = new FileOutputStream(filename + "_" + picID);
                fOut.write(image);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return result.size();
    }

    public static int getSelectedIndex(String s, String[] list) {
        for (int i = 0; i < list.length; i++) {
            if (s.equals(list[i]))
                return i;
        }
        return 0;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
