package com.gurjeet.note_gkj_android.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class Note {

    //set note table fields names and its type
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    private int noteId;

    @NonNull
    private int noteCategoryId;

    @NonNull
    @ColumnInfo(name = "note_name")
    private String noteName;

    @ColumnInfo(name = "note_detail")
    private String noteDetail;

    @ColumnInfo(name = "note_recording_path")
    private String noteRecordingPath;

    @ColumnInfo(name = "note_latitude")
    private Double noteLatitude;

    @ColumnInfo(name = "note_longitude")
    private Double noteLongitude;

    @ColumnInfo(name = "note_image", typeAffinity = ColumnInfo.BLOB)
    private byte[] noteImage;

    @ColumnInfo(name = "note_create_date")
    private String noteDate;

    //constructor
    public Note(int noteCategoryId, @NonNull String noteName, String noteDetail, String noteRecordingPath, Double noteLatitude, Double noteLongitude, byte[] noteImage, String noteDate) {
        this.noteCategoryId = noteCategoryId;
        this.noteName = noteName;
        this.noteDetail = noteDetail;
        this.noteRecordingPath = noteRecordingPath;
        this.noteLatitude = noteLatitude;
        this.noteLongitude = noteLongitude;
        this.noteImage = noteImage;
        this.noteDate = noteDate;
    }

    //setters and getters
    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getNoteCategoryId() {
        return noteCategoryId;
    }

    public void setNoteCategoryId(int noteCategoryId) {
        this.noteCategoryId = noteCategoryId;
    }

    @NonNull
    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(@NonNull String noteName) {
        this.noteName = noteName;
    }

    public String getNoteDetail() {
        return noteDetail;
    }

    public void setNoteDetail(String noteDetail) {
        this.noteDetail = noteDetail;
    }

    public String getNoteRecordingPath() {
        return noteRecordingPath;
    }

    public void setNoteRecordingPath(String noteRecordingPath) {
        this.noteRecordingPath = noteRecordingPath;
    }

    public Double getNoteLatitude() {
        return noteLatitude;
    }

    public void setNoteLatitude(Double noteLatitude) {
        this.noteLatitude = noteLatitude;
    }

    public Double getNoteLongitude() {
        return noteLongitude;
    }

    public void setNoteLongitude(Double noteLongitude) {
        this.noteLongitude = noteLongitude;
    }

    public byte[] getNoteImage() {
        return noteImage;
    }

    public void setNoteImage(byte[] noteImage) {
        this.noteImage = noteImage;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }
}
