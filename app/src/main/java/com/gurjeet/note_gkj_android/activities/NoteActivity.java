package com.gurjeet.note_gkj_android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.gurjeet.note_gkj_android.R;
import com.gurjeet.note_gkj_android.model.Note;
import com.gurjeet.note_gkj_android.model.NoteViewModel;

import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {

    ImageView createNote;
    RecyclerView rcvNotes;

    private NoteViewModel noteAppViewModel;
    ArrayList<Note> noteList = new ArrayList<>();

    private int catId;

    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        createNote = findViewById(R.id.createNote);
        rcvNotes = findViewById(R.id.rcvNotes);


        rcvNotes.setLayoutManager(new LinearLayoutManager(this));

        catId = getIntent().getIntExtra(NoteActivity.CATEGORY_ID, 0);

        createNote.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), NoteDetailActivity.class);
            intent.putExtra(NoteActivity.CATEGORY_ID, getIntent().getIntExtra(NoteActivity.CATEGORY_ID, 0));
            startActivity(intent);
        });


    }
}