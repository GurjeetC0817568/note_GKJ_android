package com.gurjeet.note_gkj_android.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

//one to many relationship set here for notes and category
public class CategoryNotes {
    @Embedded
    public Category category;

    @Relation(parentColumn = "category_id",entityColumn = "noteCategoryId")
    public List<Note> notes;
}

//Reference link: https://stackoverflow.com/questions/48443760/one-to-many-relation-in-room