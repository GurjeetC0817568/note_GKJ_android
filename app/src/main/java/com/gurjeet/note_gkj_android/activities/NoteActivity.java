package com.gurjeet.note_gkj_android.activities;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.gurjeet.note_gkj_android.R;
import com.gurjeet.note_gkj_android.model.Category;
import com.gurjeet.note_gkj_android.model.Note;
import com.gurjeet.note_gkj_android.model.NoteViewModel;

import java.util.ArrayList;
import java.util.List;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class NoteActivity extends AppCompatActivity {

    ImageView createNote;
    RecyclerView rcvNotes;
    TextView sortAZ, sortZA, sortDate;
    SearchView searchView;

    private NoteViewModel noteAppViewModel;
    ArrayList<Note> noteList = new ArrayList<>();
    List<Category> selectedCategory = new ArrayList<>();
    List<String> catSpinnerArr= new ArrayList<>();


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


        //Sort option variable
        sortAZ = findViewById(R.id.sortAZ);
        sortZA = findViewById(R.id.sortZA);
        sortDate = findViewById(R.id.sortDate);
        //setting asc,desc,date wise sort option for each variable
        sortAZ.setOnClickListener(v -> getNoteLists(true, false, false));
        sortZA.setOnClickListener(v -> getNoteLists(false, true, false));
        sortDate.setOnClickListener(v -> getNoteLists(false, false, true));

        noteAppViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(NoteViewModel.class);

        // back button click to go back
        findViewById(R.id.imgBack).setOnClickListener(v -> {
            finish();
        });


    }


    /************Starts notes list with sort option***************************/
    public void getNoteLists(boolean isAsc, boolean isDesc, boolean byDate) {
        noteAppViewModel.getNotesByCategory(catId, isAsc, isDesc, searchKey, byDate).observe(this, notes -> {
            noteList.clear();
            noteList.addAll(notes);
            noteAdapter.notifyDataSetChanged();
        });
    }
    /************Ends notes list with sort option***************************/


    /************Start onResume during search***************************/
    @Override
    protected void onResume() {//restart the activity with onResume method
        super.onResume();

        noteAdapter = new NoteAdapter(this, noteList);
        rcvNotes.setAdapter(noteAdapter);

        getNoteLists(true, false, false);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchKey = newText;
                getNoteLists(true, false, false);
                return false;
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rcvNotes);
    }
    /************Ends onResume during search***************************/

    /************Starts Left Right Swipe Part***************************/
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                //Delete task when left swipe
                case ItemTouchHelper.LEFT:
                    AlertDialog.Builder builderl = new AlertDialog.Builder(NoteActivity.this);
                    builderl.setTitle("You sure to delete this note?");

                    //when click yes then delete
                    builderl.setPositiveButton("Yes", (dialog, which) -> {
                        noteAppViewModel.delete(noteList.get(position));
                    });
                    //when click No then do nothing
                    builderl.setNegativeButton("No", (dialog, which) -> noteAdapter.notifyDataSetChanged());
                    AlertDialog alertDialog = builderl.create();
                    alertDialog.show();
                    break;
                //move category part when right swipe
                case ItemTouchHelper.RIGHT:
                    noteAppViewModel.getAllCategories().observe(NoteActivity.this, categories -> {
                        if (categories.size() == 1){
                            //move function will not work if there is only one category
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(NoteActivity.this);
                            builder.setTitle("Warning! can not possible!");
                            builder.setMessage("There is one category only");
                            builder.setCancelable(false);
                            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                        }else{
                            //if more than 1 categories then update the note's categoryId in note table
                            selectedCategory = categories;
                            for (Category category :categories){
                                catSpinnerArr.add(category.getCatName());
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
                            LayoutInflater layoutInflater = LayoutInflater.from(NoteActivity.this);
                            View view = layoutInflater.inflate(R.layout.dialog_move_note_category, null);
                            builder.setView(view);
                            final AlertDialog alertDialogr = builder.create();
                            alertDialogr.show();

                            //Reference:https://stackoverflow.com/questions/6485158/custom-style-setdropdownviewresource-android-spinner/22178862
                            //Reference: https://stackoverflow.com/questions/40261501/how-to-set-same-appearance-for-spinner-in-xml-design
                            Spinner otherCategoriesSp = view.findViewById(R.id.otherCategoriesSp);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, catSpinnerArr);
                            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            otherCategoriesSp.setAdapter(adapter);

                            //when popup dialog box's button click after selecting new category
                            Button btnChangeCat = view.findViewById(R.id.btnChangeCategory);
                            btnChangeCat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final Note noteToUpdate = noteList.get(position);
                                    final Category categoryToUpdate = selectedCategory.get(otherCategoriesSp.getSelectedItemPosition());
                                    //set note's category id to new categoryId in note table
                                    noteToUpdate.setNoteCategoryId(categoryToUpdate.getCatId());
                                    noteAppViewModel.update(noteToUpdate);
                                    alertDialogr.dismiss();
                                }
                            });
                        }
                    });
            }
        }



    };
    /************Ends Left Right Swipe Part***************************/


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