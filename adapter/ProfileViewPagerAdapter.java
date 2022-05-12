package com.yeslabapps.fictionfocus.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yeslabapps.fictionfocus.fragments.FavoritesFragment;
import com.yeslabapps.fictionfocus.fragments.WatchedFragment;
import com.yeslabapps.fictionfocus.fragments.WatchedSeriesFragment;

public class ProfileViewPagerAdapter
        extends FragmentPagerAdapter {

    public ProfileViewPagerAdapter(
            @NonNull FragmentManager fm)
    {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = null;
        if (position == 0)
            fragment = new WatchedFragment();
        else if (position == 1)
            fragment = new WatchedSeriesFragment();
        else if (position == 2)
            fragment = new FavoritesFragment();







        return fragment;
    }

    @Override
    public int getCount()
    {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        String title = null;
        if (position == 0)
            title = "Movies";
        else if (position == 1)
            title = "Series";
        else if (position == 2)
            title = "Favorites";





        return title;
    }
}
