package com.example.mitkademayaldvirelay;

import static com.example.mitkademayaldvirelay.R.layout.activity_main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mitkademayaldvirelay.adapters.VideoListAdapter;
import com.example.mitkademayaldvirelay.classes.Video;
import com.example.mitkademayaldvirelay.classes.VideoManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Switch nightSwitch;
    private SearchView searchView;
    private int currentMenuItemId = R.id.nav_home;
    private VideoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        initUI();

        setupRecyclerView();

        setupToolbarAndDrawer();

        setupSearchView();

        loadVideosFromJSON();

        setupNightModeSwitch();

        setupBottomNavigationView();

        // Set the initial checked item in the navigation view
        if (savedInstanceState == null) {
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void initUI() {
        searchView = findViewById(R.id.searchView);
        drawerLayout = findViewById(R.id.drawer_layout);
    }

    private void setupRecyclerView() {
        RecyclerView videoList = findViewById(R.id.videoList);
        adapter = new VideoListAdapter(this);
        videoList.setAdapter(adapter);
        videoList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });
    }

    private void loadVideosFromJSON() {
        String jsonData = loadJSONFromAsset();
        if (jsonData != null) {
            List<Video> videos = parseVideosFromJSON(jsonData);
            VideoManager.getVideoManager().setVideos(videos);
            adapter.setVideos(videos);
        }
    }

    private List<Video> parseVideosFromJSON(String jsonData) {
        Gson gson = new Gson();
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
        Type videoListType = new TypeToken<ArrayList<Video>>() {}.getType();
        List<Video> videoList = gson.fromJson(jsonObject.get("Videos"), videoListType);
        for (Video video : videoList) {
            if (video.getComments() == null) {
                video.setComments(new ArrayList<>());
            }
        }
        return videoList;
    }

    private void setupNightModeSwitch() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        MenuItem switchItem = navigationView.getMenu().findItem(R.id.nav_switch_night);
        nightSwitch = switchItem.getActionView().findViewById(R.id.night_switch);

        // Initialize switch state
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        nightSwitch.setChecked(nightMode == AppCompatDelegate.MODE_NIGHT_YES);

        nightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Toast.makeText(MainActivity.this, "Night mode enabled", Toast.LENGTH_SHORT).show();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Toast.makeText(MainActivity.this, "Night mode disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getResources().getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }
    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    // Already on Home, no need to start a new activity
                }
                else if (id == R.id.nav_add_video) {
                    // Implement login activity
                    // Intent intent = new Intent(this, LoginActivity.class);
                    // startActivity(intent);
                }else if (id == R.id.nav_login) {
                    // Implement login activity
                    // Intent intent = new Intent(this, LoginActivity.class);
                    // startActivity(intent);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        currentMenuItemId = id;
        if (id == R.id.nav_close) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            // No need to handle result, as VideoManager is a singleton holding the updated data
            adapter.notifyDataSetChanged();
        }
    }
}
