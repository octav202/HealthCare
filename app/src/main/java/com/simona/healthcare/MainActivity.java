package com.simona.healthcare;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.simona.healthcare.category.CategoriesFragment;
import com.simona.healthcare.event.EventsFragment;
import com.simona.healthcare.exercise.ExercisesFragment;
import com.simona.healthcare.playing.PlayBarFragment;
import com.simona.healthcare.program.ProgramsFragment;
import com.simona.healthcare.recipe.RecipeFragment;
import com.simona.healthcare.settings.SettingsFragment;

import static com.simona.healthcare.utils.Constants.EXTRA_KEY;
import static com.simona.healthcare.utils.Constants.EXTRA_KEY_EVENTS;
import static com.simona.healthcare.utils.Constants.GALLERY_INTENT;
import static com.simona.healthcare.utils.Constants.TAG;
import static com.simona.healthcare.utils.Constants.TYPE_EXERCISE;
import static com.simona.healthcare.utils.Constants.TYPE_RECIPE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment mCurrentFragment;

    // PhotoPicker
    private int mRequestedType;

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

        if (getIntent() != null) {
            String tab = getIntent().getStringExtra(EXTRA_KEY);
            if (tab != null && tab.equals(EXTRA_KEY_EVENTS)) {
                loadFragment(EventsFragment.newInstance());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart()");
        if (getIntent() != null) {
            String tab = getIntent().getStringExtra(EXTRA_KEY);
            if (tab != null && tab.equals(EXTRA_KEY_EVENTS)) {
                loadFragment(EventsFragment.newInstance());
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent()");
        if (intent != null) {
            String tab = intent.getStringExtra(EXTRA_KEY);
            if (tab != null && tab.equals(EXTRA_KEY_EVENTS)) {
                loadFragment(EventsFragment.newInstance());
            }
        }
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
            case R.id.nav_settings:
                fragment = SettingsFragment.newInstance(0);
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
                fragment = RecipeFragment.newInstance(0);
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

        if (mCurrentFragment instanceof RecipeFragment) {
            ((RecipeFragment) mCurrentFragment).add();
        }
    }

    /**
     * Choose image from gallery for an exercise.
     * @param type Object Type - Exercise / Recipe.
     */
    public void openGallery(int type) {
        mRequestedType = type;
        if (Build.VERSION.SDK_INT <19){
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, GALLERY_INTENT);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, GALLERY_INTENT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri imageUri = null;
            if (Build.VERSION.SDK_INT < 19) {
                imageUri = data.getData();
            } else {
                imageUri = data.getData();
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                try {
                    getApplicationContext().getContentResolver().takePersistableUriPermission(imageUri, takeFlags);

                    switch (mRequestedType) {
                        case TYPE_EXERCISE:
                            // Image was requested by the EditRecipeDialog - update dialog image
                            ((ExercisesFragment) mCurrentFragment).setDialogImageUri(imageUri);
                            break;

                        case TYPE_RECIPE:
                            // Image was requested by the EditRecipeDialog - update dialog image
                            ((RecipeFragment) mCurrentFragment).setDialogImageUri(imageUri);
                            break;
                        default:
                            break;
                    }

                    mRequestedType = 0;
                }
                catch (SecurityException e){
                    e.printStackTrace();
                }
            }
        }

    }
}
