package com.example.textreg2;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    EditText mEmail,mPassword;
    Button mLogin;
    TextView mRegister;
    private FirebaseAuth fAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        getSupportActionBar().hide();
        mEmail=findViewById(R.id.et_email);
        mPassword=findViewById(R.id.et_password);
        mLogin=findViewById(R.id.btn_login);
        mRegister=findViewById(R.id.tv_login);
        fAuth = FirebaseAuth.getInstance();
       mAuthStateListener = new FirebaseAuth.AuthStateListener() {

           @Override
           public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               FirebaseUser mFirebaseUser = fAuth.getCurrentUser();
               if (mFirebaseUser!=null)
               {
                   Intent intent=new Intent(LoginActivity.this, DashboardActivity.class);
                   startActivity(intent);
               }
               else
               {
                   Toast.makeText(LoginActivity.this,"Please log in to continue!",Toast.LENGTH_SHORT).show();

               }
           }
       };
       mLogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final String email = mEmail.getText().toString().trim();
               final String password = mPassword.getText().toString().trim();


               if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                   mEmail.setError("Email is Required.");
                   mEmail.requestFocus();
                   return;
               }

               else if (TextUtils.isEmpty(password)) {
                   mPassword.setError("Password is Required.");
                   mEmail.requestFocus();
                   return;
               }

               else if (password.length() < 6) {
                   mPassword.setError("Password Must be >= 6 Characters");
                   mEmail.requestFocus();
                   return;
               }


               // register the user in firebase

               else {
                   fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful())
                           {
                               Intent enter=new Intent(LoginActivity.this,DashboardActivity.class);
                               startActivity(enter);
                           }
                           else
                           {
                               Toast.makeText(LoginActivity.this,"Log in error!",Toast.LENGTH_SHORT).show();
                           }

                       }
                   });
           }
           }
       });
       mRegister.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent register = new Intent(LoginActivity.this,RegisterActivity.class);
               startActivity(register);
           }
       });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        fAuth.addAuthStateListener(mAuthStateListener);
    }
}

