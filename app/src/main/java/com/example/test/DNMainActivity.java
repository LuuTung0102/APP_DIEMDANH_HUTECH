package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class DNMainActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;
    TextView signupRedirectText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnmain);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateUserName() | !validatePassword()){

                }else {
                    checkUser();
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DNMainActivity.this, DKActivity.class);
                startActivity(intent);
            }
        });
    }
    public Boolean validateUserName(){
        String val = loginUsername.getText().toString();
        if (val.isEmpty()){
            loginUsername.setError("Ten tai khoan ko dung");
            return false;
        }else {
            loginUsername.setError(null);
            return true;
        }
    }

        public Boolean validatePassword(){
            String val = loginPassword.getText().toString();
            if (val.isEmpty()){
                loginPassword.setError("pass sai ko dung");
                return false;
            }else {
                loginPassword.setError(null);
                return true;
            }
        }

    public void checkUser(){
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Teachers");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);
                        if (passwordFromDB.equals(userPassword)) {
                            Intent intent = new Intent(DNMainActivity.this, MainActivity.class);
                            intent.putExtra("username", userUsername);
                            startActivity(intent);
                            return;
                        } else {

                            loginPassword.setError("Sai password");
                            loginPassword.requestFocus();
                            return;
                        }
                    }
                } else {

                    loginUsername.setError("Tk không đúng");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
}