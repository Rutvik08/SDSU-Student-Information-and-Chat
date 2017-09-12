package com.example.rutvik.chat;


import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LatLonMap extends Fragment implements View.OnClickListener{

    private GoogleMap gMap;
    private com.google.android.gms.maps.MapView mapView;
    public Double lat = 32.7157;
    public Double lng =-117.1611;
    Button set,cancel;

    CameraPosition cameraPosition;
    List<Address> address;
    LatLng location;

    public LatLonMap() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lat_lon_map, container, false);
        Bundle bundle = this.getArguments();
        cancel = (Button) view.findViewById(R.id.mDone);
        cancel.setOnClickListener(this);
        set = (Button) view.findViewById(R.id.mCancel);
        set.setOnClickListener(this);
        mapView = (com.google.android.gms.maps.MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String addressText = bundle.getString("city")+", "+bundle.getString("state")+", "+bundle.getString("country");
        Geocoder locator = new Geocoder(getActivity());
        try {
            address = locator.getFromLocationName(addressText, 1);
            for (Address addressLocation : address) {
                if (addressLocation.hasLatitude()){
                    lat = addressLocation.getLatitude();}
                if (addressLocation.hasLongitude()){
                    lng = addressLocation.getLongitude();}

            }
            location = new LatLng(lat, lng);
            //gMap.addMarker(new MarkerOptions().position(location));
        } catch (Exception error) {
            error.getMessage();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsInitializer.initialize(getActivity().getApplicationContext());
                gMap = googleMap;
                gMap.getUiSettings().setZoomControlsEnabled(true);
                gMap.getUiSettings().setRotateGesturesEnabled(false);
                gMap.getUiSettings().setScrollGesturesEnabled(true);
                gMap.getUiSettings().setTiltGesturesEnabled(false);
                //LatLng sanDiego = new LatLng(32.7157, -117.1611);
                gMap.addMarker(new MarkerOptions().position(location));
                cameraPosition = CameraPosition.builder().target(location).build();
                gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                gMap.animateCamera(CameraUpdateFactory.zoomTo(6));
                gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        gMap.clear();
                        cameraPosition = CameraPosition.builder().target(latLng).build();
                        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        gMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                        gMap.addMarker(markerOptions);
                        lat = latLng.latitude;
                        lng = latLng.longitude;
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mDone:
                EditText lati = (EditText)getActivity().findViewById(R.id.latEt);
                EditText longi = (EditText)getActivity().findViewById(R.id.lonEt);
                lati.setText(String.valueOf(lat));
                longi.setText(String.valueOf(lng));
                getFragmentManager().popBackStack();
                break;
            case R.id.mCancel:
                getFragmentManager().popBackStack();
                break;
        }
    }

}
