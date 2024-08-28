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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myyoutube.AppDB;
import com.example.myyoutube.R;
import com.example.myyoutube.adapters.VideoListAdapter;
import com.example.myyoutube.dao.VideoDao;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.entities.Video;
import com.example.myyoutube.login.logInScreen1;
import com.example.myyoutube.viewmodels.UserViewModel;
import com.example.myyoutube.viewmodels.VideosViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Field;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE_ADD_VIDEO = 1;
    public static final int REQUEST_CODE_EDIT_VIDEO = 2;
    public static List<Video> videos;
    public static List<User> users;
    public static boolean firstTime = true;
    private static User currentUser;
    private UserViewModel userViewModel;
    private DrawerLayout drawerLayout;
    private SearchView searchView;
    private VideoListAdapter adapter;
    private VideoDao videoDao;
    private VideosViewModel videosViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static User getCurrentUser() {
        return currentUser;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem profilePictureItem = bottomNavigationView.getMenu().findItem(R.id.nav_login);
        NavigationView navigationView = findViewById(R.id.nav_view);
        CardView cardView = navigationView.getHeaderView(0).findViewById(R.id.cardview);
        profilePictureItem.setIcon(R.drawable.login);
        profilePictureItem.setTitle("Login");
        cardView.setVisibility(View.INVISIBLE);
        if (UserViewModel.getConnectedUser() != null) {
            currentUser = UserViewModel.getConnectedUser();
        }
        if (getCurrentUser() != null) {
            assert currentUser != null;
            Bitmap bitmap = decodeImage(currentUser.getProfileImage());
            cardView.setVisibility(View.VISIBLE);
            ImageView navHeaderImageView = navigationView.getHeaderView(0).findViewById(R.id.IVHeaderProfilePic);
            TextView navHeaderTextView = navigationView.getHeaderView(0).findViewById(R.id.TVWelcomeMenu);
            if (bitmap != null) {
                navHeaderImageView.setImageBitmap(bitmap);
            }
            navHeaderTextView.setText("Hello " + currentUser.getFirstName());
            profilePictureItem.setIcon(R.drawable.settings);
            profilePictureItem.setTitle("Account");
        }

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
         if(firstTime){
            firstTime = false;
            userViewModel.get().observe(this, usersList -> {
                if (usersList != null) {
                    // Users fetched successfully, now proceed to load videos
                    videosViewModel = new ViewModelProvider(MainActivity.this).get(VideosViewModel.class);
                    setupRecyclerViewDB();
                    initUI();
                    setupToolbarAndDrawer();
                    setupSearchView();
                    setupNightModeSwitch();
                    setupBottomNavigationView();
                }
            });
        }
        else{
            videosViewModel = new ViewModelProvider(MainActivity.this).get(VideosViewModel.class);
            setupRecyclerViewDB();
            initUI();
            setupToolbarAndDrawer();
            setupSearchView();
            setupNightModeSwitch();
            setupBottomNavigationView();
        }

        if (savedInstanceState == null) {
            navigationView = findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void initUI() {
        searchView = findViewById(R.id.searchView);
        drawerLayout = findViewById(R.id.drawer_layout);
    }

    private void setupRecyclerViewDB() {
        RecyclerView videoList = findViewById(R.id.videoList);
        adapter = new VideoListAdapter(this, videosViewModel);
        videos = videosViewModel.get().getValue();
        if (videos != null) {
            adapter.setVideos(videos);
        }

        videosViewModel.get().observe(this, new Observer<List<Video>>() {
            @Override
            public void onChanged(List<Video> videos) {
                adapter.setVideos(videos);
                adapter.notifyDataSetChanged();
            }
        });

        videoList.setAdapter(adapter);
        videoList.setLayoutManager(new LinearLayoutManager(this));

        AppDB db = Room.databaseBuilder(getApplicationContext(),
                        AppDB.class, "VideoDB")
                .allowMainThreadQueries()
                .build();
        videoDao = db.videoDao();
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshVideos);
        refreshVideos();
    }

    private void refreshVideos() {
        if(videosViewModel != null){
            videosViewModel.get();
            videosViewModel.get().observe(this, videos -> {
                if (videos != null && adapter != null) {
                    adapter.setVideos(videos);
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
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


    private void setupNightModeSwitch() {
        NavigationView navigationView = findViewById(R.id.nav_view);
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
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_add_video) {
                    if (getCurrentUser() != null) {
                        Intent addIntent = new Intent(MainActivity.this, AddEditVideoActivity.class);
                        startActivityForResult(addIntent, REQUEST_CODE_ADD_VIDEO);
                    } else {
                        Intent addIntent = new Intent(MainActivity.this, logInScreen1.class);
                        startActivity(addIntent);
                    }
                    return true;
                } else if (id == R.id.nav_login) {
                    if (currentUser != null && currentUser.getEmail() != null) {
                        Intent intent = new Intent(MainActivity.this, UpdateDeleteUserActivity.class);
                        intent.putExtra("userEmail", currentUser.getEmail());
                        startActivity(intent);
                    } else {
                        currentUser = null;
                        Intent intent = new Intent(MainActivity.this, logInScreen1.class);
                        startActivity(intent);
                    }

                }
                return false;
            }
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
