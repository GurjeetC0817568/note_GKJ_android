package com.gurjeet.note_gkj_android.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gurjeet.note_gkj_android.data.NoteRepository;
import com.gurjeet.note_gkj_android.model.Category;
import com.gurjeet.note_gkj_android.model.Note;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    private final LiveData<List<Category>> allCategories;
    private LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allCategories = repository.getAllCategories();
        allNotes = repository.getAllNotes();}


    //all categories related functions
    public LiveData<List<Category>> getAllCategories() {return allCategories;}
    public void insertCategory(Category category) {repository.insertCategory(category);}
    public void deleteCategory(int categoryId) {repository.deleteCategory(categoryId);}
    public void updateCategory(Category category) {repository.updateCategory(category);}
    public LiveData<Category> getCategoryById(int id) {return repository.getCategoryById(id);}

    //all notes related functions
    public LiveData<List<Note>> getAllNotes() {return allNotes;}
    public void insertNote(Note note) {repository.insertNote(note);}
    public void delete(Note note) {repository.delete(note);}
    public void update(Note note) {repository.update(note);}
    public LiveData<Note> getNoteById(int id) {return repository.getNoteById(id);}
    public LiveData<List<Note>> getNotesByCategory(int catId, boolean isAsc, boolean isDesc, String searchKey, boolean byDate) {
        allNotes = repository.getNotesByCategory(catId, isAsc, isDesc, searchKey, byDate);return allNotes;}
    public LiveData<List<Note>> getNotesByCategory(int catId) {allNotes = repository.getNotesByCategory(catId);return allNotes;}

}
