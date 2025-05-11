package com.example.hisaabkitaab;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {
    private EditText editTextProductName, editTextProductDescription, editTextProductPrice, editTextStockQuantity;
    private Spinner spinnerProductCategory;
    private Button buttonSaveProduct;

    private DatabaseHelper databaseHelper;
    private List<Category> categoryList;
    private Product existingProduct;
    private int productId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainProductDetail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        editTextStockQuantity = findViewById(R.id.editTextStockQuantity);
        spinnerProductCategory = findViewById(R.id.spinnerProductCategory);
        buttonSaveProduct = findViewById(R.id.buttonSaveProduct);

        // Setup database helper
        databaseHelper = new DatabaseHelper(this);

        // Load categories for spinner
        loadCategories();

        // Check if editing existing product
        if (getIntent().hasExtra("product_id")) {
            productId = getIntent().getIntExtra("product_id", -1);
            loadProductData(productId);
        }

        // Setup save button
        buttonSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        // Get values from input fields
        String name = editTextProductName.getText().toString().trim();
        String description = editTextProductDescription.getText().toString().trim();
        String priceString = editTextProductPrice.getText().toString().trim();
        String stockQuantityString = editTextStockQuantity.getText().toString().trim();

        // Validate input
        if (name.isEmpty() || priceString.isEmpty() || stockQuantityString.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse numeric values
        double price;
        int stockQuantity;
        try {
            price = Double.parseDouble(priceString);
            stockQuantity = Integer.parseInt(stockQuantityString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numeric values", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected category
        int selectedPosition = spinnerProductCategory.getSelectedItemPosition();
        if (selectedPosition < 0 || selectedPosition >= categoryList.size()) {
            Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryId = categoryList.get(selectedPosition).getId();

        // Create or update product
        if (productId == -1) {
            // Create new product
            Product newProduct = new Product(0, name, description, price, categoryId, stockQuantity);
            long result = databaseHelper.addProduct(newProduct);

            if (result > 0) {
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update existing product
            Product updatedProduct = new Product(productId, name, description, price, categoryId, stockQuantity);
            int result = databaseHelper.updateProduct(updatedProduct);

            if (result > 0) {
                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadProductData(int productId) {
        existingProduct = databaseHelper.getProduct(productId);

        if (existingProduct != null) {
            // Populate UI with product data
            editTextProductName.setText(existingProduct.getName());
            editTextProductDescription.setText(existingProduct.getDescription());
            editTextProductPrice.setText(String.valueOf(existingProduct.getPrice()));
            editTextStockQuantity.setText(String.valueOf(existingProduct.getStockQuantity()));

            // Set spinner selection to match product category
            int categoryPosition = getCategoryPosition(existingProduct.getCategoryId());
            if (categoryPosition >= 0) {
                spinnerProductCategory.setSelection(categoryPosition);
            }

            // Update button text to reflect editing
            buttonSaveProduct.setText("Update Product");
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private int getCategoryPosition(int categoryId) {
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == categoryId) {
                return i;
            }
        }
        return 0; // Default to first position if not found
    }

    private void loadCategories() {
        categoryList = databaseHelper.getAllCategories();
        List<String> categoryNames = new ArrayList<>();

        for (Category category : categoryList) {
            categoryNames.add(category.getName());
        }

        if (categoryNames.isEmpty()) {
            categoryNames.add("Default");
            // Ensure we have at least one category to select
            if (databaseHelper.getAllCategories().isEmpty()) {
                Category defaultCategory = new Category(0, "Default");
                databaseHelper.addCategory(defaultCategory);
                categoryList = databaseHelper.getAllCategories();
            }
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductCategory.setAdapter(categoryAdapter);
    }
}