package com.lk.userapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.lk.userapp.GPS.GPSHelper;
import com.lk.userapp.Pojo.MapTimeObj;
import com.lk.userapp.Pojo.mapDistanceObj;
import com.lk.userapp.directionsLib.FetchURL;

public class MapsFragment extends Fragment {

    private static final int LOCATION_PERMISSION = 100;
    public GoogleMap currentGoogleMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    BitmapDescriptor bitmapDescriptor;
    private String TAG = "MapFragment";
    public Polyline polyline;

    public Marker currentMarker;


    public LatLng customerLocation;
    public LatLng dropLocation;
    public GPSHelper gpsHelper;
    public Marker destinationMaker2;
    public MarkerOptions my_location;
    public MarkerOptions event_location;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            currentGoogleMap = googleMap;

            if (ActivityCompat.checkSelfPermission(MapsFragment.super.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsFragment.super.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);

                return;
            } else {
                updateEventLocation();
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);

                    return;
                } else {
                    gpsHelper = new GPSHelper(MapsFragment.this);
                    gpsHelper.getCurrentLocationListner(getContext());
                }


            }
        }
    };

    public void updateEventLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsFragment.super.getContext());

        //get last location
        @SuppressLint("MissingPermission")
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {

                if (location != null){
                    Toast.makeText(MapsFragment.super.getContext(), "location :" +location.getLatitude() + "" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    BitmapDescriptor ico_current = getBitmapDescriptor(getActivity(),R.drawable.ic_profile);
                    BitmapDescriptor ico_event = getBitmapDescriptor(getActivity(),R.drawable.ic_calendar);


                    customerLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    my_location = new MarkerOptions().position(customerLocation).title("My Location").icon(ico_current).draggable(false);

                    currentMarker = currentGoogleMap.addMarker(my_location);
                    if (dropLocation != null){
                        event_location = new MarkerOptions().position(dropLocation).title("Event Location").icon(ico_event).draggable(false);
                        destinationMaker2 = currentGoogleMap.addMarker(event_location);
                        setPolyline(customerLocation,dropLocation);
                        currentGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(dropLocation));
                    }

                    currentGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(customerLocation));

                    currentGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(12));

//                    currentGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(customerLocation));
//                    currentGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(10));


                   // setPolyline(customerLocation,dropLocation);


                }else{
                    Toast.makeText(MapsFragment.super.getContext(), "Location Not Found !", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsFragment.super.getContext(), "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        gpsHelper = new GPSHelper(this);
        Location loc  = gpsHelper.getCurrentLocationListner(getContext());
        if (loc != null){
            Log.d("onviewCreated","lat"+loc.getLatitude()+"/loc"+loc.getLongitude());
        }
    }

    private BitmapDescriptor getBitmapDescriptor(FragmentActivity activity, int ic_walkto) {
        Drawable LAYER_1 = ContextCompat.getDrawable(activity,ic_walkto);
        LAYER_1.setBounds(0, 0, LAYER_1.getIntrinsicWidth(), LAYER_1.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(LAYER_1.getIntrinsicWidth(), LAYER_1.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        LAYER_1.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public void setPolyline(LatLng customerLocation, LatLng dropLocation) {

        new FetchURL() {
            @Override
            public void onTaskDone(Object... values) {
                if (polyline != null){
                    polyline.remove();
                }
                polyline = currentGoogleMap.addPolyline((PolylineOptions) values[0]);
            }

            @Override
            public void onDistanceTaskDone(mapDistanceObj distance) {
                if (polyline != null) {
                    ((ShowEventMap) getActivity()).setDuration(String.valueOf((distance.getDistanceValue()) / 1000));
                }
            }

            @Override
            public void onTimeTaskDone(MapTimeObj time) {
                if (polyline != null) {
                    ((ShowEventMap) getActivity()).setTime(time.getTimeInText());
                }
            }

        }.execute(getUrl(customerLocation,dropLocation,"driving"),"driving");

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        Log.d(TAG,"URL:"+url);
        return url;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION) {
            if (permissions.length > 0) {
                gpsHelper = new GPSHelper(this);
                gpsHelper.getCurrentLocationListner(getContext());
            }
        }
    }

    @Override
    public void onStop() {
        this.currentMarker.remove();
        this.polyline = null;
        this.gpsHelper = null;
        super.onStop();

    }
}