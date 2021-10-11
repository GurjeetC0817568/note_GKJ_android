package com.gurjeet.note_gkj_android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gurjeet.note_gkj_android.R;

import java.util.Timer;
import java.util.TimerTask;

public class NoteDetailActivity extends AppCompatActivity {

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


    }



}