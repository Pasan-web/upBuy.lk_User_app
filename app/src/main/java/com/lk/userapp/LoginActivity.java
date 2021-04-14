package com.lk.userapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lk.userapp.Model.Users;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText userEmail, password;
    private Button login;
    private ProgressDialog loading;
    SignInButton authLogin;
    private String dbTName = "Users";
    private static final int RC_SIGN_IN = 123;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference userCollection;
    String FCMAToken = null;
    private String TAG = "LoginActivity";
    private TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userCollection = db.collection("Users");

        login = (Button) findViewById(R.id.login_btn);
        userEmail = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        authLogin = findViewById(R.id.auth_login_btn);
        signUp = findViewById(R.id.sign_up);
        loading = new ProgressDialog(this);
        initFCM();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        authLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createSignInIntent();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                Toast.makeText(LoginActivity.this, "Incorrect User !, Please Register !", Toast.LENGTH_SHORT).show();
                intent.putExtra("email","normal");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    private void loginUser() {
        String email = userEmail.getText().toString().trim();
        String pw = password.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter Your Valid Email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pw)) {
            Toast.makeText(this, "Please Enter Your Valid Password", Toast.LENGTH_SHORT).show();
        } else {
            loading.setTitle("Login Account");
            loading.setMessage("Please Wait, while we are checking the credentials.");
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            checkEmail(email, pw);
        }
    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(

                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                String email = user.getEmail();
                String displayName = user.getDisplayName();
                String auth = user.getUid();
                Log.d("heee",email);

                userCollection.whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String documentId = document.getId();
                            Users user = document.toObject(Users.class);


                                updateFCMToken(documentId);

                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("email", user.getEmail());
                                editor.putString("docid", documentId);
                                editor.commit();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                        }else{
                            Toast.makeText(LoginActivity.this, "Incorrect User !, Please Register !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                            intent.putExtra("email",email);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error :"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("TAG","Task Completed Successfully");
                    }
                });


            } else {
                Toast.makeText(this, "Login Failed !", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // [END auth_fui_result]

    private void checkEmail(String email, String pw) {

        userCollection.whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (!task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String documentId = document.getId();
                        Users user = document.toObject(Users.class);

                        if (password.getText().toString().equals(user.getPassword())) {
                            updateFCMToken(documentId);

                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("email", user.getEmail());
                            editor.putString("docid", documentId);
                            editor.commit();
                            loading.dismiss();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else {
                            loading.dismiss();
                            Toast.makeText(LoginActivity.this, "Invalid Password, Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(LoginActivity.this, "Incorrect User !, Please Register !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                        intent.putExtra("email","normal");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Error :"+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d("TAG","Task Completed Successfully");
            }
        });




    }

    private void initFCM() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        FCMAToken = task.getResult();


                        // Log and toast
                        Log.d(TAG, FCMAToken.toString());

                    }
                });
    }

    private void updateFCMToken(String documentId) {
        userCollection.document(documentId).update("fcmToken",FCMAToken).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

}