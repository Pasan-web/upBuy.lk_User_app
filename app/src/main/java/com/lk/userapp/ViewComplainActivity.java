package com.lk.userapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.lk.userapp.Holder.ComplainHolder;
import com.lk.userapp.Holder.OrderHolder;
import com.lk.userapp.Model.Complain;
import com.lk.userapp.Model.Invoice;
import com.lk.userapp.ui.orderfund.OrderFoundFragment;
import com.lk.userapp.ui.orderhistory.OrderHistoryFragment;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ViewComplainActivity extends AppCompatActivity {

    private EditText dateTime;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private String docid;
    private int mYear, mMonth, mDay;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference refundCollection;
    private FirestoreRecyclerAdapter<Complain, ComplainHolder> fscomplainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_complain);

        dateTime = findViewById(R.id.date_select_complain);
        spinner = findViewById(R.id.spinner);
        recyclerView = findViewById(R.id.complain_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refundCollection = db.collection("Complain");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ViewComplainActivity.this);
        String mail = sp.getString("email", "empty");
        docid = sp.getString("docid", "empty");

        Query loadComplain = refundCollection.whereEqualTo("userDocId",docid);

        setSpinner();

        dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(ViewComplainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                dateTime.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                SimpleDateFormat objSDF = new SimpleDateFormat("dd-MM-yyyy");
                                try {
                                    Date dt_1 = objSDF.parse(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    Query loadComplain = refundCollection.whereEqualTo("userDocId",docid).whereEqualTo("dateTime",dt_1);
                                    setAdepter(loadComplain);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        setAdepter(loadComplain);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = spinner.getSelectedItem().toString();
                if (s.equals("Pending")){
                    ArrayList<String> list = new ArrayList<>();
                    list.add("send");
                    Query loadComplain = refundCollection.whereEqualTo("userDocId",docid).whereIn("status",list);
                    setAdepter(loadComplain);
                }else if (s.equals("Confirm")){
                    ArrayList<String> list = new ArrayList<>();
                    list.add("confirm");
                    Query loadComplain = refundCollection.whereEqualTo("userDocId",docid).whereIn("status",list);
                    setAdepter(loadComplain);
                }else if (s.equals("Rejected")){
                    ArrayList<String> list = new ArrayList<>();
                    list.add("reject");
                    Query loadComplain = refundCollection.whereEqualTo("userDocId",docid).whereIn("status",list);
                    setAdepter(loadComplain);
                }else {
                    setAdepter(loadComplain);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setAdepter(Query query) {
        FirestoreRecyclerOptions recyclerOptions = new FirestoreRecyclerOptions.Builder<Complain>().setQuery(query,Complain.class).build();
        fscomplainAdapter = new FirestoreRecyclerAdapter<Complain,ComplainHolder>(recyclerOptions){

            @NonNull
            @Override
            public ComplainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.complain_list_item,parent,false);
                return new ComplainHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ComplainHolder holder, int position, @NonNull Complain model) {
                String id = getSnapshots().getSnapshot(position).getId();

                Picasso.with(ViewComplainActivity.this).load(model.getInvoiceUrl()).into(holder.complainImg);
                SimpleDateFormat objSDF = new SimpleDateFormat("dd-MM-yyyy");
                holder.datetime.setText(objSDF.format(model.getDateTime()));
                holder.reason.setText(model.getReason());

                if (model.getStatus().equals("send")){
                    holder.status.setText("Pending..");
                    holder.status.setTextColor(Color.parseColor("#DC0000"));

                }else if (model.getStatus().equals("confirm")){
                    holder.status.setText("Received..");
                    holder.status.setTextColor(Color.parseColor("#14AA09"));
                    holder.statusImg.setImageResource(R.drawable.ic_check);
                }else if (model.getStatus().equals("reject")){
                    holder.status.setText("Rejected..");
                    holder.status.setTextColor(Color.parseColor("#EA0E0E"));
                    holder.statusImg.setImageResource(R.drawable.ic_reject);
                }

            }
        };

        //set Adapter
        recyclerView.setAdapter(fscomplainAdapter);
        fscomplainAdapter.startListening();
    }

    private void setSpinner() {
        String[] status = {"All","Pending","Confirm","Rejected"};
        ArrayAdapter vListAdapter = new ArrayAdapter(ViewComplainActivity.this,android.R.layout.simple_selectable_list_item,status);
        spinner.setAdapter(vListAdapter);
    }
}