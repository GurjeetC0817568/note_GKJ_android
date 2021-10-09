package com.gurjeet.note_gkj_android.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.gurjeet.note_gkj_android.model.Category;
import com.gurjeet.note_gkj_android.model.Note;
import com.gurjeet.note_gkj_android.utilities.NoteRoomDB;

import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Category>> allCategories;
    private LiveData<List<Note>> allNotes;

    public NoteRepository(Application application) {
        NoteRoomDB db = NoteRoomDB.getInstance(application);
        noteDao = db.noteDao();
        allCategories = noteDao.getAllCategories();
        allNotes = noteDao.getAllNotes();
    }


    // all categories related functions
    public LiveData<List<Category>> getAllCategories() {return allCategories; }
    public LiveData<Category> getCategoryById(int id) {return noteDao.getCategoryById(id); }
    public void insertCategory(Category category) {NoteRoomDB.databaseWriteExecutor.execute(() -> noteDao.insertCategory(category)); }
    public void updateCategory(Category category) { NoteRoomDB.databaseWriteExecutor.execute(() -> noteDao.updateCategory(category));}
    public void deleteCategory(int categoryId) { NoteRoomDB.databaseWriteExecutor.execute(() -> noteDao.deleteCategory(categoryId)); }



    // all notes related function
    public LiveData<List<Note>> getAllNotes() {return allNotes;}
    public LiveData<Note> getNoteById(int id) { return noteDao.getNoteById(id); }
    public void insertNote(Note note) {NoteRoomDB.databaseWriteExecutor.execute(() -> noteDao.insertNote(note));}
    public void update(Note note) {NoteRoomDB.databaseWriteExecutor.execute(() -> noteDao.update(note)); }
    public void delete(Note note) { NoteRoomDB.databaseWriteExecutor.execute(() -> noteDao.deleteNote(note)); }
    public LiveData<List<Note>> getNotesByCategory(int categoryId, boolean isAsc, boolean isDesc, String searchKey, boolean byDate) {
        return noteDao.getNotesByCategoryId(categoryId, isAsc, isDesc, searchKey, byDate);
    }public LiveData<List<Note>> getNotesByCategory(int categoryId) {return noteDao.getNotesByCategoryId(categoryId); }


}
