package com.example.smdassignment3;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {
    private Context context;
    private int resource;
    private ArrayList<Product> products;
    private itemClickedListener itemClickedListener;

    public interface itemClickedListener {
        void itemClicked(Product p);
    }

    public ProductAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Product> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        itemClickedListener = (itemClickedListener) context; // Ensure context implements itemClickedListener
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        // Find views by ID and log to verify they're not null
        TextView tvTitle = convertView.findViewById(R.id.tvProductTitle);
        ImageView ivEdit = convertView.findViewById(R.id.ivEdit);
        ImageView ivDelete = convertView.findViewById(R.id.ivDelete);

        if (tvTitle == null || ivEdit == null || ivDelete == null) {
            Log.e("ProductAdapter", "One or more views are null in getView()");
            return convertView;
        }

        // Set data for the current product
        Product p = getItem(position);
        if (p != null) {
            tvTitle.setText("Product Name: " + p.getTitle() + "\nProduct Price: " + p.getPrice() + "\nProduct Status: " + p.getStatus());

            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle edit action
                    Log.d("ProductAdapter", "Edit clicked for product ID: " + p.getId());
                }
            });

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle delete action
                    ProductDB db = new ProductDB(context);
                    db.open();
                    db.remove(p.getId());
                    db.close();
                    remove(p);
                    notifyDataSetChanged();
                    Log.d("ProductAdapter", "Delete clicked for product ID: " + p.getId());
                }
            });

            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Edit Product");
                    View v = LayoutInflater.from(context)
                            .inflate(R.layout.add_new_product_dialog_design, null, false);
                    dialog.setView(v);

                    EditText etTitle = v.findViewById(R.id.etTitle);
                    EditText etDate = v.findViewById(R.id.etDate);
                    EditText etPrice = v.findViewById(R.id.etPrice);

                    etTitle.setText(p.getTitle());
                    etDate.setText(p.getDate());
                    etPrice.setText(String.valueOf(p.getPrice())); // Convert price to String

                    dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String title = etTitle.getText().toString().trim();
                            String date = etDate.getText().toString().trim();
                            String priceText = etPrice.getText().toString();

                            if (!priceText.isEmpty()) {
                                int price = Integer.parseInt(priceText);

                                ProductDB productDB = new ProductDB(context);
                                productDB.open();
                                productDB.updateProduct(p.getId(), title, date, price);
                                productDB.close();

                                p.setTitle(title);
                                p.setDate(date);
                                p.setPrice(price);

                                Toast.makeText(context, "Product Updated", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged(); // Update the adapter after changes
                            } else {
                                Toast.makeText(context, "Price cannot be empty", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    dialog.show();
                }
            });


            // Handle item clicked listener for the entire row
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickedListener.itemClicked(p);
                    Log.d("ProductAdapter", "Item clicked for product ID: " + p.getId());
                }
            });
        } else {
            Log.e("ProductAdapter", "Product is null at position " + position);
        }

        return convertView;
    }
}
