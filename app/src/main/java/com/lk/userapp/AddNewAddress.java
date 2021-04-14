package com.lk.userapp;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.lk.userapp.Model.Address;
import com.lk.userapp.Model.Users;

import java.util.List;

public class AddNewAddress extends AppCompatActivity {

    private Button nextbtn;
    private EditText houseNo,streetName,cityName,landMarkName,firsName,lastName,mobile;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference addressCollection;
    private CollectionReference userCollection;
    private ProgressDialog loading;
    private String docid;
    private ImageView close;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);

        nextbtn = findViewById(R.id.next_btn);
        houseNo = findViewById(R.id.fName_field);
        streetName = findViewById(R.id.street_name_field);
        cityName = findViewById(R.id.city_name_field);
        landMarkName = findViewById(R.id.landmark_field);
        firsName = findViewById(R.id.first_name_field);
        lastName = findViewById(R.id.second_name_field);
        mobile = findViewById(R.id.mobile_field);
        close = findViewById(R.id.close_newaddress);

        loading = new ProgressDialog(this);

        addressCollection = db.collection("Address");
        userCollection = db.collection("Users");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AddNewAddress.this);
        String mail = sp.getString("email", "empty");
        docid = sp.getString("docid", "empty");

        addContactField(docid);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(houseNo.getText().toString())){
                    Toast.makeText(AddNewAddress.this, "House No is mandatory..", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(streetName.getText().toString())){
                    Toast.makeText(AddNewAddress.this, "Street Name is mandatory..", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(cityName.getText().toString())){
                    Toast.makeText(AddNewAddress.this, "City Name is mandatory..", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(landMarkName.getText().toString())){
                    Toast.makeText(AddNewAddress.this, "LandMark Name is mandatory..", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(firsName.getText().toString())){
                    Toast.makeText(AddNewAddress.this, "Person First Name is mandatory..", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(lastName.getText().toString())){
                    Toast.makeText(AddNewAddress.this, "Person Last Name is mandatory..", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(mobile.getText().toString())){
                    Toast.makeText(AddNewAddress.this, "Person Mobile is mandatory..", Toast.LENGTH_SHORT).show();
                }else{
                    addNewAddress(docid);
                }
            }
        });
    }

    private void addContactField(String docid) {
        userCollection.document(docid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Users users = documentSnapshot.toObject(Users.class);
                firsName.setText(users.getFirstname());
                lastName.setText(users.getLastname());
                mobile.setText(users.getMobile());
            }
        });
    }

    private void addNewAddress(String docid) {

        loading.setTitle("Adding Address");
        loading.setMessage("Please Wait, while we are checking the credentials.");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        Address address = new Address();
        address.setUserDocId(docid);
        address.setHouseNo(houseNo.getText().toString());
        address.setStreetName(streetName.getText().toString());
        address.setCityName(cityName.getText().toString());
        address.setLandmark(landMarkName.getText().toString());
        address.setPersonFirstName(firsName.getText().toString());
        address.setPersonLastName(lastName.getText().toString());
        address.setPersonMobile(mobile.getText().toString());

        addressCollection.whereEqualTo("userDocId",docid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                if (documentsSnapList.size()>0){
                    address.setStatus("Active");
                    saveAddress(address);
                }else{
                    address.setStatus("Selected");
                    saveAddress(address);
                }
            }
        });

    }

    private void saveAddress(Address address) {

        addressCollection.add(address).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                houseNo.setText("");
                streetName.setText("");
                cityName.setText("");
                landMarkName.setText("");
                firsName.setText("");
                lastName.setText("");
                mobile.setText("");
                loading.dismiss();
                addContactField(docid);
                Toast.makeText(AddNewAddress.this, "Address saved Successfully !", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading.dismiss();
                Toast.makeText(AddNewAddress.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}