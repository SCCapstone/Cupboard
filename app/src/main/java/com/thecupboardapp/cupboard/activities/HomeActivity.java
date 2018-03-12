package com.thecupboardapp.cupboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.UserData;
import com.thecupboardapp.cupboard.fragments.CupboardFragment;
import com.thecupboardapp.cupboard.fragments.HomeFragment;
import com.thecupboardapp.cupboard.fragments.SListsFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "HomeActivity";

    static final int SIGN_IN_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        UserData.get(this);

        // Set the home screen to be up first
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragment_container, new HomeFragment()).commit();

        // Set up the navigation drawer.
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Add the toggle to the appbar.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        View headerView = navView.getHeaderView(0);

        // Set the email and id if the user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            setHeaderText(email, id);
        }

        headerView.setOnClickListener(v -> {
            Intent intent = SignInActivity.newIntent(HomeActivity.this);
            startActivityForResult(intent, SIGN_IN_REQUEST_CODE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            switch (resultCode) {
                case SignInActivity.SIGN_IN_RESULT_CODE:
                    Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                    break;
                case SignInActivity.NEW_ACCOUNT_RESULT_CODE:
                    Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    return;
            }
            recreate();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = null;

        switch(item.getItemId()) {
            case R.id.nav_home: {
                fragment = new HomeFragment();
                break;
            }
            case R.id.nav_cupboard: {
                fragment = new CupboardFragment();
                break;
            }
            // case R.id.nav_recipes: {
            //     fragment = new RecipesFragment();
            //     break;
            // }
            case R.id.nav_lists: {
                fragment = new SListsFragment();
                break;
            }
            case R.id.nav_logout: {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
                UserData.get(this).reset();
                recreate();
                break;
            }
            default: {
                break;
            }
        }
        if (fragment != null) {
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setHeaderText(String primaryText, String secondaryText) {
        NavigationView navView = findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        TextView primaryEditText = headerView.findViewById(R.id.nav_header_primary);
        TextView secondaryEditText = headerView.findViewById(R.id.nav_header_secondary);

        primaryEditText.setText(primaryText);
        secondaryEditText.setText(secondaryText);
    }
}
