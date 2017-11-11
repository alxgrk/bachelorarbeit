package com.alxgrk.bachelorarbeit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class SettingsActivity extends AppCompatActivity {

    public static final String ENTRY_URL_KEY = "ENTRY_URL";

    public static final String ENTRY_URL_DEFAULT = "http://alxgrk-bachelor-level3.eu-central-1.elasticbeanstalk.com/";

    public static final String ENTRY_URL_KEY_2 = "ENTRY_URL_2";

    public static final String ENTRY_URL_DEFAULT_2 = "http://alxgrk-bachelor-level2.eu-central-1.elasticbeanstalk.com/";

    public static final String ENTRY_URL_KEY_1 = "ENTRY_URL_1";

    public static final String ENTRY_URL_DEFAULT_1 = "http://alxgrk-bachelor-level1.eu-central-1.elasticbeanstalk.com/";

    public static final String SHARED_PREF_NAME = "BACHELOR_APP_WIDE_PREFS";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar();

        prefs = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        // --- FOR LEVEL 3 ---
        String hint = prefs.getString(SettingsActivity.ENTRY_URL_KEY, SettingsActivity.ENTRY_URL_DEFAULT);

        EditText prefEntryUrl = findViewById(R.id.pref_entry_url);
        prefEntryUrl.setHint(hint);
        prefEntryUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    URL url = new URL(s.toString());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(ENTRY_URL_KEY, url.toString()).apply();
                } catch (MalformedURLException e) {
                    Toast.makeText(SettingsActivity.this,
                            "Not saving enter text - no URL!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // --- FOR LEVEL 2 ---
        String hint2 = prefs.getString(SettingsActivity.ENTRY_URL_KEY_2, SettingsActivity.ENTRY_URL_DEFAULT_2);

        EditText prefEntryUrl2 = findViewById(R.id.pref_entry_url_2);
        prefEntryUrl2.setHint(hint2);
        prefEntryUrl2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    URL url = new URL(s.toString());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(ENTRY_URL_KEY_2, url.toString()).apply();
                } catch (MalformedURLException e) {
                    Toast.makeText(SettingsActivity.this,
                            "Not saving enter text - no URL!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // --- FOR LEVEL 1 ---
        String hint1 = prefs.getString(SettingsActivity.ENTRY_URL_KEY_1, SettingsActivity.ENTRY_URL_DEFAULT_1);

        EditText prefEntryUrl1 = findViewById(R.id.pref_entry_url_1);
        prefEntryUrl1.setHint(hint1);
        prefEntryUrl1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    URL url = new URL(s.toString());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(ENTRY_URL_KEY_1, url.toString()).apply();
                } catch (MalformedURLException e) {
                    Toast.makeText(SettingsActivity.this,
                            "Not saving enter text - no URL!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onOptionsItemSelected(item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
