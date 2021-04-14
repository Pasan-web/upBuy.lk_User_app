package com.lk.userapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lk.userapp.Model.Event;

public class ShowEventMap extends AppCompatActivity {

    private String eventId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference eventCollection;
    public MapsFragment mf;
    FragmentManager fm = getSupportFragmentManager();
    private boolean dragable = true;
    private double latitude;
    private double longitude;
    private TextView expectedtime;
    private TextView duration;
    private TextView back;


    public boolean isDragable() {
        return dragable;
    }
    public void setTime(String expectTime){

        expectedtime.setText(expectTime);
    }
    public void setDuration(String timeInText) {
        duration.setText(timeInText+" km");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event_map);

        Bundle bundle = getIntent().getExtras();
        eventId = bundle.getString("eventId");
        eventCollection = db.collection("Event");
        expectedtime = findViewById(R.id.expected_time);
        duration = findViewById(R.id.distance);
        back = findViewById(R.id.back_lbl);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEventMap.super.onBackPressed();

            }
        });

        if (mf == null) {
            mf = (MapsFragment) fm.findFragmentById(R.id.fragment);
        }

        getEventLocation(eventId);
    }

    private void getEventLocation(String eventId) {
        eventCollection.document(eventId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Event event = documentSnapshot.toObject(Event.class);
                LatLng eventLat = new LatLng(Double.valueOf(event.getLatitute()),Double.valueOf(event.getLongitute()));
                mf.dropLocation = eventLat;
                mf.currentGoogleMap.clear();
                mf.updateEventLocation();
            }
        });
    }

    @Override
    protected void onStop() {
        mf.gpsHelper = null;
        mf.polyline = null;
        mf.currentMarker.remove();
        mf = null;
        super.onStop();

    }
}