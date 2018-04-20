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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.UserData;
import com.thecupboardapp.cupboard.fragments.CupboardFragment;
import com.thecupboardapp.cupboard.fragments.DashboardFragment;
import com.thecupboardapp.cupboard.fragments.SListsFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "HomeActivity";
    public static final int SIGN_IN_REQUEST_CODE = 1;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private View mHeaderView;

    private int currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentFragment = R.layout.activity_home;
        setContentView(R.layout.activity_home);

        mToolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mHeaderView = mNavigationView.getHeaderView(0);

        // Set the home screen to be up first
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragment_container, new DashboardFragment()).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setSupportActionBar(mToolbar);

        UserData.get(this);

        // Add the toggle to the appbar.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set up the navigation drawer.
        mNavigationView.setNavigationItemSelectedListener(this);

        // Set the email and id if the user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            setHeaderText(email, id);
        }

        mHeaderView.setOnClickListener(v -> {
            Intent intent = SignInActivity.newIntent(HomeActivity.this);
            startActivityForResult(intent, SIGN_IN_REQUEST_CODE);
        });
    }

    @Override
    public void onBackPressed() {
        // If the drawer is open and back is pressed, close it.
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
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
        if (currentFragment == item.getItemId()) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = null;

        switch(item.getItemId()) {
            case R.id.nav_dashboard: {
                fragment = new DashboardFragment();
                currentFragment = R.id.nav_dashboard;
                break;
            }
            case R.id.nav_cupboard: {
                fragment = new CupboardFragment();
                currentFragment = R.id.nav_cupboard;
                break;
            }
            // case R.id.nav_recipes: {
            //     fragment = new RecipesFragment();
            //     break;
            // }
            case R.id.nav_slists: {
                fragment = new SListsFragment();
                currentFragment = R.id.nav_slists;
                break;
            }
            case R.id.nav_logout: {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
                recreate();
                break;
            }
            default: {
                break;
            }
        }

        if (fragment != null) {
            fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        return false;
    }

    private void setHeaderText(String primaryText, String secondaryText) {
        TextView headerPrimary = mHeaderView.findViewById(R.id.nav_header_primary);
        TextView headerSecondary = mHeaderView.findViewById(R.id.nav_header_secondary);

        headerPrimary.setText(primaryText);
        headerSecondary.setText(secondaryText);
    }
}
