package com.lk.userapp.ui.event;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.lk.userapp.Holder.EventHolder;
import com.lk.userapp.Model.Event;
import com.lk.userapp.R;
import com.lk.userapp.ShowEventMap;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class EventFragment extends Fragment {

    private EditText selectDate;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private RecyclerView eventRecycler;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference eventCollection;
    private FirestoreRecyclerAdapter<Event, EventHolder> fseventAdapter;
    private ImageView refresh;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_event, container, false);

        selectDate = root.findViewById(R.id.date_select);
        eventRecycler = root.findViewById(R.id.event_recycler);
        eventRecycler.setLayoutManager(new LinearLayoutManager(EventFragment.super.getContext()));
        eventCollection = db.collection("Event");
        refresh = root.findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdepter("all");
                selectDate.setText("");
            }
        });

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(EventFragment.super.getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                selectDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                setAdepter(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        setAdepter("all");

        return root;
    }

    private void setAdepter(String all) {
        List<String> statusList = new ArrayList<>();
        statusList.add("publish");
        statusList.add("expired");

        Query loadCat = eventCollection.whereIn("status",statusList);
        if (all.equals("all")){
            loadCat = eventCollection.whereIn("status",statusList);
        }else{
            loadCat = eventCollection.whereEqualTo("date",all).whereIn("status",statusList);
        }
        FirestoreRecyclerOptions recyclerOptions = new FirestoreRecyclerOptions.Builder<Event>().setQuery(loadCat,Event.class).build();
        fseventAdapter = new FirestoreRecyclerAdapter<Event,EventHolder>(recyclerOptions){

            @NonNull
            @Override
            public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item,parent,false);
                return new EventHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull EventHolder holder, int position, @NonNull Event model) {
                holder.title.setText(model.getTitle());
                holder.merchant.setText(model.getMerchantName());
                holder.dateTime.setText(model.getDate()+"/"+model.getTime());
                holder.description.setText(model.getDescription());
                String id = getSnapshots().getSnapshot(position).getId();

                if (model.getStatus().equals("publish")){
                    holder.publishIcon.setImageResource(R.drawable.ic_check);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(EventFragment.super.getContext(), ShowEventMap.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("eventId",id);
                            startActivity(intent);
                        }
                    });

                }else{
                    holder.publishIcon.setImageResource(R.drawable.ic_warning);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(EventFragment.super.getContext(), "This Event Expired..", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                Picasso.with(EventFragment.super.getContext()).load(model.getImgUrl()).into(holder.eventImage);

            }
        };

        //set Adapter
        eventRecycler.setAdapter(fseventAdapter);
        fseventAdapter.startListening();
    }


}