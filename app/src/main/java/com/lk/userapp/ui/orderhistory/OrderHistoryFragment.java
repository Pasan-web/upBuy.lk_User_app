package com.lk.userapp.ui.orderhistory;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.lk.userapp.Holder.EventHolder;
import com.lk.userapp.Holder.OrderHolder;
import com.lk.userapp.InvoiceActivity;
import com.lk.userapp.Model.Event;
import com.lk.userapp.Model.Invoice;
import com.lk.userapp.R;
import com.lk.userapp.ShowEventMap;
import com.lk.userapp.ui.event.EventFragment;
import com.lk.userapp.ui.profile.ProfileFragment;
import com.squareup.picasso.Picasso;

import java.util.Calendar;


public class OrderHistoryFragment extends Fragment {

    private Spinner spinner;
    private EditText startDate,endDate;
    private ImageView refresh;
    private RecyclerView recyclerView;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference orderCollection;
    private FirestoreRecyclerAdapter<Invoice, OrderHolder> fsorderAdapter;
    private String docid;

    public OrderHistoryFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_order_history, container, false);

        spinner = root.findViewById(R.id.spinner_order_status);
        startDate = root.findViewById(R.id.date_start);
        refresh = root.findViewById(R.id.refresh_orderlist);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(OrderHistoryFragment.super.getContext());
        String mail = sp.getString("email", "empty");
        docid = sp.getString("docid", "empty");

        orderCollection = db.collection("Invoice");

        recyclerView = root.findViewById(R.id.orderlist_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderHistoryFragment.super.getContext()));

        setSpinner();

        setAdepter("All");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startDate.setText("");
                String selectedItem = spinner.getSelectedItem().toString();
                if (selectedItem.equals("Pending")){
                    setAdepter("Pending");
                }else if (selectedItem.equals("Delivered")){
                    setAdepter("Delivered");
                }else if (selectedItem.equals("Received")){
                    setAdepter("Received");
                }else{
                    setAdepter("All");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(OrderHistoryFragment.super.getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                startDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                setAdepter("time");

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


        return root;
    }

    private void setAdepter(String all) {


        Query loadInvoice = orderCollection.whereEqualTo("userDocId",docid);
        if (all.equals("All")){
            loadInvoice = orderCollection.whereEqualTo("userDocId",docid);
        }else if (all.equals("Pending")){
            loadInvoice = orderCollection.whereEqualTo("userDocId",docid).whereEqualTo("status","ordered");
        }else if (all.equals("Delivered")){
            loadInvoice = orderCollection.whereEqualTo("userDocId",docid).whereEqualTo("status","delivered");
        }else if (all.equals("Received")){
            loadInvoice = orderCollection.whereEqualTo("userDocId",docid).whereEqualTo("status","received");
        }else{
            loadInvoice = orderCollection.whereEqualTo("userDocId",docid).whereGreaterThanOrEqualTo("date",startDate.getText().toString());
        }

        FirestoreRecyclerOptions recyclerOptions = new FirestoreRecyclerOptions.Builder<Invoice>().setQuery(loadInvoice,Invoice.class).build();
        fsorderAdapter = new FirestoreRecyclerAdapter<Invoice,OrderHolder>(recyclerOptions){

            @NonNull
            @Override
            public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderhistory_list_item,parent,false);
                return new OrderHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrderHolder holder, int position, @NonNull Invoice model) {
                String id = getSnapshots().getSnapshot(position).getId();
                holder.orderId.setText("Order Id:"+ id);
                holder.orderdate.setText("Order On:" + model.getDate()+"/"+model.getTime());
                holder.orderamount.setText("Total Amount: Rs." +model.getAmount());

                if (model.getStatus().equals("ordered")){
                    holder.orderS.setText("Pending..");
                    holder.orderS.setTextColor(Color.parseColor("#DC0000"));
                  //  holder.orderS.setTextColor(Color.red(R.color.red));
                    holder.status.setImageResource(R.drawable.ic_pending);
                }else if (model.getStatus().equals("delivered")){

                    holder.orderS.setText("Delivered..");
                    holder.orderS.setTextColor(Color.parseColor("#14AA09"));
                    holder.status.setImageResource(R.drawable.ic_check);
                    holder.recieve.setImageResource(R.drawable.ic_order_now);
                    holder.recieve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recievedOrder(id);
                        }
                    });

                }else if (model.getStatus().equals("received")){
                    holder.orderS.setText("Received..");
                    holder.status.setImageResource(R.drawable.ic_check);
                    holder.orderS.setTextColor(Color.parseColor("#000000"));
                    holder.recieve.setOnClickListener(null);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent invoice = new Intent(OrderHistoryFragment.super.getContext(), InvoiceActivity.class);
                        invoice.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        invoice.putExtra("invoiceId",id);
                        startActivity(invoice);
                    }
                });

            }
        };

        //set Adapter
        recyclerView.setAdapter(fsorderAdapter);
        fsorderAdapter.startListening();
    }

    private void recievedOrder(String id) {
        orderCollection.document(id).update("status","received").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(OrderHistoryFragment.super.getContext(), "Order received successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinner() {
        String[] status = {"All","Pending","Delivered","Received"};
        ArrayAdapter vListAdapter = new ArrayAdapter(OrderHistoryFragment.super.getContext(),android.R.layout.simple_selectable_list_item,status);
        spinner.setAdapter(vListAdapter);
    }
}