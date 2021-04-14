package com.lk.userapp.ui.wishlist;

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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.lk.userapp.Holder.CartHolder;
import com.lk.userapp.Holder.WishListHolder;
import com.lk.userapp.Model.Cart;
import com.lk.userapp.Model.Product;
import com.lk.userapp.Model.WishList;
import com.lk.userapp.ProductViewActivity;
import com.lk.userapp.R;
import com.lk.userapp.SeeMessagetoLogout;
import com.lk.userapp.ui.Cart.CartFragment;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class WishListFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter<WishList, WishListHolder> fsWishListAdapter;
    private RecyclerView wishRecycler;
    private TextView clearBtn;
    private String saveCurrentDate,saveCurrentTime;
    public CollectionReference cartCollection;
    private ProgressDialog loading;

    public WishListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_wish_list, container, false);
        wishRecycler = root.findViewById(R.id.wishlistRecycler);
        wishRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        clearBtn = root.findViewById(R.id.wishlist_clear_lbl);
        loading = new ProgressDialog(WishListFragment.super.getContext());

        cartCollection = db.collection("Cart");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WishListFragment.super.getContext());
        String mail = sp.getString("email", "empty");
        String docid = sp.getString("docid", "empty");
        if (mail.equals("empty") && docid.equals("empty")){
            Intent intent = new Intent(WishListFragment.super.getContext(), SeeMessagetoLogout.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }else {
            db.collection("WishList").whereEqualTo("userDocId",docid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                    if (documentsSnapList.size()>0){
                        setAdepter(docid);

                    }else{

                    }
                }
            });

            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDb(docid);
                }
            });

        }

        return root;
    }

    private void deleteDb(String docid) {

        db.collection("WishList").whereEqualTo("userDocId",docid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                if (!documents.isEmpty()){
                    loading.setTitle("Clearing WishList");
                    loading.setMessage("Please Wait, while we are checking the credentials.");
                    loading.setCanceledOnTouchOutside(false);
                    loading.show();

                    for (DocumentSnapshot document : documents) {

                        String documentId = document.getId();


                        db.collection("WishList").document(documentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void setAdepter(String docid) {
        Query query = db.collection("WishList").whereEqualTo("userDocId",docid);
        FirestoreRecyclerOptions recyclerOptions = new FirestoreRecyclerOptions.Builder<WishList>().setQuery(query,WishList.class).build();
        fsWishListAdapter = new FirestoreRecyclerAdapter<WishList,WishListHolder>(recyclerOptions) {

            @NonNull
            @Override
            public WishListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout,parent,false);
                return new WishListHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull WishListHolder holder, int position, @NonNull WishList model) {

                db.collection("Product").document(model.getProductDocId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Product product = documentSnapshot.toObject(Product.class);
                        holder.itemName.setText(product.getProductName());
                        Picasso.with(WishListFragment.super.getContext()).load(product.getImgUrl()).into(holder.wishListImg);

                        holder.itemPrice.setText("Rs :" + product.getPrice());
                        holder.itemTimeDate.setText(model.getDate()+"/"+ model.getTime());
                        String docId = getSnapshots().getSnapshot(position).getId();
                        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deletItem(docId);
                            }
                        });

                        holder.addToCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                saveCart(model.getProductDocId(),docid);
                            }
                        });
                    }
                });

            }

        };
        //set Adapter
        wishRecycler.setAdapter(fsWishListAdapter);
        fsWishListAdapter.startListening();
    }

    private void saveCart(String productDoc, String docid) {
        cartCollection.whereEqualTo("productDocId",productDoc).whereEqualTo("userDocId",docid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                if (documentsSnapList.size()>0){
                    DocumentSnapshot d = documentsSnapList.get(0);
                    String cart_id = d.getId();
                    Cart customer = d.toObject(Cart.class);
                    int qtys = Integer.parseInt(customer.getQty());

                    int i = qtys + 1;
                    cartCollection.document(cart_id).update("qty", String.valueOf(i))

                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.w("ss", "DocumentSnapshot successfully updated!");
                                    Toast.makeText(WishListFragment.super.getContext(), String.valueOf(i) + " item added to your cart", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ss", "Error updating document", e);
                                }
                            });

                }else{
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("MM:dd:yyyy");
                    saveCurrentDate = currentDate.format(calendar.getTime());
                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                    saveCurrentTime = currentTime.format(calendar.getTime());
                    Cart cart = new Cart();

                    cart.setUserDocId(docid);
                    cart.setProductDocId(productDoc);
                    cart.setQty("1");
                    cart.setDate(saveCurrentDate);
                    cart.setTime(saveCurrentTime);

                    cartCollection.add(cart).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(WishListFragment.super.getContext(),  "1 item added to your cart", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(WishListFragment.super.getContext(), "Error :" + e.getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
    }

    private void deletItem(String docId) {
        db.collection("WishList").document(docId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(WishListFragment.super.getContext(), "Item Deleted Successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}