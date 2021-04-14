package com.lk.userapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.lk.userapp.AsyncTasks.AsyncTaskLogin;
import com.lk.userapp.Model.Users;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccBtn;
    private EditText firstName, lastName, email, password, mobile;
    private ProgressDialog loading;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference userCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userCollection = db.collection("Users");


        createAccBtn = (Button) findViewById(R.id.login_btn);
        firstName = (EditText) findViewById(R.id.fName_field);
        lastName = (EditText) findViewById(R.id.lastname);
        this.email = (EditText) findViewById(R.id.email);

        Bundle extras = getIntent().getExtras();
        String emails = extras.getString("email");
        if (!emails.equals("normal")){
            this.email.setText(emails);
            this.email.setEnabled(false);
        }
        password = (EditText) findViewById(R.id.password);
        mobile = (EditText) findViewById(R.id.phonenumber);
        loading = new ProgressDialog(this);

        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(v);
            }
        });

    }

    private void createAccount(View v) {
        String firstname = firstName.getText().toString().trim();
        String lastname = lastName.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String pw = password.getText().toString().trim();
        String number = mobile.getText().toString().trim();

        if (TextUtils.isEmpty(firstname)){
            Toast.makeText(this, "Please Enter Your First Name...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(lastname)){
            Toast.makeText(this, "Please Enter Your Last Name...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(mail)){
            Toast.makeText(this, "Please Enter Your Email...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(pw)){
            Toast.makeText(this, "Please Enter Your Password...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(number)){
            Toast.makeText(this, "Please Enter Your Mobile Number...", Toast.LENGTH_SHORT).show();
        }else{
            loading.setTitle("Create Account");
            loading.setMessage("Please Wait, while we are checking the credentials.");
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            validateEmail(firstname,lastname,mail,pw,number,v);
        }
    }

    private void validateEmail(String firstname, String lastname, String mail, String pw, String number, View v) {


        userCollection.whereEqualTo("email",mail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                if (documentSnapshotList.size()>0) {
                    Toast.makeText(RegisterActivity.this, "This "+ mail + " already exists.", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again using another email.", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(RegisterActivity.this,mail,Toast.LENGTH_LONG).show();
                    new AsyncTaskLogin(v).execute(firstname,lastname,mail,number,pw,"1");
                    loading.dismiss();
                }
            }
        });
    }
}