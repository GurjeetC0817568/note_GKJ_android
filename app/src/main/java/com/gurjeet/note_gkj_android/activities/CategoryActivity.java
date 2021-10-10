package com.gurjeet.note_gkj_android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gurjeet.note_gkj_android.R;
import com.gurjeet.note_gkj_android.model.Category;
import com.gurjeet.note_gkj_android.model.NoteViewModel;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView rcvCategories;
    ImageView createCategory;
    private NoteViewModel noteViewModel;
    ArrayList<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        noteViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(NoteViewModel.class);

        noteViewModel.getAllCategories().observe(this, categories -> {
            categoryList.clear();
            categoryList.addAll(categories);
            rcvCategories = findViewById(R.id.rcvCategories);
            rcvCategories.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
            rcvCategories.setAdapter(new CategoryAdapter(this, categoryList));
        });

        createCategory = findViewById(R.id.createCategory);
        createCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
                LayoutInflater layoutInflater = LayoutInflater.from(CategoryActivity.this);
                View view = layoutInflater.inflate(R.layout.dialog_create_category, null);
                builder.setView(view);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                EditText categoryNameET = view.findViewById(R.id.categoryNameET);
                Button btnCreate = view.findViewById(R.id.createCategoryBTN);

                btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String categoryName = categoryNameET.getText().toString().trim();
                        if (categoryName.isEmpty()) {
                            alertBox("Enter Category name");
                            return;
                        }
                       /* if (categoryList.contains(categoryName)) {
                            alertBox("Category name already exist!");
                            return;
                        }*/

                        noteViewModel.insertCategory(new Category(categoryName));
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }


    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private Activity activity;
        private ArrayList<Category> categoryList;

        CategoryAdapter(Activity activity, ArrayList<Category> categoryList) {
            this.activity = activity;
            this.categoryList = categoryList;
        }

        @NonNull
        @Override
        public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_category, parent, false);
            return new CategoryAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.categoryNameTV.setText(categoryList.get(position).getCatName());

            // goto notes of clicked category
            holder.categoryNameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //open notes page of clicked category
                    Intent intent = new Intent(getBaseContext(), NoteActivity.class);
                    intent.putExtra(NoteActivity.CATEGORY_ID, categoryList.get(position).getCatId());
                    intent.putExtra(NoteActivity.CATEGORY_NAME, categoryList.get(position).getCatName());
                    startActivity(intent);
                }
            });

            // delete category
            //Reference: https://stackoverflow.com/questions/26076965/android-recyclerview-addition-removal-of-items
            holder.categoryNameTV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CategoryActivity.this);
                    builder.setTitle("Delete this Category?");
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        noteViewModel.deleteCategory(categoryList.get(position).getCatId());
                        // Toast.makeText(CategoryActivity.this, "The category  is  deleted", Toast.LENGTH_SHORT).show();
                    });
                    builder.setNegativeButton("No", (dialog, which) -> {
                        //do nothing when clicked No
                    });
                    androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return false;
                }
            });

        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView categoryNameTV;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                categoryNameTV = itemView.findViewById(R.id.categoryNameTV);
            }
        }

    }

    public void alertBox(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
        builder.setTitle("Message!");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}