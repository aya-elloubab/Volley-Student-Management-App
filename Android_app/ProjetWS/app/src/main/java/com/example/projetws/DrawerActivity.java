package com.example.projetws;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class DrawerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_add_etudiant) {
                    // Navigate to AddEtudiantFragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, new AddEtudiantFragment())
                            .commit();
                } else if (id == R.id.nav_view_students) {
                    // Navigate to EtudiantListFragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, new EtudiantListFragment())
                            .commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, new HomePageFragment())
                    .commit();
        }
    }
}