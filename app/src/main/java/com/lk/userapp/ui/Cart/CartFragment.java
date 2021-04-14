package com.lk.userapp.ui.Cart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.lk.userapp.Holder.CartHolder;
import com.lk.userapp.Model.Cart;
import com.lk.userapp.Model.Product;
import com.lk.userapp.ProceedPayment;
import com.lk.userapp.R;
import com.lk.userapp.SeeMessagetoLogout;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter<Cart, CartHolder> fsCartAdapter;
    private RecyclerView cartRecycler;
    private TextView sub_tot,delivery,net_tot,clearDb;
    private double tot;
    private ProgressDialog loading;
    private Button placeOrderBtn;
    private String net;
    private ImageView back;

    public CartFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        sub_tot = root.findViewById(R.id.sub_total);
        delivery = root.findViewById(R.id.delivery);
        net_tot = root.findViewById(R.id.total);
        clearDb = root.findViewById(R.id.wishlist_clear_lbl);
        loading = new ProgressDialog(CartFragment.super.getContext());
        placeOrderBtn = root.findViewById(R.id.place_order_btn);
        back = root.findViewById(R.id.back_cart);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartFragment.super.getActivity().onBackPressed();
            }
        });

        cartRecycler = root.findViewById(R.id.recyclerView);
        cartRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CartFragment.super.getContext());
        String mail = sp.getString("email", "empty");
        String docid = sp.getString("docid", "empty");
        if (mail.equals("empty") && docid.equals("empty")){
            Intent intent = new Intent(CartFragment.super.getContext(), SeeMessagetoLogout.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }else {
            db.collection("Cart").whereEqualTo("userDocId",docid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                    if (documentsSnapList.size()>0){
                        setAdepter(docid);
                        setHeader(docid);
                    }else{
                        setEmptyLayout();
                    }
                }
            });

            clearDb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDb(docid);
                }
            });

            placeOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CartFragment.super.getContext(), ProceedPayment.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("total",net);
                    startActivity(intent);
                }
            });

        }
            return root;


    }

    private void deleteDb(String docid) {

        db.collection("Cart").whereEqualTo("userDocId",docid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                if (!documents.isEmpty()){
                    loading.setTitle("Clearing Cart");
                    loading.setMessage("Please Wait, while we are checking the credentials.");
                    loading.setCanceledOnTouchOutside(false);
                    loading.show();

                    for (DocumentSnapshot document : documents) {

                        String documentId = document.getId();


                        db.collection("Cart").document(documentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                loading.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }
                 }else{

                }
            }
        });
    }

    private void setEmptyLayout() {
        Toast.makeText(CartFragment.super.getContext(), "setttt", Toast.LENGTH_SHORT).show();
    }

    private void setHeader(String docid) {
        tot = 00.00;
        double del = 59.53;

        db.collection("Cart").whereEqualTo("userDocId",docid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                List<Cart> carts = queryDocumentSnapshots.toObjects(Cart.class);


                for (Cart document : carts) {

                    double qt = Double.parseDouble(document.getQty());

                    db.collection("Product").document(document.getProductDocId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            Product product = documentSnapshot.toObject(Product.class);
                            double price = Double.parseDouble(product.getPrice());
                            tot =+ tot + (price * qt);

                            sub_tot.setText("Rs :"+String.valueOf(tot));
                            delivery.setText("Rs :"+String.valueOf(del));
                            net_tot.setText("Rs :"+String.valueOf(tot + del));
                            net = String.valueOf(tot + del);
                        }
                    });
                    Log.d("aaaa",document.getProductDocId());

                }
            }
        });


    }

    private void setAdepter(String docid) {
        Query query = db.collection("Cart").whereEqualTo("userDocId",docid);
        FirestoreRecyclerOptions recyclerOptions = new FirestoreRecyclerOptions.Builder<Cart>().setQuery(query,Cart.class).build();
        fsCartAdapter = new FirestoreRecyclerAdapter<Cart, CartHolder>(recyclerOptions) {

            @NonNull
            @Override
            public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                return new CartHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CartHolder holder, int position, @NonNull Cart model) {

                db.collection("Product").document(model.getProductDocId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Product product = documentSnapshot.toObject(Product.class);
                        holder.cartItemName.setText(product.getProductName());
                        Picasso.with(CartFragment.super.getContext()).load(product.getImgUrl()).into(holder.cartItemImg);
                        double qty = Double.parseDouble(model.getQty());
                        double price = Double.parseDouble(product.getPrice());
                        double tot = qty * price;
                        holder.cartItemPrice.setText("Rs :" + String.valueOf(tot));
                        holder.qty.setText(model.getQty());

                    }
                });

                String docId = getSnapshots().getSnapshot(position).getId();
                holder.qtyIncrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String qt = holder.qty.getText().toString();
                        int i = Integer.parseInt(qt);
                        int ne = ++i;
                        //holder.qty.setText(String.valueOf(ne));
                        db.collection("Cart").document(docId).update("qty", String.valueOf(ne)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                setHeader(model.getUserDocId());
                            }
                        });
                    }
                });

                holder.qtyReduce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String qt = holder.qty.getText().toString();
                        int i = Integer.parseInt(qt);
                        int ne = --i;
                        if (ne < 1){

                        }else {
                            //holder.qty.setText(String.valueOf(ne));
                            db.collection("Cart").document(docId).update("qty", String.valueOf(ne)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    setHeader(model.getUserDocId());
                                }
                            });
                        }
                    }
                });

            }

        };
        //set Adapter
        cartRecycler.setAdapter(fsCartAdapter);
        fsCartAdapter.startListening();
    }

    private void qtyUpdate(String docId) {
        Toast.makeText(CartFragment.super.getContext(), docId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}