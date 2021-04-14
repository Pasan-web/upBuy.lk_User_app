package com.lk.userapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.lk.userapp.Model.Address;
import com.lk.userapp.Model.Users;

import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private String whoIs;
    private String docid;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollection;
    private CollectionReference addressCollection;
    private ProgressDialog loading;
    private EditText fName,lName,mobile;
    private Button updateBtn;
    private ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(EditProfileActivity.this);
        String mail = sp.getString("email", "empty");
        docid = sp.getString("docid", "empty");

        loading = new ProgressDialog(this);

        fName = findViewById(R.id.fName_field);
        lName = findViewById(R.id.lName_field);
        mobile = findViewById(R.id.m_field);
        updateBtn = findViewById(R.id.update_de_btn);
        close = findViewById(R.id.close_ic);

        addressCollection = db.collection("Address");
        userCollection = db.collection("Users");

        Bundle bundle = getIntent().getExtras();
        whoIs = bundle.getString("person");

        if (whoIs.equals("user")){
            setUserDetails(docid);
        }else if (whoIs.equals("another")){
            setAddressDetails(docid);
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(fName.getText().toString())){
                    Toast.makeText(EditProfileActivity.this, "First Name is mandatory..", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(lName.getText().toString())){
                    Toast.makeText(EditProfileActivity.this, "Last Name is mandatory..", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(mobile.getText().toString())){
                    Toast.makeText(EditProfileActivity.this, "Mobile No is mandatory..", Toast.LENGTH_SHORT).show();
                }else{
                    if (whoIs.equals("user")){
                        updateUserDetails(docid);
                    }else if (whoIs.equals("another")){
                        updateAddressDeatails(docid);
                    }
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void updateAddressDeatails(String docid) {
        loading.setTitle("Editing Address Details");
        loading.setMessage("Please Wait, while we are checking the credentials.");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        addressCollection.whereEqualTo("userDocId",docid).whereEqualTo("status","Selected").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                if (documentsSnapList.size()>0) {
                    DocumentSnapshot d = documentsSnapList.get(0);
                    String id = d.getId();


                    String nameF = fName.getText().toString();
                    String nameL = lName.getText().toString();
                    String mn = mobile.getText().toString();

                    addressCollection.document(id).update("personFirstName",nameF,"personLastName",nameL,"personMobile",mn).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully !", Toast.LENGTH_SHORT).show();
                            setAddressDetails(docid);
                            loading.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void setAddressDetails(String docid) {
        addressCollection.whereEqualTo("userDocId",docid).whereEqualTo("status","Selected").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                if (documentsSnapList.size()>0) {
                    DocumentSnapshot d = documentsSnapList.get(0);
                    Address add = d.toObject(Address.class);
                    fName.setText(add.getPersonFirstName());
                    lName.setText(add.getPersonLastName());
                    mobile.setText(add.getPersonMobile());
                }
            }
        });
    }

    private void setUserDetails(String docid) {
        userCollection.document(docid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Users user = documentSnapshot.toObject(Users.class);
                fName.setText(user.getFirstname());
                lName.setText(user.getLastname());
                mobile.setText(user.getMobile());
            }
        });
    }

    private void updateUserDetails(String docid) {
        loading.setTitle("Editing Profile");
        loading.setMessage("Please Wait, while we are checking the credentials.");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        String nameF = fName.getText().toString();
        String nameL = lName.getText().toString();
        String mn = mobile.getText().toString();

        userCollection.document(docid).update("firstname",nameF,"lastname",nameL,"mobile",mn).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully !", Toast.LENGTH_SHORT).show();
                setUserDetails(docid);
                loading.dismiss();
            }
        });
    }
}