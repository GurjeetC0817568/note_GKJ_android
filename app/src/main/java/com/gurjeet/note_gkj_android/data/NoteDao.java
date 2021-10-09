package com.gurjeet.note_gkj_android.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gurjeet.note_gkj_android.model.Category;
import com.gurjeet.note_gkj_android.model.Note;

import java.util.List;

@Dao
public interface NoteDao {

    //insert queries
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCategory(Category category);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertNote(Note note);



    //select queries
    @Query("SELECT * FROM category")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM category WHERE category_id = :id")
    public LiveData<Category> getCategoryById(int id);

    @Query("SELECT * FROM note")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM note WHERE note_id = :id")
    public LiveData<Note> getNoteById(int id);

    @Query("SELECT * FROM note WHERE noteCategoryId = :categoryId AND note_name LIKE  '%' || :searchName || '%'  ORDER BY CASE WHEN :byDate = 1 THEN note_create_date END DESC, CASE  WHEN :isAsc = 1 THEN note_name END ASC, CASE WHEN :isDesc = 1 THEN note_name END DESC")
    LiveData<List<Note>> getNotesByCategoryId(int categoryId, boolean isAsc, boolean isDesc, String searchName, boolean byDate);

    @Query("SELECT * FROM note WHERE noteCategoryId = :categoryId")
    LiveData<List<Note>> getNotesByCategoryId(int categoryId);

    @Query("SELECT * FROM note WHERE note_name LIKE :searchName AND noteCategoryId = :categoryId")
    public List<Note> getNotesSearchResult(int categoryId, String searchName);



    //update queries
    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(Note note);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateCategory(Category category);



    // delete queries
    @Delete
    void deleteNote(Note note);

    @Query("DELETE FROM note")
    void deleteAll();

    @Query("DELETE FROM category WHERE category_id = :categoryId")
    void deleteCategory(int categoryId);
}
