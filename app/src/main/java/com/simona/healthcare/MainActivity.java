package com.simona.healthcare;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.simona.healthcare.category.CategoriesFragment;
import com.simona.healthcare.event.EventsFragment;
import com.simona.healthcare.exercise.Exercise;
import com.simona.healthcare.exercise.ExercisesFragment;
import com.simona.healthcare.playing.PlayBarFragment;
import com.simona.healthcare.program.Program;
import com.simona.healthcare.program.ProgramsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        PlayBarFragment bar = PlayBarFragment.getInstance();
        getFragmentManager().beginTransaction().replace(R.id.barContent, bar).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mCurrentFragment instanceof ExercisesFragment) {

        }

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                add();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_categ:
                fragment = CategoriesFragment.newInstance(0);
                break;
            case R.id.nav_prog:
                fragment = ProgramsFragment.newInstance(0);
                break;
            case R.id.nav_ex:
                fragment = ExercisesFragment.newInstance(0);
                break;
            case R.id.nav_events:
                fragment = EventsFragment.newInstance();
                break;
            case R.id.nav_recipes:
                fragment = CategoriesFragment.newInstance(0);
                break;
        }

        loadFragment(fragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFragment(Fragment fragment) {
        mCurrentFragment = fragment;
        getFragmentManager().beginTransaction().replace(R.id.mainContent, fragment).commit();
    }

    private void add() {
        if (mCurrentFragment instanceof ExercisesFragment) {
            ((ExercisesFragment) mCurrentFragment).add();
        }

        if (mCurrentFragment instanceof CategoriesFragment) {
            ((CategoriesFragment) mCurrentFragment).add();
        }

        if (mCurrentFragment instanceof ProgramsFragment) {
            ((ProgramsFragment) mCurrentFragment).add();
        }

        if (mCurrentFragment instanceof EventsFragment) {
            ((EventsFragment) mCurrentFragment).add();
        }
    }
}
