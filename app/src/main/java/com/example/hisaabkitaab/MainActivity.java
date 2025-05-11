package com.example.hisaabkitaab;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private CardView cardProducts, cardStock, cardSales, cardReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hisaab Kitaab");

        // Initialize UI components
        cardProducts = findViewById(R.id.cardProducts);
        cardStock = findViewById(R.id.cardStock);
        cardSales = findViewById(R.id.cardSales);
        cardReports = findViewById(R.id.cardReports);

        // Set click listeners
        cardProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Will be implemented in future to navigate to ProductActivity
                Toast.makeText(MainActivity.this, "Product Catalog feature coming soon", Toast.LENGTH_SHORT).show();

                // move to product catalog activity
                Intent intent = new Intent(MainActivity.this, ProductCatalogActivity.class);
                startActivity(intent);
//                finish();
            }
        });

        cardStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Will be implemented in future to navigate to StockActivity
                Toast.makeText(MainActivity.this, "Stock Management feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        cardSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Will be implemented in future to navigate to SalesActivity
                Toast.makeText(MainActivity.this, "Sales Transactions feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        cardReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Will be implemented in future to navigate to ReportsActivity
                Toast.makeText(MainActivity.this, "Reports feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            // Log out and navigate to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}