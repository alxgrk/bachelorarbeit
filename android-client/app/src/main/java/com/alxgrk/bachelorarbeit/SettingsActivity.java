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

    public static final String SHARED_PREF_NAME = "BACHELOR_APP_WIDE_PREFS";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar();

        prefs = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        EditText prefEntryUrl = findViewById(R.id.pref_entry_url);
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
