package com.example.hisaabkitaab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProductCatalogActivity extends AppCompatActivity {
    private RecyclerView recyclerViewProducts;
    private Spinner spinnerCategories;
    private Button buttonManageCategories;
    private FloatingActionButton fabAddProduct;

    private ProductAdapter productAdapter;
    private DatabaseHelper databaseHelper;
    private List<Product> productList;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_catalog);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_catalog);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainProductCatalog), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        buttonManageCategories = findViewById(R.id.buttonManageCategories);
        fabAddProduct = findViewById(R.id.fabAddProduct);

        // Setup database helper
        databaseHelper = new DatabaseHelper(this);

        // Setup RecyclerView
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        recyclerViewProducts.setAdapter(productAdapter);

        // Load data
        loadCategories();
        loadProducts();

        // Setup listeners
        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(ProductCatalogActivity.this, ProductDetailActivity.class);
            startActivity(intent);
        });

        buttonManageCategories.setOnClickListener(v -> {
            Intent intent = new Intent(ProductCatalogActivity.this, CategoryManagementActivity.class);
            startActivity(intent);
        });

        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // "All Categories" selected
                    loadProducts();
                } else {
                    // Specific category selected
                    int categoryId = categoryList.get(position - 1).getId();
                    loadProductsByCategory(categoryId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadProducts();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
        loadProducts();
    }

    private void loadCategories() {
        categoryList = databaseHelper.getAllCategories();
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("All Categories");

        for (Category category : categoryList) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(categoryAdapter);
    }

    private void loadProducts() {
        productList.clear();
        productList.addAll(databaseHelper.getAllProducts());
        productAdapter.notifyDataSetChanged();
    }

    private void loadProductsByCategory(int categoryId) {
        productList.clear();
        productList.addAll(databaseHelper.getProductsByCategory(categoryId));
        productAdapter.notifyDataSetChanged();
    }
}