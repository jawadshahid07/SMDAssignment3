package com.example.smdassignment3;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class NewOrderFragment extends Fragment {

    // Interface defined within the Fragment
    ProductAdapter adapter;
    ArrayList<Product> products = new ArrayList<Product>();

    private Context context;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public NewOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static NewOrderFragment newInstance(String param1, String param2) {
        NewOrderFragment fragment = new NewOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView lvNewOrderList = view.findViewById(R.id.lvNewOrdersList);
        ProductDB productDB = new ProductDB(context);
        productDB.open();
        products = productDB.fetchProducts("new");
        productDB.close();

        adapter = new ProductAdapter(context, R.layout.product_item_design, products);
        lvNewOrderList.setAdapter(adapter);
    }

    public void onAddProduct(Product product) {
                products.add(product);
                adapter.notifyDataSetChanged();
            }
    public void removeFromList(Product p){
        products.remove(p);
        Toast.makeText(context, "Item scheduled", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }
}


