package com.gurjeet.note_gkj_android.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.gurjeet.note_gkj_android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class NoteDetailActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1;
    public static final int READ_EXTERNAL_STORAGE_CODE = 2;


    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    Geocoder geocoder;
    LatLng latLangNote = null;


    //variables set
    ImageButton btnPlay, btnRecord;
    TextView locationDetailsTV,saveTV;
    ImageView btnBack, uploadImage, mapIcon;
    View audioBannerV;
    EditText titleET, detailET;
    AudioManager audioManager;
    MediaRecorder mediaRecorder;
    SeekBar scrubber;
    MediaPlayer mediaPlayer;

    private static final int FASTEST_INTERVAL = 1000; // 1 seconds
    private static final int UPDATE_INTERVAL = 5000; // 5 seconds
    private static final int SMALLEST_DISPLACEMENT = 200; // 200 meters

    private List<String> permissionsToRequest;
    private List<String> permissions = new ArrayList<>();
    private List<String> permissionsRejected = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        //variable assigned
        titleET = findViewById(R.id.titleET);
        detailET = findViewById(R.id.detailET);
        btnBack = findViewById(R.id.backBtn);
        saveTV = findViewById(R.id.saveTV);
        btnRecord = findViewById(R.id.recorderBtn);
        btnPlay = findViewById(R.id.playerBtn);
        uploadImage = findViewById(R.id.uploadImage);
        audioBannerV = findViewById(R.id.audioBannerV);
        scrubber = findViewById(R.id.scrubber);
        locationDetailsTV = findViewById(R.id.locationDetailsTV);
        mapIcon = findViewById(R.id.mapIcon);




        /**************Starts scrubber function here**************/

        //Reference: https://stackoverflow.com/questions/40031333/android-audio-seekbar-delay
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    scrubber.setProgress(mediaPlayer.getCurrentPosition());
            }
        }, 0, 300);


        scrubber.setVisibility(View.GONE);
        scrubber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        /**************Ends scrubber function here**************/


        // added permissions
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);
        if (permissionsToRequest.size() > 0)
            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CODE);
        else
            startUpdateLocation();

    }
    /********************ON CREATE FUNCTIONS ENDS HERE*******************/


    /************** Start location related methods **************************/
    @SuppressLint("MissingPermission")
    private void findLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    locationDetailsTV.setText(String.format("Accuracy: %s,Altitude: %s", location.getAccuracy(), location.getAltitude()));
                }
            }
        });

        startUpdateLocation();
    }

    @SuppressLint("MissingPermission")
    private void startUpdateLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault()); // sets the geocoder object

                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    try {

                    } catch (Exception e) {
                        e.printStackTrace(); // catch the error
                    }
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    /************** Ends location related methods **************************/

    @RequiresApi(api = Build.VERSION_CODES.M)
    private List<String> permissionsToRequest(List<String> permissions) {
        ArrayList<String> results = new ArrayList<>();
        for (String perm : permissions) {
                results.add(perm);
        }

        return results;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermission(String perm) {
        return checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            for (String perm : permissions) {
                if (!hasPermission(perm))
                    permissionsRejected.add(perm);
            }

            if (permissionsRejected.size() > 0) {
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    new AlertDialog.Builder(NoteDetailActivity.this)
                            .setMessage("The permission is mandatory")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), REQUEST_CODE);
                                }
                            }).setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
            }else{
                startUpdateLocation();
            }
        }
    }
}