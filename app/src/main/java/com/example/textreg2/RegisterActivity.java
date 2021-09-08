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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    EditText mName,mEmail,mPassword,mPhone;
    Button mRegister;
    TextView mLogin;
    private FirebaseAuth fAuth;
    ProgressBar mProgressBar;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        getSupportActionBar().hide();
        mName=findViewById(R.id.et_name);
        mEmail=findViewById(R.id.et_reg_email);
        mPhone=findViewById(R.id.et_reg_phone);
        mPassword=findViewById(R.id.et_reg_password);
        mRegister=findViewById(R.id.btn_register);
        mLogin=findViewById(R.id.tv_register);
        mProgressBar=findViewById(R.id.progress_bar_register);
        mProgressBar.setVisibility(View.INVISIBLE);
        fAuth = FirebaseAuth.getInstance();
        mRegister.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             rootNode=FirebaseDatabase.getInstance();
                                             reference=rootNode.getReference("user");

                                             final String email = mEmail.getText().toString().trim();
                                             final String password = mPassword.getText().toString().trim();
                                             final String name = mName.getText().toString().trim();
                                             final String phone = mPhone.getText().toString().trim();
                                             mProgressBar.setVisibility(View.VISIBLE);

                                             if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                                 mEmail.setError("Email is required.");
                                                 mEmail.requestFocus();
                                                 return;
                                             }
                                             else if (TextUtils.isEmpty(name)) {
                                                 mName.setError("Name is required.");
                                                 mName.requestFocus();

                                                 return;
                                             }
                                             else if (TextUtils.isEmpty(phone))
                                             {
                                                 mPhone.setError("Phone is required.");
                                                 mPhone.requestFocus();
                                                 return;
                                             }
                                             else if (phone.length()<6)
                                             {
                                                 mPhone.setError("Phone number must be >= 6 digits.");
                                                 mPhone.requestFocus();
                                                 return;
                                             }
                                             else if (TextUtils.isEmpty(password)) {
                                                 mPassword.setError("Password is required.");
                                                 mPassword.requestFocus();
                                                 return;
                                             }

                                             else if (password.length() < 6) {
                                                 mPassword.setError("Password must be >= 6 characters");
                                                 mPassword.requestFocus();
                                                 return;
                                             }
                                             // register the user in firebase
                                             else {fAuth.createUserWithEmailAndPassword(email, password)
                                                     .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<AuthResult> task) {
                                                             if (task.isSuccessful()) {
                                                                 String id = email.replaceAll("[-+.^:,@_]","");
                                                                 UserHelperClass userHelperClass = new UserHelperClass(name,email,phone,password,id);
                                                                 reference.child(id).setValue(userHelperClass);
                                                                 Toast.makeText(RegisterActivity.this, "Register Sucessfully!", Toast.LENGTH_SHORT).show();
                                                                 mProgressBar.setVisibility(View.GONE);
                                                                 Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                                                 startActivity(intent);
                                                             } else {
                                                                 // If fails, display a message to the user.
                                                                 Toast.makeText(RegisterActivity.this, "Failed to Register!", Toast.LENGTH_SHORT).show();
                                                                 mProgressBar.setVisibility(View.GONE);
                                                             }
                                                         }
                                                     });}
                                         }
                                     });

                mLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                });

            }
        }
