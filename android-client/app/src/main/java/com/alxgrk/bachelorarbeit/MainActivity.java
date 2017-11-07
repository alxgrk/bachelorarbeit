package com.alxgrk.bachelorarbeit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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

import com.alxgrk.bachelorarbeit.accounts.Account;
import com.alxgrk.bachelorarbeit.accounts.collection.AccountsFragment;
import com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.organizations.Organization;
import com.alxgrk.bachelorarbeit.organizations.collection.OrganizationsFragment;
import com.alxgrk.bachelorarbeit.resources.Resource;
import com.alxgrk.bachelorarbeit.resources.collection.ResourcesFragment;
import com.alxgrk.bachelorarbeit.root.RootFragment;
import com.alxgrk.bachelorarbeit.shared.AbstractFragment;
import com.alxgrk.bachelorarbeit.shared.CreationFragment;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AbstractFragment.OnFragmentInteractionListener,
        CreationFragment.OnFragmentInteractionListener {

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

    private void transition(Fragment from, Fragment to) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.main_fragment_layout, to)
                .addToBackStack(null)
                .hide(from)
                .commit();
    }

    @Override
    public <F extends AbstractFragment> void onFragmentInteraction(F abstractFragment,
                                                                   List<Link> links,
                                                                   boolean visible) {
        List<Link> createLink = Lists.newArrayList(Collections2.filter(links, l ->
                PossibleRelation.CREATE.toString().equalsIgnoreCase(l.getRel())));

        FloatingActionButton fab = findViewById(R.id.fab);
        if (1 == createLink.size() && visible) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(view -> {
                if (abstractFragment instanceof RootFragment) {
                    // not doing anything yet
                } else {
                    CreationFragment creationFragment = null;

                    if (abstractFragment instanceof AccountsFragment) {
                        creationFragment = CreationFragment.newInstance(createLink.get(0),
                                HateoasMediaType.ACCOUNT_TYPE, Account.class, R.layout.add_screen_account);
                    } else if (abstractFragment instanceof OrganizationsFragment) {
                        creationFragment = CreationFragment.newInstance(createLink.get(0),
                                HateoasMediaType.ORGANIZATION_TYPE, Organization.class, R.layout.add_screen_org);
                    } else if (abstractFragment instanceof ResourcesFragment) {
                        creationFragment = CreationFragment.newInstance(createLink.get(0),
                                HateoasMediaType.RESOURCE_TYPE, Resource.class, R.layout.add_screen_resource);
                    }

                    transition(abstractFragment, creationFragment);
                }
            });
        } else {
            fab.setVisibility(View.GONE);
            fab.setOnClickListener(null);
        }
    }

    @Override
    public void onFragmentInteraction(CreationFragment creationFragment, boolean visible) {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(visible ? View.GONE : View.VISIBLE);
    }
}
