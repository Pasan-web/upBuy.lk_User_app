package com.lk.userapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.lk.userapp.Holder.AddressHolder;
import com.lk.userapp.Holder.CartHolder;
import com.lk.userapp.Model.Address;
import com.lk.userapp.Model.Cart;
import com.lk.userapp.Model.Product;
import com.lk.userapp.ui.Cart.CartFragment;
import com.lk.userapp.ui.profile.ProfileFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewAddressBook extends AppCompatActivity {

    private Button addNewAddress;
    private TextView recent;
    private String docid;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference addressCollection;
    private FirestoreRecyclerAdapter<Address, AddressHolder> fsAddressAdapter;
    private ProgressDialog loading;
    private RecyclerView addRecycler;
    private ImageView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_address_book);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String mail = sp.getString("email", "empty");
        docid = sp.getString("docid", "empty");

        loading = new ProgressDialog(this);

        addressCollection = db.collection("Address");

        addRecycler = findViewById(R.id.address_recyclerview);
        addRecycler.setLayoutManager(new LinearLayoutManager(this));
        
        addNewAddress = findViewById(R.id.add_new_btn);
        recent = findViewById(R.id.recent_add_lbl);
        close = findViewById(R.id.close_address_l);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewSelectedAddress(docid);

        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAddressBook.this,AddNewAddress.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        setAdapter(docid);

    }

    private void setAdapter(String docid) {
        Query query = addressCollection.whereEqualTo("userDocId",docid).whereEqualTo("status","Active");
        FirestoreRecyclerOptions recyclerOptions = new FirestoreRecyclerOptions.Builder<Address>().setQuery(query,Address.class).build();
        fsAddressAdapter = new FirestoreRecyclerAdapter<Address, AddressHolder>(recyclerOptions) {

            @NonNull
            @Override
            public AddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_list_item,parent,false);
                return new AddressHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AddressHolder holder, int position, @NonNull Address model) {
                holder.address.setText(model.getHouseNo()+","+model.getStreetName()+","+model.getCityName());
                String Id = getSnapshots().getSnapshot(position).getId();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectRecentAddress(Id,docid);
                    }
                });

                holder.deleteItemImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteRecentAddress(Id);
                    }
                });

            }

        };
        //set Adapter
        addRecycler.setAdapter(fsAddressAdapter);
        fsAddressAdapter.startListening();
    }

    private void deleteRecentAddress(String id) {
        addressCollection.document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ViewAddressBook.this, "Address Selected Successfully !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectRecentAddress(String id, String docid) {

        loading.setTitle("Changing Address");
        loading.setMessage("Please Wait, while we are checking the credentials.");
        loading.setCanceledOnTouchOutside(false);
        loading.show();

        addressCollection.whereEqualTo("userDocId",docid).whereEqualTo("status","Selected").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                if (documentsSnapList.size()>0) {
                    DocumentSnapshot d = documentsSnapList.get(0);
                    String addId = d.getId();

                    addressCollection.document(addId).update("status","Active").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            addressCollection.document(id).update("status","Selected").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    loading.dismiss();
                                    viewSelectedAddress(docid);
                                }
                            });
                        }
                    });

                }
            }
        });

    }

    private void viewSelectedAddress(String docid) {
        addressCollection.whereEqualTo("userDocId",docid).whereEqualTo("status","Selected").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                if (documentsSnapList.size()>0) {
                    DocumentSnapshot d = documentsSnapList.get(0);
                    Address address = d.toObject(Address.class);
                    recent.setText(address.getHouseNo()+","+address.getStreetName()+","+address.getCityName());
                }else {
                    recent.setText("Please Add Address");
                }
            }
        });
    }
}