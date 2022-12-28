package com.example.mrxhostel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mrxhostel.Faculty.TeacherData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText user,pass;
    Button loginBtn;
    private String username,password;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        databaseReference= FirebaseDatabase.getInstance().getReference();

        user=findViewById(R.id.loginUsername);
        pass=findViewById(R.id.loginPassword);
        loginBtn=findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               username= user.getText().toString();
                password=pass.getText().toString();

                validateUser(username,password);
            }
        });




    }

    private void validateUser(String username, String password) {

        databaseReference.child("Admin").child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                if(snapshot.exists())
                {
                     LoginData data=snapshot.getValue(LoginData.class);

                    String pass1= data.getPassword();
                     String user1 =data.getUsername();

                     if (user1.equals(username) && pass1.equals(password))
                     {
                         Toast.makeText(LoginActivity.this, "Login Succesful", Toast.LENGTH_SHORT).show();
                         startActivity(new Intent(LoginActivity.this,MainActivity.class));
                         finishAffinity();
                     }
                     else {
                         Toast.makeText(LoginActivity.this, "Wrong Password user: "+user1+ "pass: "+pass1, Toast.LENGTH_SHORT).show();
                     }





                }
                else {
                    Toast.makeText(LoginActivity.this, "User Doesn't exits", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText((Context) LoginActivity.this, (CharSequence) error, Toast.LENGTH_SHORT).show();

            }
        });
    }
}