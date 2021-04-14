package com.lk.userapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lk.userapp.Adepter.ProductDetailsAdepter;
import com.lk.userapp.Model.Cart;
import com.lk.userapp.Model.InvoiceItem;
import com.lk.userapp.Model.InvoicePdf;
import com.lk.userapp.Model.Product;
import com.lk.userapp.Model.Specification;
import com.lk.userapp.Model.WishList;
import com.lk.userapp.ui.Cart.CartFragment;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ir.androidexception.datatable.DataTable;
import ir.androidexception.datatable.model.DataTableHeader;
import ir.androidexception.datatable.model.DataTableRow;

public class ProductViewActivity extends AppCompatActivity {

    private ImageView productImage;

    private ImageView addWishlist;
    TextView mines,plus,qty,productTitle,productPrice,productDescription,textView21;
    private Button carySaveBtn;
    private String productDoc,saveCurrentDate,saveCurrentTime;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference cartCollection;
    public CollectionReference wishlistCollection;
    public CollectionReference specCollection;
    private ProgressDialog loading;
    DataTable dataTable;
    DataTableHeader header;
    private ArrayList<DataTableRow> rows = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);

       productImage = findViewById(R.id.productImage);

       productTitle = findViewById(R.id.product_title);
       productPrice = findViewById(R.id.product_price);
       carySaveBtn = findViewById(R.id.button2);
       addWishlist = findViewById(R.id.wishlist_img);
       productDescription = findViewById(R.id.product_descrip);
        dataTable = findViewById(R.id.data_table_spec);
        textView21 = findViewById(R.id.textView21);

       loading = new ProgressDialog(ProductViewActivity.this);

       cartCollection = db.collection("Cart");
       wishlistCollection = db.collection("WishList");
        specCollection = db.collection("Specification");


       Bundle getData = getIntent().getExtras();
       productDoc = getData.getString("productDocId");

       setDetailes(productDoc);
        
       mines = findViewById(R.id.mines);
       plus = findViewById(R.id.plus);
       qty = findViewById(R.id.qty);



       SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ProductViewActivity.this);
       String mail = sp.getString("email", "empty");
       String docid = sp.getString("docid", "empty");

        setWishListIcon(productDoc,docid);

       addWishlist.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (mail.equals("empty") && docid.equals("empty")){
                   Intent intent = new Intent(ProductViewActivity.this, SeeMessagetoLogout.class);
                   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(intent);
               }else {
                   saveWishList(productDoc,docid);
               }
           }
       });

       mines.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               reduceQty();
           }
       });

       plus.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               increaseQty();
           }
       });

       carySaveBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               if (mail.equals("empty") && docid.equals("empty")){
                   Intent intent = new Intent(ProductViewActivity.this, SeeMessagetoLogout.class);
                   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(intent);
               }else {
                   saveCart(productDoc,docid);
               }

           }
       });



    }

    private void setWishListIcon(String productDoc, String docid) {
        wishlistCollection.whereEqualTo("productDocId",productDoc).whereEqualTo("userDocId",docid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                if (documentsSnapList.size() > 0) {
                    addWishlist.setImageResource(R.drawable.ic_heart__1_);
                }else{
                    addWishlist.setImageResource(R.drawable.ic_like);
                }
            }
        });
    }

    private void saveWishList(String productDoc, String docid) {
        loading.setTitle("Updating WishList");
        loading.setMessage("Please Wait, while we are checking the credentials.");
        loading.setCanceledOnTouchOutside(false);
        loading.show();
        wishlistCollection.whereEqualTo("productDocId",productDoc).whereEqualTo("userDocId",docid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                if (documentsSnapList.size() > 0) {
                    DocumentSnapshot d = documentsSnapList.get(0);
                    String wishlist_id = d.getId();
                    wishlistCollection.document(wishlist_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loading.dismiss();
                            setWishListIcon(productDoc,docid);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }else{
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("MM:dd:yyyy");
                    saveCurrentDate = currentDate.format(calendar.getTime());
                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                    saveCurrentTime = currentTime.format(calendar.getTime());

                    WishList wishList = new WishList();
                    wishList.setUserDocId(docid);
                    wishList.setProductDocId(productDoc);
                    wishList.setTime(saveCurrentTime);
                    wishList.setDate(saveCurrentDate);

                    wishlistCollection.add(wishList).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            loading.dismiss();
                            setWishListIcon(productDoc,docid);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }
            }
        });
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
                    int newQ = Integer.parseInt(qty.getText().toString());
                    int i = qtys + newQ;
                    cartCollection.document(cart_id).update("qty", String.valueOf(i))

                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.w("ss", "DocumentSnapshot successfully updated!");
                                    Toast.makeText(ProductViewActivity.this, String.valueOf(i) + " item added to your cart", Toast.LENGTH_SHORT).show();

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
                    SimpleDateFormat currentDate = new SimpleDateFormat("d-M-yyyy");
                    saveCurrentDate = currentDate.format(calendar.getTime());
                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                    saveCurrentTime = currentTime.format(calendar.getTime());
                    Cart cart = new Cart();

                    cart.setUserDocId(docid);
                    cart.setProductDocId(productDoc);
                    cart.setQty(qty.getText().toString());
                    cart.setDate(saveCurrentDate);
                    cart.setTime(saveCurrentTime);

                    cartCollection.add(cart).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(ProductViewActivity.this, qty.getText().toString() + " item added to your cart", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProductViewActivity.this, "Error :" + e.getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
    }

    private void setDetailes(String productDoc) {



        db.collection("Product").document(productDoc).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);
                productTitle.setText(product.getProductName());
                productPrice.setText("Rs :"+product.getPrice());
                productDescription.setText("Description :"+product.getDescription());
                Picasso.with(ProductViewActivity.this).load(product.getImgUrl()).into(productImage);

                specCollection.whereEqualTo("productDocId",productDoc).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){
                            textView21.setText("There aren't any specification on this product");
                        }else{
                            header = new DataTableHeader.Builder()
                                    .item("Type",15)
                                    .item("Value",15)

                                    .build();


                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Specification item = document.toObject(Specification.class);

                                DataTableRow row = new DataTableRow.Builder()
                                        .value(item.getType())
                                        .value(item.getValue())
                                        .build();
                                rows.add(row);
                                dataTable.setHeader(header);
                                dataTable.setRows(rows);
                                dataTable.inflate(ProductViewActivity.this);

                            }

                        }



                    }
                });
            }
        });
    }

    private void increaseQty() {

        String qt = qty.getText().toString();
        int i = Integer.parseInt(qt);
        int ne = ++i;
        qty.setText(String.valueOf(ne));
    }

    private void reduceQty() {
        String qt = qty.getText().toString();
        int i = Integer.parseInt(qt);
        int ne = --i;
        if (ne < 1){

        }else {
            qty.setText(String.valueOf(ne));
        }
    }
}