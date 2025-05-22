package com.example.dictionaryapp.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.dictionaryapp.Database.DatabaseCopyHelper;
import com.example.dictionaryapp.Fragment.FavoriteFragment;
import com.example.dictionaryapp.Fragment.HistoryFragment;
import com.example.dictionaryapp.Fragment.SearchFragment;
import com.example.dictionaryapp.Fragment.SettingsFragment;
import com.example.dictionaryapp.Fragment.TranslateFragment;
import com.example.dictionaryapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    DatabaseCopyHelper DatabaseCopyHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseCopyHelper.copyDatabaseIfNeeded(this, "dictionary1.db");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        loadFragment(new SearchFragment());

        bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        Fragment selectedFragment = null;
                        int id = item.getItemId();

                        if (id == R.id.nav_search) {
                            selectedFragment = new SearchFragment();
                        } else if (id == R.id.nav_translate) {
                            selectedFragment = new TranslateFragment();
                        } else if (id == R.id.nav_favorite) {
                            selectedFragment = new FavoriteFragment();
                        } else if (id == R.id.nav_history) {
                            selectedFragment = new HistoryFragment();
                        } else if (id == R.id.nav_settings) {
                            selectedFragment = new SettingsFragment();
                        }

                        if (selectedFragment != null) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, selectedFragment)
                                    .commit();
                            return true;
                        }
                        return false;
                    }
                });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
