package com.yeslabapps.fictionfocus.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yeslabapps.fictionfocus.fragments.FavoritesFragment;
import com.yeslabapps.fictionfocus.fragments.SavedListsFragment;
import com.yeslabapps.fictionfocus.fragments.SavedQuotesFragment;
import com.yeslabapps.fictionfocus.fragments.SavedVideosFragment;
import com.yeslabapps.fictionfocus.fragments.WatchedFragment;
import com.yeslabapps.fictionfocus.fragments.WatchedSeriesFragment;

import java.util.ArrayList;

public class SavedViewPagerAdapter extends FragmentPagerAdapter {


    public SavedViewPagerAdapter(
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
            fragment = new SavedQuotesFragment();
        else if (position == 1)
            fragment = new SavedVideosFragment();
        else if (position == 2)
            fragment = new SavedListsFragment();


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
            title = "Quotes";
        else if (position == 1)
            title = "Videos";
        else if (position == 2)
            title = "Lists";





        return title;
    }
}

