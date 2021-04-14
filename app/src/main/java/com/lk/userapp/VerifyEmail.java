package com.lk.userapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lk.userapp.Model.Users;

public class VerifyEmail extends AppCompatActivity {

    private EditText verifyCode;
    private Button verifyBtn;

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String mobile;
    private String verifykey;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference userCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        userCollection = db.collection("Users");

        verifyCode = findViewById(R.id.verify_field);
        verifyBtn = findViewById(R.id.verify_btn);

        Bundle extras = getIntent().getExtras();
        firstname = extras.getString("firstname");
        lastname = extras.getString("lastname");
        email = extras.getString("email");
        password = extras.getString("pw");
        mobile = extras.getString("mobile");
        verifykey = extras.getString("verifykey");

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyCode.getText().toString().equals("")){
                    Toast.makeText(VerifyEmail.this, "Please enter verify code", Toast.LENGTH_SHORT).show();
                }else{
                    if (verifyCode.getText().toString().equals(verifykey)){
                        registerUsers();
                    }else{
                        Toast.makeText(VerifyEmail.this, "Please check your email", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void registerUsers() {
        Users user = new Users();
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setEmail(email);
        user.setMobile(password);
        user.setPassword(mobile);
        user.setIsactive("Active");

        userCollection.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(VerifyEmail.this, "You are registered successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(VerifyEmail.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}