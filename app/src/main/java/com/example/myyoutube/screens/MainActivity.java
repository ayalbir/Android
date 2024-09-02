package com.example.myyoutube.screens;

import static com.example.myyoutube.R.layout.activity_main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myyoutube.Helper;
import com.example.myyoutube.R;
import com.example.myyoutube.adapters.VideoListAdapter;
import com.example.myyoutube.entities.Video;
import com.example.myyoutube.login.logInScreen1;
import com.example.myyoutube.viewmodels.UserViewModel;
import com.example.myyoutube.viewmodels.VideosViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Field;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE_ADD_VIDEO = 1;
    public static final int REQUEST_CODE_EDIT_VIDEO = 2;
    public static boolean firstTime = true;
    SwipeRefreshLayout swipeRefreshLayout;
    private DrawerLayout drawerLayout;
    private SearchView searchView;
    private VideoListAdapter adapter;
    private VideosViewModel videosViewModel;
    private ProgressBar progressBar;
    private NavigationView navigationView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        initUtility();

        videosViewModel = VideosViewModel.getInstance();

        if (firstTime) {
            UserViewModel userViewModel = UserViewModel.getInstance();
            firstTime = false;
            userViewModel.get().observe(this, usersList -> {
                if (usersList != null) {
                    // Users fetched successfully, now proceed to load videos
                    setupViewModelAndUI();
                }
            });
        } else {
            setupViewModelAndUI();
        }

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initUtility() {
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        navigationView = findViewById(R.id.nav_view);
        CardView cardView = navigationView.getHeaderView(0).findViewById(R.id.cardview);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem profilePictureItem = bottomNavigationView.getMenu().findItem(R.id.nav_login);

        profilePictureItem.setIcon(R.drawable.login);
        profilePictureItem.setTitle("Login");
        cardView.setVisibility(View.INVISIBLE);

        if (Helper.getConnectedUser() != null) {
            Bitmap bitmap = decodeImage(Helper.getConnectedUser().getProfileImage());
            cardView.setVisibility(View.VISIBLE);
            ImageView navHeaderImageView = navigationView.getHeaderView(0).findViewById(R.id.IVHeaderProfilePic);
            TextView navHeaderTextView = navigationView.getHeaderView(0).findViewById(R.id.TVWelcomeMenu);
            if (bitmap != null) {
                navHeaderImageView.setImageBitmap(bitmap);
            }
            navHeaderTextView.setText("Hello " + Helper.getConnectedUser().getFirstName());
            profilePictureItem.setIcon(R.drawable.settings);
            profilePictureItem.setTitle("Account");
        }
        searchView = findViewById(R.id.searchView);
        drawerLayout = findViewById(R.id.drawer_layout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
    }

    private void setupViewModelAndUI() {
        setupRecyclerViewDB();
        setupToolbarAndDrawer();
        setupSearchView();
        setupNightModeSwitch();
        setupBottomNavigationView();
        progressBar.setVisibility(View.GONE);
    }

    private void setupRecyclerViewDB() {
        RecyclerView videoList = findViewById(R.id.videoList);
        adapter = new VideoListAdapter(this, videosViewModel);
        List<Video> v = videosViewModel.getVideosFromDao();
        adapter.setVideos(v);
        videoList.setAdapter(adapter);
        videoList.setLayoutManager(new LinearLayoutManager(this));
        videosViewModel.get().observe(this, videos -> {
            adapter.setVideos(videos);
            if (videos != null && !videos.isEmpty())
                swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        });
        swipeRefreshLayout.setOnRefreshListener(this::refreshVideos);
    }

    public void refreshVideos() {
        if (videosViewModel != null) {
            videosViewModel.get();
        }
    }

    private void setupToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    private void setupNightModeSwitch() {
        MenuItem btnNight = navigationView.getMenu().findItem(R.id.nav_switch_night);

        btnNight.setOnMenuItemClickListener(item -> {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
                // Switch to dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                // Switch to light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate(); // Recreate the activity to apply the new theme
            return true;
        });
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.custom_red));
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_add_video) {
                Intent addIntent = (Helper.getConnectedUser() != null)
                        ? new Intent(MainActivity.this, AddEditVideoActivity.class)
                        : new Intent(MainActivity.this, logInScreen1.class);
                startActivityForResult(addIntent, REQUEST_CODE_ADD_VIDEO);
                return true;
            } else if (id == R.id.nav_login) {
                Intent intent = (Helper.getConnectedUser() != null && Helper.getConnectedUser().getEmail() != null)
                        ? new Intent(MainActivity.this, UpdateDeleteUserActivity.class)
                        .putExtra("userEmail", Helper.getConnectedUser().getEmail())
                        : new Intent(MainActivity.this, logInScreen1.class);
                startActivity(intent);
            }
            return false;
        });
    }

    private Bitmap decodeImage(String encodedImage) {
        if (encodedImage != null) {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }
        return null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
    protected void onResume() {
        super.onResume();
        refreshVideos();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}