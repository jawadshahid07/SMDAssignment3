package com.example.smdassignment3;

import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.lang.ref.WeakReference;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private SparseArrayCompat<WeakReference<Fragment>> fragmentReferences = new SparseArrayCompat<>();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new DeliveredFragment();
                break;
            case 1:
                fragment = new ScheduleFragment();
                break;
            default:
                fragment = new NewOrderFragment();
                break;
        }
        fragmentReferences.put(position, new WeakReference<>(fragment));
        return fragment;
    }

    public Fragment getFragment(int position) {
        WeakReference<Fragment> ref = fragmentReferences.get(position);
        return ref != null ? ref.get() : null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
