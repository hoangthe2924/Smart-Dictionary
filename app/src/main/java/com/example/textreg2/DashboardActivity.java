package com.example.textreg2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ClipData;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.textreg2.searchword.DictionaryActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    CardView textReg;
    CardView dictionary;
    CardView aboutUs;
    CardView snapsearch;
    CardView help;
    TextView drawerText;

    FirebaseDatabase mDatabase;
    DatabaseReference mReference;

    String userName;
    String userEmail;
    String url = "https://github.com/tqtnk2000/Final_Dictionary";
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout=findViewById(R.id.drawer_main);
        navigationView=findViewById(R.id.nav_main);

        changeHeader();

        getSupportActionBar().show();

        navigationView.bringToFront();

        toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);

        navigationView.setNavigationItemSelectedListener(this);

        textReg=findViewById(R.id.textDetect);
        textReg.setOnClickListener(this);

        dictionary=findViewById(R.id.wordSearch);
        dictionary.setOnClickListener(this);

        snapsearch=findViewById(R.id.snapSearch);
        snapsearch.setOnClickListener(this);

        aboutUs=findViewById(R.id.aboutUs);
        aboutUs.setOnClickListener(this);

        help=findViewById(R.id.help);
        help.setOnClickListener(this);

    }

    private void changeHeader() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            Toast.makeText(DashboardActivity.this,"Welcome "+userEmail,Toast.LENGTH_SHORT).show();
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        final String emailID=userEmail.replaceAll("[-+.^:,@_]","");
        Query checkUser = reference.orderByChild("id").equalTo(emailID);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String emailFromDB= snapshot.child(emailID).child("email").getValue(String.class);
                    String phoneFromDB= snapshot.child(emailID).child("phone").getValue(String.class);
                    String usernameFromDB= snapshot.child(emailID).child("username").getValue(String.class);
                    View header = navigationView.getHeaderView(0);
                    TextView textEmail = (TextView) header.findViewById(R.id.drawer_textEmail);
                    TextView textName = (TextView) header.findViewById(R.id.drawer_textName);
                    textName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
                    textName.setTypeface(null, Typeface.BOLD);
                    textEmail.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                    textName.setText(usernameFromDB);
                    textEmail.setText(emailFromDB);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.textDetect)
        {
            Intent intent = new Intent(this,textRegActivity.class);
            startActivity(intent);
        }
        if (v.getId()==R.id.aboutUs)
        {
            Intent infoIntent=new Intent(this, InfoActivity.class);
            startActivity(infoIntent);
        }
        if (v.getId()==R.id.help)
        {
            Intent helpIntent = new Intent(Intent.ACTION_VIEW);
            helpIntent.setData(Uri.parse(url));
            startActivity(helpIntent);
        }
        if (v.getId()==R.id.wordSearch)
        {
            Intent searchIntent = new Intent(this, DictionaryActivity.class);
            startActivity(searchIntent);
        }
        if(v.getId()==R.id.snapSearch){
            Intent intent = new Intent(this,imageLabel.class);
            startActivity(intent);
//            Toast.makeText(this, "Hello, Bye", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_logout:
                logOut();
                return true;
            case R.id.nav_info:
                startUserProfileActivity();
                return true;
            case R.id.nav_favoriteWord:
                startUserFavoriteWordActivity();
                return true;

        }
        return true;
    }

    private void startUserFavoriteWordActivity() {
        Intent toFavoriteWord = new Intent(DashboardActivity.this,FavoriteWordActivity.class);
        startActivity(toFavoriteWord);
    }

    private void startUserProfileActivity() {
        Intent toUserProfile = new Intent(DashboardActivity.this,UserProfileActivity.class);
        startActivity(toUserProfile);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent toLogin = new Intent(DashboardActivity.this, LoginActivity.class);
        startActivity(toLogin);
    }
}