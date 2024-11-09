package com.example.smdassignment3;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity implements ProductAdapter.itemClickedListener, ScheduledAdapter.ScheduleClickListener, DeliveredAdapter.OnDataChangeListener {


    TabLayout tabLayout;
    ViewPager2 vp2;
    ViewPagerAdapter adapter;

    int count=0;
    boolean flag = false;

    FloatingActionButton fab_add;
    public NewOrderFragment getNewOrderFragment() {
        // Assuming NewOrderFragment is at a specific position, e.g., position 2
        if (adapter != null) {
            Fragment fragment = adapter.getFragment(2); // this method needs to be safely implemented
            if (fragment instanceof NewOrderFragment) {
                return (NewOrderFragment) fragment;
            }
        }
        return null;
    }
    public DeliveredFragment getDeliveredFragment() {
        // Assuming NewOrderFragment is at a specific position, e.g., position 2
        if (adapter != null) {
            Fragment fragment = adapter.getFragment(0); // this method needs to be safely implemented
            if (fragment instanceof DeliveredFragment) {
                return (DeliveredFragment) fragment;
            }
        }
        return null;
    }
    public ScheduleFragment getScheduledOrderFragment() {
        if (adapter != null) {
            Fragment fragment = adapter.getFragment(1);
            if (fragment instanceof ScheduleFragment) {
                return (ScheduleFragment) fragment;
            }
        }
        return null;
    }

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
        adapter = new ViewPagerAdapter(this);
        vp2 = findViewById(R.id.viewpager2);
        vp2.setAdapter(adapter);
        tabLayout = findViewById(R.id.tabLayout);
        fab_add = findViewById(R.id.fab_add);


        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Product");
                View v = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.add_new_product_dialog_design, null, false);
                dialog.setView(v);
                EditText etTitle = v.findViewById(R.id.etTitle);
                EditText etDate = v.findViewById(R.id.etDate);
                EditText etPrice = v.findViewById(R.id.etPrice);

                dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String title = etTitle.getText().toString().trim();
                        String date = etDate.getText().toString().trim();
                        String price = etPrice.getText().toString();

                        ProductDB productDB = new ProductDB(MainActivity.this);
                        productDB.open();
                        productDB.insert(title, date, Integer.parseInt(price),"new");
                        int id=productDB.getLatestProductId();
                        productDB.close();
                        NewOrderFragment fragment = getNewOrderFragment();

                        if (fragment != null && fragment.isAdded()) {
                            fragment.onAddProduct(new Product(id,title,date,Integer.parseInt(price),"new"));
                        } else {
                            Toast.makeText(MainActivity.this, "Fragment not available", Toast.LENGTH_SHORT).show();
                        }

                        Toast.makeText(MainActivity.this, "Product Added", Toast.LENGTH_SHORT).show();

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




        TabLayoutMediator tabLayoutMediator =
                new TabLayoutMediator(tabLayout, vp2, new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position)
                        {
                            case 0:
                                tab.setText("Delivered");
                                tab.setIcon(R.drawable.delivered_icon);
                                break;
                            case 1:
                                tab.setText("Scheduled");
                                tab.setIcon(R.drawable.schedule_icon);
//                                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
//                                badgeDrawable.setNumber(count);
//                                badgeDrawable.setMaxCharacterCount(2);
//                                badgeDrawable.setVisible(true);
                                break;
                            default:
                                tab.setText("New Orders");
                                tab.setIcon(R.drawable.new_orders_icon);
                        }
                    }
                });
        tabLayoutMediator.attach();

//        vp2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                TabLayout.Tab selectedTab = tabLayout.getTabAt(position);
////                count++;
//                BadgeDrawable badgeDrawable = selectedTab.getBadge();
//                if(badgeDrawable != null)
//                {
//                    count=0;
//                    badgeDrawable.setNumber(count);
//                    if(!flag)
//                        flag=true;
//                    else
//                        badgeDrawable.setVisible(false);
//                }
//
//
////                   badgeDrawable.setNumber(count);
//
//
//            }
//        });
//
    }

    @Override
        public void itemClicked(Product product) {
        Fragment scheduled_frag = getScheduledOrderFragment();
        ((ScheduleFragment) scheduled_frag).addProductToSchedule(product);
        Fragment newFrag = getNewOrderFragment();
        ((NewOrderFragment) newFrag).removeFromList(product);
    }

    @Override
    public void onScheduleClick(Product p) {
        DeliveredFragment delivered_frag = getDeliveredFragment();
        if (delivered_frag != null && delivered_frag.isAdded()) {
            delivered_frag.addProductToDeliver(p);
        } else {
            Toast.makeText(this, "Delivered Fragment not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDataChanged() {
        Toast.makeText(this, "called", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }
}