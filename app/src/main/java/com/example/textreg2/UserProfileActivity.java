package com.example.textreg2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    TextView profileBanner,profileName,profilePhone,profileEmail;
    FirebaseUser user;
    String userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profileBanner=findViewById(R.id.profile_banner);
        profileName=findViewById(R.id.profile_name);
        profilePhone=findViewById(R.id.profile_phone);
        profileEmail=findViewById(R.id.profile_email);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
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
                    //textName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                    //textName.setTypeface(null, Typeface.BOLD);
                    profileBanner.setText(usernameFromDB);
                    profileName.setText(usernameFromDB);
                    profileEmail.setText(emailFromDB);
                    profilePhone.setText(phoneFromDB);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}