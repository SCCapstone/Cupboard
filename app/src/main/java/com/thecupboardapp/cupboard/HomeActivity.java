package com.thecupboardapp.cupboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "HomeActivity";
    private ImageView mNavHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//         Set the home screen to be up first
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragment_container, new HomeFragment()).commit();

        // Set up the navigation drawer.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Add the toggle to the appbar.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerview = navigationView.getHeaderView(0);
        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SignInActivity.newIntent(HomeActivity.this);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // If the drawer is open and back is pressed, close it.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fm = getSupportFragmentManager();
        Log.d(TAG, "result code: " + resultCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Navigate to the correct fragment
        if (id == R.id.nav_home) {
            fragment = fm.findFragmentById(R.id.home_fragment);
            if (fragment == null) {
                Log.d(TAG, "null fragment home fragment");
                fragment = new HomeFragment();
            }
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } else if (id == R.id.nav_cupboard) { // Handle cupboard
            fragment = fm.findFragmentById(R.id.cupboard_fragment);
            if (fragment == null) {
                fragment = new CupboardFragment();
            }
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } else if (id == R.id.nav_recipes) {
            fragment = fm.findFragmentById(R.id.recipes_fragment);
            if (fragment == null) {
                fragment = new RecipesFragment();
            }
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } else if (id == R.id.nav_lists) {
            fragment = fm.findFragmentById(R.id.lists_fragment);
            if (fragment == null) {
                fragment = new ShoppingListsFragment();
            }
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
