package com.yeslabapps.fictionfocus.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yeslabapps.fictionfocus.fragments.SearchCountryFragment;
import com.yeslabapps.fictionfocus.fragments.SearchFavoriteFragment;
import com.yeslabapps.fictionfocus.fragments.SearchFragment;


public class UserViewPagerAdapter
        extends FragmentPagerAdapter {

    public UserViewPagerAdapter(
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
            fragment = new SearchCountryFragment();
        else if (position == 1)
            fragment = new SearchFavoriteFragment();
        else if (position == 2)
            fragment = new SearchFragment();






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
            title = "Country";
        else if (position == 1)
            title = "Favorite";
        else if (position == 2)
            title = "Username";





        return title;
    }
}