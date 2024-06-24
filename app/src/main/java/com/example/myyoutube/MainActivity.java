package com.example.myyoutube;

import static com.example.myyoutube.R.layout.activity_main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

import com.example.myyoutube.adapters.VideoListAdapter;
import com.example.myyoutube.classes.User;
import com.example.myyoutube.managers.UserManager;
import com.example.myyoutube.classes.Video;
import com.example.myyoutube.managers.VideoManager;
import com.example.myyoutube.login.logInScreen1;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE_ADD_VIDEO = 1;
    public static final int REQUEST_CODE_EDIT_VIDEO = 2;
    private DrawerLayout drawerLayout;
    private Switch nightSwitch;
    private SearchView searchView;
    private int currentMenuItemId = R.id.nav_home;
    private VideoListAdapter adapter;
    public static List<Video> videos;
    public static boolean firstTime = true;
    private static User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem profilePictureItem = bottomNavigationView.getMenu().findItem(R.id.nav_login);
        NavigationView navigationView = findViewById(R.id.nav_view);
        CardView cardView = navigationView.getHeaderView(0).findViewById(R.id.cardview);
        profilePictureItem.setIcon(R.drawable.login);
        profilePictureItem.setTitle("Login");
        cardView.setVisibility(View.INVISIBLE);
            if (email != null) {
                currentUser = UserManager.getUserByEmail(email);
            }
            if(getCurrentUser() != null) {
                Bitmap bitmap = decodeImage(currentUser.getProfileImage());
                cardView.setVisibility(View.VISIBLE);
                ImageView navHeaderImageView = navigationView.getHeaderView(0).findViewById(R.id.IVHeaderProfilePic);
                TextView navHeaderTextView = navigationView.getHeaderView(0).findViewById(R.id.TVWelcomeMenu);
                if (bitmap != null) {
                    navHeaderImageView.setImageBitmap(bitmap);
                }
                navHeaderTextView.setText("Hello " + currentUser.getUserName());
                profilePictureItem.setIcon(R.drawable.settings);
                profilePictureItem.setTitle("Account");

            }



        initUI();
        setupRecyclerView();
        setupToolbarAndDrawer();
        setupSearchView();
        loadVideosFromJSON();
        setupNightModeSwitch();
        setupBottomNavigationView();




        if (savedInstanceState == null) {
            navigationView = findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void initializeDefaultUsers() {
        if (UserManager.getUsers().isEmpty()) {
            User user1 = new User("Elay@gmail.com", "Elay", "12345678p", encodeImageToBase64(R.drawable.person1));
            User user2 = new User("Ayal@gmail.com", "Ayal", "12345678p", encodeImageToBase64(R.drawable.person2));
            User user3 = new User("Dvir@gmail.com", "Dvir", "12345678p", encodeImageToBase64(R.drawable.person3));

            UserManager.addUser(user1);
            UserManager.addUser(user2);
            UserManager.addUser(user3);
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
            if (firstTime) {
                videos = parseVideosFromJSON(jsonData);
                initializeDefaultUsers();
            }
            firstTime = false;
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

    public static User getCurrentUser() {
        return currentUser;
    }
    private Bitmap decodeImage(String encodedImage) {
        if (encodedImage != null) {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }
        return null;
    }

    private void setupNightModeSwitch() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        MenuItem switchItem = navigationView.getMenu().findItem(R.id.nav_switch_night);
        nightSwitch = switchItem.getActionView().findViewById(R.id.night_switch);

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
        bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.custom_red));
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    //do nothing
                } else if (id == R.id.nav_add_video) {
                    if(getCurrentUser() != null) {
                        Intent addIntent = new Intent(MainActivity.this, AddEditVideoActivity.class);
                        startActivityForResult(addIntent, REQUEST_CODE_ADD_VIDEO);
                    }
                    else {
                        Intent addIntent = new Intent(MainActivity.this, logInScreen1.class);
                        startActivity(addIntent);
                    }
                    return true;
                } else if (id == R.id.nav_login) {
                    if(currentUser != null && currentUser.getEmail() != null) {
                        Intent intent = new Intent(MainActivity.this, UpdateDeleteUserActivity.class);
                        intent.putExtra("userEmail", currentUser.getEmail());
                        startActivity(intent);
                    }
                    else{
                        currentUser = null;
                        Intent intent = new Intent(MainActivity.this, logInScreen1.class);
                        startActivity(intent);
                    }

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


    private String encodeImageToBase64(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            int updatedVideoId = data.getIntExtra("updatedVideoId", -1);
            if (updatedVideoId != -1) {
                Video updatedVideo = VideoManager.getVideoManager().getVideoById(updatedVideoId);
                if (updatedVideo != null) {
                    if (requestCode == REQUEST_CODE_ADD_VIDEO) {
                        adapter.addItem(updatedVideo);
                    } else if (requestCode == REQUEST_CODE_EDIT_VIDEO) {
                        updateVideo(updatedVideo);
                    }
                }
            }
        }
    }

    private void updateVideo(Video updatedVideo) {
        for (int i = 0; i < videos.size(); i++) {
            if (videos.get(i).getId() == updatedVideo.getId()) {
                videos.set(i, updatedVideo);
                adapter.updateItem(updatedVideo);
                break;
            }
        }
    }
}
