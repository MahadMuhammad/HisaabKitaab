package com.example.hisaabkitaab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "hisaabkitaab.db";

    // User table
    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_PASSWORD = "user_password";

    // Products and Categories tables
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_CATEGORIES = "categories";

    // Common columns
    private static final String COLUMN_ID = "id";

    // Product columns
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_STOCK_QUANTITY = "stock_quantity";

    // Category columns
    private static final String COLUMN_CATEGORY_NAME = "name";

    // SQL create statements
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_PASSWORD + " TEXT" + ")";

    private String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CATEGORY_NAME + " TEXT)";

    private String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_PRICE + " REAL,"
            + COLUMN_CATEGORY_ID + " INTEGER,"
            + COLUMN_STOCK_QUANTITY + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        // Create tables again
        onCreate(db);
    }

    // USER OPERATIONS

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getUsername());
        values.put(COLUMN_USER_PASSWORD, user.getPassword()); // In a real app, hash the password

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public boolean checkUser(String username) {
        // array of columns to fetch
        String[] columns = {COLUMN_USER_ID};
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_NAME + " = ?";
        // selection argument
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }

    public boolean checkUser(String username, String password) {
        // array of columns to fetch
        String[] columns = {COLUMN_USER_ID};
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_NAME + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        // selection arguments
        String[] selectionArgs = {username, password}; // In a real app, compare hashed passwords

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }

    // PRODUCT OPERATIONS

    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, product.getName());
        values.put(COLUMN_DESCRIPTION, product.getDescription());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_CATEGORY_ID, product.getCategoryId());
        values.put(COLUMN_STOCK_QUANTITY, product.getStockQuantity());

        long id = db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        return id;
    }

    public Product getProduct(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Product product = new Product(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_STOCK_QUANTITY))
            );
            cursor.close();
            return product;
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_STOCK_QUANTITY))
                );
                products.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return products;
    }

    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COLUMN_CATEGORY_ID + "=?",
                new String[]{String.valueOf(categoryId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_STOCK_QUANTITY))
                );
                products.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return products;
    }

    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, product.getName());
        values.put(COLUMN_DESCRIPTION, product.getDescription());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_CATEGORY_ID, product.getCategoryId());
        values.put(COLUMN_STOCK_QUANTITY, product.getStockQuantity());

        return db.update(TABLE_PRODUCTS, values, COLUMN_ID + "=?",
                new String[]{String.valueOf(product.getId())});
    }

    public void deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // CATEGORY OPERATIONS

    public long addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CATEGORY_NAME, category.getName());

        long id = db.insert(TABLE_CATEGORIES, null, values);
        db.close();
        return id;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME))
                );
                categories.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return categories;
    }

    public int updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CATEGORY_NAME, category.getName());

        return db.update(TABLE_CATEGORIES, values, COLUMN_ID + "=?",
                new String[]{String.valueOf(category.getId())});
    }

    public void deleteCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}