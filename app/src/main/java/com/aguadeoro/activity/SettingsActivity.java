package com.aguadeoro.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aguadeoro.R;
import com.aguadeoro.utils.Utils;

import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            boolean isPassword = false;

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                if(index == -1){
                    index = listPreference.findIndexOfValue("Zurich");
                }
                if (!Utils.getStringSetting("store_selected").equals("Geneva")) {
                    if (listPreference.getEntries()[index].toString().equals("Geneva")) {
                        isPassword = true;
                        AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
                        builder.setTitle("enter password");
                        LinearLayout layout = new LinearLayout(preference.getContext());
                        layout.setOrientation(LinearLayout.VERTICAL);
                        EditText input = new EditText(preference.getContext());
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        layout.addView(input);
                        builder.setView(layout);
                        int finalIndex = index;
                        builder.setPositiveButton("OK", (dialog, which) -> {
                            boolean isPasswordCorrect = input.getText().toString().equals("aguadmin21");
                            if (isPasswordCorrect) {
                                preference.setSummary(listPreference.getEntries()[finalIndex]);
                            } else {
                                Toast.makeText(preference.getContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                                preference.setSummary("Zurich");
                                ((ListPreference) preference).setValue("Zurich");
                            }
                        });
                        builder.show();
                    }
                }
                if (!isPassword) {
                    preference.setSummary(listPreference.getEntries()[index]);
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !isXLargeTablet(context);
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setupSimplePreferencesScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sync) {
            Utils.syncSettingFromDatabase();
        }
        if (id == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return true;
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        addPreferencesFromResource(R.xml.pref_general);
        addPreferencesFromResource(R.xml.pref_thankyou);
        addPreferencesFromResource(R.xml.pref_customer_email);
        addPreferencesFromResource(R.xml.pref_order_ready_email);
        addPreferencesFromResource(R.xml.pref_order_closed_email);
        addPreferencesFromResource(R.xml.pref_invoice_email);
        addPreferencesFromResource(R.xml.pref_supplier_email);


        bindPreferenceSummaryToValue(findPreference("server_address"));
        ListPreference stores = (ListPreference) findPreference("store_selected");

        bindPreferenceSummaryToValue(findPreference("store_selected"));
        bindPreferenceSummaryToValue(findPreference("max_results"));
        bindPreferenceSummaryToValue(findPreference("tva_rate"));
        bindPreferenceSummaryToValue(findPreference("tva_number"));
        bindPreferenceSummaryToValue(findPreference("thankyou_template_en"));
        bindPreferenceSummaryToValue(findPreference("thankyou_template_fr"));
        bindPreferenceSummaryToValue(findPreference("email_supplier_subject"));
        bindPreferenceSummaryToValue(findPreference("email_supplier_body"));
        bindPreferenceSummaryToValue(findPreference("email_cust_subject_en"));
        bindPreferenceSummaryToValue(findPreference("email_cust_body_en"));
        bindPreferenceSummaryToValue(findPreference("email_cust_subject_fr"));
        bindPreferenceSummaryToValue(findPreference("email_cust_body_fr"));
        bindPreferenceSummaryToValue(findPreference("email_order_ready_subject_en"));
        bindPreferenceSummaryToValue(findPreference("email_order_ready_body_en"));
        bindPreferenceSummaryToValue(findPreference("email_order_closed_subject_fr"));
        bindPreferenceSummaryToValue(findPreference("email_order_closed_body_fr"));
        bindPreferenceSummaryToValue(findPreference("email_invoice_subject_fr"));
        bindPreferenceSummaryToValue(findPreference("email_invoice_body_jewel_fr"));
        bindPreferenceSummaryToValue(findPreference("email_invoice_body_alliances_fr"));
        bindPreferenceSummaryToValue(findPreference("email_invoice_body_bague_fr"));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("server_address"));
            bindPreferenceSummaryToValue(findPreference("store_selected"));
            bindPreferenceSummaryToValue(findPreference("max_results"));
            bindPreferenceSummaryToValue(findPreference("tva_rate"));
            bindPreferenceSummaryToValue(findPreference("tva_number"));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ThankYouNotePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_thankyou);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("thankyou_template_en"));
            bindPreferenceSummaryToValue(findPreference("thankyou_template_fr"));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class CustomerEmailPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_customer_email);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("email_cust_subject_en"));
            bindPreferenceSummaryToValue(findPreference("email_cust_body_en"));
            bindPreferenceSummaryToValue(findPreference("email_cust_subject_fr"));
            bindPreferenceSummaryToValue(findPreference("email_cust_body_fr"));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class OrderReadyEmailPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_order_ready_email);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("email_order_ready_subject_en"));
            bindPreferenceSummaryToValue(findPreference("email_order_ready_body_en"));
            bindPreferenceSummaryToValue(findPreference("email_order_ready_subject_fr"));
            bindPreferenceSummaryToValue(findPreference("email_order_ready_body_fr"));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class OrderClosedEmailPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_order_closed_email);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("email_order_closed_subject_en"));
            bindPreferenceSummaryToValue(findPreference("email_order_closed_body_en"));
            bindPreferenceSummaryToValue(findPreference("email_order_closed_subject_fr"));
            bindPreferenceSummaryToValue(findPreference("email_order_closed_body_fr"));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class InvoiceEmailPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_invoice_email);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("email_invoice_subject_en"));
            bindPreferenceSummaryToValue(findPreference("email_invoice_body_jewel_en"));
            bindPreferenceSummaryToValue(findPreference("email_invoice_body_alliances_en"));
            bindPreferenceSummaryToValue(findPreference("email_invoice_body_bague_en"));
            bindPreferenceSummaryToValue(findPreference("email_invoice_subject_fr"));
            bindPreferenceSummaryToValue(findPreference("email_invoice_body_jewel_fr"));
            bindPreferenceSummaryToValue(findPreference("email_invoice_body_alliances_fr"));
            bindPreferenceSummaryToValue(findPreference("email_invoice_body_bague_fr"));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SupplierEmailPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_supplier_email);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("email_supplier_subject"));
            bindPreferenceSummaryToValue(findPreference("email_supplier_body"));
        }
    }
}