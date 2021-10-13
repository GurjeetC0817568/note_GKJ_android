package com.gurjeet.note_gkj_android.activities;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gurjeet.note_gkj_android.R;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng latLangNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String address = "";
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        latLangNote = new LatLng(getIntent().getDoubleExtra("note_latitude",0) ,  getIntent().getDoubleExtra("note_longitude",0));

        try {
            //address list array to store data using latitude and longitude
            List<Address> addressList = geocoder.getFromLocation(latLangNote.latitude, latLangNote.longitude, 1);
            if (addressList != null && addressList.size() > 0) { // if the addressList not empty
                address = "";

                if (addressList.get(0).getAdminArea() != null)
                    address += addressList.get(0).getAdminArea() + ", "; // province name
                if (addressList.get(0).getPostalCode() != null)
                    address += addressList.get(0).getPostalCode() + ", "; // postal code name
                if (addressList.get(0).getLocality() != null)
                    address += addressList.get(0).getLocality() + ", "; // city name
                if (addressList.get(0).getThoroughfare() != null)
                    address += addressList.get(0).getThoroughfare(); // street name

                //add marker on address
                mMap.addMarker(new MarkerOptions()
                        .position(latLangNote)
                        .title("Your note Location here")
                        .snippet(address)
                );

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLangNote, 15));
            }
        } catch (Exception e) {
            mMap.addMarker(new MarkerOptions()
                    .position(latLangNote)
                    .title("Your were here")
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLangNote, 15));
            e.printStackTrace();
        }
    }
}