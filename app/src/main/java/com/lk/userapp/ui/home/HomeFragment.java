package com.lk.userapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.lk.userapp.Adepter.SliderAdapter;
import com.lk.userapp.Holder.MerchantHolder;
import com.lk.userapp.Holder.CategoryHolder;
import com.lk.userapp.Model.Add;
import com.lk.userapp.Model.Category;
import com.lk.userapp.Model.Merchant;
import com.lk.userapp.R;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Category> categoryList = new ArrayList<Category>();
    private FirestoreRecyclerAdapter<Category, CategoryHolder> fsProductAdapter;
    private FirestoreRecyclerAdapter<Merchant, MerchantHolder> fsMerchantAdapter;
    private ImageView imageView2;

    private HomeViewModel homeViewModel;

    public HomeFragment(){

    }

    private RecyclerView categoryRecycleView;
    private RecyclerView merchantRecycleView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        categoryRecycleView = root.findViewById(R.id.category_recycleview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecycleView.setLayoutManager(layoutManager);

        imageView2 = root.findViewById(R.id.imageView2);

        merchantRecycleView = root.findViewById(R.id.merchant_recyclerView);
        GridLayoutManager layoutMerchant=new GridLayoutManager(getActivity(),2);
        merchantRecycleView.setLayoutManager(layoutMerchant);


       // categoryList.clear();
        Query query = db.collection("Category").whereEqualTo("isActive","Active");
        Query queryMerchant = db.collection("Merchant").whereEqualTo("isActive","Active");

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMerchantAdepter(queryMerchant);
            }
        });

        FirestoreRecyclerOptions recyclerOptions = new FirestoreRecyclerOptions.Builder<Category>().setQuery(query,Category.class).build();


        fsProductAdapter = new FirestoreRecyclerAdapter<Category, CategoryHolder>(recyclerOptions) {

            @NonNull
            @Override
            public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
                return new CategoryHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CategoryHolder holder, int position, @NonNull Category model) {

                holder.categoryName.setText(model.getCategoryName());
                Picasso.with(HomeFragment.super.getContext()).load(model.getImgUrl()).into(holder.imgUrl);
                String id = getSnapshots().getSnapshot(position).getId();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Query queryMerchant = db.collection("Merchant").whereEqualTo("isActive","Active").whereEqualTo("catDocId",id);
                        setMerchantAdepter(queryMerchant);
                    }
                });
            }

        };
        //set Adapter
        categoryRecycleView.setAdapter(fsProductAdapter);

        setMerchantAdepter(queryMerchant);


        db.collection("Add").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                if (!documents.isEmpty()){
                    // we are creating array list for storing our image urls.
                    ArrayList<Add> sliderDataArrayList = new ArrayList<>();

                    // initializing the slider view.
                    SliderView sliderView = root.findViewById(R.id.banner_slider_view_pager);

                    // adding the urls inside array list
                    for (DocumentSnapshot document : documents) {
                        Add add = document.toObject(Add.class);
                        sliderDataArrayList.add(new Add(add.getImgUrl()));
                    }



                    // passing this array list inside our adapter class.
                    SliderAdapter adapter = new SliderAdapter(HomeFragment.super.getContext(), sliderDataArrayList);

                    // below method is used to set auto cycle direction in left to
                    // right direction you can change according to requirement.
                    sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);

                    // below method is used to
                    // setadapter to sliderview.
                    sliderView.setSliderAdapter(adapter);

                    // below method is use to set
                    // scroll time in seconds.
                    sliderView.setScrollTimeInSec(3);

                    // to set it scrollable automatically
                    // we use below method.
                    sliderView.setAutoCycle(true);

                    // to start autocycle below method is used.
                    sliderView.startAutoCycle();

                }
            }
        });



        return root;
    }

    private void setMerchantAdepter(Query queryMerchant) {

        FirestoreRecyclerOptions recyclerMerchant = new FirestoreRecyclerOptions.Builder<Merchant>().setQuery(queryMerchant,Merchant.class).build();
        fsMerchantAdapter = new FirestoreRecyclerAdapter<Merchant, MerchantHolder>(recyclerMerchant) {

            @NonNull
            @Override
            public MerchantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.merchant_item,parent,false);
                return new MerchantHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MerchantHolder holder, int position, @NonNull Merchant model) {

                holder.merchantName.setText(model.getMerchantName());
                Picasso.with(HomeFragment.super.getContext()).load(model.getImgUrl()).into(holder.imgUrl);
                holder.model = model;


            }

        };
        merchantRecycleView.setAdapter(fsMerchantAdapter);
        fsMerchantAdapter.startListening();

    }

    @Override
    public void onStart() {
        super.onStart();
        fsProductAdapter.startListening();
        fsMerchantAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        fsProductAdapter.stopListening();
        fsMerchantAdapter.stopListening();
    }
}