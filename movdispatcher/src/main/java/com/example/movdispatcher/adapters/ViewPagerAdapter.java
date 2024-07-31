package com.example.movdispatcher.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.movdispatcher.OrdersHistoryFragment;
import com.example.movdispatcher.OrdersNewFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new OrdersNewFragment();
            case 1:
                return new OrdersHistoryFragment();
        }
        return new OrdersNewFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
