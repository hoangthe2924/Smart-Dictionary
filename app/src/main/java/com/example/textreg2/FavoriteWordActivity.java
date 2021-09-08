package com.example.textreg2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteWordActivity extends AppCompatActivity {

    ListView listView;
    List<FavoriteWordHelperClass> favoriteWordHelperClassList;
    DatabaseReference reference;
    FirebaseUser user;
    String userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_word);
        listView=findViewById(R.id.list_favword);
        favoriteWordHelperClassList = new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
        }
        String emailID=userEmail.replaceAll("[-+.^:,@_]","");

        reference= FirebaseDatabase.getInstance().getReference(emailID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteWordHelperClassList.clear();
                for(DataSnapshot child : snapshot.getChildren())
                {
                    FavoriteWordHelperClass favWord = child.getValue(FavoriteWordHelperClass.class);
                    favoriteWordHelperClassList.add(favWord);
                }
                FavoriteWordListAdapter adapter = new FavoriteWordListAdapter(FavoriteWordActivity.this,favoriteWordHelperClassList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}