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

public class ScheduledAdapter extends ArrayAdapter<Product> {
    private Context context;
    private int resource;

    public interface ScheduleClickListener{
        public void onScheduleClick(Product p);
    }
    ScheduleClickListener scheduleClickListener;

    public ScheduledAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Product> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        scheduleClickListener = (ScheduleClickListener) context;
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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleClickListener.onScheduleClick(p);
                remove(p);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public void addSchedule(Product product) {
        add(product);
        notifyDataSetChanged();
        ProductDB db= new ProductDB(context);
        db.open();
        db.updateStatus(product.id,"scheduled");
        db.close();
    }
}
