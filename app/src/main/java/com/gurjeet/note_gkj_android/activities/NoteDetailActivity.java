package com.gurjeet.note_gkj_android.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.gurjeet.note_gkj_android.R;
import com.gurjeet.note_gkj_android.model.Note;
import com.gurjeet.note_gkj_android.model.NoteViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class NoteDetailActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1;
    public static final int READ_EXTERNAL_STORAGE_CODE = 2;


    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    Geocoder geocoder;
    LatLng latLangNote = null;

    private NoteViewModel noteAppViewModel;
    ArrayList<Note> noteList = new ArrayList<>();

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

    private int catID = 0;
    private int noteId = 0;
    String pathSave = "", recordFile = null;
    Boolean isRecording = false, isPlaying = false, imageSet = false;

    ActivityResultLauncher<Intent> myActivityResultLauncher;

    private static final int FASTEST_INTERVAL = 1000; // 1 seconds
    private static final int UPDATE_INTERVAL = 5000; // 5 seconds
    private static final int SMALLEST_DISPLACEMENT = 200; // 200 meters

    private List<String> permissionsToRequest;
    private List<String> permissions = new ArrayList<>();
    private List<String> permissionsRejected = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
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

        /*****************Start upload image click*********************/
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
                } else {
                    pickImageFromGalary();//if permission allowed then pick image
                }
            }
        });



        /**************when record button clicked**************/
        //Reference: https://stackoverflow.com/questions/37338606/mediarecorder-not-saving-audio-to-file
        btnRecord.setOnClickListener(v -> {
            if (!isRecording) {
                if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && hasPermission(Manifest.permission.RECORD_AUDIO)) {
                    recordFile = "/" + UUID.randomUUID().toString() + ".3gp";
                    pathSave = getExternalCacheDir().getAbsolutePath()  + recordFile ;
                    setUpMediaRecorder();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        audioBannerV.setBackgroundColor(Color.parseColor("#FF7C4605"));
                        btnPlay.setEnabled(false);
                        btnPlay.setVisibility(View.GONE);
                        scrubber.setVisibility(View.GONE);

                        Toast.makeText(NoteDetailActivity.this, "Recording started...", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                mediaRecorder.stop();
                btnPlay.setEnabled(true);
                btnPlay.setVisibility(View.VISIBLE);
                scrubber.setVisibility(View.VISIBLE);
                audioBannerV.setBackgroundResource(R.color.teal_700);
            }
            isRecording = !isRecording;
        });


        /**************audio clicked for play icons**************/
        //Reference:https://www.tutlane.com/tutorial/android/android-audio-media-player-with-examples
        btnPlay.setVisibility(View.GONE);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    btnRecord.setEnabled(false);

                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(pathSave);
                        mediaPlayer.prepare();
                        scrubber.setProgress(0);
                        scrubber.setMax(mediaPlayer.getDuration());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.start();
                    audioBannerV.setBackgroundColor(Color.parseColor("#450C0C0C"));
                    btnPlay.setImageResource(R.drawable.pause);

                    Toast.makeText(NoteDetailActivity.this, "Playing...", Toast.LENGTH_SHORT).show();

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            btnRecord.setEnabled(true);
                            btnPlay.setImageResource(R.drawable.play);
                            audioBannerV.setBackgroundResource(R.color.white);
                            scrubber.setProgress(0);
                        }
                    });
                } else {
                    //if playing
                    btnRecord.setEnabled(true);
                    mediaPlayer.pause();
                    audioBannerV.setBackgroundResource(R.color.material_on_surface_disabled);
                    btnPlay.setImageResource(R.drawable.play);
                }
                isPlaying = !isPlaying;
            }
        });


        catID = getIntent().getIntExtra(NoteActivity.CATEGORY_ID, 0);
        noteId = getIntent().getIntExtra("note_id", -1) ;


        /**************Save button click for add **************/
        saveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = titleET.getText().toString().trim();
                String detail = detailET.getText().toString().trim();

                //setting date format
                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy hh:mm aa");
                String strDate = dateFormat.format(date);


                if (title.isEmpty() || detail.isEmpty()) {
                    alertBox("Enter both note title and description!");
                } else {
                    byte[] imageInByte = null;
                    if(imageSet){
                        // bitmap to jpeg converter
                        //Reference: https://stackoverflow.com/questions/20329090/how-to-convert-a-bitmap-to-a-jpeg-file-in-android

                        Bitmap bitmap = ((BitmapDrawable) uploadImage.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                        imageInByte = baos.toByteArray();
                    }


                    //add note
                    noteAppViewModel.insertNote(new Note(catID, title, detail, recordFile , latLangNote.latitude ,latLangNote.longitude,  imageInByte,strDate));
                    finish();
                }
            }
        });
        /**************Ends save button click for both add and update**************/


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

        // set the audio volume using streamVolume
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        noteAppViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(NoteViewModel.class);

        // Reference:https://stackoverflow.com/questions/62613424/java-solution-for-startactivityforresultintent-int-in-fragment-has-been-depre
        myActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();

                            uploadImage.setImageURI(data.getData());
                            imageSet = true;
                        }
                    }
                });

        // added permissions
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);
        if (permissionsToRequest.size() > 0)
            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CODE);
        else
            startUpdateLocation();

        //location initializer
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }
    /********************ON CREATE FUNCTIONS ENDS HERE*******************/

    //Reference: https://stackoverflow.com/questions/5309190/android-pick-images-from-gallery
    private void pickImageFromGalary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        myActivityResultLauncher.launch(intent);
    }


    //when activity resume is complete
    @Override
    protected void onPostResume() {
        super.onPostResume();

        int errorCode = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(this, errorCode, errorCode, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Toast.makeText(NoteDetailActivity.this, "Google Services Not Available", Toast.LENGTH_SHORT).show();
                        }
                    });
            errorDialog.show();
        }
    }

    //problem solved using reference:https://stackoverflow.com/questions/58311691/the-android-app-crashes-after-6-seconds-when-i-press-record-button
    private void setUpMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);

    }


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

    List<Address> addresses;
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
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    try {
                        //Reference:https://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
                        if(latLangNote == null){
                            latLangNote = new LatLng(location.getLatitude(), location.getLongitude());
                        }

                        addresses = geocoder.getFromLocation(latLangNote.latitude, latLangNote.longitude, 1);// Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        String address = "";
                        if (addresses != null && addresses.size() > 0) {
                            if (addresses.get(0).getThoroughfare() != null)
                                address += addresses.get(0).getThoroughfare() + ", "; // street
                            if (addresses.get(0).getPostalCode() != null)
                                address += addresses.get(0).getPostalCode() + ", "; // postal code
                            if (addresses.get(0).getLocality() != null)
                                address += addresses.get(0).getLocality() + ", "; // city
                            if (addresses.get(0).getAdminArea() != null)
                                address += addresses.get(0).getAdminArea(); // province

                        }

                        // adding address in textview
                        locationDetailsTV.setText(address);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    /************** Ends location related methods **************************/



    /************** Start permissions  methods **************************/
    @RequiresApi(api = Build.VERSION_CODES.M)
    private List<String> permissionsToRequest(List<String> permissions) {
        ArrayList<String> results = new ArrayList<>();
        for (String perm : permissions) {
            if (!hasPermission(perm))
                results.add(perm);
        }

        return results;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermission(String perm) {
        return checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
        } else if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGalary();
            }
        }
    }
    /************** Ends permissions  methods **************************/


    // alert box function
    public void alertBox(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteDetailActivity.this);
        builder.setTitle("Message!");
        builder.setMessage(message);

        builder.setCancelable(false);
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }



}