package com.gurjeet.note_gkj_android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.gurjeet.note_gkj_android.R;
import com.gurjeet.note_gkj_android.model.Note;
import com.gurjeet.note_gkj_android.model.NoteViewModel;

import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {

    ImageView createNote;
    RecyclerView rcvNotes;
    TextView sortAZ, sortZA, sortDate;
    SearchView searchView;

    private NoteViewModel noteAppViewModel;
    ArrayList<Note> noteList = new ArrayList<>();
    ArrayList<Note> filteredList = new ArrayList<>();

    //TODO: Adapter need to create
    private NoteAdapter noteAdapter;
    private int catId;
    private String searchKey = "";

    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        createNote = findViewById(R.id.createNote);
        searchView = findViewById(R.id.searchView);
        rcvNotes = findViewById(R.id.rcvNotes);

        //to not change the size of recycler view when change in adapter content
        rcvNotes.setHasFixedSize(true);

        catId = getIntent().getIntExtra(NoteActivity.CATEGORY_ID, 0);

        //by clicking move to detail activity page
        createNote.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), NoteDetailActivity.class);
            intent.putExtra(NoteActivity.CATEGORY_ID, getIntent().getIntExtra(NoteActivity.CATEGORY_ID, 0));
            startActivity(intent);
        });




        // back button click to go back
        findViewById(R.id.imgBack).setOnClickListener(v -> {
            finish();
        });


    }

    /************Starts NoteAdapter part***************************/

    class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
        private Activity activity;
        private ArrayList<Note> noteList;

        NoteAdapter(Activity activity, ArrayList<Note> noteList) {
            this.activity = activity;
            this.noteList = noteList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_notes, parent, false);

            return new NoteAdapter.ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return noteList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull NoteActivity.NoteAdapter.ViewHolder holder, int position) {

            holder.noteTitle.setText(noteList.get(position).getNoteName());
            holder.categoryName.setText(getIntent().getStringExtra(NoteActivity.CATEGORY_NAME));
            holder.noteCreateDate.setText(noteList.get(position).getNoteDate());
            holder.bind(noteList.get(position));
        }


        class ViewHolder extends RecyclerView.ViewHolder {

            TextView noteTitle,noteCreateDate,categoryName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                //setting title,category name and date for list
                noteTitle = itemView.findViewById(R.id.noteTitle);
                categoryName = itemView.findViewById(R.id.categoryName);
                noteCreateDate = itemView.findViewById(R.id.noteCreateDate);
            }

            // binding data to show in noteDetailActivity class for full view
            public void bind(Note note) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NoteActivity.this, NoteDetailActivity.class);
                        intent.putExtra("note_id", note.getNoteId());
                        intent.putExtra("noteCategoryId", note.getNoteCategoryId());
                        intent.putExtra("note_name", note.getNoteName());
                        intent.putExtra("note_detail", note.getNoteDetail());
                        intent.putExtra("note_image", note.getNoteImage());
                        intent.putExtra("note_audio_path", note.getNoteRecordingPath());
                        intent.putExtra("note_longitude", note.getNoteLongitude());
                        intent.putExtra("note_latitude", note.getNoteLatitude());

                        startActivity(intent);
                    }
                });
            }
        }
    }
    /************Ends NoteAdapter part***************************/


}