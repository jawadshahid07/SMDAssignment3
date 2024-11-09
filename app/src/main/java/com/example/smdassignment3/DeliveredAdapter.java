package com.example.smdassignment3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DeliveredAdapter extends ArrayAdapter<Product> {
    Context context;
    int resource;

    public interface OnDataChangeListener {
        void onDataChanged();
    }

    OnDataChangeListener onDataChangeListener;

    public DeliveredAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Product> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        onDataChangeListener = (OnDataChangeListener) context;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }
        TextView tvConfirmedProductTitle = convertView.findViewById(R.id.tvConfirmedProductTitle);
        Product p = getItem(position);

        if (p != null) {
            String displayText = "Product Name: " + p.getTitle() + "\nProduct Price: " + p.getPrice() + "\nProduct Status: " + p.getStatus();
            tvConfirmedProductTitle.setText(displayText);
        } else {
            tvConfirmedProductTitle.setText("No product data available");
        }

        return convertView;
    }

    public void addDeliverProduct(Product p) {
        add(p);
        ProductDB db= new ProductDB(context);
        db.open();
        db.updateStatus(p.id,"delivered");
        db.close();
        notifyDataSetChanged();
        onDataChangeListener.onDataChanged();
    }
}
