package com.example.smdassignment3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ProductDB {
    public final String DATABASE_NAME = "products_db";
    public final String DATABASE_TABLE_NAME = "products";
    public final String KEY_ID = "id";
    public final String KEY_TITLE = "title";
    public final String KEY_DATE = "date";
    public final String KEY_PRICE = "price";
    public final String KEY_STATUS = "status";

    private final int DB_VERSION = 7;
    Context context;
    DBHelper dbHelper;

    ProductDB(Context context)
    {
        this.context = context;
    }

    public void open()
    {
        dbHelper = new DBHelper(context, DATABASE_NAME, null, DB_VERSION);
    }

    public void close()
    {
        dbHelper.close();
    }

    public long insert(String title, String date, int price, String status)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, title);
        cv.put(KEY_DATE, date);
        cv.put(KEY_PRICE, price);
        cv.put(KEY_STATUS, status);

        return db.insert(DATABASE_TABLE_NAME, null, cv);
    }

    public int remove(int id)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DATABASE_TABLE_NAME, KEY_ID+"=?", new String[]{id+""});
    }

    public int updateStatus(int id, String status){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_STATUS, status);
        return db.update(DATABASE_TABLE_NAME,cv,KEY_ID+"=?",new String[]{id+""});
    }

    public int updatePrice(int id, int price) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_PRICE, price);
        return db.update(DATABASE_TABLE_NAME, cv, KEY_ID+"=?", new String[]{id+""});
    }

    public int getLatestProductId() {
        int latestId = -1; // Default value if no entries are found
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(id) FROM products", null);

        if (cursor != null && cursor.moveToFirst()) {
            latestId = cursor.getInt(0); // Get the maximum ID
            cursor.close();
        }
        db.close();
        return latestId;
    }

    public Product getProductById(int id) {
        Product product = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query to select the product by ID
        Cursor cursor = db.rawQuery("SELECT * FROM products WHERE id = ?", new String[]{String.valueOf(id)});

        int id_index = cursor.getColumnIndex(KEY_ID);
        int title_index = cursor.getColumnIndex(KEY_TITLE);
        int date_index = cursor.getColumnIndex(KEY_DATE);
        int price_index = cursor.getColumnIndex(KEY_PRICE);
        int status_index = cursor.getColumnIndex(KEY_STATUS);

        if (cursor != null && cursor.moveToFirst()) {
            // Assuming your Product class has a constructor like Product(int id, String title, String date, int price, String status)
            int productId = cursor.getInt(id_index);
            String title = cursor.getString(title_index);
            String date = cursor.getString(date_index);
            int price = cursor.getInt(price_index);
            String status = cursor.getString(status_index);

            // Create a new Product object with the retrieved values
            product = new Product(productId, title, date, price, status); // Adjust constructor as needed
        }

        // Close cursor and database
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return product;
    }



    public ArrayList<Product> fetchProducts(@Nullable String status) {
        SQLiteDatabase readDb = dbHelper.getReadableDatabase();
        ArrayList<Product> products = new ArrayList<>();
        String[] columns = new String[]{KEY_ID, KEY_TITLE, KEY_DATE, KEY_PRICE, KEY_STATUS};

        String selection = status != null ? KEY_STATUS + "=?" : null; // Prepare the selection criteria based on status
        String[] selectionArgs = status != null ? new String[]{status} : null; // Prepare the selection arguments

        // Execute the query with conditional filtering
        Cursor cursor = readDb.query(DATABASE_TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id_index = cursor.getColumnIndex(KEY_ID);
            int title_index = cursor.getColumnIndex(KEY_TITLE);
            int date_index = cursor.getColumnIndex(KEY_DATE);
            int price_index = cursor.getColumnIndex(KEY_PRICE);
            int status_index = cursor.getColumnIndex(KEY_STATUS);

            do {
                Product p = new Product(
                        cursor.getInt(id_index),
                        cursor.getString(title_index),
                        cursor.getString(date_index),
                        cursor.getInt(price_index),
                        cursor.getString(status_index)
                );
                products.add(p);
            } while (cursor.moveToNext());

            cursor.close();
        }
        readDb.close();
        return products;
    }

    public int updateProduct(int id, String title, String date, int price) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE,title);
        cv.put(KEY_DATE,date);
        cv.put(KEY_PRICE, price);
        return db.update(DATABASE_TABLE_NAME,cv,KEY_ID+"=?",new String[]{id+""});
    }


    private class DBHelper extends SQLiteOpenHelper
    {

        public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            /*
            CREATE TABLE IF NOT EXISTS products_db(
                   id INTEGER PRIMARY KEY AUTOINCREMENT,
                   title TEXT NOT NULL,
                   date TEXT NOT NULL,
                   price INTEGER
            );
             */
            String query = "CREATE TABLE IF NOT EXISTS "+DATABASE_TABLE_NAME+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    +KEY_TITLE+" TEXT NOT NULL,"+KEY_DATE+" TEXT NOT NULL,"+KEY_PRICE+" INTEGER, " +KEY_STATUS +");";
            sqLiteDatabase.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            // backup your data here
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }






}
