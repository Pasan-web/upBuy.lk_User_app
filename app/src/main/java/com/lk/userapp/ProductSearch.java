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
import android.view.Window;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.lk.userapp.Holder.ProductHolder;
import com.lk.userapp.Model.Product;
import com.squareup.picasso.Picasso;

public class ProductSearch extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter<Product, ProductHolder> fsProductAdapter;
    private CollectionReference productCollection;

    private RecyclerView productView;
    private SearchView editsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_product_search);

        productCollection = db.collection("Product");

        productView = findViewById(R.id.listview);

        productView.setLayoutManager(new LinearLayoutManager(this));

        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;
                Query query = productCollection.orderBy("productName").startAt(newText.toUpperCase()).endAt(newText+"\uf8ff");
                setAdepter(query);
                Log.d("apoo",text);
                return false;
            }
        });


        Query query = productCollection;

        setAdepter(query);



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

                Picasso.with(ProductSearch.this).load(model.getImgUrl()).into(holder.productImage);
                holder.productName.setText(model.getProductName());
                holder.productDescription.setText(model.getDescription());
                holder.productPrice.setText("RS:" + model.getPrice());
                String docId = getSnapshots().getSnapshot(position).getId();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ProductSearch.this,  docId, Toast.LENGTH_SHORT).show();
                        Intent viewIntent = new Intent(ProductSearch.this,ProductViewActivity.class);
                        viewIntent.putExtra("productDocId",docId);
                        viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(viewIntent);
                    }
                });

            }
        };

        //set Adapter
        productView.setAdapter(fsProductAdapter);
        fsProductAdapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        fsProductAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fsProductAdapter.stopListening();
    }
}