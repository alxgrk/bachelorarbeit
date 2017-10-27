package com.alxgrk.bachelorarbeit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alxgrk.bachelorarbeit.accounts.AccountsFragment;
import com.alxgrk.bachelorarbeit.organizations.OrganizationsFragment;
import com.alxgrk.bachelorarbeit.resources.ResourcesFragment;
import com.alxgrk.bachelorarbeit.root.RootFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RootFragment.OnFragmentInteractionListener,
        AccountsFragment.OnFragmentInteractionListener,
        OrganizationsFragment.OnFragmentInteractionListener,
        ResourcesFragment.OnFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final String GITHUB_LINK = "http://github.com/alxgrk/bachelorarbeit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            ProgressBar progressBar = findViewById(R.id.transition_progress);
            progressBar.setVisibility(View.VISIBLE);

            Fragment fragment = RootFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_layout, fragment)
                    .commit();
        }

        setUpUiComponents();
    }

    private void setUpUiComponents() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_level1) {
            Toast.makeText(this, "level one", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_level2) {
            Toast.makeText(this, "level two", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_level3) {
            Toast.makeText(this, "level three", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_github) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_LINK));
            startActivity(browserIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(RootFragment rootFragment) {

    }

    @Override
    public void onFragmentInteraction(AccountsFragment accountsFragment) {

    }

    @Override
    public void onFragmentInteraction(OrganizationsFragment organizationsFragment) {

    }

    @Override
    public void onFragmentInteraction(ResourcesFragment resourcesFragment) {

    }
}
