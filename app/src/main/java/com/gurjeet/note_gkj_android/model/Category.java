package com.gurjeet.note_gkj_android.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category")
public class Category {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    int catId;

    @NonNull
    @ColumnInfo(name = "category_name")
    private String catName;

    public Category(@NonNull String catName) {
        this.catName = catName;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    @NonNull
    public String getCatName() {
        return catName;
    }

    public void setCatName(@NonNull String catName) {
        this.catName = catName;
    }
}
