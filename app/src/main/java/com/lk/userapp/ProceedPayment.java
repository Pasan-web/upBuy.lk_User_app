package com.lk.userapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lk.userapp.Model.Address;
import com.lk.userapp.Model.Cart;
import com.lk.userapp.Model.Invoice;
import com.lk.userapp.Model.InvoiceItem;
import com.lk.userapp.Model.Product;
import com.lk.userapp.Model.Users;
import com.lk.userapp.ui.orderhistory.OrderHistoryFragment;
import com.lk.userapp.ui.profile.ProfileFragment;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class ProceedPayment extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference addressCollection;
    private TextView address,personName,personMobile,changeAddress,chageAddressPerson;
    private String docid,saveCurrentDate,saveCurrentTime;
    private ImageView close;
    private Button checkout;
    private int PAYHERE_REQUEST = 1001;
    private String TAG = "ProceedPayment";
    private String total;
    private CollectionReference userCollection;
    private CollectionReference cartCollection;
    private CollectionReference invoiceCollection;
    private CollectionReference invoiceItemCollection;
    private String addressId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proceed_payment);

        address = findViewById(R.id.delivey_address);
        personName = findViewById(R.id.contact_name);
        personMobile = findViewById(R.id.contact_no);
        changeAddress = findViewById(R.id.change_delivery_lbl);
        chageAddressPerson = findViewById(R.id.change_contact_lbl);
        close = findViewById(R.id.close_payment);
        checkout = findViewById(R.id.next_step_lbtn);

        userCollection = db.collection("Users");
        cartCollection = db.collection("Cart");
        invoiceCollection = db.collection("Invoice");
        invoiceItemCollection = db.collection("InvoiceItem");

        Bundle bundle = getIntent().getExtras();
        total = bundle.getString("total");


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProceedPayment.this, ViewAddressBook.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        chageAddressPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(ProceedPayment.this, EditProfileActivity.class);
                editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                editIntent.putExtra("person","another");
                startActivity(editIntent);
            }
        });

        addressCollection = db.collection("Address");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ProceedPayment.this);
        String mail = sp.getString("email", "empty");
        docid = sp.getString("docid", "empty");

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPaymentGetway(docid);
            }
        });

        setDetails(docid);
    }

    private void getPaymentGetway(String docid) {

        userCollection.document(docid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Users users = documentSnapshot.toObject(Users.class);
                addressCollection.whereEqualTo("userDocId",docid).whereEqualTo("status","Selected").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                        if (documentsSnapList.size()>0) {
                            DocumentSnapshot d = documentsSnapList.get(0);
                            Address add = d.toObject(Address.class);
                            addressId = d.getId();

                            InitRequest req = new InitRequest();
                            req.setMerchantId("1214659");       // Your Merchant PayHere ID
                            req.setMerchantSecret("8n2zXdSN3A24vW5rNhjxbi4jmmSIvZXhA4vW67LdWijC"); // Your Merchant secret (Add your app at Settings > Domains & Credentials, to get this))
                            req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
                            req.setAmount(Double.parseDouble(total));             // Final Amount to be charged
                            req.setOrderId("invoiceId");        // Unique Reference ID
                            req.setItemsDescription("Nxtpharma customer payment");  // Item description title
                            req.setCustom1("This is the custom message 1");
                            req.setCustom2("This is the custom message 2");
                            req.getCustomer().setFirstName(users.getFirstname());
                            req.getCustomer().setLastName(users.getLastname());
                            req.getCustomer().setEmail(users.getEmail());
                            req.getCustomer().setPhone(users.getMobile());
                            req.getCustomer().getAddress().setAddress(add.getHouseNo()+","+add.getStreetName());
                            req.getCustomer().getAddress().setCity(add.getCityName());
                            req.getCustomer().getAddress().setCountry("Sri Lanka");


                            Intent intent = new Intent(ProceedPayment.this, PHMainActivity.class);
                            intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
                            PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
                            startActivityForResult(intent, PAYHERE_REQUEST);



                        }
                    }
                });
            }
        });




    }

    private void setDetails(String docid) {
        addressCollection.whereEqualTo("userDocId",docid).whereEqualTo("status","Selected").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                if (documentsSnapList.size()>0) {
                    DocumentSnapshot d = documentsSnapList.get(0);
                    Address add = d.toObject(Address.class);
                    address.setText(add.getHouseNo()+","+add.getStreetName()+","+add.getCityName());
                    personName.setText(add.getPersonFirstName()+" "+add.getPersonLastName());
                    personMobile.setText(add.getPersonMobile());
                    String id = d.getId();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            if (resultCode == Activity.RESULT_OK) {
                String msg;
                if (response != null) {
                    if (response.isSuccess()) {

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat currentDate = new SimpleDateFormat("d-M-yyyy");
                        saveCurrentDate = currentDate.format(calendar.getTime());
                        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                        saveCurrentTime = currentTime.format(calendar.getTime());

                        Invoice invoice = new Invoice();
                        invoice.setUserDocId(docid);
                        invoice.setAddressDocId(addressId);
                        invoice.setDate(saveCurrentDate);
                        invoice.setTime(saveCurrentTime);
                        invoice.setAmount(total);
                        invoice.setStatus("ordered");

                        invoiceCollection.add(invoice).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                String invoiceId = documentReference.getId();

                                db.collection("Cart").whereEqualTo("userDocId",docid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Cart cart = document.toObject(Cart.class);
                                                db.collection("Product").document(cart.getProductDocId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                        Product product = documentSnapshot.toObject(Product.class);
                                                        String productId = documentSnapshot.getId();

                                                        InvoiceItem item = new InvoiceItem();
                                                        item.setInvoiceDocId(invoiceId);
                                                        item.setProductDocId(productId);
                                                        item.setQty(cart.getQty());

                                                        invoiceItemCollection.add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                db.collection("Cart").document(document.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Intent invoice = new Intent(ProceedPayment.this, InvoiceActivity.class);
                                                                        invoice.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                        invoice.putExtra("invoiceId",invoiceId);
                                                                        startActivity(invoice);
                                                                    }
                                                                });
                                                            }
                                                        });

                                                    }
                                                });
                                                Log.d("aaaa",cart.getProductDocId());

                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });;


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProceedPayment.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        Toast.makeText(this, "Success Payment", Toast.LENGTH_SHORT).show();


                    } else {
                        msg = "Result:" + response.toString();
                    }
                }else {
                    msg = "Result: no response";
                }
                //Log.d(TAG, msg);
                //  textView.setText(msg);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response != null) {
                    Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    // textView.setText("User canceled the request");
                    Toast.makeText(this, "User canceled the request", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        setDetails(docid);
    }
}