package com.lk.userapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.lk.userapp.Holder.CategoryHolder;
import com.lk.userapp.Holder.ProductHolder;
import com.lk.userapp.Model.Product;
import com.lk.userapp.ui.orderhistory.OrderHistoryFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private String merchantName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter<Product, ProductHolder> fsProductAdapter;
    private CollectionReference productCollection;
    private CollectionReference merchantCollection;
    private RecyclerView productView;
    public String merchant_id;
    private Spinner spinnerProduct;
    private Button filter;
    private EditText priceMin;
    private EditText priceMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        productView = findViewById(R.id.productView);

        Bundle getData = getIntent().getExtras();
        merchantName = getData.getString("merchantName");

        priceMin = findViewById(R.id.price_min);
        priceMax = findViewById(R.id.price_max);

        productView.setLayoutManager(new LinearLayoutManager(this));

        spinnerProduct = findViewById(R.id.spinnerProduct);
        filter = findViewById(R.id.filter_btn);

        productCollection = db.collection("Product");
        merchantCollection = db.collection("Merchant");
        Query query = productCollection;

        //setAdepter(query);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = spinnerProduct.getSelectedItem().toString();

                if (!priceMin.getText().toString().equals("")){
                    if (!priceMax.getText().toString().equals("")) {
                        Query query = productCollection.whereGreaterThanOrEqualTo("doublePrice", Double.parseDouble(priceMin.getText().toString()+"")).whereLessThanOrEqualTo("doublePrice", Double.parseDouble(priceMax.getText().toString()+""));
                        setAdepter(query);
                        fsProductAdapter.startListening();
                    }
                }


            }
        });

        setSpinner();

        getMerchantId();

        spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = spinnerProduct.getSelectedItem().toString();
                if (s.equals("Best Match")){

                }else if (s.equals("Price High to Low")){
                    Query query = productCollection.orderBy("doublePrice", Query.Direction.DESCENDING);
                    setAdepter(query);
                    fsProductAdapter.startListening();
                }else if (s.equals("Price Low to High")){
                    Query query = productCollection.orderBy("doublePrice", Query.Direction.ASCENDING);
                    setAdepter(query);
                    fsProductAdapter.startListening();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSpinner() {
        String[] status = {"Best Match","Price High to Low","Price Low to High"};
        ArrayAdapter vListAdapter = new ArrayAdapter(ProductActivity.this,android.R.layout.simple_selectable_list_item,status);
        spinnerProduct.setAdapter(vListAdapter);
    }

    private void setAdepter(Query query) {

        FirestoreRecyclerOptions recyclerOptions = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query,Product.class).build();

        fsProductAdapter = new FirestoreRecyclerAdapter<Product, ProductHolder>(recyclerOptions){

            @NonNull
            @Override
            public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item,parent,false);
                return new ProductHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull Product model) {

                Picasso.with(ProductActivity.this).load(model.getImgUrl()).into(holder.productImage);
                holder.productName.setText(model.getProductName());
                holder.productDescription.setText(model.getDescription());
                holder.productPrice.setText("RS:" + model.getPrice());
                String docId = getSnapshots().getSnapshot(position).getId();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent viewIntent = new Intent(ProductActivity.this,ProductViewActivity.class);
                        viewIntent.putExtra("productDocId",docId);
                        viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(viewIntent);
                    }
                });

            }
        };

        //set Adapter
        productView.setAdapter(fsProductAdapter);
    }

    private void getMerchantId() {
        merchantCollection.whereEqualTo("merchantName",merchantName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                if (documentsSnapList.size()>0) {
                    DocumentSnapshot d = documentsSnapList.get(0);

                    String merchantid = d.getId();
                    merchant_id = merchantid;
                    Log.d("getMerchan",merchant_id);

                    Query queryS = productCollection.whereEqualTo("merchantDoc",merchantid).whereEqualTo("isActive","Active");
                    setAdepter(queryS);
                    fsProductAdapter.startListening();

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
