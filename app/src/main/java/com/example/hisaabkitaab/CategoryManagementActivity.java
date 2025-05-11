package com.example.hisaabkitaab;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCategories;
    private FloatingActionButton fabAddCategory;
    private DatabaseHelper databaseHelper;
    private List<Category> categoryList;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_management);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCategoryManagement), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manage Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        fabAddCategory = findViewById(R.id.fabAddCategory);

        // Setup database helper
        databaseHelper = new DatabaseHelper(this);

        // Setup RecyclerView
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList, this::showEditCategoryDialog, this::confirmDeleteCategory);
        recyclerViewCategories.setAdapter(categoryAdapter);

        // Load data
        loadCategories();

        // Setup FAB
        fabAddCategory.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void loadCategories() {
        categoryList.clear();
        categoryList.addAll(databaseHelper.getAllCategories());
        categoryAdapter.notifyDataSetChanged();
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_category, null);
        builder.setView(dialogView);

        final EditText editTextCategoryName = dialogView.findViewById(R.id.editTextCategoryName);
        Button buttonSave = dialogView.findViewById(R.id.buttonSaveCategory);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancelCategory);

        final AlertDialog dialog = builder.create();
        dialog.show();

        buttonSave.setOnClickListener(v -> {
            String categoryName = editTextCategoryName.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                Category category = new Category(0, categoryName);
                long result = databaseHelper.addCategory(category);
                if (result > 0) {
                    Toast.makeText(CategoryManagementActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                    loadCategories();
                } else {
                    Toast.makeText(CategoryManagementActivity.this, "Failed to add category", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            } else {
                editTextCategoryName.setError("Category name is required");
            }
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void showEditCategoryDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_category, null);
        builder.setView(dialogView);

        final EditText editTextCategoryName = dialogView.findViewById(R.id.editTextCategoryName);
        Button buttonSave = dialogView.findViewById(R.id.buttonSaveCategory);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancelCategory);

        editTextCategoryName.setText(category.getName());

        final AlertDialog dialog = builder.create();
        dialog.show();

        buttonSave.setOnClickListener(v -> {
            String categoryName = editTextCategoryName.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                category.setName(categoryName);
                int result = databaseHelper.updateCategory(category);
                if (result > 0) {
                    Toast.makeText(CategoryManagementActivity.this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                    loadCategories();
                } else {
                    Toast.makeText(CategoryManagementActivity.this, "Failed to update category", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            } else {
                editTextCategoryName.setError("Category name is required");
            }
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void confirmDeleteCategory(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete this category? All associated products will lose their category.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deleteCategory(category.getId());
                    Toast.makeText(CategoryManagementActivity.this, "Category deleted", Toast.LENGTH_SHORT).show();
                    loadCategories();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}